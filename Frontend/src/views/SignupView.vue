<script setup lang="ts">
import { reactive, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { checkEmail, signup } from '@/api/auth'
import { useToastStore } from '@/store/toast'

const router = useRouter()
const toast = useToastStore()

const form = reactive({
  email: '',
  password: '',
  name: '',
})
const submitting = ref(false)
const emailMessage = ref('')
const emailAvailable = ref<boolean | null>(null)

async function onEmailBlur() {
  const email = form.email.trim()
  if (!email) {
    emailMessage.value = ''
    emailAvailable.value = null
    return
  }
  try {
    const result = await checkEmail(email)
    emailAvailable.value = !result.duplicated
    emailMessage.value = result.duplicated ? '이미 사용 중인 이메일입니다.' : '사용 가능한 이메일입니다.'
  } catch {
    emailAvailable.value = null
    emailMessage.value = ''
  }
}

async function onSubmit() {
  if (submitting.value) {
    return
  }
  if (emailAvailable.value === false) {
    toast.show('이미 사용 중인 이메일입니다.', 'error')
    return
  }
  submitting.value = true
  try {
    await signup({
      email: form.email.trim(),
      password: form.password,
      name: form.name.trim(),
    })
    toast.show('회원가입이 완료되었습니다. 로그인해주세요.', 'success')
    await router.push('/login')
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
      <h1 class="mb-1 text-2xl font-semibold text-ink">회원가입</h1>
      <p class="mb-8 text-sm text-slate-500">이메일, 비밀번호, 이름을 입력해주세요.</p>

      <form class="space-y-4" @submit.prevent="onSubmit">
        <label class="block">
          <span class="mb-1.5 block text-sm text-slate-600">이름</span>
          <input
            v-model="form.name"
            type="text"
            required
            maxlength="50"
            placeholder="홍길동"
            class="h-11 w-full rounded-xl border border-slate-200 px-3 outline-none focus:border-primary focus:ring-4 focus:ring-primary/15"
          />
        </label>
        <label class="block">
          <span class="mb-1.5 block text-sm text-slate-600">Email</span>
          <input
            v-model="form.email"
            type="email"
            required
            autocomplete="email"
            placeholder="user@example.com"
            class="h-11 w-full rounded-xl border border-slate-200 px-3 outline-none focus:border-primary focus:ring-4 focus:ring-primary/15"
            @blur="onEmailBlur"
          />
          <p
            v-if="emailMessage"
            class="mt-1.5 text-xs"
            :class="emailAvailable ? 'text-emerald-600' : 'text-rose-500'"
          >
            {{ emailMessage }}
          </p>
        </label>
        <label class="block">
          <span class="mb-1.5 block text-sm text-slate-600">Password</span>
          <input
            v-model="form.password"
            type="password"
            required
            minlength="8"
            autocomplete="new-password"
            placeholder="8자 이상"
            class="h-11 w-full rounded-xl border border-slate-200 px-3 outline-none focus:border-primary focus:ring-4 focus:ring-primary/15"
          />
        </label>
        <button
          type="submit"
          class="mt-2 h-11 w-full rounded-xl bg-primary text-sm font-medium text-white hover:bg-primary-dark disabled:opacity-60"
          :disabled="submitting"
        >
          {{ submitting ? '가입 중...' : '회원가입' }}
        </button>
      </form>

      <div class="mt-8 border-t border-slate-100 pt-6 text-center">
        <RouterLink to="/login" class="text-sm text-slate-500 hover:text-primary">
          이미 계정이 있으신가요? 로그인
        </RouterLink>
      </div>
    </div>
  </section>
</template>
