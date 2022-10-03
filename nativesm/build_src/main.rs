use std::{
    collections::HashMap,
    fs,
    path::{Path, PathBuf},
};

use file::*;
use shaderc::{CompileOptions, Compiler};
use util::*;

mod file;
mod util;

pub const SHADER_SRC: &'static str = "./shader_sources";
pub const SHADER_DST: &'static str = "./shader";

enum Change {
    Create(CreateChange),
    Update(UpdateChange),
    Delete(DeleteChange),
}

struct CreateChange {
    pub src: FileRef,
    pub dst: PathBuf,
    pub ty: ShaderFileType,
}

struct UpdateChange {
    pub src: FileRef,
    pub dst: FileRef,
    pub ty: ShaderFileType,
}

struct DeleteChange {
    pub dst: FileRef,
}

#[derive(Debug, Clone, Copy, PartialEq, Eq, PartialOrd, Ord)]
pub enum ShaderFileType {
    Vertex,
    Fragment,
    Compute,
}

impl ShaderFileType {
    pub fn from_file_extension(ext: &str) -> Option<Self> {
        let ext = if ext.starts_with(".") { &ext[1..] } else { ext };
        let ty = match ext {
            "vs" => Self::Vertex,
            "fs" => Self::Fragment,
            "comp" => Self::Compute,
            _ => return None,
        };
        Some(ty)
    }

    pub fn extension(self) -> &'static str {
        match self {
            Self::Vertex => "vs",
            Self::Fragment => "fs",
            Self::Compute => "comp",
        }
    }

    pub fn glsl_type(self) -> shaderc::ShaderKind {
        match self {
            Self::Vertex => shaderc::ShaderKind::Vertex,
            Self::Fragment => shaderc::ShaderKind::Fragment,
            Self::Compute => shaderc::ShaderKind::Compute,
        }
    }
}

fn main() {
    println!("PREPARE - Load shaderc");
    let compile = shaderc::Compiler::new().unwrap();
    let options = shaderc::CompileOptions::new().unwrap();

    println!("BEGIN - compile_shaders");
    let changes = collect_changes();

    println!("{} changes found", changes.len());

    for c in changes.into_iter() {
        match c {
            Change::Create(change) => {
                println!(
                    "Create: {:?} -> {:?} :: {:?}",
                    change.src.path, change.dst, change.ty
                );
                compile_and_save(change.src, change.dst, change.ty, &compile, Some(&options))
                    .unwrap_display();
            }
            Change::Update(change) => {
                println!(
                    "Update: {:?} -> {:?} :: {:?}",
                    change.src.path, change.dst.path, change.ty
                );
                compile_and_save(
                    change.src,
                    change.dst.path,
                    change.ty,
                    &compile,
                    Some(&options),
                )
                .unwrap_display();
            }
            Change::Delete(change) => {
                println!("Delete: {}", change.dst.name);
                fs::remove_file(change.dst.path).unwrap_display();
            }
        }
    }

    println!("END - compile_shaders");
}

fn compile_and_save(
    src: FileRef,
    dst: impl AsRef<Path>,
    ty: ShaderFileType,
    compiler: &Compiler,
    options: Option<&CompileOptions>,
) -> Result<(), String> {
    let code = fs::read_to_string(&src.path)
        .map_err(|err| format!("Read code error: {}", err.to_string()))?;

    let name = src.name;
    let artifact = compiler
        .compile_into_spirv(&code, ty.glsl_type(), &name, "main", options)
        .map_err(|err| format!("Compile error {}", err.to_string()))?;

    fs::write(dst, artifact.as_binary_u8())
        .map_err(|err| format!("Write spir output error: {}", err.to_string()))?;

    Ok(())
}

fn collect_changes() -> Vec<Change> {
    let inputs = collect_dir(crate::SHADER_SRC);
    fs::create_dir_all(crate::SHADER_DST).ok();
    let mut outputs = collect_dir(crate::SHADER_DST);

    let mut changes = Vec::new();

    for (k, from) in inputs.into_iter() {
        let ty = match ShaderFileType::from_file_extension(&from.ext) {
            Some(v) => v,
            None => continue,
        };
        let to = outputs.remove(&k);

        if let Some(to) = to {
            if from > to {
                // Target is older then source
                changes.push(Change::Update(UpdateChange {
                    src: from,
                    dst: to,
                    ty,
                }));
            } else {
                // Do nothing, file not update
            }
        } else {
            // Target does not exist
            let mut dst = PathBuf::new();
            dst.push(crate::SHADER_DST);
            dst.push(format!("{}.{}.spv", from.name, ty.extension()));

            changes.push(Change::Create(CreateChange { src: from, dst, ty }))
        }
    }

    for (_, dst) in outputs.into_iter() {
        changes.push(Change::Delete(DeleteChange { dst }));
    }

    changes
}

fn collect_dir(dir_path: &str) -> HashMap<String, FileRef> {
    let dir = fs::read_dir(dir_path).unwrap_display();

    let mut result = HashMap::new();
    for v in dir {
        match v {
            Ok(entry) => {
                let file = FileRef::new(entry.path()).expect("Create FileRef from");

                // Convert `frag.spv` -> `frag`
                let ext_split = file.ext.find('.');
                let ext = if let Some(split) = ext_split {
                    &file.ext[..split]
                } else {
                    &file.ext
                };

                let name = format!("{}.{}", file.name, ext);
                result.insert(name, file);
            }
            Err(e) => {
                eprintln!("Read input files error {:?}", e);
            }
        }
    }

    result
}
