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
          component: () => import('@/views/admin/AdminStats.vue'),
        },
        {
          path: 'merchants',
          name: 'MerchantAudit',
          component: () => import('@/views/admin/MerchantAudit.vue'),
        },
        {
          path: 'users',
          name: 'AdminUsers',
          component: () => import('@/views/admin/AdminUsers.vue'),
        },
        {
          path: 'category',
          name: 'AdminCategory',
          component: () => import('@/views/admin/AdminCategory.vue'),
        },
        {
          path: 'notice',
          name: 'AdminNotice',
          component: () => import('@/views/admin/AdminNotice.vue'),
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
          path: 'orders',
          name: 'MerchantOrders',
          component: () => import('@/views/merchant/MerchantOrders.vue'),
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
        },
        {
          path: 'shop',
          name: 'ShopSetting',
          component: () => import('@/views/merchant/ShopSetting.vue'),
        },
        {
          path: 'coupon',
          name: 'CouponManage',
          component: () => import('@/views/merchant/CouponManage.vue'),
        },
      ]
    },
    {
      path: '/user',
      component: () => import('@/views/user/UserLayout.vue'),
      redirect: '/user/home',
      children: [
        {
          path: 'home',
          name: 'UserHome',
          component: () => import('@/views/user/Home.vue'),
        },
        {
          path: 'shop/:merchantId',
          name: 'ShopDetail',
          component: () => import('@/views/user/ShopDetail.vue'),
        },
        {
          path: 'cart',
          name: 'UserCart',
          component: () => import('@/views/user/Cart.vue'),
        },
        {
          path: 'address',
          name: 'AddressManage',
          component: () => import('@/views/user/AddressManage.vue'),
        },
        {
          path: 'checkout',
          name: 'Checkout',
          component: () => import('@/views/user/Checkout.vue'),
        },
        {
          path: 'orders',
          name: 'UserOrders',
          component: () => import('@/views/user/Orders.vue'),
        },
        {
          path: 'profile',
          name: 'UserProfile',
          component: () => import('@/views/user/Profile.vue'),
        },
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