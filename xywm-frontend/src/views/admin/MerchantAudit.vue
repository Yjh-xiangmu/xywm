<template>
  <div class="manage-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span style="font-weight: bold;">商家管理</span>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" style="width: 100%" border stripe>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="username" label="登录账号" width="130" />
        <el-table-column prop="nickname" label="店铺名称" width="180" />
        <el-table-column prop="phone" label="联系电话" width="140" />

        <el-table-column label="营业执照" align="center" width="120">
          <template #default="scope">
            <el-image
                style="width: 60px; height: 60px; border-radius: 4px; border: 1px solid #ebeef5;"
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

        <el-table-column prop="createTime" label="入驻时间" min-width="170" />

        <el-table-column label="状态" align="center" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 2" type="warning">待审核</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="success">营业中</el-tag>
            <el-tag v-else type="danger">已封禁</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" align="center" fixed="right" width="200">
          <template #default="scope">
            <template v-if="scope.row.status === 2">
              <el-button type="success" size="small" @click="handleStatusChange(scope.row.id, 1, '通过入驻')">通过</el-button>
              <el-button type="danger" size="small" @click="handleStatusChange(scope.row.id, 0, '驳回申请')">驳回</el-button>
            </template>

            <template v-else-if="scope.row.status === 1">
              <el-button type="danger" size="small" plain @click="handleStatusChange(scope.row.id, 0, '封禁该商家')">封禁店铺</el-button>
            </template>

            <template v-else>
              <el-button type="success" size="small" plain @click="handleStatusChange(scope.row.id, 1, '恢复该商家营业')">解封店铺</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMerchantListApi, updateMerchantStatusApi } from '@/api/admin'

const tableData = ref([])
const loading = ref(false)

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getMerchantListApi()
    tableData.value = res.data || []
  } finally {
    loading.value = false
  }
}

const handleStatusChange = (id, targetStatus, actionText) => {
  ElMessageBox.confirm(
      `确定要${actionText}吗？`,
      '操作确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: targetStatus === 1 ? 'success' : 'warning'
      }
  ).then(async () => {
    await updateMerchantStatusApi({ userId: id, status: targetStatus })
    ElMessage.success('操作成功')
    fetchList() // 重新拉取列表刷新页面数据
  }).catch(() => {})
}

onMounted(() => {
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