from __future__ import annotations
from typing import Any, Generic, TypeVar
from pydantic import BaseModel, ConfigDict

T = TypeVar("T")

class ResponseCode:
    SUCCESS = ("0000", "成功")
    UN_ERROR = ("0001", "未知失败")
    ILLEGAL_PARAMETER = ("0400", "非法参数")
    NOT_FOUND = ("0404", "资源不存在")
    METHOD_NOT_ALLOWED = ("0405", "请求方法不支持")

class Response(BaseModel, Generic[T]):
    model_config = ConfigDict(populate_by_name=True)
    code: str
    message: str
    data: T | None = None

    @classmethod
    def success(cls, data: T | None = None) -> Response[T]:
        c, m = ResponseCode.SUCCESS
        return cls(code=c, message=m, data=data)

    @classmethod
    def fail(cls, code: str, message: str) -> Response[Any]:
        return cls(code=code, message=message, data=None)

    @classmethod
    def fail_code(cls, pair: tuple[str, str]) -> Response[Any]:
        return cls.fail(pair[0], pair[1])

class BizError(Exception):
    def __init__(self, code: str, message: str, http_status: int = 400) -> None:
        super().__init__(message)
        self.code = code
        self.message = message
        self.http_status = http_status if http_status > 0 else 400
