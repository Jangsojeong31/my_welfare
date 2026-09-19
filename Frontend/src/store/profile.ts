import { defineStore } from 'pinia'
import { ref } from 'vue'

const PROFILE_KEY = 'welfareProfileFilters'

export interface ProfileFilters {
  lifeStages: string[]
  householdTypes: string[]
  interests: string[]
}

function readProfile(): ProfileFilters {
  const raw = localStorage.getItem(PROFILE_KEY)
  if (!raw) {
    return { lifeStages: [], householdTypes: [], interests: [] }
  }
  try {
    const parsed = JSON.parse(raw) as ProfileFilters
    return {
      lifeStages: parsed.lifeStages ?? [],
      householdTypes: parsed.householdTypes ?? [],
      interests: parsed.interests ?? [],
    }
  } catch {
    localStorage.removeItem(PROFILE_KEY)
    return { lifeStages: [], householdTypes: [], interests: [] }
  }
}

export const useProfileStore = defineStore('profile', () => {
  const filters = ref<ProfileFilters>(readProfile())

  function save(next: ProfileFilters) {
    filters.value = {
      lifeStages: [...next.lifeStages],
      householdTypes: [...next.householdTypes],
      interests: [...next.interests],
    }
    localStorage.setItem(PROFILE_KEY, JSON.stringify(filters.value))
  }

  return {
    filters,
    save,
  }
})
