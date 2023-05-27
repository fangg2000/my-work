import {Notification, Message} from 'element-ui'

// 必须注意--在axios中（或封装的相关方法中）使用$slideMsg时（其他$函数也一样），
// 将vue的this使用var xxx = this;表示，不然会出异常

//应用插件
//Vue.prototype.$notify = Notification;

const SLIDEMSG = function(options) {
  if (options.type == 'message') {
    return message(options.message, options.title)
  } 
  else if (options.type == 'success') {
    return success(options.message, options.title)
  }
  else if (options.type == 'warn') {
    return warning(options.message, options.title, options.timeout)
  }
  else if (options.type == 'info') {
    return info(options.message, options.title, options.timeout)
  }
  else if (options.type == 'error') {
    return error(options.message, options.title)
  }

  return;
}

const type_arr = ['success', 'warn', 'info', 'error', 'message'];

type_arr.forEach(type => {
  SLIDEMSG[type] = (msg, title, timeout) => {
    let options = {};
    /* if (typeof options === 'string') {
      options = {
        message: options
      };
    } */
    options.message = msg;
    options.title = title;
    options.type = type;
    options.timeout = timeout;
    return SLIDEMSG(options);
  };
});


function warning(msg, title, timeout) {
  Promise.resolve().then(() => {
    Notification.warning({ 
      title: title!=undefined?title:'警告', 
      message: msg!=undefined?msg:'有消息警告', 
      position: 'bottom-right',
      duration: timeout!=undefined?timeout:6500
    })
  }) 
}

function success(msg, title) {
  Promise.resolve().then(() => {
    Notification.success({ title: title!=undefined?title:'提示', message: msg!=undefined?msg:'操作成功', position: 'bottom-right' })
  }) 
}

function info(msg, title, timeout) {
  Promise.resolve().then(() => {
    Notification.info({ 
      title: title!=undefined?title:'提示', 
      message: msg!=undefined?msg:'', 
      position: 'bottom-right',
      duration: timeout!=undefined?timeout:4500
    })
  }) 
}

function error(msg, title) {
  Promise.resolve().then(() => {
    Notification.error({ title: title!=undefined?title:'提示', message: msg!=undefined?msg:'操作失败', position: 'bottom-right' })
  }) 
}

// 此为上面居中显示消息，通过不同类型（warning/error，默认为success）提示不同消息
function message(msg, type) {
  Promise.resolve().then(() => {
    Message({
      showClose: true,
      message: (msg!=undefined?msg:'操作成功'),
      type: (type!=undefined?type:'success')
    });
  }) 
}

export default SLIDEMSG