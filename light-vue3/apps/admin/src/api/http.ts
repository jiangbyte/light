import { createHttp } from '@light/request'

/** 本端 HTTP 客户端；baseURL 来自环境变量 */
export const http = createHttp({
  baseURL: import.meta.env.VITE_API_BASE,
})
