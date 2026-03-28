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
      path: '/admin',
      component: () => import('@/views/admin/AdminLayout.vue'),
      redirect: '/admin/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'AdminDashboard',
          component: () => import('@/views/admin/Dashboard.vue'),
        },
        {
          path: 'merchants',
          name: 'MerchantAudit',
          component: () => import('@/views/admin/MerchantAudit.vue'),
        },
      ]
    },
    {
      path: '/merchant',
      component: () => import('@/views/merchant/MerchantLayout.vue'),
      redirect: '/merchant/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'MerchantDashboard',
          component: () => import('@/views/merchant/Dashboard.vue'),
        },
        {
          path: 'category',
          name: 'MerchantCategory',
          component: () => import('@/views/merchant/CategoryManage.vue'),
        },
        {
          path: 'dishes',
          name: 'MerchantDish',
          component: () => import('@/views/merchant/DishManage.vue'),
        }
      ]
    },
    {
      path: '/user',
      component: () => import('@/views/user/UserLayout.vue'),
      redirect: '/user/home',
      children: [
        {
          path: 'home', // 对应 /user/home
          name: 'UserHome',
          component: () => import('@/views/user/Home.vue'),
        }
        // 以后你写的购物车、订单页面也都加在这里面
      ]
    },
  ]
})

router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || 'null')
  const isLoggedIn = !!token
  const userRole = userInfo?.role ?? -1
  const roleHome = { 0: '/admin/dashboard', 1: '/merchant/dashboard', 2: '/user/home' }
  const rolePrefix = { 0: '/admin', 1: '/merchant', 2: '/user' }

  if (to.path === '/login') {
    if (isLoggedIn && roleHome[userRole]) return roleHome[userRole]
    if (isLoggedIn && !roleHome[userRole]) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
    return true
  }
  if (!isLoggedIn) return '/login'
  if (!roleHome[userRole]) {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    return '/login'
  }
  if (!to.path.startsWith(rolePrefix[userRole])) return roleHome[userRole]
  return true
})

export default router