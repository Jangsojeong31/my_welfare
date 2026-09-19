<script setup lang="ts">
import { reactive, ref } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { login } from '@/api/auth'
import { useAuthStore } from '@/store/auth'
import { useToastStore } from '@/store/toast'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const toast = useToastStore()

const form = reactive({
  email: '',
  password: '',
})
const submitting = ref(false)

async function onSubmit() {
  if (submitting.value) {
    return
  }
  submitting.value = true
  try {
    const result = await login({
      email: form.email.trim(),
      password: form.password,
    })
    auth.setSession(result)
    toast.show('로그인되었습니다.', 'success')
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/main'
    await router.push(redirect)
  } catch {
    // Axios interceptor already shows the error toast.
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <section class="mx-auto flex min-h-[calc(100vh-4rem)] max-w-md flex-col justify-center px-4 py-12">
    <div class="rounded-2xl border border-slate-200 bg-white p-8 shadow-sm">
      <h1 class="mb-1 text-2xl font-semibold text-ink">로그인</h1>
      <p class="mb-8 text-sm text-slate-500">이메일과 비밀번호를 입력해주세요.</p>

      <form class="space-y-4" @submit.prevent="onSubmit">
        <label class="block">
          <span class="mb-1.5 block text-sm text-slate-600">Email</span>
          <input
            v-model="form.email"
            type="email"
            required
            autocomplete="email"
            placeholder="user@example.com"
            class="h-11 w-full rounded-xl border border-slate-200 px-3 outline-none focus:border-primary focus:ring-4 focus:ring-primary/15"
          />
        </label>
        <label class="block">
          <span class="mb-1.5 block text-sm text-slate-600">Password</span>
          <input
            v-model="form.password"
            type="password"
            required
            autocomplete="current-password"
            placeholder="비밀번호"
            class="h-11 w-full rounded-xl border border-slate-200 px-3 outline-none focus:border-primary focus:ring-4 focus:ring-primary/15"
          />
        </label>
        <button
          type="submit"
          class="mt-2 h-11 w-full rounded-xl bg-primary text-sm font-medium text-white hover:bg-primary-dark disabled:opacity-60"
          :disabled="submitting"
        >
          {{ submitting ? '로그인 중...' : '로그인' }}
        </button>
      </form>

      <div class="mt-8 border-t border-slate-100 pt-6 text-center">
        <RouterLink
          to="/signup"
          class="inline-flex items-center gap-1.5 text-sm text-slate-500 hover:text-primary"
        >
          <svg class="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
            <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
            <circle cx="9" cy="7" r="4" />
            <line x1="19" y1="8" x2="19" y2="14" />
            <line x1="22" y1="11" x2="16" y2="11" />
          </svg>
          회원가입
        </RouterLink>
      </div>
    </div>
  </section>
</template>
