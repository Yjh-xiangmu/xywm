<template>
  <div class="address-page">

    <div class="page-header">
      <h2 class="page-title">收货地址</h2>
      <el-button
          class="add-btn"
          @click="openDialog()"
      >
        <el-icon><Plus /></el-icon> 新增地址
      </el-button>
    </div>

    <!-- 地址列表 -->
    <el-skeleton :rows="3" animated v-if="loading" />

    <div v-else-if="addressList.length === 0" class="empty">
      <div class="empty-icon">📍</div>
      <p>还没有收货地址，快去添加吧</p>
    </div>

    <div v-else class="address-list">
      <div
          v-for="item in addressList"
          :key="item.id"
          class="address-card"
          :class="{ 'is-default': item.isDefault === 1 }"
      >
        <div class="card-main">
          <div class="addr-top">
            <span class="addr-name">{{ item.name }}</span>
            <span class="addr-phone">{{ item.phone }}</span>
            <el-tag
                v-if="item.isDefault === 1"
                size="small"
                type="success"
                effect="plain"
                class="default-tag"
            >默认</el-tag>
          </div>
          <p class="addr-detail">📍 {{ item.address }}</p>
        </div>
        <div class="card-actions">
          <el-button link type="primary" @click="openDialog(item)">编辑</el-button>
          <el-divider direction="vertical" />
          <el-button
              link
              type="success"
              @click="setDefault(item)"
              v-if="item.isDefault !== 1"
          >设为默认</el-button>
          <el-divider direction="vertical" v-if="item.isDefault !== 1" />
          <el-button link type="danger" @click="handleDelete(item.id)">删除</el-button>
        </div>
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
        :title="isEdit ? '编辑地址' : '新增地址'"
        v-model="dialogVisible"
        width="480px"
        border-radius="16px"
    >
      <el-form
          :model="formData"
          :rules="rules"
          ref="formRef"
          label-width="80px"
      >
        <el-form-item label="收货人" prop="name">
          <el-input v-model="formData.name" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="详细地址" prop="address">
          <el-input
              v-model="formData.address"
              type="textarea"
              :rows="3"
              placeholder="如：学生公寓1号楼301室"
          />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="formData.isDefault" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ isEdit ? '保存修改' : '确认添加' }}
        </el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAddressListApi,
  addAddressApi,
  updateAddressApi,
  deleteAddressApi
} from '@/api/user'

const loading   = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit    = ref(false)
const formRef   = ref()
const addressList = ref([])

const formData = reactive({
  id: null,
  name: '',
  phone: '',
  address: '',
  isDefault: 0
})

const rules = {
  name:    [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  phone:   [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

const loadList = async () => {
  loading.value = true
  try {
    const res = await getAddressListApi()
    addressList.value = res.data || []
  } finally {
    loading.value = false
  }
}

const openDialog = (item = null) => {
  isEdit.value = !!item
  if (item) {
    Object.assign(formData, {
      id: item.id,
      name: item.name,
      phone: item.phone,
      address: item.address,
      isDefault: item.isDefault
    })
  } else {
    Object.assign(formData, { id: null, name: '', phone: '', address: '', isDefault: 0 })
  }
  dialogVisible.value = true
  setTimeout(() => formRef.value?.clearValidate(), 0)
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateAddressApi(formData)
      ElMessage.success('修改成功')
    } else {
      await addAddressApi(formData)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadList()
  } finally {
    submitting.value = false
  }
}

const setDefault = async (item) => {
  await updateAddressApi({ ...item, isDefault: 1 })
  ElMessage.success('已设为默认地址')
  loadList()
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定删除该地址吗？', '提示', {
    type: 'warning',
    confirmButtonText: '删除',
    cancelButtonText: '取消'
  }).then(async () => {
    await deleteAddressApi(id)
    ElMessage.success('已删除')
    loadList()
  }).catch(() => {})
}

onMounted(() => loadList())
</script>

<style scoped>
.address-page { min-height: 100%; }

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}
.page-title {
  font-size: 20px;
  font-weight: 700;
  color: #303133;
  margin: 0;
}
.add-btn {
  background: linear-gradient(135deg, #43e97b, #38f9d7);
  border: none;
  color: white;
  font-weight: 600;
  border-radius: 20px;
  padding: 8px 20px;
}
.add-btn:hover { opacity: 0.9; transform: translateY(-1px); }

.empty {
  text-align: center;
  padding: 80px 0;
  color: #909399;
}
.empty-icon { font-size: 56px; margin-bottom: 12px; }

/* ===== 地址卡片 ===== */
.address-list { display: flex; flex-direction: column; gap: 12px; }
.address-card {
  background: white;
  border-radius: 14px;
  padding: 18px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 10px rgba(0,0,0,0.04);
  border: 2px solid transparent;
  transition: all 0.2s;
}
.address-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.08); }
.address-card.is-default { border-color: #43e97b; background: #f6fffb; }

.card-main { flex: 1; min-width: 0; }
.addr-top {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}
.addr-name {
  font-size: 15px;
  font-weight: 700;
  color: #303133;
}
.addr-phone { font-size: 14px; color: #606266; }
.default-tag { flex-shrink: 0; }
.addr-detail {
  font-size: 13px;
  color: #606266;
  margin: 0;
  line-height: 1.5;
}

.card-actions {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  margin-left: 20px;
}
</style>