<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink, useRoute } from 'vue-router'

const route = useRoute()

const tabs: { name: string; to: string; activeWhen: string[] }[] = [
  { name: '복지 검색', to: '/main', activeWhen: ['main'] },
  { name: '복지 목록', to: '/welfare', activeWhen: ['welfare-list'] },
  { name: '프로필 맞춤 복지', to: '/personalized', activeWhen: ['personalized'] },
]

const activeName = computed(() => String(route.name ?? ''))

function isActive(tab: { activeWhen: string[] }): boolean {
  return tab.activeWhen.includes(activeName.value)
}
</script>

<template>
  <nav class="border-t border-slate-100 bg-white">
    <div class="mx-auto flex max-w-6xl justify-center gap-8 px-4">
      <RouterLink
        v-for="tab in tabs"
        :key="tab.to"
        :to="tab.to"
        class="relative py-3 text-sm tracking-wide"
        :class="isActive(tab) ? 'font-semibold text-primary' : 'text-slate-500 hover:text-primary'"
      >
        {{ tab.name }}
        <span
          v-if="isActive(tab)"
          class="absolute inset-x-0 -bottom-px h-0.5 rounded-full bg-primary"
        />
      </RouterLink>
    </div>
  </nav>
</template>
