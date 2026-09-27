/** 프로필 폼에서 쓰는 성별·소득 선택지. */
export const GENDERS = [
  { code: 'F', name: '여성' },
  { code: 'M', name: '남성' },
] as const

export const INCOME_LEVELS = [
  '기초생활수급',
  '차상위',
  '중위소득 50% 이하',
  '중위소득 80% 이하',
  '중위소득 100% 이하',
  '중위소득 150% 이하',
  '해당 없음',
] as const
