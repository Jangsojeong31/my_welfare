export interface WelfareListItem {
  id: string
  servNm: string
  servDgst: string
  onapPsbltYn: string
  ctpvNm: string
  sggNm: string
  wlfareInfoOutlCn: string
  score?: number | null
  matchReasons?: string[] | null
}

export interface WelfareListResponse {
  content: WelfareListItem[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  first: boolean
  last: boolean
}

export interface WelfareCodeName {
  code: string
  name: string
}

export interface WelfareApplicationItem {
  servSeCode: string
  servSeDetailNm: string
  servSeDetailLink: string
}

export interface WelfareContactItem {
  servSeCode: string
  contactName: string
  contactValue: string
}

export interface WelfareLinkItem {
  servSeCode: string
  linkName: string
  linkUrl: string
}

export interface WelfareFormItem {
  formName: string
  formUrl: string
}

export interface WelfareLawItem {
  lawName: string
  lawUrl: string
}

export interface WelfareDetail {
  id: string
  apiCd: string
  servCd: string
  servNm: string
  jurMnofNm: string
  jurOrgNm: string
  inqNum: number | null
  servDgst: string
  servDtlLink: string
  svcfrstRegTs: string
  sprtCycNm: string
  srvPvsnNm: string
  rprsCtadr: string
  onapPsbltYn: string
  enfcBgngYmd: string
  enfcEndYmd: string
  bizChrDeptNm: string
  ctpvNm: string
  sggNm: string
  wlfareInfoOutlCn: string
  crtrYr: string
  tgtrDtlCn: string
  slctCritCn: string
  alwServCn: string
  sprtTrgtCn: string
  lastModYmd: string
  lifeStages: WelfareCodeName[]
  householdTypes: WelfareCodeName[]
  interests: WelfareCodeName[]
  applications: WelfareApplicationItem[]
  contacts: WelfareContactItem[]
  links: WelfareLinkItem[]
  forms: WelfareFormItem[]
  laws: WelfareLawItem[]
}

export interface WelfareSearchParams {
  lifeStages?: string[]
  householdTypes?: string[]
  interests?: string[]
  page?: number
  size?: number
  sort?: string
}

export interface FilterOption {
  code: string
  name: string
}
