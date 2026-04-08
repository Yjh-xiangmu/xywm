<template>
  <div class="shop-detail" v-loading="pageLoading">

    <!-- 商家头部信息 -->
    <div class="shop-header" v-if="shopInfo">
      <div class="shop-cover">
        <el-image
            :src="shopInfo.avatar || shopInfo.licenseUrl || 'https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png'"
            fit="cover"
            class="cover-img"
        />
        <div class="cover-overlay" />
        <div class="shop-header-content">
          <el-button class="back-btn" circle @click="router.back()">
            <el-icon><ArrowLeft /></el-icon>
          </el-button>
          <div class="shop-title-area">
            <h1 class="shop-name">{{ shopInfo.nickname }}</h1>
            <div class="shop-meta-row">
              <span class="meta-item"><span class="star">⭐</span> 4.9 (800+评价)</span>
              <span class="meta-divider">·</span>
              <span class="meta-item">📞 {{ shopInfo.phone || '暂无联系方式' }}</span>
              <span class="meta-divider">·</span>
              <el-tag size="small" effect="dark" class="open-tag">营业中</el-tag>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 主体：左侧分类 + 右侧菜品 -->
    <div class="shop-body">

      <!-- 左侧分类导航 -->
      <aside class="category-sidebar">
        <div
            v-for="group in dishGroups"
            :key="group.categoryId"
            class="cat-item"
            :class="{ active: activeCategoryId === group.categoryId }"
            @click="scrollToCategory(group.categoryId)"
        >
          <span class="cat-name">{{ group.categoryName }}</span>
          <span class="cat-count">{{ group.dishes.length }}</span>
        </div>
      </aside>

      <!-- 右侧菜品列表 -->
      <main class="dish-main" ref="dishMainRef" @scroll="handleScroll">
        <div
            v-for="group in dishGroups"
            :key="group.categoryId"
            :id="`cat-${group.categoryId}`"
            class="dish-group"
        >
          <div class="group-title">
            <span class="group-name">{{ group.categoryName }}</span>
            <span class="group-count">共 {{ group.dishes.length }} 款</span>
          </div>

          <div
              v-for="dish in group.dishes"
              :key="dish.id"
              class="dish-card"
          >
            <el-image :src="dish.image" class="dish-img" fit="cover">
              <template #error>
                <div class="img-placeholder">🍽️</div>
              </template>
            </el-image>
            <div class="dish-info">
              <h3 class="dish-name">{{ dish.name }}</h3>
              <p class="dish-desc" v-if="dish.description">{{ dish.description }}</p>
              <div class="dish-footer">
                <span class="dish-price">
                  <span class="price-symbol">¥</span>{{ dish.price }}
                </span>
                <div class="cart-control">
                  <transition name="count-fade">
                    <div v-if="getCartCount(dish.id) > 0" class="stepper">
                      <button class="stepper-btn minus" @click="decreaseCart(dish)">
                        <el-icon><Minus /></el-icon>
                      </button>
                      <span class="stepper-count">{{ getCartCount(dish.id) }}</span>
                    </div>
                  </transition>
                  <button class="add-btn" @click="addDishToCart(dish)">
                    <el-icon><Plus /></el-icon>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 底部空白，防止最后一组无法滚到顶 -->
        <div style="height: 200px;" />
      </main>
    </div>

    <!-- 底部购物车浮动栏 -->
    <transition name="slide-up">
      <div class="cart-bar" v-if="cartTotalCount > 0" @click="goToCart">
        <div class="cart-bar-left">
          <div class="cart-icon-wrapper">
            <el-icon class="cart-icon"><ShoppingCart /></el-icon>
            <span class="cart-badge">{{ cartTotalCount }}</span>
          </div>
          <div class="cart-bar-info">
            <span class="cart-bar-total">¥ {{ cartTotalPrice }}</span>
            <span class="cart-bar-hint">已选 {{ cartTotalCount }} 件</span>
          </div>
        </div>
        <div class="cart-bar-action">
          去结算 →
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getShopDetailApi, getGroupedDishesApi, addToCartApi, updateCartQuantityApi, removeCartItemApi, getCartApi } from '@/api/user'

const route = useRoute()
const router = useRouter()
const merchantId = route.params.merchantId

const pageLoading = ref(false)
const shopInfo = ref(null)
const dishGroups = ref([])
const cartItems = ref([])        // 购物车原始数据 [{id, dishId, quantity, ...}]
const activeCategoryId = ref(null)
const dishMainRef = ref(null)
const isScrolling = ref(false)   // 防止点击分类时触发 scroll 监听

