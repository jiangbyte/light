//! 出站端口占位。

pub trait IXxxPort {
    fn fetch_external(&self, id: &str) -> String;
}
