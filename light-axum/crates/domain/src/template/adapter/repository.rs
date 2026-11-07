//! 仓储端口占位；实现在 infrastructure。

use crate::template::model::entity::XxxEntity;

pub trait IXxxRepository {
    fn find_by_id(&self, id: &str) -> Option<XxxEntity>;
}
