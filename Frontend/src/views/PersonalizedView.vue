<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { fetchWelfareList } from '@/api/welfare'
import PaginationBar from '@/components/PaginationBar.vue'
import WelfareFilterBox from '@/components/WelfareFilterBox.vue'
import WelfareListCard from '@/components/WelfareListCard.vue'
import { useAuthStore } from '@/store/auth'
import { useProfileStore } from '@/store/profile'
import { useToastStore } from '@/store/toast'
import type { WelfareListResponse } from '@/types/welfare'

const auth = useAuthStore()
const profile = useProfileStore()
const toast = useToastStore()

const lifeStages = ref<string[]>([...profile.filters.lifeStages])
const householdTypes = ref<string[]>([...profile.filters.householdTypes])
const interests = ref<string[]>([...profile.filters.interests])
const page = ref(0)
const size = ref(20)
const loading = ref(false)
const result = ref<WelfareListResponse | null>(null)

const items = computed(() => result.value?.content ?? [])
const totalPages = computed(() => result.value?.totalPages ?? 0)
const totalElements = computed(() => result.value?.totalElements ?? 0)
const hasFilters = computed(
  () => lifeStages.value.length + householdTypes.value.length + interests.value.length > 0,
)

async function load() {
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

function saveAndSearch() {
  profile.save({
    lifeStages: lifeStages.value,
    householdTypes: householdTypes.value,
    interests: interests.value,
  })
  page.value = 0
  toast.show('맞춤 조건이 저장되었습니다.', 'success')
  void load()
}

function onPageChange(next: number) {
  page.value = next
  void load()
}

function onSizeChange(next: number) {
  size.value = next
  page.value = 0
  void load()
}

onMounted(() => {
  void load()
})
</script>

<template>
  <section class="mx-auto max-w-6xl px-4 py-8">
    <div class="mb-6">
      <h1 class="text-2xl font-semibold text-ink">프로필 맞춤 복지</h1>
      <p class="mt-1 text-sm text-slate-500">
        {{ auth.user?.name }}님의 조건에 맞는 복지 서비스를 보여드립니다.
      </p>
    </div>

    <WelfareFilterBox
      v-model:life-stages="lifeStages"
      v-model:household-types="householdTypes"
      v-model:interests="interests"
    >
      <template #actions>
        <button
          type="button"
          class="rounded-lg bg-primary px-3 py-1.5 text-xs font-medium text-white hover:bg-primary-dark"
          @click="saveAndSearch"
        >
          조건 저장 후 조회
        </button>
      </template>
    </WelfareFilterBox>

    <p v-if="!hasFilters" class="mt-4 text-xs text-slate-400">
      조건을 선택하지 않으면 전체 복지 목록이 표시됩니다.
    </p>

    <div class="mt-6 space-y-3">
      <p v-if="loading" class="rounded-2xl border border-dashed border-slate-200 bg-white py-16 text-center text-sm text-slate-400">
        맞춤 복지를 불러오는 중입니다...
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
