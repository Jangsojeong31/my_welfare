<script setup lang="ts">
import { HOUSEHOLD_TYPES, INTERESTS, LIFE_STAGES } from '@/constants/welfareFilters'

const lifeStages = defineModel<string[]>('lifeStages', { default: () => [] })
const householdTypes = defineModel<string[]>('householdTypes', { default: () => [] })
const interests = defineModel<string[]>('interests', { default: () => [] })

defineProps<{
  compact?: boolean
}>()

function toggle(target: string[], code: string): string[] {
  return target.includes(code) ? target.filter((item) => item !== code) : [...target, code]
}
</script>

<template>
  <section class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
    <div class="mb-4 flex items-center justify-between">
      <h2 class="text-sm font-semibold text-ink">조건 필터</h2>
      <slot name="actions" />
    </div>

    <div class="grid gap-5" :class="compact ? 'md:grid-cols-1' : 'md:grid-cols-3'">
      <div>
        <p class="mb-2 text-xs font-medium tracking-wide text-slate-500">생애주기</p>
        <div class="flex flex-wrap gap-2">
          <button
            v-for="option in LIFE_STAGES"
            :key="option.code"
            type="button"
            class="rounded-full border px-3 py-1 text-xs"
            :class="
              lifeStages.includes(option.code)
                ? 'border-primary bg-primary-soft text-primary-dark'
                : 'border-slate-200 text-slate-600 hover:border-primary/40'
            "
            @click="lifeStages = toggle(lifeStages, option.code)"
          >
            {{ option.name }}
          </button>
        </div>
      </div>

      <div>
        <p class="mb-2 text-xs font-medium tracking-wide text-slate-500">가구형태</p>
        <div class="flex flex-wrap gap-2">
          <button
            v-for="option in HOUSEHOLD_TYPES"
            :key="option.code"
            type="button"
            class="rounded-full border px-3 py-1 text-xs"
            :class="
              householdTypes.includes(option.code)
                ? 'border-primary bg-primary-soft text-primary-dark'
                : 'border-slate-200 text-slate-600 hover:border-primary/40'
            "
            @click="householdTypes = toggle(householdTypes, option.code)"
          >
            {{ option.name }}
          </button>
        </div>
      </div>

      <div>
        <p class="mb-2 text-xs font-medium tracking-wide text-slate-500">관심분야</p>
        <div class="flex flex-wrap gap-2">
          <button
            v-for="option in INTERESTS"
            :key="option.code"
            type="button"
            class="rounded-full border px-3 py-1 text-xs"
            :class="
              interests.includes(option.code)
                ? 'border-primary bg-primary-soft text-primary-dark'
                : 'border-slate-200 text-slate-600 hover:border-primary/40'
            "
            @click="interests = toggle(interests, option.code)"
          >
            {{ option.name }}
          </button>
        </div>
      </div>
    </div>
  </section>
</template>
