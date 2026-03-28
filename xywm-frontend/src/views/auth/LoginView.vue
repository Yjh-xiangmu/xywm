<template>
  <div class="login-page">
    <div class="bg-animation">
      <div class="bubble" v-for="i in 8" :key="i"></div>
    </div>

    <div class="login-container">
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

      <div class="login-right">
        <div class="form-card">
          <div class="tab-bar">
            <div class="tab-item" :class="{ active: activeTab === 'login' }" @click="activeTab = 'login'">登录</div>
            <div class="tab-item" :class="{ active: activeTab === 'register' }" @click="activeTab = 'register'">注册</div>
            <div class="tab-indicator" :style="{ left: activeTab === 'login' ? '0%' : '50%' }"></div>
          </div>

          <transition name="slide-up" mode="out-in">
            <div v-if="activeTab === 'login'" key="login" class="form-body">
              <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef">
                <el-form-item prop="username">
                  <div class="input-wrapper">
                    <el-icon class="input-icon"><User /></el-icon>
                    <el-input v-model="loginForm.username" placeholder="请输入用户名" size="large" />
                  </div>
                </el-form-item>
                <el-form-item prop="password">
                  <div class="input-wrapper">
                    <el-icon class="input-icon"><Lock /></el-icon>
                    <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" size="large" show-password @keyup.enter="handleLogin" />
                  </div>
                </el-form-item>

                <el-form-item prop="role">
                  <div class="role-select">
                    <div class="role-item" v-for="role in roles" :key="role.value" :class="{ active: loginForm.role === role.value }" @click="loginForm.role = role.value">
                      <span class="role-emoji">{{ role.icon }}</span>
                      <span class="role-name">{{ role.label }}</span>
                    </div>
                  </div>
                </el-form-item>

                <el-button class="submit-btn" type="primary" size="large" :loading="loading" @click="handleLogin">
                  {{ loading ? '登录中...' : '登 录' }}
                </el-button>
              </el-form>
            </div>

            <div v-else key="register" class="form-body">
              <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef">

                <el-form-item prop="role">
                  <div class="role-select">
                    <div class="role-item" v-for="role in registerRoles" :key="role.value" :class="{ active: registerForm.role === role.value }" @click="registerForm.role = role.value">
                      <span class="role-emoji">{{ role.icon }}</span>
                      <span class="role-name">{{ role.label }}</span>
                    </div>
                  </div>
                </el-form-item>

                <el-form-item prop="username">
                  <div class="input-wrapper">
                    <el-icon class="input-icon"><User /></el-icon>
                    <el-input v-model="registerForm.username" placeholder="请输入账号" size="large" />
                  </div>
                </el-form-item>
                <el-form-item prop="nickname">
                  <div class="input-wrapper">
                    <el-icon class="input-icon"><EditPen /></el-icon>
                    <el-input v-model="registerForm.nickname" :placeholder="registerForm.role === 1 ? '请输入店铺名称' : '请输入昵称'" size="large" />
                  </div>
                </el-form-item>
                <el-form-item prop="phone">
                  <div class="input-wrapper">
                    <el-icon class="input-icon"><Phone /></el-icon>
                    <el-input v-model="registerForm.phone" placeholder="请输入手机号" size="large" />
                  </div>
                </el-form-item>
                <el-form-item prop="password">
                  <div class="input-wrapper">
                    <el-icon class="input-icon"><Lock /></el-icon>
                    <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" size="large" show-password />
                  </div>
                </el-form-item>
                <el-form-item prop="confirmPassword">
                  <div class="input-wrapper">
                    <el-icon class="input-icon"><Lock /></el-icon>
                    <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请确认密码" size="large" show-password />
                  </div>
                </el-form-item>

                <el-form-item v-if="registerForm.role === 1" prop="licenseUrl" style="margin-top: 10px;">
                  <div style="width: 100%; text-align: center;">
                    <el-upload
                        class="license-uploader"
                        action="http://localhost:8080/api/file/upload"
                        :show-file-list="false"
                        :on-success="handleUploadSuccess"
                        :before-upload="beforeUpload"
                    >
                      <img v-if="registerForm.licenseUrl" :src="registerForm.licenseUrl" class="license-img" />
                      <div v-else class="upload-placeholder">
                        <el-icon><Plus /></el-icon>
                        <span>点击上传营业执照</span>
                      </div>
                    </el-upload>
                  </div>
                </el-form-item>

                <el-button class="submit-btn" type="primary" size="large" :loading="loading" @click="handleRegister">
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
import { Plus } from '@element-plus/icons-vue'
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
  { value: 1, icon: '🍜', label: '商家入驻' },
  { value: 2, icon: '🎓', label: '学生注册' },
]

