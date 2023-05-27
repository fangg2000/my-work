import RSA from '@/base/rsaEncrypt.js'
import AES from '@/base/aes.js'
import store from '@/store'
import sessionStore from '@/util/session'

const SYSTEM_CODE = 'SYSTEM'

const ReadSocket = function (options) {
  this.socketId = options.socketId
  this.websockLink = ''
  this.websock = null
  
  this.url_cache = null
  this.socket_cache = null
  this.chatInterval = null
  this.select_user_cache = null
  return this
}

ReadSocket.prototype.initWebSocket = function (select_user, login_user,  backReadData) {
  console.log('启动websocket链接--', this.socket_cache)

  //this.websockLink = process.env.BASE_URL.replace('http', 'ws').replace('https', 'ws') + '/chat/readSocket'
  let company_code, server, client, type
  if (select_user.userType != login_user.userType) {
    if (login_user.userType === 0) {
      company_code = login_user.companyCode
      server = login_user.userCode
      client = select_user.userCode
      type = '0'
    } else if (select_user.userType === 2 || select_user.userType === 3) {
      company_code = select_user.companyCode
      server = select_user.companyCode
      client = login_user.userCode
      // 如果是群/房间，则用户为发送方
      type = '1'
    } else {
      company_code = select_user.companyCode
      server = select_user.userCode
      client = login_user.userCode
      type = '1'
    }
  } else {
    company_code = SYSTEM_CODE
    server = login_user.userCode
    client = select_user.userCode
    type = '0'
  }

  /* let url_content = company_code + 
    '/' + server +
    '/' + client +
    '/' + type + '/0'  */
  let infoParams = {
    cc: company_code,
    s: server,
    c: client,
    t: type,
    st: '0'
  }

  let rsa_info = RSA.encrypt(JSON.stringify(infoParams))
  let ticket = sessionStore.getTicket()
  let info = ticket +'&'+ store.getters.getFingerPrint +'&'+ rsa_info

  // 缓存当前调用参数
  this.url_cache = {}
  this.url_cache.companyCode = company_code
  this.url_cache.server = server
  this.url_cache.client = client
  this.url_cache.type = type
  this.url_cache.status = '1'
  this.url_cache.contactCode = select_user.userCode
  this.url_cache.ticket = ticket

  this.websockLink = process.env.SOCKET_URL + '/readSocket/' + info.replaceAll('\\/', '*')
  this.select_user_cache = select_user

  let view = this

  this.websocketonopen = function () {
    console.log('WebSocket连接成功')
    // let urlMsg = AES.encrypt(JSON.stringify(view.url_cache), store.getters.getAesKey)
    let urlMsg = JSON.stringify(view.url_cache)
    /* let params_json = {
      ticket: login_user.ticket,
      info: urlMsg
    }
    let msg = JSON.stringify(params_json) */

    view.socket_cache.send(urlMsg)

    // 如果是群聊天窗口开启，则缓存
    if (select_user.userType === 2 || select_user.userType === 3) {
      // 此处的companyCode为群编号
      store.commit('updateUgChatList', select_user.companyCode)
    }

    // 每一秒向后台发送一回“已读”消息
    view.chatInterval = setInterval(function (params) {
      view.socket_cache.send(urlMsg)
    }, 1600);
  }

  this.websocketonerror = function (e) {
    console.log('WebSocket连接发生错误')
  }

  this.websocketonmessage = function (e) {
    console.log('WebSocket后台消息：', e.data)
    // let data = JSON.parse(e.data)
    // console.log('WebSocket信息通知--', data.msg)
  }

  this.websocketclose = function (e) {
    console.log('关闭WebSocket连接')
    clearInterval(view.chatInterval)
  }

  this.socket_cache = new WebSocket(this.websockLink)
  this.socket_cache.onopen = this.websocketonopen
  this.socket_cache.onerror = this.websocketonerror
  this.socket_cache.onmessage = backReadData
  this.socket_cache.onclose = this.websocketclose
}

ReadSocket.prototype.websocketSend = function (text) { 
  // 数据发送
  this.socket_cache.send(text);
}

ReadSocket.prototype.socketState = function () {
  if (this.socket_cache != null && this.socket_cache.readyState === 1) {
    return true
  }
  return false
}

ReadSocket.prototype.getSelectUser = function () {
  if (this.socketState()) {
    return this.select_user_cache
  }
  return null
}

ReadSocket.prototype.destoryWS = function () {
  //this.initWebSocket();
  if (this.socket_cache != null) {
    clearInterval(this.chatInterval)
    this.socket_cache.close()
  }
  this.socket_cache = null
  this.url_cache = null
  this.select_user_cache = null
}

export default ReadSocket
