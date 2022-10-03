use std::{io::Write, mem::size_of};

use bytemuck::{Pod, Zeroable};
use cgmath::{vec3, ElementWise};

use crate::{
    scene::{Modifier, Node, Op, ShapeUnion, Viewer},
    ty::Vec3,
};

const SHAPE_SPHERE: f32 = 0.0;
const SHAPE_BOX: f32 = 1.0;

const OP_UNION: f32 = 0.0;
const OP_SUBTRACT: f32 = 1.0;
const OP_INTERSECT: f32 = 2.0;
const OP_UNION_SMOOTH: f32 = 3.0;
const OP_SUBTRACT_SMOOTH: f32 = 4.0;
const OP_INTERSECT_SMOOTH: f32 = 5.0;

pub struct SceneBuffer;

impl SceneBuffer {
    #[inline]
    pub fn size_for_capacity(capacity: u64) -> u64 {
        let min_size = 96;
        // size, pad, pad, pad
        let size = size_of::<[u32; 4]>() as u64;
        std::cmp::max(
            capacity * size_of::<NodePod>() as u64 + size,
            min_size,
        )
    }

    #[inline]
    pub fn create_buffer(
        dev: &wgpu::Device,
        capacity: u64,
        mapped_at_creation: bool,
    ) -> wgpu::Buffer {
        dev.create_buffer(&wgpu::BufferDescriptor {
            label: Some("SceneBuffer"),
            usage: wgpu::BufferUsages::STORAGE | wgpu::BufferUsages::UNIFORM | wgpu::BufferUsages::MAP_WRITE,
            size: Self::size_for_capacity(capacity),
            mapped_at_creation,
        })
    }

    #[inline]
    pub fn write_buffer(src: &[Node], dst: &mut [u8]) -> std::io::Result<()> {
        let mut cursor = std::io::Cursor::new(dst);
        // Size + paddins
        cursor.write_all(bytemuck::bytes_of(&[src.len() as u32, 0, 0, 0]))?;

        let mut pod = NodePod::zeroed();
        for n in src {
            pod.copy_from(n);
            cursor.write_all(bytemuck::bytes_of(&pod))?;
        }

        cursor.flush()
    }
}

#[repr(C)]
#[derive(Clone, Copy, Pod, Zeroable, Debug)]
pub struct NodePod {
    pub shape: [f32; 4],
    pub tr: [f32; 4],
    pub sc: [f32; 4],
    pub diffuse: [f32; 4],
    pub op: [f32; 4],
}

impl NodePod {
    pub fn copy_from(&mut self, node: &Node) {
        self.shape = match &node.shape {
            ShapeUnion::Sphere(x) => [x.radius, 0.0, 0.0, SHAPE_SPHERE],
            ShapeUnion::Box(x) => [x.half_size.x, x.half_size.y, x.half_size.z, SHAPE_BOX],
        };

        self.op = match node.op {
            Op::Union => [0.0, 0.0, 0.0, OP_UNION],
            Op::Subtract => [0.0, 0.0, 0.0, OP_SUBTRACT],
            Op::Intersect => [0.0, 0.0, 0.0, OP_INTERSECT],
            Op::SmoothUnion(x) => [x, 0.0, 0.0, OP_UNION_SMOOTH],
            Op::SmoothSubtract(x) => [x, 0.0, 0.0, OP_SUBTRACT_SMOOTH],
            Op::SmoothIntersect(x) => [x, 0.0, 0.0, OP_INTERSECT_SMOOTH],
        };

        let mut tr: Vec3 = vec3(0.0, 0.0, 0.0);
        let mut sc: Vec3 = vec3(1.0, 1.0, 1.0);

        for m in &node.modifiers {
            match m {
                &Modifier::Translate(x) => tr += x.mul_element_wise(sc),
                &Modifier::Scale(x) => sc.mul_assign_element_wise(x),
            }
        }

        self.diffuse = node.material.diffuse.into();
        self.tr = [tr.x, tr.y, tr.z, 0.0];
        self.sc = [sc.x, sc.y, sc.z, 0.0];

        log::debug!("Copied node {self:?} from {node:?}");
    }

    #[inline]
    pub fn from(node: &Node) -> Self {
        let mut this = Self::zeroed();
        this.copy_from(node);
        this
    }
}

#[repr(C, align(16))]
#[derive(Clone, Copy, Pod, Zeroable)]
pub struct ViewerPod {
    pub eye_pos: [f32; 4],
    pub eye_at: [f32; 4],
    pub eye_up: [f32; 4],
    pub spread: f32,
    pub max_draw_distance: f32,
    pub _pad1: [f32; 2],
}

impl ViewerPod {
    pub const SIZE: u32 = size_of::<Self>() as u32;

    #[inline]
    pub fn from(v: &Viewer) -> Self {
        Self {
            eye_pos: [v.eye_pos.x, v.eye_pos.y, v.eye_pos.z, 0.0],
            eye_at: [v.eye_at.x, v.eye_at.y, v.eye_at.z, 0.0],
            eye_up: [v.eye_up.x, v.eye_up.y, v.eye_up.z, 0.0],
            max_draw_distance: v.max_draw_distance,
            spread: v.spread,
            ..Self::zeroed()
        }
    }
}
