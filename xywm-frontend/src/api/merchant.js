import request from '@/utils/request'

// ===== 分类管理接口 =====
export const getCategoryListApi = () => request.get('/api/category/list')
// 下面三个接口的路径里加上了 /merchant
export const addCategoryApi = (data) => request.post('/api/category/merchant', data)
export const updateCategoryApi = (data) => request.put('/api/category/merchant', data)
export const deleteCategoryApi = (id) => request.delete(`/api/category/merchant/${id}`)

// ===== 菜品管理接口 (保持不变) =====
export const getDishListApi = (params) => request.get('/api/dish/list', { params })
export const addDishApi = (data) => request.post('/api/dish', data)
export const updateDishApi = (data) => request.put('/api/dish', data)
export const deleteDishApi = (id) => request.delete(`/api/dish/${id}`)
export const updateDishStatusApi = (status, id) => request.post(`/api/dish/status/${status}?id=${id}`)