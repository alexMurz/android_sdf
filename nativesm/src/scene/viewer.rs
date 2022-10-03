use crate::ty::{vec3, Scalar, Vec3};

#[derive(Debug, Clone, PartialEq)]
pub struct Viewer {
    pub eye_pos: Vec3,
    pub eye_at: Vec3,
    pub eye_up: Vec3,
    pub spread: Scalar,
    pub max_draw_distance: Scalar,
}

impl Default for Viewer {
    #[inline]
    fn default() -> Self {
        Self {
            eye_pos: vec3(0.0, 0.0, 1.0),
            eye_at: vec3(0.0, 0.0, 0.0),
            eye_up: vec3(0.0, 1.0, 0.0),
            spread: 1 as Scalar,
            max_draw_distance: 10 as Scalar,
        }
    }
}
