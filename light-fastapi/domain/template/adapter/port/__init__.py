"""出站端口占位；访问外部系统时声明。"""

from typing import Protocol

class IXxxPort(Protocol):
    def fetch_external(self, id: str) -> str: ...
