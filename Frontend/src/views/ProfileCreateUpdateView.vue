<!-- 프로필 생성·수정 폼. 생년월일로 생애주기를 미리 보여 주고 PUT으로 저장한다. -->
<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { fetchMyProfile, saveMyProfile } from '@/api/profile'
import { GENDERS, INCOME_LEVELS } from '@/constants/profile'
import { HOUSEHOLD_TYPES, INTERESTS } from '@/constants/welfareFilters'
import { useAuthStore } from '@/store/auth'
import { useToastStore } from '@/store/toast'
import type { UserProfile } from '@/types/profile'
import { ageFromBirthDate, lifeStageFromAge, todayYmd } from '@/utils/lifeStage'

const router = useRouter()
const auth = useAuthStore()
const toast = useToastStore()

const loading = ref(true)
const loadFailed = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const savedAge = ref<number | null>(null)
const savedLifeStageName = ref('')
const maxBirthDate = todayYmd()

const form = reactive({
  birthDate: '',
  region: '',
  gender: '' as '' | 'F' | 'M',
  incomeLevel: '',
  householdTypeCodes: [] as string[],
  interestCodes: [] as string[],
})

const previewAge = computed(() => ageFromBirthDate(form.birthDate))
const previewLifeStage = computed(() =>
  previewAge.value == null ? null : lifeStageFromAge(previewAge.value),
)

function toggle(target: string[], code: string): string[] {
  return target.includes(code) ? target.filter((item) => item !== code) : [...target, code]
}

function applyProfile(profile: UserProfile) {
  form.birthDate = profile.birthDate ?? ''
  form.region = profile.region ?? ''
  form.gender = profile.gender ?? ''
  form.incomeLevel = profile.incomeLevel ?? ''
  form.householdTypeCodes = profile.householdTypes.map((item) => item.code)
  form.interestCodes = profile.interests.map((item) => item.code)
  savedAge.value = profile.age
  savedLifeStageName.value = profile.lifeStages.map((item) => item.name).join(', ')
}

async function load() {
  loading.value = true
  loadFailed.value = false
  try {
    const profile = await fetchMyProfile()
    if (profile) {
      applyProfile(profile)
      isEdit.value = true
    }
  } catch {
    loadFailed.value = true
  } finally {
    loading.value = false
  }
}

async function onSubmit() {
  if (submitting.value) {
    return
  }
  if (!form.gender) {
    toast.show('성별을 선택해 주세요.', 'info')
    return
  }
  submitting.value = true
  try {
    await saveMyProfile({
      birthDate: form.birthDate,
      region: form.region.trim(),
      gender: form.gender,
      incomeLevel: form.incomeLevel || undefined,
      householdTypeCodes: form.householdTypeCodes,
      interestCodes: form.interestCodes,
    })
    toast.show('프로필이 저장되었습니다.', 'success')
    await router.push({ name: 'profile' })
  } catch {
    // Axios interceptor already shows the error toast.
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  void load()
})
</script>

