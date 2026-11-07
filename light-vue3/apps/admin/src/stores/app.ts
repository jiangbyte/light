import { defineStore } from 'pinia'
import { ref } from 'vue'

/**
 * 应用级状态：端名称等全局展示信息。
 */
export const useAppStore = defineStore('app', () => {
  const title = ref('Light Admin')
  const stackHint = ref('Vite + Vue3 + Pinia + Tailwind + Axios（统一 code/message/data）')

  return { title, stackHint }
})
