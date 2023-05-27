<template>
  <div id='UserConfig'>
    <div class="config-header">
      <div>用户配置</div>
    </div>
    <div class="config-content">
      <el-form ref="configForm" :model="configForm" size="mini">
        <div class="config-title el-icon-setting">基本设置</div>
        <!-- <el-divider content-position="left">少年包青天</el-divider> -->
        <div class="config-row">
          <div class="row-left">
            <el-switch v-model="configForm.showBall"></el-switch>
          </div>
          <label>禁止泡泡提示</label>
        </div>
        <div class="config-row" >
          <div class="row-left">
            <el-select v-model="configForm.ballShowSeconds" style="width: 90px;margin: 5px 15px;" placeholder="请选择">
              <el-option v-for="item in ssOptions" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </div>
          <label>泡泡显示时长</label>
        </div>
        <div class="config-row" style="margin-top: 30px;">
          <div class="row-left">
            <el-switch v-model="configForm.strangerContact"></el-switch>
          </div>
          <label>禁止陌生人联系</label>
        </div>
        <div class="config-row" >
          <div class="row-left">
            <el-select v-model="configForm.ppAvtiveType" style="width: 90px;margin: 5px 15px;" placeholder="请选择">
              <el-option v-for="item in ppOptions" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </div>
          <label>来新消息时，头像动作类型</label>
        </div>
        <div class="config-row" style="margin-top: 30px;">
          <div class="row-left">
            <el-switch key="outConfirm" v-model="configForm.outConfirm"></el-switch>
          </div>
          <label>用户退出确认提示</label>
        </div>

        <!-- 安全相关 -->
        <div class="config-title el-icon-key" style="margin-top: 30px;">安全设置</div>
        <div class="config-row" >
          <div class="row-left">
            <el-switch key="bindIpFlag" v-model="configForm.bindIpFlag" :disabled="bindIpFlag" @change="bindIpChange"></el-switch>
          </div>
          <label>绑定IP（注意：绑定成功后，WEB端只有当前IP可以登录）</label>
        </div>
        <div class="config-row" style="margin-top: 15px;">
          <div class="row-left">
            <el-switch key="bindBrowerFlag" v-model="configForm.bindBrowerFlag" :disabled="bindBrowerFlag" @change="bindBrowerChange"></el-switch>
          </div>
          <label>绑定浏览器（注意：绑定成功后，WEB端只有本机当前版本的浏览器和当前IP可以登录）</label>
        </div>
        <div class="config-row" style="margin-top: 35px;">
          <div class="row-left row-left-btn">
            <el-button key="unbind" type="success" size="mini" round @click="unbind" :disabled="bindIpFlag==false">解绑</el-button>
          </div>
          <label>解绑信息(暂不支持短信通知)</label>
        </div>
      </el-form>
      <div class="config-save">
        <el-button type="primary" :disabled="(changeCheck?false:true)" round @click="onSubmit">保存设置</el-button>
      </div>
    </div>
  </div>
</template>

