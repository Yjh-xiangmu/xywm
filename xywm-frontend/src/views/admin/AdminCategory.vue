<template>
  <div class="category-manage">

    <div class="page-header">
      <h2 class="page-title">平台分类管理</h2>
      <el-button class="add-btn" @click="openDialog()">
        <el-icon><Plus /></el-icon> 新增分类
      </el-button>
    </div>

    <el-card>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="sort" label="排序权重" width="120" align="center" />
        <el-table-column label="关联商家数" width="130" align="center">
          <template #default="scope">
            <el-tag type="info" effect="plain">{{ getMerchantCount(scope.row.id) }} 家</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
        <el-table-column label="操作" width="160" align="center">
          <template #default="scope">
            <el-button type="primary" link @click="openDialog(scope.row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
        :title="isEdit ? '编辑分类' : '新增分类'"
        v-model="dialogVisible"
        width="400px"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="formData.name" placeholder="如：中式快餐、饮品甜点" />
        </el-form-item>
        <el-form-item label="排序权重" prop="sort">
          <el-input-number v-model="formData.sort" :min="1" :max="99" style="width:100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading      = ref(false)
const submitting   = ref(false)
const dialogVisible = ref(false)
const isEdit       = ref(false)
const formRef      = ref()
const tableData    = ref([])
const merchantList = ref([])

const formData = reactive({ id: null, name: '', sort: 1 })
const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const [catRes, shopRes] = await Promise.all([
      request.get('/api/category/platform'),
      request.get('/api/shop/list')
    ])
    tableData.value    = catRes.data  || []
    merchantList.value = shopRes.data || []
  } finally {
    loading.value = false
  }
}

const getMerchantCount = (categoryId) => {
  return merchantList.value.filter(m => m.categoryId === categoryId).length
}

const openDialog = (row = null) => {
  isEdit.value = !!row
  if (row) {
    Object.assign(formData, { id: row.id, name: row.name, sort: row.sort })
  } else {
    Object.assign(formData, { id: null, name: '', sort: 1 })
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
      await request.put('/api/category/platform', formData)
      ElMessage.success('修改成功')
    } else {
      await request.post('/api/category/platform', formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

const handleDelete = (id) => {
  ElMessageBox.confirm(
      '删除该分类后，关联商家将变为未分类状态，确定删除吗？',
      '提示',
      { type: 'warning', confirmButtonText: '确定删除', cancelButtonText: '取消' }
  ).then(async () => {
    await request.delete(`/api/category/platform/${id}`)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

onMounted(() => loadData())
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}
.page-title { font-size: 20px; font-weight: 700; color: #303133; margin: 0; }
.add-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  color: white;
  font-weight: 600;
  border-radius: 20px;
  padding: 8px 20px;
}
</style>