import store from '@/store'
import sessionStore from '@/util/session'

let url_cache = ''
let socket_cache = null
let params_cache = null
let chatInterval = null

export default {
  websockLink: '',
  websock: null,
  watch: {
    /* websockLink(val) {
      if (val) {
        this.initWebSocket()
      }
    } */
  },
  initWebSocket(userCode, userType, ticket, socketMsg) {
    let t = sessionStore.getTicket()
    let url_content = '/onlineSocket/' + userCode + '/' + t.replaceAll('\\/','*') + '/' + store.getters.getFingerPrint
    this.websockLink = process.env.SOCKET_URL + url_content
    url_cache = this.websockLink
    console.log('连接信息--', url_cache)

    params_cache = {}
    params_cache.userCode = userCode
    params_cache.userType = userType

    socket_cache = new WebSocket(this.websockLink)
    socket_cache.onopen = this.websocketonopen
    socket_cache.onerror = this.websocketonerror
    // socket_cache.onmessage = this.websocketonmessage
    socket_cache.onmessage = socketMsg
    socket_cache.onclose = this.websocketclose

    return this
  },
  websocketonopen() {
    console.log('onlineSocket连接成功')

    try {
      // 开启的群聊天
      let ugChatList = store.getters.getUgChatList
      params_cache.gcList = ugChatList
      socket_cache.send(JSON.stringify(params_cache))

      // 每一秒向后台发送一回“已读”消息
      chatInterval = setInterval(function (params) {
        // socket_cache.send(JSON.stringify({time: new Date()}))
        
        // 开启的群聊天
        let ugChatList = store.getters.getUgChatList
        params_cache.gcList = ugChatList
        
        socket_cache.send(JSON.stringify(params_cache))
      }, 5000);
    } catch (err) {
      console.log("连接异常 (" + err.code + ")");
      // this.$slideMsg.error('连接异常，'+err.code)
    }
  },
  websocketonerror(e) {
    console.log('onlineSocket连接发生错误')
  },
  websocketonmessage(e) {
    console.log('onlineSocket后台消息：', e.data)
    // let data = JSON.parse(e.data)
    // console.log('WebSocket信息通知--', data.msg)
  },
  websocketclose(e) {
    console.log('关闭onlineSocket连接')
    clearInterval(chatInterval)
  },
  websocketSend(text) { // 数据发送
    socket_cache.send(text)
  },
  checkOnline(mapCode) { 
    // 发送在线/没有在线的用户编号列表
    // console.log('在线/没有在线的用户编号列表--', JSON.stringify(mapCode))
    params_cache.mapCode = mapCode
    return params_cache
  },
  socketState () {
    if (socket_cache != null && socket_cache.readyState === 1) {
      return true
    }
    return false
  },
  destoryWS () {
    //this.initWebSocket();
    if (socket_cache != null) {
      clearInterval(chatInterval)
      socket_cache.close()
    }
    socket_cache = null
    params_cache = null
  }
}