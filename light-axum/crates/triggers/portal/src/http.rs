//! Portal HTTP；路径建议 /portal。

use axum::{routing::get, Json, Router};
use apis_portal::{XxxRequest, XxxResponse};
use types::Response;

pub fn xxx_router() -> Router {
    Router::new().route("/portal/xxx/query", get(query))
}

async fn query(axum::extract::Query(req): axum::extract::Query<XxxRequest>) -> Json<Response<XxxResponse>> {
    // 1. 入参已绑定
    // 2. 调用领域（示例直接返回）
    let data = XxxResponse {
        id: req.id.clone(),
        name: Some("portal-xxx-placeholder".into()),
    };
    // 3. 统一 Response
    Json(Response::success(Some(data)))
}
