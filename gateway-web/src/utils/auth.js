import storage from "@/utils/storage.js";

const AccessTokenKey = 'ezml-access-token'
const RefreshTokenKey = 'ezml-refresh-token'
const UserDataKey = 'ezml-user-data'
export function getAccessToken() {
  var token = storage.getItem(AccessTokenKey);
  if (token) {
    token = token.replace(/"/g, '');
  }
  return token;

}

export function setAccessToken(token) {
  return storage.setItem(AccessTokenKey, token)
}

export function removeAccessToken() {

  return storage.removeItem(AccessTokenKey)
}
export function getRefreshToken() {

  var token = storage.getItem(RefreshTokenKey);
  if (token) {
    token = token.replace(/"/g, '');
  }
  return token;
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