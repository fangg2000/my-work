const sessionStorage = window.sessionStorage;
var session = {
  getItem(key) {
    try {
      return JSON.parse(sessionStorage.getItem(key));
    } catch (err) {
      return null;
    }
  },
  setItem(key, val) {
    sessionStorage.setItem(key, JSON.stringify(val));
  },
  removeItem(key) {
    sessionStorage.removeItem(key);
  },
  clear() {
    sessionStorage.clear();
  }
}

var sessionStore = {
  debug: true,
  getItem(key) {
    return session.getItem(key)
  },
  setItem(key, val) {
    session.setItem(key, val)
  },
  getTicket() {
    //if (this.debug) console.log('setMessageAction triggered with', newValue)
    return session.getItem('ticket')
  },
  setTicket(newValue) {
    if (this.debug) console.log('更新ticket值为', newValue)
    session.setItem('ticket', newValue)
  },
  delTicket() {
    if (this.debug) console.log('删除ticket值')
    session.removeItem('ticket')
  },
  clearSession() {
    if (this.debug) console.log('clear ticket')
    //this.state.ticket = ''
    session.clear()
  }
}

const LOCAL_STORAGE = window.localStorage;

export function localStore(params) {
  
}

localStore.prototype.getItem = function (key) {
  try {
    return JSON.parse(LOCAL_STORAGE.getItem(key));
  } catch (err) {
    return null;
  }
}

localStore.prototype.setItem = function (key, val) {
  LOCAL_STORAGE.setItem(key, JSON.stringify(val));
}

localStore.prototype.removeItem = function (key) {
  LOCAL_STORAGE.removeItem(key);
}

localStore.prototype.clear = function () {
  LOCAL_STORAGE.clear();
}

export default sessionStore