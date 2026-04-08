<template>
  <div class="profile-page">

    <div class="page-header">
      <h2 class="page-title">个人信息</h2>
    </div>

    <div class="profile-card" v-loading="loading">
      <!-- 头像区域 -->
      <div class="avatar-section">
        <el-upload
            class="avatar-uploader"
            action="http://localhost:8080/api/file/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeUpload"
        >
          <el-avatar :size="90" :src="formData.avatar" class="big-avatar">
            {{ formData.nickname?.charAt(0) || formData.username?.charAt(0) }}
          </el-avatar>
          <div class="avatar-mask">
            <el-icon><Camera /></el-icon>
            <span>更换头像</span>
          </div>
        </el-upload>
        <p class="avatar-hint">点击头像可更换</p>
      </div>

      <!-- 表单 -->
      <el-form
          :model="formData"
          :rules="rules"
          ref="formRef"
          label-width="80px"
          class="profile-form"
      >
        <el-form-item label="用户名">
          <el-input v-model="formData.username" disabled />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="formData.nickname" placeholder="请输入昵称" maxlength="20" show-word-limit />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="角色">
          <el-tag v-if="formData.role === 0" type="danger">管理员</el-tag>
          <el-tag v-else-if="formData.role === 1" type="warning">商家</el-tag>
          <el-tag v-else type="success">学生</el-tag>
        </el-form-item>
        <el-form-item label="注册时间">
          <span style="color:#909399;">{{ formData.createTime }}</span>
        </el-form-item>

        <el-divider>修改密码（选填）</el-divider>

        <el-form-item label="新密码" prop="newPassword">
          <el-input
              v-model="formData.newPassword"
              type="password"
              placeholder="不修改请留空"
              show-password
              maxlength="20"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
              v-model="formData.confirmPassword"
              type="password"
              placeholder="再次输入新密码"
              show-password
              maxlength="20"
          />
        </el-form-item>

        <el-form-item>
          <el-button
              class="save-btn"
              :loading="submitting"
              @click="handleSave"
          >保存修改</el-button>
        </el-form-item>
      </el-form>
    </div>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import request from '@/utils/request'

const authStore  = ref(useAuthStore())
const loading    = ref(false)
const submitting = ref(false)
const formRef    = ref()

const formData = reactive({
  username: '',
  nickname: '',
  phone: '',
  avatar: '',
  role: 2,
  createTime: '',
  newPassword: '',
  confirmPassword: ''
})

const rules = {
  nickname: [{ required: true, message: '昵称不能为空', trigger: 'blur' }],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  confirmPassword: [{
    validator: (rule, value, callback) => {
      if (formData.newPassword && value !== formData.newPassword) {
        callback(new Error('两次密码不一致'))
      } else {
        callback()
      }
    },
    trigger: 'blur'
  }]
}

const loadProfile = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/user/profile')
    const data = res.data
    Object.assign(formData, {
      username:    data.username,
      nickname:    data.nickname,
      phone:       data.phone || '',
      avatar:      data.avatar || '',
      role:        data.role,
      createTime:  data.createTime
    })
  } finally {
    loading.value = false
  }
}

const handleSave = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  if (formData.newPassword && formData.newPassword.length < 6) {
    ElMessage.warning('新密码至少6位')
    return
  }

  submitting.value = true
  try {
    const body = {
      nickname: formData.nickname,
      phone:    formData.phone,
      avatar:   formData.avatar,
    }
    if (formData.newPassword) {
      body.newPassword = formData.newPassword
    }

    await request.put('/api/user/profile', body)

    // 同步更新本地 store 里的用户信息
    const store = authStore.value
    const userInfo = { ...store.userInfo, nickname: formData.nickname, avatar: formData.avatar }
    store.setAuth(store.token, userInfo)

    ElMessage.success('保存成功')
    formData.newPassword = ''
    formData.confirmPassword = ''
  } finally {
    submitting.value = false
  }
}

const handleAvatarSuccess = (res) => {
  if (res.code === 200) {
    formData.avatar = res.data
    ElMessage.success('头像上传成功')
  }
}

const beforeUpload = (file) => {
  const isImage = ['image/jpeg', 'image/png', 'image/webp'].includes(file.type)
  const isLt2M  = file.size / 1024 / 1024 < 2
  if (!isImage) ElMessage.error('只能上传 JPG/PNG/WEBP 格式图片')
  if (!isLt2M)  ElMessage.error('图片不能超过 2MB')
  return isImage && isLt2M
}

onMounted(() => loadProfile())
</script>

<style scoped>
.profile-page { min-height: 100%; }
.page-header { margin-bottom: 20px; }
.page-title { font-size: 20px; font-weight: 700; color: #303133; margin: 0; }

.profile-card {
  background: white;
  border-radius: 16px;
  padding: 36px;
  max-width: 520px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}

/* 头像 */
.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 32px;
}
.avatar-uploader {
  position: relative;
  cursor: pointer;
  border-radius: 50%;
  overflow: hidden;
}
.big-avatar {
  background: linear-gradient(135deg, #43e97b, #38f9d7);
  font-size: 32px;
  font-weight: 700;
  color: white;
  display: block;
}
.avatar-mask {
  position: absolute;
  inset: 0;
  background: rgba(0,0,0,0.45);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 12px;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.2s;
  border-radius: 50%;
}
.avatar-uploader:hover .avatar-mask { opacity: 1; }
.avatar-hint { font-size: 12px; color: #c0c4cc; margin: 8px 0 0; }

/* 表单 */
.profile-form { padding: 0 20px; }
.save-btn {
  width: 100%;
  height: 44px;
  border-radius: 22px;
  font-size: 15px;
  font-weight: 600;
  background: linear-gradient(135deg, #43e97b, #38f9d7);
  border: none;
  color: white;
  margin-top: 8px;
  transition: all 0.2s;
}
.save-btn:hover { transform: translateY(-1px); box-shadow: 0 4px 14px rgba(67,233,123,0.4); }
</style>