import http from '@/api/axios'
import type { ApiResponse } from '@/types/api'
import type { WelfareAiResponse } from '@/types/ai'

function unwrap<T>(response: ApiResponse<T>, fallbackMessage: string): T {
  if (!response.success || response.data == null) {
    throw new Error(response.message ?? fallbackMessage)
  }
  return response.data
}

function isWrapped(data: WelfareAiResponse | ApiResponse<WelfareAiResponse>): data is ApiResponse<WelfareAiResponse> {
  return 'success' in data && 'data' in data && !('recommendations' in data)
}

export async function searchWelfareByQuestion(question: string): Promise<WelfareAiResponse> {
  const { data } = await http.post<WelfareAiResponse | ApiResponse<WelfareAiResponse>>(
    '/api/ai/welfare/search',
    { question },
    { timeout: 90_000 },
  )

  if (isWrapped(data)) {
    return unwrap(data, '복지 검색에 실패했습니다.')
  }

  return data
}
