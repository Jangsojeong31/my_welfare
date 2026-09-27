import http from '@/api/axios'
import type { ApiResponse } from '@/types/api'
import type { WelfareDetail, WelfareListResponse, WelfareSearchParams } from '@/types/welfare'

function unwrap<T>(response: ApiResponse<T>, fallbackMessage: string): T {
  if (!response.success || response.data == null) {
    throw new Error(response.message ?? fallbackMessage)
  }
  return response.data
}

function compactParams(params: WelfareSearchParams): Record<string, string | number> {
  const result: Record<string, string | number> = {
    page: params.page ?? 0,
    size: params.size ?? 20,
    sort: params.sort ?? 'inqNum,desc',
  }

  if (params.lifeStages?.length) {
    result.lifeStages = params.lifeStages.join(',')
  }
  if (params.householdTypes?.length) {
    result.householdTypes = params.householdTypes.join(',')
  }
  if (params.interests?.length) {
    result.interests = params.interests.join(',')
  }

  return result
}

export async function fetchWelfareList(params: WelfareSearchParams = {}): Promise<WelfareListResponse> {
  const { data } = await http.get<ApiResponse<WelfareListResponse>>('/api/welfare', {
    params: compactParams(params),
  })
  return unwrap(data, '복지 목록을 불러오지 못했습니다.')
}

/** 저장된 프로필 기준 맞춤 복지 목록. 점수순. */
export async function fetchMyWelfareList(params: Pick<WelfareSearchParams, 'page' | 'size'> = {}): Promise<WelfareListResponse> {
  const { data } = await http.get<ApiResponse<WelfareListResponse>>('/api/welfare/me', {
    params: {
      page: params.page ?? 0,
      size: params.size ?? 20,
    },
  })
  return unwrap(data, '맞춤 복지 목록을 불러오지 못했습니다.')
}

export async function fetchWelfareDetail(id: string): Promise<WelfareDetail> {
  const { data } = await http.get<ApiResponse<WelfareDetail>>(`/api/welfare/${id}`)
  return unwrap(data, '복지 상세 정보를 불러오지 못했습니다.')
}
