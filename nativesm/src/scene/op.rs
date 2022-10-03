use crate::ty::{Scalar, Vec4};

/// One of operands of operation (op)
/// Distance and material properties
pub struct RayState(pub Scalar, pub Vec4);

#[derive(Debug, Clone, PartialEq, Default)]
pub enum Op {
    #[default]
    Union,
    Subtract,
    Intersect,
    SmoothUnion(Scalar),
    SmoothSubtract(Scalar),
    SmoothIntersect(Scalar),
}

impl Op {
    #[inline]
    pub fn apply(&self, scene: RayState, obj: RayState) -> RayState {
        match self {
            Self::Union => op_union(scene, obj),
            Self::Subtract => op_subtract(scene, obj),
            Self::Intersect => op_intersect(scene, obj),
            &Self::SmoothUnion(arg) => op_sunion(scene, obj, arg),
            &Self::SmoothSubtract(arg) => op_ssubtract(scene, obj, arg),
            &Self::SmoothIntersect(arg) => op_sintersect(scene, obj, arg),
        }
    }
}

#[inline]
fn op_union(a: RayState, b: RayState) -> RayState {
    if a.0 < b.0 {
        a
    } else {
        b
    }
}

#[inline]
fn op_subtract(a: RayState, b: RayState) -> RayState {
    if a.0 > -b.0 {
        a
    } else {
        RayState(-b.0, a.1)
    }
}

#[inline]
fn op_intersect(a: RayState, b: RayState) -> RayState {
    if a.0 > b.0 {
        a
    } else {
        RayState(b.0, a.1)
    }
}

#[inline]
fn op_sunion(RayState(d1, c1): RayState, RayState(d2, c2): RayState, arg: Scalar) -> RayState {
    let h = (0.5 + 0.5 * (d2 - d1) / arg).clamp(0.0, 1.0);
    let d = (d2 + (d1 - d2) * h) - arg * h * (1.0 - h);
    let c = c2 + (c1 - c2) * h;
    RayState(d, c)
}

#[inline]
fn op_ssubtract(RayState(d1, c1): RayState, RayState(d2, _): RayState, arg: Scalar) -> RayState {
    let h = (0.5 - 0.5 * (d2 + d1) / arg).clamp(0.0, 1.0);
    let d = (d2 + (-d1 + d2) * h) + arg * h * (1.0 - h);
    let c = c1;
    RayState(d, c)
}

#[inline]
fn op_sintersect(RayState(d1, c1): RayState, RayState(d2, _): RayState, arg: Scalar) -> RayState {
    let h = (0.5 - 0.5 * (d2 - d1) / arg).clamp(0.0, 1.0);
    let d = (d2 + (d1 - d2) * h) + arg * h * (1.0 - h);
    let c = c1;
    RayState(d, c)
}
