<template>
  <div class="login-page">
    <!-- 背景动画 -->
    <div class="bg-animation">
      <div class="bubble" v-for="i in 8" :key="i"></div>
    </div>

    <div class="login-container">
      <!-- 左侧介绍区 -->
      <div class="login-left">
        <div class="brand">
          <div class="brand-icon">🍜</div>
          <h1>校园外卖</h1>
          <p>美食就在身边，快速送达宿舍</p>
        </div>
        <div class="features">
          <div class="feature-item" v-for="item in features" :key="item.text">
            <span class="feature-icon">{{ item.icon }}</span>
            <span>{{ item.text }}</span>
          </div>
        </div>
      </div>

      <!-- 右侧表单区 -->
      <div class="login-right">
        <div class="form-card">
          <!-- Tab切换 -->
          <div class="tab-bar">
            <div
                class="tab-item"
                :class="{ active: activeTab === 'login' }"
                @click="activeTab = 'login'"
            >登录</div>
            <div
                class="tab-item"
                :class="{ active: activeTab === 'register' }"
                @click="activeTab = 'register'"
            >注册</div>
            <div class="tab-indicator" :style="{ left: activeTab === 'login' ? '0%' : '50%' }"></div>
          </div>

          <!-- 登录表单 -->
          <transition name="slide-up" mode="out-in">
            <div v-if="activeTab === 'login'" key="login" class="form-body">
              <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef">
                <el-form-item prop="username">
                  <div class="input-wrapper">
                    <el-icon class="input-icon"><User /></el-icon>
                    <el-input
                        v-model="loginForm.username"
                        placeholder="请输入用户名"
                        size="large"
                    />
                  </div>
                </el-form-item>
                <el-form-item prop="password">
                  <div class="input-wrapper">
                    <el-icon class="input-icon"><Lock /></el-icon>
                    <el-input
                        v-model="loginForm.password"
                        type="password"
                        placeholder="请输入密码"
                        size="large"
                        show-password
                        @keyup.enter="handleLogin"
                    />
                  </div>
                </el-form-item>

                <!-- 角色选择 -->
                <el-form-item prop="role">
                  <div class="role-select">
                    <div
                        class="role-item"
                        v-for="role in roles"
                        :key="role.value"
                        :class="{ active: loginForm.role === role.value }"
                        @click="loginForm.role = role.value"
                    >
                      <span class="role-emoji">{{ role.icon }}</span>
                      <span class="role-name">{{ role.label }}</span>
                    </div>
                  </div>
                </el-form-item>

                <el-button
                    class="submit-btn"
                    type="primary"
                    size="large"
                    :loading="loading"
                    @click="handleLogin"
                >
                  {{ loading ? '登录中...' : '登 录' }}
                </el-button>
              </el-form>
            </div>

            <!-- 注册表单 -->
            <div v-else key="register" class="form-body">
              <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef">
                <el-form-item prop="username">
                  <div class="input-wrapper">
                    <el-icon class="input-icon"><User /></el-icon>
                    <el-input
                        v-model="registerForm.username"
                        placeholder="请输入用户名"
                        size="large"
                    />
                  </div>
                </el-form-item>
                <el-form-item prop="nickname">
                  <div class="input-wrapper">
                    <el-icon class="input-icon"><EditPen /></el-icon>
                    <el-input
                        v-model="registerForm.nickname"
                        placeholder="请输入昵称"
                        size="large"
                    />
                  </div>
                </el-form-item>
                <el-form-item prop="phone">
                  <div class="input-wrapper">
                    <el-icon class="input-icon"><Phone /></el-icon>
                    <el-input
                        v-model="registerForm.phone"
                        placeholder="请输入手机号"
                        size="large"
                    />
                  </div>
                </el-form-item>
                <el-form-item prop="password">
                  <div class="input-wrapper">
                    <el-icon class="input-icon"><Lock /></el-icon>
                    <el-input
                        v-model="registerForm.password"
                        type="password"
                        placeholder="请输入密码"
                        size="large"
                        show-password
                    />
                  </div>
                </el-form-item>
                <el-form-item prop="confirmPassword">
                  <div class="input-wrapper">
                    <el-icon class="input-icon"><Lock /></el-icon>
                    <el-input
                        v-model="registerForm.confirmPassword"
                        type="password"
                        placeholder="请确认密码"
                        size="large"
                        show-password
                        @keyup.enter="handleRegister"
                    />
                  </div>
                </el-form-item>

                <!-- 注册角色选择 -->
                <el-form-item prop="role">
                  <div class="role-select">
                    <div
                        class="role-item"
                        v-for="role in registerRoles"
                        :key="role.value"
                        :class="{ active: registerForm.role === role.value }"
                        @click="registerForm.role = role.value"
                    >
                      <span class="role-emoji">{{ role.icon }}</span>
                      <span class="role-name">{{ role.label }}</span>
                    </div>
                  </div>
                </el-form-item>

                <el-button
                    class="submit-btn"
                    type="primary"
                    size="large"
                    :loading="loading"
                    @click="handleRegister"
                >
                  {{ loading ? '注册中...' : '注 册' }}
                </el-button>
              </el-form>
            </div>
          </transition>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { loginApi, registerApi } from '@/api/auth'

const router = useRouter()
const authStore = useAuthStore()

const activeTab = ref('login')
const loading = ref(false)
const loginFormRef = ref()
const registerFormRef = ref()

const features = [
  { icon: '🚀', text: '30分钟极速送达' },
  { icon: '🍱', text: '多家校内商家入驻' },
  { icon: '💰', text: '学生专属优惠折扣' },
  { icon: '⭐', text: '真实评价放心点餐' },
]

