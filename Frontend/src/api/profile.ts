/** 내 프로필 조회·저장 API. 404(프로필 없음)는 null로 처리한다. */
import axios from 'axios'
import http from '@/api/axios'
import type { ApiResponse } from '@/types/api'
import type { UserProfile, UserProfileRequest } from '@/types/profile'

/** ApiResponse 래퍼에서 data만 꺼낸다. */
function unwrap<T>(response: ApiResponse<T>, fallbackMessage: string): T {
  if (!response.success || response.data == null) {
    throw new Error(response.message ?? fallbackMessage)
  }
  return response.data
}

/** 저장된 프로필을 가져온다. 없으면 null. */
export async function fetchMyProfile(): Promise<UserProfile | null> {
  try {
    const { data } = await http.get<ApiResponse<UserProfile>>('/api/users/me/profile')
    return unwrap(data, '프로필을 불러오지 못했습니다.')
  } catch (error) {
    if (axios.isAxiosError(error) && error.response?.status === 404) {
      return null
    }
    throw error
  }
}

/** 프로필을 생성하거나 수정한다. */
export async function saveMyProfile(payload: UserProfileRequest): Promise<UserProfile> {
  const { data } = await http.put<ApiResponse<UserProfile>>('/api/users/me/profile', payload)
  return unwrap(data, '프로필 저장에 실패했습니다.')
}
