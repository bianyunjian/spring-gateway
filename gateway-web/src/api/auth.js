import request from '@/utils/request'


function login(data) {
    return request({
        url: '/login',
        method: 'post',
        data
    })
} function loginOut(data) {
    return request({
        url: '/loginOut',
        method: 'post',
        data
    })
}

export default {
    login,
    loginOut
}



