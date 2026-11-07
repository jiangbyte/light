//! 出站端口实现占位。

use domain::template::adapter::port::IXxxPort;

pub struct XxxPort;

impl IXxxPort for XxxPort {
    fn fetch_external(&self, id: &str) -> String {
        format!("external-{id}")
    }
}
