export interface WelfareAiSearchCondition {
  age: number | null
  region: string | null
  employment: string | null
  housing: string | null
  income: string | null
  keywords: string[] | null
}

export interface WelfareAiRecommendation {
  id: string | null
  servId: string
  servNm: string
  summary: string | null
  detailLink: string | null
  reason: string | null
}

export interface WelfareAiResponse {
  condition: WelfareAiSearchCondition | null
  recommendations: WelfareAiRecommendation[]
  answer: string | null
}
