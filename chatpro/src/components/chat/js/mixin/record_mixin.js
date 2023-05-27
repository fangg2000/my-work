import {DateTime} from '@/util/timeUtil.js'
import wangEditor from '@/components/chat/wEditer'
import dr_view from '@/components/chat/daily_record'

const dr_map = {
  "2022/01": [{
    recordId: 123456784,
    userCode: "u123454",
    title: "test4",
    createTime: 1645077939e3
  },
  {
    recordId: 123456785,
    userCode: "u123455",
    title: "test5",
    createTime: 1644077939e3
  },
  {
    recordId: 123456786,
    userCode: "u123456",
    title: "test6",
    createTime: 1643077939e3
  },
  {
    recordId: 123456787,
    userCode: "u123457",
    title: "test7",
    createTime: 1642077939e3
  },
  {
    recordId: 123456788,
    userCode: "u123458",
    title: "test8",
    createTime: 1641077939e3
  }],
  "2021/02": [{
    recordId: 123456782,
    userCode: "u123452",
    title: "test2",
    createTime: 1629863995e3
  },
  {
    recordId: 123456783,
    userCode: "u123453",
    title: "test3",
    createTime: 1629463995e3
  }],
  "2018/03": [{
    recordId: 123456781,
    userCode: "u123451",
    title: "test1",
    createTime: 1525756282e3
  }]
},
RECORD_MIXIN = {
  components: {
    wangEditor: wangEditor,
    dr_view: dr_view
  },
  data() {
    return {
      timeFormat: new DateTime("yyyy/MM/dd hh:mm:ss"),
      recordData: {},
      editor: null,
      drUserCode: null,
      replyInfo: null,
      replyContent: "",
      recordFormVisible: false,
      recordFormClear: false,
      newRGVisible: false,
      collectFlag: false,
      recordGroup: [],
      drDataForMonth: {},
      drMonthData: {},
      drData: {},
      // 0草稿,  1公开，2好友可见，3保密
      recordType: [{
        recordStatus: 0,
        title: "草稿"
      },
      {
        recordStatus: 1,
        title: "公开"
      },
      {
        recordStatus: 2,
        title: "好友可见"
      },
      {
        recordStatus: 3,
        title: "保密"
      }],
      recordForm: {
        userCode: null,
        title: "日记标题",
        content: "添加内容",
        recordGroupId: 0,
        recordStatus: 0
      },
      replyData: {
        replyUsername: "xxx",
        recordId: "",
        parentId: 0
      }
    }
  },
  props: [],
  created() {},
  mounted() {
    // 初始化时查询登录人日记
    // this.drDataForMonth = dr_map
    this.getDRListByMonth(this.userInfo.userCode)
  },
  computed: {},
  watch: {},
  methods: {
    // 显示收藏日记列表
    showCollect() {
      let r = this,
      t = this.userInfo.userCode;
      // 清空用户列表选中状态
      // 关注用户列表
      this.focusUserList.forEach((e, t) => {
        r.$refs["focus_user_" + t][0].style.borderRight = "10px solid transparent",
        r.$refs["focus_user_" + t][0].style.backgroundColor = "#fff"
      }),
      // 查询用户列表
      this.userList.forEach((e, t) => {
        r.$refs["user_" + t][0].style.borderRight = "10px solid transparent",
        r.$refs["user_" + t][0].style.backgroundColor = "#fff"
      }),
      this.$ajaxRequest({
        url: "/daily/getUserCollectList",
        method: "POST",
        data: {
          userCode: t
        }
      }).then(function(e) {
        1 == e.code ? (r.drDataForMonth = e.data, r.collectFlag = !0, r.drUserCode = t) : r.$slideMsg.error(e.msg)
      })
    },
    submitReply() {
      let t = this;
      this.replyContent.trim().length < 1 || (this.replyData.userCode = this.userInfo.userCode, this.replyData.username = this.userInfo.username, this.$ajaxRequest({
        url: "/daily/putDiscussInfo",
        method: "POST",
        data: this.replyData
      }).then(function(e) {
        1 == e.code ? (t.replyInfo = e.data, t.replyData = {},
        t.replyContent = "", t.$slideMsg.info(e.msg)) : t.$slideMsg.error(e.msg)
      }))
    },
    drChange(r) {
      if (r && 0 < r.length) {
        let t = this;
        null != this.userInfo && this.selectUser.userCode == this.userInfo.userCode || this.$ajaxRequest({
          url: "/daily/patchDRReviewNum",
          method: "POST",
          data: {
            recordId: r[0],
            userCode: this.selectUser.userCode
          }
        }).then(function(e) {
          if (1 == e.code && 1 == e.data) {
            let e = {};
            e.recordId = r[0],
            e.reviewNum = 1,
            t.drData = e
          }
        })
      }
    },
    getDRListByMonth(t) {
      let r = this;
      if (this.drUserCode != t) return Object.hasOwnProperty.call(this.drMonthData, t) ? (r.drDataForMonth = this.drMonthData[t], void(r.drUserCode = t)) : void this.$ajaxRequest({
        url: "/daily/getDRListForMonth",
        method: "POST",
        data: {
          userCode: t
        }
      }).then(function(e) {
        1 == e.code && (r.drDataForMonth = e.data, r.drUserCode = t, r.drMonthData[t] = e.data)
      })
    },
    newRG() {
      let t = this;
      var e = this.$refs.newReGroup.innerHTML;
      0 < e.length && this.$ajaxRequest({
        url: "/daily/putRecordGroup",
        method: "POST",
        data: {
          userCode: this.userInfo.userCode,
          groupName: e
        }
      }).then(function(e) {
        1 == e.code ? (t.$slideMsg.info(e.msg), t.recordGroup = t.recordGroup.concat(e.data)) : t.$slideMsg.error(e.msg)
      })
    },
    newRGBlur(e) {
      let t = this;
      setTimeout(() => {
        t.newRGVisible = !1,
        t.$refs.newReGroup.innerHTML = "新建分组"
      },
      300)
    },
    newRGFocus() {
      this.newRGVisible = !0
    },
    push2DRMList(t) {
      let r = null,
      o = null;
      // 如果日记列表显示的不是登录人的日记，则返回
      if (this.drUserCode != this.userInfo.userCode) {
        // 日记放入操作人日记列表中
        if (Object.hasOwnProperty.call(this.drMonthData, this.userInfo.userCode)) {
          let e = this.drMonthData[this.userInfo.userCode];
          for (const s in e) Object.hasOwnProperty.call(t, s) && (o = s, r = e[s]);
          null != o && (r.push(t[o]), e[o] = r, this.drMonthData[this.userInfo.userCode] = e)
        }
      } else {
        for (const e in this.drDataForMonth) Object.hasOwnProperty.call(t, e) && (o = e, r = this.drDataForMonth[e]);
        if (null != o) r.push(t[o]),
        this.drDataForMonth[o] = r,
        this.drMonthData[this.userInfo.userCode] = this.drDataForMonth;
        else for (const d in t) Object.hasOwnProperty.call(t, d) && ((r = []).push(val), this.drDataForMonth[d] = r)
      }
    },
    submitDR() {
      let t = this;
      "" != this.recordForm.title && "" != this.recordForm.content && this.$ajaxRequest({
        url: "/daily/putDailyRecord",
        method: "POST",
        data: this.recordForm
      }).then(function(e) {
        1 == e.code ? (t.$slideMsg.info(e.msg), t.recordFormClear = !0, t.recordForm.recordStatus = 0, t.recordForm.title = "", t.recordFormVisible = !1, t.push2DRMList(e.data)) : t.$slideMsg.error(e.msg)
      })
    },
    openDialog() {
      if (this.recordFormClear = !1, 0 == this.recordGroup.length) {
        let t = this;
        this.$ajaxRequest({
          url: "/daily/getRecordGroupList",
          method: "POST",
          data: {
            userCode: this.userInfo.userCode
          }
        }).then(function(e) {
          1 == e.code ? (t.recordForm.userCode = t.userInfo.userCode, 0 < e.data.length ? (t.recordGroup = e.data, t.recordForm.recordGroupId = e.data[0].recordGroupId) : (e = {
            recordGroupId: 0,
            groupName: "默认分组",
            groupCode: "DEFAULT"
          },
          t.recordGroup = [e])) : (e = {
            recordGroupId: 0,
            groupName: "默认分组",
            groupCode: "DEFAULT"
          },
          t.recordGroup = [e])
        })
      }
    },
    // 输入框失去焦点时
    replyWirteHide (e) {
      this.replyData.content = e.currentTarget.value
      setTimeout(() => {
        let rw_el = document.getElementById('reply-content-w')
        rw_el.style.backgroundColor = '#EBEEF5'
        let write_el = document.getElementById('record-footer')
        write_el.setAttribute('class', 'record-footer')
      }, 500);
    }
  }
};

export default RECORD_MIXIN;