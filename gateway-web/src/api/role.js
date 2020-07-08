import request from '@/utils/request'




function addRole(data) {
  return request({
    url: '/user-service/role/add',
    method: 'post',
    data
  })
}
function updateRole(data) {
  return request({
    url: '/user-service/role/update',
    method: 'post',
    data
  })
}
function deleteRole(data) {
  return request({
    url: '/user-service/role/delete',
    method: 'post',
    data
  })
}
function getRoleList(data) {
  return request({
    url: '/user-service/role/table',
    method: 'post',
    data
  })
}
function getRole(id) {
  return request({
    url: '/user-service/role/' + id,
    method: 'get'
  })
}


export default {
  addRole,
  updateRole,
  deleteRole,
  getRoleList,
  getRole
}

