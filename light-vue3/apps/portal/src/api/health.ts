import { http } from './http'

export interface HealthData {
  app?: string
}

/** 调用后端 /health，演示统一响应解包 */
export function fetchHealth() {
  return http.get<HealthData>('/health')
}