// ======= 初始化 =======
onMounted(async () => {
  pageLoading.value = true
  try {
    const [shopRes, dishRes, cartRes] = await Promise.all([
      getShopDetailApi(merchantId),
      getGroupedDishesApi(merchantId),
      getCartApi()
    ])
    shopInfo.value = shopRes.data
    dishGroups.value = dishRes.data || []
    cartItems.value = cartRes.data || []

    if (dishGroups.value.length > 0) {
      activeCategoryId.value = dishGroups.value[0].categoryId
    }
  } catch (e) {
    ElMessage.error('加载失败，请重试')
  } finally {
    pageLoading.value = false
  }
})

// ======= 购物车计算 =======
const getCartCount = (dishId) => {
  const item = cartItems.value.find(c => c.dishId === dishId)
  return item ? item.quantity : 0
}

const cartTotalCount = computed(() =>
    cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
)

const cartTotalPrice = computed(() => {
  return cartItems.value.reduce((sum, item) => {
    return sum + (parseFloat(item.price || 0) * item.quantity)
  }, 0).toFixed(2)
})

// ======= 加购物车 =======
const addDishToCart = async (dish) => {
  try {
    await addToCartApi({ dishId: dish.id, quantity: 1 })
    const existing = cartItems.value.find(c => c.dishId === dish.id)
    if (existing) {
      existing.quantity++
    } else {
      // 乐观更新本地状态
      cartItems.value.push({
        id: null,         // 真实 id 下次刷新时获取
        dishId: dish.id,
        quantity: 1,
        price: dish.price
      })
    }
  } catch (e) {
    // 错误已在拦截器提示
  }
}

// ======= 减少数量 =======
const decreaseCart = async (dish) => {
  const item = cartItems.value.find(c => c.dishId === dish.id)
  if (!item) return

  if (item.quantity <= 1) {
    // 需要真实 cartId，先刷新一次购物车
    await refreshCart()
    const freshItem = cartItems.value.find(c => c.dishId === dish.id)
    if (freshItem && freshItem.id) {
      await removeCartItemApi(freshItem.id)
      cartItems.value = cartItems.value.filter(c => c.dishId !== dish.id)
    }
  } else {
    item.quantity--
    // 如果有真实 cartId 就同步后端
    if (item.id) {
      await updateCartQuantityApi(item.id, item.quantity).catch(() => {})
    } else {
      await refreshCart()
    }
  }
}

const refreshCart = async () => {
  const res = await getCartApi()
  cartItems.value = res.data || []
}

// ======= 分类滚动 =======
const scrollToCategory = async (categoryId) => {
  activeCategoryId.value = categoryId
  isScrolling.value = true
  await nextTick()
  const el = document.getElementById(`cat-${categoryId}`)
  if (el && dishMainRef.value) {
    dishMainRef.value.scrollTo({ top: el.offsetTop - 12, behavior: 'smooth' })
  }
  setTimeout(() => { isScrolling.value = false }, 600)
}

const handleScroll = () => {
  if (isScrolling.value || !dishMainRef.value) return
  const scrollTop = dishMainRef.value.scrollTop
  for (const group of dishGroups.value) {
    const el = document.getElementById(`cat-${group.categoryId}`)
    if (el && el.offsetTop - 60 <= scrollTop) {
      activeCategoryId.value = group.categoryId
    }
  }
}

// ======= 去购物车 =======
const goToCart = () => {
  router.push(`/user/cart?merchantId=${merchantId}`)
}
</script>

<style scoped>
.shop-detail {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #f7f8fc;
  position: relative;
}

