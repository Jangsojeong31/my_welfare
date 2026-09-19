<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import { fetchWelfareDetail } from '@/api/welfare'
import type { WelfareDetail } from '@/types/welfare'
import { breakAfterPeriod, formatRegion, formatYmd, hasText, isOnlineApply } from '@/utils/format'

const route = useRoute()
const loading = ref(false)
const detail = ref<WelfareDetail | null>(null)

const region = computed(() => formatRegion(detail.value?.ctpvNm, detail.value?.sggNm))
const inquiryContact = computed(() => {
  if (hasText(detail.value?.rprsCtadr)) {
    return detail.value?.rprsCtadr
  }
  const firstContact = detail.value?.contacts?.[0]?.contactValue
  return hasText(firstContact) ? firstContact : '-'
})

async function load(id: string) {
  loading.value = true
  detail.value = null
  try {
    detail.value = await fetchWelfareDetail(id)
  } catch {
    detail.value = null
  } finally {
    loading.value = false
  }
}

watch(
  () => route.params.id,
  (id) => {
    if (typeof id === 'string' && id) {
      void load(id)
    }
  },
  { immediate: true },
)
</script>

<template>
  <section class="mx-auto max-w-5xl px-4 py-8">
    <RouterLink to="/welfare" class="mb-6 inline-flex text-sm text-slate-500 hover:text-primary">
      ← 목록으로
    </RouterLink>

    <p v-if="loading" class="rounded-2xl border border-dashed border-slate-200 bg-white py-20 text-center text-sm text-slate-400">
      상세 정보를 불러오는 중입니다...
    </p>

    <article v-else-if="detail" class="space-y-6">
      <header class="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
        <div class="mb-3 flex flex-wrap items-center gap-2">
          <span
            v-if="isOnlineApply(detail.onapPsbltYn)"
            class="rounded-full bg-primary-soft px-2.5 py-1 text-xs font-medium text-primary-dark"
          >
            온라인 신청 가능
          </span>
          <span v-if="region" class="rounded-full bg-slate-100 px-2.5 py-1 text-xs text-slate-500">
            {{ region }}
          </span>
          <span v-if="detail.crtrYr" class="rounded-full bg-slate-100 px-2.5 py-1 text-xs text-slate-500">
            기준연도 {{ detail.crtrYr }}
          </span>
        </div>
        <h1 class="text-2xl font-semibold text-ink">{{ detail.servNm }}</h1>
        <p v-if="hasText(detail.servDgst)" class="mt-3 whitespace-pre-wrap text-sm leading-7 text-slate-600">
          {{ breakAfterPeriod(detail.servDgst) }}
        </p>
      </header>

      <section class="grid gap-4 md:grid-cols-2">
        <div class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <h2 class="mb-3 text-sm font-semibold text-ink">기본 정보</h2>
          <dl class="grid grid-cols-[7rem_1fr] gap-y-2 text-sm">
            <!-- <dt class="text-slate-400">소관 부처</dt> -->
            <!-- <dd>{{ detail.jurMnofNm || '-' }}</dd> -->
            <dt class="text-slate-400">담당 기관</dt>
            <dd>{{ detail.jurMnofNm || '-' }}</dd>
            <dt class="text-slate-400">담당 부서</dt>
            <dd>{{ detail.jurOrgNm || detail.bizChrDeptNm || '-' }}</dd>
            <dt class="text-slate-400">지원 주기</dt>
            <dd>{{ detail.sprtCycNm || '-' }}</dd>
            <dt class="text-slate-400">제공 형태</dt>
            <dd>{{ detail.srvPvsnNm || '-' }}</dd>
            <dt class="text-slate-400">문의처</dt>
            <dd>{{ inquiryContact }}</dd>
          </dl>
        </div>
        <div class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <h2 class="mb-3 text-sm font-semibold text-ink">시행 정보</h2>
          <dl class="grid grid-cols-[7rem_1fr] gap-y-2 text-sm">
            <dt class="text-slate-400">시행 시작</dt>
            <dd>{{ formatYmd(detail.enfcBgngYmd) || '-' }}</dd>
            <dt class="text-slate-400">시행 종료</dt>
            <dd>{{ formatYmd(detail.enfcEndYmd) || '-' }}</dd>
            <!-- <dt class="text-slate-400">최종 수정</dt>
            <dd>{{ formatYmd(detail.lastModYmd) || '-' }}</dd> -->
            <!-- <dt class="text-slate-400">조회수</dt>
            <dd>{{ detail.inqNum?.toLocaleString() ?? '-' }}</dd>
            <dt class="text-slate-400">서비스 코드</dt>
            <dd>{{ detail.servCd || '-' }}</dd> -->
            <dt class="text-slate-400">상세 링크</dt>
            <dd>
              <a
                v-if="hasText(detail.servDtlLink)"
                :href="detail.servDtlLink"
                target="_blank"
                rel="noreferrer"
                class="text-primary hover:underline"
              >
                바로가기
              </a>
              <span v-else>-</span>
            </dd>
          </dl>
        </div>
      </section>

      <section v-if="hasText(detail.wlfareInfoOutlCn)" class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
        <h2 class="mb-3 text-sm font-semibold text-ink">사업 개요</h2>
        <p class="whitespace-pre-wrap text-sm leading-7 text-slate-600">{{ breakAfterPeriod(detail.wlfareInfoOutlCn) }}</p>
      </section>

      <section class="grid gap-4 md:grid-cols-2">
        <div v-if="hasText(detail.tgtrDtlCn) || hasText(detail.sprtTrgtCn)" class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <h2 class="mb-3 text-sm font-semibold text-ink">지원 대상</h2>
          <p class="whitespace-pre-wrap text-sm leading-7 text-slate-600">
            {{ breakAfterPeriod(detail.tgtrDtlCn || detail.sprtTrgtCn) }}
          </p>
        </div>
        <div v-if="hasText(detail.slctCritCn)" class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <h2 class="mb-3 text-sm font-semibold text-ink">선정 기준</h2>
          <p class="whitespace-pre-wrap text-sm leading-7 text-slate-600">{{ breakAfterPeriod(detail.slctCritCn) }}</p>
        </div>
      </section>

      <section v-if="hasText(detail.alwServCn)" class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
        <h2 class="mb-3 text-sm font-semibold text-ink">지원 내용</h2>
        <p class="whitespace-pre-wrap text-sm leading-7 text-slate-600">{{ breakAfterPeriod(detail.alwServCn) }}</p>
      </section>

      <section class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
        <h2 class="mb-3 text-sm font-semibold text-ink">대상 분류</h2>
        <div class="grid gap-4 md:grid-cols-3">
          <div>
            <p class="mb-2 text-xs text-slate-400">생애주기</p>
            <div class="flex flex-wrap gap-1.5">
              <span
                v-for="item in detail.lifeStages"
                :key="item.code"
                class="rounded-full bg-primary-soft px-2.5 py-1 text-xs text-primary-dark"
              >
                {{ item.name }}
              </span>
              <span v-if="!detail.lifeStages.length" class="text-xs text-slate-400">-</span>
            </div>
          </div>
          <div>
            <p class="mb-2 text-xs text-slate-400">가구형태</p>
            <div class="flex flex-wrap gap-1.5">
              <span
                v-for="item in detail.householdTypes"
                :key="item.code"
                class="rounded-full bg-slate-100 px-2.5 py-1 text-xs text-slate-600"
              >
                {{ item.name }}
              </span>
              <span v-if="!detail.householdTypes.length" class="text-xs text-slate-400">-</span>
            </div>
          </div>
          <div>
            <p class="mb-2 text-xs text-slate-400">관심분야</p>
            <div class="flex flex-wrap gap-1.5">
              <span
                v-for="item in detail.interests"
                :key="item.code"
                class="rounded-full bg-slate-100 px-2.5 py-1 text-xs text-slate-600"
              >
                {{ item.name }}
              </span>
              <span v-if="!detail.interests.length" class="text-xs text-slate-400">-</span>
            </div>
          </div>
        </div>
      </section>

      <section v-if="detail.applications.length" class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
        <h2 class="mb-3 text-sm font-semibold text-ink">신청 방법</h2>
        <ul class="space-y-2 text-sm">
          <li v-for="(item, index) in detail.applications" :key="`${item.servSeCode}-${index}`">
            <span class="font-medium text-ink">{{ item.servSeDetailNm || '신청 안내' }}</span>
            <span v-if="hasText(item.servSeDetailLink)" class="mx-1.5 font-bold text-primary">|</span>
            <span v-if="hasText(item.servSeDetailLink)" class="whitespace-pre-wrap leading-6 text-slate-600">
              {{ breakAfterPeriod(item.servSeDetailLink) }}
            </span>
          </li>
        </ul>
      </section>

      <section v-if="detail.contacts.length" class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
        <h2 class="mb-3 text-sm font-semibold text-ink">문의처</h2>
        <ul class="space-y-2 text-sm text-slate-600">
          <li v-for="(item, index) in detail.contacts" :key="`${item.contactName}-${index}`">
            <span class="font-medium text-ink">{{ item.contactName }}</span>
            <span v-if="item.contactValue"> · {{ item.contactValue }}</span>
          </li>
        </ul>
      </section>

      <section v-if="detail.links.length || detail.forms.length || detail.laws.length" class="grid gap-4 md:grid-cols-3">
        <div v-if="detail.links.length" class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <h2 class="mb-3 text-sm font-semibold text-ink">관련 링크</h2>
          <ul class="space-y-2 text-sm">
            <li v-for="(item, index) in detail.links" :key="`${item.linkName}-${index}`">
              <a :href="item.linkUrl" target="_blank" rel="noreferrer" class="text-primary hover:underline">
                {{ item.linkName }}
              </a>
            </li>
          </ul>
        </div>
        <div v-if="detail.forms.length" class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <h2 class="mb-3 text-sm font-semibold text-ink">서식</h2>
          <ul class="space-y-2 text-sm">
            <li v-for="(item, index) in detail.forms" :key="`${item.formName}-${index}`">
              <a :href="item.formUrl" target="_blank" rel="noreferrer" class="text-primary hover:underline">
                {{ item.formName }}
              </a>
            </li>
          </ul>
        </div>
        <div v-if="detail.laws.length" class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <h2 class="mb-3 text-sm font-semibold text-ink">관련 법령</h2>
          <ul class="space-y-2 text-sm">
            <li v-for="(item, index) in detail.laws" :key="`${item.lawName}-${index}`">
              <a
                v-if="hasText(item.lawUrl)"
                :href="item.lawUrl"
                target="_blank"
                rel="noreferrer"
                class="text-primary hover:underline"
              >
                {{ item.lawName }}
              </a>
              <span v-else>{{ item.lawName }}</span>
            </li>
          </ul>
        </div>
      </section>
    </article>

    <p v-else class="rounded-2xl border border-dashed border-slate-200 bg-white py-20 text-center text-sm text-slate-400">
      복지 서비스 정보를 찾을 수 없습니다.
    </p>
  </section>
</template>
