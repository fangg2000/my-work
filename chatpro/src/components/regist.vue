<template>
  <div id="Regist" class="regist-form">

    <el-form class="el-from-regist" ref="registForm" :label-position="labelPosition" :model="registForm" :rules="rules"
      label-width="80px">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="registForm.username"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input type="password" v-model="registForm.password"></el-input>
      </el-form-item>
      <el-form-item label="手机" prop="mobile">
        <el-input v-model="registForm.mobile"></el-input>
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="registForm.email"></el-input>
      </el-form-item>
      <el-form-item label="自动登录" >
        <el-switch v-model="registForm.autoLogin"></el-switch>
      </el-form-item>
      <el-form-item label="性别">
        <el-radio-group v-model="registForm.sex">
          <el-radio label="1">男</el-radio>
          <el-radio label="0">女</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="省份">
        <el-select v-model="registForm.province" placeholder="请选择省份" @change="changePro">
          <el-option
            v-for="item in provinceList"
            :key="item.pid"
            :label="item.province"
            :value="item.province">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="城市">
        <el-select v-model="registForm.city" placeholder="请选择城市">
          <el-option
            v-for="item in cityList"
            :key="item.cid"
            :label="item.city"
            :value="item.city">
          </el-option>
        </el-select>
      </el-form-item>
      
      
      <el-form-item label="签名">
        <el-input type="textarea" v-model="registForm.userSign"></el-input>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="onSubmit('registForm')">注册</el-button>
      </el-form-item>
    </el-form>

  </div>
</template>

<script>
  import ajaxRequest from '@/axios/ajax'

  export default {
    name: 'Regist',
    data() {
      
      let valiPhone = (rule, value, callback) => {
        if (value !== '') { 
          var reg=/^1[3456789]\d{9}$/;
          if(!reg.test(value)){
            callback(new Error('手机格式不正确'));
          }
        }
        callback();
      }

      let valiEmail = (rule, value, callback) => {
        if (value !== '') { 
          var reg=/^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
          if(!reg.test(value)){
            callback(new Error('邮箱格式不正确'));
          }
        }
        callback();
      }

      return {
        labelPosition: 'right',
        provinceList: [],
        cityList: [],
        cityCacheList: [],
        provinceId: 0,
        registForm: {
          username: '',
          password: '',
          mobile: '',
          autoLogin: false,
          sex: '',
          province: '',
          city: '',
          email: '',
          userSign: '',
          userType: 1,
          registType: 0,
        },
        rules: {
          username: [
            { required: true, message: '请输入用户名称', trigger: 'blur' },
            { min: 2, max: 15, message: '长度在 2 到 15 个字符', trigger: 'blur' }
          ],
          password: [
            { required: true, message: '请输入用户密码', trigger: 'blur' },
            { min: 6, max: 15, message: '长度在 6 到 15 个字符', trigger: 'blur' }
          ],
          mobile: [{ validator: valiPhone, trigger: 'blur' }],
          email: [{ validator: valiEmail, trigger: 'blur' }],
        }
      }
    },
    props: ["drawer"],
    methods: {
      onSubmit(formName) {
        var vm = this;
        this.$refs[formName].validate((valid) => {
          if (valid) {
            //vm.$slideMsg.message('验证成功')
            //vm.$emit('update:successLogin', {userCode:'test', password:'123456'})

            ajaxRequest({
              url: '/user/regist',
              method: 'POST',
              data: vm.registForm
            }).then(response => {
              console.log(response)
              if (response.code == 1) {
                vm.$slideMsg.message(response.msg)

                if (vm.registForm.autoLogin) {
                  vm.$emit('successRegist', response.data, vm.registForm.password)
                } else {
                  vm.$emit('update:successCode', response.data)
                }
                
                // 清空表单
                vm.$refs[formName].resetFields();
                vm.$emit('update:drawer', false)
              } else {
                vm.$slideMsg.message(response.msg, 'error')
              }
            })
          } else {
            //console.log('error submit!!');
            //vm.slideMsg.message(res.msg, 'error')
            return false;
          }
        })
      },
      changePro () {
        // console.log('province!!', this.registForm.province);
        let selectPro = this.registForm.province
        this.registForm.city = ''

        if (selectPro != '') {
          let pro_id = 0
          let cList = []

          this.provinceList.forEach(province => {
            if (province.province === selectPro) {
              pro_id = province.pid
            }
          });

          this.cityCacheList.forEach(city => {
            if (city.pid === pro_id) {
              cList.push(city)
            }
          });

          this.cityList = cList
        }
      }
    },
    created () {
      let view = this

      ajaxRequest({
        url: '/user/getProCityList',
        method: 'POST',
        data: {}
      }).then(res => {
        if (res.code == 1) {
          let data = res.data
          view.provinceList = data.province
          view.cityCacheList = data.city
        } else {
          view.$slideMsg.message(res.msg, 'error')
        }
      })
    }, 
    mounted () {
      
    },
    computed: {
      
    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<!-- scoped作用是防止声明的CSS作用在外部元素中 -->
<style scoped>
  .regist-form {
    width: 300px;
    height: auto;
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    color: #2c3e50;
    margin: 80px 15px;
  }

  .el-switch {
    text-align: left;
  }
</style>