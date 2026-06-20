<template>
  <div class="auth-page">
    <div class="auth-card" style="max-width: 480px;">
      <h1>注册账号</h1>
      <p class="subtitle">创建您的签到账号</p>

      <div v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</div>
      <div v-if="successMsg" class="alert alert-success">{{ successMsg }}</div>

      <!-- Step 1: Basic Info -->
      <div v-if="step === 1">
        <div class="form-group">
          <label>用户名</label>
          <input
            v-model="form.username"
            type="text"
            placeholder="请输入用户名"
            autocomplete="username"
            required
          />
        </div>

        <div class="form-group">
          <label>密码</label>
          <input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            autocomplete="new-password"
            required
          />
        </div>

        <div class="form-group">
          <label>手机号（选填）</label>
          <input
            v-model="form.phone"
            type="tel"
            placeholder="请输入手机号"
            autocomplete="tel"
          />
        </div>

        <button class="btn btn-primary btn-block btn-lg" @click="nextStep">
          下一步：录入人脸
        </button>
      </div>

      <!-- Step 2: Face Capture -->
      <div v-if="step === 2">
        <div class="alert alert-info">
          请正对摄像头，确保光线充足，面部无遮挡
        </div>
        <FaceCamera @capture="onFaceCapture" />
      </div>

      <!-- Step 3: Confirm & Submit -->
      <div v-if="step === 3">
        <div class="alert alert-info">
          请确认人脸照片并完成注册
        </div>
        <div class="preview-container" style="margin-bottom: 16px;">
          <img :src="'data:image/jpeg;base64,' + faceImageBase64" alt="face preview" />
        </div>
        <div style="display: flex; gap: 12px;">
          <button class="btn btn-outline" style="flex:1;" @click="step = 2">重新拍照</button>
          <button class="btn btn-primary" style="flex:1;" :disabled="loading" @click="handleRegister">
            <span v-if="loading" class="loading-spinner"></span>
            {{ loading ? '注册中...' : '完成注册' }}
          </button>
        </div>
      </div>

      <div style="text-align: center; margin-top: 16px;">
        <router-link to="/login" style="font-size: 14px;">已有账号？返回登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/auth'
import { useUserStore } from '@/stores/user'
import FaceCamera from '@/components/FaceCamera.vue'

const router = useRouter()
const userStore = useUserStore()

const step = ref(1)
const form = reactive({
  username: '',
  password: '',
  phone: ''
})
const faceImageBase64 = ref('')
const loading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

function nextStep() {
  errorMsg.value = ''
  if (!form.username || !form.password) {
    errorMsg.value = '请输入用户名和密码'
    return
  }
  if (form.password.length < 6) {
    errorMsg.value = '密码长度不能少于6位'
    return
  }
  step.value = 2
}

function onFaceCapture(base64: string) {
  faceImageBase64.value = base64
  step.value = 3
}

async function handleRegister() {
  errorMsg.value = ''
  loading.value = true
  try {
    const res = await register({
      username: form.username,
      password: form.password,
      phone: form.phone || undefined,
      faceImageBase64: faceImageBase64.value
    })
    userStore.setUser(res.data.data)
    successMsg.value = '注册成功！正在跳转...'
    setTimeout(() => {
      router.push('/')
    }, 1000)
  } catch (e: any) {
    errorMsg.value = e.response?.data?.message || e.message || '注册失败'
    step.value = 2
  } finally {
    loading.value = false
  }
}
</script>