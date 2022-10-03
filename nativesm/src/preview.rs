use std::mem::size_of;

use cgmath::vec3;
use nativesm::{
    processor::{grepr::ViewerPod, renderer::Renderer},
    scene::Viewer,
};
use winit::{
    event::{DeviceEvent, ElementState, Event, KeyboardInput, VirtualKeyCode, WindowEvent},
    event_loop::{ControlFlow, EventLoop},
    window::Window,
};

async fn run(event_loop: EventLoop<()>, window: Window) {
    let size = window.inner_size();
    let instance = wgpu::Instance::new(wgpu::Backends::PRIMARY);

    let surface = unsafe { instance.create_surface(&window) };
    let adapter = instance
        .request_adapter(&wgpu::RequestAdapterOptions {
            power_preference: wgpu::PowerPreference::default(),
            force_fallback_adapter: false,
            // Request an adapter which can render to our surface
            compatible_surface: Some(&surface),
        })
        .await
        .expect("Failed to find an appropriate adapter");

    // Create the logical device and command queue
    let (dev, queue) = adapter
        .request_device(
            &wgpu::DeviceDescriptor {
                label: None,
                features: wgpu::Features::PUSH_CONSTANTS,
                // Make sure we use the texture resolution limits from the adapter, so we can support images the size of the swapchain.
                limits: wgpu::Limits {
                    max_push_constant_size: size_of::<ViewerPod>() as u32,
                    ..wgpu::Limits::downlevel_webgl2_defaults().using_resolution(adapter.limits())
                },
            },
            None,
        )
        .await
        .expect("Failed to create device");

    let swapchain_format = surface.get_supported_formats(&adapter)[0];
    let ren = Renderer::new(&dev, swapchain_format);

    let mut config = wgpu::SurfaceConfiguration {
        usage: wgpu::TextureUsages::RENDER_ATTACHMENT,
        format: swapchain_format,
        width: size.width,
        height: size.height,
        present_mode: wgpu::PresentMode::AutoVsync,
    };
    surface.configure(&dev, &config);

    let mut viewer = Viewer {
        eye_pos: vec3(0.0, 2.0, -3.0),
        eye_at: vec3(0.0, 0.0, 0.0),
        eye_up: vec3(0.0, 1.0, 0.0),
        max_draw_distance: 200.0,
        spread: 1.0,
    };

    let mut t = std::time::Instant::now();
    event_loop.run(move |event, _, control_flow| {
        *control_flow = ControlFlow::Poll;
        match event {
            Event::WindowEvent {
                event: WindowEvent::Resized(size),
                ..
            } => {
                config.width = size.width;
                config.height = size.height;
                surface.configure(&dev, &config);
                window.request_redraw();
            }
            Event::DeviceEvent {
                event:
                    DeviceEvent::Key(KeyboardInput {
                        virtual_keycode: Some(code),
                        state: ElementState::Pressed,
                        ..
                    }),
                ..
            } => match code {
                VirtualKeyCode::Escape => *control_flow = ControlFlow::Exit,
                VirtualKeyCode::W => viewer.eye_pos.z += 0.2,
                VirtualKeyCode::S => viewer.eye_pos.z -= 0.2,
                VirtualKeyCode::D => viewer.eye_pos.x += 0.2,
                VirtualKeyCode::A => viewer.eye_pos.x -= 0.2,
                VirtualKeyCode::R => viewer.eye_pos.y += 0.2,
                VirtualKeyCode::F => viewer.eye_pos.y -= 0.2,
                _ => (),
            },
            Event::MainEventsCleared => {
                let t2 = std::time::Instant::now();
                let d = t2.saturating_duration_since(t);
                t = t2;

                log::info!("Delta {:.2} UPS", 1.0 / d.as_secs_f64());

                let frame = surface
                    .get_current_texture()
                    .expect("Failed to acquire next swap chain texture");
                let view = frame
                    .texture
                    .create_view(&wgpu::TextureViewDescriptor::default());
                let mut encoder = dev.create_command_encoder(&Default::default());

                ren.render(&viewer, config.width, config.height, &mut encoder, &view);

                queue.submit(Some(encoder.finish()));
                frame.present();
            }
            Event::WindowEvent {
                event: WindowEvent::CloseRequested,
                ..
            } => *control_flow = ControlFlow::Exit,
            _ => {}
        }
    });
}

fn main() {
    let event_loop = EventLoop::new();
    let window = Window::new(&event_loop).unwrap();
    #[cfg(not(target_arch = "wasm32"))]
    {
        simple_logger::init_with_level(log::Level::Info).unwrap();
        // Temporarily avoid srgb formats for the swapchain on the web
        futures::executor::block_on(run(event_loop, window));
    }
    #[cfg(target_arch = "wasm32")]
    {
        std::panic::set_hook(Box::new(console_error_panic_hook::hook));
        console_log::init().expect("could not initialize logger");
        use winit::platform::web::WindowExtWebSys;
        // On wasm, append the canvas to the document body
        web_sys::window()
            .and_then(|win| win.document())
            .and_then(|doc| doc.body())
            .and_then(|body| {
                body.append_child(&web_sys::Element::from(window.canvas()))
                    .ok()
            })
            .expect("couldn't append canvas to document body");
        wasm_bindgen_futures::spawn_local(run(event_loop, window));
    }
}
