"""管理端 HTTP 路由；路径建议 /admin。"""

from fastapi import APIRouter
from apis.admin.dto import XxxRequest, XxxResponse
from types_ import Response

def build_xxx_router() -> APIRouter:
    router = APIRouter(prefix="/admin/xxx", tags=["xxx"])

    @router.get("/query")
    async def query(id: str | None = None) -> dict:
        # 1. 组装入参
        req = XxxRequest(id=id)
        # 2. 调用领域（示例直接返回）
        data = XxxResponse(id=req.id, name="admin-xxx-placeholder")
        # 3. 统一响应
        return Response.success(data.model_dump()).model_dump()

    return router
