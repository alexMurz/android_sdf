pub mod scene;
pub mod ty;

#[cfg(all(feature = "async", feature = "wgpu"))]
compile_error!("async and wgpu features active at the same time!");

#[cfg(feature = "wgpu")]
#[path = "./gpu_processor/mod.rs"]
pub mod processor;

#[cfg(not(feature = "wgpu"))]
#[path = "./cpu_processor/mod.rs"]
pub mod processor;

#[cfg(target_os = "android")]
mod jni_bindings;

static INIT_ONCE: std::sync::Once = std::sync::Once::new();

pub fn init() {
    INIT_ONCE.call_once(|| {
        #[cfg(target_os = "android")]
        {
            // android_log::init("SphereMarcherNative").unwrap();
        }

        #[cfg(not(target_os = "android"))]
        {
            simple_logger::init().unwrap();
        }
    })
}
