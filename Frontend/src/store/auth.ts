import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import type { AuthUser, LoginResponse } from '@/types/auth'

const TOKEN_KEY = 'accessToken'
const USER_KEY = 'authUser'

function readUser(): AuthUser | null {
  const raw = localStorage.getItem(USER_KEY)
  if (!raw) {
    return null
  }
  try {
    return JSON.parse(raw) as AuthUser
  } catch {
    localStorage.removeItem(USER_KEY)
    return null
  }
}

export const useAuthStore = defineStore('auth', () => {
  const accessToken = ref<string | null>(localStorage.getItem(TOKEN_KEY))
  const user = ref<AuthUser | null>(readUser())

  const isAuthenticated = computed(() => Boolean(accessToken.value))

  function setSession(payload: LoginResponse) {
    accessToken.value = payload.accessToken
    user.value = {
      userId: payload.userId,
      email: payload.email,
      name: payload.name,
    }
    localStorage.setItem(TOKEN_KEY, payload.accessToken)
    localStorage.setItem(USER_KEY, JSON.stringify(user.value))
  }

  function logout() {
    accessToken.value = null
    user.value = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  }

  return {
    accessToken,
    user,
    isAuthenticated,
    setSession,
    logout,
  }
})
