//! Portal传输对象占位。

use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, Deserialize, Serialize)]
pub struct XxxRequest {
    pub id: Option<String>,
}

#[derive(Debug, Clone, Deserialize, Serialize)]
pub struct XxxResponse {
    pub id: Option<String>,
    pub name: Option<String>,
}
