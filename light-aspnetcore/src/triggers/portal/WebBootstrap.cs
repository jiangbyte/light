using System.Text.Encodings.Web;
using System.Text.Json;
using System.Text.Json.Serialization;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.DependencyInjection;
using types;

using Triggers.Portal.Http;

namespace Triggers.Portal;

/// <summary>
/// 组装统一 JSON 约定与 HTTP 管道。
/// </summary>
public static class WebBootstrap
{
    public static WebApplication BuildApp(string[] args, string appName, int port)
    {
        // 1. 配置 snake_case + 数字一律按字符串写出
        var jsonOptions = new JsonSerializerOptions
        {
            PropertyNamingPolicy = JsonNamingPolicy.SnakeCaseLower,
            DictionaryKeyPolicy = JsonNamingPolicy.SnakeCaseLower,
            NumberHandling = JsonNumberHandling.WriteAsString,
            PropertyNameCaseInsensitive = true,
            Encoder = JavaScriptEncoder.UnsafeRelaxedJsonEscaping
        };

        var builder = WebApplication.CreateBuilder(args);
        builder.Services.ConfigureHttpJsonOptions(o =>
        {
            o.SerializerOptions.PropertyNamingPolicy = JsonNamingPolicy.SnakeCaseLower;
            o.SerializerOptions.DictionaryKeyPolicy = JsonNamingPolicy.SnakeCaseLower;
            o.SerializerOptions.NumberHandling = JsonNumberHandling.WriteAsString;
            o.SerializerOptions.Encoder = JavaScriptEncoder.UnsafeRelaxedJsonEscaping;
        });

        var app = builder.Build();

        // 2. 统一异常与 404 JSON，避免默认错误页
        app.Use(async (context, next) =>
        {
            try
            {
                await next();
                if (context.Response.StatusCode == StatusCodes.Status404NotFound && !context.Response.HasStarted)
                {
                    context.Response.ContentType = "application/json; charset=utf-8";
                    await JsonSerializer.SerializeAsync(context.Response.Body, Response<object>.Fail(ResponseCode.NotFound), jsonOptions);
                }
            }
            catch (BizException ex)
            {
                context.Response.StatusCode = ex.HttpStatus;
                context.Response.ContentType = "application/json; charset=utf-8";
                await JsonSerializer.SerializeAsync(context.Response.Body, Response<object>.Fail(ex.Code, ex.Message), jsonOptions);
            }
            catch (Exception)
            {
                context.Response.StatusCode = StatusCodes.Status500InternalServerError;
                context.Response.ContentType = "application/json; charset=utf-8";
                await JsonSerializer.SerializeAsync(context.Response.Body, Response<object>.Fail(ResponseCode.UnError), jsonOptions);
            }
        });

        // 3. 绑定监听地址
        app.Urls.Clear();
        app.Urls.Add($"http://0.0.0.0:{port}");

        app.MapXxx();
        app.MapGet("/health", () => Results.Json(Response<object>.Success(new { app = appName }), jsonOptions));
        return app;
    }
}
