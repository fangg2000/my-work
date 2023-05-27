<template>
  <div id='Home'>
    <div class="b-design">
      <img :src="getLocalStoreImg" width="100%" height="100%" />
    </div>
    <chat_view :socketStatus="status" 
    :newConUsers="socketBackData" 
    :xyOffset="xyOffset"
    v-if="isRouterAlive"
    @notOnlineUserCheck2Home="checkNotOnlineUser"></chat_view>
  </div>
</template>

<script>
  import ajaxRequest from '@/axios/ajax'
  import chat_view from './chat'
  import OnlineSocket from '@/socket/onlineSocket'
  import { checkLoginIdentifier } from '@/axios/api'
  import sessionStore from '@/util/session'
  import { localStore } from '@/util/session'

  export default {
    name: 'Home',
    provide(){
      return{
        reload: this.reload //2、定义状态
      }
    },
    data() {
      return {
        'name': 'sa',
        'age': 18, 
        status: true,
        userInfo: null,
        myInterval: null,
        count: 0,
        outTime: 15*60, // 倒数N分钟,
        around_time: 3*60,  // N分钟后再触发一次
        moveFlag: true,
        startFlag: false,
        xyOffset: null,
        thisSocket: null,
        socketBackData: {},
        userInterval: null,
        isRouterAlive:true,
        localStora: new localStore(),
        NOlineUserMap: {}
      }
    },
    props: ['sex'],
    computed: {
      getLocalStoreImg () {
        let bgImg = this.localStora.getItem('bgImg')
        return bgImg&&bgImg.base64?bgImg.base64:'/static/img/bg/01.jpg'
      }
    },
    created() {
      let view = this;
      /* let thisInterval = this.$store.getters.getHomeInterval
      // console.log('缓存的HomeInterval--', thisInterval)

      if (thisInterval != null) {
        clearInterval(thisInterval)
        this.$store.commit('updateHomeInterval', null)
      } */
      // 销毁所有缓存的线程
      this.$store.commit('clearVmInterval')

      /* ajaxRequest({
        url: '/chat/getNotReadUserList',
        method: 'POST',
      }).then(function(res){
        view.$slideMsg.info('读取未读数据：'+res.msg)
      }) */

      this.userInterval = setInterval(() => {
        view.initUserInfo(view)
      }, 2000);
      this.$store.commit('updateVmInterval', this.userInterval)

      // 开启线程监听用户操作
      // this.actionListener()
    },
    methods: {
      // 页面刷新方法
      reload(){
        // 定义方法，调动让他销毁掉
        this.isRouterAlive = false
        this.$nextTick(()=>{
          this.isRouterAlive = true
        })
      },
      initUserInfo (view) {
        let user = view.$store.getters.getUserInfo
        console.log('home获取用户信息--', user);

        if (user != null) {
          view.userInfo = user
          view.startOS(view)
          clearInterval(view.userInterval)
          
          // 开启线程监听用户操作
          view.actionListener(view)

          // 初次登录时
          if (user.loginAgain == 0 && user.userType == 1) {
            view.$slideMsg.warn('尊敬的用户您好，为保证您的信息安全，强烈建议您到“用户中心”绑定WEB端信息', '初次登录提示', 0)
          }
        }
      },
      socketMsg (e) {
        // console.log('onlineSocket后台消息：', JSON.stringify(e.data))

        let backData = JSON.parse(e.data)
        if (backData.code == 1) {
          this.socketBackData = backData.data
        }
      },
      async beforeCheckIdentifier (view, user) {
        let flag = await checkLoginIdentifier().then(function (res) {
          if (res.code !== 1) {
            return false
          }
          return true
        })
        return flag
      },
      afterCheckIdentifier (view, user) {
        // 建立连接
        if (OnlineSocket.socketState() == false) {
          OnlineSocket.destoryWS()
          view.thisSocket = OnlineSocket.initWebSocket(user.userCode, user.userType, user.ticket, view.socketMsg)
          setTimeout(function(){
            if (OnlineSocket.socketState()) {
              OnlineSocket.checkOnline(view.NOlineUserMap)
            }
          }, 1000)
        }
        view.$store.commit('updateOnlineState', true)
        view.status = true
        view.startFlag = true
        return true
      },
      async startOS(view) {
        let user = view.userInfo

        if (user != null) {
          let online = view.$store.getters.onlineState

          if (online == false) {
            // 如果识别码不匹配则返回到登录页面
            let thisInterval = view.$store.getters.getHomeInterval
            // 判断登录识别码
            if (thisInterval != null) {
              let checkFlag = await view.beforeCheckIdentifier()
              if (checkFlag == false) {
                view.$slideMsg.message('登录异常', 'error')
                clearInterval(thisInterval)
                view.$store.commit('updateHomeInterval', null)

                // 删除ticket(此处要删除ticket，在登录后再重新添加)
                sessionStore.delTicket()
                view.$router.push({ path: '/LoginOut' })
              } else {
                return view.afterCheckIdentifier(view, user)
              }
              return true
            } else {
              // console.log('用户在线状态--', online)
              return view.afterCheckIdentifier(view, user)
            }
          }
        }
        return false
      },
      actionListener(view) {

        //页面倒计时
        view.myInterval = setInterval(function go() {
          // console.log(view.count)
          view.count = view.count+1;
          if (view.count == view.outTime) {
            // alert('您长时间未操作页面');
            // 此处处理后续操作
            view.$slideMsg.warn('您长时间没有操作……', view.count)

            OnlineSocket.destoryWS()
            view.$store.commit('updateOnlineState', false)
            view.status = false
            view.moveFlag = false
            view.startFlag = false
          } else if (view.count > view.outTime) {
            view.moveFlag = false   
            view.count = view.count-view.around_time
          }
        }, 1000)
        
        view.$store.commit('updateHomeInterval', view.myInterval)

        //监听鼠标(此方法不能在别的地方覆盖，否则不起作用)
        // var x;
        // var y;
        let homeElement = document.getElementById('Home')
        homeElement.onmousemove = function (event) {
          /* Act on the event */
          var x1 = event.clientX;
          var y1 = event.clientY;
          // console.log('HOME页面', x1+'/'+y1)
          let screenWidth = document.body.clientWidth

          if (x1 <= 5) {
            // console.log('这是左边界判断--',x1+'/'+y1+'/'+screenWidth)
            view.xyOffset = x1+'-'+y1
          }

          view.count = 0;

          if (view.moveFlag == false) {
            // console.log('这是重连判断--',x1+'/'+y1)
            view.moveFlag = view.startOS(view)
          }
        }

        homeElement.onmousedown = function (event) {
          var x1 = event.clientX;
          var y1 = event.clientY;
          // console.log(x1+'/'+y1, view.count)
          view.count = 0;
        }

      },
      // 不在线用户状态更新
      checkNotOnlineUser (listUsers) {
        let view = this
        console.log('不在线用户--', JSON.stringify(listUsers))
        this.NOlineUserMap = listUsers
        if (this.thisSocket != null && this.thisSocket.socketState()) {
          this.thisSocket.checkOnline(listUsers)
        }
      }
    },
    components: {
      chat_view
    }
  }


</script>
<style scoped>
  #Home {
    width: 100%;
    height: 100%;
    margin: 0px;
    padding: 0px;
  }
</style>