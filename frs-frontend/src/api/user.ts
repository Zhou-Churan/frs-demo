import request from '@/utils/request'
import type { ApiResponse } from '@/utils/request'

export interface FaceInfo {
  faceId: string
  externalImageId: string
  hasFace: boolean
}

export interface FaceUpdateResult {
  message: string
  faceId: string
}

export function getFaceInfo() {
  return request.get<ApiResponse<FaceInfo>>('/user/face')
}

export function updateFace(data: { faceImageBase64: string }) {
  return request.put<ApiResponse<FaceUpdateResult>>('/user/face', data)
}