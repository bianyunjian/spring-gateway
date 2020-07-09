import axios from 'axios'
import { Message } from 'element-ui'
import { getAccessToken } from '@/utils/auth'

var baseUrl = process.env.VUE_APP_BASE_API;
if (window.API_URI) {
  baseUrl = window.API_URI;
}

console.log(baseUrl);

// create an axios instance
const service = axios.create({
  baseURL: baseUrl, // url = base url + request url
  // withCredentials: true, // send cookies when cross-domain requests
  timeout: 60000 // request timeout
})

// request interceptor
service.interceptors.request.use(
  config => {
    // do something before request is sent
    var accessToken = getAccessToken();
    if (accessToken) { 
      accessToken = accessToken.replace(/"/g, '');
      // console.log(token);

      config.headers['Authorization'] = accessToken;
    }
    return config
  },
  error => {
    // do something with request error
    console.log(error) // for debug

    return Promise.reject(error)
  }
)

// response interceptor
service.interceptors.response.use(
  /**
   * If you want to get http information such as headers or status
   * Please return  response => response
  */

  /**
   * Determine the request status by custom code
   * Here is just an example
   * You can also judge the status by HTTP Status Code
   */
  response => {
    const res = response.data
    const statusCode = response.status;
    // debugger
    if (res.errorCode !== undefined) {
      if (res.errorCode === null) {
        res.errorCode = 0;
      }

    }

    if (statusCode !== 200) {


      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      return res
    }
  },
  error => {
    console.log('err' + error) // for debug
    Message({
      message: error.message,
      type: 'error',
      duration: 3 * 1000
    })
    if (error.message && error.message.indexOf("401") >= 0) {
      console.log("401 token invalid");
      window.postMessage("ezml-401-token-invalid", "*");
    }


    return Promise.reject(error)
  }
)

export default service
