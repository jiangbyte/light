"""管理端传输对象。可含运营字段；勿与 Portal DTO 强行共用。"""

from pydantic import BaseModel

class XxxRequest(BaseModel):
    id: str | None = None

class XxxResponse(BaseModel):
    id: str | None = None
    name: str | None = None
