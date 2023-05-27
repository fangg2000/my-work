//request.js
import axios from "axios";
import slideMsg from '@/base/slideMsg'
import sessionStore from '@/util/session'
import router from '@/router'
import store from '@/store'
import RSA from '@/base/rsaEncrypt.js'
import AES from '@/base/aes.js'

const view = this;
const ENCRYPT_TYPE = 'RSA';

/****** 创建axios实例 ******/
const ajaxRequest = axios.create({
  baseURL: process.env.BASE_URL,  // api的base_url
  timeout: 60000  // 请求超时时间
});

// request拦截器
ajaxRequest.interceptors.request.use(config => {
  let ticket = sessionStore.getTicket()

  if (ticket != null) {
    config.headers['ticket'] = ticket // 让每个请求携带自定义token 请根据实际情况自行修改
  }
  if (config.headers['Content-Type'] == undefined || config.headers['Content-Type'] == null) {
    //config.headers['Content-Type'] = 'application/x-www-form-urlencoded'  //指定消息格式
    config.headers['Content-Type'] = 'application/json;charset=UTF-8'  //指定默认消息格式（如果要改变格式，则在请求的方法上覆盖）
  }

  // 如要对POST请球加密，必须是data数据提交，不支持表单
  // 提交数据data中须有属性 encryptFlag = true
  if (config.data.encryptFlag && config.method.toLowerCase() == "post") {
    // console.log('调用方法需要加密--');
    // 删除encryptFlag属性（不需要传后端）
    delete config.data.encryptFlag;

    // 判断是否使用RSA加密，否则使用AES加密
    if (config.data.encryptType && config.data.encryptType.toUpperCase() === ENCRYPT_TYPE) {
      // 删除encryptType属性（不需要传后端）
      delete config.data.encryptType;
      config.data = RSA.encrypt(JSON.stringify(config.data));
    } else {
      let aesKey = store.getters.getAesKey
      // 登录成功后会新生成一个aes密钥放在缓存中，前后端AES加解密数据时使用此密钥
      config.data = AES.encrypt(JSON.stringify(config.data), aesKey);
    }
    // console.log('加密结果--', config.data);
  }

  return config
}, error => {
  // Do something with request error
  //console.log(error) // for debug
  //slideMsg.message(undefined, 'error')
  Promise.reject(error)
})

// respone拦截器
ajaxRequest.interceptors.response.use(
  response => {
    /**
    * code为非200是抛错 可结合自己业务进行修改
    */
    // console.log('返回值--', response.data);

    let res = null

    // 返回的是字符串则做解密处理（一般情况，如果数据没有加密则返回的是json格式对象）
    if (typeof(response.data) == 'string') {
      let aesKey = store.getters.getAesKey
      /* let str_arr = response.data.split('\r\n')
      let decrypt_str = ''
      if (str_arr.length > 1) {
        str_arr.forEach((str, i) => {
          if (str && str.length > 0) {
            // console.log('分段'+i, str);
            decrypt_str = decrypt_str + str
          }
        });
        decrypt_str = AES.decrypt(decrypt_str, aesKey)
      } else {
        decrypt_str = AES.decrypt(response.data, aesKey);
      } */
      let decrypt_str = AES.decrypt(response.data.replaceAll('\r\n', ''), aesKey);
      // console.log('解密结果--', decrypt_str);
      res = JSON.parse(decrypt_str)
    } else {
      res = response.data
    }

    if (res.code !== 1) {
      slideMsg.message(res.msg, 'error')

      // 101:未登录/登录超时;
      if (res.code === 101) {
        // 删除ticket
        sessionStore.delTicket()
        router.push({ path: '/LoginOut'})
        // 销毁全部可能还在运行的线程
        store.commit('clearVmInterval')
      }
      // 103:登录状态异常
      else if (res.code === 103) {
        slideMsg.message('登录状态异常，请重新登录', 'error')
        // 缓存全部清空，否则刷新时会查询用户信息
        sessionStorage.clear()
        localStorage.clear()
        router.push({ path: '/LoginOut'})
        // 销毁全部可能还在运行的线程
        store.commit('clearVmInterval')
      }
      // 数据解释异常
      else if (res.code == undefined || res.code == null) {
        slideMsg.message('数据解释异常', 'error')
        //router.push({ path: '/LoginOut'})
      }

      // return Promise.reject('error')
    }
    
    return res
  },
  error => {
    //console.log('err' + error)// for debug
    slideMsg.message('操作失败', 'error')
    return Promise.reject(error)
  }
)

export default ajaxRequest
