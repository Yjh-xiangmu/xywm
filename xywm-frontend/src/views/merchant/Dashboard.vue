<template>
  <div class="dashboard" v-loading="loading">

    <div class="page-header">
      <div>
        <h2 class="page-title">营业概览</h2>
        <p class="page-sub">{{ today }} · 实时数据</p>
      </div>
      <el-button :icon="Refresh" round @click="loadStats" :loading="loading">刷新</el-button>
    </div>

    <!-- 四个核心指标卡片 -->
    <div class="stat-cards">
      <div class="stat-card card-today-order">
        <div class="card-icon">📦</div>
        <div class="card-body">
          <p class="card-label">今日订单</p>
          <p class="card-value">{{ stats.todayCount ?? 0 }}</p>
          <p class="card-hint">单</p>
        </div>
      </div>

      <div class="stat-card card-today-sales">
        <div class="card-icon">💰</div>
        <div class="card-body">
          <p class="card-label">今日营业额</p>
          <p class="card-value">{{ stats.todaySales ?? '0.00' }}</p>
          <p class="card-hint">元</p>
        </div>
      </div>

      <div class="stat-card card-pending">
        <div class="card-icon">⏳</div>
        <div class="card-body">
          <p class="card-label">待处理订单</p>
          <p class="card-value">{{ stats.pendingCount ?? 0 }}</p>
          <p class="card-hint">单待接单</p>
        </div>
        <div class="pending-badge" v-if="stats.pendingCount > 0">
          {{ stats.pendingCount }}
        </div>
      </div>

      <div class="stat-card card-total">
        <div class="card-icon">🏆</div>
        <div class="card-body">
          <p class="card-label">累计完成营业额</p>
          <p class="card-value">{{ stats.totalSales ?? '0.00' }}</p>
          <p class="card-hint">元（共 {{ stats.totalCount ?? 0 }} 单）</p>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-row">
      <!-- 近7天营业额折线图 -->
      <div class="chart-card">
        <div class="chart-title">近7天营业额趋势</div>
        <div ref="lineChartRef" class="chart-box" />
      </div>

      <!-- 订单状态分布饼图 -->
      <div class="chart-card chart-card-sm">
        <div class="chart-title">订单状态分布</div>
        <div ref="pieChartRef" class="chart-box" />
      </div>
    </div>

    <!-- 快捷入口 -->
    <div class="quick-actions">
      <div class="qa-title">快捷操作</div>
      <div class="qa-list">
        <div class="qa-item" @click="router.push('/merchant/orders')">
          <span class="qa-icon">📋</span>
          <span class="qa-label">去接单</span>
          <el-badge :value="stats.pendingCount" :hidden="!stats.pendingCount" />
        </div>
        <div class="qa-item" @click="router.push('/merchant/dishes')">
          <span class="qa-icon">🍜</span>
          <span class="qa-label">管理菜品</span>
        </div>
        <div class="qa-item" @click="router.push('/merchant/category')">
          <span class="qa-icon">📂</span>
          <span class="qa-label">管理分类</span>
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
const stats   = ref({})
const lineChartRef = ref(null)
const pieChartRef  = ref(null)

// 今日日期显示
const today = new Date().toLocaleDateString('zh-CN', {
  year: 'numeric', month: 'long', day: 'numeric', weekday: 'long'
})

// 动态加载 ECharts（避免打包体积膨胀）
let echarts = null
const loadECharts = () => new Promise((resolve) => {
  if (window.echarts) { resolve(window.echarts); return }
  const script = document.createElement('script')
  script.src = 'https://cdnjs.cloudflare.com/ajax/libs/echarts/5.4.3/echarts.min.js'
  script.onload = () => resolve(window.echarts)
  document.head.appendChild(script)
})

const loadStats = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/merchant/stats')
    stats.value = res.data || {}
    await nextTick()
    renderCharts()
  } finally {
    loading.value = false
  }
}

