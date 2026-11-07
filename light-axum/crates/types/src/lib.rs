//! 共享内核：统一响应与通用类型；不含领域模型与 Web 依赖。

pub mod constants;
pub mod enums;
pub mod event;
pub mod exception;
pub mod utils;

use serde::Serialize;

#[derive(Debug, Serialize)]
pub struct Response<T: Serialize> {
    pub code: String,
    pub message: String,
    pub data: Option<T>,
}

impl Response<()> {
    pub fn fail(code: impl Into<String>, message: impl Into<String>) -> Self {
        Self { code: code.into(), message: message.into(), data: None }
    }
}

impl<T: Serialize> Response<T> {
    pub fn success(data: Option<T>) -> Self {
        Self { code: "0".into(), message: "成功".into(), data }
    }
}

pub const NOT_FOUND: (&str, &str) = ("404", "资源不存在");
pub const METHOD_NOT_ALLOWED: (&str, &str) = ("405", "请求方法不支持");
