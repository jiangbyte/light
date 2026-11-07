"""管理端 API 契约占位；由 triggers.admin.http 实现。"""

from apis.admin.dto import XxxRequest, XxxResponse

class XxxService:
    def query(self, request: XxxRequest) -> XxxResponse:
        raise NotImplementedError
