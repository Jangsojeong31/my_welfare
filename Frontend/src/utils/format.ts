export function formatYmd(value?: string | null): string {
  if (!value || value.trim() === '99991231') {
    return ''
  }
  const digits = value.replace(/\D/g, '')
  if (digits.length === 8) {
    return `${digits.slice(0, 4)}.${digits.slice(4, 6)}.${digits.slice(6, 8)}`
  }
  return value
}

export function formatRegion(ctpvNm?: string | null, sggNm?: string | null): string {
  return [ctpvNm, sggNm].filter((value) => Boolean(value && value.trim())).join(' ')
}

export function isOnlineApply(value?: string | null): boolean {
  return value === 'Y'
}

export function hasText(value?: string | null): boolean {
  return Boolean(value && value.trim())
}

/** 한글 문장 마침표(.) 뒤에서 줄바꿈한다. 숫자 목록(1.)은 유지한다. */
export function breakAfterPeriod(value?: string | null): string {
  if (!value) {
    return ''
  }
  return value.replace(/([가-힣])\.\s*(?=\S)/g, '$1.\n')
}

export function toStringArray(value: unknown): string[] {
  if (Array.isArray(value)) {
    return value.flatMap((item) => toStringArray(item))
  }
  if (typeof value === 'string' && value.trim()) {
    return value
      .split(',')
      .map((item) => item.trim())
      .filter(Boolean)
  }
  return []
}

export function toPositiveInt(value: unknown, fallback: number): number {
  const parsed = Number(value)
  if (Number.isInteger(parsed) && parsed >= 0) {
    return parsed
  }
  return fallback
}
