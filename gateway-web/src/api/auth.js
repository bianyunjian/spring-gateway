import request from '@/utils/request'


function login(data) {
    return request({
        url: '/login',
        method: 'post',
        data
    })
}

function loginOut(data) {
    return request({
        url: '/loginOut',
        method: 'post',
        data
    })
} function refreshToken(data) {
    return request({
        url: '/refreshToken',
        method: 'post',
        data
    })
}

export default {
    login,
    loginOut,
    refreshToken
}



