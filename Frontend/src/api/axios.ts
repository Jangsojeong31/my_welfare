import axios, { AxiosError, type AxiosRequestConfig } from 'axios'
import type { ApiResponse } from '@/types/api'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
})

function isAuthLoginRequest(config?: AxiosRequestConfig): boolean {
  const url = config?.url ?? ''
  return url.includes('/api/auth/login')
}

function resolveErrorMessage(error: AxiosError<ApiResponse<unknown>>): string {
  if (!error.response) {
    return '서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.'
  }
  return error.response.data?.message ?? '요청 처리 중 오류가 발생했습니다.'
}

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    if (import.meta.env.DEV) {
      const method = response.config.method?.toUpperCase() ?? 'GET'
      const url = response.config.url ?? ''
      console.log(`[API] ${method} ${url}`, response.data)
    }
    return response
  },
  async (error: AxiosError<ApiResponse<unknown>>) => {
    if (axios.isCancel(error)) {
      return Promise.reject(error)
    }

    const { useToastStore } = await import('@/store/toast')
    useToastStore().show(resolveErrorMessage(error), 'error')

    if (error.response?.status === 401 && !isAuthLoginRequest(error.config)) {
      const { useAuthStore } = await import('@/store/auth')
      const { default: router } = await import('@/router')
      const auth = useAuthStore()
      const wasAuthenticated = auth.isAuthenticated
      auth.logout()
      if (wasAuthenticated && router.currentRoute.value.name !== 'login') {
        await router.push({
          name: 'login',
          query: { redirect: router.currentRoute.value.fullPath },
        })
      }
    }

    return Promise.reject(error)
  },
)

export default http
