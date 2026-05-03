<template>
  <div class="category-manage">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span style="font-weight: bold;">分类管理</span>
          <el-button color="#f5576c" @click="openDialog('add')">新增分类</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="sort" label="排序权重" width="120" align="center" />
        <el-table-column prop="createTime" label="创建时间" width="200" align="center" />
        <el-table-column label="操作" width="180" align="center">
          <template #default="scope">
            <el-button type="primary" link @click="openDialog('edit', scope.row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogType === 'add' ? '新增分类' : '修改分类'" v-model="dialogVisible" width="400px">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="formData.sort" :min="1" :max="99" style="width: 100%;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button color="#f5576c" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCategoryListApi, addCategoryApi, updateCategoryApi, deleteCategoryApi } from '@/api/merchant'

const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref()

const formData = reactive({ id: '', name: '', sort: 1 })
const rules = { name: [{ required: true, message: '分类名称不能为空', trigger: 'blur' }] }

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getCategoryListApi()
    tableData.value = res.data || []
  } finally {
    loading.value = false
  }
}

const openDialog = (type, row = null) => {
  dialogType.value = type
  dialogVisible.value = true
  if (type === 'add') {
    Object.assign(formData, { id: '', name: '', sort: 1 })
  } else {
    Object.assign(formData, { id: row.id, name: row.name, sort: row.sort })
  }
  setTimeout(() => formRef.value?.clearValidate(), 0)
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  if (dialogType.value === 'add') {
    await addCategoryApi(formData)
    ElMessage.success('新增成功')
  } else {
    await updateCategoryApi(formData)
    ElMessage.success('修改成功')
  }
  dialogVisible.value = false
  fetchList()
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该分类吗？如果是关联了菜品的分类可能无法删除。', '提示', { type: 'warning' }).then(async () => {
    await deleteCategoryApi(id)
    ElMessage.success('删除成功')
    fetchList()
  }).catch(() => {})
}

onMounted(() => fetchList())
</script>