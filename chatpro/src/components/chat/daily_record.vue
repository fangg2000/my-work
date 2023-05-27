<template>
  
  <!-- 日记内容 -->
  <div  id="DailyRecord" class="record-container" >
    <div class="record-header">
      <!-- <span class="record-title">日记标题</span> -->
      <span class="record-time">创建于--{{timeFormat.format(createTime)}}</span>
    </div>
    <div class="record-content demoScroll sc2">
      <span v-html="recordData.content"></span>
    </div>
    <div class="record-other">
      <div class="other-num">
        <label class="fa fa-eye fa-lg">&nbsp;{{recordData.reviewNum}}</label>
        <label class="fa fa-commenting-o fa-lg">&nbsp;{{getDiscussList.length}}</label>
        <span class="fa fa-folder fa-lg dis-reply" @click="collectDR(drId)">&nbsp;{{recordData.collectNum}}</span>
        <span class="fa fa-reply fa-lg dis-reply" @click="replyWrite(drId, '0', userInfo.username)"></span>
      </div>
    </div>
    <div class="record-discuss demoScroll sc2">
      <!-- <div class="discuss-title">评论：</div>
      <ul class="discuss-list">
        <div v-for="(item, index) in discussList" :key="'dis_'+item.userCode+'_'+index">{{item.content}}</div>
      </ul> -->
      <div class="discuss-list" v-for="(item, index) in getDiscussList" :key="'parent_'+item.userCode+'_'+index">
        <el-avatar class="discuss-pp" :size="45">
          <img src="@static/img/user/001.jpg" draggable="false"/>
        </el-avatar>
        <div class="discuss-right">
          <div class="discuss-num">
            <label>{{item.username}}&nbsp;<span class="reply-time">{{timeFormat.timeAgo(item.createTime)}}</span></label>
          </div>
          <div class="discuss-content">{{item.content}}</div>
          <div class="discuss-footer">
            <span class="fa fa-thumbs-o-up fa-lg dis-agree agree" @click="agreeYou(item.userCode, item.disId, drId)">&nbsp;{{item.agreeNum}}</span>
            <span class="fa fa-reply fa-lg dis-reply reply" @click="replyWrite(drId, item.disId, item.username)"></span>
          </div>
          <div v-if="item.children.length > 0" class="discuss-back" >
            <div v-for="(child, re_index) in item.children" :key="'child_'+child.userCode+'_'+re_index">
              <div v-if="re_index == 0" class="reply-list">
                <el-avatar class="reply-pp" :size="45">
                  <img src="@static/img/user/001.jpg" draggable="false"/>
                </el-avatar>
                <div class="reply-right">
                  <div class="reply-num">
                    <label>{{child.username}}<span class="reply-other"></span>@{{child.replyName}}&nbsp;<span class="reply-time">{{timeFormat.timeAgo(child.createTime)}}</span></label>
                  </div>
                  <div class="reply-content">{{child.content}}</div>
                  <div class="reply-footer">
                    <span class="fa fa-thumbs-o-up fa-lg dis-agree agree" @click="agreeYou(child.userCode, child.disId, drId)">&nbsp;{{child.agreeNum}}</span>
                    <span class="fa fa-reply fa-lg dis-reply reply" @click="replyWrite(drId, child.disId, child.username)"></span>
                  </div>
                </div>
                <el-divider class="reply-expand" v-if="item.children.length > 1">
                  <el-badge :value="item.children.length-1" class="item" type="primary">
                    <i class="fa fa-angle-double-down fa-lg reply-expand-down" @click="showMoreDisInfo($event, item.userCode, index, item.children.length-1)"></i>
                  </el-badge>
                </el-divider>
              </div>
              <div v-else :ref="'rp_'+item.userCode+'_'+index+'_'+re_index" style="display: none;">
                <div class="reply-list">
                  <el-avatar class="reply-pp" :size="45">
                    <img src="@static/img/user/001.jpg" draggable="false"/>
                  </el-avatar>
                  <div class="reply-right">
                    <div class="reply-num">
                      <label>{{child.username}}<span class="reply-other"></span>@{{child.replyName}}&nbsp;<span class="reply-time">{{timeFormat.timeAgo(child.createTime)}}</span></label>
                    </div>
                    <div class="reply-content">{{child.content}}</div>
                    <div class="reply-footer">
                      <span class="fa fa-thumbs-o-up fa-lg dis-agree agree" @click="agreeYou(child.userCode, child.disId, drId)">&nbsp;{{child.agreeNum}}</span>
                      <span class="fa fa-reply fa-lg dis-reply reply" @click="replyWrite(drId, child.disId, child.username)"></span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="discuss-back"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import Vue from 'vue'
  import {DateTime} from '@/util/timeUtil.js'

  const dis_list = [
    {'discussId': 1, 'pp': '', 'recordId': 123456, 'userCode': 'U123456781', 'username': '张三', 'content': '阿秀，是你吗？你妈叫你回家吃饭呢', 'agreeNum': 1, 'parentId': 0, 'createTime': 1639833995000},
    {'discussId': 2, 'pp': '', 'recordId': 123457, 'userCode': 'U123456782', 'username': '李四', 'content': '国足没男人了吗？怎么叫那11个软蛋上去？', 'agreeNum': 5, 'parentId': 0, 'createTime': 1639843995000},
    {'discussId': 3, 'pp': '', 'recordId': 123458, 'userCode': 'U123456783', 'username': '王五', 'content': '有啊，还有11个软蛋替补……', 'agreeNum': 34, 'parentId': 2, 'createTime': 1639533995000},
    {'discussId': 4, 'pp': '', 'recordId': 123459, 'userCode': 'U123456784', 'username': '吴六', 'content': '不，是15个', 'agreeNum': 5, 'parentId': 3, 'createTime': 1639863995000},
    {'discussId': 5, 'pp': '', 'recordId': 123450, 'userCode': 'U123456785', 'username': '杨七', 'content': '哎，今天真冷啊，比国足输越南还冷……', 'agreeNum': 66, 'parentId': 0, 'createTime': 1639733995000},
    {'discussId': 6, 'pp': '', 'recordId': 123451, 'userCode': 'U123456786', 'username': '黄八', 'content': '运动员来自全国12个省、自治区、直辖市，包括汉族、满族、佤族、傣族、侗族、彝族、苗族等7个民族，全部为业余选手，职业有工人、农民、学生、职员和自由职业者等。运动员平均年龄25岁，年龄最大的为45岁的高山滑雪运动员张海原，年龄最小的为17岁的单板滑雪运动员耿焱红。', 'agreeNum': 66, 'parentId': 0, 'createTime': 1639733995000},
  ]

  export default {
    name: 'DailyRecord',
    components: {
      
    },
    data () {
      return {
        discussList: [],
        timeFormat: new DateTime('yyyy/MM/dd hh:mm'),
        recordData: {
          drId: '',
          recordId: 0,
          content: '',
          reviewNum: 0,
          collectNum: 0,
          discussNum: 0,
        },
        drDataForMonth: [],
      }
    },
    props: ['drId', 'createTime', 'userInfo', 'replyInfo', 'drData'],
    created () {
      // console.log('当前日记ID--', this.drId);
      if (this.drId) {
        this.initDRInfo(this.drId)
      }
    },
    mounted () {
      // 查询日记
    },
    computed: {
      getDiscussList () {
        let view = this
        let list = []
        // 只取parentId=0的数据
        this.discussList.forEach(element => {
          if (element.parentId == '0') {
            list.push(view.checkReplyA(element))
          }
        });
        // console.log('评论列表--', list);
        return list
      },
      getUserInfo () {
        return this.$store.getters.getUserInfo
      }
    },
    watch: {
      replyInfo: function (newV, oldV) {
        if (newV) {
          this.discussList.push(newV)
        }
      },
      drData: function (newV, oldV) {
        if (newV && newV.recordId == this.recordData.recordId) {
          this.recordData.reviewNum = this.recordData.reviewNum + newV.reviewNum
        }
      }
    },
    methods: {
      collectDR (record_id) {
        let view = this
        let userCode = this.getUserInfo.userCode
        let user_code = this.userInfo.userCode

        // 不能对自己点赞
        if (userCode == user_code) {
          return
        }

        this.$ajaxRequest({
          url: '/daily/patchDRCollectNum',
          method: 'POST',
          data: {recordId: record_id, 
            title: this.recordData.title,
            createTime: this.recordData.createTime,
            userCode: user_code, 
            username: this.userInfo.username
          }
        }).then(function (res) {
          // console.log('日记信息--', JSON.stringify(res))
          if (res.code == 1) {
            if (res.data == 1) {
              view.recordData.collectNum = view.recordData.collectNum + 1
            } else if (res.data == 2) {
              view.recordData.collectNum = view.recordData.collectNum - 1
            }
          }
        })
      },
      updateDisNum (discuss_id)  {
        this.discussList.forEach(item => {
          if (item.disId == discuss_id) {
            item.agreeNum = item.agreeNum + 1
          }
        });
      },
      agreeYou (user_code, discuss_id, record_id) {
        let view = this
        let userCode = this.getUserInfo.userCode

        // 不能对自己点赞
        if (userCode == user_code) {
          return
        }

        this.$ajaxRequest({
          url: '/daily/patchDisNum',
          method: 'POST',
          data: {recordId: record_id, discussId: discuss_id, userCode: user_code}
        }).then(function (res) {
          // console.log('日记信息--', JSON.stringify(res))
          if (res.code == 1 && res.data == 1) {
            view.updateDisNum(discuss_id)
          }
        })
      },
      initDRInfo (record_id) {
        let view = this
        let userCode = this.getUserInfo.userCode
        this.$ajaxRequest({
          url: '/daily/getDailyRecordInfo',
          method: 'POST',
          data: {recordId: record_id, userCode: userCode}
        }).then(function (res) {
          // console.log('日记信息--', JSON.stringify(res))
          if (res.code == 1) {
            view.recordData = res.data
            view.discussList = res.data.discussList
          }
        })
      },
      checkReplyA (item) {
        let view = this
        let children = []
        this.discussList.forEach(element => {
          if (element.parentId == item.disId) {
            element.replyName = item.username
            if (element.username == this.recordData.username) {
              element.username = '作者'
            }
            children.push(element)
            children = children.concat(view.checkReplyB(element))
          }
        });
        item.children = children
        if (item.username == this.recordData.username) {
          item.username = '作者'
        }
        return item
      },
      checkReplyB (item) {
        let view = this
        let children = []
        this.discussList.forEach(element => {
          if (element.parentId == item.disId) {
            // console.log('匹配父节点--', element.username);
            element.replyName = item.username
            if (element.username == this.recordData.username) {
              element.username = '作者'
            }
            children.push(element)
            children = children.concat(view.checkReplyB(element))
          }
        });
        return children
      },
      showMoreDisInfo (e, userCode, p_index, displayNum) {
        e.currentTarget.parentElement.parentElement.parentElement.remove()
        for (let i = 1; i <= displayNum; i++) {
          let element = this.$refs['rp_'+userCode+'_'+p_index+'_'+i];
          element[0].style.display = 'block'
        }
      },
      replyWrite (record_id, parent_id, user_name) {
        // console.log('回复ID--', record_id+'/'+parent_id);
        let write_el = document.getElementById('record-footer')
        write_el.setAttribute('class', write_el.getAttribute('class') + ' reply-write-show')
        let rw_el = document.getElementById('reply-content-w')
        rw_el.focus()
        rw_el.style.backgroundColor = 'white'

        // 回复信息时初始化参数
        // this.recordData.recordId = record_id
        // this.recordData.parentId = parent_id
        let replyData = {
          replyUsername: '@' + user_name,
          recordId: record_id,
          parentId: parent_id,
        }
        this.$emit('update:replyData', replyData)
      }
    },
  }
</script>

<style scoped>
  @import url('./css/daily_record.css');
</style>