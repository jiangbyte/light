"""仓储实现占位。"""

from domain.template.model.entity import XxxEntity

class XxxRepository:
    def find_by_id(self, id: str) -> XxxEntity | None:
        return XxxEntity(id=id)
