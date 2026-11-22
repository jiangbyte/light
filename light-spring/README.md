# light-spring

Light 系列的 **Spring Boot** 实现：DDD + 六边形（Maven 多模块）。

账户领域已提供完整的实体、端口、持久化、API 和 Trigger 示例，新增业务按相同分层直接扩展。

## 启动

```bash
cd light-spring
export LIGHT_BOOTSTRAP_ADMIN_USERNAME=admin
export LIGHT_BOOTSTRAP_ADMIN_PASSWORD='Admin@123456'
mvn -DskipTests install
mvn -pl light-apps/light-admin-app spring-boot:run   # :8081
mvn -pl light-apps/light-portal-app spring-boot:run  # :8080
```

默认使用本机 MySQL `127.0.0.1:3306/light` 和 Redis `127.0.0.1:6379`。连接参数可通过
`LIGHT_DB_URL`、`LIGHT_DB_USERNAME`、`LIGHT_DB_PASSWORD`、`LIGHT_REDIS_HOST`、
`LIGHT_REDIS_PORT`、`LIGHT_REDIS_PASSWORD`、`LIGHT_REDIS_DATABASE` 覆盖。

## 账户体系

- Admin 与 Portal 分表：`light_admin_user`、`light_portal_user`，由 Flyway 自动建表。
- 登录标识支持用户名、邮箱和手机号，登录接口分别为 `login/username`、`login/email`、`login/phone`。
- 两端接口统一由控制器声明 `/api`，不配置全局 `context-path`；Admin CRUD 路径为
  `/api/accounts/admins` 和 `/api/accounts/portal-users`。
- 所有业务接口只接受 GET/POST，不使用路径变量；详情使用 `?id=`，更新和删除通过 POST 请求体传 ID。
- 删除请求使用 `{ "ids": ["1"] }`，单个删除和批量删除共用同一数组契约。
- 登录成功后返回不透明 Sa-Token 令牌。后续请求将原始令牌直接放入
  `Authorization` 请求头，值中不添加任何认证方案前缀。
- 登录、创建账户和修改密码时，`password` 必须使用传输密钥加密；
  客户端先通过 `/api/auth/transport-key` 获取当前端密钥。服务端拒绝明文密码。
- 令牌最长 7 天、连续 2 小时无请求失效，Admin/Portal 令牌不能跨域使用。
- Sa-Token 配置分别位于 Admin 的 `light.sa-token.admin` 和 Portal 的
  `light.sa-token.portal`，两端独立创建 Redis DAO、StpLogic 和会话端口。
- 所有 `LocalDateTime` 字段统一使用 `yyyy-MM-dd HH:mm:ss` 读写。
- 领域实体到 API DTO 的转换使用 MapStruct 在编译期生成，避免手写 setter 映射。
- 分页响应统一使用泛型 `PageResult<T>`；查询参数封装为业务查询对象，并由 GET 参数 DTO 绑定。

首个管理员仅在 `light_admin_user` 为空时创建，配置项为
`LIGHT_BOOTSTRAP_ADMIN_USERNAME`、`LIGHT_BOOTSTRAP_ADMIN_PASSWORD`、
`LIGHT_BOOTSTRAP_ADMIN_EMAIL`、`LIGHT_BOOTSTRAP_ADMIN_PHONE`。

生产环境必须配置固定的 SM2 密钥对：

- Admin：`LIGHT_SM2_ADMIN_PRIVATE_KEY`、`LIGHT_SM2_ADMIN_PUBLIC_KEY`
- Portal：`LIGHT_SM2_PORTAL_PRIVATE_KEY`、`LIGHT_SM2_PORTAL_PUBLIC_KEY`

密钥格式为 Base64 编码的 PKCS#8 私钥和 X.509 公钥。未配置时本地会生成临时密钥并输出警告。

## 模块角色

| 角色 | 模块 |
|------|------|
| 共享内核 | `light-types` |
| 领域 | `light-domain` |
| 出站适配器 | `light-infrastructure` |
| 入站契约 | `light-apis/*` |
| 入站适配器 | `light-triggers/*` |
| 组装启动 | `light-apps/*` |

## 目录组织

实现代码统一按“业务领域 → 子模块 → 技术适配”组织，避免在 API、Trigger、Infrastructure
根目录下平铺所有业务的 DTO、Controller、DAO。例如账户领域：

```text
domain/account
api/admin/account/auth
api/admin/account/management
trigger/admin/account/auth/http
trigger/admin/account/auth/application
trigger/admin/account/management/http
trigger/admin/account/management/application
infrastructure/account/auth
infrastructure/account/persistence
```

新增业务时参照 `account` 的领域目录结构，在 domain、api、trigger、infrastructure 中使用
相同的领域目录名。
