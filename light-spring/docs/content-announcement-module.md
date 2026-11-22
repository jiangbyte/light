# 内容领域通知公告模块

通知公告模块用于示范内容领域的标准 CRUD、分页、发布状态机和操作审计。模块面向
Admin 管理端和 Portal 登录用户，不包含附件、定向受众和已读记录。

## 分层目录

```text
light-apis/
├── light-api-admin/.../api/admin/content/announcement/ # Admin 公告契约与 DTO
└── light-api-portal/.../api/portal/content/announcement/ # Portal 公告查询契约与 DTO

light-domain/
└── .../domain/content/announcement/
    ├── adapter/repository/                            # 公告仓储接口
    ├── model/entity/                                  # 公告实体
    ├── model/valobj/                                  # 类型、优先级、状态和分页条件
    └── service/                                       # 公告领域规则

light-triggers/
├── light-trigger-admin/.../trigger/admin/content/announcement/ # Admin HTTP、用例与映射
└── light-trigger-portal/.../trigger/portal/content/announcement/ # Portal HTTP、查询用例与映射

light-infrastructure/
└── src/main/java/io/jiangbyte/github/infrastructure/content/announcement/
    ├── config/                                        # 领域服务装配
    └── persistence/                                   # Repository、DAO、PO
```

## 接口契约

所有接口只使用 GET 和 POST，不使用路径变量。分页接口统一使用 `/page`。

### Admin

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/announcements/page` | 查询全部状态公告 |
| GET | `/api/announcements/detail?id=` | 查询公告详情 |
| POST | `/api/announcements/create` | 创建草稿 |
| POST | `/api/announcements/update` | 修改草稿或已发布公告 |
| POST | `/api/announcements/delete` | 物理批量删除 |
| POST | `/api/announcements/publish` | 发布或重新发布 |
| POST | `/api/announcements/archive` | 归档已发布公告 |

分页参数：

- `page`：默认 1。
- `pageSize`：默认 20，最大 100。
- `keyword`：匹配标题或摘要。
- `type`：`NOTICE`、`ANNOUNCEMENT`、`MAINTENANCE`。
- `priority`：`NORMAL`、`IMPORTANT`、`URGENT`。
- `status`：`DRAFT`、`PUBLISHED`、`ARCHIVED`。

分页默认值和上限统一来自 `light-types` 的 `PageConstants`，领域查询对象和 API
DTO 校验共用同一组常量。

删除请求：

```json
{
  "ids": ["1", "2"]
}
```

删除 DTO 不在公告域内单独定义，而是复用：

```text
light-types/src/main/java/io/jiangbyte/github/types/request/
└── IdsRequestDTO.java                                  # 通用 ID 数组及基础校验
```

账户和公告删除接口共用 `IdsRequestDTO`；单资源操作共用同一个 `IdRequestDTO`，
避免每个新业务重复定义字段完全相同的 DTO。

### Portal

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/announcements/page` | 查询当前已生效公告 |
| GET | `/api/announcements/detail?id=` | 查询当前已生效公告详情 |

Portal 只返回同时满足以下条件的公告：

```text
status = PUBLISHED
AND publish_at <= 当前时间
AND (expire_at IS NULL OR expire_at > 当前时间)
```

## 状态机

```text
DRAFT -> PUBLISHED -> ARCHIVED
ARCHIVED -> PUBLISHED
```

- 创建后固定为 `DRAFT`。
- `DRAFT` 和 `PUBLISHED` 可更新，`ARCHIVED` 不可直接更新。
- `DRAFT` 和 `ARCHIVED` 可发布，`PUBLISHED` 重复发布返回 409。
- 只有 `PUBLISHED` 可归档。
- 归档公告重新发布时，以本次发布请求的发布时间和过期时间为准。
- 未来发布时间和过期时间通过查询过滤实现，不使用后台定时任务修改状态。

## 数据表

```text
light_content_announcement
├── id
├── title
├── summary
├── content
├── type
├── priority
├── status
├── publish_at
├── expire_at
├── created_by
├── updated_by
├── created_at
└── updated_at
```

迁移文件：

```text
light-infrastructure/src/main/resources/db/migration/content/
└── V3__create_content_announcement_table.sql          # 公告表和查询索引
```

MyBatis 扫描同时包含账户 DAO 和公告 DAO；Admin/Portal 的 Flyway 同时加载
`db/migration/account` 与 `db/migration/content`。

## 业务规则

- 标题必填，最长 120 字符。
- 摘要可选，最长 500 字符。
- 正文必填，最长 100000 字符。
- 过期时间存在时必须晚于生效的发布时间。
- `created_by`、`updated_by` 只保存 Admin 账户 ID。
- 创建、更新、发布和归档从当前 Admin 会话获取操作者。
- 批量删除先去重并确认全部 ID 存在，避免部分删除。
- Portal 分页只返回摘要，详情才返回完整正文。

## 测试覆盖

```text
light-domain/src/test/.../AnnouncementDomainServiceTest.java  # 状态机和领域校验
light-trigger-admin/src/test/.../AdminAnnouncementApplicationServiceTest.java # Admin 用例编排
light-trigger-admin/src/test/.../AdminAnnouncementHttpContractTest.java # Admin 契约
light-trigger-portal/src/test/.../PortalAnnouncementHttpContractTest.java # Portal 契约
```

账户分页契约同步迁移为 `/api/accounts/admins/page` 和
`/api/accounts/portal-users/page`，旧 `/list` 分页地址不再映射。
