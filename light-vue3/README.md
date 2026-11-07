# light-vue3

Vue 3 前端（pnpm workspace）：`apps/admin`（:5173）与 `apps/portal`（:5174）。

## 技术

- Vite + Vue 3 + TypeScript（**单文件** `tsconfig.json`）
- Pinia（`src/stores`，Composition Store）
- Tailwind CSS v4
- ESLint Flat + Prettier
- Axios 封装：`@light/request`（成功 `code === "0000"` 时返回 `data`）

## 命令

```bash
pnpm install
pnpm dev:admin
pnpm dev:portal
pnpm build
pnpm lint
pnpm format
```

## 环境变量

各 app 的 `.env.development`：

- admin：`VITE_API_BASE=http://127.0.0.1:8081`
- portal：`VITE_API_BASE=http://127.0.0.1:8080`

## 请求约定

与系列后端统一响应对齐：`{ code, message, data }`。业务失败抛出 `BizError`。
