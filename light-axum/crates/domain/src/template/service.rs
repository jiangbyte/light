//! 领域服务占位：编排实体与端口。

use crate::template::adapter::repository::IXxxRepository;
use crate::template::model::entity::XxxEntity;

pub struct XxxService<R: IXxxRepository> {
    pub repo: R,
}

impl<R: IXxxRepository> XxxService<R> {
    pub fn load(&self, id: &str) -> Option<XxxEntity> {
        // 1. 通过仓储加载
        // 2. 可做跨实体校验
        self.repo.find_by_id(id)
    }
}
