import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { HealthData } from '@/api/health'
import { fetchHealth } from '@/api/health'
import { BizError } from '@/utils/request'

/**
 * 健康检查示例 Store：演示异步 action 与错误码透出。
 */
export const useHealthStore = defineStore('health', () => {
  const loading = ref(false)
  const data = ref<HealthData | null>(null)
  const error = ref('')

  /**
   * 拉取 /health 并写入状态。
   */
  async function load() {
    // 1. 进入加载态并清空上次错误，避免残留展示
    loading.value = true
    error.value = ''
    try {
      // 2. 走统一 http 封装；成功时 data 已是业务载荷
      data.value = await fetchHealth()
    }
    catch (e) {
      // 3. BizError 保留四位码；其它异常给通用文案
      error.value = e instanceof BizError
        ? `[${e.code}] ${e.message}`
        : '请求失败'
      data.value = null
    }
    finally {
      loading.value = false
    }
  }

  return { loading, data, error, load }
})
