use wgpu::util::DeviceExt;

use crate::scene::Viewer;

use super::grepr::ViewerPod;

static VERTICES: &[f32] = &[
    -1.0, -1.0, -1.0, 1.0, 1.0, 1.0, -1.0, -1.0, 1.0, 1.0, 1.0, -1.0,
];

pub struct Renderer {
    pipeline: wgpu::RenderPipeline,
    vbo: wgpu::Buffer,
}

impl Renderer {
    pub fn new(dev: &wgpu::Device, target_format: wgpu::TextureFormat) -> Self {
        let vbo = dev.create_buffer_init(&wgpu::util::BufferInitDescriptor {
            label: Some("VBO"),
            contents: bytemuck::cast_slice(VERTICES),
            usage: wgpu::BufferUsages::VERTEX,
        });

        let module_vs_desc = wgpu::include_spirv!("../../shader/sm.vs.spv");
        let module_fs_desc = wgpu::include_spirv!("../../shader/sm.fs.spv");

        let vs_module = dev.create_shader_module(module_vs_desc);
        let fs_module = dev.create_shader_module(module_fs_desc);

        let pipeline_layout = dev.create_pipeline_layout(&wgpu::PipelineLayoutDescriptor {
            label: Some("RenderPipelineLayout"),
            bind_group_layouts: &[],
            push_constant_ranges: &[wgpu::PushConstantRange {
                range: 0..ViewerPod::SIZE,
                stages: wgpu::ShaderStages::VERTEX | wgpu::ShaderStages::FRAGMENT,
            }],
        });

        let pipeline = dev.create_render_pipeline(&wgpu::RenderPipelineDescriptor {
            label: Some("RenderPipeline"),
            layout: Some(&pipeline_layout),
            vertex: wgpu::VertexState {
                module: &vs_module,
                entry_point: "main",
                buffers: &[wgpu::VertexBufferLayout {
                    array_stride: wgpu::VertexFormat::Float32x2.size(),
                    step_mode: wgpu::VertexStepMode::Vertex,
                    attributes: &[wgpu::VertexAttribute {
                        format: wgpu::VertexFormat::Float32x2,
                        offset: 0,
                        shader_location: 0,
                    }],
                }],
            },
            primitive: wgpu::PrimitiveState::default(),
            depth_stencil: None,
            multisample: wgpu::MultisampleState {
                count: 1,
                mask: !0,
                alpha_to_coverage_enabled: false,
            },
            fragment: Some(wgpu::FragmentState {
                module: &fs_module,
                entry_point: "main",
                targets: &[Some(wgpu::ColorTargetState {
                    format: target_format,
                    blend: None,
                    write_mask: wgpu::ColorWrites::COLOR,
                })],
            }),
            multiview: None,
        });

        Self {
            pipeline,
            vbo,
        }
    }

    #[inline]
    pub fn create_target(&self, dev: &wgpu::Device, w: u32, h: u32) -> wgpu::Texture {
        dev.create_texture(&wgpu::TextureDescriptor {
            label: None,
            size: wgpu::Extent3d {
                width: w,
                height: h,
                depth_or_array_layers: 1,
            },
            mip_level_count: 1,
            sample_count: 1,
            dimension: wgpu::TextureDimension::D2,
            format: wgpu::TextureFormat::Rgba8UnormSrgb,
            usage: wgpu::TextureUsages::RENDER_ATTACHMENT | wgpu::TextureUsages::COPY_SRC,
        })
    }

    pub fn render(
        &self,
        viewer: &Viewer,
        width: u32,
        height: u32,
        enc: &mut wgpu::CommandEncoder,
        target: &wgpu::TextureView,
    ) {
        let Self {
            pipeline,
            vbo,
        } = self;

        let mut pass = enc.begin_render_pass(&wgpu::RenderPassDescriptor {
            label: None,
            color_attachments: &[Some(wgpu::RenderPassColorAttachment {
                view: &target,
                resolve_target: None,
                ops: wgpu::Operations {
                    load: wgpu::LoadOp::Clear(wgpu::Color::RED),
                    store: true,
                },
            })],
            depth_stencil_attachment: None,
        });

        pass.set_pipeline(pipeline);
        pass.set_viewport(0.0, 0.0, width as f32, height as f32, 0.0, 1.0);
        pass.set_scissor_rect(0, 0, width, height);
        pass.set_vertex_buffer(0, vbo.slice(..));

        let pod = ViewerPod::from(viewer);
        pass.set_push_constants(
            wgpu::ShaderStages::VERTEX | wgpu::ShaderStages::FRAGMENT,
            0,
            bytemuck::bytes_of(&pod),
        );

        pass.draw(0..6, 0..1);
    }
}
