<template>
  <div class="user-manage">
    <div class="page-header">
      <h2 class="page-title">用户管理</h2>
      <el-input v-model="searchKeyword" placeholder="搜索用户名 / 昵称 / 手机号" clearable style="width: 260px" :prefix-icon="Search" />
    </div>
    <el-card>
      <div style="margin-bottom: 16px;">
        <el-radio-group v-model="roleFilter">
          <el-radio-button :value="-1">全部</el-radio-button>
          <el-radio-button :value="2">学生用户</el-radio-button>
          <el-radio-button :value="1">商家</el-radio-button>
          <el-radio-button :value="0">管理员</el-radio-button>
        </el-radio-group>
      </div>
      <el-table :data="filteredList" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称/店铺名" min-width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column label="角色" width="100" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.role === 0" type="danger">管理员</el-tag>
            <el-tag v-else-if="scope.row.role === 1" type="warning">商家</el-tag>
            <el-tag v-else type="success">学生</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 1" type="success">正常</el-tag>
            <el-tag v-else-if="scope.row.status === 2" type="warning">待审核</el-tag>
            <el-tag v-else type="danger">已封禁</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="170" align="center" />
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="scope">
            <template v-if="scope.row.role !== 0">
              <el-button v-if="scope.row.status === 1" type="danger" link @click="toggleStatus(scope.row, 0)">封禁</el-button>
              <el-button v-else-if="scope.row.status === 0" type="success" link @click="toggleStatus(scope.row, 1)">解封</el-button>
              <el-button type="info" link @click="viewDetail(scope.row)">详情</el-button>
            </template>
            <span v-else style="color:#c0c4cc; font-size:13px;">系统账号</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog title="用户详情" v-model="detailVisible" width="440px">
      <el-descriptions :column="1" border v-if="currentUser">
        <el-descriptions-item label="ID">{{ currentUser.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ currentUser.nickname }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentUser.phone || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag v-if="currentUser.role === 0" type="danger">管理员</el-tag>
          <el-tag v-else-if="currentUser.role === 1" type="warning">商家</el-tag>
          <el-tag v-else type="success">学生</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="currentUser.status === 1" type="success">正常</el-tag>
          <el-tag v-else-if="currentUser.status === 2" type="warning">待审核</el-tag>
          <el-tag v-else type="danger">已封禁</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ currentUser.createTime }}</el-descriptions-item>
        <el-descriptions-item label="营业执照" v-if="currentUser.role === 1">
          <el-image v-if="currentUser.licenseUrl" :src="currentUser.licenseUrl" style="width:120px;height:80px;border-radius:6px;" fit="cover" :preview-src-list="[currentUser.licenseUrl]" preview-teleported />
          <span v-else style="color:#c0c4cc;">未上传</span>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const userList = ref([])
const searchKeyword = ref('')
const roleFilter = ref(-1)
const detailVisible = ref(false)
const currentUser = ref(null)

const filteredList = computed(() => {
  let list = userList.value
  if (roleFilter.value !== -1) list = list.filter(u => u.role === roleFilter.value)
  if (searchKeyword.value) {
    const kw = searchKeyword.value.toLowerCase()
    list = list.filter(u =>
        (u.username || '').toLowerCase().includes(kw) ||
        (u.nickname  || '').toLowerCase().includes(kw) ||
        (u.phone     || '').includes(kw)
    )
  }
  return list
})

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/admin/user/list')
    userList.value = res.data || []
  } finally {
    loading.value = false
  }
}

const toggleStatus = (user, targetStatus) => {
  const action = targetStatus === 0 ? '封禁' : '解封'
  ElMessageBox.confirm(`确定要${action}用户「${user.nickname || user.username}」吗？`, '提示', {
    type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消'
  }).then(async () => {
    await request.post('/api/admin/user/status', { userId: user.id, status: targetStatus })
    ElMessage.success(`${action}成功`)
    loadUsers()
  }).catch(() => {})
}

const viewDetail = (user) => { currentUser.value = user; detailVisible.value = true }

onMounted(() => loadUsers())
</script>

<style scoped>
.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; }
.page-title { font-size: 20px; font-weight: 700; color: #303133; margin: 0; }
</style>