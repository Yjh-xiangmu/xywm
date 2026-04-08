<template>
  <div class="orders-page">

    <div class="page-header">
      <h2 class="page-title">我的订单</h2>
    </div>

    <!-- 状态筛选标签 -->
    <div class="status-tabs">
      <div
          v-for="tab in tabs"
          :key="tab.value"
          class="tab-item"
          :class="{ active: currentTab === tab.value }"
          @click="currentTab = tab.value"
      >
        {{ tab.label }}
      </div>
    </div>

    <!-- 订单列表 -->
    <el-skeleton :rows="4" animated v-if="loading" style="margin-top:16px;" />

    <div v-else-if="filteredOrders.length === 0" class="empty">
      <div class="empty-icon">📋</div>
      <p class="empty-text">暂无相关订单</p>
    </div>

    <div v-else class="order-list">
      <div
          v-for="order in filteredOrders"
          :key="order.id"
          class="order-card"
      >
        <!-- 订单头部 -->
        <div class="order-header">
          <span class="merchant-name">🏪 {{ order.merchantName }}</span>
          <span class="order-status" :class="statusClass(order.status)">
            {{ order.statusText }}
          </span>
        </div>

        <!-- 菜品列表 -->
        <div class="order-items">
          <div
              v-for="item in order.items"
              :key="item.dishId"
              class="order-item"
          >
            <el-image :src="item.dishImage" class="dish-img" fit="cover">
              <template #error>
                <div class="img-err">🍽️</div>
              </template>
            </el-image>
            <div class="dish-info">
              <span class="dish-name">{{ item.dishName }}</span>
              <span class="dish-price">¥{{ item.price }} × {{ item.quantity }}</span>
            </div>
            <span class="dish-subtotal">
              ¥{{ (parseFloat(item.price) * item.quantity).toFixed(2) }}
            </span>
          </div>
        </div>

        <!-- 订单底部 -->
        <div class="order-footer">
          <div class="order-meta">
            <span class="order-no">订单号：{{ order.orderNo }}</span>
            <span class="order-time">{{ order.createTime }}</span>
          </div>
          <div class="order-total">
            共 {{ totalCount(order) }} 件 &nbsp;实付
            <span class="total-price">¥{{ order.actualAmount }}</span>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="order-actions">
          <!-- 待接单：可以取消 -->
          <el-button
              v-if="order.status === 1"
              size="small"
              plain
              type="danger"
              round
              @click="cancelOrder(order)"
          >取消订单</el-button>

          <!-- 已完成：去评价（暂时占位） -->
          <el-button
              v-if="order.status === 4"
              size="small"
              type="warning"
              plain
              round
              @click="goReview(order)"
          >去评价</el-button>

          <!-- 所有订单都能再次点餐 -->
          <el-button
              size="small"
              type="primary"
              plain
              round
              @click="reorder(order)"
          >再来一单</el-button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const route  = useRoute()

const loading     = ref(false)
const orderList   = ref([])
const currentTab  = ref(-1)  // -1 表示全部

const tabs = [
  { label: '全部',   value: -1 },
  { label: '待接单', value: 1  },
  { label: '已接单', value: 2  },
  { label: '配送中', value: 3  },
  { label: '已完成', value: 4  },
  { label: '已取消', value: 5  },
]

// 过滤订单
const filteredOrders = computed(() => {
  if (currentTab.value === -1) return orderList.value
  return orderList.value.filter(o => o.status === currentTab.value)
})

// 加载订单
const loadOrders = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/order/user')
    orderList.value = res.data || []
  } finally {
    loading.value = false
  }
}

// 订单状态样式
const statusClass = (status) => {
  const map = {
    0: 'status-pending',
    1: 'status-waiting',
    2: 'status-accepted',
    3: 'status-delivering',
    4: 'status-done',
    5: 'status-cancelled',
  }
  return map[status] || ''
}

// 计算总件数
const totalCount = (order) => {
  return (order.items || []).reduce((s, i) => s + i.quantity, 0)
}

