//! 聚合占位。

use super::entity::XxxEntity;

#[derive(Debug, Clone)]
pub struct XxxAggregate {
    pub entity: Option<XxxEntity>,
}
