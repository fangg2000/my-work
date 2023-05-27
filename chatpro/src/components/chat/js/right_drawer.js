
import {DateTime} from '@/util/timeUtil.js'
import {mapState, mapGetters, mapActions, mapMutations} from 'vuex';

import rkm from "./mixin/right_km";

const dateFormat = new DateTime("yyyy/MM/dd hh:mm:ss");

let userNList = [{
  username: "sayye",
  updateTime: new Date,
  profilePicture: null
},
{
  username: "dom",
  updateTime: 1642293833408,
  profilePicture: null
},
{
  username: "gogoge",
  updateTime: 1642292402976,
  profilePicture: null
}];

export default {
  name: "ChatUserList",
  mixins: [rkm],
  components: {},
  data() {
    return {
      userInfo: null, // 当前登录用户信息
      timeFormat: new DateTime('yyyy/MM/dd hh:mm:ss'),
      nearContactList: [],
      focusUserList: [],
      newConUserList: [],
      chatGroupList: [],
      csList: [],
      activeName: ["1", "2", "3"],
      ppAvtiveType: 0,
      selectUser: null, //当前对话用户
      initFlag: false,
      initInterval: null,
      userInterval: null,
      dragUserCode: null,
      formLabelWidth: "120px",
      cgFormVisible: !1,
      descript: null,
      cgForm: {
        chatGroupName: null,
        applyCondition: 0,
        groupType: 0
      },
      groupType: [{
        name: "服务",
        value: 1
      },
      {
        name: "技术",
        value: 2
      },
      {
        name: "交流",
        value: 3
      },
      {
        name: "生活",
        value: 4
      },
      {
        name: "办公",
        value: 5
      },
      {
        name: "其他",
        value: 0
      }],
      condition: [{
        name: "自由加入",
        value: 0
      },
      {
        name: "管理员审核",
        value: 1
      },
      {
        name: "拒绝加入",
        value: 2
      }],
      rules: {
        chatGroupName: [{
          required: !0,
          message: "请输入群名称",
          trigger: "blur"
        },
        {
          min: 1,
          max: 10,
          message: "长度在 1 到 10 个字符",
          trigger: "blur"
        }]
      },
      cgSearchVisible: !1,
      cgSMoreVisible: !1,
      cgApplyVisible: !1,
      cgGridData: [],
      cgApplyList: [],
      applyGroupCode: null,
      cgSearchForm: {
        chatGroupName: null,
        groupType: null
      },
      csVisible: !1,
      pageNum: 1,
      pageSize: 10,
      total: 0
    }
  },
  filters: {
    showTextByLength (str) {
      return str!=undefined&&str.length>15?(str.substring(0,15)+'...'):str
    }
  },
  props: ["newConUsers2RD", "newFocusUser2RD", "add2RDNearContact", "newBB2RD", "csVisible2RD"],
  created() {},
  mounted() {
    let e = this;
    this.userInterval = setInterval(() => {
      e.initUserInfo()
    }, 1000),
    this.$store.commit("updateVmInterval", this.userInterval)
  },
  computed: {...mapGetters({
      f0002Per: "funcPer/getP4VNC",
      f0003Per: "funcPer/getP4MNC",
      f0004Per: "funcPer/getP4NNC",
      f0002Limit: "funcPer/getP4VNCLimit",
      f0003Limit: "funcPer/getP4MNCLimit",
      f0004Limit: "funcPer/getP4F0004Limit"
    }),
    getUserInfo: function(e) {
      return this.$store.getters.getUserInfo
    },
    getAllUser: function(e) {
      let s = [],
      i = [],
      t = this.nearContactList.concat(this.focusUserList.concat(this.newConUserList));
      return t.forEach((e, t) => {
        0 == i.includes(e.userCode) && (s.push(e), i.push(e.userCode))
      }),
      s
    },
    // 取得不在线和在线的用户列表
    getNotOnlineUsers: function (params) {
      let mapCodes = {}
      let listNOCodes = []
      let listONCodes = []
      let allListUser = this.nearContactList.concat(this.focusUserList.concat(this.newConUserList))
      allListUser.forEach((user, index) => {
        if (user.onlineStatus != undefined && user.onlineStatus != null) {
          if (user.onlineStatus == 0 && listNOCodes.includes(user.userCode) == false) {
            listNOCodes.push(user.userCode)
          } else if (user.onlineStatus == 1 && listONCodes.includes(user.userCode) == false) {
            listONCodes.push(user.userCode)
          }
        }
      })

      mapCodes.noList = listNOCodes
      mapCodes.onList = listONCodes
      return mapCodes
    },
    // 取得功能权限
    getFuncPermission () {
      return this.$store.getters.getFuncPermission
    },
    // 通过权限返回最近联系人列表
    getNCListByPermission () {
      // let ncList = JSON.parse(JSON.stringify(this.nearContactList))
      let ncList = this.nearContactList
      // console.log('ncList--', JSON.stringify(this.nearContactList));
      // console.log('最低权限--', this.f004Per)
      // console.log('最低权限--', this.f004Limit)
      // console.log('最低权限--', JSON.stringify(this.$store.getters['funcPer/getP4F0004Limit']))

      if (this.f0003Per == false) {
        if (this.f0002Per == false) {
          if (this.f0004Per && this.nearContactList.length > this.f0004Limit) {
            ncList = this.nearContactList.splice(0, this.f0004Limit)
          } else if (this.f0004Per == false) {
            ncList = []
          }
        } else if (this.nearContactList.length > this.f0002Limit) {
          ncList = this.nearContactList.splice(0, this.f0002Limit)
        }
      } else {
        // 如果mvp权限不是无限量的话
        if (this.f0003Limit != null && this.f0003Limit > -1 && this.nearContactList.length > this.f0003Limit) {
          ncList = this.nearContactList.splice(0, this.f0003Limit)
        }
      } 

      // 设置客服图片
      if (ncList && ncList.length > 0) {
        ncList.forEach(item => {
          if (item.userType == 0) {
            item.profilePicture = '/uploaded/cs.jpg'
          }
        });
      }

      return ncList
    },
    getPPClass() {
      return 0 == this.ppAvtiveType ? "contact-pp btn--wiggle is-active": "contact-pp btn--jump is-active"
    },
    getPPNGClass() {
      return 0 == this.ppAvtiveType ? "contact-pp imgGray btn--wiggle is-active": "contact-pp imgGray btn--jump is-active"
    },
    getPPONClass() {
      return 0 == this.ppAvtiveType ? "contact-pp btn--wiggle": "contact-pp btn--jump"
    },
    getPPGClass() {
      return 0 == this.ppAvtiveType ? "contact-pp imgGray btn--wiggle": "contact-pp imgGray btn--jump"
    },
    getPath() {
      return process.env.BASE_URL
    }
  },
  watch: {
    // 数据是从Home的socket中返回的，包括新用户列表、在线用户列表
    newConUsers2RD: function (newV, oldV) {
      if (newV != null) {
        // console.log('RD新用户列表--', JSON.stringify(newV))

        // 窗口初始化是否完成
        if (this.initFlag == false) {
          console.log('RD数据初始化没有完成');
          let view = this
          this.initInterval = setInterval(() => {
            view.checkInitRD(newV)
          }, 1000);
        } else {
          this.checkInitRD(newV)
        }
      }
    },
    newFocusUser2RD: function (newV, oldV) {
      if (newV != null && newV.userCode != undefined) {
        // console.log('新关注用户--', JSON.stringify(newV))
        this.afterFocusUser(newV)
      }
    },
    nearContactList: function (newV, oldV) {
      // console.log('新用户列表--', JSON.stringify(newV))
      if (newV != null && newV.length > 0) {
        // 当列表有变化时
        this.$emit('notOnlineUserCheck2Idx', this.getNotOnlineUsers)
      }
    },
    focusUserList: function(e, t) {
      null != e && 0 < e.length && this.$emit("notOnlineUserCheck2Idx", this.getNotOnlineUsers)
    },
    // 主页面选中的对话用户信息
    add2RDNearContact: function(e, t) {
      null != e && null != e.userCode && null != e.userCode ? (0 == e.focusFlag && this.add2RDNearContactList(e), this.selectUser = e) : null == e && (this.selectUser = null)
    },
    newBB2RD: function(s, e) {
      null != s && (this.nearContactList.forEach((e, t) => {
        e.userCode == s.userCode && (e.newFlag = 0)
      }), this.focusUserList.forEach((e, t) => {
        e.userCode == s.userCode && (e.newFlag = 0)
      }))
    },
    csVisible2RD: function(e, t) { ! 0 === e && (this.csVisible = e, 0 == this.activeName.includes("5") && this.activeName.push("5"), this.initCSList())
    }
  },
  methods: {
    initCSList() {
      let t = this;
      this.$ajaxRequest({
        url: "/cs/getCustomerServiceList",
        method: "POST",
        data: {
          userCode: ""
        }
      }).then(e => {
        1 == e.code && (t.csList = e.data)
      })
    },
    submitCGForm() {
      let t = this;
      this.$refs.cgForm.validate(e => {
        if (!e) return ! 1;
        t.cgForm.userCode = t.userInfo.userCode,
        t.$ajaxRequest({
          url: "/chat/postChatGroupInfo",
          method: "POST",
          data: t.cgForm
        }).then(e => {
          1 == e.code && (t.$slideMsg.info(e.msg), t.cgForm.chatGroupCode = e.data, t.chatGroupList.push(t.cgForm), t.cgFormVisible = !1)
        })
      })
    },
    newChatGroup() {
      this.cgFormVisible = !0,
      this.cgForm.applyCondition = 0
    },
    submitCGApply() {
      let t = this;
      this.cgApplyVisible = !1,
      this.descript.trim().length < 1 ? this.$slideMsg.warn("内容不能为空") : this.$ajaxRequest({
        url: "/chat/postChatGroupApply",
        method: "POST",
        data: {
          userCode: this.userInfo.userCode,
          chatGroupCode: this.applyGroupCode,
          applyDescript: this.descript
        }
      }).then(e => {
        1 == e.code && (t.$slideMsg.info("操作成功，等待管理员审核……"), t.descript = "", t.cgApplyList.push(t.applyGroupCode), t.applyGroupCode = "")
      })
    },
    groupApply(e) {
      let t = this;
      this.checkCGExist(e) || (0 == e.applyCondition ? this.$ajaxRequest({
        url: "/chat/postChatGroupApply",
        method: "POST",
        data: {
          userCode: this.userInfo.userCode,
          chatGroupCode: e.chatGroupCode
        }
      }).then(e => {
        1 == e.code && (t.$slideMsg.info(e.msg), t.chatGroupList.push(e.data))
      }) : 1 == e.applyCondition ? (this.cgApplyVisible = !0, this.applyGroupCode = e.chatGroupCode) : t.$slideMsg.info("该群拒绝加入新成员", "warn"))
    },
    checkCGExist(t) {
      let s = !1;
      return this.chatGroupList.forEach(e => {
        e.chatGroupCode == t.chatGroupCode && (s = !0)
      }),
      0 == s && this.cgApplyList.forEach(e => {
        e == t.chatGroupCode && (s = !0)
      }),
      s
    },
    cgSearchMore(e) {
      let t = this,
      s = this.cgGridData.length;
      0 < s && (this.cgSearchForm.chatGroupId = this.cgGridData[s - 1].chatGroupId),
      this.$ajaxRequest({
        url: "/chat/getChatGroupList",
        method: "POST",
        data: this.cgSearchForm
      }).then(e => {
        1 == e.code && (0 < (s = e.data.length) ? (t.cgGridData = t.cgGridData.concat(e.data), s < t.pageSize && (t.cgSMoreVisible = !1)) : t.cgSMoreVisible = !1)
      })
    },
    searchChatGroup() {
      this.cgSearchVisible = !0
    },
    searchSubmit() {
      let s = this;
      this.cgSearchForm.pageSize = this.pageSize,
      this.cgSearchForm.chatGroupId = null,
      this.$ajaxRequest({
        url: "/chat/getChatGroupList",
        method: "POST",
        data: this.cgSearchForm
      }).then(e => {
        var t;
        1 == e.code && (0 < (t = e.data.length) ? (s.cgGridData = e.data, t < s.pageSize ? s.cgSMoreVisible = !1 : s.cgSMoreVisible = !0) : (s.cgSMoreVisible = !1, s.cgGridData = []))
      })
    },
    rowClassNameFn({
      row: e,
      rowIndex: t
    }) {
      e.index = t + 1
    },
    groupTypeFilter(t) {
      let s = "";
      return this.groupType.forEach(e => {
        e.value == t && (s = e.name)
      }),
      s
    },
    initUserInfo () {
      let user = this.getUserInfo
      if (user != null) {
        this.userInfo = user

        // 取当前操作用户的头像动态类型
        this.ppAvtiveType = this.userInfo.ppAvtiveType
        
        // 初始化用户列表
        this.initUserList()
        
        clearInterval(this.userInterval)
      }
    },
    // 初始化用户列表
    initUserList() {
      var e = {
        userId: this.userInfo.userId,
        userCode: this.userInfo.userCode,
        pageNum: this.pageNum,
        pageSize: this.pageSize
      };
      this.$ajaxRequest({
        url: "/user/getNearContactUserList",
        method: "POST",
        data: {
          userCode: this.userInfo.userCode,
          contactCode: ""
        }
      }).then(e => {
        1 == e.code && (e.data.forEach((e, t) => {
          e.newFlag = 0
        }), this.nearContactList = this.nearContactList.concat(e.data))
      }),
      null != this.userInfo && 1 == this.userInfo.userType ? (this.$ajaxRequest({
        url: "/user/getFocusUserList",
        method: "POST",
        data: e
      }).then(e => {
        1 == e.code && (this.total = e.data.total, this.afterLoadFocusUser(e.data.rows)),
        this.initFlag = !0
      }), this.$ajaxRequest({
        url: "/chat/getUCGList",
        method: "POST",
        data: {
          userCode: this.userInfo.userCode
        }
      }).then(e => {
        1 == e.code && (this.chatGroupList = e.data)
      })) : null != this.userInfo && 0 == this.userInfo.userType && (this.initFlag = !0)
    },
    // 拖动时触发
    userDragStart(e, t) {},
    // 在拖动中
    userDrag(e, t) {},
    // 拖动结束
    userDragEnd(e, i, r) {
      let n = this;
      // 如果用户已经在对话状态，则不再新增泡泡球
      if (null != this.selectUser && this.selectUser.userCode == i) this.$slideMsg.warn("用户【" + this.selectUser.username + "】目前已经在对话状态中……");
      else {
        let t = e.clientX + 50,
        s = e.clientY + 50;
        if (2 == r || 3 == r) this.chatGroupList.forEach(e => {
          e.chatGroupCode == i && (e.userCode = e.chatGroupCode, e.username = e.chatGroupName, e.companyCode = e.chatGroupCode, e.x = t, e.y = s, e.draggable = !0, e.initTime = (new Date).getTime(), e.userType = 2, n.$emit("contact2User", e))
        });
        // 如果是客服
        else if (0 == r && 0 < this.csList.length) this.csList.forEach(e => {
          e.userCode == i && (
            e.x = t, 
            e.y = s, 
            e.draggable = !0, 
            e.initTime = (new Date).getTime(), 
            // 使用客服默认图片(不能在此设置)
            //e.profilePicture = '',
            n.$emit("contact2User", e)
          )
        });
        else {
          let e = this.getAllUser;
          e.forEach(e => {
            e.userCode == i && (e.x = t, e.y = s, e.draggable = !0, e.initTime = (new Date).getTime(), n.$emit("contact2User", e))
          })
        }
      }
    },
    afterLoadFocusUser(e) {
      if (e.forEach((e, t) => {
        e.newFlag = 0
      }), 0 < this.focusUserList.length) {
        let t = this,
        i = !1;
        e.forEach((s, e) => {
          i = !1,
          t.focusUserList.forEach((e, t) => {
            e.userCode == s.userCode && (i = !0)
          }),
          0 == i && t.focusUserList.push(s)
        })
      } else this.focusUserList = this.focusUserList.concat(e)
    },
    afterFocusUser(e) {
      let s = !1,
      i = e.userCode,
      r = 0;
      this.focusUserList.forEach((e, t) => {
        i == e.userCode && (s = !0, r = t)
      }),
      e.focusFlag ? 0 == s && this.focusUserList.push(e) : s && this.focusUserList.splice(r, 1)
    },
    mouseoutHandle (event) {
      var x1 = event.clientX;
      var y1 = event.clientY;
      let screenWidth = document.body.clientWidth
      // console.log('鼠标移动--', (screenWidth - x1))
      // 鼠标移开关闭右边抽屉
      if ((screenWidth - x1) > 260) {
        this.$emit('update:drawer', false)
      }
      
      this.$emit('update:drawerMove', true)
    }, 
    // 点击用户
    contactUser(n, o, s) {
      let e = this;
      if (4 == s) n.userCode = n.chatGroupCode,
      n.companyCode = n.chatGroupCode,
      n.userType = 2;
      else {
        let e = 1 == s ? this.nearContactList: 2 == s ? this.focusUserList: this.newConUserList,
        i = 1 == s ? "near_user_": 2 == s ? "focus_user_": "new_con_",
        // 最近联系人和关注用户同时存在相同用户时的判断
        t = 1 == s ? this.focusUserList: 2 == s ? this.nearContactList: [],
        r = 1 == s ? "focus_user_": 2 == s ? "near_user_": "";
        if (1 == n.strangerContact && 0 == n.byFocusFlag) return void this.$slideMsg.warn("对方不想和陌生人联系，换个好友吧");
        e.forEach((t, s) => {
          if (s == o) {
            let e = this.$refs[i + s][0].$el.classList;
            e.contains("is-active") && (e.remove("is-active"), t.userSign = t.signCache, t.newFlag = 0)
          }
        }),
        t.forEach((t, s) => {
          if (t.userCode == n.userCode) {
            let e = this.$refs[r + s][0].$el.classList;
            e.contains("is-active") && (e.remove("is-active"), t.newFlag = 0, "focus_user_" == r && (t.userSign = t.signCache))
          }
        })
      }
      delete n.draggable,
      this.$emit("contact2User", n),
      // 选中用户关闭右边抽屉
      setTimeout(() => {
        e.$emit("update:drawer", !1)
      },
      800)
    },
    updateOnlineStatus(s) {
      let i = [];
      // 更新最近联系人在线状态
      this.nearContactList.forEach((e, t) => {
        s.includes(e.userCode) && (e.onlineStatus = 1, i.push(e.userCode))
      }),
      1 == this.getUserInfo.userType && this.focusUserList.forEach((e, t) => {
        // 更新关注用户在线状态
        s.includes(e.userCode) && (e.onlineStatus = 1, i.push(e.userCode))
      }),
      // 更新在线用户消息推送
      this.updateNewConUserList(i)
    },
    updateNotOnlineStatus(s) {
      let i = [];
      
      // 更新最近联系人在线状态
      this.nearContactList.forEach((e, t) => {
        s.includes(e.userCode) && (e.onlineStatus = 0, i.push(e.userCode))
      }),
      // 更新陌生联系人在线状态
      this.newConUserList.forEach((e, t) => {
        s.includes(e.userCode) && (e.onlineStatus = 0, i.push(e.userCode))
      }),
      1 == this.getUserInfo.userType && this.focusUserList.forEach((e, t) => {
        // 更新关注用户在线状态
        s.includes(e.userCode) && (e.onlineStatus = 0, i.push(e.userCode))
      }),
      0 < i.length && this.$emit("notOnlineUserCheck2Idx", this.getNotOnlineUsers)
    },
    updateNewConUserList (listCodes) {
      let view = this

      // 当前操作用户联系用户code列表
      let firstContactList = this.$store.getters.getNewContactList
      listCodes.forEach(code => {
        if (firstContactList.includes(code)) {
          view.$store.commit('removeNewContact', code)
        }
      })
    }, 
    add2RDNearContactList (userIn) {
      let haveFlag = false
      let user_code = userIn.userCode
      let index = 0
      this.nearContactList.forEach((user, i) => {
        if (user_code == user.userCode) {
          haveFlag = true
          index = i
        }
      })

      // 如果不存在则添加到列表前面
      if (haveFlag == false) {
        this.nearContactList.unshift(userIn)
      }
    },
    showNewConUser(e) {
      console.log("新消息--", JSON.stringify(e));
      let s = this,
      i = null;
      if (null == e) return ! 1;
      if (e.forEach((e, t) => {
        s.isActiveUser(e) && (i = t)
      }), null != i && e.splice(i, 1), null != e && 0 == e.length) return ! 1;
      // 取不在列表中的用户
      let t = this.backFromList(e);
      // 取存在列表中的用户
      e = 0 == t.length ? e: this.backFromList(e, !0);
      return 0 < t.length && (this.$slideMsg.info("有您的新信息，请注意查看"), this.newConUserList.forEach((e, t) => {
        let s = this.$refs["new_con_" + t][0].$el.classList;
        0 == s.contains("is-active") && (e.newFlag = 0)
      }), t.forEach((e, t) => {
        e.newFlag = 1
      }), this.newConUserList = this.newConUserList.concat(t)),
      // 处理存在最近联系人和关注用户列表中的用户
      0 < e.length && (this.$slideMsg.info("有您的新信息，请注意查看"), this.nearContactList = this.showNewConUserWithList(this.nearContactList, e, "near_user_"), this.focusUserList = this.showNewConUserWithList(this.focusUserList, e, "focus_user_")),
      !0
    },
    showNewConUserWithList(e, s, i) {
      let r = this,
      n = s.map(e => e.userCode);
      return e.forEach((e, t) => {
        let s = r.$refs["" + i + t][0].$el.classList;
        0 == s.contains("is-active") && (e.newFlag = 0)
      }),
      e.forEach((e, t) => {
        n.includes(e.userCode) && (e.newFlag = 1, e.signCache = e.userSign, e.userSign = s[n.indexOf(e.userCode)].content)
      }),
      e
    },
    // 返回用户列表(existFlag为true时返回在列表的用户列表，其他返回不存在列表中的用户列表)
    backFromList: function(e, s) {
      let i = [],
      t = this.nearContactList.concat(this.focusUserList.concat(this.newConUserList)),
      r = t.map(e => e.userCode);
      return e.forEach((e, t) => {
        s ? r.includes(e.userCode) && i.push(e) : 0 == r.includes(e.userCode) && i.push(e)
      }),
      i
    },
    // 判断用户是否存在列表中
    checkUserInList: function (userCode) {
      let existFlag = false
      let allListUser = this.nearContactList.concat(this.focusUserList.concat(this.newConUserList))
      allListUser.forEach((user, index) => {
        if (user.userCode == userCode) {
          existFlag = true
        }
      })
      return existFlag
    },
    // 判断当前对话用户
    isActiveUser(userIn) {
      if (userIn != null 
        && this.selectUser != null 
        && userIn.userCode != null 
        && userIn.userCode != '' 
        && userIn.userCode === this.selectUser.userCode) {
          return true
      }
      return false
    },
    checkInitRD (newV) {
      if (this.initFlag == false) {
        return false
      }
      if (this.initInterval != null) {
        clearInterval(this.initInterval)
      }

      let listUC = newV.listUC
      let listOC = newV.listOC
      let listNC = newV.listNC

      if (listUC != undefined && listUC.length > 0) {
        this.showNewConUser(listUC)
      } 
      if (listOC != undefined && listOC.length > 0) {
        console.log('好友上线--', JSON.stringify(listOC))
        this.$slideMsg.info('您有好友上线了')
        this.updateOnlineStatus(listOC)
        this.$emit('notOnlineUserCheck2Idx', this.getNotOnlineUsers)
      }
      if (listNC != undefined && listNC.length > 0) {
        this.updateNotOnlineStatus(listNC)
      }
      return true
    }
  }
};