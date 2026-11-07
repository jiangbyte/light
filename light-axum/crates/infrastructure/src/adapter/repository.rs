//! 仓储实现占位。

use domain::template::adapter::repository::IXxxRepository;
use domain::template::model::entity::XxxEntity;

pub struct XxxRepository;

impl IXxxRepository for XxxRepository {
    fn find_by_id(&self, id: &str) -> Option<XxxEntity> {
        Some(XxxEntity { id: id.to_string() })
    }
}
