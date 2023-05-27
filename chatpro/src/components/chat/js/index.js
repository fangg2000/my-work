import read_view from "@/components/chat/read";
import ChatUserListView from "@/components/chat/rightDrawer";
import { chatWrite } from '@/axios/api'
import sessionStore from "@/util/session";
import userCenter from "@/components/chat/userCenter";
import biaoqing from "@/base/biaoqing.js";
import write_msg_view from "@/components/chat/ballbox/write_msg.vue";
import { DateTime }
from "@/util/timeUtil.js";

// 混入
import bbMixin from "@/components/chat/ballbox/bb_mixin";
import csMixin from "@/components/company/cs_mixin.js";

const SYSTEM_CODE = "SYSTEM";
let view = null;

export default {
  name: "Chat",
  mixins: [bbMixin, csMixin],
  components: {
    read_view: read_view,
    userCenter: userCenter,
    ChatUserListView: ChatUserListView,
    write_msg_view: write_msg_view
  },
  data() {
    return {
      activeBox: null,
      selectCode: "",
      selectUser: null,
      wirteInfo: "",
      writeMsgContent: "",
      maxLength: 1024,    // 输入最大长度
      userInfo: null,
      newInfo: null,
      status: !0,
      editDrawer: !1,
      rightDrawer: !1,
      lockDrawer: !1,
      direction: "ltr",
      socketBackData: {},   // 新用户来信列表
      firstContactList: [], // 登录后第一次联系的用户列表
      newFocusUser: null,
      outConfirm: null,
      userInterval: null,
      showMore: !1,
      writeOver: !1,
      emojiVisible: !1,
      emojiVisibleMsg: !1,
      destoryWS: !1,
      biaoqingList: biaoqing,
      emojiList: [],
      emojiType: 0,         // 0为主输入框表情，1为消息框表情
      previewUrl: "#",
      previewUrlList: [],
      ucgApplyInfoList: [],
      waitDoVisible: !1,
      inputPholderVisible: !0,
      timeFormat: new DateTime("yyyy/MM/dd hh:mm:ss"),
      nowMillSeconds: 0,
      byMillSeconds: 3e3
    }
  },
  props: ["socketStatus", "xyOffset", "newConUsers"],
  filters: {
    showNameFilter(e) {
      return e ? 0 == e.userType || 1 == e.userType ? "@" + e.username: 2 == e.userType || 3 == e.userType ? e.chatGroupName: "": ""
    }
  },
  created() {
    // 登录用户新联系用户列表
    this.firstContactList = this.getNewContactList,
    // 键盘响应
    this.writeByKeydown(),
    // 销毁socket缓存
    this.destoryRWS()
  },
  mounted() {
    view = this;
    let time_out = 1000
    this.userInterval = setInterval(() => {
      view.initUserInfo(view)
      time_out += 1000
    }, time_out);
    this.$store.commit("updateVmInterval", this.userInterval),
    this.nowMillSeconds = this.timeFormat.getMillSeconds()
  },
  computed: {
    // 映射带有命名空间的state，第一个参数模块名
    // ...mapState('funcPer', {
    //   funcPermission: state => state.funcPermission
    // }),
    // 映射带有命名空间的state，第一个参数模块名
    // ...mapGetters('funcPer', {
    //   // 把 `this.doneCount` 映射为 `this.$store.getters.doneTodosCount`
    //   rootPer: 'getRootPer'
    // }),
    getUserInfo() {
      return this.$store.getters.getUserInfo
    },
    getNewContactList() {
      return this.$store.getters.getNewContactList
    },
    getFuncPermission() {
      return this.$store.getters.getFuncPermission
    },
    getPath() {
      return process.env.BASE_URL
    },
    getUCGApplyList() {
      return 0 < this.ucgApplyInfoList.length ? this.ucgApplyInfoList.length: null
    }
  },
  watch: {
    rightDrawer: function(e, t) {
      if (1 == e) {
        let e = this.$refs.rightListDrawer;
        e.style.right = "0px"
      }
    },
    socketStatus: function (newV, oldV) {
      if (this.selectUser != null) {
        this.status = newV
      }
    },
    xyOffset: function(e, t) {
      null != e && (this.editDrawer = !0)
    },
    // 从Home传来
    newConUsers: function(t, e) {
      if (null != t) {
        this.socketBackData = t;
        let e = this.checkActiveUser(t.listUC);
        null != e && (console.log("新用户列表--", JSON.stringify(e)), this.ballGo(e));
        t = t.listUG,
        t = this.formatCGMsg(t);
        null != t && null != (e = this.checkActiveUser(t)) && (console.log("新群消息列表--", JSON.stringify(e)), this.ballGo(e))
      }
    },
    selectUser: function (newV, oldV) {
      if (newV != null) {
        let view = this
        // 主输入框拖动
        setTimeout(() => {
          view.dragWElement(document.getElementById('mainWirte'));
          view.zoomElement();
        }, 1000);
      }
    }
  },
  methods: {
    checkUCGApply(s, e) {
      let t = this;
      this.$ajaxRequest({
        url: "/chat/postCGApplyCheck",
        method: "POST",
        data: {
          userCode: this.userInfo.userCode,
          chatGroupCode: s.gc,
          applyCode: s.uc,
          checkStatus: e
        }
      }).then(e => {
        if (1 == e.code) {
          t.$slideMsg.info(e.msg);
          let i = null;
          t.ucgApplyInfoList.forEach((e, t) => {
            e.uc == s.uc && e.gc == s.gc && (i = t)
          }),
          t.ucgApplyInfoList.splice(i, 1)
        }
      })
    },
    changeText(e) {
      // console.log('输入内容a--', e.currentTarget.innerHTML);
      // console.log('输入内容b--', this.wirteInfo);
      // this.wirteInfo = e.currentTarget.innerHTML
      this.$refs.writeLength.innerText = e.currentTarget.innerHTML.length + "/" + this.maxLength,
      0 < e.currentTarget.innerHTML.length ? this.inputPholderVisible = !1 : this.inputPholderVisible = !0
    },
    holderClick(e) {
      this.$refs.wirteInfo.focus()
    },
    //压缩图片
    compress(e, t) {
      for (var i = e.height,
      s = e.width,
      n = document.createElement("canvas"), s = (n.width = s, n.height = i, n.getContext("2d")), i = (s.fillStyle = "#FFF", s.fillRect(0, 0, n.width, n.height), s.drawImage(e, 0, 0, n.width, n.height), n.toDataURL("image/jpeg", .1 < t ? t: .1)), s = new FormData, e = i.split(","), n = e[0].match(/:(.*?);/)[1], o = atob(e[1]), r = o.length, l = new Uint8Array(r); r--;) l[r] = o.charCodeAt(r);
      t = new Blob([l], {
        type: n
      });
      return s.append("imgFile", t, t.type),
      s
    },
    uploadImage(e) {
      let s = this,
      n = this.$refs.wirteInfo.innerHTML;
      var t, i = new FormData;
      //必须以这种方式获取，以JQuery的方式获取不到
      (1024 * 512) < e.size ? (t = ((250 * 1024) / e.size).toFixed(2), i = s.compress(e.raw, parseFloat(t))) : i.append("imgFile", e.raw, e.type),
      s.$ajaxRequest({
        method: "post",
        url: "/chat/sendImgCache",
        data: i,
        headers: {
          "Content-Type": "multipart/form-data",   //并且header中的Content-type必须是multipart/form-data类型
        }
      }).then(function(res) {
        // console.log('图片发送结果--', JSON.stringify(res));
        if (res.code === 1) {
          let imgPath = res.data;
        
          if (imgPath != '') {
            imgPath = view.getPath + imgPath.replace("\\", "/");

            let image = new Image();
            image.src = imgPath;
            image.onload = function() {
              let img_width = image.width
              if (img_width > 500) {
                view.wirteInfo = info + '<img src="' + imgPath + '" width="65%" >'
              } else {
                view.wirteInfo = info + '<img src="' + imgPath + '">'
              }
            }
          }
        }
      })
    },
    fileChange(e) {
      // 发送图片处理
      this.uploadImage(e)
    },
    bqNameChange(type) {
      this.biaoqingList.forEach(list => {
        if (list.name == type) {
          this.emojiList = list.iconArr
        }
      });
    },
    addEmoji (icon, type) {
      if (type == 0) {
        this.wirteInfo = this.$refs.wirteInfo.innerHTML + icon
        this.emojiVisible = false
      } else {
        this.$refs.sendMsg.value = this.$refs.sendMsg.value + icon
        this.emojiVisibleMsg = false
      }
    },
    showEmoji(e, t) {
      this.bqNameChange("normal"),
      0 == t ? this.emojiVisible = !this.emojiVisible: this.emojiVisibleMsg = !this.emojiVisibleMsg
    },
    zoomElement() {
      let t = 0,
      i = 0,
      s = 0,
      n = 0,
      o = document.getElementById("mainWirte"),
      r = document.getElementById("writeInfo"),
      e = document.getElementById("zoomWrite");
      function l(e) {
        e = e || window.event,
        r.style.width = t + e.clientX - s + "px",
        r.style.height = i + e.clientY - n + "px"
      }
      function a(e) {
        e = e || window.event,
        o.setAttribute("draggable", !0),
        document.removeEventListener("mousemove", l, !1)
      }
      e.onmousedown = function(e) {
        e = e || window.event,
        t = parseInt(r.style.width.replace("px", ""), 10),
        i = parseInt(r.style.height.replace("px", ""), 10),
        s = e.clientX,
        n = e.clientY,
        o.setAttribute("draggable", !1),
        // 删除拖动事件
        document.addEventListener("mousemove", l, !1),
        document.addEventListener("mouseup", a, !1)
      },
      e.onmouseup = a
    },
    dragWElement(t) {
      let i,
      s,
      n = 0,
      o = 0;
      t.onmousedown = function(e) {
        e = e || window.event,
        n = e.clientX,
        o = e.clientY
      },
      // 当拖完p元素输出一些文本元素和重置透明度
      t.addEventListener("dragend",
      function(e) {
        e = e || window.event,
        i = n - e.clientX,
        s = o - e.clientY,
        t.style.top = t.offsetTop - s + "px",
        t.style.left = t.offsetLeft - i + "px"
      })
    },
    // 用户在线未读消息提醒
    remindOtherSide(e) {
      console.log("用户在线未读消息提醒--", e);
      var t = this.firstContactList.indexOf(e); - 1 < t && (this.firstContactList.splice(t, 1), this.$store.commit("removeNewContact", e))
    },
    sendClick(e) {
      // 提交内容
      let i = this.writeMsgContent,
      // console.log('发送内容--', e.currentTarget.id, '/', e.target.getAttribute('id'));
      s = (console.log("发送内容--", this.$refs.sendMsg), this.$refs.sendMsg.$el.dataset.code);
      this.bbUserList.forEach((e, t) => {
        // console.log('发送用户信息--', JSON.stringify(user));
        // 提交内容
        e.userCode == s && view.writeSubmit(e, i, 1)
      })
    },
    // 泡泡球消息框响应
    msgKeydown(e) {
      let n = this;
      if ("Enter" == e.key && e.shiftKey) {
        let s = this.writeMsgContent;
        if (0 < s.length) {
          let i = this.$refs.sendMsg.$el.dataset.code;
          this.bbUserList.forEach((e, t) => {
            e.userCode == i && (s.startsWith("<p>") && s.endsWith("</p>") && (s = s.slice(3, s.length - 4)), n.writeSubmit(e, s, 1))
          })
        }
      }
    },
    // 映射带有命名空间的mutations，第一个参数模块名
    // ...mapMutations('funcPer' ,[
    //   'updateFuncPermission'
    // ]),
    newRWS(socket) {
      this.$store.commit('socket/add2SocketList', socket)
    },
    closeRWS() {
      this.destoryRWS(),
      this.wirteInfo = "",
      this.selectUser = null
    },
    destoryRWS() {
      // 清空缓存
      this.$store.commit("socket/destroySocket"),
      this.$store.commit("clearUgChatList")
    },
    showUser() {
      console.log("用户缓存信息:/n", this.$store.getters.getUserInfo)
    },
    changeCode(code) {
      this.selectCode = code
    },
    // 右边抽屉点击用户后，如果有用户泡泡球消息则销毁
    changeUser(inUser) {
      // 清空输入框内容
      this.wirteInfo = ''
      // 销毁泡泡球（如果存在）
      this.destoryBall(inUser)

      // 如果联系人已经是当前对话人
      if (this.selectUser != null && inUser.userCode == this.selectUser.userCode) {
        return false
      }

      this.selectUser = inUser
      return true
    },
    getNotReadUserList() {
      let t = this;
      this.$ajaxRequest({
        url: "/user/getNotReadUserList",
        method: "POST",
        data: {
          userCode: this.userInfo.userCode,
          userId: this.userInfo.userId
        }
      }).then(function(e) {
        console.log("未读的用户信息--", JSON.stringify(e.data)),
        1 == e.code && (t.ballGo(e.data), 0 == t.dragBall && 1 == t.userInfo.showBall && 0 < e.data.length && (t.socketBackData = {
          listUC: e.data
        }))
      })
    },
    wirteHandler(e) {},
    loginOut() {
      let t = this;
      if (1 == this.userInfo.outConfirm) {
        const e = this.$createElement;
        this.$msgbox({
          title: "消息",
          message: e("p", null, [e("span", null, "确定退出登录？"), e("el-checkbox", {
            style: "position: relative;left: 180px;",
            on: {
              change: function(e) {
                // console.log("change--", e),
                t.outConfirm = e ? 0 : 1
              }
            }
          }, "不再提示")]),
          showCancelButton: !0,
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          beforeClose: (e, t, i) => {
            i()
          }
        }).then(e => {
          this.$ajaxRequest({
            url: "/user/loginOut",
            method: "POST",
            data: {
              userCode: this.userInfo.userCode,
              outConfirm: this.outConfirm,
              configId: this.userInfo.configId
            }
          }).then(function(e) {
            // console.log(e.data),
            1 == e.code && t.clearAll()
          })
        })
      } else this.$ajaxRequest({
        url: "/user/loginOut",
        method: "POST",
        data: {
          userCode: this.userInfo.userCode
        }
      }).then(function(e) {
        // console.log(e.data),
        1 == e.code && t.clearAll()
      })
    },
    clearAll() {
      sessionStore.delTicket(),
      this.destoryRWS(),
      this.$router.push({
        path: "/LoginOut"
      }),
      this.$destroy(),
      this.$store.commit("clearVmInterval"),
      this.$store.commit("clearUgChatList"),
      this.$store.commit("socket/clearViewInterval")
    },
    // 用户中心打开
    opened() {
      // this.$refs['user-center-drawer'].$el.style.zIndex = 225
    },
    writeByKeydown() {
      document.onkeydown = function(e) {
        "Enter" == e.key && e.ctrlKey && 0 < (e = view.$refs.wirteInfo.innerHTML).length && (view.timeFormat.getMillSeconds() - view.nowMillSeconds > view.byMillSeconds ? (view.writeOver = !0, view.writeSubmit(view.selectUser, e, 0)) : view.$slideMsg.warn("操作太快，信息发送失败"))
      }
    },
    // 提交聊天内容时的基本参数初始化
    initWriteParams(e) {
      var t = view.getUserInfo;
      let i,
      s,
      n,
      o;
      return o = e.userType != t.userType ? 0 === t.userType ? (i = t.companyCode, s = t.userCode, n = e.userCode, "0") : (n = (s = 2 === e.userType || 3 === e.userType ? (i = e.companyCode, e.companyCode) : (i = e.companyCode, e.userCode), t.userCode), "1") : (i = SYSTEM_CODE, s = t.userCode, n = e.userCode, "0"),
      {
        companyCode: i,
        server: s,
        client: n,
        type: o
      }
    },
    writeSubmit(i, s, n) {
      if (this.timeFormat.getMillSeconds() - this.nowMillSeconds < this.byMillSeconds) this.$slideMsg.warn("操作太快，信息发送失败");
      else if (0 < s.length && null != i && i.userCode) {
        let t = i,
        e = view.initWriteParams(t);
        e.content = s,
        e.contactCode = t.userCode,
        e.newContactFlag = !view.firstContactList.includes(t.userCode) && 2 != i.userType,
        chatWrite(e).then(function(e) {
          if (1 == e.code) {
            if (view.$slideMsg.info(e.msg), 0 == view.firstContactList.includes(t.userCode) && (view.firstContactList.push(t.userCode), view.$store.commit("updateNewContactList", t.userCode)), 0 == n) view.$refs.wirteInfo.innerHTML = "",
            view.$refs.writeLength.innerText = "0/" + view.maxLength;
            else if (1 == n) {
              view.writeMsgContent = "";
              let e = document.getElementsByClassName("w-e-text")[0];
              e.innerHTML = '<p data-we-empty-p=""><br></p>'
            }
            view.wirteInfo = ""
          } else view.$slideMsg.error("消息发送失败");
          view.nowMillSeconds = view.timeFormat.getMillSeconds()
        })
      } else null == i && view.$slideMsg.warn("你想发送给谁？先选个好友吧")
    },
    newContactUserNotice (listNewUser) {
      // this.newContactUserNotice(backData.listNewUser)
      // 新用户联系通知处理
      if (listNewUser.length > 0) {
        console.log('新联系用户--', JSON.stringify(listNewUser))
      }
    },
    // 从用户中心--关注用户操作传来(作用是--更新右边关注用户列表)
    afterFUClilk (focusUserInfo) {
      this.newFocusUser = focusUserInfo
    }, 
    // 右边抽屉点击用户
    contactFromRD(e) {
      e.draggable ? null == this.ballExist(e) && (console.log("新增泡泡球--", JSON.stringify(e)), this.dragBall = !0, this.ballGo([e])) : this.changeUser(e)
    },
    // 不在线用户状态更新
    checkNotOnlineUser(e) {
      e && e.onList && (this.onlineUserList = e.onList),
      this.$emit("notOnlineUserCheck2Home", e)
    },
    initUserInfo(i) {
      var e = i.getUserInfo;
      console.log("index获取用户信息--", JSON.stringify(e)),
      null != e && (i.userInfo = e, clearInterval(i.userInterval), this.getNotReadUserList(), i.rightDrawer = !0, setTimeout(function(e) {
        let t = i.$refs.rightListDrawer;
        t.style.right = "-280px",
        // 右边抽屉自动展开，如果鼠标没有锁定则5秒后关闭
        i.rightDrawer = !1,
        i.initManagerInfo()
      }, 5000))
    },
    waittingDo() {
      0 < this.ucgApplyInfoList.length && (this.waitDoVisible = !0)
    },
    initManagerInfo() {
      let t = this;
      this.$ajaxRequest({
        url: "/chat/getUserCGApplyList",
        method: "POST",
        data: {
          userCode: this.userInfo.userCode
        }
      }).then(e => {
        1 == e.code && e.data && 0 < e.data.length && (t.ucgApplyInfoList = e.data, t.$slideMsg.warn("有您待处理的新事项"))
      })
    },
    rdrawerOpen() {
      this.$refs.rightListDrawer.$el.style.zIndex = 50
    },
    // 判断当前对话用户是否在列表中，在则从列表中删除
    checkActiveUser(t) {
      if (null == t || 0 == t.length) return null;
      let i = this,
      s = null,
      n = null;
      // 如果当前对话用户在列表中，则从列表中删除
      return t.forEach((e, t) => {
        i.isActiveUser(e) && (s = t)
      }),
      null != s && t.splice(s, 1),
      // 判断是否在弹出泡泡中
      this.bbUserList.forEach((i, e) => {
        t.forEach((e, t) => {
          e.userCode == i.userCode && (n = t)
        })
      }),
      null != n && t.splice(n, 1),
      0 < t.length ? t: null
    },
    // 判断当前对话用户
    isActiveUser(e) {
      return null != e && null != this.selectUser && null != e.userCode && "" != e.userCode && e.userCode === this.selectUser.userCode
    },
    // 组装群消息参数
    formatCGMsg(e) {
      if (null == e || 0 == e.length) return null;
      let t = [];
      return e.forEach(e => {
        t.push({
          companyCode: e.cc,
          content: e.co,
          initTime: e.it,
          onlineStatus: e.os,
          profilePicture: e.pp || "",
          userCode: e.cc,
          userSign: e.gs || "",
          userType: e.ut,
          username: e.gn,
          server: e.cc,
          client: e.cl
        })
      }),
      t
    }
  }
};