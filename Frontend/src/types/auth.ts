export interface LoginRequest {
  email: string
  password: string
}

export interface LoginResponse {
  accessToken: string
  tokenType: string
  expiresIn: number
  userId: string
  email: string
  name: string
}

export interface SignupRequest {
  email: string
  password: string
  name: string
}

export interface SignupResponse {
  userId: string
  email: string
  name: string
}

export interface EmailCheckResponse {
  email: string
  duplicated: boolean
}

export interface AuthUser {
  userId: string
  email: string
  name: string
}
