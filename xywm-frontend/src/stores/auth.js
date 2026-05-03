import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
    const token = ref(localStorage.getItem('token') || '')
    const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

    const isLoggedIn = computed(() => !!token.value)
    const userRole = computed(() => userInfo.value?.role ?? -1)
    const userName = computed(() => userInfo.value?.nickname || userInfo.value?.username || '')

    function setAuth(tokenVal, userVal) {
        token.value = tokenVal
        userInfo.value = userVal
        localStorage.setItem('token', tokenVal)
        localStorage.setItem('userInfo', JSON.stringify(userVal))
    }

    function logout() {
        token.value = ''
        userInfo.value = null
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
    }

    return { token, userInfo, isLoggedIn, userRole, userName, setAuth, logout }
})