const loginForm = reactive({ username: '', password: '', role: 2 })
const registerForm = reactive({
  username: '', nickname: '', phone: '',
  password: '', confirmPassword: '', role: 2,
  licenseUrl: '' // 新增的凭证URL字段
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const registerRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称/店名', trigger: 'blur' }],
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
  ]
}

// 处理图片上传前校验
const beforeUpload = (file) => {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImage) ElMessage.error('营业执照只能是 JPG/PNG 格式!')
  if (!isLt2M) ElMessage.error('图片大小不能超过 2MB!')
  return isImage && isLt2M
}

// 处理上传成功回调
const handleUploadSuccess = (res) => {
  if (res.code === 200) {
    registerForm.licenseUrl = res.data
    ElMessage.success('执照上传成功')
  } else {
    ElMessage.error(res.msg || '上传失败')
  }
}

const handleLogin = async () => {
  const valid = await loginFormRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await loginApi(loginForm)
    // 如果返回状态是被封禁或待审核的情况
    if (res.data.userInfo.status === 2) {
      ElMessage.warning('您的商家账号正在审核中，请耐心等待！')
      loading.value = false
      return
    }

    authStore.setAuth(res.data.token, res.data.userInfo)
    ElMessage.success('登录成功，欢迎回来！')
    const roleMap = { 0: '/admin/dashboard', 1: '/merchant/dashboard', 2: '/user/home' }
    router.push(roleMap[res.data.userInfo.role])
  } catch (e) {
    // 错误在拦截器抛出
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  const valid = await registerFormRef.value?.validate().catch(() => false)
  if (!valid) return

  // 商家必须上传图片
  if (registerForm.role === 1 && !registerForm.licenseUrl) {
    ElMessage.warning('商家入驻必须上传营业执照图片')
    return
  }

  loading.value = true
  try {
    await registerApi(registerForm)
    if (registerForm.role === 1) {
      ElMessage.success('申请提交成功，请等待管理员审核！')
    } else {
      ElMessage.success('注册成功，请登录')
    }
    activeTab.value = 'login'
    loginForm.username = registerForm.username
    loginForm.role = registerForm.role
  } catch (e) {
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 之前的样式全部保留 */
.login-page { min-height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); display: flex; align-items: center; justify-content: center; overflow: hidden; position: relative; }
.bg-animation { position: absolute; inset: 0; overflow: hidden; pointer-events: none; }
.bubble { position: absolute; border-radius: 50%; background: rgba(255, 255, 255, 0.08); animation: float linear infinite; }
.bubble:nth-child(1) { width: 80px; height: 80px; left: 10%; animation-duration: 8s; animation-delay: 0s; }
.bubble:nth-child(2) { width: 120px; height: 120px; left: 25%; animation-duration: 12s; animation-delay: 2s; }
.bubble:nth-child(3) { width: 60px; height: 60px; left: 40%; animation-duration: 9s; animation-delay: 1s; }
.bubble:nth-child(4) { width: 100px; height: 100px; left: 55%; animation-duration: 11s; animation-delay: 3s; }
.bubble:nth-child(5) { width: 70px; height: 70px; left: 70%; animation-duration: 7s; animation-delay: 0.5s; }
.bubble:nth-child(6) { width: 140px; height: 140px; left: 80%; animation-duration: 13s; animation-delay: 4s; }
.bubble:nth-child(7) { width: 50px; height: 50px; left: 88%; animation-duration: 10s; animation-delay: 1.5s; }
.bubble:nth-child(8) { width: 90px; height: 90px; left: 5%; animation-duration: 14s; animation-delay: 2.5s; }

@keyframes float { 0% { transform: translateY(110vh) scale(0); opacity: 0; } 10% { opacity: 1; } 90% { opacity: 1; } 100% { transform: translateY(-10vh) scale(1); opacity: 0; } }

.login-container { display: flex; width: 900px; min-height: 560px; background: white; border-radius: 24px; overflow: hidden; box-shadow: 0 24px 80px rgba(0, 0, 0, 0.25); position: relative; z-index: 1; }
.login-left { flex: 1; background: linear-gradient(160deg, #667eea 0%, #764ba2 100%); padding: 48px 40px; display: flex; flex-direction: column; justify-content: center; color: white; }
.brand-icon { font-size: 56px; margin-bottom: 16px; }
.brand h1 { font-size: 32px; font-weight: 700; margin-bottom: 10px; }
.brand p { font-size: 15px; opacity: 0.85; margin-bottom: 40px; }
.features { display: flex; flex-direction: column; gap: 16px; }
.feature-item { display: flex; align-items: center; gap: 12px; font-size: 14px; opacity: 0.9; }
.feature-icon { font-size: 20px; width: 36px; height: 36px; background: rgba(255,255,255,0.15); border-radius: 8px; display: flex; align-items: center; justify-content: center; }

.login-right { flex: 1; display: flex; align-items: center; justify-content: center; padding: 40px; background: #fff; }
.form-card { width: 100%; max-width: 360px; }

.tab-bar { display: flex; position: relative; background: #f5f6fa; border-radius: 10px; padding: 4px; margin-bottom: 28px; }
.tab-item { flex: 1; text-align: center; padding: 9px 0; font-size: 15px; font-weight: 500; color: var(--text-secondary); cursor: pointer; position: relative; z-index: 1; transition: color 0.3s; }
.tab-item.active { color: var(--primary); }
.tab-indicator { position: absolute; top: 4px; width: 50%; height: calc(100% - 8px); background: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); transition: left 0.3s cubic-bezier(0.4, 0, 0.2, 1); }

.input-wrapper { position: relative; width: 100%; }
.input-icon { position: absolute; left: 12px; top: 50%; transform: translateY(-50%); color: var(--text-secondary); z-index: 1; font-size: 16px; }
.input-wrapper :deep(.el-input__wrapper) { padding-left: 36px; border-radius: 10px; box-shadow: 0 0 0 1px var(--border); transition: var(--transition); }
.input-wrapper :deep(.el-input__wrapper:hover), .input-wrapper :deep(.el-input__wrapper.is-focus) { box-shadow: 0 0 0 2px var(--primary) !important; }

.role-select { display: flex; gap: 10px; width: 100%; }
.role-item { flex: 1; display: flex; flex-direction: column; align-items: center; gap: 4px; padding: 10px 6px; border: 2px solid var(--border); border-radius: 10px; cursor: pointer; transition: var(--transition); background: #fafafa; }
.role-item:hover { border-color: var(--primary); background: #f0f1ff; }
.role-item.active { border-color: var(--primary); background: linear-gradient(135deg, #eef0ff, #f5f0ff); }
.role-emoji { font-size: 20px; }
.role-name { font-size: 12px; color: var(--text-secondary); font-weight: 500; }

.submit-btn { width: 100%; height: 46px; border-radius: 10px; font-size: 16px; font-weight: 600; background: linear-gradient(135deg, #667eea, #764ba2); border: none; margin-top: 4px; letter-spacing: 2px; transition: var(--transition); }
.submit-btn:hover { transform: translateY(-1px); box-shadow: 0 6px 20px rgba(102, 126, 234, 0.45); }
.form-body { padding: 0 2px; }
:deep(.el-form-item) { margin-bottom: 16px; }

/* 营业执照上传框样式 */
.license-uploader {
  border: 1px dashed var(--border);
  border-radius: 10px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 100%;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafafa;
  transition: border-color 0.3s;
}
.license-uploader:hover { border-color: var(--primary); }
.upload-placeholder { display: flex; flex-direction: column; align-items: center; color: var(--text-secondary); }
.upload-placeholder .el-icon { font-size: 28px; margin-bottom: 8px; color: #8c939d; }
.license-img { width: 100%; height: 100%; object-fit: contain; }
</style>