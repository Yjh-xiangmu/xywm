<template>
  <div class="shop-setting">
    <div class="page-header">
      <h2 class="page-title">店铺设置</h2>
    </div>

    <div class="setting-card" v-loading="loading">
      <div class="avatar-section">
        <el-upload
            action="http://localhost:8080/api/file/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeUpload"
        >
          <div class="avatar-wrapper">
            <el-avatar :size="100" :src="formData.avatar" class="shop-avatar">
              {{ formData.nickname?.charAt(0) }}
            </el-avatar>
            <div class="avatar-mask">
              <el-icon><Camera /></el-icon>
              <span>更换店铺头像</span>
            </div>
          </div>
        </el-upload>
        <p class="avatar-hint">建议上传正方形图片，支持JPG/PNG</p>
      </div>

      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px" style="max-width:480px;">
        <el-form-item label="店铺名称" prop="nickname">
          <el-input v-model="formData.nickname" placeholder="请输入店铺名称" maxlength="20" show-word-limit />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="登录账号">
          <el-input :value="formData.username" disabled />
        </el-form-item>

        <el-divider>修改密码（选填）</el-divider>

        <el-form-item label="新密码">
          <el-input v-model="formData.newPassword" type="password" placeholder="不修改请留空" show-password />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="formData.confirmPassword" type="password" placeholder="再次输入新密码" show-password />
        </el-form-item>

        <el-form-item>
          <el-button class="save-btn" :loading="submitting" @click="handleSave">保存设置</el-button>
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

const authStore = useAuthStore()
const loading = ref(false)
const submitting = ref(false)
const formRef = ref()

const formData = reactive({
  username: '', nickname: '', phone: '',
  avatar: '', newPassword: '', confirmPassword: ''
})

const rules = {
  nickname: [{ required: true, message: '店铺名称不能为空', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }]
}

const loadProfile = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/user/profile')
    const d = res.data
    Object.assign(formData, { username: d.username, nickname: d.nickname,
      phone: d.phone || '', avatar: d.avatar || '' })
  } finally {
    loading.value = false
  }
}

const handleSave = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  if (formData.newPassword && formData.newPassword !== formData.confirmPassword) {
    ElMessage.error('两次密码不一致')
    return
  }
  submitting.value = true
  try {
    const body = { nickname: formData.nickname, phone: formData.phone, avatar: formData.avatar }
    if (formData.newPassword) body.newPassword = formData.newPassword
    await request.put('/api/user/profile', body)
    const userInfo = { ...authStore.userInfo, nickname: formData.nickname, avatar: formData.avatar }
    authStore.setAuth(authStore.token, userInfo)
    ElMessage.success('保存成功')
    formData.newPassword = ''
    formData.confirmPassword = ''
  } finally {
    submitting.value = false
  }
}

const handleAvatarSuccess = (res) => {
  if (res.code === 200) { formData.avatar = res.data; ElMessage.success('头像上传成功') }
}

const beforeUpload = (file) => {
  const ok = ['image/jpeg', 'image/png'].includes(file.type) && file.size / 1024 / 1024 < 2
  if (!ok) ElMessage.error('只能上传2MB以内的JPG/PNG图片')
  return ok
}

onMounted(() => loadProfile())
</script>

<style scoped>
.page-header { margin-bottom:24px; }
.page-title { font-size:20px; font-weight:700; color:#303133; margin:0; }
.setting-card { background:white; border-radius:16px; padding:36px; box-shadow:0 2px 12px rgba(0,0,0,0.06); }
.avatar-section { display:flex; flex-direction:column; align-items:center; margin-bottom:32px; }
.avatar-wrapper { position:relative; cursor:pointer; border-radius:50%; overflow:hidden; }
.shop-avatar { background:linear-gradient(135deg,#f093fb,#f5576c); font-size:32px; color:white; font-weight:700; display:block; }
.avatar-mask { position:absolute; inset:0; background:rgba(0,0,0,0.45); display:flex; flex-direction:column; align-items:center; justify-content:center; color:white; font-size:12px; gap:4px; opacity:0; transition:opacity 0.2s; border-radius:50%; }
.avatar-wrapper:hover .avatar-mask { opacity:1; }
.avatar-hint { font-size:12px; color:#c0c4cc; margin:8px 0 0; }
.save-btn { width:100%; height:44px; border-radius:22px; font-size:15px; font-weight:600; background:linear-gradient(135deg,#f093fb,#f5576c); border:none; color:white; }
</style>