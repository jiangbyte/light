"""数据访问占位。"""

from typing import Protocol
from infrastructure.dao.po import XxxPO

class IXxxDao(Protocol):
    def select_by_id(self, id: str) -> XxxPO | None: ...
