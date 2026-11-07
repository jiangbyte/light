//! 管理端 API 契约占位；由 triggers/admin 实现。

pub mod dto;
pub mod response;


pub use dto::{XxxRequest, XxxResponse};

pub trait XxxService {
    fn query(&self, request: XxxRequest) -> XxxResponse;
}
