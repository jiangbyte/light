import axios, {
  type AxiosInstance,
  type AxiosRequestConfig,
  type AxiosResponse,
  type CreateAxiosDefaults,
} from 'axios'

/** 与 Light 后端统一响应对齐：code / message / data */
export interface ApiResponse<T = unknown> {
  code: string
  message: string
  data: T | null
}

/** 业务失败（HTTP 可能仍为 2xx，但 code !== "0"） */
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
  /** 业务成功码，默认 "0" */
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
    typeof value === 'object' &&
    value !== null &&
    'code' in value &&
    'message' in value &&
    'data' in value
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

/**
 * 创建对齐 Light 后端的 HTTP 客户端：成功时直接返回 data。
 */
export function createHttp(options: CreateHttpOptions = {}): HttpClient {
  const { successCode = SUCCESS_CODE, ...axiosDefaults } = options
  const instance = axios.create({
    timeout: 15000,
    headers: { 'Content-Type': 'application/json' },
    ...axiosDefaults,
  })

  instance.interceptors.response.use(
    (response: AxiosResponse) => {
      // 拦截器内抛出业务错误；外层方法再 unwrap
      return response
    },
    (error: unknown) => {
      if (axios.isAxiosError(error)) {
        const data = error.response?.data
        if (isApiResponse(data)) {
          return Promise.reject(new BizError(String(data.code), data.message || error.message))
        }
        const status = error.response?.status
        const msg = error.message || '网络异常'
        return Promise.reject(new BizError(status ? String(status) : '1', msg))
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
