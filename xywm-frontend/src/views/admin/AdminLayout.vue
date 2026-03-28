<template>
  <div class="layout">
    <aside class="sidebar" :class="{ collapsed }">
      <div class="sidebar-logo">
        <span class="logo-icon">🍜</span>
        <transition name="fade">
          <span v-if="!collapsed" class="logo-text">校园外卖</span>
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
                  <el-dropdown-item command="logout">
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
          <transition name="fade" mode="out-in">
            <component :is="Component" v-if="Component" />
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
// 删除了 import Dashboard 的引入

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const collapsed = ref(false)

const menuItems = [
  { path: '/admin/dashboard', icon: 'DataBoard', label: '数据概览' },
  { path: '/admin/users',     icon: 'User',      label: '用户管理' },
  // 注意这里的路径是 /admin/merchants
  { path: '/admin/merchants', icon: 'Shop',      label: '商家管理' },
  { path: '/admin/category',  icon: 'Grid',      label: '分类管理' },
  { path: '/admin/orders',    icon: 'List',      label: '订单管理' },
  { path: '/admin/notice',    icon: 'Bell',      label: '公告管理' },
]

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => {
  return menuItems.find(i => i.path === route.path)?.label || '管理后台'
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
  }
}
</script>

<style scoped>
/* 你的样式完全保留，没有任何修改 */
.layout { display: flex; height: 100vh; background: var(--bg-light); overflow: hidden; }
.sidebar { width: 220px; height: 100vh; background: white; display: flex; flex-direction: column; box-shadow: 2px 0 12px rgba(0,0,0,0.06); transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1); flex-shrink: 0; position: relative; z-index: 10; }
.sidebar.collapsed { width: 64px; }
.sidebar-logo { height: 64px; display: flex; align-items: center; gap: 10px; padding: 0 20px; border-bottom: 1px solid var(--border); overflow: hidden; white-space: nowrap; }
.logo-icon { font-size: 26px; flex-shrink: 0; }
.logo-text { font-size: 17px; font-weight: 700; background: linear-gradient(135deg, #667eea, #764ba2); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }
.sidebar-menu { flex: 1; border: none !important; padding: 12px 0; overflow-y: auto; overflow-x: hidden; }
:deep(.el-menu-item) { height: 48px; margin: 2px 8px; border-radius: 10px; font-size: 14px; transition: var(--transition); }
:deep(.el-menu-item.is-active) { background: linear-gradient(135deg, #eef0ff, #f5f0ff) !important; color: var(--primary) !important; font-weight: 500; }
:deep(.el-menu-item:hover) { background: #f5f6fa !important; }
:deep(.el-menu--collapse) { width: 100% !important; }
.collapse-btn { height: 48px; display: flex; align-items: center; justify-content: center; cursor: pointer; border-top: 1px solid var(--border); color: var(--text-secondary); transition: var(--transition); }
.collapse-btn:hover { color: var(--primary); background: #f5f6fa; }
.main-wrapper { flex: 1; display: flex; flex-direction: column; overflow: hidden; }
.topbar { height: 64px; background: white; display: flex; align-items: center; justify-content: space-between; padding: 0 24px; box-shadow: 0 2px 8px rgba(0,0,0,0.04); flex-shrink: 0; }
.page-title { font-size: 17px; font-weight: 600; color: var(--text-primary); }
.user-info { display: flex; align-items: center; gap: 10px; }
.avatar { background: linear-gradient(135deg, #667eea, #764ba2); color: white; font-weight: 600; flex-shrink: 0; }
.username { display: flex; align-items: center; gap: 4px; font-size: 14px; color: var(--text-primary); cursor: pointer; font-weight: 500; }
.content { flex: 1; padding: 24px; overflow-y: auto; }
/* 页面切换动画 */
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>