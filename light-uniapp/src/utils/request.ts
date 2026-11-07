import type { AxiosAdapter, AxiosInstance, AxiosRequestConfig, AxiosResponse, CreateAxiosDefaults, InternalAxiosRequestConfig } from 'axios'
import axios from 'axios'

/** 与 Light 后端统一响应对齐 */
export interface ApiResponse<T = unknown> {
  code: string
  message: string
  data: T | null
}

export class BizError extends Error {
  readonly code: string

  constructor(code: string, message: string) {
    super(message)
    this.name = 'BizError'
    this.code = code
  }
}

export const SUCCESS_CODE = '0'

export interface CreateHttpOptions extends CreateAxiosDefaults {
  successCode?: string
}

export interface HttpClient {
  get: <T = unknown>(url: string, config?: AxiosRequestConfig) => Promise<T | null>
  post: <T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig) => Promise<T | null>
  put: <T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig) => Promise<T | null>
  delete: <T = unknown>(url: string, config?: AxiosRequestConfig) => Promise<T | null>
  raw: AxiosInstance
}

function isApiResponse(value: unknown): value is ApiResponse {
  return (
    typeof value === 'object'
    && value !== null
    && 'code' in value
    && 'message' in value
    && 'data' in value
  )
}

function unwrap<T>(payload: unknown, successCode: string): T | null {
  if (!isApiResponse(payload)) {
    throw new BizError('1', '响应不是统一 ApiResponse 结构')
  }
  if (String(payload.code) !== successCode) {
    throw new BizError(String(payload.code), payload.message || '业务失败')
  }
  return payload.data as T | null
}

function buildUrl(baseURL: string | undefined, url: string | undefined): string {
  const base = baseURL || ''
  const path = url || ''
  if (/^https?:\/\//i.test(path))
    return path
  if (!base)
    return path
  return `${base.replace(/\/$/, '')}/${path.replace(/^\//, '')}`
}

/** 非 H5（小程序 / App）走 uni.request，保证多端可用 */
const uniAdapter: AxiosAdapter = (config) => {
  return new Promise((resolve, reject) => {
    const method = (config.method || 'GET').toUpperCase()
    const header: Record<string, string> = {}
    const rawHeaders = config.headers
    if (rawHeaders && typeof rawHeaders === 'object') {
      for (const [k, v] of Object.entries(rawHeaders as Record<string, unknown>)) {
        if (v == null)
          continue
        if (typeof v === 'string')
          header[k] = v
        else if (typeof v === 'number' || typeof v === 'boolean')
          header[k] = String(v)
      }
    }

    uni.request({
      url: buildUrl(config.baseURL, config.url),
      method: method as UniApp.RequestOptions['method'],
      data: config.data as UniApp.RequestOptions['data'],
      header,
      timeout: config.timeout,
      success: (res) => {
        resolve({
          data: res.data,
          status: res.statusCode,
          statusText: '',
          headers: (res.header || {}) as AxiosResponse['headers'],
          config: config as InternalAxiosRequestConfig,
          request: null,
        })
      },
      fail: (err) => {
        reject(new Error(err.errMsg || 'uni.request failed'))
      },
    })
  })
}

function resolveAdapter(): AxiosAdapter | undefined {
  // uni-app 编译期注入；H5 用默认 xhr，其它端用 uni.request
  const platform = import.meta.env.UNI_PLATFORM
  if (!platform || platform === 'h5')
    return undefined
  return uniAdapter
}

export function createHttp(options: CreateHttpOptions = {}): HttpClient {
  const { successCode = SUCCESS_CODE, ...axiosDefaults } = options
  const instance = axios.create({
    timeout: 15000,
    headers: { 'Content-Type': 'application/json' },
    adapter: resolveAdapter(),
    ...axiosDefaults,
  })

  instance.interceptors.response.use(
    response => response,
    (error: unknown) => {
      if (axios.isAxiosError(error)) {
        const data = error.response?.data
        if (isApiResponse(data)) {
          return Promise.reject(new BizError(String(data.code), data.message || error.message))
        }
        const status = error.response?.status
        return Promise.reject(new BizError(status
          ? String(status)
          : '1', error.message || '网络异常'))
      }
      return Promise.reject(error)
    },
  )

  async function request<T>(config: AxiosRequestConfig): Promise<T | null> {
    const response = await instance.request<ApiResponse<T>>(config)
    return unwrap<T>(response.data, successCode)
  }

  return {
    raw: instance,
    get: <T = unknown>(url: string, config?: AxiosRequestConfig) =>
      request<T>({ ...config, method: 'GET', url }),
    post: <T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig) =>
      request<T>({ ...config, method: 'POST', url, data }),
    put: <T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig) =>
      request<T>({ ...config, method: 'PUT', url, data }),
    delete: <T = unknown>(url: string, config?: AxiosRequestConfig) =>
      request<T>({ ...config, method: 'DELETE', url }),
  }
}

export const http = createHttp({
  baseURL: import.meta.env.VITE_API_BASE || 'http://127.0.0.1:8080',
})
