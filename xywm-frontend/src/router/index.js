import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'Root',
      component: () => import('@/views/auth/LoginView.vue'),
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/auth/LoginView.vue'),
    },
    {
      path: '/admin',
      component: () => import('@/views/admin/AdminLayout.vue'),
      children: [
        {
          path: 'dashboard',
          name: 'AdminDashboard',
          component: () => import('@/views/admin/Dashboard.vue')
        }
      ]
    },
    {
      path: '/merchant',
      component: () => import('@/views/merchant/MerchantLayout.vue'),
      children: [
        {
          path: 'dashboard',
          name: 'MerchantDashboard',
          component: () => import('@/views/merchant/Dashboard.vue')
        }
      ]
    },
    {
      path: '/user',
      component: () => import('@/views/user/UserLayout.vue'),
      children: [
        {
          path: 'home',
          name: 'UserHome',
          component: () => import('@/views/user/Home.vue')
        }
      ]
    }
  ]
})

router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || 'null')
  const isLoggedIn = !!token
  const userRole = userInfo?.role ?? -1
  const roleHome = { 0: '/admin/dashboard', 1: '/merchant/dashboard', 2: '/user/home' }
  const rolePrefix = { 0: '/admin', 1: '/merchant', 2: '/user' }

  // 访问 / 或 /login
  if (to.name === 'Root' || to.name === 'Login') {
    if (isLoggedIn) return { path: roleHome[userRole] }
    return true
  }

  // 未登录
  if (!isLoggedIn) return { name: 'Login' }

  // 角色不匹配
  if (userRole !== -1 && !to.path.startsWith(rolePrefix[userRole])) {
    return { path: roleHome[userRole] }
  }

  return true
})

export default router