<template>
  <div class="admin-stats" v-loading="loading">
    <div class="page-header">
      <h2 class="page-title">平台数据统计</h2>
      <el-button :icon="Refresh" round @click="loadStats" :loading="loading">刷新</el-button>
    </div>
    <div class="stat-cards">
      <div class="stat-card" style="background:linear-gradient(135deg,#667eea,#764ba2)">
        <div class="card-icon">🏪</div>
        <div class="card-body">
          <p class="card-label">入驻商家</p>
          <p class="card-value">{{ stats.merchantCount ?? 0 }}</p>
          <p class="card-hint">家（营业中 {{ stats.activeMerchant ?? 0 }} 家）</p>
        </div>
      </div>
      <div class="stat-card" style="background:linear-gradient(135deg,#43e97b,#38f9d7)">
        <div class="card-icon">🎓</div>
        <div class="card-body">
          <p class="card-label">注册学生</p>
          <p class="card-value">{{ stats.studentCount ?? 0 }}</p>
          <p class="card-hint">人</p>
        </div>
      </div>
      <div class="stat-card" style="background:linear-gradient(135deg,#f5576c,#f093fb)">
        <div class="card-icon">📦</div>
        <div class="card-body">
          <p class="card-label">平台总订单</p>
          <p class="card-value">{{ stats.totalOrders ?? 0 }}</p>
          <p class="card-hint">单（今日 {{ stats.todayOrders ?? 0 }} 单）</p>
        </div>
      </div>
      <div class="stat-card" style="background:linear-gradient(135deg,#f6d365,#fda085)">
        <div class="card-icon">💰</div>
        <div class="card-body">
          <p class="card-label">平台总营业额</p>
          <p class="card-value">{{ stats.totalSales ?? '0.00' }}</p>
          <p class="card-hint">元（已完成订单）</p>
        </div>
      </div>
    </div>
    <div class="charts-row">
      <div class="chart-card">
        <div class="chart-title">近7天平台订单量趋势</div>
        <div ref="lineChartRef" class="chart-box" />
      </div>
      <div class="chart-card">
        <div class="chart-title">商家分类分布</div>
        <div ref="pieChartRef" class="chart-box" />
      </div>
    </div>
    <div class="section-card" v-if="stats.pendingMerchants && stats.pendingMerchants.length > 0">
      <div class="section-title">
        ⚠️ 待审核商家
        <el-tag type="warning" size="small" style="margin-left:8px;">{{ stats.pendingMerchants.length }} 家</el-tag>
      </div>
      <div class="pending-list">
        <div v-for="m in stats.pendingMerchants" :key="m.id" class="pending-item">
          <span class="pending-name">{{ m.nickname }}</span>
          <span class="pending-time">{{ m.createTime }}</span>
          <el-button size="small" type="primary" round @click="router.push('/admin/merchants')">去审核</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { Refresh } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const loading = ref(false)
const stats = ref({})
const lineChartRef = ref(null)
const pieChartRef = ref(null)

const loadStats = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/admin/stats')
    stats.value = res.data || {}
    await nextTick()
    renderCharts()
  } finally {
    loading.value = false
  }
}

const loadECharts = () => new Promise((resolve) => {
  if (window.echarts) { resolve(window.echarts); return }
  const script = document.createElement('script')
  script.src = 'https://cdnjs.cloudflare.com/ajax/libs/echarts/5.4.3/echarts.min.js'
  script.onload = () => resolve(window.echarts)
  document.head.appendChild(script)
})

const renderCharts = async () => {
  const echarts = await loadECharts()
  if (lineChartRef.value) {
    const line = echarts.init(lineChartRef.value)
    line.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: 40, right: 20, top: 20, bottom: 30 },
      xAxis: { type: 'category', data: stats.value.days || [], axisLine: { lineStyle: { color: '#e4e7ed' } }, axisTick: { show: false }, axisLabel: { color: '#909399', fontSize: 12 } },
      yAxis: { type: 'value', axisLabel: { color: '#909399', fontSize: 12 }, splitLine: { lineStyle: { color: '#f0f0f0' } }, minInterval: 1 },
      series: [{ data: stats.value.dailyOrders || [], type: 'bar', barMaxWidth: 36, itemStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: '#667eea' }, { offset: 1, color: '#764ba2' }] }, borderRadius: [6,6,0,0] } }]
    })
  }
  if (pieChartRef.value) {
    const dist = stats.value.categoryDist || {}
    const pieData = Object.entries(dist).filter(([,v]) => v > 0).map(([name, value]) => ({ name, value }))
    const pie = echarts.init(pieChartRef.value)
    pie.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c}家 ({d}%)' },
      legend: { bottom: 0, itemWidth: 10, itemHeight: 10, textStyle: { fontSize: 12 } },
      color: ['#667eea','#43e97b','#f5576c','#f6d365','#909399'],
      series: [{ type: 'pie', radius: ['38%','65%'], center: ['50%','44%'], data: pieData.length ? pieData : [{ name: '暂无数据', value: 1 }], label: { show: false }, emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0,0,0,0.2)' } } }]
    })
  }
}

onMounted(() => loadStats())
</script>

<style scoped>
.admin-stats { min-height: 100%; }
.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px; }
.page-title { font-size: 20px; font-weight: 700; color: #303133; margin: 0; }
.stat-cards { display: grid; grid-template-columns: repeat(4,1fr); gap: 16px; margin-bottom: 20px; }
.stat-card { border-radius: 16px; padding: 20px; display: flex; align-items: center; gap: 16px; box-shadow: 0 4px 16px rgba(0,0,0,0.07); transition: transform 0.2s; }
.stat-card:hover { transform: translateY(-3px); }
.card-icon { font-size: 36px; flex-shrink: 0; }
.card-label { font-size: 12px; color: rgba(255,255,255,0.8); margin: 0 0 4px; }
.card-value { font-size: 28px; font-weight: 800; color: white; margin: 0 0 2px; line-height: 1; }
.card-hint { font-size: 12px; color: rgba(255,255,255,0.7); margin: 0; }
.charts-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 20px; }
.chart-card { background: white; border-radius: 16px; padding: 20px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }
.chart-title { font-size: 15px; font-weight: 700; color: #303133; margin-bottom: 14px; }
.chart-box { height: 220px; }
.section-card { background: white; border-radius: 16px; padding: 20px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }
.section-title { font-size: 15px; font-weight: 700; color: #303133; margin-bottom: 14px; }
.pending-list { display: flex; flex-direction: column; gap: 10px; }
.pending-item { display: flex; align-items: center; gap: 12px; padding: 12px 16px; background: #fdf6ec; border-radius: 10px; border-left: 3px solid #e6a23c; }
.pending-name { font-size: 14px; font-weight: 600; color: #303133; flex: 1; }
.pending-time { font-size: 12px; color: #909399; }
</style>