/* ===== 商家头部 ===== */
.shop-header { flex-shrink: 0; }
.shop-cover {
  position: relative;
  height: 180px;
  overflow: hidden;
}
.cover-img {
  width: 100%;
  height: 100%;
}
.cover-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to bottom, rgba(0,0,0,0.15) 0%, rgba(0,0,0,0.55) 100%);
}
.shop-header-content {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 14px 20px 18px;
}
.back-btn {
  align-self: flex-start;
  background: rgba(255,255,255,0.2) !important;
  border: none !important;
  color: white !important;
  backdrop-filter: blur(6px);
}
.shop-title-area {}
.shop-name {
  font-size: 24px;
  font-weight: 700;
  color: white;
  margin: 0 0 8px;
  text-shadow: 0 2px 8px rgba(0,0,0,0.3);
}
.shop-meta-row {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.meta-item { color: rgba(255,255,255,0.9); font-size: 13px; }
.meta-divider { color: rgba(255,255,255,0.5); }
.star { color: #ffd700; }
.open-tag {
  background: rgba(67,233,123,0.85) !important;
  border-color: transparent !important;
  backdrop-filter: blur(4px);
}

/* ===== 主体布局 ===== */
.shop-body {
  flex: 1;
  display: flex;
  overflow: hidden;
  background: white;
}

/* ===== 左侧分类 ===== */
.category-sidebar {
  width: 90px;
  flex-shrink: 0;
  background: #f5f6fa;
  overflow-y: auto;
  border-right: 1px solid #eee;
}
.category-sidebar::-webkit-scrollbar { display: none; }
.cat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 3px;
  padding: 14px 6px;
  cursor: pointer;
  border-left: 3px solid transparent;
  transition: all 0.2s;
  text-align: center;
}
.cat-item:hover { background: #eef0ff; }
.cat-item.active {
  background: white;
  border-left-color: #43e97b;
}
.cat-name {
  font-size: 13px;
  color: #606266;
  line-height: 1.4;
  word-break: break-all;
}
.cat-item.active .cat-name {
  color: #38a169;
  font-weight: 600;
}
.cat-count {
  font-size: 11px;
  color: #b0b8c1;
}

/* ===== 右侧菜品 ===== */
.dish-main {
  flex: 1;
  overflow-y: auto;
  padding: 0 16px 16px;
}
.dish-main::-webkit-scrollbar { width: 4px; }
.dish-main::-webkit-scrollbar-thumb { background: #e0e0e0; border-radius: 2px; }

.dish-group { margin-bottom: 8px; }
.group-title {
  display: flex;
  align-items: baseline;
  gap: 10px;
  padding: 16px 0 10px;
  position: sticky;
  top: 0;
  background: white;
  z-index: 2;
}
.group-name {
  font-size: 15px;
  font-weight: 700;
  color: #303133;
}
.group-count {
  font-size: 12px;
  color: #b0b8c1;
}

.dish-card {
  display: flex;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid #f5f5f5;
  align-items: center;
}
.dish-img {
  width: 88px;
  height: 88px;
  border-radius: 10px;
  flex-shrink: 0;
  background: #f5f5f5;
}
.img-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  background: #f5f7fa;
}
.dish-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.dish-name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.dish-desc {
  font-size: 12px;
  color: #909399;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.dish-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: auto;
}
.dish-price {
  font-size: 18px;
  font-weight: 700;
  color: #f5576c;
}
.price-symbol {
  font-size: 13px;
  font-weight: normal;
}

/* ===== 购物车加减控件 ===== */
.cart-control {
  display: flex;
  align-items: center;
  gap: 6px;
}
.stepper {
  display: flex;
  align-items: center;
  gap: 6px;
}
.stepper-btn {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  transition: all 0.2s;
}
.stepper-btn.minus {
  background: #f0f0f0;
  color: #606266;
}
.stepper-btn.minus:hover { background: #e0e0e0; }
.stepper-count {
  font-size: 15px;
  font-weight: 700;
  color: #303133;
  min-width: 20px;
  text-align: center;
}
.add-btn {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, #43e97b, #38f9d7);
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  transition: all 0.2s;
  box-shadow: 0 3px 8px rgba(67, 233, 123, 0.4);
}
.add-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(67, 233, 123, 0.5);
}

/* ===== 底部购物车栏 ===== */
.cart-bar {
  position: fixed;
  bottom: 24px;
  left: 50%;
  transform: translateX(-50%);
  width: calc(100% - 280px);  /* 280px = 侧边栏宽度 */
  max-width: 680px;
  background: #2d3748;
  border-radius: 50px;
  padding: 12px 14px 12px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 8px 32px rgba(0,0,0,0.25);
  cursor: pointer;
  z-index: 100;
  transition: transform 0.2s;
}
.cart-bar:hover { transform: translateX(-50%) translateY(-2px); }
.cart-bar-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.cart-icon-wrapper {
  position: relative;
  width: 40px;
  height: 40px;
  background: #43e97b;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.cart-icon { font-size: 20px; color: white; }
.cart-badge {
  position: absolute;
  top: -4px;
  right: -4px;
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
.cart-bar-total {
  display: block;
  font-size: 18px;
  font-weight: 700;
  color: white;
}
.cart-bar-hint {
  display: block;
  font-size: 12px;
  color: rgba(255,255,255,0.6);
}
.cart-bar-action {
  background: linear-gradient(135deg, #43e97b, #38f9d7);
  color: white;
  font-size: 15px;
  font-weight: 600;
  padding: 8px 20px;
  border-radius: 30px;
  letter-spacing: 1px;
}

/* ===== 动画 ===== */
.count-fade-enter-active, .count-fade-leave-active { transition: all 0.2s; }
.count-fade-enter-from, .count-fade-leave-to { opacity: 0; transform: scale(0.8); }
.slide-up-enter-active, .slide-up-leave-active { transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); }
.slide-up-enter-from, .slide-up-leave-to { opacity: 0; transform: translateX(-50%) translateY(20px); }
</style>