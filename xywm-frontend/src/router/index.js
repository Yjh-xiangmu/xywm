import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/auth/LoginView.vue'),
    },
    {
      path: '/admin/dashboard',
      name: 'AdminDashboard',
      component: () => import('@/views/admin/AdminLayout.vue'),
    },
    {
      path: '/merchant/dashboard',
      name: 'MerchantDashboard',
      component: () => import('@/views/merchant/MerchantLayout.vue'),
    },
    {
      path: '/user/home',
      name: 'UserHome',
      component: () => import('@/views/user/UserLayout.vue'),
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

  if (to.name === 'Login') {
    if (isLoggedIn) return roleHome[userRole] || '/login'
    return true
  }

  if (!isLoggedIn) return '/login'

  if (userRole !== -1 && !to.path.startsWith(rolePrefix[userRole])) {
    return roleHome[userRole]
  }

  return true
})

export default router