<template>
  <div class="auth-page">
    <div class="auth-card">
      <h1>人脸识别签到</h1>
      <p class="subtitle">登录您的账号</p>

      <div v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</div>

      <form @submit.prevent="handleLogin">
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
            autocomplete="current-password"
            required
          />
        </div>

        <button type="submit" class="btn btn-primary btn-block btn-lg" :disabled="loading">
          <span v-if="loading" class="loading-spinner"></span>
          {{ loading ? '登录中...' : '登 录' }}
        </button>
      </form>

      <div style="text-align: center; margin-top: 20px;">
        <span style="color: var(--text-light); font-size: 14px;">还没有账号？</span>
        <router-link to="/register" style="font-size: 14px;">立即注册</router-link>
      </div>

      <div style="text-align: center; margin-top: 16px;">
        <router-link to="/sign" style="font-size: 14px;">直接刷脸签到</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { login } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const form = reactive({
  username: '',
  password: ''
})

const loading = ref(false)
const errorMsg = ref('')

async function handleLogin() {
  errorMsg.value = ''
  if (!form.username || !form.password) {
    errorMsg.value = '请输入用户名和密码'
    return
  }

  loading.value = true
  try {
    const res = await login(form)
    userStore.setUser(res.data.data)
    const redirect = (route.query.redirect as string) || '/'
    router.push(redirect)
  } catch (e: any) {
    errorMsg.value = e.response?.data?.message || e.message || '登录失败，请检查用户名和密码'
  } finally {
    loading.value = false
  }
}
</script>