/* 
  缓存socket相关
*/
const SocketCache = {
  namespaced: true,
  state: () => ({
    socketList: [],
    viewInterval: {},   // 针对不同组件的线程进行缓存
  }),
  mutations: {
    add2SocketList (state, key) {
      state.socketList.push(key)
    },
    updateSocketList (state, key) {
      let index = null
      state.socketList.forEach((socket, i) => {
        if (key != undefined && key.socketId == socket.socketId) {
          index = i
        }
      });

      if (index != null) {
        state.socketList.splice(index, 1)
      }
      
      state.socketList.push(key)
    },
    destroySocket (state, key) {
      if (key != undefined) {
        let index = null
        state.socketList.forEach((socket, i) => {
          if (key == socket.socketId) {
            console.log('销毁socket--', socket.socketId);
            socket.destoryWS()
            index = i
          }
        });

        if (index != null) {
          state.socketList.splice(index, 1)
        }
      } else {
        state.socketList.forEach((socket, i) => {
          console.log('销毁socket--', socket.socketId);
          socket.destoryWS()
        });
        state.socketList = []
      }
    },
    updateViewInterval (state, data) {
      let key = data.key,
      field = data.field,
      val = data.val
      // console.log('组件interval缓存--', key, '/', field, '/', val);
      
      if (state.viewInterval[key]) {
        let view_data = state.viewInterval[key]
        view_data[field] = val
      } else {
        let view_data = state.viewInterval[key] = {}
        view_data[field] = val
      }
    },
    clearViewInterval (state, data) {
      let key,field,val

      if (data) {
        key = data.key
        field = data.field
        val = data.val
      }

      if (data && state.viewInterval[key]) {
        // 清空单个属性
        if (state.viewInterval[key][field]) {
          // console.log('清空组件下的单个缓存interval--', key, '/', field);
          clearInterval(state.viewInterval[key][field])
          delete state.viewInterval[key][field]
        } 
        // 清空组件下的所有属性
        else {
          // console.log('清空组件下的所有缓存interval--', key);
          for (const interval_key in state.viewInterval[key]) {
            if (Object.hasOwnProperty.call(state.viewInterval[key], interval_key)) {
              clearInterval(state.viewInterval[key][interval_key])
            }
          }
          delete state.viewInterval[key]
        }
      } 
      // 清空所有
      else {
        console.log('清空所有组件下的缓存interval');
        for (const view_key in state.viewInterval) {
          if (Object.hasOwnProperty.call(state.viewInterval, view_key)) {
            for (const interval_key in state.viewInterval[view_key]) {
              if (Object.hasOwnProperty.call(state.viewInterval[view_key], interval_key)) {
                clearInterval(state.viewInterval[view_key][interval_key])
              }
            }
          }
        }
        state.viewInterval = {}
      }
    }
  },
  getters: {
    getSocketList: (state, getters, rootState, rootGetters) => (key) => {
      return state.socketList
    },
    /**
     * 缓存组件Interval
     * @param {*} key 组件
     * @param {*} field 组件中的某个key
     */
    getViewInterval: (state, getters, rootState, rootGetters) => (data) => {
      let key = data.key,
      field = data.field,
      val = data.val

      if (state.viewInterval[key]) {
        return state.viewInterval[key][field]
      }
      return state.viewInterval
    }
  }
}

export default SocketCache
