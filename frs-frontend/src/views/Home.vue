<template>
  <div class="app-layout">
    <AppHeader />
    <main class="app-main">
      <div v-if="loading" class="page-loading">
        <span class="loading-spinner dark"></span> 加载中...
      </div>

      <template v-else>
        <!-- Sign Status -->
        <div class="card sign-status-card">
          <div class="status-icon">
            <span v-if="todayStatus?.signed">&#9989;</span>
            <span v-else>&#128197;</span>
          </div>
          <div class="status-text" :style="{ color: todayStatus?.signed ? 'var(--success)' : 'var(--text)' }">
            {{ todayStatus?.signed ? '今日已签到' : '今日尚未签到' }}
          </div>
          <div v-if="todayStatus?.signed && todayStatus.signTime" class="status-time">
            签到时间：{{ formatTime(todayStatus.signTime) }}
          </div>
          <div class="status-time" style="margin-top: 4px;">
            {{ todayStatus?.date }}
          </div>
        </div>

        <!-- Quick Actions -->
        <div class="quick-actions">
          <router-link v-if="!todayStatus?.signed" to="/sign" class="btn btn-primary btn-lg" style="flex:1;">
            刷脸签到
          </router-link>
          <button v-else class="btn btn-success btn-lg" style="flex:1;" disabled>
            今日已签到
          </button>
          <router-link to="/records" class="btn btn-outline btn-lg" style="flex:1;">
            签到记录
          </router-link>
          <router-link to="/profile" class="btn btn-outline btn-lg" style="flex:1;">
            个人中心
          </router-link>
        </div>

        <!-- Stats -->
        <div class="dashboard-grid">
          <div class="card stat-card">
            <div class="stat-icon">&#128100;</div>
            <div class="stat-value">{{ userStore.username }}</div>
            <div class="stat-label">当前用户</div>
          </div>
          <div class="card stat-card">
            <div class="stat-icon">&#128247;</div>
            <div class="stat-value">{{ todayStatus?.signed ? '已录入' : '--' }}</div>
            <div class="stat-label">人脸状态</div>
          </div>
        </div>
      </template>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getTodayStatus } from '@/api/attendance'
import type { TodayStatus } from '@/api/attendance'
import { useUserStore } from '@/stores/user'
import AppHeader from '@/components/AppHeader.vue'

const userStore = useUserStore()
const todayStatus = ref<TodayStatus | null>(null)
const loading = ref(true)

function formatTime(timeStr: string): string {
  if (!timeStr) return ''
  const d = new Date(timeStr)
  return `${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}:${d.getSeconds().toString().padStart(2, '0')}`
}

onMounted(async () => {
  try {
    const res = await getTodayStatus()
    todayStatus.value = res.data.data
  } catch (e) {
    console.error('Failed to load today status:', e)
  } finally {
    loading.value = false
  }
})
</script>