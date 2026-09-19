<script setup lang="ts">
withDefaults(
  defineProps<{
    modelValue: string
    placeholder?: string
    loading?: boolean
    disabled?: boolean
    submitLabel?: string
  }>(),
  {
    placeholder: '상황을 자유롭게 적어 주세요',
    loading: false,
    disabled: false,
    submitLabel: '검색',
  },
)

const emit = defineEmits<{
  'update:modelValue': [value: string]
  submit: []
}>()

function onSubmit() {
  emit('submit')
}
</script>

<template>
  <form class="w-full" @submit.prevent="onSubmit">
    <label class="relative block">
      <span class="sr-only">복지 검색</span>
      <input
        :value="modelValue"
        type="search"
        :placeholder="placeholder"
        :disabled="disabled || loading"
        autocomplete="off"
        class="h-16 w-full rounded-2xl border border-slate-200 bg-white pl-6 pr-32 text-lg text-ink shadow-sm outline-none placeholder:text-slate-400 focus:border-primary focus:ring-4 focus:ring-primary/15 disabled:bg-slate-50"
        @input="emit('update:modelValue', ($event.target as HTMLInputElement).value)"
      />
      <button
        type="submit"
        class="absolute top-1/2 right-3 -translate-y-1/2 rounded-xl bg-primary px-5 py-2.5 text-sm font-medium text-white hover:bg-primary-dark disabled:opacity-60"
        :disabled="disabled || loading"
      >
        {{ loading ? '검색 중...' : submitLabel }}
      </button>
    </label>
  </form>
</template>
