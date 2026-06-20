import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userId = ref<number>(Number(localStorage.getItem('userId')) || 0)
  const username = ref<string>(localStorage.getItem('username') || '')

  const isLoggedIn = computed(() => !!token.value)

  function setUser(data: { token: string; userId: number; username: string }) {
    token.value = data.token
    userId.value = data.userId
    username.value = data.username
    localStorage.setItem('token', data.token)
    localStorage.setItem('userId', String(data.userId))
    localStorage.setItem('username', data.username)
  }

  function logout() {
    token.value = ''
    userId.value = 0
    username.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
  }

  function getDeviceType(): string {
    const ua = navigator.userAgent
    if (/Mobile|Android|iPhone|iPad|iPod/i.test(ua)) {
      return 'MOBILE'
    }
    return 'PC'
  }

  return {
    token,
    userId,
    username,
    isLoggedIn,
    setUser,
    logout,
    getDeviceType
  }
})