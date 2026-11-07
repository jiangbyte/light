# light-gin

Gin 实现。分组：`apis` / `triggers` / `apps`，系列内用 `admin` / `portal`。

占位约定：`domain/template` 为限界上下文骨架；各层 `doc.go` 说明放什么；`Xxx*` 为命名示例，复制后改名。

```bash
go run ./apps/admin   # :8081
go run ./apps/portal  # :8080
```

| 角色 | 路径 |
|------|------|
| types | `types`（含 constants/enums/event/…） |
| domain | `domain` + `domain/template/...` |
| infrastructure | `infrastructure`（dao/redis/gateway/adapter） |
| api | `apis/admin` · `apis/portal` |
| trigger | `triggers/*/http|job|listener` |
| app | `apps/admin` · `apps/portal` |
