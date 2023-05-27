/**
 * 调用接口JS
 */
import ajaxRequest from './ajax'
import UUID from '@/util/uuidUtil.js'
import store from '@/store'
import sessionStore from '@/util/session'
import AES from '@/base/aes.js'
import Fingerprint2 from 'fingerprintjs2'

// 取得浏览器指纹ID
Fingerprint2.get(function(components) {
  const values = components.map(function(component,index) {
    if (index === 0) { //把微信浏览器里UA的wifi或4G等网络替换成空,不然切换网络会ID不一样
      return component.value.replace(/\bNetType\/\w+\b/, '')
    }
    return component.value
  })
  // 生成最终id murmur   
  const murmur = Fingerprint2.x64hash128(values.join(''), 31)
  console.log('浏览器指纹A--', murmur);
  store.commit('updateFingerPrint', murmur)
  return murmur
}) 

// 用户登录
// 此方法为Promise异步调用，要在调用此方法的函数前加上async，在调用此方法前加上await(其中async和await是成对出现的)
export function login(userCode, password, userType) {
  // 登录识别码
  var uuid = UUID()
  store.commit('updateLoginIdentifier', uuid)
  
  let key_p = AES.generatekey(16)
  store.commit('updateAesKey', key_p)
  // console.log('AES密钥--', key_p);
  
  return ajaxRequest({
    url: '/user/login',
    method: 'POST',
    /* headers: {
      'Content-Type': 'application/json;charset=UTF-8',  //指定消息格式
    }, */
    // 后面为了加强安全性，可以做签名处理
    data: {
      userCode: userCode,
      password: password,
      userType: userType,
      loginIdentifier: uuid,
      fingerPrint: store.getters.getFingerPrint,
      encryptFlag: true,
      encryptType: 'RSA',
      aesKey: key_p
    }
  }) 
}

// 登录验证
export function loginCheck(checkIdentifier) {
  // 登录识别码
  var uuid = UUID()
  store.commit('updateLoginIdentifier', uuid)

  return ajaxRequest({
    url: '/user/putLoginCheck',
    method: 'POST',
    data: {
      checkIdentifier: checkIdentifier, 
      loginIdentifier: uuid,
      fingerPrint: store.getters.getFingerPrint,
      encryptFlag: true,
      encryptType: 'RSA'
    }
  })
}

export function chatWrite(params) {
  // 消息加密
  params.encryptFlag = true

  return ajaxRequest({
    url: '/chat/write',
    method: 'POST',
    /* headers: {
      'Content-Type': 'application/json;charset=UTF-8',  //指定消息格式
    }, */
    data: params
  }) 
}

// 重新上线识别码判断
export function checkLoginIdentifier(params) {
  return ajaxRequest({
    url: '/user/getLoginIdentifierStatus',
    method: 'post',
    data: {
      loginIdentifier: sessionStore.getItem('loginIdentifier'),
      fingerPrint: store.getters.getFingerPrint,
      encryptFlag: true,
      encryptType: 'RSA'
    }
  })
}

// 用户信息更新
export function updateUserInfo(params) {
  // 需要加密
  params.encryptFlag = true
  params.fingerPrint = store.getters.getFingerPrint
  // ticket不需要
  delete params.ticket

  return ajaxRequest({
    url: '/user/patchUserInfo',
    method: 'post',
    data: params
  })
}

// 页面刷新获取用户信息
export function checkFromReload(to, from, next) {
  let fpInterval = setInterval(() => {
    checkFromReloadOk(to, from, next, fpInterval)
  }, 1000);
}

function checkFromReloadOk(to, from, next, fpInterval) {
  let fp = store.getters.getFingerPrint
  if (fp == null) {
    console.log('fp没准备好');
    return false
  }

  clearInterval(fpInterval)

  // 新生成AES密钥
  let key_p = AES.generatekey(16)
  store.commit('updateAesKey', key_p)
  
  console.log('刷新浏览器指纹B--', fp);

  ajaxRequest({
    url: '/user/getUserInfo',
    method: 'POST', 
    data: {
      loginIdentifier: sessionStore.getItem('loginIdentifier'),
      fingerPrint: fp,
      encryptFlag: true,
      encryptType: 'RSA', // 使用RSA公钥加密
      aesKey: key_p
    }
  }).then(function(res){
    //this.$slideMsg.info('读取未读数据：'+res.msg)

    if (res.code == 1) {
      store.commit('updateUserInfo', res.data)
      // 更新ticket
      sessionStore.setTicket(res.data.ticket.replaceAll('\r\n', ''))
    
      if (to.path == '/Login') {
        next({path:'/Home'})
      }
    }
  })

  return true
}

// 解绑--通过邮箱
export function unbindByMail(params) {
  return ajaxRequest({
    url: '/config/unbindByMail',
    method: 'post',
    data: params
  })
}

