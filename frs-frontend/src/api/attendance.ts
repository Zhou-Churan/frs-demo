import request from '@/utils/request'
import type { ApiResponse } from '@/utils/request'

export interface SignParams {
  faceImageBase64: string
  deviceType?: string
}

export interface SignResult {
  message: string
  username: string
  similarity: number
  signTime: string
}

export interface AttendanceRecord {
  id: number
  userId: number
  signDate: string
  signTime: string
  similarity: number
  deviceType: string
  ipAddress: string
  createdAt: string
}

export interface AttendancePage {
  records: AttendanceRecord[]
  total: number
  size: number
  current: number
  pages: number
}

export interface TodayStatus {
  signed: boolean
  date: string
  signTime: string
}

export function faceSign(data: SignParams) {
  return request.post<ApiResponse<SignResult>>('/attendance/sign', data)
}

export function getAttendanceRecords(params: { page?: number; size?: number } = {}) {
  return request.get<ApiResponse<AttendancePage>>('/attendance/records', { params })
}

export function getTodayStatus() {
  return request.get<ApiResponse<TodayStatus>>('/attendance/today')
}