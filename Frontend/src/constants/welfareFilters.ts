import type { FilterOption } from '@/types/welfare'

export const LIFE_STAGES: FilterOption[] = [
  { code: '01', name: '전생애' },
  { code: '02', name: '영유아' },
  { code: '03', name: '아동' },
  { code: '04', name: '청소년' },
  { code: '05', name: '청년' },
  { code: '06', name: '중장년' },
  { code: '07', name: '노년' },
  { code: '08', name: '임신·출산' },
]

export const HOUSEHOLD_TYPES: FilterOption[] = [
  { code: '01', name: '다문화·탈북민' },
  { code: '02', name: '다자녀' },
  { code: '03', name: '보훈대상자' },
  { code: '04', name: '장애인' },
  { code: '05', name: '저소득' },
  { code: '06', name: '한부모·조손' },
]

export const INTERESTS: FilterOption[] = [
  { code: '01', name: '신체건강' },
  { code: '02', name: '정신건강' },
  { code: '03', name: '생활지원' },
  { code: '04', name: '주거' },
  { code: '05', name: '일자리' },
  { code: '06', name: '문화·여가' },
  { code: '07', name: '안전·위기' },
  { code: '08', name: '임신·출산' },
  { code: '09', name: '보육' },
  { code: '10', name: '교육' },
  { code: '11', name: '입양·위탁' },
  { code: '12', name: '보호·돌봄' },
  { code: '13', name: '서민금융' },
  { code: '14', name: '법률' },
  { code: '15', name: '관계개선' },
  { code: '16', name: '에너지' },
]

export const PAGE_SIZE_OPTIONS = [10, 20, 50] as const

function normalize(value: string): string {
  return value.replace(/[·\s-]/g, '').toLowerCase()
}

function matchOption(options: FilterOption[], token: string): FilterOption | undefined {
  const normalizedToken = normalize(token)
  return options.find(
    (option) =>
      option.code === token ||
      normalize(option.name) === normalizedToken ||
      normalize(option.name).includes(normalizedToken),
  )
}

export function resolveFiltersFromKeyword(keyword: string): {
  lifeStages: string[]
  householdTypes: string[]
  interests: string[]
} {
  const tokens = keyword
    .split(/\s+/)
    .map((token) => token.trim())
    .filter(Boolean)

  const lifeStages = new Set<string>()
  const householdTypes = new Set<string>()
  const interests = new Set<string>()

  for (const token of tokens) {
    const lifeStage = matchOption(LIFE_STAGES, token)
    if (lifeStage) {
      lifeStages.add(lifeStage.code)
    }
    const householdType = matchOption(HOUSEHOLD_TYPES, token)
    if (householdType) {
      householdTypes.add(householdType.code)
    }
    const interest = matchOption(INTERESTS, token)
    if (interest) {
      interests.add(interest.code)
    }
  }

  return {
    lifeStages: [...lifeStages],
    householdTypes: [...householdTypes],
    interests: [...interests],
  }
}
