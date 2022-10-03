use cgmath::{vec3, InnerSpace};

use crate::ty::{vec3_abs, vec3_max, Scalar, Vec3};

/// Base generic [`Shape`] trait
///
/// Implemented by all predefined shapes and can be implemented by custom shapes
pub trait Shape: std::fmt::Debug + Clone + PartialEq {
    fn distance_to(&self, p: Vec3) -> Scalar;
}

#[derive(Debug, Clone, PartialEq)]
pub enum ShapeUnion {
    Sphere(SphereShape),
    Box(BoxShape),
}

#[derive(Debug, Clone, PartialEq)]
pub struct SphereShape {
    pub radius: Scalar,
}

#[derive(Debug, Clone, PartialEq)]
pub struct BoxShape {
    pub half_size: Vec3,
}

impl Shape for SphereShape {
    #[inline]
    fn distance_to(&self, p: Vec3) -> Scalar {
        p.magnitude() - self.radius
    }
}

impl Shape for BoxShape {
    #[inline]
    fn distance_to(&self, p: Vec3) -> Scalar {
        let q = vec3_abs(p) - self.half_size;
        vec3_max(q, vec3(0.0, 0.0, 0.0)).magnitude() + q.x.max(q.y.max(q.z)).min(0.0)
    }
}

impl Shape for ShapeUnion {
    #[inline]
    fn distance_to(&self, p: Vec3) -> Scalar {
        match self {
            Self::Sphere(b) => b.distance_to(p),
            Self::Box(b) => b.distance_to(p),
        }
    }
}
