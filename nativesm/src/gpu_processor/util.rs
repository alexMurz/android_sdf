pub struct BufferDimensions {
    pub width: usize,
    pub height: usize,
    pub unpadded_bytes_per_row: usize,
    pub padded_bytes_per_row: usize,
}

impl BufferDimensions {
    #[inline]
    pub fn size_bytes(&self) -> u64 {
        (self.padded_bytes_per_row * self.height) as u64
    }

    pub fn new(width: usize, height: usize, bytes_per_pixel: usize) -> Self {
        let unpadded_bytes_per_row = width * bytes_per_pixel;
        let align = wgpu::COPY_BYTES_PER_ROW_ALIGNMENT as usize;
        let padded_bytes_per_row_padding = (align - unpadded_bytes_per_row % align) % align;
        let padded_bytes_per_row = unpadded_bytes_per_row + padded_bytes_per_row_padding;
        Self {
            width,
            height,
            unpadded_bytes_per_row,
            padded_bytes_per_row,
        }
    }
}

pub async fn create_wgpu() -> (wgpu::Device, wgpu::Queue) {
    let instance = wgpu::Instance::new(wgpu::Backends::PRIMARY);
    let adapter = instance
        .request_adapter(&wgpu::RequestAdapterOptions::default())
        .await
        .unwrap();

    adapter
        .request_device(
            &wgpu::DeviceDescriptor {
                label: None,
                features: wgpu::Features::PUSH_CONSTANTS,
                limits: wgpu::Limits {
                    max_push_constant_size: super::ViewerPod::SIZE,
                    ..wgpu::Limits::downlevel_defaults()
                },
            },
            None,
        )
        .await
        .unwrap()
}

pub fn buffer_async_map(
    mode: wgpu::MapMode,
    slice: wgpu::BufferSlice,
) -> futures::channel::oneshot::Receiver<Result<(), wgpu::BufferAsyncError>> {
    let (send, recv) = futures::channel::oneshot::channel();
    slice.map_async(mode, move |x| {
        send.send(x).ok();
    });
    recv
}

pub fn buffer_blocking_map(
    dev: &wgpu::Device,
    mode: wgpu::MapMode,
    slice: wgpu::BufferSlice,
) -> Result<(), wgpu::BufferAsyncError> {
    let mut recv = buffer_async_map(mode, slice);

    loop {
        dev.poll(wgpu::MaintainBase::Poll);
        match recv.try_recv() {
            Ok(Some(v)) => return v,
            Ok(None) => (),
            Err(e) => panic!("Cancellation not expected {e}"),
        }
    }
}
