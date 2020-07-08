import storage from "@/utils/storage.js";

const AccessTokenKey = 'ezml-access-token'
const RefreshTokenKey = 'ezml-refresh-token'
const UserDataKey = 'ezml-user-data'
export function getAccessToken() {

  return storage.getItem(AccessTokenKey);
}

export function setAccessToken(token) {
  return storage.setItem(AccessTokenKey, token)
}

export function removeAccessToken() {
  return storage.removeItem(AccessTokenKey)
}
export function getRefreshToken() {

  return storage.getItem(RefreshTokenKey);
}

export function setRefreshToken(token) {
  return storage.setItem(RefreshTokenKey, token)
}

export function removeRefreshToken() {
  return storage.removeItem(RefreshTokenKey)
}

export function setUserData(item) {
  return storage.setItem(UserDataKey, item)
}
export function getUserData(key, item) {
  return storage.getItem(UserDataKey, item)
}
export function removeUserData() {
  return storage.removeItem(UserDataKey)
}