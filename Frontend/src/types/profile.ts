/** 프로필 API 요청/응답 타입. */
import type { WelfareCodeName } from '@/types/welfare'

export interface UserProfileRequest {
  birthDate: string
  region: string
  interestCodes: string[]
  householdTypeCodes: string[]
  incomeLevel?: string
  gender: 'F' | 'M'
}

export interface UserProfile {
  userId: string
  birthDate: string
  age: number
  gender: 'F' | 'M'
  region: string
  incomeLevel: string | null
  lifeStages: WelfareCodeName[]
  householdTypes: WelfareCodeName[]
  interests: WelfareCodeName[]
}
