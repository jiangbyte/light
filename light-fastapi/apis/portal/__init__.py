"""Portal API 契约占位；由 triggers.portal.http 实现。"""

from apis.portal.dto import XxxRequest, XxxResponse

class XxxService:
    def query(self, request: XxxRequest) -> XxxResponse:
        raise NotImplementedError