<template>
  <section class="mx-auto max-w-3xl px-4 py-8">
    <div class="mb-6">
      <h1 class="text-2xl font-semibold text-ink">
        {{ isEdit ? '프로필 수정' : '프로필 입력' }}
      </h1>
      <p class="mt-1 text-sm text-slate-500">
        {{ auth.user?.name }}님의 정보를 입력하면 맞춤 복지 조회에 사용됩니다.
      </p>
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

    <form
      v-else
      class="space-y-6"
      @submit.prevent="onSubmit"
    >
      <section class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
        <h2 class="mb-4 text-sm font-semibold text-ink">기본 정보</h2>
        <div class="grid gap-4 sm:grid-cols-2">
          <label class="block">
            <span class="mb-1.5 block text-sm text-slate-600">생년월일</span>
            <input
              v-model="form.birthDate"
              type="date"
              required
              :max="maxBirthDate"
              class="h-11 w-full rounded-xl border border-slate-200 px-3 outline-none focus:border-primary focus:ring-4 focus:ring-primary/15"
            />
            <p v-if="previewAge != null && previewLifeStage" class="mt-1.5 text-xs text-slate-500">
              만 {{ previewAge }}세 · {{ previewLifeStage.name }}
            </p>
          </label>

          <div>
            <p class="mb-1.5 text-sm text-slate-600">성별</p>
            <div class="flex gap-2">
              <button
                v-for="option in GENDERS"
                :key="option.code"
                type="button"
                class="h-11 flex-1 rounded-xl border text-sm"
                :class="
                  form.gender === option.code
                    ? 'border-primary bg-primary-soft text-primary-dark'
                    : 'border-slate-200 text-slate-600 hover:border-primary/40'
                "
                @click="form.gender = option.code"
              >
                {{ option.name }}
              </button>
            </div>
          </div>

          <label class="block sm:col-span-2">
            <span class="mb-1.5 block text-sm text-slate-600">거주 지역</span>
            <input
              v-model="form.region"
              type="text"
              required
              maxlength="100"
              placeholder="예: 서울특별시 강남구"
              class="h-11 w-full rounded-xl border border-slate-200 px-3 outline-none focus:border-primary focus:ring-4 focus:ring-primary/15"
            />
            <p class="mt-1.5 text-xs text-slate-400">시도와 시군구를 함께 입력해 주세요.</p>
          </label>

          <label class="block sm:col-span-2">
            <span class="mb-1.5 block text-sm text-slate-600">소득 수준</span>
            <select
              v-model="form.incomeLevel"
              class="h-11 w-full rounded-xl border border-slate-200 bg-white px-3 outline-none focus:border-primary focus:ring-4 focus:ring-primary/15"
            >
              <option value="">선택하지 않음</option>
              <option v-for="level in INCOME_LEVELS" :key="level" :value="level">
                {{ level }}
              </option>
            </select>
          </label>
        </div>
      </section>

      <section class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
        <h2 class="mb-1 text-sm font-semibold text-ink">해당하는 상황</h2>
        <p class="mb-3 text-xs text-slate-400">해당하는 항목을 모두 선택해 주세요.</p>
        <div class="flex flex-wrap gap-2">
          <button
            v-for="option in HOUSEHOLD_TYPES"
            :key="option.code"
            type="button"
            class="rounded-full border px-3 py-1.5 text-xs"
            :class="
              form.householdTypeCodes.includes(option.code)
                ? 'border-primary bg-primary-soft text-primary-dark'
                : 'border-slate-200 text-slate-600 hover:border-primary/40'
            "
            @click="form.householdTypeCodes = toggle(form.householdTypeCodes, option.code)"
          >
            {{ option.name }}
          </button>
        </div>
      </section>

      <section class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
        <h2 class="mb-1 text-sm font-semibold text-ink">관심 분야</h2>
        <p class="mb-3 text-xs text-slate-400">관심 있는 복지 분야를 선택해 주세요. 여러 개 선택할 수 있습니다.</p>
        <div class="flex flex-wrap gap-2">
          <button
            v-for="option in INTERESTS"
            :key="option.code"
            type="button"
            class="rounded-full border px-3 py-1.5 text-xs"
            :class="
              form.interestCodes.includes(option.code)
                ? 'border-primary bg-primary-soft text-primary-dark'
                : 'border-slate-200 text-slate-600 hover:border-primary/40'
            "
            @click="form.interestCodes = toggle(form.interestCodes, option.code)"
          >
            {{ option.name }}
          </button>
        </div>
      </section>

      <div class="flex items-center justify-between gap-3">
        <p v-if="savedAge != null" class="text-xs text-slate-400">
          저장된 나이 만 {{ savedAge }}세
          <span v-if="savedLifeStageName"> · {{ savedLifeStageName }}</span>
        </p>
        <span v-else />
        <button
          type="submit"
          class="h-11 rounded-xl bg-primary px-5 text-sm font-medium text-white hover:bg-primary-dark disabled:opacity-60"
          :disabled="submitting"
        >
          {{ submitting ? '저장 중...' : '프로필 저장' }}
        </button>
      </div>
    </form>
  </section>
</template>