const renderCharts = async () => {
  echarts = await loadECharts()

  // ===== 折线图 =====
  if (lineChartRef.value) {
    const line = echarts.init(lineChartRef.value)
    line.setOption({
      tooltip: { trigger: 'axis', formatter: (p) => `${p[0].name}<br/>营业额：¥${p[0].value}` },
      grid: { left: 40, right: 20, top: 20, bottom: 30 },
      xAxis: {
        type: 'category',
        data: stats.value.days || [],
        axisLine: { lineStyle: { color: '#e4e7ed' } },
        axisTick: { show: false },
        axisLabel: { color: '#909399', fontSize: 12 }
      },
      yAxis: {
        type: 'value',
        axisLabel: { color: '#909399', fontSize: 12, formatter: '¥{value}' },
        splitLine: { lineStyle: { color: '#f0f0f0' } }
      },
      series: [{
        data: stats.value.dailySales || [],
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 7,
        lineStyle: { color: '#f5576c', width: 3 },
        itemStyle: { color: '#f5576c' },
        areaStyle: {
          color: {
            type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(245,87,108,0.25)' },
              { offset: 1, color: 'rgba(245,87,108,0.02)' }
            ]
          }
        }
      }]
    })
  }

  // ===== 饼图 =====
  if (pieChartRef.value) {
    const dist = stats.value.statusDist || {}
    const pieData = Object.entries(dist)
        .filter(([, v]) => v > 0)
        .map(([name, value]) => ({ name, value }))

    const pie = echarts.init(pieChartRef.value)
    pie.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c}单 ({d}%)' },
      legend: { bottom: 0, itemWidth: 10, itemHeight: 10, textStyle: { fontSize: 12 } },
      color: ['#409eff', '#67c23a', '#43e97b', '#909399'],
      series: [{
        type: 'pie',
        radius: ['40%', '68%'],
        center: ['50%', '44%'],
        data: pieData.length ? pieData : [{ name: '暂无数据', value: 1 }],
        label: { show: false },
        emphasis: {
          itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.2)' }
        }
      }]
    })
  }
}

onMounted(() => loadStats())
</script>

<style scoped>
.dashboard { min-height: 100%; }

.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 24px;
}
.page-title {
  font-size: 22px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 4px;
}
.page-sub { font-size: 13px; color: #909399; margin: 0; }

/* ===== 指标卡片 ===== */
.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}
.stat-card {
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0,0,0,0.07);
  transition: transform 0.2s;
}
.stat-card:hover { transform: translateY(-3px); }

.card-today-order { background: linear-gradient(135deg, #667eea, #764ba2); }
.card-today-sales  { background: linear-gradient(135deg, #f5576c, #f093fb); }
.card-pending      { background: linear-gradient(135deg, #f6d365, #fda085); }
.card-total        { background: linear-gradient(135deg, #43e97b, #38f9d7); }

.card-icon { font-size: 36px; flex-shrink: 0; }
.card-body { flex: 1; }
.card-label {
  font-size: 12px;
  color: rgba(255,255,255,0.8);
  margin: 0 0 4px;
}
.card-value {
  font-size: 28px;
  font-weight: 800;
  color: white;
  margin: 0 0 2px;
  line-height: 1;
}
.card-hint {
  font-size: 12px;
  color: rgba(255,255,255,0.7);
  margin: 0;
}
.pending-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: white;
  color: #fda085;
  font-size: 13px;
  font-weight: 800;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 6px rgba(0,0,0,0.15);
}

/* ===== 图表区域 ===== */
.charts-row {
  display: grid;
  grid-template-columns: 1fr 340px;
  gap: 16px;
  margin-bottom: 20px;
}
.chart-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
}
.chart-title {
  font-size: 15px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 14px;
}
.chart-box { height: 220px; }

/* ===== 快捷操作 ===== */
.quick-actions {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
}
.qa-title {
  font-size: 15px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 16px;
}
.qa-list {
  display: flex;
  gap: 14px;
}
.qa-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 24px;
  background: #f7f8fc;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  border: 2px solid transparent;
  position: relative;
}
.qa-item:hover {
  background: #fff0f5;
  border-color: #f5576c;
  transform: translateY(-2px);
}
.qa-icon { font-size: 22px; }
.qa-label { font-size: 14px; font-weight: 600; color: #303133; }
</style>