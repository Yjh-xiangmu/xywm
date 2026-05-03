<template>
  <div class="cart-page" v-loading="loading">

    <!-- 空购物车 -->
    <div v-if="!loading && cartGroups.length === 0" class="empty-cart">
      <div class="empty-icon">🛒</div>
      <p class="empty-text">购物车还是空的</p>
      <p class="empty-sub">去首页逛逛，挑点好吃的吧</p>
      <el-button type="primary" round @click="router.push('/user/home')" style="margin-top:20px;">
        去点餐
      </el-button>
    </div>

    <template v-else>
      <!-- 按商家分组展示 -->
      <div
          v-for="group in cartGroups"
          :key="group.merchantId"
          class="merchant-group"
      >
        <!-- 商家标题栏 -->
        <div class="merchant-header">
          <el-checkbox
              v-model="group.checked"
              @change="(val) => toggleMerchant(group, val)"
          />
          <span class="merchant-name">🏪 {{ group.merchantName }}</span>
          <el-button
              type="danger"
              link
              size="small"
              @click="clearMerchantCart(group)"
              style="margin-left:auto;"
          >
            清空
          </el-button>
        </div>

        <!-- 菜品列表 -->
        <div
            v-for="item in group.items"
            :key="item.id"
            class="cart-item"
        >
          <el-checkbox v-model="item.checked" @change="syncMerchantCheck(group)" />
          <el-image :src="item.dishImage" class="item-img" fit="cover">
            <template #error>
              <div class="img-err">🍽️</div>
            </template>
          </el-image>
          <div class="item-info">
            <p class="item-name">{{ item.dishName }}</p>
            <p class="item-price">¥ {{ item.price }}</p>
          </div>
          <div class="item-stepper">
            <button class="step-btn minus" @click="decrease(item)">
              <el-icon><Minus /></el-icon>
            </button>
            <span class="step-num">{{ item.quantity }}</span>
            <button class="step-btn plus" @click="increase(item)">
              <el-icon><Plus /></el-icon>
            </button>
          </div>
        </div>
      </div>

      <!-- 底部结算栏 -->
      <div class="checkout-bar">
        <el-checkbox v-model="allChecked" @change="toggleAll">全选</el-checkbox>
        <div class="bar-right">
          <div class="total-info">
            <span class="total-label">合计：</span>
            <span class="total-price">¥ {{ selectedTotal }}</span>
          </div>
          <el-button
              class="checkout-btn"
              :disabled="selectedCount === 0"
              @click="goCheckout"
          >
            结算 ({{ selectedCount }})
          </el-button>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getCartApi,
  updateCartQuantityApi,
  removeCartItemApi
} from '@/api/user'

const router = useRouter()
const route  = useRoute()
const loading = ref(false)
// cartGroups: [{ merchantId, merchantName, checked, items: [{...cartVO, checked}] }]
const cartGroups = ref([])

// ======= 加载购物车 =======
const loadCart = async () => {
  loading.value = true
  try {
    const res = await getCartApi()
    const raw = res.data || []
    buildGroups(raw)
  } finally {
    loading.value = false
  }
}

const buildGroups = (raw) => {
  const map = {}
  raw.forEach(item => {
    if (!map[item.merchantId]) {
      map[item.merchantId] = {
        merchantId: item.merchantId,
        merchantName: item.merchantName || '未知商家',
        checked: false,
        items: []
      }
    }
    map[item.merchantId].items.push({ ...item, checked: false })
  })
  cartGroups.value = Object.values(map)
}

// ======= 全选 / 商家选 =======
const allChecked = computed({
  get: () => cartGroups.value.length > 0 && cartGroups.value.every(g => g.checked),
  set: () => {}
})

const toggleAll = (val) => {
  cartGroups.value.forEach(g => {
    g.checked = val
    g.items.forEach(i => { i.checked = val })
  })
}

const toggleMerchant = (group, val) => {
  group.items.forEach(i => { i.checked = val })
}

const syncMerchantCheck = (group) => {
  group.checked = group.items.every(i => i.checked)
}

// ======= 已选统计 =======
const selectedItems = computed(() =>
    cartGroups.value.flatMap(g => g.items.filter(i => i.checked))
)
const selectedCount = computed(() =>
    selectedItems.value.reduce((s, i) => s + i.quantity, 0)
)
const selectedTotal = computed(() =>
    selectedItems.value
        .reduce((s, i) => s + parseFloat(i.price) * i.quantity, 0)
        .toFixed(2)
)

// ======= 数量加减 =======
const increase = async (item) => {
  item.quantity++
  await updateCartQuantityApi(item.id, item.quantity).catch(() => { item.quantity-- })
}

const decrease = async (item) => {
  if (item.quantity === 1) {
    await removeItem(item)
  } else {
    item.quantity--
    await updateCartQuantityApi(item.id, item.quantity).catch(() => { item.quantity++ })
  }
}

