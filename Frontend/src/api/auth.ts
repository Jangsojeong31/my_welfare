import http from '@/api/axios'
import type { ApiResponse } from '@/types/api'
import type {
  EmailCheckResponse,
  LoginRequest,
  LoginResponse,
  SignupRequest,
  SignupResponse,
} from '@/types/auth'

function unwrap<T>(response: ApiResponse<T>, fallbackMessage: string): T {
  if (!response.success || response.data == null) {
    throw new Error(response.message ?? fallbackMessage)
  }
  return response.data
}

export async function login(payload: LoginRequest): Promise<LoginResponse> {
  const { data } = await http.post<ApiResponse<LoginResponse>>('/api/auth/login', payload)
  return unwrap(data, '로그인에 실패했습니다.')
}

export async function signup(payload: SignupRequest): Promise<SignupResponse> {
  const { data } = await http.post<ApiResponse<SignupResponse>>('/api/auth/signup', payload)
  return unwrap(data, '회원가입에 실패했습니다.')
}

export async function checkEmail(email: string): Promise<EmailCheckResponse> {
  const { data } = await http.get<ApiResponse<EmailCheckResponse>>('/api/auth/email-check', {
    params: { email },
  })
  return unwrap(data, '이메일 중복 확인에 실패했습니다.')
}
