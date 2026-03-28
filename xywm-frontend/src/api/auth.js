import request from '@/utils/request'

export const loginApi = (data) => request.post('/api/auth/login', data)
export const registerApi = (data) => request.post('/api/auth/register', data)
export const logoutApi = () => request.post('/api/auth/logout')