const roles = [
  { value: 0, icon: '🛠️', label: '管理员' },
  { value: 1, icon: '🍜', label: '商家' },
  { value: 2, icon: '🎓', label: '学生' },
]

const registerRoles = [
  { value: 1, icon: '🍜', label: '商家' },
  { value: 2, icon: '🎓', label: '学生' },
]

const loginForm = reactive({ username: '', password: '', role: 2 })
const registerForm = reactive({
  username: '', nickname: '', phone: '',
  password: '', confirmPassword: '', role: 2
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const registerRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次密码不一致'))
        } else {
          callback()
        }
      }, trigger: 'blur'
    }
  ],
}

const handleLogin = async () => {
  const valid = await loginFormRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await loginApi(loginForm)
    authStore.setAuth(res.data.token, res.data.userInfo)
    ElMessage.success('登录成功，欢迎回来！')
    const roleMap = { 0: '/admin', 1: '/merchant', 2: '/user' }
    router.push(roleMap[res.data.userInfo.role])
  } catch (e) {
    // 错误已在拦截器处理
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  const valid = await registerFormRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await registerApi(registerForm)
    ElMessage.success('注册成功，请登录')
    activeTab.value = 'login'
    loginForm.username = registerForm.username
  } catch (e) {
    // 错误已在拦截器处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  position: relative;
}

/* 背景气泡动画 */
.bg-animation {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
}
.bubble {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  animation: float linear infinite;
}
.bubble:nth-child(1) { width: 80px; height: 80px; left: 10%; animation-duration: 8s; animation-delay: 0s; }
.bubble:nth-child(2) { width: 120px; height: 120px; left: 25%; animation-duration: 12s; animation-delay: 2s; }
.bubble:nth-child(3) { width: 60px; height: 60px; left: 40%; animation-duration: 9s; animation-delay: 1s; }
.bubble:nth-child(4) { width: 100px; height: 100px; left: 55%; animation-duration: 11s; animation-delay: 3s; }
.bubble:nth-child(5) { width: 70px; height: 70px; left: 70%; animation-duration: 7s; animation-delay: 0.5s; }
.bubble:nth-child(6) { width: 140px; height: 140px; left: 80%; animation-duration: 13s; animation-delay: 4s; }
.bubble:nth-child(7) { width: 50px; height: 50px; left: 88%; animation-duration: 10s; animation-delay: 1.5s; }
.bubble:nth-child(8) { width: 90px; height: 90px; left: 5%; animation-duration: 14s; animation-delay: 2.5s; }

@keyframes float {
  0% { transform: translateY(110vh) scale(0); opacity: 0; }
  10% { opacity: 1; }
  90% { opacity: 1; }
  100% { transform: translateY(-10vh) scale(1); opacity: 0; }
}

/* 主容器 */
.login-container {
  display: flex;
  width: 900px;
  min-height: 560px;
  background: white;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 24px 80px rgba(0, 0, 0, 0.25);
  position: relative;
  z-index: 1;
}

/* 左侧 */
.login-left {
  flex: 1;
  background: linear-gradient(160deg, #667eea 0%, #764ba2 100%);
  padding: 48px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: white;
}
.brand-icon {
  font-size: 56px;
  margin-bottom: 16px;
}
.brand h1 {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 10px;
}
.brand p {
  font-size: 15px;
  opacity: 0.85;
  margin-bottom: 40px;
}
.features {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  opacity: 0.9;
}
.feature-icon {
  font-size: 20px;
  width: 36px;
  height: 36px;
  background: rgba(255,255,255,0.15);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 右侧 */
.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: #fff;
}
.form-card {
  width: 100%;
  max-width: 360px;
}

/* Tab */
.tab-bar {
  display: flex;
  position: relative;
  background: #f5f6fa;
  border-radius: 10px;
  padding: 4px;
  margin-bottom: 28px;
}
.tab-item {
  flex: 1;
  text-align: center;
  padding: 9px 0;
  font-size: 15px;
  font-weight: 500;
  color: var(--text-secondary);
  cursor: pointer;
  position: relative;
  z-index: 1;
  transition: color 0.3s;
}
.tab-item.active {
  color: var(--primary);
}
.tab-indicator {
  position: absolute;
  top: 4px;
  width: 50%;
  height: calc(100% - 8px);
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  transition: left 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 输入框 */
.input-wrapper {
  position: relative;
  width: 100%;
}
.input-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-secondary);
  z-index: 1;
  font-size: 16px;
}
.input-wrapper :deep(.el-input__wrapper) {
  padding-left: 36px;
  border-radius: 10px;
  box-shadow: 0 0 0 1px var(--border);
  transition: var(--transition);
}
.input-wrapper :deep(.el-input__wrapper:hover),
.input-wrapper :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px var(--primary) !important;
}

/* 角色选择 */
.role-select {
  display: flex;
  gap: 10px;
  width: 100%;
}
.role-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 10px 6px;
  border: 2px solid var(--border);
  border-radius: 10px;
  cursor: pointer;
  transition: var(--transition);
  background: #fafafa;
}
.role-item:hover {
  border-color: var(--primary);
  background: #f0f1ff;
}
.role-item.active {
  border-color: var(--primary);
  background: linear-gradient(135deg, #eef0ff, #f5f0ff);
}
.role-emoji { font-size: 20px; }
.role-name { font-size: 12px; color: var(--text-secondary); font-weight: 500; }

/* 提交按钮 */
.submit-btn {
  width: 100%;
  height: 46px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  margin-top: 4px;
  letter-spacing: 2px;
  transition: var(--transition);
}
.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.45);
}

.form-body {
  padding: 0 2px;
}

:deep(.el-form-item) {
  margin-bottom: 16px;
}
</style>