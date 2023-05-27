<template>
  <div id="Regist" class="regist-form">

    <el-form class="el-from-regist" ref="editForm" :label-position="labelPosition" :model="editForm" :rules="rules"
      label-width="80px">
      <el-form-item label="编号">
        <span>{{editForm.userCode}}</span>
      </el-form-item>
      <el-form-item label="用户名" prop="username">
        <el-input v-model="editForm.username"></el-input>
      </el-form-item>
      <el-form-item label="原密码" prop="password">
        <el-input type="password" v-model="editForm.password"></el-input>
        <el-button class="update-pwd" type="text" @click="newPwdVisible = !newPwdVisible">修改</el-button>
      </el-form-item>
      <div v-show="newPwdVisible">
        <el-form-item label="新密码" prop="newPassword">
          <el-input type="password" v-model="editForm.newPassword"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" >
          <el-input type="password" v-model="rePassword"></el-input>
        </el-form-item>
      </div>
      <el-form-item label="手机" prop="mobile">
        <el-input v-model="editForm.mobile"></el-input>
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-if="bindFlag2UE==0" v-model="editForm.email"></el-input>
        <span v-else >{{editForm.email}}</span>
      </el-form-item>
      <el-form-item label="头像">
        <el-avatar :size="45" src="#">
          <img ref="userProPic" :src="userProPic" />
        </el-avatar>
        <el-button class="edit-img" type="primary" size="mini" round @click="dialogVisible = true">修改</el-button>
      </el-form-item>
      <el-form-item label="性别">
        <el-radio-group v-model="editForm.sex">
          <el-radio label="1">男</el-radio>
          <el-radio label="0">女</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item class="age" label="年龄">
        <el-slider v-model="editForm.age" :min="1" :max="100" :marks="{100: getAge}"></el-slider>
      </el-form-item>
      <el-form-item label="省份">
        <el-select v-model="editForm.province" placeholder="请选择省份" @change="changePro">
          <el-option v-for="item in provinceList" :key="item.pid" :label="item.province" :value="item.province">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="城市">
        <el-select v-model="editForm.city" placeholder="请选择城市">
          <el-option v-for="item in cityList" :key="item.cid" :label="item.city" :value="item.city">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="背景图片">
        <el-select v-model="bgSelect" placeholder="请选择图片" @change="changeBg">
          <el-option v-for="item in bgList" :key="item.value" :label="item.name" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>


      <el-form-item label="签名">
        <el-input type="textarea" v-model="editForm.userSign"></el-input>
      </el-form-item>

      <el-form-item>
        <el-button @click="$emit('update:drawer', false)">取消</el-button>
        <el-button type="primary" :disabled="(changeCheck?false:true)" @click="onSubmit('editForm')">保存</el-button>
      </el-form-item>
    </el-form>

    <el-dialog title="头像裁剪" :visible.sync="dialogVisible" :append-to-body="true" width="850px;">
      <!-- :before-close="handleClose" -->
      <span class="img-cut">
        <div class="container">
          <div class="img-container">
            <img id="image" width="100%" height="100%" ref="image" :src="imgFile" :style="{display: (imgDisplay?'block':'none')}" alt="">
            <el-upload action="#" 
            ref="imgUpload"
            :style="{display: (imgDisplay?'none':'block')}"
            list-type="picture-card" 
            :show-file-list="false" 
            :auto-upload="false" 
            :on-change="fileChange">
              <i slot="default" style="width: 100%;height: 100%;" class="el-icon-plus"></i>
            </el-upload>
          </div>
          <!-- <div class="before" />
          <div class="afterCropper">
            <img :src="afterImg" alt="">
          </div> -->
        </div>

        <div class="img-preview">
          <el-avatar :size="120" src="#" >
            <img :src="imgPreview"/>
          </el-avatar>
          <div class="cut-wh">宽度：{{cutWidth}},高度：{{cutHeight}}</div>
        </div>

        <el-button style="margin: 30px auto;" type="error" @click="clearCut">清除</el-button>
        <el-button ref="crop" style="margin: 30px auto;" type="error" @click.prevent="crop()">裁剪</el-button>
        <!-- <el-button style="margin: 30px auto;" type="error" @click="sureSava">裁剪</el-button> -->
        <el-button type="primary" @click="cropperzoom(0.1)"><i class="el-icon-plus" /></el-button>
        <el-button type="primary" @click="cropperzoom(-0.1)"><i class="el-icon-minus" /></el-button>
        <el-button type="primary" @click="cropperRotate(-90)"><i class="el-icon-refresh-left" /></el-button>
        <el-button type="primary" @click="cropperRotate(90)"><i class="el-icon-refresh-right" /></el-button>
        <el-button type="primary" @click="cropperScaleX()">Y翻转</el-button>
        <el-button type="primary" @click="cropperScaleY()">X翻转</el-button>
        旋转度数：
        <el-input-number v-model="inputRotate" controls-position="right" :min="0" :max="360"
          @change="handleChangeRotate" />
        <span style="position: relative;left: 50px;">
          <el-button type="primary" @click="aspectRatioSet(1,1)">1:1</el-button>
          <el-button type="primary" @click="aspectRatioSet(16,9)">16:9</el-button>
          <el-button type="primary" @click="aspectRatioSet(0,0)">自由裁剪</el-button>
        </span>        
      </span>
      
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancel">取 消</el-button>
        <el-button type="primary" @click="cutOver">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
  import UserEdit from './js/user_edit.js'

  // 外部JS当作内部使用(避免内容过于臃肿)
  export default UserEdit
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

  .edit-img {
    position: relative;
    left: 110px;
    bottom: 18px;
  }



  .img-cut {
    width: 100%;
    height: 550px;
  }

  .container {
    margin-bottom: 20px;
    display: flex;
  }

  .before {
    width: 100px;
    height: 100px;
    overflow: hidden;
    /* 这个属性可以得到想要的效果 */
  }

  .img-container {
    width: 600px;
    height: 400px;
    overflow: hidden;
  }

  .img-container >>> .el-upload__input {
    display: none;
  }

  .afterCropper {
    flex: 1;
    margin-left: 20px;
    border: 1px solid salmon;
    text-align: center;
  }

  .afterCropper img {
    width: 150px;
    margin-top: 30px;
  }

  ::v-deep.el-input-number {
    position: relative;
    display: inline-block;
    width: 92px;
    line-height: 38px;
  }

  .img-preview {
    position: absolute;
    right: 220px;
    top: 150px;
    float: left;
    width: 180px;
    height: 180px;
    text-align: center;
    margin: 30px auto;
  }  

  .age >>> .el-slider {
    width: 200px;
    margin-right: 10px;
  }
  .age >>> .el-slider__marks-text {
    position: absolute;
    top: -32px;
    left: 220px !important;
  }

  .update-pwd {
    width: 35px;
    position: absolute;
    float: left;
  }
</style>