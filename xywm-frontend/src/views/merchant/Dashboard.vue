<template>
  <div class="dashboard">
    <h2>欢迎回来，{{ authStore.userName }} 👋</h2>
    <p>商家管理中心</p>
  </div>
</template>
<script setup>
import { useAuthStore } from '@/stores/auth'
const authStore = useAuthStore()
</script>
<style scoped>
.dashboard { padding: 20px; }
h2 { font-size: 24px; font-weight: 600; color: var(--text-primary); margin-bottom: 8px; }
p { color: var(--text-secondary); }
</style>