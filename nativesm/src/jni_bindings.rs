use cgmath::{vec3, vec4};
use jni::{
    objects::JClass,
    sys::{jfloat, jint, jlong, jobject},
    JNIEnv,
};
use ndk::bitmap::AndroidBitmap;

use crate::{
    processor::Processor,
    scene::{BoxShape, Material, Modifier, Node, Op, SceneConfig, SceneProcessor, SphereShape},
    ty::Scalar,
};

fn to_ptr<T>(value: T) -> jlong {
    let ptr = Box::into_raw(Box::new(value));
    ptr as jlong
}

unsafe fn from_ptr<'a, T>(ptr: jlong) -> &'a mut T {
    let ptr = ptr as *mut T;
    &mut *ptr
}

unsafe fn drop_ptr<T>(ptr: jlong) {
    let ptr = ptr as *mut T;
    std::ptr::drop_in_place(ptr)
}

mod marcher_glue {
    use std::sync::Mutex;
    use super::*;

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_example_spheremarch_marcher_MarcherComputeNative_create(
        _: JNIEnv,
        _: JClass,
        resolution: jint,
    ) -> jlong {
        crate::init();
        to_ptr(Mutex::new(Processor::create(resolution as u32)))
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_example_spheremarch_marcher_MarcherComputeNative_applyConfig(
        _: JNIEnv,
        _: JClass,
        this: jlong,
        conf: jlong,
    ) {
        let config = from_ptr::<SceneConfig>(conf).clone();
        let processor = from_ptr::<Mutex<Processor>>(this);
        let mut processor = processor.lock().unwrap();
        processor.update(config);

        drop_ptr::<SceneConfig>(conf);
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_example_spheremarch_marcher_MarcherComputeNative_run(
        env: JNIEnv,
        _: JClass,
        ptr: jlong,
        dst: jobject,
    ) {
        let processor = from_ptr::<Mutex<Processor>>(ptr);
        let mut processor = processor.lock().unwrap();
        processor.run();

        let bitmap = AndroidBitmap::from_jni(env.get_native_interface(), dst);
        let info = bitmap.get_info().unwrap();

        let target = bitmap.lock_pixels().unwrap() as *mut u32;
        let as_slice =
            std::slice::from_raw_parts_mut(target, (info.width() * info.height()) as usize);

        as_slice.copy_from_slice(processor.get_draw_cache());

        bitmap.unlock_pixels().unwrap();
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_example_spheremarch_marcher_MarcherComputeNative_destroy(
        _: JNIEnv,
        _: JClass,
        ptr: jlong,
    ) {
        drop_ptr::<Mutex<Processor>>(ptr);
    }
}

// -------------------------------------------------------------------------
// Config builder

mod config_builder {
    use super::*;

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_example_spheremarch_marcher_ConfigBuilder_create(
        _: JNIEnv,
        _: JClass,
    ) -> jlong {
        to_ptr::<SceneConfig>(SceneConfig::default())
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_example_spheremarch_marcher_ConfigBuilder_destroyUnused(
        _: JNIEnv,
        _: JClass,
        ptr: jlong,
    ) {
        drop_ptr::<SceneConfig>(ptr);
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_example_spheremarch_marcher_ConfigBuilder_addNode(
        _: JNIEnv,
        _: JClass,
        this: jlong,
        node: jlong,
    ) {
        let conf = from_ptr::<SceneConfig>(this);
        let n = from_ptr::<Node>(node).clone();
        conf.scene.nodes.push(n);

        drop_ptr::<Node>(node);
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_example_spheremarch_marcher_ConfigBuilder_setEyePos(
        _: JNIEnv,
        _: JClass,
        ptr: jlong,
        x: jfloat,
        y: jfloat,
        z: jfloat,
    ) {
        let conf = from_ptr::<SceneConfig>(ptr);
        conf.viewer.eye_pos = vec3(x as Scalar, y as Scalar, z as Scalar);
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_example_spheremarch_marcher_ConfigBuilder_setEyeAt(
        _: JNIEnv,
        _: JClass,
        ptr: jlong,
        x: jfloat,
        y: jfloat,
        z: jfloat,
    ) {
        let conf = from_ptr::<SceneConfig>(ptr);
        conf.viewer.eye_at = vec3(x as Scalar, y as Scalar, z as Scalar);
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_example_spheremarch_marcher_ConfigBuilder_setEyeUp(
        _: JNIEnv,
        _: JClass,
        ptr: jlong,
        x: jfloat,
        y: jfloat,
        z: jfloat,
    ) {
        let conf = from_ptr::<SceneConfig>(ptr);
        conf.viewer.eye_up = vec3(x as Scalar, y as Scalar, z as Scalar);
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_example_spheremarch_marcher_ConfigBuilder_setSpread(
        _: JNIEnv,
        _: JClass,
        ptr: jlong,
        x: jfloat,
    ) {
        let conf = from_ptr::<SceneConfig>(ptr);
        conf.viewer.spread = x as Scalar;
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_example_spheremarch_marcher_ConfigBuilder_setMaxDrawDistance(
        _: JNIEnv,
        _: JClass,
        ptr: jlong,
        x: jfloat,
    ) {
        let conf = from_ptr::<SceneConfig>(ptr);
        conf.viewer.max_draw_distance = x as Scalar;
    }
}

// -------------------------------------------------------------------------
// Node builder
mod node_builder {
    use super::*;

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_example_spheremarch_marcher_NodeBuilder_createSphere(
        _: JNIEnv,
        _: JClass,
        x: jfloat,
    ) -> jlong {
        to_ptr(Node {
            shape: crate::scene::ShapeUnion::Sphere(SphereShape {
                radius: x as Scalar,
            }),
            material: Material::default(),
            modifiers: Default::default(),
            op: Default::default(),
        })
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_example_spheremarch_marcher_NodeBuilder_createBox(
        _: JNIEnv,
        _: JClass,
        x: jfloat,
        y: jfloat,
        z: jfloat,
    ) -> jlong {
        to_ptr(Node {
            shape: crate::scene::ShapeUnion::Box(BoxShape {
                half_size: vec3(x, y, z),
            }),
            material: Material::default(),
            modifiers: Default::default(),
            op: Default::default(),
        })
    }
    #[no_mangle]
    pub unsafe extern "system" fn Java_com_example_spheremarch_marcher_NodeBuilder_destroyUnused(
        _: JNIEnv,
        _: JClass,
        ptr: jlong,
    ) {
        drop_ptr::<Node>(ptr)
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_example_spheremarch_marcher_NodeBuilder_addModTranslate(
        _: JNIEnv,
        _: JClass,
        ptr: jlong,
        x: jfloat,
        y: jfloat,
        z: jfloat,
    ) {
        let node = from_ptr::<Node>(ptr);
        node.modifiers.push(Modifier::Translate(vec3(x, y, z)));
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_example_spheremarch_marcher_NodeBuilder_addModScale(
        _: JNIEnv,
        _: JClass,
        ptr: jlong,
        x: jfloat,
        y: jfloat,
        z: jfloat,
    ) {
        let node = from_ptr::<Node>(ptr);
        node.modifiers.push(Modifier::Scale(vec3(x, y, z)));
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_example_spheremarch_marcher_NodeBuilder_opUnion(
        _: JNIEnv,
        _: JClass,
        ptr: jlong,
    ) {
        let node = from_ptr::<Node>(ptr);
        node.op = Op::Union;
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_example_spheremarch_marcher_NodeBuilder_opSubtract(
        _: JNIEnv,
        _: JClass,
        ptr: jlong,
    ) {
        let node = from_ptr::<Node>(ptr);
        node.op = Op::Subtract;
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_example_spheremarch_marcher_NodeBuilder_opIntersect(
        _: JNIEnv,
        _: JClass,
        ptr: jlong,
    ) {
        let node = from_ptr::<Node>(ptr);
        node.op = Op::Intersect;
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_example_spheremarch_marcher_NodeBuilder_opSmoothUnion(
        _: JNIEnv,
        _: JClass,
        ptr: jlong,
        arg: jfloat,
    ) {
        let node = from_ptr::<Node>(ptr);
        node.op = Op::SmoothUnion(arg as Scalar);
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_example_spheremarch_marcher_NodeBuilder_opSmoothSubtract(
        _: JNIEnv,
        _: JClass,
        ptr: jlong,
        arg: jfloat,
    ) {
        let node = from_ptr::<Node>(ptr);
        node.op = Op::SmoothSubtract(arg as Scalar);
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_example_spheremarch_marcher_NodeBuilder_opSmoothIntersect(
        _: JNIEnv,
        _: JClass,
        ptr: jlong,
        arg: jfloat,
    ) {
        let node = from_ptr::<Node>(ptr);
        node.op = Op::SmoothIntersect(arg as Scalar);
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_example_spheremarch_marcher_NodeBuilder_matDiffuse(
        _: JNIEnv,
        _: JClass,
        ptr: jlong,
        r: jfloat,
        g: jfloat,
        b: jfloat,
    ) {
        let node = from_ptr::<Node>(ptr);
        node.material.diffuse = vec4(r, g, b, 1.0);
    }
}
