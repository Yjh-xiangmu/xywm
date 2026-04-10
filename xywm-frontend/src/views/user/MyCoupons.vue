<template>
  <div class="my-coupons">
    <div class="page-header">
      <h2 class="page-title">我的优惠券</h2>
    </div>

    <div class="tab-bar">
      <div v-for="tab in tabs" :key="tab.value"
           class="tab-item" :class="{ active: currentTab === tab.value }"
           @click="currentTab = tab.value">
        {{ tab.label }}
      </div>
    </div>

    <el-skeleton :rows="3" animated v-if="loading" />

    <div v-else-if="filteredCoupons.length === 0" class="empty">
      <div class="empty-icon">🎫</div>
      <p>暂无优惠券，去商家详情页领取吧！</p>
    </div>

    <div v-else class="coupon-list">
      <div v-for="item in filteredCoupons" :key="item.id"
           class="coupon-card" :class="{ used: item.status !== 0 }">
        <div class="coupon-left">
          <div class="coupon-amount">
            <template v-if="item.type === 1">
              <span class="amount-symbol">¥</span>
              <span class="amount-value">{{ item.discountAmount }}</span>
            </template>
            <template v-else>
              <span class="amount-value">{{ item.discountRate }}</span>
              <span class="amount-symbol">折</span>
            </template>
          </div>
          <div class="coupon-condition">满{{ item.minAmount }}可用</div>
        </div>

        <div class="coupon-divider">
          <div class="notch top" />
          <div class="dashed-line" />
          <div class="notch bottom" />
        </div>

        <div class="coupon-right">
          <div class="coupon-name">{{ item.name }}</div>
          <div class="coupon-merchant">{{ item.merchantName }}</div>
          <div class="coupon-expire" v-if="item.expireTime">
            有效期至 {{ formatDate(item.expireTime) }}
          </div>
          <div class="coupon-expire" v-else>长期有效</div>
          <div class="coupon-type-tag">{{ item.typeText }}</div>
        </div>

        <div class="coupon-status-badge" v-if="item.status !== 0">
          {{ item.statusText }}
        </div>

        <el-button v-if="item.status === 0" class="use-btn"
                   size="small" type="danger"
                   @click="goToShop(item.merchantId)">
          去使用
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'

const router = useRouter()
const loading = ref(false)
const couponList = ref([])
const currentTab = ref(0)

const tabs = [
  { label: '未使用', value: 0 },
  { label: '已使用', value: 1 },
  { label: '已过期', value: 2 },
]

const filteredCoupons = computed(() =>
    couponList.value.filter(c => {
      if (currentTab.value === 2) return c.status === 2 || c.statusText === '已过期'
      return c.status === currentTab.value && c.statusText !== '已过期'
    })
)

const loadCoupons = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/coupon/my')
    couponList.value = res.data || []
  } finally {
    loading.value = false
  }
}

const formatDate = (dt) => {
  if (!dt) return ''
  return String(dt).slice(0, 10)
}

const goToShop = (merchantId) => {
  router.push(`/user/shop/${merchantId}`)
}

onMounted(() => loadCoupons())
</script>

<style scoped>
.my-coupons { min-height:100%; }
.page-header { margin-bottom:16px; }
.page-title { font-size:20px; font-weight:700; color:#303133; margin:0; }

.tab-bar { display:flex; gap:8px; margin-bottom:20px; }
.tab-item { padding:6px 20px; border-radius:20px; font-size:13px; font-weight:500; color:#606266; background:white; border:1px solid #e4e7ed; cursor:pointer; transition:all 0.2s; }
.tab-item.active { background:linear-gradient(135deg,#43e97b,#38f9d7); border-color:transparent; color:white; font-weight:600; }

.empty { text-align:center; padding:80px 0; color:#909399; }
.empty-icon { font-size:56px; margin-bottom:12px; }

.coupon-list { display:flex; flex-direction:column; gap:14px; }

.coupon-card {
  display:flex; align-items:stretch; background:white;
  border-radius:14px; overflow:hidden;
  box-shadow:0 2px 10px rgba(0,0,0,0.05);
  position:relative; height:100px;
}
.coupon-card.used { opacity:0.55; filter:grayscale(30%); }

.coupon-left {
  width:110px; flex-shrink:0;
  background:linear-gradient(135deg,#f5576c,#f093fb);
  display:flex; flex-direction:column;
  align-items:center; justify-content:center; gap:4px;
  padding:0 12px;
}
.coupon-amount { display:flex; align-items:baseline; gap:2px; color:white; }
.amount-value { font-size:32px; font-weight:800; line-height:1; }
.amount-symbol { font-size:14px; font-weight:600; }
.coupon-condition { font-size:11px; color:rgba(255,255,255,0.85); }

.coupon-divider {
  width:20px; flex-shrink:0; position:relative;
  background:white; display:flex; flex-direction:column; align-items:center;
}
.notch { width:18px; height:18px; border-radius:50%; background:#f7f8fc; position:absolute; }
.notch.top { top:-9px; }
.notch.bottom { bottom:-9px; }
.dashed-line { height:100%; border-left:2px dashed #e4e7ed; margin:0 auto; }

.coupon-right {
  flex:1; padding:14px 16px;
  display:flex; flex-direction:column; justify-content:center; gap:3px;
}
.coupon-name { font-size:15px; font-weight:700; color:#303133; }
.coupon-merchant { font-size:12px; color:#909399; }
.coupon-expire { font-size:11px; color:#c0c4cc; }
.coupon-type-tag {
  display:inline-block; font-size:11px; padding:1px 6px;
  border-radius:4px; background:#fff0f5; color:#f5576c;
  border:1px solid #ffd0dc; margin-top:2px; width:fit-content;
}

.coupon-status-badge {
  position:absolute; top:12px; right:14px;
  font-size:12px; font-weight:600; color:#c0c4cc;
  border:1px solid #e4e7ed; border-radius:4px; padding:2px 8px;
}
.use-btn {
  position:absolute; bottom:12px; right:14px;
  border-radius:14px !important;
}
</style>