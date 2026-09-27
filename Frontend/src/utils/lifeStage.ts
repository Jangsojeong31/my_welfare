/** 생년월일로 만 나이와 생애주기(02~07)를 계산한다. */
import { LIFE_STAGES } from '@/constants/welfareFilters'

/** 오늘 날짜를 YYYY-MM-DD로 반환한다. */
export function todayYmd(): string {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

/** 생년월일 기준 만 나이. 미래 날짜면 null. */
export function ageFromBirthDate(birthDate: string, today = todayYmd()): number | null {
  if (!birthDate || birthDate > today) {
    return null
  }
  const [by, bm, bd] = birthDate.split('-').map(Number)
  const [ty, tm, td] = today.split('-').map(Number)
  if (!by || !bm || !bd || !ty || !tm || !td) {
    return null
  }
  let age = ty - by
  if (tm < bm || (tm === bm && td < bd)) {
    age -= 1
  }
  return age < 0 ? null : age
}

/** 만 나이를 생애주기 코드·이름으로 변환한다. 전생애(01)는 쓰지 않는다. */
export function lifeStageFromAge(age: number): { code: string; name: string } | null {
  const code = age <= 5 ? '02' : age <= 12 ? '03' : age <= 18 ? '04' : age <= 34 ? '05' : age <= 64 ? '06' : '07'
  return LIFE_STAGES.find((item) => item.code === code) ?? null
}
