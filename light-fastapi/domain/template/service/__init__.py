"""领域服务占位：编排实体与端口。"""

from domain.template.adapter.repository import IXxxRepository
from domain.template.model.entity import XxxEntity

class XxxService:
    def __init__(self, repository: IXxxRepository) -> None:
        self._repository = repository

    def load(self, id: str) -> XxxEntity | None:
        # 1. 通过仓储加载
        # 2. 按需校验
        return self._repository.find_by_id(id)
