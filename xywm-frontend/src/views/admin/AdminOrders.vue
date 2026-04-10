<template>
  <div class="admin-orders">
    <div class="page-header">
      <h2 class="page-title">订单管理</h2>
      <el-input v-model="searchKeyword" placeholder="搜索订单号" clearable
                style="width:260px" :prefix-icon="Search" />
    </div>
    <el-card>
      <div style="margin-bottom:16px;">
        <el-radio-group v-model="statusFilter">
          <el-radio-button :value="-1">全部</el-radio-button>
          <el-radio-button :value="1">待接单</el-radio-button>
          <el-radio-button :value="2">已接单</el-radio-button>
          <el-radio-button :value="3">配送中</el-radio-button>
          <el-radio-button :value="4">已完成</el-radio-button>
          <el-radio-button :value="5">已取消</el-radio-button>
        </el-radio-group>
      </div>
      <el-table :data="filteredList" v-loading="loading" border stripe>
        <el-table-column prop="orderNo" label="订单号" min-width="200" show-overflow-tooltip />
        <el-table-column prop="merchantName" label="商家" width="120" />
        <el-table-column label="菜品" min-width="180">
          <template #default="scope">
            <div v-for="item in scope.row.items" :key="item.dishId"
                 style="font-size:12px;color:#606266;">
              {{ item.dishName }} × {{ item.quantity }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="addressSnapshot" label="收货地址" min-width="180" show-overflow-tooltip />
        <el-table-column label="总价/实付" width="120" align="center">
          <template #default="scope">
            <div style="font-size:12px;color:#909399;" v-if="scope.row.totalAmount !== scope.row.actualAmount">
              原价 ¥{{ scope.row.totalAmount }}
            </div>
            <span style="color:#f5576c;font-weight:bold;">¥{{ scope.row.actualAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="scope">
            <el-tag :type="statusTagType(scope.row.status)" size="small">
              {{ scope.row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="165" align="center" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const orderList = ref([])
const searchKeyword = ref('')
const statusFilter = ref(-1)

const filteredList = computed(() => {
  let list = orderList.value
  if (statusFilter.value !== -1) list = list.filter(o => o.status === statusFilter.value)
  if (searchKeyword.value) list = list.filter(o => o.orderNo?.includes(searchKeyword.value))
  return list
})

const statusTagType = (status) => {
  const map = { 1: 'warning', 2: 'primary', 3: '', 4: 'success', 5: 'info' }
  return map[status] || 'info'
}

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/admin/order/list')
    orderList.value = res.data || []
  } finally {
    loading.value = false
  }
}

onMounted(() => loadOrders())
</script>

<style scoped>
.page-header { display:flex; align-items:center; justify-content:space-between; margin-bottom:20px; }
.page-title { font-size:20px; font-weight:700; color:#303133; margin:0; }
</style>