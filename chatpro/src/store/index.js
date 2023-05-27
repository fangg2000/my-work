import Vue from 'vue'
import Vuex from 'vuex'
import FuncPermission from './modules/func_permission'
import SocketCache from './modules/socket_store'

Vue.use(Vuex)

const store = new Vuex.Store({
  modules: {
    funcPer: FuncPermission,
    socket: SocketCache,
  },
  state: {
    prepare: false,
    loginIdentifier: null,
    userInfo: null,
    funcPermission: null,
    socketIds: [],
    online: false,
    homeInterval: null,
    vmInterval: [],
    newContactList: [],
    ugChatList: [],   // 群聊天开启列表
    aesKey: null,
    fingerPrint: null
  },
  getters: {
    getUserInfo: state => {
      //return state.todos.filter(todo => todo.done)
      return state.userInfo
    },
    getLoginIdentifier: state => {
      //return state.todos.filter(todo => todo.done)
      return state.loginIdentifier
    },
    getSocketState: (state) => (key) => {
      return state.socketIds.includes(key)
    },
    onlineState: (state) => {
      return state.online
    },
    getHomeInterval: (state) => {
      return state.homeInterval
    },
    getNewContactList: (state) => {
      return state.newContactList
    },
    getFuncPermission: (state) => {
      return state.funcPermission
    },
    getAesKey: (state) => {
      return state.aesKey
    },
    getFingerPrint: (state) => {
      return state.fingerPrint
    },
    getUgChatList: (state) => {
      return state.ugChatList
    }
  },
  mutations: {
    updateUserInfo (state, user) {
      let outUser = JSON.parse(JSON.stringify(user))
      // 不需要显示的字段
      delete outUser.fp4Web
      delete outUser.aesKey

      if (user.ticket != undefined && user.ticket != null) {
        outUser.ticket = user.ticket.replaceAll('\r\n', '')
      } 

      // 查询用户信息时不带功能权限信息
      state.userInfo = outUser
      state.funcPermission = user.fp4Web
      state.prepare = true
    },
    updateLoginIdentifier (state, key) {
      state.loginIdentifier = key
    },
    updateBlackUserList (state, data) {
      if (data.blackType == 1) {
        state.userInfo.blackUserList.push(data.userCode)
      } else if (data.blackType == 0) {
        let index = state.userInfo.blackUserList.indexOf(data.userCode)
        if (index > -1) {
          state.userInfo.blackUserList.splice(index, 1)
        }
      }
    },
    updateSocketIds (state, key) {
      state.socketIds.push(key)
    },
    removeSocketIds (state, key) {
      //state.socketIds.remove(key)
      let index = state.socketIds.indexOf(key)
      if (index > -1) {
        state.socketIds.splice(index, 1)
      }
    },
    clearSocketIds (state, key) {
      state.socketIds = []
    },
    updateOnlineState (state, key) {
      state.online = key
    },
    updateHomeInterval (state, key) {
      state.homeInterval = key
    },
    updateNewContactList (state, key) {
      if (key && state.newContactList.includes(key) == false) {
        state.newContactList.push(key)
      }
    },
    removeNewContact (state, key) {
      let index = state.newContactList.indexOf(key)
      if (index > -1) {
        state.newContactList.splice(index, 1)
      }
    },
    updateAesKey (state, key) {
      state.aesKey = key
    },
    updateFingerPrint (state, key) {
      state.fingerPrint = key
    },
    updateUgChatList (state, key) {
      if (key && state.ugChatList.includes(key) == false) {
        state.ugChatList.push(key)
      }
    },
    clearUgChatList (state, key) {
      if (key != undefined) {
        let index = state.ugChatList.indexOf(key)
        if (index > -1) {
          state.ugChatList.splice(index, 1)
        }
      } else {
        state.ugChatList = []
      }
    },
    updateVmInterval (state, key) {
      state.vmInterval.push(key)
    },
    clearVmInterval (state, key) {
      if (key != undefined) {
        let index = state.vmInterval.indexOf(key)
        if (index > -1) {
          clearInterval(key)
          state.vmInterval.splice(index, 1)
        }
      } else {
        state.vmInterval.forEach(inter => {
          clearInterval(inter)
        });
        state.vmInterval = []
      }
    }
  }
})

export default store