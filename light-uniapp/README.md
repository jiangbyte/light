# light-uniapp

uni-app（Vue3 + Vite + TS）：**H5 / 微信小程序 / App（APK）**。

## 技术

- `@uni-helper/unh` + `@dcloudio/vite-plugin-uni`
- Tailwind CSS v4 + `weapp-tailwindcss`
- **单文件** `tsconfig.json`
- ESLint + Prettier
- Axios 封装（`src/utils/request.ts`）：成功 `code === "0000"` 返回 `data`；非 H5 使用 `uni.request` 适配器

## 命令

```bash
pnpm install
pnpm dev:h5
pnpm dev:mp-weixin
pnpm dev:app
pnpm build:h5
pnpm build:mp-weixin
pnpm build:app
pnpm lint
pnpm format
```

## 环境变量

`VITE_API_BASE` 默认 `http://127.0.0.1:8080`（Portal）。

## APK

1. `pnpm build:app` 生成 App 资源
2. 使用 **HBuilderX** 或 uni 云打包，导入本项目后制作 Android 自定义基座 / 云打包 APK
3. 包名占位见 `src/manifest.json` → `app-plus.distribute.android.package`：`io.jiangbyte.light.uniapp`

本地仅 `build:app` 不会直接产出已签名安装包。
