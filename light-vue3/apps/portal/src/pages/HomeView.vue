<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { BizError } from '@light/request'
import { fetchHealth, type HealthData } from '@/api/health'

const title = 'Light Portal'
const loading = ref(false)
const health = ref<HealthData | null>(null)
const error = ref('')

onMounted(async () => {
  loading.value = true
  error.value = ''
  try {
    health.value = await fetchHealth()
  } catch (e) {
    error.value = e instanceof BizError ? `[${e.code}] ${e.message}` : '请求失败'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <main class="mx-auto flex max-w-3xl flex-col gap-6 px-6 py-16">
    <header class="space-y-2">
      <p class="text-sm font-medium tracking-wide text-slate-500">Light Vue3</p>
      <h1 class="text-3xl font-semibold tracking-tight">{{ title }}</h1>
      <p class="text-slate-600">Vite + Vue3 + Tailwind + Axios（统一 code/message/data）</p>
    </header>

    <section class="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
      <h2 class="mb-3 text-lg font-medium">/health</h2>
      <p v-if="loading" class="text-slate-500">加载中…</p>
      <p v-else-if="error" class="text-rose-600">{{ error }}</p>
      <pre v-else class="overflow-x-auto rounded-lg bg-slate-900 p-4 text-sm text-slate-100">{{ health }}</pre>
    </section>
  </main>
</template>
