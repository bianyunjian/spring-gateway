const PREFIX = ""; //Sotrage 存储前缀

function setItem(keyName, keyValue) {
  try {
    return localStorage.setItem(PREFIX + keyName, JSON.stringify(keyValue));
  } catch (error) {
    console.error(error);
  }
}
function getItem(keyName) {
  try {
    return localStorage.getItem(PREFIX + keyName);
  } catch (error) {
    console.error(error);
  }
}
function removeItem(keyName) {
  return localStorage.removeItem(PREFIX + keyName);
}

export default {
  PREFIX,
  setItem,
  getItem,
  removeItem
};
