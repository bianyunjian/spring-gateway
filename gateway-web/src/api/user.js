import request from '@/utils/request'

//   function fetchList(query) {
//   return request({
//     url: '/article/list',
//     method: 'get',
//     params: query
//   })
// }


function getUserList(data) {
  return request({
    url: '/user-service/user/table',
    method: 'post',
    data
  })
}
function addUser(data) {
  return request({
    url: '/user-service/user/add',
    method: 'post',
    data
  })
}
function updateUser(data) {
  return request({
    url: '/user-service/user/update',
    method: 'post',
    data
  })
}

function deleteUser(data) {
  return request({
    url: '/user-service/user/delete',
    method: 'post',
    data
  })
}
function enableUser(data) {
  return request({
    url: '/user-service/user/enable',
    method: 'post',
    data
  })
}
function disableUser(data) {
  return request({
    url: '/user-service/user/disable',
    method: 'post',
    data
  })
}
function getUser(id) {
  return request({
    url: '/user-service/user/' + id,
    method: 'get',

  })
}

function getUserInfo(data) {
  return request({
    url: '/user-service/user/getUserInfo',
    method: 'post',
    data
  })
}

export default {
  getUserList,
  addUser,
  updateUser,
  deleteUser,
  enableUser,
  disableUser,
  getUser,
  getUserInfo


}



