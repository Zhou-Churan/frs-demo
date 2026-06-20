<template>
  <div class="app-layout">
    <AppHeader />
    <main class="app-main">
      <div class="card">
        <h2 class="card-title">签到记录</h2>

        <div v-if="loading" class="page-loading">
          <span class="loading-spinner dark"></span> 加载中...
        </div>

        <template v-else>
          <div v-if="records.length === 0" class="empty-state">
            <div class="empty-icon">&#128203;</div>
            <p>暂无签到记录</p>
          </div>

          <template v-else>
            <div class="table-wrapper">
              <table class="data-table">
                <thead>
                  <tr>
                    <th>日期</th>
                    <th>签到时间</th>
                    <th>相似度</th>
                    <th>设备</th>
                    <th>IP地址</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="record in records" :key="record.id">
                    <td>{{ record.signDate }}</td>
                    <td>{{ formatTime(record.signTime) }}</td>
                    <td>
                      <span
                        class="badge"
                        :class="record.similarity >= 0.95 ? 'badge-success' : 'badge-warning'"
                      >
                        {{ (record.similarity * 100).toFixed(1) }}%
                      </span>
                    </td>
                    <td>
                      <span class="badge badge-info">{{ record.deviceType }}</span>
                    </td>
                    <td>{{ record.ipAddress }}</td>
                  </tr>
                </tbody>
              </table>
            </div>

            <!-- Pagination -->
            <div class="pagination">
              <button :disabled="currentPage <= 1" @click="loadPage(currentPage - 1)">
                上一页
              </button>
              <template v-for="p in displayPages" :key="p">
                <button
                  v-if="p === '...'"
                  disabled
                  style="border: none; background: transparent;"
                >
                  ...
                </button>
                <button
                  v-else
                  :class="{ active: p === currentPage }"
                  @click="loadPage(p as number)"
                >
                  {{ p }}
                </button>
              </template>
              <button :disabled="currentPage >= totalPages" @click="loadPage(currentPage + 1)">
                下一页
              </button>
              <span class="page-info">共 {{ totalRecords }} 条</span>
            </div>
          </template>
        </template>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getAttendanceRecords } from '@/api/attendance'
import type { AttendanceRecord } from '@/api/attendance'
import AppHeader from '@/components/AppHeader.vue'

const records = ref<AttendanceRecord[]>([])
const loading = ref(true)
const currentPage = ref(1)
const totalPages = ref(0)
const totalRecords = ref(0)
const pageSize = 10

const displayPages = computed(() => {
  const pages: (number | string)[] = []
  const total = totalPages.value
  const current = currentPage.value

  if (total <= 7) {
    for (let i = 1; i <= total; i++) pages.push(i)
  } else {
    pages.push(1)
    if (current > 3) pages.push('...')
    const start = Math.max(2, current - 1)
    const end = Math.min(total - 1, current + 1)
    for (let i = start; i <= end; i++) pages.push(i)
    if (current < total - 2) pages.push('...')
    pages.push(total)
  }
  return pages
})

function formatTime(timeStr: string): string {
  if (!timeStr) return ''
  const d = new Date(timeStr)
  return `${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}:${d.getSeconds().toString().padStart(2, '0')}`
}

async function loadPage(page: number) {
  loading.value = true
  try {
    const res = await getAttendanceRecords({ page, size: pageSize })
    const data = res.data.data
    records.value = data.records
    currentPage.value = data.current
    totalPages.value = data.pages
    totalRecords.value = data.total
  } catch (e) {
    console.error('Failed to load records:', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadPage(1)
})
</script>

<style scoped>
.badge-info {
  background: #e6f7ff;
  color: #1890ff;
  border: 1px solid #91d5ff;
}
</style>