const removeItem = async (item) => {
  await removeCartItemApi(item.id)
  cartGroups.value.forEach(g => {
    g.items = g.items.filter(i => i.id !== item.id)
  })
  cartGroups.value = cartGroups.value.filter(g => g.items.length > 0)
}

// ======= 清空某商家购物车 =======
const clearMerchantCart = async (group) => {
  await ElMessageBox.confirm(`确定清空 ${group.merchantName} 的购物车吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '清空',
    cancelButtonText: '取消'
  })
  for (const item of group.items) {
    await removeCartItemApi(item.id).catch(() => {})
  }
  cartGroups.value = cartGroups.value.filter(g => g.merchantId !== group.merchantId)
  ElMessage.success('已清空')
}

// ======= 去结算 =======
const goCheckout = () => {
  if (selectedCount.value === 0) {
    ElMessage.warning('请先选择要结算的商品')
    return
  }

  // 检查是否跨商家
  const selectedGroups = cartGroups.value.filter(g => g.items.some(i => i.checked))
  if (selectedGroups.length > 1) {
    ElMessage.warning('暂不支持跨商家结算，请只选择一家商家的菜品')
    return
  }

  const group = selectedGroups[0]
  const items = group.items.filter(i => i.checked)

  // 把选中的商品信息存到 sessionStorage，结算页读取
  const orderData = {
    merchantId: group.merchantId,
    merchantName: group.merchantName,
    items: items.map(i => ({
      cartId: i.id,
      dishId: i.dishId,
      dishName: i.dishName,
      dishImage: i.dishImage,
      price: i.price,
      quantity: i.quantity
    })),
    totalPrice: selectedTotal.value
  }
  sessionStorage.setItem('pendingOrder', JSON.stringify(orderData))
  router.push('/user/checkout')
}

onMounted(() => {
  loadCart()
})
</script>

<style scoped>
.cart-page {
  min-height: 100%;
  padding-bottom: 80px;
}

/* ===== 空购物车 ===== */
.empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-top: 120px;
}
.empty-icon { font-size: 72px; margin-bottom: 16px; }
.empty-text {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px;
}
.empty-sub { font-size: 14px; color: #909399; margin: 0; }

/* ===== 商家分组 ===== */
.merchant-group {
  background: white;
  border-radius: 14px;
  margin-bottom: 14px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0,0,0,0.04);
}

.merchant-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 16px;
  background: #f9fafb;
  border-bottom: 1px solid #f0f0f0;
}
.merchant-name {
  font-size: 15px;
  font-weight: 700;
  color: #303133;
}

/* ===== 购物车单品 ===== */
.cart-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border-bottom: 1px solid #f5f5f5;
  transition: background 0.2s;
}
.cart-item:last-child { border-bottom: none; }
.cart-item:hover { background: #fafafa; }

.item-img {
  width: 72px;
  height: 72px;
  border-radius: 10px;
  flex-shrink: 0;
  background: #f5f5f5;
}
.img-err {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  background: #f5f7fa;
}

.item-info {
  flex: 1;
  min-width: 0;
}
.item-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.item-price {
  font-size: 16px;
  font-weight: 700;
  color: #f5576c;
  margin: 0;
}

/* ===== 步进器 ===== */
.item-stepper {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}
.step-btn {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  transition: all 0.2s;
}
.step-btn.minus {
  background: #f0f0f0;
  color: #606266;
}
.step-btn.minus:hover { background: #e0e0e0; }
.step-btn.plus {
  background: linear-gradient(135deg, #43e97b, #38f9d7);
  color: white;
  box-shadow: 0 2px 6px rgba(67,233,123,0.4);
}
.step-btn.plus:hover { transform: scale(1.1); }
.step-num {
  font-size: 16px;
  font-weight: 700;
  min-width: 24px;
  text-align: center;
  color: #303133;
}

/* ===== 底部结算栏 ===== */
.checkout-bar {
  position: fixed;
  bottom: 0;
  left: 220px; /* 侧边栏宽度 */
  right: 0;
  height: 64px;
  background: white;
  border-top: 1px solid #eee;
  display: flex;
  align-items: center;
  padding: 0 24px;
  box-shadow: 0 -4px 16px rgba(0,0,0,0.06);
  z-index: 100;
}
.bar-right {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 20px;
}
.total-label {
  font-size: 14px;
  color: #606266;
}
.total-price {
  font-size: 22px;
  font-weight: 700;
  color: #f5576c;
}
.checkout-btn {
  height: 42px;
  padding: 0 28px;
  border-radius: 21px;
  font-size: 15px;
  font-weight: 600;
  background: linear-gradient(135deg, #43e97b, #38f9d7);
  border: none;
  color: white;
  cursor: pointer;
  transition: all 0.2s;
  letter-spacing: 1px;
}
.checkout-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 14px rgba(67,233,123,0.45);
}
.checkout-btn:disabled {
  background: #c0c4cc;
  cursor: not-allowed;
}
</style>