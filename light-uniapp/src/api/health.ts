import { http } from '@/utils/request'

export interface HealthData {
  app?: string
}

export function fetchHealth() {
  return http.get<HealthData>('/health')
}
