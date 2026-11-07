import { defineStore } from 'pinia'
import { ref } from 'vue'

/**
 * 应用级状态：多端脚手架展示文案。
 */
export const useAppStore = defineStore('app', () => {
  const title = ref('H5 / 微信小程序 / App')
  const stackHint = ref('Pinia + Axios 封装对齐后端 code / message / data')

  return { title, stackHint }
})
