<template>
  <div class="app-layout">
    <AppHeader />
    <main class="app-main">
      <div style="max-width: 600px; margin: 0 auto;">
        <!-- Profile Header -->
        <div class="card profile-header">
          <div class="profile-avatar">
            {{ userStore.username.charAt(0).toUpperCase() }}
          </div>
          <div class="profile-info">
            <h2>{{ userStore.username }}</h2>
            <p>ID: {{ userStore.userId }}</p>
          </div>
        </div>

        <!-- Face Info -->
        <div class="card" style="margin-top: 16px;">
          <h3 class="card-title">人脸信息</h3>

          <div v-if="faceLoading" class="page-loading">
            <span class="loading-spinner dark"></span>
          </div>

          <template v-else>
            <div class="face-status">
              <span style="font-size: 24px;">{{ faceInfo?.hasFace ? '&#128247;' : '&#10060;' }}</span>
              <div>
                <div style="font-weight: 500;">{{ faceInfo?.hasFace ? '已录入人脸' : '未录入人脸' }}</div>
                <div v-if="faceInfo?.hasFace" style="font-size: 13px; color: var(--text-light);">
                  人脸ID: {{ faceInfo?.faceId?.substring(0, 12) }}...
                </div>
              </div>
            </div>

            <div style="margin-top: 16px;">
              <button
                class="btn btn-primary"
                @click="showUpdateFace = true"
              >
                {{ faceInfo?.hasFace ? '更新人脸照片' : '录入人脸' }}
              </button>
            </div>
          </template>
        </div>

        <!-- Update Face Modal -->
        <div v-if="showUpdateFace" class="card" style="margin-top: 16px;">
          <h3 class="card-title">{{ faceInfo?.hasFace ? '更新人脸照片' : '录入人脸' }}</h3>

          <div v-if="updateMsg" :class="['alert', updateSuccess ? 'alert-success' : 'alert-error']">
            {{ updateMsg }}
          </div>

          <FaceCamera @capture="onUpdateFace" />

          <div v-if="updating" style="text-align: center; margin-top: 12px;">
            <span class="loading-spinner dark"></span>
            <span style="color: var(--text-light); margin-left: 8px;">更新中...</span>
          </div>

          <button class="btn btn-outline" style="margin-top: 12px;" @click="showUpdateFace = false">
            取消
          </button>
        </div>

        <!-- Actions -->
        <div class="card" style="margin-top: 16px;">
          <h3 class="card-title">账号操作</h3>
          <button class="btn btn-danger" @click="handleLogout">退出登录</button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getFaceInfo, updateFace } from '@/api/user'
import type { FaceInfo } from '@/api/user'
import { useUserStore } from '@/stores/user'
import FaceCamera from '@/components/FaceCamera.vue'
import AppHeader from '@/components/AppHeader.vue'

const router = useRouter()
const userStore = useUserStore()

const faceInfo = ref<FaceInfo | null>(null)
const faceLoading = ref(true)
const showUpdateFace = ref(false)
const updating = ref(false)
const updateMsg = ref('')
const updateSuccess = ref(false)

async function loadFaceInfo() {
  faceLoading.value = true
  try {
    const res = await getFaceInfo()
    faceInfo.value = res.data.data
  } catch (e) {
    console.error('Failed to load face info:', e)
  } finally {
    faceLoading.value = false
  }
}

async function onUpdateFace(base64: string) {
  updating.value = true
  updateMsg.value = ''
  try {
    await updateFace({ faceImageBase64: base64 })
    updateSuccess.value = true
    updateMsg.value = '人脸照片更新成功'
    await loadFaceInfo()
    setTimeout(() => {
      showUpdateFace.value = false
      updateMsg.value = ''
    }, 1500)
  } catch (e: any) {
    updateSuccess.value = false
    updateMsg.value = e.response?.data?.message || e.message || '更新失败'
  } finally {
    updating.value = false
  }
}

function handleLogout() {
  userStore.logout()
  router.push('/login')
}

onMounted(() => {
  loadFaceInfo()
})
</script>