<template>
  <div id="Login" class="login-form"> 
    <div class="container">
      <div class="circle">
        <input type="radio" name="tabs" id="tab1"/>
        <label class="nav-label" for="tab1" @click="selectUserType(0)">
          <div class="rain-drop"></div>
          <div class="rain-drop"></div>
          <div class="rain-drop"></div>
          <div class="rain-drop"></div>
          <div class="wave">
            <div class="wave2"></div>
          </div><span class="cu">客服</span>
        </label>
        <input type="radio" name="tabs" id="tab2"/>
        <label class="nav-label" for="tab2" style="display: none;">
          <div class="rain-drop"></div>
          <div class="rain-drop"></div>
          <div class="rain-drop"></div>
          <div class="rain-drop"></div>
          <div class="wave">
            <div class="wave2"></div>
          </div><span class="cu">Account</span>
        </label>
        <input type="radio" name="tabs" id="tab3"/>
        <label class="nav-label" for="tab3" style="display: none;">
          <div class="rain-drop"></div>
          <div class="rain-drop"></div>
          <div class="rain-drop"></div>
          <div class="rain-drop"></div>
          <div class="wave">
            <div class="wave2"></div>
          </div><span class="cu">Settings</span>
        </label>
        <input type="radio" name="tabs" id="tab4" checked="checked"/>
        <label class="nav-label" for="tab4" @click="selectUserType(1)">
          <div class="rain-drop"></div>
          <div class="rain-drop"></div>
          <div class="rain-drop"></div>
          <div class="rain-drop"></div>
          <div class="wave">
            <div class="wave2"></div>
          </div><span class="cu">用户</span>
        </label>
        <!-- <input type="radio" name="tabs" id="tab5"/>
        <label class="nav-label" for="tab5">
          <div class="rain-drop"></div>
          <div class="rain-drop"></div>
          <div class="rain-drop"></div>
          <div class="rain-drop"></div>
          <div class="wave">
            <div class="wave2"></div>
          </div><span class="cu">Billing</span>
        </label>
        <input type="radio" name="tabs" id="tab6"/>
        <label class="nav-label" for="tab6">
          <div class="rain-drop"></div>
          <div class="rain-drop"></div>
          <div class="rain-drop"></div>
          <div class="rain-drop"></div>
          <div class="wave">
            <div class="wave2"></div>
          </div><span class="cu">Miscellaneous</span>
        </label> -->
        <div class="tab-content" id="tab-content1">
          <el-form class="container-all" ref="loginForm" :model="loginForm" :rules="rules">
            <div class="profile"><img class="pic" src="@/assets/logo.png"/>
              <p class="name">欢乐聊天吧</p>
              <p class="account">{{loginForm.userType==1?'用户登录':'客服登录'}}</p>
            </div>
            <div class="formfield">
              <el-form-item class="one" prop="userCode">
                <input id="texter" ref="userCode" type="text" v-model="loginForm.userCode" placeholder="编号" @keydown="checkText('userCode')"/>
                <span class="underline"></span><span class="text"><span>编号</span></span>
              </el-form-item>
              <el-form-item class="two" prop="password">
                <input id="texter" ref="login_pwd" type="password" v-model="loginForm.password" @focus="$event.currentTarget.select()" 
                @keydown="checkText('password')" @keyup.enter="loginByEnter($event)" placeholder="密码"/>
                <span class="underline"></span><span class="text" id="secondtext"><span>密码</span></span>
              </el-form-item>
              <el-form-item class="three">
                <input id="texter" type="text" placeholder="手机"/>
                <span class="underline"></span><span class="text" id="thirdtext"><span>手机</span></span>
              </el-form-item>
            </div>
            <div class="submit">
              <el-button type="primary" @click="onSubmit('loginForm')" >登录</el-button>
              <!-- <el-button @click="checkRegist" :disabled="loginForm.userType==1?false:true">注册</el-button> -->
              <el-button :disabled="true">注册</el-button>
            </div>
          </el-form>
        </div>
        <div class="tab-content" id="tab-content2">
          <h1 class="center"></h1>
        </div>
        <div class="tab-content" id="tab-content3">
          <h1 class="center"></h1>
        </div>
        <div class="tab-content" id="tab-content4">
          <h1 class="center"></h1>
        </div>
      </div>
    </div>
    

    <el-drawer
      title="用户注册"
      size="350px"
      :visible.sync="drawer"
      :with-header="false">
      <span>
        <h1 class="regist-title"><span class="fa fa-user-circle-o fa-lg pull-center ">用户注册</span></h1>
        <regist :drawer.sync="drawer" :success-code.sync="loginForm.userCode" @successRegist="autoLogin" ></regist>
      </span>
    </el-drawer>
    

    <el-dialog
      title="登录验证"
      :visible.sync="dialogVisible"
      width="30%">
      <!-- <span>验证信息</span> -->
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="checkOver">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
  import { login, loginCheck } from '@/axios/api'
  import sessionStore from '@/util/session'
  import regist from './regist'
  import OnlineSocket from '@/socket/onlineSocket'
  import AES from '@/base/aes.js'
  import RSA from '@/base/rsaEncrypt.js'
  import 'font-awesome/css/font-awesome.min.css';

  export default {
    name: 'Login',
    data() {
      return {
        drawer: false,
        dialogVisible: false,
        checkIdentifier: null,
        labelPosition: 'right',
        editeType: 1,
        loginForm: {
          userCode: '',
          password: '',
          userType: 1
        }, 
        rules: {
          userCode: [
            { required: true, message: '请输入用户编号', trigger: 'blur' },
            { min: 2, max: 15, message: '长度在 2 到 15 个字符', trigger: 'blur' }
          ],
          password: [
            { required: true, message: '请输入用户密码', trigger: 'blur' },
            { min: 6, max: 15, message: '长度在 6 到 15 个字符', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      loginByEnter (e) {
        // console.log('回车登录');
        this.onSubmit('loginForm')
      },
      checkRegist () {
        if (this.loginForm.userType != 1) {
          this.$slideMsg.warn('无效操作')
          return false
        }

        this.drawer = true
        return true
      },
      checkText (id) {
        this.$refs['loginForm'].clearValidate(id)
      },
      selectUserType (userType) {
        this.loginForm.userType = userType
      },
      onSubmit(formName) {
        // console.log(this.$refs[formName]);
        this.$refs[formName].validate((valid) => {
          if (valid) {
            var view = this;
            login(this.loginForm.userCode, this.loginForm.password, this.loginForm.userType).then(response => {
              // console.log('登录结果--', JSON.stringify(response))
              if (response.code === 1) {
                view.$slideMsg.message('登录成功')
                sessionStore.setTicket(response.data.ticket.replaceAll('\r\n', ''))
                sessionStore.setItem('loginIdentifier', view.$store.getters.getLoginIdentifier)

                // 缓存用户信息
                view.$store.commit('updateUserInfo', response.data)
                
                view.$router.push('/Home')
              } 
              // 登录验证异常
              else if (response.code === -5) {
                console.log('登录验证异常--', JSON.stringify(response))

                // 这里需要做画面验证
                view.dialogVisible = true
                view.checkIdentifier = response.data.checkIdentifier
                sessionStore.setTicket(response.data.ticket.replaceAll('\r\n', ''))
              } else {
                view.$slideMsg.message('登录失败', 'error')
                // view.$refs.login_pwd.$el.focus()
              }
            })
          } else {
            //console.log('error submit!!');
            //this.$slideMsg.message(res.msg, 'error')
            return false;
          }
        })
      },
      autoLogin (userCode, password) {
        // console.log(userCode+'/'+password)
        this.loginForm.userCode = userCode
        this.loginForm.password = password
        //this.onsubmit('loginForm')
      }, 
      checkOver () {
        let view = this
        this.dialogVisible = false
        loginCheck(this.checkIdentifier).then(function (res) {
          // console.log(res.data)
          if (res.code === 1) {
            view.$slideMsg.message('登录成功')
            
            // 缓存AES密钥(点击登录时已经保存，如果刷新页面则会从用户信息中返回再缓存)
            // view.$store.commit('updateAesKey', res.data.aesKey)
            sessionStore.setTicket(res.data.ticket.replaceAll('\r\n', ''))
            sessionStore.setItem('loginIdentifier', view.$store.getters.getLoginIdentifier)

            // 缓存用户信息
            view.$store.commit('updateUserInfo', res.data)
            
            view.$router.push('/Home')
          } else if (res.code === -5) {
            console.log('登录验证异常--', JSON.stringify(res))

            // 这里需要做画面验证
            // ...

            setTimeout(() => {
              view.dialogVisible = true
            }, 1500);
          }
        })
      }
    },
    components: {
      regist
    }, 
    created () {
      // console.log('浏览器信息--', window.navigator);

      // 加载页面关闭连接
      OnlineSocket.destoryWS()
      this.$store.commit('updateOnlineState', false)
      
      // 测试RSA加密
      // let data = {
      //   userCode: 'sa',
      //   password: 'sasasa',
      //   userType: 1,
      //   loginIdentifier: 'e91d0141b9c6407eb43571718c4d740a',
      //   fingerPrint: 'c51a44bd65a54ae53c3acefc6feebb26',
      //   aesKey: 'wxG7VbeUrl8Svm89'
      // }
      // // console.log('RSA加密结果--', RSA.encrypt('这是一段加密信息'));
      // console.log('RSA加密结果--', RSA.encrypt({"userCode":"sa","password":"sasasa","userType":1,"loginIdentifier":"e91d0141b9c6407eb43571718c4d740a","fingerPrint":"c51a44bd65a54ae53c3acefc6feebb26","aesKey":"wxG7VbeUrl8Svm89"}));
      // console.log('RSA加密结果--', RSA.encrypt(data));

      // let msg = 'Ii5RWI/YrvbmlUUvIFw9zRESFPVCJICuEXHRafpHGxBn3b5wqVyEoVxSk6a/0gdtyEola/EOG00BRr0hNV+GJM042k+VtXwEE4dir25lfgFfv+OLfpobebKMsZlg7J6qZ2cG6plPt+3Japo82vekM6jajqiOV+oIV3TrGcLofBubeoh6Pr6JR0OSoJf6XeTg2hmjnCjNrZqgGIh0nty/VKdRasxgCMftP1n8l+X5w1wSwtSLNgdyr01VYhTVDkDSKxWfpN1cgc4nnfkMkzpWlZuXaq+m6l8sz2TkapVhpNY='
      // console.log('AES解密结果--', AES.decrypt(msg, 'BmeCP6nguEPufD75'));
      // let decrypt_msg = AES.decrypt(msg.replaceAll('\r\n', ''), 'KeXBSTCtKSj4RNj2')
      // console.log('解密后消息--', decrypt_msg);

    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<!-- scoped作用是防止声明的CSS作用在外部元素中 -->
<style scoped>
  @import "./chat/css/login.css";

  .regist-title {
    text-align: center;
  }


</style>