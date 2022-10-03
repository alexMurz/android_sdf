use crate::ty::Vec3;

use super::{
    material::Material,
    modifier::Modifier,
    op::{Op, RayState},
    shape::{Shape, ShapeUnion},
};

#[derive(Debug, Clone, PartialEq)]
pub struct Node {
    pub shape: ShapeUnion,
    pub material: Material,
    pub modifiers: Vec<Modifier>,
    pub op: Op,
}

impl Node {
    #[inline]
    fn resolve_origin(mut o: Vec3, modifiers: &[Modifier]) -> Option<Vec3> {
        for m in modifiers.iter() {
            match m.apply(o) {
                Some(v) => o = v,
                None => return None,
            }
        }
        Some(o)
    }

    pub fn apply(&self, world: RayState, o: Vec3) -> RayState {
        let origin = match Self::resolve_origin(o, &self.modifiers) {
            Some(v) => v,
            None => return world,
        };

        let d = self.shape.distance_to(origin);
        let c = self.material.diffuse;

        self.op.apply(world, RayState(d, c))
    }
}
