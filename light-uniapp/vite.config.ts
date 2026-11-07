import { fileURLToPath, URL } from 'node:url'

import Uni from '@uni-helper/plugin-uni'
import { defineConfig } from 'vite'
import { WeappTailwindcss } from 'weapp-tailwindcss/vite'

export default defineConfig({
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  plugins: [
    // https://uni-helper.js.org/plugin-uni
    Uni(),
    // https://tw.weapp.dev/
    WeappTailwindcss({
      // Tailwind CSS 4 的入口文件，需要被项目实际引入
      cssEntries: [
        fileURLToPath(new URL('./src/app.css', import.meta.url)),
      ],
      cssOptions: {
        // 将 rem 转换为 rpx，保证小程序端的尺寸表现
        rem2rpx: true,
      },
    }),
  ],

})
