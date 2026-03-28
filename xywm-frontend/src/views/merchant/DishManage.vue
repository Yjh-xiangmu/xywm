<template>
  <div class="dish-manage">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span style="font-weight: bold;">菜品管理</span>
          <el-button color="#f5576c" @click="openDialog('add')">新增菜品</el-button>
        </div>
      </template>

      <div style="margin-bottom: 20px;">
        <el-select v-model="searchCategory" placeholder="请选择分类过滤" clearable style="width: 200px" @change="fetchList">
          <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column label="图片" width="100" align="center">
          <template #default="scope">
            <el-image style="width: 60px; height: 60px; border-radius: 6px;" :src="scope.row.image" :preview-src-list="[scope.row.image]" preview-teleported fit="cover" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="菜品名称" min-width="150" />
        <el-table-column prop="categoryName" label="所属分类" width="120" />
        <el-table-column label="价格" width="100">
          <template #default="scope">
            <span style="color: #f5576c; font-weight: bold;">￥{{ scope.row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '售卖中' : '已停售' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center">
          <template #default="scope">
            <el-button type="primary" link @click="openDialog('edit', scope.row)">编辑</el-button>
            <el-button :type="scope.row.status === 1 ? 'warning' : 'success'" link @click="handleStatusChange(scope.row)">
              {{ scope.row.status === 1 ? '停售' : '起售' }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogType === 'add' ? '新增菜品' : '修改菜品'" v-model="dialogVisible" width="550px">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="菜品名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入菜品名称" />
        </el-form-item>
        <el-form-item label="菜品分类" prop="categoryId">
          <el-select v-model="formData.categoryId" placeholder="请选择菜品分类" style="width: 100%">
            <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="菜品价格" prop="price">
          <el-input-number v-model="formData.price" :precision="2" :step="1" :min="0" style="width: 200px" />
          <span style="margin-left: 10px; color: #666;">元</span>
        </el-form-item>
        <el-form-item label="菜品图片" prop="image">
          <el-upload
              class="avatar-uploader"
              action="http://localhost:8080/api/file/upload"
              :show-file-list="false"
              :on-success="handleUploadSuccess"
          >
            <img v-if="formData.image" :src="formData.image" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="菜品描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="介绍一下这道菜吧" />
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
import { Plus } from '@element-plus/icons-vue'
import { getDishListApi, addDishApi, updateDishApi, deleteDishApi, updateDishStatusApi, getCategoryListApi } from '@/api/merchant'

const tableData = ref([])
const categoryList = ref([])
const searchCategory = ref('')
const loading = ref(false)
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref()

const formData = reactive({ id: '', name: '', categoryId: '', price: 0, image: '', description: '' })
const rules = {
  name: [{ required: true, message: '菜品名称不能为空', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  image: [{ required: true, message: '请上传菜品图片', trigger: 'change' }]
}

const fetchCategory = async () => {
  const res = await getCategoryListApi()
  categoryList.value = res.data || []
}

const fetchList = async () => {
  loading.value = true
  try {
    const params = searchCategory.value ? { categoryId: searchCategory.value } : {}
    const res = await getDishListApi(params)
    tableData.value = res.data || []
  } finally {
    loading.value = false
  }
}

const handleUploadSuccess = (res) => {
  if (res.code === 200) {
    formData.image = res.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(res.msg)
  }
}

const openDialog = (type, row = null) => {
  dialogType.value = type
  dialogVisible.value = true
  if (type === 'add') {
    Object.assign(formData, { id: '', name: '', categoryId: '', price: 0, image: '', description: '' })
  } else {
    Object.assign(formData, { id: row.id, name: row.name, categoryId: row.categoryId, price: row.price, image: row.image, description: row.description })
  }
  setTimeout(() => formRef.value?.clearValidate(), 0)
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  if (dialogType.value === 'add') {
    await addDishApi(formData)
    ElMessage.success('新增成功')
  } else {
    await updateDishApi(formData)
    ElMessage.success('修改成功')
  }
  dialogVisible.value = false
  fetchList()
}

const handleStatusChange = async (row) => {
  const targetStatus = row.status === 1 ? 0 : 1
  await updateDishStatusApi(targetStatus, row.id)
  ElMessage.success(targetStatus === 1 ? '已起售' : '已停售')
  fetchList()
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该菜品吗？', '提示', { type: 'warning' }).then(async () => {
    await deleteDishApi(id)
    ElMessage.success('删除成功')
    fetchList()
  }).catch(() => {})
}

onMounted(async () => {
  await fetchCategory()
  fetchList()
})
</script>

<style scoped>
.avatar-uploader .el-upload {
  border: 1px dashed var(--border);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--transition);
}
.avatar-uploader .el-upload:hover { border-color: #f5576c; }
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 150px;
  height: 150px;
  text-align: center;
  line-height: 150px;
  border: 1px dashed #d9d9d9;
}
.avatar { width: 150px; height: 150px; display: block; object-fit: cover; }
</style>