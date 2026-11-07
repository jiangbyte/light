<script setup lang="ts">
import type { HealthData } from '@/api/health'
import { onMounted, ref } from 'vue'
import { fetchHealth } from '@/api/health'
import { BizError } from '@/utils/request'

const loading = ref(false)
const health = ref<HealthData | null>(null)
const error = ref('')

onMounted(async () => {
  loading.value = true
  error.value = ''
  try {
    health.value = await fetchHealth()
  }
  catch (e) {
    error.value = e instanceof BizError
      ? `[${e.code}] ${e.message}`
      : '请求失败'
  }
  finally {
    loading.value = false
  }
})
</script>

<template>
  <view class="min-h-screen bg-slate-50 px-6 py-12 text-slate-900">
    <view class="mb-6 space-y-2">
      <text class="block text-sm text-slate-500">Light UniApp</text>
      <text class="block text-2xl font-semibold">H5 / 微信小程序 / App</text>
      <text class="block text-slate-600">Axios 封装对齐后端 code / message / data</text>
    </view>

    <view class="rounded-xl border border-slate-200 bg-white p-4">
      <text class="mb-2 block text-lg font-medium">/health</text>
      <text v-if="loading" class="text-slate-500">加载中…</text>
      <text v-else-if="error" class="text-rose-600">{{ error }}</text>
      <text v-else class="break-all text-sm">{{ health }}</text>
    </view>
  </view>
</template>
