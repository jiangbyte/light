# light-spring

Light 系列的 **Spring Boot** 实现：DDD + 六边形（Maven 多模块）。

占位约定：
- 各包 `package-info.java` 说明放什么、怎么命名（XxxEntity / IXxxService 等）
- `domain/template` 为限界上下文骨架，复制后改真实上下文名
- `Xxx*` 类为命名示例，落地业务时改名或删除

## 启动

```bash
cd light-spring
mvn -DskipTests install
mvn -pl light-apps/light-admin-app spring-boot:run   # :8081
mvn -pl light-apps/light-portal-app spring-boot:run  # :8080
```

## 模块角色

| 角色 | 模块 |
|------|------|
| 共享内核 | `light-types` |
| 领域 | `light-domain`（含 `template`） |
| 出站适配器 | `light-infrastructure` |
| 入站契约 | `light-apis/*` |
| 入站适配器 | `light-triggers/*` |
| 组装启动 | `light-apps/*` |
