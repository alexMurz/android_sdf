use cgmath::vec3;

use crate::ty::Vec3;

#[derive(Debug, Clone, PartialEq)]
pub enum Modifier {
    Translate(Vec3),
    Scale(Vec3),
}

impl Modifier {
    pub fn apply(&self, origin: Vec3) -> Option<Vec3> {
        match self {
            Self::Translate(v) => mod_translate(origin, v),
            Self::Scale(v) => mod_scale(origin, v),
        }
    }
}

#[inline]
fn mod_translate(o: Vec3, v: &Vec3) -> Option<Vec3> {
    Some(o - v)
}

#[inline]
fn mod_scale(o: Vec3, v: &Vec3) -> Option<Vec3> {
    Some(vec3(o.x / v.x, o.y / v.y, o.z / v.z))
}
