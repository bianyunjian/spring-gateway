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
    let token = getAccessToken();
    if (token) {
      // let each request carry token
      // ['X-Token'] is a custom headers key
      // please modify it according to the actual situation

      token = token.replace(/"/g, '');
      // console.log(token);

      config.headers['Authorization'] = token;
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

      // 50008: Illegal token; 50012: Other clients logged in; 50014: Token expired;
      if (statusCode === 401) {
        console.log("401 need login");

        this.$router.push('Login');
      }
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
    return Promise.reject(error)
  }
)

export default service
