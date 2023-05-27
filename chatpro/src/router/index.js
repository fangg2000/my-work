import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from '@/components/Login'
import sessionStore from '@/util/session'
import store from '@/store'
import { checkFromReload } from '@/axios/api'

// const Home = () => import('@/components/home')


Vue.use(VueRouter)

const ROUTES = [
  {
    path: '/Login',
    name: 'Login',
    component: Login
  },
  {
    path: '/LoginOut',
    name: 'LoginOut',
    redirect: 'Login'
  },
  {
    path: '/Home',
    name: 'Home',
    component: resolve => require(['@/components/home'], resolve)
  }
]

const router = new VueRouter({
  base: '/chatweb',
  mode: 'history',
  routes: ROUTES
})

// 路由前调用
router.beforeEach((to,from,next)=>{
  //console.log(to)
  //console.log(ticket)

  const ticket = sessionStore.getTicket()

  if (ticket == null) {
    if (to.path == '/Login') {
      next()
    } else if (to.name != 'Login') {
      next({path:'/LoginOut'})
    }
  } else {
    let ok = store.state.prepare
    console.log('路由开始前--', ok)

    if (ok == false) {
      // 获取用户信息
      checkFromReload(to,from,next)
    } else {
      go(to,from,next)
    }
  }

  next()
})

let go = function (to,from,next) {
  if (to.path == '/Login') {
    next({path:'/Home'})
  }
}

export default router
