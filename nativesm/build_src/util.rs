
pub trait UnwrapDisplay<T> {
    fn unwrap_display(self) -> T;
}

impl<T, E: std::fmt::Display> UnwrapDisplay<T> for std::result::Result<T, E> {
    fn unwrap_display(self) -> T {
        match self {
            Ok(v) => v,
            Err(e) => panic!("{}", e.to_string()),
        }
    }
}
