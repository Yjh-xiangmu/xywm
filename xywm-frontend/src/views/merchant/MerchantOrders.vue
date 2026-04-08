<template>
  <div class="merchant-orders">

    <div class="page-header">
      <h2 class="page-title">订单管理</h2>
      <el-button :icon="Refresh" circle @click="loadOrders" :loading="loading" />
    </div>

    <!-- 状态筛选 -->
    <div class="status-tabs">
      <div
          v-for="tab in tabs"
          :key="tab.value"
          class="tab-item"
          :class="{ active: currentTab === tab.value }"
          @click="currentTab = tab.value"
      >
        {{ tab.label }}
        <span class="tab-count" v-if="tabCount(tab.value) > 0">
          {{ tabCount(tab.value) }}
        </span>
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
          :class="{ 'is-new': order.status === 1 }"
      >
        <!-- 卡片头部 -->
        <div class="order-header">
          <div class="header-left">
            <span class="order-no">订单号：{{ order.orderNo }}</span>
            <span class="order-time">{{ order.createTime }}</span>
          </div>
          <span class="order-status" :class="statusClass(order.status)">
            {{ order.statusText }}
          </span>
        </div>

        <!-- 收货地址 -->
        <div class="address-row" v-if="order.addressSnapshot">
          <el-icon><Location /></el-icon>
          <span>{{ order.addressSnapshot }}</span>
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
              <span class="dish-meta">¥{{ item.price }} × {{ item.quantity }}</span>
            </div>
            <span class="dish-subtotal">
              ¥{{ (parseFloat(item.price) * item.quantity).toFixed(2) }}
            </span>
          </div>
        </div>

        <!-- 备注 -->
        <div class="remark-row" v-if="order.remark">
          <el-icon><ChatDotRound /></el-icon>
          <span>备注：{{ order.remark }}</span>
        </div>

        <!-- 底部金额 + 操作 -->
        <div class="order-footer">
          <div class="order-total">
            共 {{ totalCount(order) }} 件 &nbsp;实付
            <span class="total-price">¥{{ order.actualAmount }}</span>
          </div>
          <div class="order-actions">

            <!-- 待接单：接单 / 拒单 -->
            <template v-if="order.status === 1">
              <el-button
                  size="small"
                  type="danger"
                  plain
                  round
                  @click="updateStatus(order, 5, '拒绝该订单')"
              >拒单</el-button>
              <el-button
                  size="small"
                  type="success"
                  round
                  @click="updateStatus(order, 2, '接受该订单')"
              >接单</el-button>
            </template>

            <!-- 已接单：开始配送 -->
            <el-button
                v-if="order.status === 2"
                size="small"
                type="primary"
                round
                @click="updateStatus(order, 3, '开始配送')"
            >开始配送</el-button>

            <!-- 配送中：确认完成 -->
            <el-button
                v-if="order.status === 3"
                size="small"
                type="success"
                round
                @click="updateStatus(order, 4, '确认完成')"
            >确认完成</el-button>

          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading    = ref(false)
const orderList  = ref([])
const currentTab = ref(-1)

const tabs = [
  { label: '全部',   value: -1 },
  { label: '待接单', value: 1  },
  { label: '已接单', value: 2  },
  { label: '配送中', value: 3  },
  { label: '已完成', value: 4  },
  { label: '已取消', value: 5  },
]

const filteredOrders = computed(() => {
  if (currentTab.value === -1) return orderList.value
  return orderList.value.filter(o => o.status === currentTab.value)
})

// 每个 tab 的订单数量（只显示大于0的）
const tabCount = (status) => {
  if (status === -1) return 0
  return orderList.value.filter(o => o.status === status).length
}

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/order/merchant')
    orderList.value = res.data || []
  } finally {
    loading.value = false
  }
}

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

const totalCount = (order) => {
  return (order.items || []).reduce((s, i) => s + i.quantity, 0)
}

const updateStatus = async (order, targetStatus, actionText) => {
  await ElMessageBox.confirm(
      `确定要${actionText}吗？`,
      '操作确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: targetStatus === 5 ? 'warning' : 'success'
      }
  )
  await request.put(`/api/order/${order.id}/status/${targetStatus}`)
  ElMessage.success('操作成功')
  loadOrders()
}

// 每30秒自动刷新一次，模拟实时接单
let timer = null
onMounted(() => {
  loadOrders()
  timer = setInterval(() => loadOrders(), 30000)
})
onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.merchant-orders { min-height: 100%; }

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
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
  display: flex;
  align-items: center;
  gap: 5px;
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
.tab-item:hover { border-color: #f5576c; color: #f5576c; }
.tab-item.active {
  background: linear-gradient(135deg, #f093fb, #f5576c);
  border-color: transparent;
  color: white;
  font-weight: 600;
}
.tab-count {
  background: #f5576c;
  color: white;
  font-size: 11px;
  font-weight: 700;
  min-width: 18px;
  height: 18px;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 4px;
}
.tab-item.active .tab-count {
  background: rgba(255,255,255,0.35);
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
  border: 2px solid transparent;
  transition: all 0.2s;
}
.order-card:hover { box-shadow: 0 4px 18px rgba(0,0,0,0.09); }
/* 待接单的订单高亮边框 */
.order-card.is-new {
  border-color: #f5576c;
  box-shadow: 0 4px 18px rgba(245,87,108,0.15);
}

/* 头部 */
.order-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 18px 10px;
  border-bottom: 1px solid #f5f5f5;
}
.header-left {
  display: flex;
  flex-direction: column;
  gap: 3px;
}
.order-no { font-size: 12px; color: #c0c4cc; }
.order-time { font-size: 13px; color: #606266; }

/* 状态徽标 */
.order-status {
  font-size: 13px;
  font-weight: 600;
  padding: 4px 12px;
  border-radius: 12px;
}
.status-pending    { background: #fdf6ec; color: #e6a23c; }
.status-waiting    { background: #fef0f0; color: #f5576c; }
.status-accepted   { background: #ecf5ff; color: #409eff; }
.status-delivering { background: #f0f9eb; color: #67c23a; }
.status-done       { background: #f4f4f5; color: #909399; }
.status-cancelled  { background: #f4f4f5; color: #c0c4cc; }

/* 收货地址 */
.address-row {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 18px;
  font-size: 13px;
  color: #606266;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}

/* 菜品列表 */
.order-items { padding: 8px 18px; }
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
.dish-meta { font-size: 12px; color: #909399; }
.dish-subtotal {
  font-size: 14px;
  font-weight: 600;
  color: #f5576c;
  flex-shrink: 0;
}

/* 备注 */
.remark-row {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 18px;
  font-size: 13px;
  color: #e6a23c;
  background: #fdf6ec;
  border-top: 1px solid #faecd8;
}

/* 底部 */
.order-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 18px;
  border-top: 1px solid #f0f0f0;
  background: #fafafa;
}
.order-total {
  font-size: 13px;
  color: #606266;
}
.total-price {
  font-size: 18px;
  font-weight: 700;
  color: #f5576c;
}
.order-actions {
  display: flex;
  gap: 10px;
}
</style>