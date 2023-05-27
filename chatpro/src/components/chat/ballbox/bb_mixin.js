import Vue from 'vue'
import { BallBox, Ball } from '@/base/bubble/collision.min.js'
import ReadWebSocket from '@/socket/readSocket'
import BbMessageView from './bb_message'
import "./color_text.css";

// 混入--泡泡的消息提示
// mixin只有方法里面能使用组件的this(即调用时起作用，因为方法已经合并到组件中)
const bbMixin = {
  data () {
    return {
      bbUserList: [],
      bbColor: null,
      msg_num: 0,
      dragBall: false,
      bbVisible: false,
      chatMap: {},
      moreInterval: null,
      cguData: {},    // 群成员信息
      writeMsgId: '',
      newBBUser: null,
      onlineUserList: [],   // 在线用户列表
    }
  }, 
  created () {
    this.chatMap = {}
    
    // 清空当前组件下的所有缓存线程
    let index_interval = {
      key: 'index'
    }
    this.$store.commit('socket/clearViewInterval', index_interval)
  },
  mounted () {
    // 拖动消息输入框
    this.zoomWriteMsg()
  },
  computed: {
    
  },
  watch: {
    bbUserList: function (newV, oldV) {
      console.log('泡泡用户列表--', newV);
    },
    /* cguData: function (newV, oldV) {
      console.log('从read.vue中转过来数据--', newV);
      
    },
    'queryData.name': {
      handler: function() {
        //do something
      },
    } */
    writeMsgContent: function (newV, oldV) {
      let inputPElement = document.getElementsByClassName('w-e-text')[0]
      if (inputPElement.innerText.length <= this.maxLength) {
        this.$refs.writeMsgLength.innerText = inputPElement.innerText.length +'/'+this.maxLength
      } else {
        // this.$refs.writeMsgLength.innerText = this.maxLength +'/'+this.maxLength
        this.$refs.writeMsgLength.innerText = '字数超出'
      }
    }
  }, 
  methods: {
    // 消息输入框可拖动设置
    zoomWriteMsg () {
      let pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0, pos5 = 0, pos6 = 0
      let mainElement = document.getElementById('bbPopoer')
      let writeElement = document.getElementsByClassName('input-w-message')[0]
      let min_width = parseInt(writeElement.style.minWidth.replace('px', ''), 10)
      let min_height = parseInt(writeElement.style.minHeight.replace('px', ''), 10) 
      let inputPElement = writeElement.childNodes[1]
      inputPElement.style.minWidth = writeElement.style.minWidth
      inputPElement.style.minHeight = (min_height - 50) + 'px'
      // 输入框
      let inputElement = inputPElement.childNodes[0]
      inputElement.onkeydown = this.msgKeydown
      // inputElement.onkeyup = wmKeyup
      // 内置内容
      let placeholderElement = inputPElement.childNodes[1]
      placeholderElement.innerHTML = '<p>请输入内容(Shift+Enter发送)</p>'

      
      // let inputElement = document.getElementById(writeElement.getAttribute('write-msg-id')).parentNode
      let zoomElement = document.getElementById('zoomWriteMsg')
      zoomElement.onmousedown = mouseDown;
      zoomElement.onmouseup = mouseUp;
      // console.log('输入框大小--', writeElement);

      function mouseDown(e) {
        e = e || window.event;
        // get the mouse cursor position at startup:
        pos1 = parseInt(writeElement.style.width.replace('px', ''), 10)
        pos2 = parseInt(writeElement.style.height.replace('px', ''), 10)
        pos3 = e.clientX;
        pos4 = e.clientY;
        pos5 = parseInt(inputPElement.style.width.replace('px', ''), 10)
        pos6 = parseInt(inputPElement.style.height.replace('px', ''), 10)

        mainElement.setAttribute('draggable', false);
     
        // 添加拖动事件
        document.addEventListener("mousemove", zoomWrite, false);
        document.addEventListener("mouseup", mouseUp, false);

        // console.log('拖动开始--', pos5+'/'+pos6);
      }

      function zoomWrite (e) {
        e = e || window.event;
        
        // 需要限制边框显示的最小宽高
        if ((pos1 + e.clientX - pos3) > min_width) {
          writeElement.style.width = pos1 + e.clientX - pos3 + "px"
        }
        if ((pos2 + e.clientY - pos4) > min_height) {
          writeElement.style.height = pos2 + e.clientY - pos4 + "px"
        }
        if ((pos5 + e.clientX - pos3) > min_width) {
          inputPElement.style.width = pos5 + e.clientX - pos3 + "px"
        }
        if ((pos6 + e.clientY - pos4) > (min_height - 50)) {
          inputPElement.style.height = pos6 + e.clientY - pos4 + "px"
        }
        
        // console.log('拖动--', writeElement.style.width+'/'+writeElement.style.height);
      }

      function mouseUp(e) {
        e = e || window.event;

        // mainElement.setAttribute('draggable', true);
        // 删除拖动事件
        document.removeEventListener("mousemove", zoomWrite, false);

        // console.log('拖动开始--', posX+'/'+posY);
        // console.log('拖动结束--', e.clientX+'/'+e.clientY);
      }

      function wmKeyup(e) {
        e = e || window.event;
        if ('Enter' == e.key && e.shiftKey) {
          inputPElement.innerHTML = '<p data-we-empty-p=""><br></p>'
          // placeholderElement.innerHTML = '<p>请输入内容(Shift+Enter发送)</p>'
        }
      }
    },
    moreCallBack (inUser) {
      // console.log('新用户消息--', inUser.userCode);
      let view = this
      
      clearInterval(this.moreInterval)

      let index_interval = {
        key: 'index',
        field: inUser.userCode, 
        val: ''
      }
      // this.$store.commit('socket/clearViewInterval', index_interval)

      if (this.showMore) {
        this.moreInterval = setInterval(() => {
          view.showChatMoreInfo(inUser.userCode)
        }, 1000);

        index_interval.val = this.moreInterval
        this.$store.commit('socket/updateViewInterval', index_interval)
      } 
    },
    showChatMoreInfo (userCode) {
      let chat_list = this.chatMap[userCode]
      let pElement = document.getElementById('moreInfoList')

      if (chat_list != undefined) {
        let content= ''
        chat_list.forEach(div_info => {
          content = content + div_info
        });

        if (pElement && pElement.innerHTML != content) {
          pElement.innerHTML = content
          
          // 滚动条到最下面
          // var scoller = this.$refs.moreInfoList
          if (pElement.scrollHeight != undefined) {
            pElement.scrollTop = pElement.scrollHeight
          }
        }
        
        // 滚动条颜色(没效果)
        /* this.bbUserList.forEach((user,i) => {
          console.log(user.userCode);
          if (user.userCode == userCode) {
            console.log('这是滚动条颜色修改--', user.color);
            // view.loadStyleString(".sc2::-webkit-scrollbar-thumb{background-color: "+user.color+";}");
            document.getElementById("moreInfoList").style.webkitScrollbarThumb = "background-color("+user.color+")"
            document.getElementById("moreInfoList").style.webkitScrollbarThumb = "backgroundColor("+user.color+")"
          }
        }); */

      } else {
        pElement.innerHTML = ''
      }
    },
    moreInfoRead () {
      // console.log('读取更多信息');
      let view = this
      this.showMore = this.showMore?false:true
      let userCode = this.$refs.sendMsg.$el.dataset.code
      
      let index_interval = {
        key: 'index',
        field: userCode, 
        val: ''
      }
      
      // let last_info = elPopper.find('#moreInfoList').html() + '<div>'+elPopper.find('#bb_info').html()+'</div>'
      // elPopper.find('#moreInfoList').html(last_info)
      if (this.showMore) {
        clearInterval(this.moreInterval)
        this.moreInterval = setInterval(() => {
          view.showChatMoreInfo(userCode)
        }, 1000);

        index_interval.val = this.moreInterval
        this.$store.commit('socket/updateViewInterval', index_interval)
      } else {
        clearInterval(this.moreInterval)
      }
    },
    handleOpen(key, keyPath) {
      // console.log(key, keyPath);
    },
    // 点击泡泡时回调此方法
    ballClick (view, user) {
      // console.log('选中--', user.userCode)
      // view.selectUser = user
      
      /* this.activeBox.child.forEach((ball, i) => {
        // 重置球体移动新位置
        if (view.e.id == ('bb_'+user.userCode)) {
          console.log('球体位置', ball.x+'/'+ball.y);
          console.log('球体位置', view.px+'/'+view.py);
          ball.x = view.px
          ball.y = view.py
        }
      }); */
    },
    loadStyleString(css) {
      var style = document.createElement("style");
      try {
          style.appendChild(document.createTextNode(css));
      } catch(ex) {
          style.styleSheet.cssText = css;
      }
      var head = document.getElementsByTagName('head')[0];
      head.appendChild(style);
    },
    changeMsgBorder (inUser, type) {
      let pElement = document.getElementById('msg_name_'+inUser.userCode).parentNode
      let child_num = pElement.childNodes.length
      // console.log('消息子元素数量--', child_num);
      let msg_el_num = 3

      if (type === 1) { 
        // 消息框右移大小  
        let msg_left = 0
        if (child_num > msg_el_num) {
          // 最后一个元素样式设定
          let msg_final = pElement.childNodes[child_num-1].firstChild
          msg_final.style.borderTop = '1.5px solid ' + (inUser.color==undefined?'red':inUser.color)
          msg_final.style.borderRadius = '0px 0px 25px 0px'
          // 最后一个元素before属性
          this.loadStyleString("#"+msg_final.id+":before{display: none;}");
          msg_left = msg_final.offsetLeft
          if (inUser.radius > 30) {
            msg_final.style.left = msg_left + (inUser.radius-45) + 'px'
          }
  
          // 中间元素样式设定
          if (child_num > (msg_el_num+1)) {
            for (let i = 2; i < child_num-1; i++) {
              let msg_next = pElement.childNodes[i].firstChild
              // msg_next.style.border = '1px solid ' + inUser.color
              msg_next.style.borderTop = '1.5px solid ' + (inUser.color==undefined?'red':inUser.color)
              msg_next.style.borderRadius = '0px 0px 0px 0px'
              this.loadStyleString("#"+msg_next.id+":before{display: none;}");
            }
          }
          
          // 第一个元素样式设定
          let msg_first = pElement.childNodes[msg_el_num-1].firstChild
          msg_first.style.borderRadius = '25px 0px 0px 0px'
        } else {
          // 最后一个元素样式设定
          let msg_final = pElement.childNodes[child_num-1].firstChild
          msg_final.style.borderRadius = '25px 0px 25px 0px'
          msg_left = msg_final.offsetLeft
          if (inUser.radius > 30) {
            msg_final.style.left = msg_left + (inUser.radius-45) + 'px'
          }
        }
      } else if (child_num >= msg_el_num &&  type === 0) {
        // 第一个元素样式设定
        let msg_first = pElement.childNodes[msg_el_num-1].firstChild
        msg_first.style.borderTop = '15px solid ' + (inUser.color==undefined?'red':inUser.color)
        if (child_num == msg_el_num) {
          msg_first.style.borderRadius = '25px 0px 25px 0px'
        } else {
          msg_first.style.borderRadius = '25px 25px 0px 0px'
        }

        this.loadStyleString("#"+msg_first.id+":before{margin-left: 15px;--height: calc(22.5);width: 24px;height: 55px;"+
        "position: absolute;top: 5px;bottom: 1rem;right: calc(100% - 2px);clip-path: polygon(0 100%, 100% 0, 100% 75%);display: block;}");
      }
    },
    getCGUsername (server, client) {
      let result_name = '匿名用户'
      console.log('消息框群信息--', server, '/', JSON.stringify(this.cguData));
      if (this.cguData[server]) {
        let cgu_list = this.cguData[server].cguList
        cgu_list.forEach(item => {
          if (item && item.userCode == client) {
            result_name = item.username
          }
        });
      }
      return result_name
    },
    // 消息发送框
    bbMessage (inUser) {
      // console.log('消息框用户信息--', JSON.stringify(inUser));

      // 如果消息框存在，则更新内容
      // 发送消息框内容更新
      let el_bbPopoer = document.getElementById('bbPopoer')
      // 群用户名称样式
      let group_nstyle = null

      if (el_bbPopoer.style.display == 'block') {
        let message_el = document.getElementById('bb_info')
        let userCode = this.$refs.sendMsg.$el.dataset.code

        // 保存上一条聊天记录
        let chat_list = this.chatMap[inUser.userCode]
        if (chat_list == undefined) {
          chat_list = []
        }

        let in_style = '<div style="max-height:220px;overflow-y:auto;max-width:650px;width:auto;display:inline-block !important; display:inline;text-align:left;margin-right:15px;box-shadow: -3px 3px 5px #E2E0ED;padding: 5px 10px 15px 10px;border: 1px solid #E2E0ED;border-top: 5px solid red;border-radius: 0 25px 0 25px;">'
        let out_style = '<div style="max-height:220px;overflow-y:auto;max-width:650px;width:auto;display:inline-block !important; display:inline;text-align:left;margin-left:7px;box-shadow: 3px 3px 5px #E2E0ED;padding: 5px 10px 15px 10px;border: 1px solid #E2E0ED;border-top: 5px solid #00ff00;border-radius: 25px 0 25px 0;">' 
        let in_style_0 = '<div style="min-height: 45px;margin: 15px 0px;padding: 0px 0px;">' + in_style
        let in_style_1 = '<div style="min-height: 45px;margin: 15px 0px;padding: 0px 0px;">'
        let out_style_0 = '<div style="min-height: 45px;margin: 15px 0px;padding: 0px 0px;display: flex;justify-content: flex-end;">' 
        if (userCode == inUser.userCode) {
          // console.log('上一条消息--', message_el.innerText);
          let class_content = message_el.getAttribute('class')
          // 这里要判断上一条记录是否已经在列表中
          if (chat_list.length == 1 && chat_list[0].indexOf(message_el.innerText) != -1) {
            // 不再添加
          } else {
            if (class_content.indexOf('bb-info-right') != -1) {
              // message_el.innerText这里如果只取文本内容则无法显示样式和图片等
              chat_list.push(out_style_0 + message_el.innerHTML + '</div>')
            } else {
              // chat_list.push(in_style_0 + message_el.innerHTML + '</span></div>')
              chat_list.push(in_style_1 + message_el.innerHTML + '</div>')
            }
          }
        } else {
          // 其他用户消息保存
          // chat_list.push(in_style_0 + inUser.content + '</span></div>')
          // 这里需要判断是哪方的消息
          // 群消息
          if (inUser.userType == 2 || inUser.userType == 3) {
            group_nstyle = '<label style="font-size: 12px;color: #C0C4CC;margin-left:35px;">(来自--'+this.getCGUsername(inUser.server, inUser.client)+')</label>'
            if (inUser.server == inUser.userCode && inUser.type === '1') {
              chat_list.push(in_style_0 + inUser.content + group_nstyle + '</div></div>')
            } else if (inUser.client == inUser.userCode && inUser.type === '0') {
              chat_list.push(in_style_0 + inUser.content + group_nstyle + '</div></div>')
            } else {
              chat_list.push(out_style_0 + out_style + inUser.content + '</div></div>')
            }
          }
          // 泡泡球为客服
          else if (inUser.userType == 0) {
            if (inUser.type === '0') {
              chat_list.push(in_style_0 + inUser.content + '</div></div>')
            }  else {
              chat_list.push(out_style_0 + out_style + inUser.content + '</div></div>')
            }
          } 
          // 操作人为客服
          else if (this.getUserInfo.userType == 0) {
            if (inUser.type === '1') {
              chat_list.push(in_style_0 + inUser.content + '</div></div>')
            }  else {
              chat_list.push(out_style_0 + out_style + inUser.content + '</div></div>')
            }
          }
          else {
            chat_list.push(out_style_0 + out_style + inUser.content + '</div></div>')
          }
        }
        this.chatMap[inUser.userCode] = chat_list
        
        // 内容显示更新
        // console.log('message_el--', message_el);
		    let left_flag = true
        let out_content = out_style + inUser.content + '</div>'
        if (inUser.server != undefined) {
          // console.log('消息用户信息--', JSON.stringify(inUser));
          if (inUser.userType == 2 || inUser.userType == 3) {
            if (this.getUserInfo.userCode == inUser.client) {
              message_el.innerHTML = out_content
              left_flag = false
            } else {
              if (group_nstyle == null) {
                group_nstyle = '<label style="font-size: 12px;color: #C0C4CC;margin-left:35px;">(来自--'+this.getCGUsername(inUser.server, inUser.client)+')</label>'
              }
              message_el.innerHTML = in_style + inUser.content + group_nstyle + '</div>'
            }
          } 
          // 泡泡球为客服
          else if (inUser.userType == 0) {
            if (inUser.type == '0') {
              message_el.innerHTML = in_style + inUser.content + '</div>'
            } else {
              message_el.innerHTML = out_content
              left_flag = false
            }
          }
          // 操作人为客服
          else if (this.getUserInfo.userType == 0) {
            if (inUser.type == '1') {
              message_el.innerHTML = in_style + inUser.content + '</div>'
            } else {
              message_el.innerHTML = out_content
              left_flag = false
            }
          }
          else {
            if (inUser.server == inUser.userCode && inUser.type === '1') {
              message_el.innerHTML = in_style + inUser.content + '</div>'
            } else if (inUser.client == inUser.userCode && inUser.type === '0') {
              message_el.innerHTML = in_style + inUser.content + '</div>'
            } else {
              message_el.innerHTML = out_content
              left_flag = false
            }
          }
        } else {
          message_el.innerHTML = in_style + inUser.content + '</div>'
        }

        message_el.setAttribute('class', left_flag?'bb-info':'bb-info bb-info-right')
      }

      // 当前操作人的消息不需弹出，只弹出对方的消息
      if (inUser.server != undefined) {
        if (inUser.userType == 2 || inUser.userType == 3) {
          // 如果是当前登录人的群消息，则不显示
          if (inUser.client == this.getUserInfo.userCode) {
            return false
          } else {
            if (group_nstyle == null) {
              group_nstyle = '<label style="font-size: 12px;color: #C0C4CC;margin-left:35px;">(来自--'+this.getCGUsername(inUser.server, inUser.client)+')</label>'
            }
          }
        }
        // 泡泡球为客服
        else if (inUser.userType == 0) {
          // 操作人的消息不显示
          if (inUser.type == '1') {
            return false
          }
        }
        // 操作人为客服
        else if (this.getUserInfo.userType == 0) {
          // 操作人的消息不显示
          if (inUser.type == '0') {
            return false
          }
        } else if (inUser.server == inUser.userCode && inUser.type === '0') {
          return false
        } else if (inUser.client == inUser.userCode && inUser.type === '1') {
          return false
        }
      }

      // 动态新增元素<span id="msg_'+listUser[i].userCode+'"></span>
      let element = document.createElement("span")
      element.id = 'msg_' + inUser.userCode
      let pElement = document.getElementById('msg_name_'+inUser.userCode).parentNode
      // 元素数量
      // let el_num = parseInt(JSON.stringify(pElement.childNodes.length), 10)
      let el_num = pElement.childNodes.length
      // 追加元素
      pElement.appendChild(element);

      // 创建构造器
      let message = Vue.extend(BbMessageView)
      // 创建 Profile 实例，并挂载到一个元素上。
      let new_msg = new message({
        propsData: {
          msg: inUser.content + (group_nstyle!=null?group_nstyle:''),
          userCode: inUser.userCode+'_'+this.msg_num,
          initTime: inUser.initTime,
          borderTop: '15px solid '+(inUser.color==undefined?'red':inUser.color),
          leftType: el_num>2?'atop':'abottom'
        }
      }).$mount('#msg_'+inUser.userCode)

      // 重置消息框的边框
      this.changeMsgBorder(inUser, 1)

      // 消息在30秒后关闭
      // let create_time = JSON.stringify(inUser.initTime)
      inUser.msgNum = this.msg_num
      let newUser = JSON.parse(JSON.stringify(inUser))
      let dom_msg = document.getElementById('bbMessage_'+inUser.userCode+'_'+this.msg_num)
      let view = this
      setTimeout(function () {
        // new_msg.$destroy()
        dom_msg.parentNode.removeChild(dom_msg);
        view.changeMsgBorder(newUser, 0)
      }, 30000); 

      this.msg_num = this.msg_num + 1
      return true
    },
    bbSocketMsg (e) {
      // console.log('bbWebSocket后台消息：', e.data)
      
      // 消息需要解密
      // let decrypt_msg = AES.decrypt(e.data.replaceAll('\r\n', ''), this.$store.getters.getAesKey)
      // console.log('解密后消息--', decrypt_msg);
      let backData = JSON.parse(e.data)

      if (backData.code == 1) {
        let contactCode = backData.data.contactCode
        let chatList = backData.data.chatList
        let final_data = chatList[chatList.length - 1]
        // 空信息不显示
        if (final_data != null && final_data.content.length > 0) {
          let index = null
          let view = this
          this.bbUserList.forEach((user,i) => {
            // 通过contactCode判断对应用户
            if (user.userCode == contactCode && final_data.initTime > user.initTime) {
              if (chatList.length > 1) {
                chatList.forEach(chat => {
                  if (chat.initTime > user.initTime && chat.status == '0') {
                    // user = final_data
                    user.content = chat.content
                    user.server = chat.server
                    user.client = chat.client
                    user.type = chat.type
                    user.initTime = chat.initTime
                    view.bbMessage(user)
                  }
                });
              } else {
                // user = final_data
                user.content = final_data.content
                user.server = final_data.server
                user.client = final_data.client
                user.type = final_data.type
                user.initTime = final_data.initTime
                view.bbMessage(user)
              }
            }
            // 如果是拖动出来的泡泡球，则显示第一条记录
            else if (user.content == undefined && user.draggable) {
              user.content = final_data.content
              user.server = final_data.server
              user.client = final_data.client
              user.type = final_data.type
              user.initTime = final_data.initTime
              view.bbMessage(user)
            }
          });
          /* if (index != null) {
            this.bbUserList[index] = final_data
          } */

          // 如果用户在线，但未读信息则提醒用户
          if (final_data.status == '0') {
            let contact_list = this.$store.getters.getNewContactList
            if (this.onlineUserList.includes(contactCode) && contact_list.includes(contactCode)) {
              this.remindOtherSide(contactCode)
            }
          }
        }

      }
    },
    // 查询更多未读聊天记录
    readMoreLog (inUser) {
      let view = this
      let login_user = this.getUserInfo
      let params_in = {}
      params_in.receiver = login_user.userCode
      params_in.client = inUser.userCode
      params_in.status = '0'
      params_in.pageSize = inUser.num-5
      params_in.initTime = inUser.initTime
      this.$ajaxRequest({
        url: '/chat/readMore',
        method: 'POST',
        data: params_in
      }).then(function (res) {
        console.log('未读的聊天记录--', res.data)
        if (res.code == 1) {
          let nrList = res.data
          nrList.forEach(user => {
            view.bbMessage(user)
          });
        }
      })
    },
    initBbSocket (inUser) {
      let view = this
      let socketNew = new ReadWebSocket({socketId: inUser.userCode})
      // console.log('socketList--', this.socketList);
      
      // 开启websocket链接
      socketNew.initWebSocket(inUser, this.getUserInfo, this.bbSocketMsg)

      setTimeout(function(){
        if (socketNew.socketState()) {
          // 将socket对象缓存
          view.newRWS(socketNew)
          // 新联系用户已经在泡泡球中打开，通知右边抽屉列表（更新样式）
          if (inUser.draggable) {
            // 如果是拖动的泡泡球，则不需要通知
          } else {
            view.newBBUser = inUser
          }
        }
      }, 1000)

      // 显示未读记录(大于5时需要从数据库中读取，否则直接显示第一次缓存数据)
      if (inUser.num > 5) {
        // this.readMoreLog(inUser)
        this.$slideMsg.warn('【'+inUser.username+'】的未读信息较多，请在右边点击用户查看');
      } else if (inUser.draggable) {
        // 拖动用户没有新消息
      } else {
        // 添加消息框
        this.bbMessage(inUser)
      }

      // 测试
      /* setTimeout(function(){
        inUser.content = '这是测试消息1'
        view.bbMessage(inUser)
      }, 3000)
      setTimeout(function(){
        inUser.content = '这是测试消息2'
        view.bbMessage(inUser)
      }, 6000)
      setTimeout(function(){
        inUser.content = '这是测试消息3'
        view.bbMessage(inUser)
      }, 9000) */
    },
    ballExist (inUser) {
      let index = null
      this.bbUserList.forEach((user, i) => {
        if (user.userCode == inUser.userCode) {
          index = i
        }
      });
      return index
    },
    // 取关对话（销毁泡泡）
    closeChat (e) {
      let userCode = this.$refs.sendMsg.$el.dataset.code
      let inUser = {userCode: userCode}
      this.destoryBall(inUser)

      // 关闭群对话
      this.$store.commit('clearUgChatList', userCode)
    },
    destoryBall (inUser) {
      let index = this.ballExist(inUser)
      if (index != null) {
        console.log('销毁泡泡球--', inUser.username);
        // 将泡泡球从列表中删除
        this.bbUserList.splice(index, 1)

        // 销毁泡泡socket
        this.$store.commit('socket/destroySocket', inUser.userCode)
        
        // 清空群编号(如果为群的话)
        this.$store.commit('clearUgChatList', inUser.userCode)

        // 销毁泡泡
        if (this.activeBox != null) {
          let bb_index = null
          this.activeBox.child.forEach((ball, i) => {
            if (ball.ages.userCode == inUser.userCode) {
              bb_index = i
              // 关闭消息输入框
              ball.closeMsg()
            }
          });
          if (bb_index != null) {
            // 如果泡泡的消息输入框显示，则关闭
            /* let userCode = this.$refs.sendMsg.$el.dataset.code
            if (userCode == inUser.userCode) {
              let el_bbPopoer = document.getElementById('bbPopoer')
              el_bbPopoer.style.display == 'none'
              // this.bbVisible = false
            } */

            // 销毁泡泡球运行对象
            this.activeBox.child.splice(bb_index, 1)
            // 销毁泡泡球div
            let bb_div = document.getElementById('bb_'+inUser.userCode)
            if (bb_div != undefined && bb_div != null) {
              bb_div.parentNode.removeChild(bb_div)
            }
          }
        }
      }
    },
    // 定时销毁泡泡球
    ballShowTimeout (inUser) {
      // 登录用户设置的泡泡显示时长
      let login_user = this.getUserInfo
      let view = this
      let time_out = login_user.ballShowSeconds * 1000
      setTimeout(() => {
        // 如果正在对话中则取消关闭(取code失败)
        // let userCode = view.$refs.sendMsg.dataset.code
        //获取demo类下面的所有子元素
        let el_bbPopoer = document.getElementById('bbPopoer')
        if (el_bbPopoer.style.display == 'block') {
          // let children = el_bbPopoer.childNodes
          // let active_falg = false
          // 因为浏览器会把dom节点下的空格 换行 文本都会当成一个元素 ，我们要找元素节点的话，只能把他们剔除
          // (取code失败)
          /* for(var i;i<children.length;i++){
            if(children[i].id == inUser.userCode){
              active_falg = true
            }
          } */

          // 使用getAttribute获取 data- 属性
          var msg_el = document.getElementsByClassName('input-w-message')[0]
          var msg_code = msg_el.getAttribute('data-code');
          // var msg_code = el_bbPopoer.childNodes[2].childNodes[0].getAttribute('data-code');

          if(msg_code == inUser.userCode){
            // 取消销毁泡泡
          } else {
            view.destoryBall(inUser)
          }
        } else {
          view.destoryBall(inUser)
        }
      }, time_out);
    },
    // 加载群成员数据
    initCGUDataList (inUser) {
      let view = this
      // 如果数据之前已经加载过，则直接取
      if (Object.hasOwnProperty.call(this.cguData, inUser.userCode)) {
        // 为球体新增群成员数据
        view.activeBox.child.forEach((ball, i) => {
          // console.log('泡泡球用户信息--', JSON.stringify(ball.ages));
          if (ball.ages.userCode == inUser.userCode) {
            ball.cgu = view.cguData[inUser.userCode]
          }
        });

        // 初始化socket和消息框
        this.initBbSocket(inUser)
      } else {
        this.$ajaxRequest({
          url: '/chat/getCGUserList',
          method: 'POST',
          data: {
            userCode: this.getUserInfo.userCode,
            chatGroupCode: inUser.userCode
          }
        }).then(res => {
          // console.log('群成员列表--', JSON.stringify(res));
          if (res.code == 1) {
            // 群用户列表
            // view.cgUserList = res.data.cguList
            // 用户头像列表
            // view.userPPList = res.data.ppList
            // 往父组件index.vue转成员数据
            // view.$emit('update:cgu-data', res.data)
            // 群对应的成员数据
            view.cguData[inUser.userCode] = res.data
            
            // 为球体新增群成员数据
            view.activeBox.child.forEach((ball, i) => {
              // console.log('泡泡球用户信息--', JSON.stringify(ball.ages));
              if (ball.ages.userCode == inUser.userCode) {
                ball.cgu = res.data
              }
            });
            
            // 初始化socket和消息框
            view.initBbSocket(inUser)
          }
        })
        
      }
    },
    ballGo(listUser) {
      let view = this

      // console.log('列表--', JSON.stringify(listUser))
      // 如果用户禁止泡泡提示(从列表中直接拖动外)
      if (this.dragBall == false && this.userInfo.showBall == 1) {
        return false
      } 

      this.bbUserList = this.bbUserList.concat(listUser)

      // 判断用户是否在黑名单列表中(已经在后端判断)
      /* listUser = this.checkBlackUser(listUser)
      if (listUser.length == 0) {
        return false
      } */

      if (this.activeBox == null) {
        this.activeBox = new BallBox('container');
        this.activeBox.ballRun();
      }

      function rand(m, n) { 
        return m + parseInt((n - m) * Math.random()); 
      }
      
      var bColors = ['#00FFFF', '#7FFF00', '#DC143C', '#9400D3', '#FF0000', '#FF1493', '#00BFFF', '#FF00FF', '#FFD700', '#ADFF2F', '#FF4500', '#00FF00'];
      var bcLength = bColors.length;
      var oC = document.getElementById('container');
      var mxwidth = oC.offsetWidth;
      var mxheight = oC.offsetHeight;


      for (var i = 0; i < listUser.length; i++) {
        var b = rand(25, 45);
        // 泡泡球开始位置
        var x = this.dragBall?listUser[i].x:rand(b, mxwidth - b);
        var y = this.dragBall?listUser[i].y:rand(b, mxheight - b);
        let bColor = bColors[rand(0, bcLength)]
        // 设置消息上边框颜色
        listUser[i].color = bColor
        // 球体半径
        listUser[i].radius = b
        // 用户头像
        let profilePicture = (listUser[i].profilePicture!=undefined&&listUser[i].profilePicture!='')?this.getPath+listUser[i].profilePicture:''
        /* let element = null
        if (profilePicture != '') {
          element = document.createElement('img')
          element.src = profilePicture
        } else {
          element = document.createElement('div')
        } */

        var ball = new Ball({
          'b': b,
          'x': x,
          'y': y,
          // 'e': element,
          // 'sx':rand(1,4),
          // 'sy':rand(1,4),
          'sx': 1,
          'sy': 1,
          'html': '<span id="msg_name_'+listUser[i].userCode+'" class="clip" style="margin:0px auto;display:inline-block;width:100px;" >' + listUser[i].username + '</span>',
          'c': bColor,
          'bs': '0 6px 15px '+bColor+',inset 0 5px 10px 5px rgba(255,255,255,1)',
          // 'opa': rand(60, 100) / 100,
          'opa': 100,
          'cls': 'ball-animation',
          'fun': this.ballClick,
          'moreCallBack': this.moreCallBack,
          'ages': listUser[i],
          'pp': profilePicture,
          'lu': this.getUserInfo,
          'callBack': function (n) {
            //this.setB(rand(30,50));
            //this.setM();
            // this.setOpa(rand(60, 100) / 100);
            //if(n%3==0){this.setC('url(../img/phiz/'+rand(1,6)+'.gif)')};//撞三次改变下图片
            /* if (n % 3 == 0) {
              // let bColor = bColors[rand(0, bcLength)]
              // 改变颜色
              // this.setC(bColor)
              // this.setBS('0 6px 15px ' + bColor + ',inset 0 5px 10px 5px rgba(255,255,255,1)')
            } */
            
            //撞三次改变下图片
            /* if (n % 50 == 0) {
              this.setB(rand(40 + parseInt((n > 1000 ? 1000 : n) / 50), 60 + parseInt((n > 1000 ? 1000 : n) / 50)));
            } */
            //撞50次改大小
            //this.setHTML('<h3 style="position:absolute;top:-60px;width:100px;">'+says[rand(0,saysLength)]+'</h3>');
          },
          'enterCallBack': function (e) {
            
          }
        });

        this.activeBox.addBall(ball);

        // 泡泡时间限制
        // 登录用户设置的泡泡显示时长(这个时间可能更新，所以从计算属性中取)
        let login_user = this.getUserInfo
        if (this.dragBall == false && login_user != null && login_user.ballShowSeconds > 0) {
          this.ballShowTimeout(listUser[i])
        }

        // 如果是群/房间，则要加载成员信息
        if (listUser[i].userType == 2 || listUser[i].userType == 3) {
          this.initCGUDataList(listUser[i])
        } else {
          // 初始化socket和消息框
          this.initBbSocket(listUser[i])
        }
        
      }

      this.dragBall = false
    },
    checkBlackUser (listUser) {
      let black_list = this.getUserInfo.blackUserList
      if (black_list == undefined || black_list.length == 0) {
        return listUser
      }

      let result_list = []
      listUser.forEach(item => {
        if (black_list.includes(item.userCode) == false) {
          result_list.push(item)
        }
      });
      return result_list
    }
  }
}

export default bbMixin