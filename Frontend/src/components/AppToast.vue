<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { useToastStore } from '@/store/toast'

const toast = useToastStore()
const { visible, message, type } = storeToRefs(toast)

const toneClass: Record<string, string> = {
  error: 'bg-white text-slate-700 border-rose-200',
  success: 'bg-white text-slate-700 border-emerald-200',
  info: 'bg-white text-slate-700 border-primary/30',
}
</script>

<template>
  <Teleport to="body">
    <Transition
      enter-active-class="transition duration-200 ease-out"
      enter-from-class="translate-y-2 opacity-0"
      enter-to-class="translate-y-0 opacity-100"
      leave-active-class="transition duration-150 ease-in"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div
        v-if="visible"
        class="fixed top-5 right-5 z-50 max-w-sm rounded-xl border px-4 py-3 shadow-md"
        :class="toneClass[type] ?? toneClass.info"
        role="alert"
      >
        <div class="flex items-start gap-3">
          <span
            class="mt-0.5 h-2 w-2 shrink-0 rounded-full"
            :class="{
              'bg-rose-400': type === 'error',
              'bg-emerald-400': type === 'success',
              'bg-primary': type === 'info',
            }"
          />
          <p class="text-sm leading-6">{{ message }}</p>
          <button type="button" class="ml-2 text-slate-400 hover:text-slate-600" @click="toast.hide()">
            ×
          </button>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>
