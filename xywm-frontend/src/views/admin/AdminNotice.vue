<template>
  <div class="notice-manage">
    <div class="page-header">
      <h2 class="page-title">公告管理</h2>
      <el-button class="add-btn" @click="openDialog()">
        <el-icon><Plus /></el-icon> 发布公告
      </el-button>
    </div>

    <el-card>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="title" label="公告标题" min-width="180" />
        <el-table-column prop="content" label="内容预览" min-width="220" show-overflow-tooltip />
        <el-table-column label="状态" width="100" align="center">
          <template #default="scope">
            <el-switch
                v-model="scope.row.status"
                :active-value="1"
                :inactive-value="0"
                @change="toggleStatus(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180" align="center" />
        <el-table-column label="操作" width="150" align="center">
          <template #default="scope">
            <el-button type="primary" link @click="openDialog(scope.row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="isEdit ? '编辑公告' : '发布公告'" v-model="dialogVisible" width="560px">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入公告标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <el-input
              v-model="formData.content"
              type="textarea"
              :rows="5"
              placeholder="请输入公告内容"
              maxlength="1000"
              show-word-limit
          />
        </el-form-item>
        <el-form-item label="是否显示">
          <el-switch v-model="formData.status" :active-value="1" :inactive-value="0" active-text="显示" inactive-text="隐藏" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ isEdit ? '保存' : '发布' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading     = ref(false)
const submitting  = ref(false)
const dialogVisible = ref(false)
const isEdit      = ref(false)
const formRef     = ref()
const tableData   = ref([])

const formData = reactive({ id: null, title: '', content: '', status: 1 })
const rules = {
  title:   [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }]
}

const loadList = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/notice/list')
    tableData.value = res.data || []
  } finally {
    loading.value = false
  }
}

const openDialog = (row = null) => {
  isEdit.value = !!row
  if (row) {
    Object.assign(formData, { id: row.id, title: row.title, content: row.content, status: row.status })
  } else {
    Object.assign(formData, { id: null, title: '', content: '', status: 1 })
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
      await request.put('/api/notice', formData)
      ElMessage.success('修改成功')
    } else {
      await request.post('/api/notice', formData)
      ElMessage.success('发布成功')
    }
    dialogVisible.value = false
    loadList()
  } finally {
    submitting.value = false
  }
}

const toggleStatus = async (row) => {
  await request.put('/api/notice', { id: row.id, status: row.status })
  ElMessage.success(row.status === 1 ? '已显示' : '已隐藏')
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定删除该公告吗？', '提示', {
    type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消'
  }).then(async () => {
    await request.delete(`/api/notice/${id}`)
    ElMessage.success('删除成功')
    loadList()
  }).catch(() => {})
}

onMounted(() => loadList())
</script>

<style scoped>
.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; }
.page-title { font-size: 20px; font-weight: 700; color: #303133; margin: 0; }
.add-btn { background: linear-gradient(135deg, #667eea, #764ba2); border: none; color: white; font-weight: 600; border-radius: 20px; padding: 8px 20px; }
</style>