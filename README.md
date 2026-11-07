# Light

以 **DDD + 六边形架构** 为核心的多实现系列。  
跨栈统一**分组**：`apis` / `triggers` / `apps`，系列成员用 `admin` / `portal` 辨识（不强制连字符）。  
Spring 仓内仍用 `light-*` 模块名与聚合目录习惯。

## 实现一览

| 目录 | 技术载体 | 工程形态 |
|------|----------|----------|
| [light-spring](light-spring/) | Spring Boot | Maven 多模块 |
| [light-aspnetcore](light-aspnetcore/) | ASP.NET Core | Solution + Projects |
| [light-gin](light-gin/) | Gin | 单 Go module |
| [light-fastapi](light-fastapi/) | FastAPI | 顶层包 |
| [light-axum](light-axum/) | Axum | Cargo workspace |

## 分层对照

| 角色 | Spring | 其他栈（目录） |
|------|--------|----------------|
| 共享内核 | `light-types` | `types`（Python：`types_`） |
| 领域 | `light-domain` | `domain` |
| 出站适配器 | `light-infrastructure` | `infrastructure` |
| 入站契约 | `light-apis/light-api-*` | `apis/admin` · `apis/portal` |
| 入站适配器 | `light-triggers/light-trigger-*` | `triggers/admin` · `triggers/portal` |
| 组装启动 | `light-apps/light-*-app` | `apps/admin` · `apps/portal` |

依赖规则：domain 不依赖 Web/DB；infrastructure 实现端口；trigger 调 domain；app 只组装。

各栈均提供占位骨架：`domain/template`（或等价命名空间）限界上下文目录、分层说明，以及 `Xxx*` 命名示例（复制后改名）。

## 统一应用约定

- 响应：`code` / `message` / `data`
- JSON：snake_case；数字尽量按字符串输出
- 404 → JSON；Admin `:8081`，Portal `:8080`

## 启动摘要

```bash
cd light-spring && mvn -pl light-apps/light-admin-app spring-boot:run
cd light-aspnetcore && dotnet run --project src/apps/admin
cd light-gin && go run ./apps/admin
cd light-fastapi && .venv/bin/uvicorn apps.admin.main:app --port 8081
cd light-axum && cargo run -p apps_admin
```
