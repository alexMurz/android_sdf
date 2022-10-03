use cgmath::vec4;

use crate::ty::Vec4;

#[derive(Debug, Clone, PartialEq)]
pub struct Material {
    pub diffuse: Vec4,
}

impl Default for Material {
    #[inline]
    fn default() -> Self {
        Self {
            diffuse: vec4(1.0, 1.0, 1.0, 1.0),
        }
    }
}
