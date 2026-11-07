from __future__ import annotations

from fastapi import FastAPI, Request
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from starlette.exceptions import HTTPException as StarletteHTTPException

from types_ import BizError, Response, ResponseCode
from triggers.portal.http import build_xxx_router


def create_app(app_name: str) -> FastAPI:
    # 1. 创建应用并关闭文档入口
    app = FastAPI(title=app_name, docs_url=None, redoc_url=None)

    # 2. 业务异常 → 统一 Response
    @app.exception_handler(BizError)
    async def biz_handler(_: Request, exc: BizError) -> JSONResponse:
        return JSONResponse(status_code=exc.http_status, content=Response.fail(exc.code, exc.message).model_dump())

    # 3. HTTP 异常（含 404/405）→ 统一 JSON
    @app.exception_handler(StarletteHTTPException)
    async def http_handler(_: Request, exc: StarletteHTTPException) -> JSONResponse:
        if exc.status_code == 404:
            body = Response.fail_code(ResponseCode.NOT_FOUND)
        elif exc.status_code == 405:
            body = Response.fail_code(ResponseCode.METHOD_NOT_ALLOWED)
        else:
            body = Response.fail(str(exc.status_code), str(exc.detail))
        return JSONResponse(status_code=exc.status_code, content=body.model_dump())

    # 4. 参数校验失败
    @app.exception_handler(RequestValidationError)
    async def validation_handler(_: Request, __: RequestValidationError) -> JSONResponse:
        return JSONResponse(status_code=400, content=Response.fail_code(ResponseCode.ILLEGAL_PARAMETER).model_dump())

    # 5. 兜底未知异常
    @app.exception_handler(Exception)
    async def unhandled(_: Request, __: Exception) -> JSONResponse:
        return JSONResponse(status_code=500, content=Response.fail_code(ResponseCode.UN_ERROR).model_dump())

    # 6. 注册占位示例路由与健康检查
    app.include_router(build_xxx_router())

    @app.get("/health")
    async def health() -> dict:
        return Response.success({"app": app_name}).model_dump()

    return app
