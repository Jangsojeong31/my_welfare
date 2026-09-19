<script setup lang="ts">
import { RouterLink } from 'vue-router'
</script>

<template>
  <section class="mx-auto flex min-h-screen max-w-lg flex-col items-center justify-center px-4 text-center">
    <p class="mb-3 text-sm tracking-wide text-primary">404</p>
    <h1 class="mb-3 text-3xl font-semibold text-ink">페이지를 찾을 수 없습니다</h1>
    <p class="mb-8 text-sm leading-6 text-slate-500">
      요청하신 주소가 변경되었거나 존재하지 않습니다.
    </p>
    <RouterLink
      to="/main"
      class="rounded-xl bg-primary px-5 py-2.5 text-sm font-medium text-white hover:bg-primary-dark"
    >
      메인으로 돌아가기
    </RouterLink>
  </section>
</template>
