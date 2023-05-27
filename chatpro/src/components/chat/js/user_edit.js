import ajaxRequest from '@/axios/ajax'
import Cropper from 'cropperjs'
import 'cropperjs/dist/cropper.css'
import { updateUserInfo } from '@/axios/api'
import { localStore as ls} from "@/util/session";

export default {
  name: 'UserEdit',
  data() {
    return {
      labelPosition: 'right',
      provinceList: [],
      cityList: [],
      cityCacheList: [],
      provinceId: 0,
      dialogVisible: false,
      myCropper: null,
      imgFile: null,
      imgPreview: null,
      userProPic: null,
      cutWidth: 100,
      cutHeight: 100,
      afterImg: '',
      ScaleX: 1,
      ScaleY: 1,
      fixed: false,
      fixedBox: false,
      imgDisplay: false,
      cutFlag: false,
      newPwdVisible: false,
      fixedNumber: [0, 0],
      inputRotate: 0,
      fileList: [],
      cacheForm: null,
      cachePP: null,
      rePassword: '',
      editForm: {
        username: '',
        password: '',
        mobile: '',
        autoLogin: false,
        sex: null,
        age: null,
        province: '',
        city: '',
        email: null,
        userSign: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名称', trigger: 'blur' },
          { min: 2, max: 15, message: '长度在 2 到 15 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入原密码', trigger: 'blur' },
          { min: 6, max: 15, message: '长度在 6 到 15 个字符', trigger: 'blur' }
        ]
      },
      marks: { 100: '' },
      bgList: [{
        name: "碧水湖",
        value: "01.jpg"
      },
      {
        name: "远山近水",
        value: "02.jpg"
      },
      {
        name: "罗赖马山",
        value: "03.jpg"
      },
      {
        name: "蓝水母",
        value: "04.jpg"
      },
      {
        name: "红水母",
        value: "05.jpg"
      }],
      bgSelect: "01.jpg",
      localStora: new ls()
    }
  },
  props: ['bindFlag2UE', 'detail'],
  methods: {
    getBase64Image(e) {
      var t = document.createElement("canvas");
      t.width = e.width / 3,
      t.height = e.height / 3;
      t.getContext("2d").drawImage(e, 0, 0, e.width / 3, e.height / 3);
      e = e.src.substring(e.src.lastIndexOf(".") + 1).toLowerCase();
      return t.toDataURL("image/" + e)
    },
    changeBg(t) {
      let i = this;
      var e = "/static/img/bg/" + t;
      let r = new Image;
      r.crossOrigin = "Anonymous",
      r.src = e,
      r.onload = function() {
        var e = {
          base64: i.getBase64Image(r),
          value: t
        };
        try {
          i.localStora.setItem("bgImg", e),
          i.$slideMsg.info("缓存背景图片成功，刷新页面后更新背景图片")
        } catch(e) {
          console.log("缓存背景图片数据异常：", e),
          i.$slideMsg.warn("缓存背景图片失败")
        }
      }
    },
    checkUserDetail (editData) {
      // console.log('用户中心详细信息A--', JSON.stringify(this.detail[0]));
      // console.log('用户中心详细信息B--', JSON.stringify(editData));
      if (this.detail[0].userCode == editData.userCode) {
        this.detail[0].profilePicture = process.env.BASE_URL + editData.profilePicture
        this.$emit('update:detail', this.detail)
      }
    },
    submitUser(vm) {
      updateUserInfo(vm.editForm).then(response => {
        // console.log(response)
        if (response.code == 1) {
          vm.clearCut()
          vm.cacheForm = JSON.parse(JSON.stringify(vm.editForm))
          vm.$emit('update:drawer', false)
          // 更新用户缓存信息
          delete vm.editForm.password
          delete vm.editForm.newPassword
          vm.$store.commit('updateUserInfo', vm.editForm)

          // 判断用户详细信息
          vm.checkUserDetail(vm.editForm)
        } else {
          vm.$slideMsg.message(response.msg, 'error')
        }
      })
    },
    onSubmit(formName) {
      // 只有当参数值发生改变时才提交
      if (this.changeCheck == false) {
        // this.$slideMsg.warn('哈哈……没想到吧');
        this.$slideMsg.warn('无效的操作');
        return false
      }

      // 判断新密码
      if (this.newPwdVisible) {
        if (this.editForm.newPassword == '' || this.rePassword == '') {
          this.$slideMsg.warn('新密码或确认密码不能为空');
          return false
        } 
        if (this.editForm.newPassword !== this.rePassword) {
          this.$slideMsg.warn('新密码与确认密码不匹配');
          return false
        }
      }

      var vm = this;
      this.$refs[formName].validate((valid) => {
        if (valid) {
          //vm.$slideMsg.message('验证成功')
          //vm.$emit('update:successLogin', {userCode:'test', password:'123456'})
          if (vm.cutFlag) {
            // 如果裁剪图片，则上传
            vm.uploadImg(vm)
          } else {
            vm.submitUser(vm)
          }
        } else {
          //console.log('error submit!!');
          //vm.slideMsg.message(res.msg, 'error')
          return false;
        }
      })
    },
    // 上传图片
    uploadImg(vm) {
      //传递的参数是FormData对象
      // 创建form对象
      // let param = new FormData(); 
      // 将文件存入file下面(不能直接使用放在src里面的值当作file文件)
      // param.append("imgFile", this.imgPreview); 
      let view = vm
      let flag = false
      let image = view.$refs.image

      view.myCropper.getCroppedCanvas({
        width: view.cutWidth,
        height: view.cutHeight,
        minWidth: 80,
        minHeight: 80,
        maxWidth: 2048,
        maxHeight: 2048,
        fillColor: '#000',
        imageSmoothingEnabled: false,
        imageSmoothingQuality: 'low',
      }).toBlob((blob) => {
        let formData = new FormData();

        if (blob.size > (1024 * 512)) {
          let compressR = (512 * 1024 / blob.size).toFixed(2);
          //console.log(compressR);
          //大于0.5M的时候压缩
          formData = view.compress(view, image, parseFloat(compressR));
        } else {
          // Pass the image file name as the third parameter if necessary.
          formData.append('imgFile', blob, 'example.jpg');
        }
        formData.append('lastImg', view.cachePP)

        // Use `jQuery.ajax` method for example
        flag = view.$ajaxRequest({
          method: "post",
          url: '/chat/img',
          data: formData,
          headers: {
            "Content-Type": "multipart/form-data",   //并且header中的Content-type必须是multipart/form-data类型
          },
        }).then(function (res) {
          // console.log('图片上传结果--', JSON.stringify(res));
          if (res.code === 1) {
            view.editForm.profilePicture = res.data
            view.cachePP = res.data
          }

          view.submitUser(view)
        })

      }, 'image/jpg');

      return flag
    },
    //压缩图片
    compress(view, image, compressR) {
      var canvas = document.createElement('canvas');
      //height、 width 和图片实际的高、宽一致时，直接赋值canvas的宽高为上述宽高
      canvas.width = view.cutWidth;
      canvas.height = view.cutHeight;
      var ctx = canvas.getContext('2d');
      ctx.fillStyle = '#FFF';//绘制背景色
      ctx.fillRect(0, 0, canvas.width, canvas.height);
      ctx.drawImage(image, 0, 0, canvas.width, canvas.height);
      //0.5为压缩的质量比例，范围是0~1（越小压缩越小）。
      var imgBase = canvas.toDataURL("image/jpeg",
        compressR > 0.5 ? compressR : 0.5);
      //转成Blob对象，以ajax的方式上传
      var formData = new FormData();
      var arr = imgBase.split(",");
      var mime = arr[0].match(/:(.*?);/)[1];
      var bstr = atob(arr[1]);
      var n = bstr.length;
      var u8arr = new Uint8Array(n);
      while (n--) {
        u8arr[n] = bstr.charCodeAt(n);
      }
      var obj = new Blob([u8arr], {
        type: mime
      });
      formData.append("imgFile", obj, 'example.jpg');
      return formData;
      //这里是用了toDataURL,然后再转成了blob，直接用canvas.toBlob不知道好不好使。
    },
    changePro() {
      // console.log('province!!', this.editForm.province);
      let selectPro = this.editForm.province
      this.editForm.city = ''

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
    },
    handleChangeRotate(value) {
      this.cropperrotateTo(value)
    },
    init() {
      /* this.myCropper = new Cropper(this.$refs.image, {
        viewMode: 1,
        dragMode: 'move',
        // initialAspectRatio: 1,
        preview: '.before',
        background: false,
        autoCropArea: 0.6,
        zoomOnWheel: true,
        aspectRatio: false
        // movable :true,
        // rotatable :true
      }) */

      let view = this
      // 操作裁剪框结束时预览
      this.$refs.image.addEventListener('cropend', (event) => {
        // const canvasData = cropper.getCanvasData();
        // console.log(canvasData.width, '/',canvasData.height);
        // 预览图片
        view.previewImage()
      });
      let cropper = new Cropper(this.$refs.image, {
        viewMode: 1,
        autoCrop: true,
        aspectRatio: 1,
        minCropBoxWidth: 80,
        minCropBoxHeight: 80,
        ready: () => {
          if (this.croppedData) {
            this.myCropper.crop()
              .setData(this.croppedData)
              .setCanvasData(this.canvasData)
              .setCropBoxData(this.cropBoxData);
          }
        },

        crop: ({ detail }) => {
          if (detail.width > 0 && detail.height > 0) {
            view.cutWidth = Number(detail.width).toFixed(0)
            view.cutHeight = Number(detail.height).toFixed(0)

            // 预览图片
            // view.previewImage()
          }
        },
        /* crop(event) {
          // console.log('width--', event.detail.width);
          // console.log('height--', event.detail.height);
          view.cutWidth = Number(event.detail.width).toFixed(0)
          view.cutHeight = Number(event.detail.height).toFixed(0)
        } */
      })
      // console.log("开始裁剪", cropper);
      this.myCropper = cropper
    },
    // 预览头像(低质量)
    previewImage() {
      this.imgPreview = this.myCropper
        .getCroppedCanvas({
          imageSmoothingQuality: 'low'
        })
        .toDataURL('image/jpg')
    },
    // 裁剪
    crop() {
      const { myCropper } = this
      const view = this
      this.imgFile = myCropper.getCroppedCanvas({
        width: this.cutWidth,
        height: this.cutHeight,
        imageSmoothingQuality: 'high'
      }).toDataURL('image/jpg')
      myCropper.stop();
      myCropper.destroy()
      this.$refs.crop.$el.blur()

      setTimeout(() => {
        view.init()
      }, 100);
    },
    // 缩放
    cropperzoom(val) {
      if (this.myCropper != null) {
        this.myCropper.zoom(val)
      }
    },
    // 充值
    cropperReset() {
      if (this.myCropper != null) {
        this.myCropper.reset()
      }
    },
    // 移动
    croppermove(val1, val2) {
      if (this.myCropper != null) {
        this.myCropper.move(val1, val2)
      }
    },
    // 旋转
    cropperRotate(val) {
      if (this.myCropper != null) {
        this.myCropper.rotate(val)
      }
    },
    //绝对角度旋转
    cropperrotateTo(val) {
      if (this.myCropper != null) {
        this.myCropper.rotateTo(val)
      }
    },
    // X轴翻转
    cropperScaleX() {
      if (this.myCropper != null) {
        this.ScaleX = -this.ScaleX
        this.myCropper.scale(this.ScaleX, 1)
      }
    },
    // y轴翻转
    cropperScaleY() {
      if (this.myCropper != null) {
        this.ScaleY = -this.ScaleY
        this.myCropper.scale(1, this.ScaleY)
      }
    },
    aspectRatioSet(x, y) {
      if (this.myCropper != null) {
        this.myCropper.setAspectRatio(x / y)
      }
    },
    fileChange(file) {
      //console.log(file);
      this.imgFile = file.url
      this.imgDisplay = true

      let view = this
      setTimeout(() => {
        view.init()
      }, 500);
    },
    clearCut() {
      if (this.myCropper != null) {
        this.$refs['imgUpload'].clearFiles();
        this.myCropper.destroy()
        // this.myCropper.clear();
      }
      this.imgFile = null
      this.imgDisplay = false
      this.cutFlag = false
    },
    cutOver() {
      this.dialogVisible = false
      if (this.myCropper != null) {
        this.userProPic = this.imgPreview
        this.cutFlag = true
        // 最终裁剪区域(为显示图片裁剪框区域，而不是预览区域)
        /* this.imgFile = this.myCropper.getCroppedCanvas({
          width: this.cutWidth,
          height: this.cutHeight,
          imageSmoothingQuality: 'high'
        }).toDataURL('image/jpg')
        this.myCropper.stop(); */

        // 标识图片修改
        this.cacheForm.profilePicture = ''
      }

    },
    cancel() {
      this.$refs['editForm'].resetFields();
      // Object.assign(this.$data.editForm, this.$options.data.call(this).editForm);
      this.dialogVisible = false
    }
  },
  created() {
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
        if (data.city != undefined) {
          view.cityList = data.city
        }

        let user = view.$store.getters.getUserInfo
        user.newPassword = ''
        view.editForm = user
        view.cacheForm = JSON.parse(JSON.stringify(user))
        if (user.profilePicture != undefined) {
          view.userProPic = process.env.BASE_URL + user.profilePicture
          view.cachePP = user.profilePicture
        }
        if (user.sex != undefined) {
          view.$set(view.editForm, 'sex', '' + user.sex)
        }
      } else {
        view.$slideMsg.message(res.msg, 'error')
      }
    })
  },
  mounted() {
    
  },
  computed: {
    // 判断修改
    changeCheck: function () {
      // console.log('propsA--', JSON.stringify(this.editForm))
      // console.log('propsB--', JSON.stringify(this.cacheForm))

      if (this.cacheForm != null) {
        for (const key in this.editForm) {
          if (Object.hasOwnProperty.call(this.editForm, key)) {
            // const element = object[key];
            if (this.editForm[key] !== this.cacheForm[key]) {
              return true
            }
          }
        }
      }
      return false
    },
    getAge() {
      if (this.editForm.age != undefined) {
        return this.editForm.age + ''
      }
      return ''
    }
  },
  watch: {
    /* bindFlag2UE: function (newV, oldV) {
      console.log('unbind--', newV);
      if (newV != undefined && newV != null) {
        this.bindFlag = newV
      }
    }  */
  }
}