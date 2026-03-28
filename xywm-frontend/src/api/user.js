import request from '@/utils/request'

// 获取所有正常营业的商家列表
export const getShopListApi = () => request.get('/api/shop/list')

// 获取平台大分类（用于首页顶部导航）
export const getPlatformCategoryApi = () => request.get('/api/category/platform')