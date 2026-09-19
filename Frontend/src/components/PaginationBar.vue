<script setup lang="ts">
import { computed } from 'vue'
import { PAGE_SIZE_OPTIONS } from '@/constants/welfareFilters'

const props = defineProps<{
  page: number
  size: number
  totalPages: number
  totalElements: number
}>()

const emit = defineEmits<{
  'update:page': [value: number]
  'update:size': [value: number]
}>()

const currentPage = computed(() => props.page + 1)

const visiblePages = computed(() => {
  const total = Math.max(props.totalPages, 1)
  const current = props.page
  const start = Math.max(0, current - 2)
  const end = Math.min(total - 1, start + 4)
  const from = Math.max(0, end - 4)
  const pages: number[] = []
  for (let i = from; i <= end; i += 1) {
    pages.push(i)
  }
  return pages
})

function changePage(next: number) {
  if (next < 0 || next >= Math.max(props.totalPages, 1) || next === props.page) {
    return
  }
  emit('update:page', next)
}

function changeSize(event: Event) {
  const value = Number((event.target as HTMLSelectElement).value)
  emit('update:size', value)
}
</script>

<template>
  <div class="flex flex-col items-center justify-between gap-4 rounded-2xl border border-slate-200 bg-white px-4 py-3 sm:flex-row">
    <label class="flex items-center gap-2 text-sm text-slate-600">
      조회 건수
      <select
        :value="size"
        class="rounded-lg border border-slate-200 bg-white px-2 py-1 text-sm outline-none focus:border-primary"
        @change="changeSize"
      >
        <option v-for="option in PAGE_SIZE_OPTIONS" :key="option" :value="option">
          {{ option }}건
        </option>
      </select>
      <span class="text-xs text-slate-400">총 {{ totalElements.toLocaleString() }}건</span>
    </label>

    <div class="flex items-center gap-1">
      <button
        type="button"
        class="rounded-lg px-2 py-1 text-sm text-slate-500 hover:bg-slate-50 disabled:text-slate-300"
        :disabled="page <= 0"
        @click="changePage(page - 1)"
      >
        이전
      </button>
      <button
        v-for="item in visiblePages"
        :key="item"
        type="button"
        class="min-w-8 rounded-lg px-2 py-1 text-sm"
        :class="item === page ? 'bg-primary text-white' : 'text-slate-600 hover:bg-primary-soft'"
        @click="changePage(item)"
      >
        {{ item + 1 }}
      </button>
      <button
        type="button"
        class="rounded-lg px-2 py-1 text-sm text-slate-500 hover:bg-slate-50 disabled:text-slate-300"
        :disabled="page >= totalPages - 1 || totalPages === 0"
        @click="changePage(page + 1)"
      >
        다음
      </button>
      <span class="ml-2 text-xs text-slate-400">{{ currentPage }} / {{ Math.max(totalPages, 1) }}</span>
    </div>
  </div>
</template>
