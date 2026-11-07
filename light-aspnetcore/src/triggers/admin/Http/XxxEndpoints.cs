using Apis.Admin;
using Apis.Admin.Dto;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Routing;
using types;

namespace Triggers.Admin.Http;

/// <summary>HTTP 占位示例路由。</summary>
public static class XxxEndpoints
{
    public static IEndpointRouteBuilder MapXxx(this IEndpointRouteBuilder endpoints)
    {
        var group = endpoints.MapGroup("/admin/xxx");
        group.MapGet("/query", (string? id) =>
        {
            // 1. 组装入参
            var req = new XxxRequestDto { Id = id };
            // 2. 调用领域（示例直接返回）
            var data = new XxxResponseDto { Id = req.Id, Name = "admin-xxx-placeholder" };
            // 3. 统一 Response
            return Results.Json(Response<object>.Success(data));
        });
        return endpoints;
    }
}
