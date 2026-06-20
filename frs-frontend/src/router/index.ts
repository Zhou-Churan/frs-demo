import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false, title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false, title: '注册' }
  },
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { requiresAuth: true, title: '首页' }
  },
  {
    path: '/sign',
    name: 'Sign',
    component: () => import('@/views/Sign.vue'),
    meta: { requiresAuth: false, title: '刷脸签到' }
  },
  {
    path: '/records',
    name: 'Records',
    component: () => import('@/views/Records.vue'),
    meta: { requiresAuth: true, title: '签到记录' }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { requiresAuth: true, title: '个人中心' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  document.title = `${to.meta.title || '人脸识别签到系统'} - FRS`
  const userStore = useUserStore()

  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else if (!to.meta.requiresAuth && userStore.isLoggedIn && (to.name === 'Login' || to.name === 'Register')) {
    next({ name: 'Home' })
  } else {
    next()
  }
})

export default router