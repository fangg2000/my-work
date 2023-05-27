import BScroll from "@better-scroll/core";
import PullDown from "@better-scroll/pull-down";
import ReadWebSocket from "@/socket/readSocket";
import { DateTime } from "@/util/timeUtil.js";
import { AES } from "crypto-js";

BScroll.use(PullDown);
// 初始化默认读取socket
const ReadSocket = new ReadWebSocket({
  socketId: "default"
});

function generateData() {
  let e = [{
    chatId: "685179408650579968",
    client: "C80759eeb",
    companyCode: "S20211219",
    content: "好友已经上线，现在可以开始聊天了",
    initTime: (new Date).getTime(),
    server: "S80d10cf7",
    status: "1",
    type: "0",
    updateReceptorCount: 0,
    updateStarterCount: 0
  }];
  return e
}

const TIME_BOUNCE = 800,
REQUEST_TIME = 1000,
// 下拉距离px
THRESHOLD = 60,
STOP = 56,
SYSTEM_CODE = "SYSTEM";
let STEP = 0;

export default {
  name: "Read",
  components: {},
  data() {
    return {
      userInfo: null,
      moreVisible: !0,
      beforePullDown: true,
      isPullingDown: false,
      dataList: [],
      lastInfo: "",
      firstLoad: true,
      repeatLoad: false,
      rightType: "",
      selectUser: null,
      userInterval: null,
      loaderStart: false,
      timeFormat: new DateTime("yyyy/MM/dd hh:mm:ss"),
      previewUrl: "#",
      previewUrlList: [],
      cgUserList: [],
      userPPList: [],
      remindNum: 0
    }
  },
  props: ["userCode", "user", "socketStatus", "destorysocket", "loader"],
  created() {
    // <img>标签中的onclick事件关联
    window.imgMousemove = this.imgMousemove
  },
  mounted() {
    var e = this;
    let t = 3e3;
    this.userInterval = setInterval(() => {
      e.initUserInfo(e),
      t += 3e3
    },
    t),
    this.$store.commit("updateVmInterval", this.userInterval)
  },
  watch: {
    // 监听userCode的值变化
    user(e, t) {
      null != e && null != e.userCode && "" != e.userCode && (this.selectUser = e, null != t && (ReadSocket.destoryWS(), this.$store.commit("removeSocketIds", t.userCode)), this.newSocket(e), this.remindNum = 0, 2 != e.userType && 3 != e.userType || this.getCGUserList(e.userCode))
    },
    socketStatus: function(e, t) {
      null != this.selectUser && (e ? 0 == ReadSocket.socketState() && (this.firstLoad = !1, this.repeatLoad = !0, ReadSocket.destoryWS(), this.initConnection(this.selectUser)) : (ReadSocket.destoryWS(), this.$store.commit("removeSocketIds", this.selectUser.userCode)))
    },
    destorysocket: function (newV, oldV) {
      console.log('关闭websocket--', newV);
      if (newV != undefined && newV) {
        ReadSocket.destoryWS()
      }
    },
    loader: function (newV, oldV) {
      console.log('开始加载信息--', newV);
      this.loaderStart = newV
    },
  },
  computed: {
    getUserInfo() {
      return this.$store.getters.getUserInfo
    },
    getPath() {
      return process.env.BASE_URL
    },
    getDataList() {
      let s = this;
      return this.dataList.forEach((e, t) => {
        e.username = s.showUsername(e.client),
        e.profilePicture = s.showUserPP(s, e.client)
      }),
      this.dataList
    }
  },
  methods: {
    showUserPP(s, i) {
      let o = "";
      return this.cgUserList.forEach((e, t) => {
        e && e.userCode == i && s.userPPList[t] && (o = s.userPPList[t])
      }),
      o
    },
    showUsername(t) {
      let s = "匿名用户";
      return this.cgUserList.forEach(e => {
        e && e.userCode == t && e.username && (s = e.username)
      }),
      s
    },
    getCGUserList(e) {
      let t = this;
      this.$ajaxRequest({
        url: "/chat/getCGUserList",
        method: "POST",
        data: {
          userCode: this.userInfo.userCode,
          chatGroupCode: e
        }
      }).then(e => {
        1 == e.code && (t.cgUserList = e.data.cguList, t.userPPList = e.data.ppList)
      })
    },
    previewImgClick() {},
    // 图片点击
    imgMousemove(e, t) {
      // console.log('图片点击--', view.src);
      var s = e.clientX,
      e = e.clientY;
      let i = document.getElementById("previewImg");
      i.style.position = "fixed",
      i.style.left = s - 5 + "px",
      i.style.top = e - 5 + "px",
      // 放大镜
      i.style.cursor = "zoom-in",
      i.firstChild.firstChild.style.cursor = "zoom-in",
      this.previewUrl = t.src,
      this.previewUrlList = [],
      this.previewUrlList[0] = t.src
    },
    initUserInfo(e) {
      var t = e.getUserInfo;
      console.log("read获取用户信息--", JSON.stringify(t)),
      null != t && (e.userInfo = t, t = ReadSocket.getSelectUser(), console.log("websocket用户信息--", JSON.stringify(t)), null != t && (e.selectUser = t, e.newSocket(e.selectUser), e.$emit("update:user", t)), clearInterval(e.userInterval))
    },
    // 查看更多
    loadMoreInfo() {
      if (null != this.userInfo && null != this.selectUser) {
        let t = this,
        e = this.$listeners.initParams(this.selectUser);
        e.pageSize = 10,
        e.initTime = null != this.dataList && 0 < this.dataList.length ? this.dataList[0].initTime: null,
        this.$ajaxRequest({
          url: "/chat/readMore",
          method: "POST",
          data: e
        }).then(function(e) {
          console.log("读取更新数据--", e),
          1 == e.code && (e.data && 0 < e.data.length ? t.dataList = e.data.concat(t.dataList) : t.moreVisible = !1)
        })
      }
    },
    scrollTopWithTimeout(e) {
      // 滚动条到最下面
      let i = e.$refs.bsWrapper;
      if (null != i && null != i.scrollHeight) {
        let e = i.scrollHeight,
        t = i.scrollTop,
        s;
        s = setInterval(function() { (t += 1) < e ? i.scrollTop = t: clearInterval(s)
        },
        6)
      }
    },
    // 此方法接收后台推送的消息
    socketMsg(s) {
      let i = this;
      console.log("WebSocket后台消息：", s.data);
      // 消息需要解密
      // let aesKey = this.$store.getters.getAesKey
      // let decrypt_msg = AES.decrypt(e.data.replaceAll('\r\n', ''), aesKey)
      // console.log('解密后消息--', decrypt_msg);
      
      s = JSON.parse(s.data);
      if (1 == s.code) {
        if (this.repeatLoad) return this.repeatLoad = !1;
        var o = /<img[^>]+src="[^"]+"[^>]*>/g;
        if (this.firstLoad) {
          this.firstLoad = !1;
          var e = s.data.chatList,
          t = e[e.length - 1];
          null != t && "" != t.content && (e.forEach(e => {
            e.content.match(o) && (e.content = e.content.replaceAll("<img", '<img onmousemove="imgMousemove(event, this)"')),
            i.dataList = i.dataList.concat(e)
          }), this.lastInfo = this.dataList[this.dataList.length - 1], this.scrollTopWithTimeout(i), 0 < e.length && this.checkUnRead(e[e.length - 1]))
        } else {
          let e = s.data.chatList,
          t = this.dataList[this.dataList.length - 1];
          e.forEach(e => {
            e && 0 < e.content.length && (null == t || e.initTime > t.initTime) && (e.content.match(o) && (e.content = e.content.replaceAll("<img", '<img onmousemove="imgMousemove(event, this)"')), i.dataList = i.dataList.concat(e), i.scrollTopWithTimeout(i), i.loaderStart = !1, i.$emit("update:loader", !1))
          }),
          0 < e.length && i.checkUnRead(e[e.length - 1])
        }
      }
    },
    // 连接websocket
    initConnection(t) {
      var s = this.getUserInfo;
      if (null != s) {
        let e = this;
        ReadSocket.socketState() && ReadSocket.destoryWS(),
        // 清空之前显示数据
        this.dataList = [],
        // 开启websocket链接
        ReadSocket.initWebSocket(t, s, this.socketMsg),
        setTimeout(function() {
          ReadSocket.socketState() && (e.$store.commit("updateSocketIds", t.userCode), e.$store.commit("socket/updateSocketList", ReadSocket))
        },
        1e3)
      }
    },
    checkUnRead(e) {
      // 更新列表已方"未读"全部为“已读”
      if ("1" === e.status) {
        let e = this.dataList;
        var s = this;
        e.forEach((t, e) => {
          try {
            null != t.status && "0" === t.status && (t.status = "1", s.dataList[e] = t)
          } catch(e) {
            console.log("error--", t)
          }
        })
      } 
      // 如果用户在线，但未读信息则提醒用户
      else if (0 < this.remindNum) {
        let e = this.$store.getters.getNewContactList;
        null != this.selectUser && 1 == this.selectUser.onlineStatus && e.includes(this.selectUser.userCode) && (console.log("用户在线且联系过--", JSON.stringify(this.selectUser)), this.$emit("onlineUnread", this.selectUser.userCode), this.remindNum = 0)
      } else this.remindNum = this.remindNum + 1
    },
    checkSendType(e) {
      // console.log('判断用户发送类型--', in_user)
      // console.log('当前用户为空--', this.userInfo)
      var t;
      null != this.userInfo && (t = this, 0 == e.userType && 1 == this.userInfo.userType ? this.rightType = "1": 1 === e.userType ? 0 === this.userInfo.userType ? this.rightType = "0": 1 == this.userInfo.userType && this.$ajaxRequest({
        url: "/user/getSendType",
        method: "POST",
        data: {
          server: this.userInfo.userCode,
          client: e.userCode
        }
      }).then(function(e) {
        1 == e.code ? t.rightType = e.data: t.rightType = "1"
      }) : 2 === e.userType ? this.rightType = "2": 3 === e.userType && (this.rightType = "3"))
    },
    newSocket(e) {
      ReadSocket.destoryWS(),
      this.$store.commit("removeSocketIds", e.userCode),
      // 判断发送类型
      this.checkSendType(e),
      this.initConnection(e),
      // 初始化聊天内容
      this.firstLoad = true
    }
  }
};