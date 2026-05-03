<template>
  <div class="checkout-page" v-loading="pageLoading">
    <div class="page-header">
      <el-button circle plain @click="router.back()" class="back-btn">
        <el-icon><ArrowLeft /></el-icon>
      </el-button>
      <h2 class="page-title">确认订单</h2>
    </div>

    <!-- 收货地址 -->
    <div class="section">
      <div class="section-title">📍 收货地址</div>
      <div v-if="selectedAddress" class="address-card selected" @click="showAddressPicker = true">
        <div class="addr-left">
          <p class="addr-name-phone">
            <span class="addr-name">{{ selectedAddress.name }}</span>
            <span class="addr-phone">{{ selectedAddress.phone }}</span>
          </p>
          <p class="addr-detail">{{ selectedAddress.address }}</p>
        </div>
        <el-icon class="addr-arrow"><ArrowRight /></el-icon>
      </div>
      <div v-else class="address-empty" @click="showAddressPicker = true">
        <el-icon><Plus /></el-icon>
        <span>请选择收货地址</span>
      </div>
    </div>

    <!-- 商家 & 菜品 -->
    <div class="section">
      <div class="section-title">🏪 {{ orderData.merchantName }}</div>
      <div v-for="item in orderData.items" :key="item.dishId" class="order-item">
        <el-image :src="item.dishImage" class="dish-img" fit="cover">
          <template #error><div class="img-err">🍽️</div></template>
        </el-image>
        <div class="dish-info">
          <p class="dish-name">{{ item.dishName }}</p>
          <p class="dish-price">¥ {{ item.price }}</p>
        </div>
        <span class="dish-qty">× {{ item.quantity }}</span>
        <span class="dish-subtotal">¥ {{ (parseFloat(item.price) * item.quantity).toFixed(2) }}</span>
      </div>
    </div>

    <!-- 备注 -->
    <div class="section">
      <div class="section-title">📝 备注</div>
      <el-input v-model="remark" type="textarea" :rows="2"
                placeholder="口味偏好、特殊要求等（选填）" maxlength="100" show-word-limit />
    </div>

    <!-- 优惠券 -->
    <div class="section">
      <div class="section-title">🎫 优惠券</div>
      <div v-if="usableCoupons.length === 0" class="no-coupon">
        暂无可用优惠券
        <el-button link type="primary" @click="router.push('/user/home')" style="margin-left:8px;">去领券</el-button>
      </div>
      <div v-else class="coupon-select">
        <div v-for="uc in usableCoupons" :key="uc.id"
             class="coupon-option" :class="{ selected: selectedCouponId === uc.id }"
             @click="toggleCoupon(uc)">
          <div class="co-left">
            <span class="co-amount" v-if="uc.type === 1">-¥{{ uc.discountAmount }}</span>
            <span class="co-amount" v-else>{{ uc.discountRate }}折</span>
          </div>
          <div class="co-info">
            <div class="co-name">{{ uc.name }}</div>
            <div class="co-desc">{{ uc.descText }}</div>
          </div>
          <el-icon v-if="selectedCouponId === uc.id" color="#43e97b" size="20"><CircleCheck /></el-icon>
        </div>
      </div>
    </div>

    <!-- 金额明细 -->
    <div class="section price-section">
      <div class="price-row">
        <span>商品合计</span>
        <span>¥ {{ orderData.totalPrice }}</span>
      </div>
      <div class="price-row" v-if="discountAmount > 0">
        <span style="color:#f5576c;">优惠券减免</span>
        <span style="color:#f5576c;">-¥ {{ discountAmount.toFixed(2) }}</span>
      </div>
      <div class="price-row">
        <span>配送费</span>
        <span class="free">免费</span>
      </div>
      <div class="price-row total-row">
        <span>实付金额</span>
        <span class="total-price">¥ {{ actualPrice }}</span>
      </div>
    </div>

    <!-- 底部按钮 -->
    <div class="submit-bar">
      <div class="submit-total">
        <span>实付：</span>
        <span class="submit-price">¥ {{ actualPrice }}</span>
      </div>
      <el-button class="submit-btn" :loading="submitting" @click="submitOrder">提交订单</el-button>
    </div>

    <!-- 地址选择抽屉 -->
    <el-drawer v-model="showAddressPicker" title="选择收货地址" direction="btt" size="60%">
      <div class="picker-list">
        <div v-for="addr in addressList" :key="addr.id"
             class="picker-item" :class="{ active: selectedAddress?.id === addr.id }"
             @click="selectAddress(addr)">
          <div class="pick-left">
            <p class="pick-name-phone">
              <span class="addr-name">{{ addr.name }}</span>
              <span class="addr-phone">{{ addr.phone }}</span>
              <el-tag v-if="addr.isDefault === 1" size="small" type="success" effect="plain">默认</el-tag>
            </p>
            <p class="addr-detail">{{ addr.address }}</p>
          </div>
          <el-icon v-if="selectedAddress?.id === addr.id" color="#43e97b"><Check /></el-icon>
        </div>
        <div class="picker-add" @click="goAddAddress">
          <el-icon><Plus /></el-icon><span>新增收货地址</span>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAddressListApi } from '@/api/user'
