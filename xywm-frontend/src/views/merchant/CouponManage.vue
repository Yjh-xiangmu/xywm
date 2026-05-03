<template>
  <div class="coupon-manage">
    <div class="page-header">
      <h2 class="page-title">优惠活动</h2>
      <el-button class="add-btn" @click="openDialog()">
        <el-icon><Plus /></el-icon> 发布优惠券
      </el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" border stripe>
      <el-table-column prop="name" label="券名称" min-width="130" />
      <el-table-column label="类型" width="80" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.type === 1 ? 'danger' : 'warning'" size="small">
            {{ scope.row.type === 1 ? '满减' : '折扣' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="优惠内容" min-width="150">
        <template #default="scope">
          <span style="color:#f5576c;font-weight:600;">
            {{ scope.row.type === 1
              ? `满${scope.row.minAmount}减${scope.row.discountAmount}`
              : `满${scope.row.minAmount}享${scope.row.discountRate}折` }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="领取规则" width="110" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.canRepeat ? 'success' : 'info'" size="small" effect="plain">
            {{ scope.row.canRepeat ? '用完可再领' : '仅限一次' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="领取/总量" width="100" align="center">
        <template #default="scope">{{ scope.row.received }} / {{ scope.row.total }}</template>
      </el-table-column>
      <el-table-column prop="expireDays" label="有效天数" width="90" align="center">
        <template #default="scope">{{ scope.row.expireDays }}天</template>
      </el-table-column>
      <el-table-column label="状态" width="80" align="center">
        <template #default="scope">
          <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0"
                     @change="toggleStatus(scope.row)" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="130" align="center">
        <template #default="scope">
          <el-button type="primary" link @click="openDialog(scope.row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="isEdit ? '编辑优惠券' : '发布优惠券'" v-model="dialogVisible" width="500px">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="券名称" prop="name">
          <el-input v-model="formData.name" placeholder="如：新人专享券、周末特惠" />
        </el-form-item>
        <el-form-item label="优惠类型" prop="type">
          <el-radio-group v-model="formData.type">
            <el-radio :value="1">满减券</el-radio>
            <el-radio :value="2">折扣券</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="使用门槛" prop="minAmount">
          <el-input-number v-model="formData.minAmount" :min="0" :precision="2" style="width:160px" />
          <span style="margin-left:8px;color:#909399;">元（0表示无门槛）</span>
        </el-form-item>
        <el-form-item label="减免金额" prop="discountAmount" v-if="formData.type === 1">
          <el-input-number v-model="formData.discountAmount" :min="0.01" :precision="2" style="width:160px" />
          <span style="margin-left:8px;color:#909399;">元</span>
        </el-form-item>
        <el-form-item label="折扣率" prop="discountRate" v-if="formData.type === 2">
          <el-input-number v-model="formData.discountRate" :min="0.1" :max="9.9" :step="0.1"
                           :precision="1" style="width:160px" />
          <span style="margin-left:8px;color:#909399;">折（8.5 = 85折）</span>
        </el-form-item>
        <el-form-item label="发放总量" prop="total">
          <el-input-number v-model="formData.total" :min="1" :max="9999" style="width:160px" />
          <span style="margin-left:8px;color:#909399;">张</span>
        </el-form-item>
        <el-form-item label="有效天数" prop="expireDays">
          <el-input-number v-model="formData.expireDays" :min="1" :max="365" style="width:160px" />
          <span style="margin-left:8px;color:#909399;">天（领取后计算）</span>
        </el-form-item>
        <el-form-item label="领取规则">
          <el-radio-group v-model="formData.canRepeat">
            <el-radio :value="0">
              <span>仅限一次</span>
              <span style="font-size:12px;color:#909399;margin-left:4px;">领过就不能再领</span>
            </el-radio>
            <el-radio :value="1">
              <span>用完可再领</span>
              <span style="font-size:12px;color:#909399;margin-left:4px;">用掉后可再领一张</span>
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button color="#f5576c" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const tableData = ref([])

const formData = reactive({
  id: null, name: '', type: 1, minAmount: 0,
  discountAmount: 5, discountRate: 9.0,
  total: 100, expireDays: 7, canRepeat: 0
})

const rules = {
  name: [{ required: true, message: '请输入券名称', trigger: 'blur' }],
  total: [{ required: true, message: '请输入发放总量', trigger: 'blur' }],
}

const loadList = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/coupon/merchant/list')
    tableData.value = res.data || []
  } finally {
    loading.value = false
  }
}

const openDialog = (row = null) => {
  isEdit.value = !!row
  if (row) {
    Object.assign(formData, {
      id: row.id, name: row.name, type: row.type,
      minAmount: row.minAmount, discountAmount: row.discountAmount,
      discountRate: row.discountRate, total: row.total,
      expireDays: row.expireDays, canRepeat: row.canRepeat ?? 0
    })
  } else {
    Object.assign(formData, {
      id: null, name: '', type: 1, minAmount: 0,
      discountAmount: 5, discountRate: 9.0,
      total: 100, expireDays: 7, canRepeat: 0
    })
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
      await request.put('/api/coupon/merchant', formData)
      ElMessage.success('修改成功')
    } else {
      await request.post('/api/coupon/merchant', formData)
      ElMessage.success('发布成功')
    }
    dialogVisible.value = false
    loadList()
  } finally {
    submitting.value = false
  }
}

const toggleStatus = async (row) => {
  await request.put(`/api/coupon/merchant/${row.id}/status/${row.status}`)
  ElMessage.success(row.status === 1 ? '已启用' : '已停用')
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定删除该优惠券吗？', '提示', { type: 'warning' })
      .then(async () => {
        await request.delete(`/api/coupon/merchant/${id}`)
        ElMessage.success('删除成功')
        loadList()
      }).catch(() => {})
}

onMounted(() => loadList())
</script>

<style scoped>
.page-header { display:flex; align-items:center; justify-content:space-between; margin-bottom:20px; }
.page-title { font-size:20px; font-weight:700; color:#303133; margin:0; }
.add-btn { background:linear-gradient(135deg,#f093fb,#f5576c); border:none; color:white; font-weight:600; border-radius:20px; padding:8px 20px; }
</style>