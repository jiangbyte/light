//! 管理端入站适配器：http / job / listener。

pub mod http;
pub mod job;
pub mod listener;

use axum::{http::StatusCode, response::IntoResponse, Json, Router};
use serde::Serialize;
use types::{Response, METHOD_NOT_ALLOWED, NOT_FOUND};

#[derive(Serialize)]
struct HealthBody { app: String }

/// 组装 HTTP 路由与统一 404/405。
pub fn router(app_name: &str) -> Router {
    // 1. 注册健康检查与占位示例
    let name = app_name.to_string();
    Router::new()
        .merge(http::xxx_router())
        .route("/health", axum::routing::get(move || {
            let name = name.clone();
            async move { Json(Response::success(Some(HealthBody { app: name }))) }
        }))
        // 2. 未匹配路径/方法统一 JSON
        .fallback(not_found)
        .method_not_allowed_fallback(method_not_allowed)
}

async fn not_found() -> impl IntoResponse {
    let (c, m) = NOT_FOUND;
    (StatusCode::NOT_FOUND, Json(Response::<()>::fail(c, m)))
}

async fn method_not_allowed() -> impl IntoResponse {
    let (c, m) = METHOD_NOT_ALLOWED;
    (StatusCode::METHOD_NOT_ALLOWED, Json(Response::<()>::fail(c, m)))
}
