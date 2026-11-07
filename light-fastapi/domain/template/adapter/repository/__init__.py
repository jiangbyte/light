"""仓储端口占位；由 infrastructure 实现。"""

from typing import Protocol
from domain.template.model.entity import XxxEntity

class IXxxRepository(Protocol):
    def find_by_id(self, id: str) -> XxxEntity | None: ...