import request from '@/utils/request'

const router = useRouter()

const rawOrder = sessionStorage.getItem('pendingOrder')
const orderData = ref(rawOrder ? JSON.parse(rawOrder) : {
  merchantId: null, merchantName: '', items: [], totalPrice: '0.00'
})

const pageLoading = ref(false)
const submitting = ref(false)
const remark = ref('')
const addressList = ref([])
const selectedAddress = ref(null)
const showAddressPicker = ref(false)
const usableCoupons = ref([])
const selectedCouponId = ref(null)
const selectedCoupon = ref(null)

// 计算优惠后价格
const discountAmount = computed(() => {
  if (!selectedCoupon.value) return 0
  const total = parseFloat(orderData.value.totalPrice)
  const uc = selectedCoupon.value
  if (uc.type === 1) {
    return parseFloat(uc.discountAmount)
  } else {
    return total - total * uc.discountRate / 10
  }
})

const actualPrice = computed(() => {
  const total = parseFloat(orderData.value.totalPrice)
  const result = total - discountAmount.value
  return result > 0 ? result.toFixed(2) : '0.00'
})

const loadAddresses = async () => {
  pageLoading.value = true
  try {
    const res = await getAddressListApi()
    addressList.value = res.data || []
    const def = addressList.value.find(a => a.isDefault === 1)
    selectedAddress.value = def || addressList.value[0] || null
  } finally {
    pageLoading.value = false
  }
}

const loadCoupons = async () => {
  if (!orderData.value.merchantId) return
  try {
    const res = await request.get(`/api/coupon/usable/${orderData.value.merchantId}`)
    usableCoupons.value = (res.data || []).filter(uc => {
      const total = parseFloat(orderData.value.totalPrice)
      return total >= parseFloat(uc.minAmount)
    })
  } catch (e) {}
}

const toggleCoupon = (uc) => {
  if (selectedCouponId.value === uc.id) {
    selectedCouponId.value = null
    selectedCoupon.value = null
  } else {
    selectedCouponId.value = uc.id
    selectedCoupon.value = uc
  }
}

const selectAddress = (addr) => {
  selectedAddress.value = addr
  showAddressPicker.value = false
}

const goAddAddress = () => {
  showAddressPicker.value = false
  router.push('/user/address')
}

const submitOrder = async () => {
  if (!selectedAddress.value) { ElMessage.warning('请先选择收货地址'); return }
  if (!orderData.value.merchantId) { ElMessage.error('订单数据异常'); return }

  submitting.value = true
  try {
    const body = {
      merchantId: orderData.value.merchantId,
      addressId: selectedAddress.value.id,
      remark: remark.value,
      items: orderData.value.items.map(i => ({ dishId: i.dishId, quantity: i.quantity })),
      userCouponId: selectedCouponId.value || null
    }
    const res = await request.post('/api/order', body)
    sessionStorage.removeItem('pendingOrder')
    ElMessage.success('下单成功！')
    router.replace(`/user/orders?orderNo=${res.data}`)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  if (!orderData.value.merchantId) {
    ElMessage.error('订单数据丢失，请返回购物车重新操作')
    router.replace('/user/cart')
    return
  }
  loadAddresses()
  loadCoupons()
})
</script>

