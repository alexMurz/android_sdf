use std::{mem::size_of, num::NonZeroU32, sync::Arc};

use crate::scene::{SceneConfig, SceneProcessor};

use grepr::*;
pub mod grepr;
pub mod renderer;
pub mod util;

pub struct Processor {
    draw_cache: Vec<u32>,
    resolution: u32,
    config: Arc<SceneConfig>,
    scene_dirty: bool,

    // Wgpu
    dev: wgpu::Device,
    queue: wgpu::Queue,
    ren: renderer::Renderer,
    tex: wgpu::Texture,
    output_dimensions: util::BufferDimensions,
    output: wgpu::Buffer,
}

impl SceneProcessor for Processor {
    fn create(resolution: u32) -> Self
    where
        Self: Sized,
    {
        let (dev, queue) = futures::executor::block_on(util::create_wgpu());

        let output_dimensions =
            util::BufferDimensions::new(resolution as usize, resolution as usize, size_of::<u32>());
        let output = dev.create_buffer(&wgpu::BufferDescriptor {
            label: Some("GPUStagingBuffer"),
            usage: wgpu::BufferUsages::COPY_DST | wgpu::BufferUsages::MAP_READ,
            size: output_dimensions.size_bytes(),
            mapped_at_creation: false,
        });

        let ren = renderer::Renderer::new(&dev, wgpu::TextureFormat::Rgba8UnormSrgb);

        let tex = ren.create_target(&dev, resolution, resolution);

        Self {
            draw_cache: vec![0xFF000000; (resolution * resolution) as usize],
            resolution,
            config: Default::default(),
            scene_dirty: false,
            dev,
            queue,
            ren,
            tex,
            output_dimensions,
            output,
        }
    }

    fn update(&mut self, conf: SceneConfig) {
        let config = Arc::make_mut(&mut self.config);
        if config.scene != conf.scene {
            self.scene_dirty = true;
        }
        *config = conf;
    }

    fn get_draw_cache(&self) -> &[u32] {
        &self.draw_cache
    }

    fn run(&mut self) {
        let Self {
            draw_cache,
            resolution,
            ref dev,
            ref queue,
            ref ren,
            ref tex,
            ref output,
            ref output_dimensions,
            ..
        } = self;

        let resolution = *resolution;

        let conf = self.config.clone();
        let mut enc = dev.create_command_encoder(&Default::default());
        let tex_view = tex.create_view(&Default::default());

        ren.render(&conf.viewer, resolution, resolution, &mut enc, &tex_view);

        enc.copy_texture_to_buffer(
            tex.as_image_copy(),
            wgpu::ImageCopyBufferBase {
                buffer: output,
                layout: wgpu::ImageDataLayout {
                    offset: 0,
                    bytes_per_row: Some(
                        NonZeroU32::new(output_dimensions.padded_bytes_per_row as u32).unwrap(),
                    ),
                    rows_per_image: None,
                },
            },
            wgpu::Extent3d {
                width: resolution,
                height: resolution,
                depth_or_array_layers: 1,
            },
        );

        let cb = enc.finish();
        queue.submit(Some(cb));

        {
            let slice = output.slice(..);

            let map_recv = util::buffer_async_map(wgpu::MapMode::Read, slice);
            dev.poll(wgpu::Maintain::Wait);
            futures::executor::block_on(map_recv).unwrap().unwrap();

            let range = slice.get_mapped_range();
            draw_cache.copy_from_slice(bytemuck::cast_slice(&*range));
        }
        output.unmap();
    }
}

#[test]
fn should_run() {
    let mut p = Processor::create(128);
    p.run();
    p.update(SceneConfig {
        scene: crate::scene::Scene {
            nodes: vec![
                crate::scene::Node {
                    material: crate::scene::Material {
                        ..Default::default()
                    },
                    modifiers: vec![],
                    op: crate::scene::Op::Union,
                    shape: crate::scene::ShapeUnion::Sphere(crate::scene::SphereShape {
                        radius: 1.0,
                    }),
                },
                crate::scene::Node {
                    material: crate::scene::Material {
                        ..Default::default()
                    },
                    modifiers: vec![],
                    op: crate::scene::Op::Union,
                    shape: crate::scene::ShapeUnion::Sphere(crate::scene::SphereShape {
                        radius: 1.0,
                    }),
                },
                crate::scene::Node {
                    material: crate::scene::Material {
                        ..Default::default()
                    },
                    modifiers: vec![],
                    op: crate::scene::Op::Union,
                    shape: crate::scene::ShapeUnion::Sphere(crate::scene::SphereShape {
                        radius: 1.0,
                    }),
                },
            ],
        },
        viewer: crate::scene::Viewer::default(),
    });
    p.run();
}
