<template>
  <div class="manage-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span style="font-weight: bold;">商家入驻管理</span>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" style="width: 100%" border stripe>
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column prop="username" label="登录账号" width="120" />
        <el-table-column prop="nickname" label="店铺名称" min-width="150" />
        <el-table-column prop="phone" label="联系电话" width="130" />

        <el-table-column label="营业执照" align="center" width="100">
          <template #default="scope">
            <el-image
                style="width: 50px; height: 50px; border-radius: 4px; border: 1px solid #ebeef5;"
                :src="scope.row.licenseUrl"
                :preview-src-list="[scope.row.licenseUrl]"
                fit="cover"
                preview-teleported
            >
              <template #error>
                <div class="image-slot">暂无</div>
              </template>
            </el-image>
          </template>
        </el-table-column>

        <el-table-column label="所属分类" width="120" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.categoryId" type="info" effect="plain">
              {{ getCategoryName(scope.row.categoryId) }}
            </el-tag>
            <span v-else style="color: #909399; font-size: 13px;">未分配</span>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="入驻时间" width="170" align="center" />

        <el-table-column label="状态" align="center" width="90">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 2" type="warning">待审核</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="success">营业中</el-tag>
            <el-tag v-else type="danger">已封禁</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" align="center" fixed="right" width="220">
          <template #default="scope">
            <template v-if="scope.row.status === 2">
              <el-button type="success" size="small" @click="openCategoryDialog('audit', scope.row)">通过</el-button>
              <el-button type="danger" size="small" @click="handleRejectOrBan(scope.row.id, 0, '驳回申请')">驳回</el-button>
            </template>

            <template v-else-if="scope.row.status === 1">
              <el-button type="primary" size="small" plain @click="openCategoryDialog('edit', scope.row)">修改分类</el-button>
              <el-button type="danger" size="small" plain @click="handleRejectOrBan(scope.row.id, 0, '封禁该商家')">封禁</el-button>
            </template>

            <template v-else>
              <el-button type="success" size="small" plain @click="handleRejectOrBan(scope.row.id, 1, '恢复该商家营业')">解封</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
        :title="dialogMode === 'audit' ? '请为该商家分配平台分类' : '修改商家所属分类'"
        v-model="categoryDialogVisible"
        width="400px"
    >
      <el-form label-width="80px">
        <el-form-item label="所属分类">
          <el-select v-model="selectedCategoryId" placeholder="请选择平台大分类" style="width: 100%">
            <el-option
                v-for="item in categoryList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="categoryDialogVisible = false">取消</el-button>
        <el-button type="success" @click="submitCategoryUpdate">
          {{ dialogMode === 'audit' ? '确定通过并分配' : '确认修改' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMerchantListApi, updateMerchantStatusApi } from '@/api/admin'
import request from '@/utils/request'

const tableData = ref([])
const loading = ref(false)

// 分类相关数据
const categoryList = ref([])
const categoryDialogVisible = ref(false)
const dialogMode = ref('audit') // 'audit' 审核通过, 'edit' 修改老商家分类
const selectedCategoryId = ref('')
const currentUserId = ref(null)

// 获取商家列表
const fetchList = async () => {
  loading.value = true
  try {
    const res = await getMerchantListApi()
    tableData.value = res.data || []
  } finally {
    loading.value = false
  }
}

// 获取平台大分类列表
const fetchCategories = async () => {
  try {
    const res = await request.get('/api/category/platform')
    categoryList.value = res.data || []
  } catch (error) {
    console.error('获取分类失败', error)
  }
}

// 辅助方法：通过分类ID获取分类名称用于表格显示
const getCategoryName = (id) => {
  const category = categoryList.value.find(item => item.id === id)
  return category ? category.name : '未知分类'
}

// 打开分类选择弹窗 (审核或编辑共用)
const openCategoryDialog = (mode, row) => {
  dialogMode.value = mode
  currentUserId.value = row.id
  // 如果是修改老商家，把原有的分类ID回显出来
  selectedCategoryId.value = row.categoryId || ''
  categoryDialogVisible.value = true
}

// 提交分类绑定 (包含审核通过和修改分类)
const submitCategoryUpdate = async () => {
  if (!selectedCategoryId.value) {
    ElMessage.warning('必须为商家选择一个平台分类！')
    return
  }

  await updateMerchantStatusApi({
    userId: currentUserId.value,
    status: 1, // 无论是审核通过还是修改分类，状态都保持1(营业中)
    categoryId: selectedCategoryId.value
  })

  ElMessage.success(dialogMode.value === 'audit' ? '审核通过，分配成功' : '分类修改成功')
  categoryDialogVisible.value = false
  fetchList()
}

// 处理直接的驳回、封禁、解封操作 (不需要选分类)
const handleRejectOrBan = (id, targetStatus, actionText) => {
  ElMessageBox.confirm(
      `确定要${actionText}吗？`,
      '操作确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: targetStatus === 1 ? 'success' : 'warning'
      }
  ).then(async () => {
    // 驳回、封禁、无分类解封时，不传 categoryId
    await updateMerchantStatusApi({ userId: id, status: targetStatus })
    ElMessage.success('操作成功')
    fetchList()
  }).catch(() => {})
}

onMounted(async () => {
  // 先获取分类，再获取商家列表，保证表格里能正确渲染出分类名称
  await fetchCategories()
  fetchList()
})
</script>

<style scoped>
.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 12px;
}
</style>