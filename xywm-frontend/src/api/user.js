import request from '@/utils/request'

// 获取所有正常营业的商家列表
export const getShopListApi = () => request.get('/api/shop/list')

// 获取平台大分类（用于首页顶部导航）
export const getPlatformCategoryApi = () => request.get('/api/category/platform')
// 获取商家详情
export const getShopDetailApi = (merchantId) => request.get(`/api/shop/${merchantId}`)

// 获取商家菜品（按分类分组）
export const getGroupedDishesApi = (merchantId) => request.get(`/api/dish/grouped/${merchantId}`)

// 购物车操作
export const getCartApi = () => request.get('/api/cart')
export const addToCartApi = (data) => request.post('/api/cart/add', data)
export const updateCartQuantityApi = (cartId, quantity) => request.put(`/api/cart/${cartId}/quantity/${quantity}`)
export const removeCartItemApi = (cartId) => request.delete(`/api/cart/${cartId}`)
// 地址管理
export const getAddressListApi = () => request.get('/api/address')
export const addAddressApi = (data) => request.post('/api/address', data)
export const updateAddressApi = (data) => request.put('/api/address', data)
export const deleteAddressApi = (id) => request.delete(`/api/address/${id}`)