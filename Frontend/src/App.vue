<script setup lang="ts">
import { computed } from 'vue'
import { RouterView, useRoute } from 'vue-router'
import AppHeader from '@/components/AppHeader.vue'
import AppToast from '@/components/AppToast.vue'

const route = useRoute()
const showTabs = computed(() => !route.meta.hideTabs && !route.meta.hideChrome)
const showHeader = computed(() => !route.meta.hideChrome)
</script>

<template>
  <div class="min-h-screen bg-canvas">
    <AppHeader v-if="showHeader" :show-tabs="showTabs" />
    <RouterView />
    <AppToast />
  </div>
</template>
