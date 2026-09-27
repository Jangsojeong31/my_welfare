<script setup lang="ts">
import axios from 'axios'
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchMyWelfareList } from '@/api/welfare'
import PaginationBar from '@/components/PaginationBar.vue'
import WelfareListCard from '@/components/WelfareListCard.vue'
import { useAuthStore } from '@/store/auth'
import type { WelfareListResponse } from '@/types/welfare'
import { toPositiveInt } from '@/utils/format'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const page = ref(0)
const size = ref(20)
const loading = ref(false)
const loadFailed = ref(false)
const missingProfile = ref(false)
const result = ref<WelfareListResponse | null>(null)

const items = computed(() => result.value?.content ?? [])
const totalPages = computed(() => result.value?.totalPages ?? 0)
const totalElements = computed(() => result.value?.totalElements ?? 0)

function isProfileNotFound(error: unknown): boolean {
  return axios.isAxiosError(error) && error.response?.data?.errorCode === 'PROFILE_NOT_FOUND'
}

function syncFromRoute() {
  page.value = toPositiveInt(route.query.page, 0)
  size.value = toPositiveInt(route.query.size, 20)
}

function pushQuery(nextPage = 0, nextSize = size.value) {
  void router.push({
    name: 'personalized',
    query: {
      page: String(nextPage),
      size: String(nextSize),
    },
  })
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
  loadFailed.value = false
  missingProfile.value = false
  try {
    result.value = await fetchMyWelfareList({
      page: page.value,
      size: size.value,
    })
  } catch (error) {
    result.value = null
    if (isProfileNotFound(error)) {
      missingProfile.value = true
    } else {
      loadFailed.value = true
    }
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
      <h1 class="text-2xl font-semibold text-ink">프로필 맞춤 복지</h1>
      <p class="mt-1 text-sm text-slate-500">
        {{ auth.user?.name }}님의 프로필에 맞는 복지 서비스를 점수 높은 순으로 보여드립니다.
      </p>
    </div>

    <div class="space-y-3">
      <p
        v-if="loading"
        class="rounded-2xl border border-dashed border-slate-200 bg-white py-16 text-center text-sm text-slate-400"
      >
        맞춤 복지를 불러오는 중입니다...
      </p>

      <div
        v-else-if="missingProfile"
        class="rounded-2xl border border-slate-200 bg-white p-8 text-center shadow-sm"
      >
        <h2 class="mb-2 text-lg font-semibold text-ink">저장된 프로필이 없습니다</h2>
        <p class="mb-6 text-sm text-slate-500">맞춤 복지를 보려면 프로필을 먼저 입력해 주세요.</p>
        <button
          type="button"
          class="h-11 rounded-xl bg-primary px-5 text-sm font-medium text-white hover:bg-primary-dark"
          @click="router.push({ name: 'profile-edit' })"
        >
          프로필 입력하기
        </button>
      </div>

      <div
        v-else-if="loadFailed"
        class="rounded-2xl border border-slate-200 bg-white p-8 text-center shadow-sm"
      >
        <h2 class="mb-2 text-lg font-semibold text-ink">맞춤 복지를 불러오지 못했습니다</h2>
        <p class="mb-6 text-sm text-slate-500">잠시 후 다시 시도해 주세요.</p>
        <button
          type="button"
          class="h-11 rounded-xl bg-primary px-5 text-sm font-medium text-white hover:bg-primary-dark"
          @click="load"
        >
          다시 시도
        </button>
      </div>

      <template v-else-if="items.length">
        <WelfareListCard v-for="item in items" :key="item.id" :item="item" />
      </template>

      <p
        v-else
        class="rounded-2xl border border-dashed border-slate-200 bg-white py-16 text-center text-sm text-slate-400"
      >
        프로필에 맞는 복지 서비스가 없습니다.
      </p>
    </div>

    <div v-if="!loading && !missingProfile && !loadFailed" class="mt-6">
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
