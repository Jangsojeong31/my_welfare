import { defineStore } from 'pinia'
import { ref } from 'vue'

export type ToastType = 'error' | 'success' | 'info'

export const useToastStore = defineStore('toast', () => {
  const message = ref('')
  const type = ref<ToastType>('info')
  const visible = ref(false)
  let timer: number | undefined

  function show(text: string, toastType: ToastType = 'error') {
    message.value = text
    type.value = toastType
    visible.value = true
    window.clearTimeout(timer)
    timer = window.setTimeout(() => {
      visible.value = false
    }, 4000)
  }

  function hide() {
    visible.value = false
    window.clearTimeout(timer)
  }

  return {
    message,
    type,
    visible,
    show,
    hide,
  }
})
