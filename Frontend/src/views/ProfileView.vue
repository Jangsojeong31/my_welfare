<!-- 저장된 프로필 조회 화면. 없으면 입력 화면으로 보낸다. -->
<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { fetchMyProfile } from '@/api/profile'
import { GENDERS } from '@/constants/profile'
import { useAuthStore } from '@/store/auth'
import type { UserProfile } from '@/types/profile'

const router = useRouter()
const auth = useAuthStore()

const loading = ref(true)
const loadFailed = ref(false)
const profile = ref<UserProfile | null>(null)

const genderName = computed(() => {
  const code = profile.value?.gender
  return GENDERS.find((item) => item.code === code)?.name ?? '-'
})

const lifeStageName = computed(() => {
  const names = profile.value?.lifeStages.map((item) => item.name).filter(Boolean) ?? []
  return names.join(', ') || '-'
})

async function load() {
  loading.value = true
  loadFailed.value = false
  try {
    const result = await fetchMyProfile()
    if (!result) {
      await router.replace({ name: 'profile-edit' })
      return
    }
    profile.value = result
  } catch {
    loadFailed.value = true
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  void load()
})
</script>

<template>
  <section class="mx-auto max-w-3xl px-4 py-8">
    <div class="mb-6 flex items-start justify-between gap-4">
      <div>
        <h1 class="text-2xl font-semibold text-ink">내 프로필</h1>
        <p class="mt-1 text-sm text-slate-500">
          {{ auth.user?.name }}님의 저장된 프로필입니다.
        </p>
      </div>
      <button
        v-if="profile"
        type="button"
        class="h-10 shrink-0 rounded-xl bg-primary px-4 text-sm font-medium text-white hover:bg-primary-dark"
        @click="router.push({ name: 'profile-edit' })"
      >
        프로필 수정
      </button>
    </div>

    <p
      v-if="loading"
      class="rounded-2xl border border-dashed border-slate-200 bg-white py-16 text-center text-sm text-slate-400"
    >
      프로필을 불러오는 중입니다...
    </p>

    <div
      v-else-if="loadFailed"
      class="rounded-2xl border border-slate-200 bg-white p-8 text-center shadow-sm"
    >
      <h2 class="mb-2 text-lg font-semibold text-ink">프로필을 불러오지 못했습니다</h2>
      <p class="mb-6 text-sm text-slate-500">잠시 후 다시 시도해 주세요.</p>
      <button
        type="button"
        class="h-11 rounded-xl bg-primary px-5 text-sm font-medium text-white hover:bg-primary-dark"
        @click="load"
      >
        다시 시도
      </button>
    </div>

    <div v-else-if="profile" class="space-y-5">
      <section class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
        <h2 class="mb-4 text-sm font-semibold text-ink">기본 정보</h2>
        <dl class="grid grid-cols-[7rem_1fr] gap-y-3 text-sm">
          <dt class="text-slate-400">생년월일</dt>
          <dd class="text-ink">{{ profile.birthDate }}</dd>
          <dt class="text-slate-400">나이</dt>
          <dd class="text-ink">만 {{ profile.age }}세</dd>
          <dt class="text-slate-400">생애주기</dt>
          <dd class="text-ink">{{ lifeStageName }}</dd>
          <dt class="text-slate-400">성별</dt>
          <dd class="text-ink">{{ genderName }}</dd>
          <dt class="text-slate-400">거주 지역</dt>
          <dd class="text-ink">{{ profile.region || '-' }}</dd>
          <dt class="text-slate-400">소득 수준</dt>
          <dd class="text-ink">{{ profile.incomeLevel || '-' }}</dd>
        </dl>
      </section>

      <section class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
        <h2 class="mb-3 text-sm font-semibold text-ink">해당하는 상황</h2>
        <div v-if="profile.householdTypes.length" class="flex flex-wrap gap-2">
          <span
            v-for="item in profile.householdTypes"
            :key="item.code"
            class="rounded-full bg-primary-soft px-3 py-1 text-xs text-primary-dark"
          >
            {{ item.name }}
          </span>
        </div>
        <p v-else class="text-sm text-slate-400">선택한 항목이 없습니다.</p>
      </section>

      <section class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
        <h2 class="mb-3 text-sm font-semibold text-ink">관심 분야</h2>
        <div v-if="profile.interests.length" class="flex flex-wrap gap-2">
          <span
            v-for="item in profile.interests"
            :key="item.code"
            class="rounded-full bg-primary-soft px-3 py-1 text-xs text-primary-dark"
          >
            {{ item.name }}
          </span>
        </div>
        <p v-else class="text-sm text-slate-400">선택한 항목이 없습니다.</p>
      </section>
    </div>
  </section>
</template>
