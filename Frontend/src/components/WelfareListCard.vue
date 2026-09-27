<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
import type { WelfareListItem } from '@/types/welfare'
import { breakAfterPeriod, formatRegion, isOnlineApply } from '@/utils/format'

const props = defineProps<{
  item: WelfareListItem
}>()

const matchReasons = computed(() => props.item.matchReasons?.filter(Boolean) ?? [])
</script>

<template>
  <RouterLink
    :to="{ name: 'welfare-detail', params: { id: item.id } }"
    class="block rounded-2xl border border-slate-200 bg-white p-5 shadow-sm transition hover:border-primary/40 hover:shadow-md"
  >
    <div class="mb-2 flex items-start justify-between gap-3">
      <div class="flex min-w-0 flex-wrap items-center gap-2">
        <h3 class="text-base font-semibold text-ink">{{ item.servNm }}</h3>
        <span
          v-if="isOnlineApply(item.onapPsbltYn)"
          class="rounded-full bg-primary-soft px-2 py-0.5 text-[11px] font-medium text-primary-dark"
        >
          온라인 신청
        </span>
      </div>
      <span
        v-if="item.score != null"
        class="shrink-0 rounded-full bg-primary px-2.5 py-0.5 text-[11px] font-semibold text-white"
      >
        {{ item.score }}점
      </span>
    </div>
    <p class="mb-3 line-clamp-2 whitespace-pre-wrap text-sm leading-6 text-slate-600">
      {{ breakAfterPeriod(item.servDgst || item.wlfareInfoOutlCn) || '상세 설명이 없습니다.' }}
    </p>
    <p class="text-xs text-slate-400">
      {{ formatRegion(item.ctpvNm, item.sggNm) || '지역 정보 없음' }}
    </p>
    <div v-if="matchReasons.length" class="mt-3 flex flex-wrap gap-1.5">
      <span
        v-for="reason in matchReasons"
        :key="reason"
        class="rounded-full bg-primary-soft px-2.5 py-0.5 text-[11px] text-primary-dark"
      >
        {{ reason }}
      </span>
    </div>
  </RouterLink>
</template>
