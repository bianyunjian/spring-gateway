import request from '@/utils/request'

function getAll () {
  return request({
    url: '/user-service/permission/all',
    method: 'get'
  })
}

function getTable (data) {
  return request({
    url: '/user-service/permission/table',
    method: 'post',
    data
  })
}

function addPermission (data) {
  return request({
    url: '/user-service/permission/add',
    method: 'post',
    data
  })
}

function updatePermission (data) {
  return request({
    url: '/user-service/permission/update',
    method: 'post',
    data
  })
}

function deletePermission (data) {
  return request({
    url: '/user-service/permission/delete',
    method: 'post',
    data
  })
}

export default {
  table: getTable,
  add: addPermission,
  update: updatePermission,
  delete: deletePermission,
  all: getAll
}
