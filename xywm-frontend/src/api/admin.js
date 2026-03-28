import request from '@/utils/request'

// 获取所有商家列表
export const getMerchantListApi = () => {
    return request.get('/api/admin/merchant/list')
}

// 更改商家状态（审核/封禁/解封）
export const updateMerchantStatusApi = (data) => {
    return request.post('/api/admin/merchant/status', data)
}