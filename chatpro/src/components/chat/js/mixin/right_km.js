import jquery from "@/components/chat/jquery.min.js";

/* 右键菜单点击 */
export default {
    name: "rkm",
    data() {
      return {
        rkmVisible: false,
        rkmCode: null
      }
    },
    methods: {
      /* 离开 */
      rightKeyOut(e) {
        var t = document.getElementById("container").offsetWidth;
        if (e.pageX < t - 280) {
          let rmkElement = $("#right-key-menu");
          rmkElement && rmkElement.css({
            display: "none"
          })
        }
      },
      /* 右键选中 */
      rmkClick(type) {
        let rmkElement = $("#right-key-menu"),
        e = this,
        i = (rmkElement && rmkElement.css({
          display: "none"
        }), {
          userCode: this.userInfo.userCode,
          blackCode: this.rkmCode
        });

        /* 如果是用户或客服 */
        1 == type || 2 == type 
        ? (i.blackType = 1 == type ? 1 : 0, this.$ajaxRequest({
          url: "/config/patchUserBlackList",
          method: "POST",
          data: i
        }).then(function(s) {
          1 == s.code && (e.$slideMsg.info((1 == type ? "加入黑名单,": "解除黑名单,") + s.msg), e.$store.commit("updateBlackUserList", i))
        })) 
        /* 如果是群或房间 */
        : 3 != type && 4 != type || (i.chatGroupCode = this.rkmCode, i.msgStatus = 3 == type ? 0 : 1, this.$ajaxRequest({
          url: "/chat/patchCGMsgStatus",
          method: "POST",
          data: i
        }).then(function(s) {
          1 == s.code && (e.$slideMsg.info((3 == type ? "禁止群消息,": "接收群消息,") + s.msg), e.chatGroupList.forEach(s => {
            s.chatGroupCode == e.rkmCode && (s.msgStatus = 3 == type ? 0 : 1)
          }))
        }))
      },
      /* 右键点击 */
      rightKeyMenu(e, type, user_code) {
        let rmkElement = $("#right-key-menu");
        e.pageX;

        var c = e.offsetX,
        o = e.pageY;

        // 如果不是鼠标右键则返回
        if (2 != e.button) return !1;

        // 如果是客服，右键菜单隐藏
        if (rmkElement) if (0 == type) rmkElement.css({
          display: "none"
        });
        else {
          this.rkmCode = user_code,
          rmkElement.css({
            display: "block",
            top: o,
            left: 80 < c ? 160 : 60
          });

          let s = rmkElement.find(".el-menu-item");

          // 如果是用户，显示用户的禁用信息
          if (1 == type) {
            let t = this.checkInBlack(user_code);
            s.each(function(s) {
              1 < s ? $(this).css({
                display: "none"
              }) : t ? 0 == s ? $(this).css({
                display: "none"
              }) : $(this).css({
                display: "block",
                color: "black"
              }) : 1 == s ? $(this).css({
                display: "none"
              }) : $(this).css({
                display: "block",
                color: "black"
              })
            })
          } 
          // 如果是群或房间，显示群的禁用信息
          else if (2 == type) {
            let t = this.checkUCGMsgStatus(user_code);
            s.each(function(s) {
              1 < s ? t ? 3 == s ? $(this).css({
                display: "none"
              }) : $(this).css({
                display: "block",
                color: "black"
              }) : 2 == s ? $(this).css({
                display: "none"
              }) : $(this).css({
                display: "block",
                color: "black"
              }) : $(this).css({
                display: "none"
              })
            })
          }
        }
      },
      checkInBlack(s) {
        let t = this.getUserInfo.blackUserList;
        return ! (!t || !t.includes(s))
      },
      checkUCGMsgStatus(e) {
        let s = this.chatGroupList;
        if (s) {
          let t = false;
          return s.forEach(s => {
            s.chatGroupCode == e && 1 == s.msgStatus && (t = !0)
          }),
          t
        }
        return false
      }
    }
  };