// 取消订单
const cancelOrder = async (order) => {
  await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
    type: 'warning',
    confirmButtonText: '确定取消',
    cancelButtonText: '再想想'
  })
  await request.put(`/api/order/${order.id}/status/5`)
  ElMessage.success('订单已取消')
  loadOrders()
}

// 去评价（后续完善）
const goReview = (order) => {
  ElMessage.info('评价功能即将上线')
}

// 再来一单 → 跳转到该商家
const reorder = (order) => {
  router.push(`/user/shop/${order.merchantId}`)
}

onMounted(() => {
  loadOrders()
  // 如果从结算页跳过来带了 orderNo 参数，自动定位到该订单
  if (route.query.orderNo) {
    ElMessage.success(`订单 ${route.query.orderNo} 提交成功！`)
  }
})
</script>

<style scoped>
.orders-page { min-height: 100%; }

.page-header {
  margin-bottom: 16px;
}
.page-title {
  font-size: 20px;
  font-weight: 700;
  color: #303133;
  margin: 0;
}

/* ===== 状态标签 ===== */
.status-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}
.tab-item {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  color: #606266;
  background: white;
  border: 1px solid #e4e7ed;
  cursor: pointer;
  transition: all 0.2s;
}
.tab-item:hover { border-color: #43e97b; color: #38a169; }
.tab-item.active {
  background: linear-gradient(135deg, #43e97b, #38f9d7);
  border-color: transparent;
  color: white;
  font-weight: 600;
}

/* ===== 空状态 ===== */
.empty {
  text-align: center;
  padding: 80px 0;
  color: #909399;
}
.empty-icon { font-size: 56px; margin-bottom: 12px; }
.empty-text { font-size: 15px; margin: 0; }

/* ===== 订单卡片 ===== */
.order-list { display: flex; flex-direction: column; gap: 14px; }
.order-card {
  background: white;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  transition: box-shadow 0.2s;
}
.order-card:hover { box-shadow: 0 4px 18px rgba(0,0,0,0.09); }

/* 订单头部 */
.order-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 18px 10px;
  border-bottom: 1px solid #f5f5f5;
}
.merchant-name {
  font-size: 15px;
  font-weight: 700;
  color: #303133;
}

/* 状态徽标 */
.order-status {
  font-size: 13px;
  font-weight: 600;
  padding: 3px 10px;
  border-radius: 12px;
}
.status-pending    { background: #fdf6ec; color: #e6a23c; }
.status-waiting    { background: #ecf5ff; color: #409eff; }
.status-accepted   { background: #f0f9eb; color: #67c23a; }
.status-delivering { background: #f0f9eb; color: #43e97b; }
.status-done       { background: #f4f4f5; color: #909399; }
.status-cancelled  { background: #fef0f0; color: #f56c6c; }

/* 菜品列表 */
.order-items { padding: 10px 18px; }
.order-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 0;
  border-bottom: 1px solid #f9f9f9;
}
.order-item:last-child { border-bottom: none; }
.dish-img {
  width: 52px;
  height: 52px;
  border-radius: 8px;
  flex-shrink: 0;
  background: #f5f5f5;
}
.img-err {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  background: #f5f7fa;
}
.dish-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 3px;
}
.dish-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.dish-price { font-size: 12px; color: #909399; }
.dish-subtotal {
  font-size: 14px;
  font-weight: 600;
  color: #f5576c;
  flex-shrink: 0;
}

/* 订单底部信息 */
.order-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 18px;
  background: #fafafa;
  border-top: 1px solid #f0f0f0;
}
.order-meta {
  display: flex;
  flex-direction: column;
  gap: 3px;
}
.order-no { font-size: 11px; color: #c0c4cc; }
.order-time { font-size: 12px; color: #909399; }
.order-total {
  font-size: 13px;
  color: #606266;
  text-align: right;
}
.total-price {
  font-size: 17px;
  font-weight: 700;
  color: #f5576c;
  margin-left: 2px;
}

/* 操作按钮区 */
.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 12px 18px;
  border-top: 1px solid #f5f5f5;
}
</style>