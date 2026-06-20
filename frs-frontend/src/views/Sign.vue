<template>
  <div class="app-layout">
    <AppHeader v-if="userStore.isLoggedIn" />
    <main class="app-main">
      <div class="card" style="max-width: 500px; margin: 0 auto;">
        <h2 class="card-title" style="text-align: center;">刷脸签到</h2>

        <div v-if="!signResult">
          <div class="alert alert-info">
            请正对摄像头，确保面部清晰可见
          </div>

          <div v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</div>

          <FaceCamera @capture="onFaceCapture" />

          <div v-if="submitting" style="text-align: center; margin-top: 16px;">
            <span class="loading-spinner dark"></span>
            <span style="color: var(--text-light); margin-left: 8px;">识别中，请稍候...</span>
          </div>
        </div>

        <!-- Sign Result -->
        <div v-else class="sign-result">
          <div v-if="signResult.success" style="text-align: center;">
            <div style="font-size: 64px; margin-bottom: 16px;">&#9989;</div>
            <h3 style="color: var(--success); margin-bottom: 8px;">签到成功</h3>
            <p style="color: var(--text-secondary); margin-bottom: 4px;">
              用户：{{ signResult.username }}
            </p>
            <p style="color: var(--text-secondary); margin-bottom: 4px;">
              相似度：{{ (signResult.similarity * 100).toFixed(1) }}%
            </p>
            <p style="color: var(--text-light); margin-bottom: 24px;">
              签到时间：{{ formatTime(signResult.signTime) }}
            </p>
            <div style="display: flex; gap: 12px; justify-content: center;">
              <router-link v-if="userStore.isLoggedIn" to="/" class="btn btn-primary">返回首页</router-link>
              <router-link v-else to="/login" class="btn btn-primary">去登录</router-link>
              <button class="btn btn-outline" @click="resetSign">再次签到</button>
            </div>
          </div>
          <div v-else style="text-align: center;">
            <div style="font-size: 64px; margin-bottom: 16px;">&#10060;</div>
            <h3 style="color: var(--danger); margin-bottom: 8px;">签到失败</h3>
            <p style="color: var(--text-secondary); margin-bottom: 24px;">
              {{ signResult.message }}
            </p>
            <div style="display: flex; gap: 12px; justify-content: center;">
              <button class="btn btn-primary" @click="resetSign">重新签到</button>
              <router-link to="/register" class="btn btn-outline">去注册</router-link>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { faceSign } from '@/api/attendance'
import { useUserStore } from '@/stores/user'
import FaceCamera from '@/components/FaceCamera.vue'
import AppHeader from '@/components/AppHeader.vue'

const userStore = useUserStore()

const submitting = ref(false)
const errorMsg = ref('')
const signResult = ref<{ success: boolean; username?: string; similarity?: number; signTime?: string; message?: string } | null>(null)

async function onFaceCapture(base64: string) {
  errorMsg.value = ''
  submitting.value = true
  try {
    const res = await faceSign({
      faceImageBase64: base64,
      deviceType: userStore.getDeviceType()
    })
    const data = res.data.data
    signResult.value = {
      success: true,
      username: data.username,
      similarity: data.similarity,
      signTime: data.signTime
    }
  } catch (e: any) {
    const msg = e.response?.data?.message || e.message || '签到失败'
    signResult.value = {
      success: false,
      message: msg
    }
  } finally {
    submitting.value = false
  }
}

function formatTime(timeStr: string): string {
  if (!timeStr) return ''
  const d = new Date(timeStr)
  return `${d.getFullYear()}-${(d.getMonth()+1).toString().padStart(2,'0')}-${d.getDate().toString().padStart(2,'0')} ${d.getHours().toString().padStart(2,'0')}:${d.getMinutes().toString().padStart(2,'0')}:${d.getSeconds().toString().padStart(2,'0')}`
}

function resetSign() {
  signResult.value = null
  errorMsg.value = ''
}
</script>