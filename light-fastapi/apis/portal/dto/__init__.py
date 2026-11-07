"""Portal传输对象。字段精简；勿泄漏运营信息。"""

from pydantic import BaseModel

class XxxRequest(BaseModel):
    id: str | None = None

class XxxResponse(BaseModel):
    id: str | None = None
    name: str | None = None
