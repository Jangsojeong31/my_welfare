<script setup lang="ts">
import { RouterLink, useRoute, useRouter } from 'vue-router'
import AppTabs from '@/components/AppTabs.vue'
import { useAuthStore } from '@/store/auth'

defineProps<{
  showTabs: boolean
}>()

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

function logout() {
  auth.logout()
  if (route.meta.requiresAuth) {
    void router.push('/main')
  }
}
</script>

<template>
  <header class="border-b border-slate-200 bg-white">
    <div class="mx-auto flex h-16 max-w-6xl items-center justify-between px-4">
      <RouterLink to="/main" class="flex items-center gap-2 text-primary">
        <span class="flex h-8 w-8 items-center justify-center rounded-full bg-primary-soft text-sm font-semibold">
          MW
        </span>
        <span class="text-lg font-semibold tracking-tight text-ink">마이 복지</span>
      </RouterLink>

      <div class="flex items-center gap-2">
        <template v-if="auth.isAuthenticated">
          <span class="hidden text-sm text-slate-500 sm:inline">{{ auth.user?.name }}님</span>
          <RouterLink
            to="/profile"
            class="rounded-full px-3 py-1.5 text-sm text-slate-600 hover:bg-primary-soft hover:text-primary-dark"
          >
            프로필
          </RouterLink>
          <button
            type="button"
            class="rounded-full px-3 py-1.5 text-sm text-slate-600 hover:bg-slate-50"
            @click="logout"
          >
            로그아웃
          </button>
        </template>
        <template v-else>
          <RouterLink
            to="/login"
            class="inline-flex items-center gap-1.5 rounded-full px-3 py-1.5 text-sm text-slate-600 hover:bg-primary-soft hover:text-primary-dark"
            title="로그인"
          >
            <svg class="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
              <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4" />
              <polyline points="10 17 15 12 10 7" />
              <line x1="15" y1="12" x2="3" y2="12" />
            </svg>
            로그인
          </RouterLink>
          <RouterLink
            to="/signup"
            class="inline-flex items-center gap-1.5 rounded-full px-3 py-1.5 text-sm text-slate-600 hover:bg-primary-soft hover:text-primary-dark"
            title="회원가입"
          >
            <svg class="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
              <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
              <circle cx="9" cy="7" r="4" />
              <line x1="19" y1="8" x2="19" y2="14" />
              <line x1="22" y1="11" x2="16" y2="11" />
            </svg>
            회원가입
          </RouterLink>
        </template>
      </div>
    </div>

    <AppTabs v-if="showTabs" />
  </header>
</template>
