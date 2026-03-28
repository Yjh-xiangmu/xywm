<template>
  <div class="layout">
    <aside class="sidebar" :class="{ collapsed }">
      <div class="sidebar-logo">
        <span class="logo-icon">🎓</span>
        <transition name="fade">
          <span v-if="!collapsed" class="logo-text">点餐平台</span>
        </transition>
      </div>

      <el-menu
          :default-active="activeMenu"
          :collapse="collapsed"
          :collapse-transition="false"
          router
          class="sidebar-menu"
      >
        <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <template #title>{{ item.label }}</template>
        </el-menu-item>
      </el-menu>

      <div class="collapse-btn" @click="collapsed = !collapsed">
        <el-icon><ArrowLeft v-if="!collapsed" /><ArrowRight v-else /></el-icon>
      </div>
    </aside>

    <div class="main-wrapper">
      <header class="topbar">
        <div class="topbar-left">
          <span class="page-title">{{ currentTitle }}</span>
        </div>
        <div class="topbar-right">
          <el-badge :value="cartCount" :hidden="cartCount === 0" class="cart-badge">
            <el-button circle @click="router.push('/user/cart')">
              <el-icon><ShoppingCart /></el-icon>
            </el-button>
          </el-badge>
          <div class="user-info">
            <el-avatar :size="34" class="avatar">
              {{ authStore.userName?.charAt(0) }}
            </el-avatar>
            <el-dropdown @command="handleCommand">
              <span class="username">
                {{ authStore.userName }}
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon> 个人信息
                  </el-dropdown-item>
                  <el-dropdown-item command="logout" divided>
                    <el-icon><SwitchButton /></el-icon> 退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </header>

      <main class="content">
        <router-view v-slot="{ Component }">
          <transition name="slide-up" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const collapsed = ref(false)
const cartCount = ref(0)

const menuItems = [
  { path: '/user/home',    icon: 'House',         label: '首页点餐' },
  { path: '/user/orders',  icon: 'List',          label: '我的订单' },
  { path: '/user/cart',    icon: 'ShoppingCart',  label: '购物车' },
  { path: '/user/coupon',  icon: 'Ticket',        label: '我的优惠券' },
  { path: '/user/profile', icon: 'User',          label: '个人信息' },
]

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => {
  return menuItems.find(i => i.path === route.path)?.label || '点餐平台'
})

const handleCommand = async (cmd) => {
  if (cmd === 'logout') {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '退出',
      cancelButtonText: '取消',
      type: 'warning'
    })
    authStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  } else if (cmd === 'profile') {
    router.push('/user/profile')
  }
}
</script>

<style scoped>
.layout {
  display: flex;
  height: 100vh;
  background: var(--bg-light);
  overflow: hidden;
}
.sidebar {
  width: 220px;
  height: 100vh;
  background: white;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 12px rgba(0,0,0,0.06);
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  flex-shrink: 0;
  position: relative;
  z-index: 10;
}
.sidebar.collapsed { width: 64px; }
.sidebar-logo {
  height: 64px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 20px;
  border-bottom: 1px solid var(--border);
  overflow: hidden;
  white-space: nowrap;
}
.logo-icon { font-size: 26px; flex-shrink: 0; }
.logo-text {
  font-size: 17px;
  font-weight: 700;
  background: linear-gradient(135deg, #43e97b, #38f9d7);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
.sidebar-menu {
  flex: 1;
  border: none !important;
  padding: 12px 0;
  overflow-y: auto;
  overflow-x: hidden;
}
:deep(.el-menu-item) {
  height: 48px;
  margin: 2px 8px;
  border-radius: 10px;
  font-size: 14px;
  transition: var(--transition);
}
:deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #f0fff8, #f0fffd) !important;
  color: #38a169 !important;
  font-weight: 500;
}
:deep(.el-menu-item:hover) { background: #f5f6fa !important; }
:deep(.el-menu--collapse) { width: 100% !important; }
.collapse-btn {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-top: 1px solid var(--border);
  color: var(--text-secondary);
  transition: var(--transition);
}
.collapse-btn:hover { color: #38a169; background: #f5f6fa; }
.main-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.topbar {
  height: 64px;
  background: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  flex-shrink: 0;
}
.page-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--text-primary);
}
.topbar-right {
  display: flex;
  align-items: center;
  gap: 16px;
}
.cart-badge { cursor: pointer; }
.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}
.avatar {
  background: linear-gradient(135deg, #43e97b, #38f9d7);
  color: white;
  font-weight: 600;
  flex-shrink: 0;
}
.username {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: var(--text-primary);
  cursor: pointer;
  font-weight: 500;
}
.content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}
</style>