use std::sync::Arc;

use cgmath::{vec3, vec4, InnerSpace};

use crate::{
    scene::{Node, RayState, SceneConfig, SceneProcessor},
    ty::{Scalar, Vec3, Vec4},
};

const EPS: Scalar = 0.0001;

pub struct Processor {
    #[cfg(feature = "async")]
    local_pool: scoped_threadpool::Pool,
    conf: Arc<SceneConfig>,
    draw_cache: Vec<u32>,
    resolution: u32,
}

impl SceneProcessor for Processor {
    fn create(resolution: u32) -> Self
    where
        Self: Sized,
    {
        let area = (resolution * resolution) as usize;
        Self {
            #[cfg(feature = "async")]
            local_pool: scoped_threadpool::Pool::new(4),
            conf: Arc::new(SceneConfig::default()),
            draw_cache: vec![0; area],
            resolution,
        }
    }

    #[inline]
    fn update(&mut self, conf: SceneConfig) {
        self.conf = Arc::new(conf);
    }

    #[inline]
    fn get_draw_cache(&self) -> &[u32] {
        &*self.draw_cache
    }

    fn run(&mut self) {
        self.perform_run()
    }
}

impl Processor {
    #[cfg(not(feature = "async"))]
    #[inline]
    fn perform_run(&mut self) {
        let conf = self.conf.clone();
        let conf = &*conf;

        let r = self.resolution;
        let dst = &mut *self.draw_cache;

        for x in 0..r {
            for y in 0..r {
                dst[(x + y * r) as usize] = run_ray(x, y, r, conf);
            }
        }
    }

    #[cfg(feature = "async")]
    #[inline]
    fn perform_run(&mut self) {
        let conf = self.conf.clone();
        let conf = &*conf;

        let r = self.resolution;
        let pool = &mut self.local_pool;
        let mut dst = &mut *self.draw_cache;

        pool.scoped(move |scope| {
            let total_work = dst.len() as u32;
            let thread_count = 4;
            let thread_work_size = total_work / thread_count;
            for t in 0..thread_count {
                let (local_dst, a) = dst.split_at_mut(thread_work_size as usize);
                dst = a;

                let index_offset = thread_work_size * t;
                scope.execute(move || {
                    for i in 0..thread_work_size {
                        let global_i = i + index_offset;
                        let x = global_i % r;
                        let y = global_i / r;
                        local_dst[i as usize] = run_ray(x, y, r, conf);
                    }
                });
            }
        });
    }
}

#[inline]
fn march_dir(origin: Vec3, at: Vec3, up: Vec3, x: u32, y: u32, r: u32, spread: Scalar) -> Vec3 {
    let xt = ((x as f32 / r as f32) * 2.0 - 1.0) * spread;
    let yt = ((y as f32 / r as f32) * 2.0 - 1.0) * spread;
    let dir = (at - origin).normalize();
    let xd = dir.cross(up).normalize();
    let yd = dir.cross(xd).normalize();

    (dir + xd * xt + yd * yt).normalize()
}

#[inline]
fn map_dist(o: Vec3, nodes: &[Node], max_dist: Scalar) -> RayState {
    const DEFAULT_COLOR: Vec4 = vec4(0.0, 0.0, 0.0, 0.0);
    nodes
        .iter()
        .fold(RayState(max_dist, DEFAULT_COLOR), |acc, v| v.apply(acc, o))
}

#[inline]
fn calc_normal(o: Vec3, nodes: &[Node], max_dist: Scalar) -> Vec3 {
    const XYY: Vec3 = vec3(EPS, -EPS, -EPS);
    const YYX: Vec3 = vec3(-EPS, -EPS, EPS);
    const YXY: Vec3 = vec3(-EPS, EPS, -EPS);
    const XXX: Vec3 = vec3(EPS, EPS, EPS);

    (XYY * map_dist(o + XYY, nodes, max_dist).0
        + YYX * map_dist(o + YYX, nodes, max_dist).0
        + YXY * map_dist(o + YXY, nodes, max_dist).0
        + XXX * map_dist(o + XXX, nodes, max_dist).0)
        .normalize()
}

#[inline]
fn calc_ao(o: Vec3, nodes: &[Node], max_dist: Scalar, normal: &Vec3) -> Scalar {
    let mut occ = 0.0;
    let mut sca = 1.0;
    for i in 0..5 {
        let h = 0.01 + 0.12 * 0.25 * i as f32;
        let d = map_dist(o + normal * h, nodes, max_dist).0;
        occ += (h - d) * sca;
        sca *= 0.95;
        if occ > 0.35 {
            break;
        }
    }
    return (1.0 - 3.0 * occ).clamp(0.0, 1.0) * (0.5 + 0.5 * normal.y);
}

#[inline]
fn parse_color(c: Vec4) -> u32 {
    #[inline]
    fn comp(f: Scalar, off: u32) -> u32 {
        ((f.clamp(0.0, 1.0) * 255.0) as u32) << off
    }

    // Returning format is ABGR
    comp(c.w, 24) + comp(c.z, 16) + comp(c.y, 8) + comp(c.x, 0)
}

fn run_ray(x: u32, y: u32, r: u32, conf: &SceneConfig) -> u32 {
    let nodes = &*conf.scene.nodes;
    let max_dist = conf.viewer.max_draw_distance;
    let mut travel_dist = 0.0;
    let mut hop_count = 0;

    let origin = conf.viewer.eye_pos;
    let dir = march_dir(
        origin,
        conf.viewer.eye_at,
        conf.viewer.eye_up,
        x,
        y,
        r,
        conf.viewer.spread,
    );

    // TODO: Replace hardcoded with configs
    while hop_count < 100 && travel_dist < max_dist {
        hop_count += 1;

        let p = origin + dir * travel_dist;

        let RayState(dist, col) = map_dist(p, nodes, max_dist - travel_dist);

        if dist.abs() < 0.01 {
            let n = calc_normal(p, nodes, max_dist);

            let mut lin = 0.0;
            lin += n.dot(vec3(-1.0, 1.0, 1.0).normalize()).clamp(0.0, 0.5);
            lin += n.dot(vec3(1.0, 1.0, 1.0).normalize()).clamp(0.0, 0.5);
            lin = lin.clamp(0.2, 1.0);

            lin *= calc_ao(p, nodes, max_dist, &n);

            return parse_color(col * lin);
        } else {
            travel_dist += dist
        }
    }

    // Black fallback
    0xFF000000
}

#[test]
fn should_run() {
    let mut runner = Processor::create(128);
    runner.run();
}