<style scoped>
.checkout-page { min-height:100%; padding-bottom:88px; background:#f7f8fc; }
.page-header { display:flex; align-items:center; gap:14px; padding:4px 0 20px; }
.back-btn { flex-shrink:0; }
.page-title { font-size:20px; font-weight:700; color:#303133; margin:0; }
.section { background:white; border-radius:14px; padding:18px 20px; margin-bottom:12px; box-shadow:0 2px 8px rgba(0,0,0,0.04); }
.section-title { font-size:14px; font-weight:700; color:#909399; margin-bottom:14px; letter-spacing:0.5px; }
.address-card { display:flex; align-items:center; cursor:pointer; padding:4px 0; }
.addr-left { flex:1; }
.addr-name-phone { display:flex; align-items:center; gap:10px; margin:0 0 6px; }
.addr-name { font-size:15px; font-weight:700; color:#303133; }
.addr-phone { font-size:13px; color:#606266; }
.addr-detail { font-size:13px; color:#606266; margin:0; }
.addr-arrow { color:#c0c4cc; flex-shrink:0; }
.address-empty { display:flex; align-items:center; gap:8px; color:#909399; cursor:pointer; padding:10px 0; font-size:14px; }
.order-item { display:flex; align-items:center; gap:12px; padding:10px 0; border-bottom:1px solid #f5f5f5; }
.order-item:last-child { border-bottom:none; }
.dish-img { width:60px; height:60px; border-radius:8px; flex-shrink:0; background:#f5f5f5; }
.img-err { width:100%; height:100%; display:flex; align-items:center; justify-content:center; font-size:22px; background:#f5f7fa; }
.dish-info { flex:1; min-width:0; }
.dish-name { font-size:14px; font-weight:600; color:#303133; margin:0 0 4px; }
.dish-price { font-size:13px; color:#909399; margin:0; }
.dish-qty { font-size:13px; color:#606266; flex-shrink:0; }
.dish-subtotal { font-size:15px; font-weight:700; color:#f5576c; flex-shrink:0; min-width:60px; text-align:right; }

/* 优惠券 */
.no-coupon { font-size:14px; color:#c0c4cc; display:flex; align-items:center; }
.coupon-select { display:flex; flex-direction:column; gap:10px; }
.coupon-option {
  display:flex; align-items:center; gap:12px;
  border:2px solid #e4e7ed; border-radius:12px; padding:12px 14px;
  cursor:pointer; transition:all 0.2s;
}
.coupon-option:hover { border-color:#43e97b; }
.coupon-option.selected { border-color:#43e97b; background:#f6fffb; }
.co-left { width:56px; text-align:center; flex-shrink:0; }
.co-amount { font-size:18px; font-weight:800; color:#f5576c; }
.co-info { flex:1; }
.co-name { font-size:14px; font-weight:600; color:#303133; }
.co-desc { font-size:12px; color:#909399; margin-top:2px; }

.price-section {}
.price-row { display:flex; justify-content:space-between; font-size:14px; color:#606266; padding:6px 0; }
.free { color:#43e97b; font-weight:600; }
.total-row { border-top:1px dashed #eee; margin-top:6px; padding-top:12px; font-weight:700; font-size:15px; color:#303133; }
.total-price { color:#f5576c; font-size:18px; }

.submit-bar { position:fixed; bottom:0; left:220px; right:0; height:68px; background:white; border-top:1px solid #eee; display:flex; align-items:center; justify-content:space-between; padding:0 24px; box-shadow:0 -4px 16px rgba(0,0,0,0.06); z-index:100; }
.submit-total { font-size:14px; color:#606266; }
.submit-price { font-size:22px; font-weight:700; color:#f5576c; margin-left:4px; }
.submit-btn { height:44px; padding:0 36px; border-radius:22px; font-size:16px; font-weight:600; background:linear-gradient(135deg,#43e97b,#38f9d7); border:none; color:white; letter-spacing:1px; }

.picker-list { padding:0 4px; }
.picker-item { display:flex; align-items:center; padding:16px 12px; border-radius:12px; cursor:pointer; margin-bottom:8px; border:2px solid transparent; transition:all 0.2s; }
.picker-item:hover { background:#f9fafb; }
.picker-item.active { border-color:#43e97b; background:#f6fffb; }
.pick-left { flex:1; }
.pick-name-phone { display:flex; align-items:center; gap:8px; margin:0 0 6px; }
.picker-add { display:flex; align-items:center; gap:8px; padding:16px 12px; color:#43e97b; font-weight:600; cursor:pointer; border:2px dashed #43e97b; border-radius:12px; justify-content:center; margin-top:8px; }
</style>