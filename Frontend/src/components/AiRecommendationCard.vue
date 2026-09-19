<script setup lang="ts">
import { RouterLink } from 'vue-router'
import type { WelfareAiRecommendation } from '@/types/ai'

defineProps<{
  item: WelfareAiRecommendation
}>()
</script>

<template>
  <component
    :is="item.id ? RouterLink : 'article'"
    v-bind="item.id ? { to: { name: 'welfare-detail', params: { id: item.id } } } : {}"
    class="block rounded-2xl border border-slate-200 bg-white p-5 shadow-sm transition hover:border-primary/40 hover:shadow-md"
  >
    <div class="mb-2 flex flex-wrap items-start justify-between gap-2">
      <h3 class="text-base font-semibold text-ink">{{ item.servNm }}</h3>
    </div>
    <p class="mb-3 text-sm leading-6 text-slate-600">
      {{ item.summary || '상세 설명이 없습니다.' }}
    </p>
    <p v-if="item.reason" class="rounded-xl bg-primary-soft px-3 py-2 text-xs leading-5 text-primary-dark">
      {{ item.reason }}
    </p>
  </component>
</template>
