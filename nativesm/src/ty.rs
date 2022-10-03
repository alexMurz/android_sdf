#![allow(unused)]

use std::cmp::max_by;

pub use cgmath::{vec2, vec3, vec4};

pub type Scalar = f32;

pub type Vec2 = cgmath::Vector2<Scalar>;
pub type Vec3 = cgmath::Vector3<Scalar>;
pub type Vec4 = cgmath::Vector4<Scalar>;

const ZERO: Scalar = 0 as Scalar;
const ONE: Scalar = 1 as Scalar;

// max functions

#[inline]
pub fn vec2_max(a: Vec2, b: Vec2) -> Vec2 {
    vec2(a.x.max(b.x), a.y.max(b.y))
}

#[inline]
pub fn vec3_max(a: Vec3, b: Vec3) -> Vec3 {
    vec3(a.x.max(b.x), a.y.max(b.y), a.z.max(b.z))
}

#[inline]
pub fn vec4_max(a: Vec4, b: Vec4) -> Vec4 {
    vec4(a.x.max(b.x), a.y.max(b.y), a.z.max(b.z), a.w.max(b.w))
}

// abs functions

#[inline]
pub fn vec2_abs(a: Vec2) -> Vec2 {
    vec2(a.x.abs(), a.y.abs())
}

#[inline]
pub fn vec3_abs(a: Vec3) -> Vec3 {
    vec3(a.x.abs(), a.y.abs(), a.z.abs())
}

#[inline]
pub fn vec4_abs(a: Vec4) -> Vec4 {
    vec4(a.x.abs(), a.y.abs(), a.z.abs(), a.w.abs())
}
