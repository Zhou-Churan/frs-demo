import request from '@/utils/request'
import type { ApiResponse } from '@/utils/request'

export interface LoginParams {
  username: string
  password: string
}

export interface RegisterParams {
  username: string
  password: string
  phone?: string
  faceImageBase64: string
}

export interface AuthResult {
  token: string
  userId: number
  username: string
}

export function login(data: LoginParams) {
  return request.post<ApiResponse<AuthResult>>('/auth/login', data)
}

export function register(data: RegisterParams) {
  return request.post<ApiResponse<AuthResult>>('/auth/register', data)
}