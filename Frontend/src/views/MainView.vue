<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { searchWelfareByQuestion } from '@/api/ai'
import AiRecommendationCard from '@/components/AiRecommendationCard.vue'
import SearchBox from '@/components/SearchBox.vue'
import { useToastStore } from '@/store/toast'
import type { WelfareAiResponse } from '@/types/ai'
import { renderSimpleMarkdown, rewriteWelfareDetailLinks } from '@/utils/markdown'

const route = useRoute()
const router = useRouter()
const toast = useToastStore()

const keyword = ref('')
const loading = ref(false)
const searched = ref(false)
const result = ref<WelfareAiResponse | null>(null)

const exampleQuestions = [
  '청년 주거 지원',
  '육아 중인 맞벌이',
  '저소득 생계 지원',
  '퇴직 후 재취업',
]

const conditionChips = computed(() => {
  const condition = result.value?.condition
  if (!condition) {
    return []
  }

  const chips: { label: string; value: string }[] = []
  if (condition.age != null) {
    chips.push({ label: '나이', value: `${condition.age}세` })
  }
  if (condition.region) {
    chips.push({ label: '지역', value: condition.region })
  }
  if (condition.employment) {
    chips.push({ label: '취업', value: condition.employment })
  }
  if (condition.housing) {
    chips.push({ label: '주거', value: condition.housing })
  }
  if (condition.income) {
    chips.push({ label: '소득', value: condition.income })
  }
  return chips
})

const keywords = computed(() => result.value?.condition?.keywords?.filter(Boolean) ?? [])
const recommendations = computed(() => result.value?.recommendations ?? [])
const answerHtml = computed(() => {
  const answer = result.value?.answer?.trim()
  if (!answer) {
    return ''
  }
  return renderSimpleMarkdown(rewriteWelfareDetailLinks(answer, recommendations.value))
})
const hasCondition = computed(() => conditionChips.value.length > 0 || keywords.value.length > 0)

async function searchWith(value: string) {
  const trimmed = value.trim()
  if (!trimmed) {
    toast.show('현재 상황을 입력해 주세요.', 'info')
    return
  }

  keyword.value = trimmed
  loading.value = true
  searched.value = true
  result.value = null

  if (route.query.q !== trimmed) {
    await router.replace({ query: { q: trimmed } })
  }

  try {
    result.value = await searchWelfareByQuestion(trimmed)
  } catch {
    result.value = null
  } finally {
    loading.value = false
  }
}

function onSubmit() {
  void searchWith(keyword.value)
}

function onExampleSearch(value: string) {
  keyword.value = value
  void searchWith(value)
}

function onAnswerClick(event: MouseEvent) {
  const anchor = (event.target as HTMLElement | null)?.closest('a')
  if (!anchor) {
    return
  }
  const href = anchor.getAttribute('href')
  if (!href?.startsWith('/welfare/')) {
    return
  }
  event.preventDefault()
  void router.push(href)
}

onMounted(() => {
  const query = route.query.q
  if (typeof query === 'string' && query.trim()) {
    void searchWith(query)
  }
})
</script>

<template>
  <section
    class="mx-auto flex max-w-3xl flex-col px-4"
    :class="searched ? 'items-stretch py-10' : 'min-h-[calc(100vh-8rem)] items-center justify-center py-16'"
  >
    <h1
      class="mb-3 text-3xl font-semibold tracking-tight text-ink sm:text-4xl"
      :class="{ 'text-center': !searched }"
    >
      상황에 맞는 복지를 찾아드립니다
    </h1>
    <p
      class="text-sm leading-6 text-slate-500"
      :class="searched ? 'mb-6' : 'mb-10 text-center'"
    >
      나이, 직업, 주거·소득 등 현재 상황을 자유롭게 적어 주세요.
      AI가 조건을 파악하고 관련 복지 서비스를 추천합니다.
    </p>

    <SearchBox
      v-model="keyword"
      placeholder="예: 20대 혼자 거주하는 청년에게 맞는 복지를 알려줘"
      :loading="loading"
      @submit="onSubmit"
    />

    <div class="mt-6 flex flex-wrap gap-2" :class="{ 'justify-center': !searched }">
      <button
        v-for="item in exampleQuestions"
        :key="item"
        type="button"
        class="rounded-full border border-slate-200 bg-white px-3 py-1.5 text-xs text-slate-600 hover:border-primary hover:text-primary disabled:opacity-60"
        :disabled="loading"
        @click="onExampleSearch(item)"
      >
        {{ item }}
      </button>
    </div>

    <div v-if="searched" class="mt-10 space-y-6">
      <p
        v-if="loading"
        class="rounded-2xl border border-dashed border-slate-200 bg-white py-16 text-center text-sm text-slate-400"
      >
        질문을 분석하고 복지 서비스를 찾고 있습니다...
      </p>

      <template v-else-if="result">
        <section v-if="hasCondition">
          <h2 class="mb-3 text-sm font-semibold text-ink">파악된 조건</h2>
          <div class="flex flex-wrap gap-2">
            <span
              v-for="chip in conditionChips"
              :key="chip.label"
              class="rounded-full bg-primary-soft px-3 py-1 text-xs text-primary-dark"
            >
              {{ chip.label }} {{ chip.value }}
            </span>
            <span
              v-for="word in keywords"
              :key="word"
              class="rounded-full bg-slate-100 px-3 py-1 text-xs text-slate-600"
            >
              {{ word }}
            </span>
          </div>
        </section>

        <section
          v-if="answerHtml"
          class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm"
        >
          <h2 class="mb-3 text-sm font-semibold text-ink">AI 추천 설명</h2>
          <div
            class="ai-answer text-sm leading-7 text-slate-600 [&_a]:font-medium [&_a]:text-primary [&_a]:underline [&_p]:mb-3 [&_p:last-child]:mb-0 [&_strong]:font-semibold [&_strong]:text-ink"
            v-html="answerHtml"
            @click="onAnswerClick"
          />
        </section>

        <section>
          <h2 class="mb-3 text-sm font-semibold text-ink">
            관련 복지 서비스
            <span v-if="recommendations.length" class="ml-1 font-normal text-slate-400">
              {{ recommendations.length }}건
            </span>
          </h2>
          <div v-if="recommendations.length" class="space-y-3">
            <AiRecommendationCard
              v-for="item in recommendations"
              :key="item.id || item.servId"
              :item="item"
            />
          </div>
          <p
            v-else
            class="rounded-2xl border border-dashed border-slate-200 bg-white py-12 text-center text-sm text-slate-400"
          >
            조건에 맞는 복지 서비스를 찾지 못했습니다. 상황을 조금 더 구체적으로 적어 보세요.
          </p>
        </section>
      </template>

      <p
        v-else
        class="rounded-2xl border border-dashed border-slate-200 bg-white py-16 text-center text-sm text-slate-400"
      >
        검색 결과를 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.
      </p>
    </div>
  </section>
</template>
