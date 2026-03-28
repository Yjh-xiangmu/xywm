<template>
  <div class="user-home">
    <div class="banner">
      <div class="banner-content">
        <h1>今天想吃点什么？😋</h1>
        <p>校园美食，一触即达</p>
        <div class="search-box">
          <el-input
              v-model="searchKeyword"
              placeholder="搜索你想吃的商家或美食..."
              size="large"
              clearable
              :prefix-icon="Search"
          />
          <el-button color="#43e97b" size="large" class="search-btn">搜索</el-button>
        </div>
      </div>
    </div>

    <div class="main-container">
      <div class="category-filter">
        <div
            class="category-item"
            :class="{ active: currentCategoryId === '' }"
            @click="filterByCategory('')"
        >
          <div class="cat-icon">🍽️</div>
          <span class="cat-name">全部商家</span>
        </div>
        <div
            class="category-item"
            v-for="cat in categoryList"
            :key="cat.id"
            :class="{ active: currentCategoryId === cat.id }"
            @click="filterByCategory(cat.id)"
        >
          <div class="cat-icon">✨</div>
          <span class="cat-name">{{ cat.name }}</span>
        </div>
      </div>

      <div class="section-title">
        <h2>🔥 附近好店</h2>
        <span class="subtitle">共找到 {{ filteredShops.length }} 家</span>
      </div>

      <el-skeleton :rows="4" animated v-if="loading" style="margin-top: 20px;" />

      <div v-else class="shop-grid">
        <el-empty v-if="filteredShops.length === 0" description="没有找到相关的商家哦" />

        <div
            class="shop-card"
            v-for="shop in filteredShops"
            :key="shop.id"
            @click="goToShop(shop)"
        >
          <div class="shop-img-wrapper">
            <el-image
                :src="shop.avatar || shop.licenseUrl || 'https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png'"
                class="shop-image"
                fit="cover"
            />
            <div class="status-badge">营业中</div>
          </div>

          <div class="shop-info">
            <h3 class="shop-name">{{ shop.nickname }}</h3>
            <div class="shop-meta">
              <span class="rating">⭐ 4.9</span>
              <span class="divider">|</span>
              <span class="sales">月售 800+</span>
            </div>
            <div class="shop-tags">
              <el-tag size="small" effect="plain" color="#f0fff8" style="color: #43e97b; border-color: #43e97b;">
                {{ getCategoryName(shop.categoryId) }}
              </el-tag>
              <el-tag size="small" type="danger" effect="plain">满20减2</el-tag>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getShopListApi, getPlatformCategoryApi } from '@/api/user'

const router = useRouter()
const shopList = ref([])
const categoryList = ref([])
const searchKeyword = ref('')
const currentCategoryId = ref('')
const loading = ref(false)

const initData = async () => {
  loading.value = true
  try {
    const [shopRes, catRes] = await Promise.all([
      getShopListApi(),
      getPlatformCategoryApi()
    ])
    shopList.value = shopRes.data || []
    categoryList.value = catRes.data || []
  } finally {
    loading.value = false
  }
}

const getCategoryName = (id) => {
  if (!id) return '精选美食'
  const cat = categoryList.value.find(c => c.id === id)
  return cat ? cat.name : '精选美食'
}

const filterByCategory = (id) => {
  currentCategoryId.value = id
}

const filteredShops = computed(() => {
  let result = shopList.value
  if (currentCategoryId.value !== '') {
    result = result.filter(shop => shop.categoryId === currentCategoryId.value)
  }
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(shop =>
        shop.nickname.toLowerCase().includes(keyword)
    )
  }
  return result
})

const goToShop = (shop) => {
  ElMessage.success(`准备进入 ${shop.nickname} 点餐啦！`)
}

onMounted(() => {
  initData()
})
</script>

<style scoped>
.user-home {
  min-height: 100%;
  padding-bottom: 40px;
}
.banner {
  height: 240px;
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  border-radius: 12px;
}
.banner-content {
  text-align: center;
  color: white;
  z-index: 1;
  width: 100%;
  max-width: 650px;
  padding: 0 20px;
}
.banner-content h1 { font-size: 30px; margin-bottom: 10px; font-weight: bold; letter-spacing: 2px; }
.banner-content p { font-size: 15px; margin-bottom: 25px; opacity: 0.9; }
.search-box {
  display: flex;
  gap: 10px;
  background: white;
  padding: 6px;
  border-radius: 12px;
  box-shadow: 0 8px 20px rgba(0,0,0,0.1);
}
.search-box :deep(.el-input__wrapper) { box-shadow: none !important; background: transparent; }
.search-btn { border-radius: 8px; font-weight: bold; padding: 0 25px; color: white; }

.main-container {
  margin-top: 20px;
  position: relative;
  z-index: 2;
}
.category-filter {
  background: white;
  border-radius: 16px;
  padding: 20px;
  display: flex;
  gap: 30px;
  overflow-x: auto;
  box-shadow: 0 4px 16px rgba(0,0,0,0.04);
  margin-bottom: 30px;
}
.category-filter::-webkit-scrollbar { display: none; }
.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.3s;
  min-width: 64px;
}
.category-item:hover { transform: translateY(-3px); }
.cat-icon {
  width: 48px;
  height: 48px;
  background: #f5f7fa;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  transition: all 0.3s;
}
.cat-name { font-size: 14px; color: #606266; font-weight: 500; }
.category-item.active .cat-icon {
  background: #f0fff8;
  color: #43e97b;
  box-shadow: 0 4px 12px rgba(67, 233, 123, 0.2);
}
.category-item.active .cat-name { color: #43e97b; font-weight: bold; }

.section-title { display: flex; align-items: baseline; gap: 15px; margin-bottom: 20px; }
.section-title h2 { font-size: 20px; color: #303133; margin: 0; font-weight: bold; }
.subtitle { color: #909399; font-size: 13px; }

.shop-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}
.shop-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0,0,0,0.04);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}
.shop-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0,0,0,0.08);
  border-color: #f0fff8;
}
.shop-img-wrapper { position: relative; height: 140px; overflow: hidden; }
.shop-image { width: 100%; height: 100%; transition: transform 0.5s; }
.shop-card:hover .shop-image { transform: scale(1.05); }
.status-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(67, 233, 123, 0.9);
  color: white;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: bold;
  backdrop-filter: blur(4px);
}
.shop-info { padding: 16px; }
.shop-name { margin: 0 0 8px 0; font-size: 17px; color: #303133; font-weight: bold; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.shop-meta { display: flex; align-items: center; color: #606266; font-size: 12px; margin-bottom: 12px; }
.rating { color: #ff9900; font-weight: bold; font-size: 13px; }
.divider { margin: 0 8px; color: #dcdfe6; }
.shop-tags { display: flex; gap: 8px; flex-wrap: wrap; }
</style>