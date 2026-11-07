<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { onMounted } from 'vue'
import { useAppStore } from '@/stores/app'
import { useHealthStore } from '@/stores/health'

const appStore = useAppStore()
const healthStore = useHealthStore()
const { title, stackHint } = storeToRefs(appStore)
const { loading, data: health, error } = storeToRefs(healthStore)

onMounted(() => {
  void healthStore.load()
})
</script>

<template>
  <view class="min-h-screen bg-slate-50 px-6 py-12 text-slate-900">
    <view class="mb-6 space-y-2">
      <text class="block text-sm text-slate-500">Light UniApp</text>
      <text class="block text-2xl font-semibold">{{ title }}</text>
      <text class="block text-slate-600">{{ stackHint }}</text>
    </view>

    <view class="rounded-xl border border-slate-200 bg-white p-4">
      <text class="mb-2 block text-lg font-medium">/health</text>
      <text v-if="loading" class="text-slate-500">加载中…</text>
      <text v-else-if="error" class="text-rose-600">{{ error }}</text>
      <text v-else class="break-all text-sm">{{ health }}</text>
    </view>
  </view>
</template>