<script>
  import { unbindByMail } from '@/axios/api'

  export default {
    name: 'UserConfig',
    data() {
      return {
        userInfo: null,
        ballSSSelected: 0,
        ppSelected: 0,
        changeFlag: false,
        oldForm: null,
        bindIpFlag: false,
        bindBrowerFlag: false,
        oldForm: null,
        configForm: {
          configId: null,
          userCode: null,
          showBall: false,
          ballShowSeconds: 0,
          strangerContact: false,
          ppAvtiveType: 0,
          outConfirm: true,
          bindIpFlag: false,
          bindBrowerFlag: false
        },
        ssOptions: [{
          value: 0,
          label: '无限'
        }, {
          value: 30,
          label: '30s'
        }, {
          value: 60,
          label: '1分钟'
        }, {
          value: 180,
          label: '3分钟'
        }, {
          value: 900,
          label: '15分钟'
        }, {
          value: 1800,
          label: '30分钟'
        }],
        ppOptions: [{
          value: 0,
          label: '晃动'
        }, {
          value: 1,
          label: '闪动'
        }]
      }
    },
    props: [],
    created() {

    },
    filters: {

    },
    methods: {
      bindIpChange (e) {
        if (this.configForm.bindIpFlag == false) {
          this.configForm.bindBrowerFlag = false
        }
      },
      bindBrowerChange (e) {
        if (this.configForm.bindBrowerFlag) {
          this.configForm.bindIpFlag = true
        }
      },
      unbind () {
        let view = this
        let user = this.getUserInfo
        let params = {
          userCode: user.userCode
        }

        // 测试邮件
        unbindByMail(params).then((res) => {
          // console.log('发送邮件结果--', JSON.stringify(res));
          if (res.code == 1) {
            view.$slideMsg.info('邮件已经发送到您的邮箱(可能要几分钟)，请复制链接地址到浏览器验证解绑信息。', '解绑验证提示', 6500)
          }
        })
      },
      onSubmit() {
        // 只有当参数值发生改变时才提交
        if (this.changeCheck == false) {
          // this.$slideMsg.warn('哈哈……没想到吧');
          this.$slideMsg.warn('无效的操作');
          return false
        }

        // 判断邮件和手机号是否存在，否则绑定后无法解绑
        let user_info = this.getUserInfo
        if (this.bindIpFlag == false && this.configForm.bindIpFlag) {
          // console.log('绑定提示--', (user_info.mobile || user_info.email));
          if (user_info.mobile || user_info.email) {
            this.submitStart()
          } else {
            this.$confirm('您的手机和邮箱信息不完整，绑定信息后将无法进行解绑，是否继续？', '绑定信息提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }).then(() => {
              this.submitStart()
            }).catch(() => {
              
            });
          }
        } else {
          this.submitStart()
        }

        // console.log('信息--', JSON.stringify(this.configForm))
      },
      submitStart () {
        let params_in = JSON.parse(JSON.stringify(this.configForm))
        // 如果用户已经绑定过信息，则不需要绑定
        if (this.bindIpFlag) {
          params_in.bindIpFlag = false
        } else if (params_in.bindIpFlag) {
          this.$emit('update:bindfromuc', 1)
        }
        if (this.bindBrowerFlag) {
          params_in.bindBrowerFlag = false
        }

        this.$ajaxRequest({
          url: '/config/patchUserConfigInfo',
          method: 'POST',
          data: params_in
        }).then(res => {
          // console.log('返回信息--', JSON.stringify(res))
          if (res.code == 1) {
            this.$slideMsg.message(res.msg)

            // 更新用户缓存里面的配置属性
            this.userInfo.showBall = this.configForm.showBall ? 1 : 0
            this.userInfo.ballShowSeconds = this.configForm.ballShowSeconds
            this.userInfo.strangerContact = this.configForm.strangerContact ? 1 : 0
            this.userInfo.ppAvtiveType = this.configForm.ppAvtiveType
            this.userInfo.outConfirm = this.configForm.outConfirm ? 1 : 0
            this.$store.commit('updateUserInfo', this.userInfo)

            if (this.configForm.bindIpFlag) {
              this.bindIpFlag = true
            }
            if (this.configForm.bindBrowerFlag) {
              this.bindBrowerFlag = true
            }
            
            // 改变后的form对象
            this.oldForm = JSON.parse(JSON.stringify(this.configForm))
          } else {
            this.$slideMsg.message(res.msg, 'error')
          }
        })
      }
    },
    mounted() {
      this.userInfo = this.getUserInfo
      // console.log('之前--', JSON.stringify(this.configForm))

      // 加载用户配置
      this.$ajaxRequest({
        url: '/config/getUserConfigInfo',
        method: 'POST',
        data: { userCode: this.userInfo.userCode }
      }).then(res => {
        // console.log('返回信息--', JSON.stringify(res))
        if (res.code == 1) {
          var data = res.data
          data.showBall = data.showBall == 0 ? false : true
          data.strangerContact = data.strangerContact == 0 ? false : true
          data.outConfirm = data.outConfirm == 0 ? false : true
          this.configForm = data
          this.ballSSSelected = data.ballShowSeconds
          this.ppSelected = data.ppAvtiveType

          if (data.bindIpFlag) {
            this.bindIpFlag = true
            this.configForm.bindIpFlag = true
          }
          if (data.bindBrowerFlag) {
            this.bindBrowerFlag = true
            this.configForm.bindBrowerFlag = true
          }

          // 最初的form对象
          this.oldForm = JSON.parse(JSON.stringify(this.configForm))
        } else {
          this.$slideMsg.message(res.msg, 'error')
        }
      })
    },
    computed: {
      getUserInfo: function (params) {
        return this.$store.getters.getUserInfo
      },
      changeCheck: function (params) {
        // console.log('propsA--', JSON.stringify(this.configForm))
        // console.log('propsB--', JSON.stringify(this.oldForm))
        
        if (this.oldForm != null) {
          for (const key in this.configForm) {
            if (Object.hasOwnProperty.call(this.configForm, key)) {
              // const element = object[key];
              if (this.configForm[key] !== this.oldForm[key]) {
                return true
              }
            }
          }
        }
        return false
      }
    },
    watch: {
      configForm: function (newV, oldV) {
        // console.log('newV--', JSON.stringify(newV))
        if (oldV.userCode != null) {
          if (newV != oldV) {
            this.changeFlag = true
          }
        }
      }
    }
  }
</script>

<style scoped>
  @import url('./css/userConfig.css');
</style>