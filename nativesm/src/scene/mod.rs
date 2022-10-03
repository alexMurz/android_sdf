pub use material::*;
pub use modifier::*;
pub use node::*;
pub use op::*;
pub use shape::*;
pub use viewer::*;

mod material;
mod modifier;
mod node;
mod op;
mod shape;
mod viewer;

pub trait SceneProcessor {
    fn create(resolution: u32) -> Self
    where
        Self: Sized;

    fn update(&mut self, conf: SceneConfig);

    fn get_draw_cache(&self) -> &[u32];

    fn run(&mut self);
}

#[derive(Debug, Clone, PartialEq)]
pub struct Scene {
    pub nodes: Vec<Node>,
}

#[derive(Debug, Clone, PartialEq, Default)]
pub struct SceneConfig {
    pub scene: Scene,
    pub viewer: Viewer,
}

impl Default for Scene {
    #[inline]
    fn default() -> Self {
        Self { nodes: vec![] }
    }
}
