<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchWelfareList } from '@/api/welfare'
import PaginationBar from '@/components/PaginationBar.vue'
import WelfareFilterBox from '@/components/WelfareFilterBox.vue'
import WelfareListCard from '@/components/WelfareListCard.vue'
import type { WelfareListResponse } from '@/types/welfare'
import { toPositiveInt, toStringArray } from '@/utils/format'

const route = useRoute()
const router = useRouter()

const lifeStages = ref<string[]>([])
const householdTypes = ref<string[]>([])
const interests = ref<string[]>([])
const page = ref(0)
const size = ref(20)
const loading = ref(false)
const result = ref<WelfareListResponse | null>(null)

const items = computed(() => result.value?.content ?? [])
const totalPages = computed(() => result.value?.totalPages ?? 0)
const totalElements = computed(() => result.value?.totalElements ?? 0)

function syncFromRoute() {
  lifeStages.value = toStringArray(route.query.lifeStages)
  householdTypes.value = toStringArray(route.query.householdTypes)
  interests.value = toStringArray(route.query.interests)
  page.value = toPositiveInt(route.query.page, 0)
  size.value = toPositiveInt(route.query.size, 20)
}

function pushQuery(nextPage = 0, nextSize = size.value) {
  void router.push({
    name: 'welfare-list',
    query: {
      lifeStages: lifeStages.value.join(',') || undefined,
      householdTypes: householdTypes.value.join(',') || undefined,
      interests: interests.value.join(',') || undefined,
      page: String(nextPage),
      size: String(nextSize),
    },
  })
}

function applyFilters() {
  pushQuery(0, size.value)
}

function resetFilters() {
  lifeStages.value = []
  householdTypes.value = []
  interests.value = []
  pushQuery(0, size.value)
}

function onPageChange(next: number) {
  pushQuery(next, size.value)
}

function onSizeChange(next: number) {
  pushQuery(0, next)
}

async function load() {
  syncFromRoute()
  loading.value = true
  try {
    result.value = await fetchWelfareList({
      lifeStages: lifeStages.value,
      householdTypes: householdTypes.value,
      interests: interests.value,
      page: page.value,
      size: size.value,
    })
  } catch {
    result.value = null
  } finally {
    loading.value = false
  }
}

watch(
  () => route.query,
  () => {
    void load()
  },
  { immediate: true },
)
</script>

<template>
  <section class="mx-auto max-w-6xl px-4 py-8">
    <div class="mb-6">
      <h1 class="text-2xl font-semibold text-ink">복지 목록</h1>
      <p class="mt-1 text-sm text-slate-500">조건에 맞는 복지 서비스를 확인하고 상세 정보를 살펴보세요.</p>
    </div>

    <WelfareFilterBox
      v-model:life-stages="lifeStages"
      v-model:household-types="householdTypes"
      v-model:interests="interests"
    >
      <template #actions>
        <div class="flex gap-2">
          <button
            type="button"
            class="rounded-lg px-3 py-1.5 text-xs text-slate-500 hover:bg-slate-50"
            @click="resetFilters"
          >
            초기화
          </button>
          <button
            type="button"
            class="rounded-lg bg-primary px-3 py-1.5 text-xs font-medium text-white hover:bg-primary-dark"
            @click="applyFilters"
          >
            적용
          </button>
        </div>
      </template>
    </WelfareFilterBox>

    <div class="mt-6 space-y-3">
      <p v-if="loading" class="rounded-2xl border border-dashed border-slate-200 bg-white py-16 text-center text-sm text-slate-400">
        목록을 불러오는 중입니다...
      </p>
      <template v-else-if="items.length">
        <WelfareListCard v-for="item in items" :key="item.id" :item="item" />
      </template>
      <p v-else class="rounded-2xl border border-dashed border-slate-200 bg-white py-16 text-center text-sm text-slate-400">
        조건에 맞는 복지 서비스가 없습니다.
      </p>
    </div>

    <div class="mt-6">
      <PaginationBar
        :page="page"
        :size="size"
        :total-pages="totalPages"
        :total-elements="totalElements"
        @update:page="onPageChange"
        @update:size="onSizeChange"
      />
    </div>
  </section>
</template>
