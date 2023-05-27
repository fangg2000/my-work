// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import SLIDEMSG from './base/slideMsg'
import AJAXREQUEST from '@/axios/ajax'
import store from './store'
import 'animate.css'
import VueLazyComponent from '@xunlei/vue-lazy-component'

// 组件懒加载插件--全局注册
Vue.use(VueLazyComponent)

Vue.config.productionTip = false

//Vue.component('userInfo', userInfo)


// 引入element ui
Vue.use(ElementUI, {zIndex: 100 });

// 自定义消息提示框
Vue.prototype.$slideMsg = SLIDEMSG
// 定义全局请求方法
Vue.prototype.$ajaxRequest = AJAXREQUEST
//Vue.use(SLIDEMSG);

// 全局替换
Vue.prototype.replaceAll = function (input, s1, s2) {
  if (input != undefined && input != null && input.trim() != '') {
    return input.replace(new RegExp(s1,"gm"),s2);
  }
  return input
}
String.prototype.replaceAll = function (s1, s2) {
  if (this != undefined && this != null && this.length > 0) {
    return this.replace(new RegExp(s1,"gm"),s2);
  }
  return this
}

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  components: { App },
  template: '<App/>',
  methods: {
    reLoad () {
      let result = null

      /* this.$ajaxRequest({
        url: '/user/getUserInfo',
        method: 'POST', 
        data: {userCode: ''}
      }).then(function(res){
        //this.$slideMsg.info('读取未读数据：'+res.msg)
        if (res.code == 1) {
          store.commit('updateUserInfo', res.data)
          result = res.data
        }

        return result
      }) */
    }
  },
  created () {
    // console.log('页面更新了')

    // let user = store.getters.getUserInfo
    // console.log('路由开始前--', JSON.stringify(user))
    
  }
})
