<template>
  <div id='wangEditor' ref='wangEditor'>

  </div>
</template>

<script>
  import E from 'wangeditor'

  export default {
    name: 'wangEditor',
    data () {
      return {
        editor: null
      }
    },
    props: ['clear'],
    mounted () {
      let view = this
      this.editor = new E(this.$refs.wangEditor)
      // 或者 const editor = new E( document.getElementById('div1') )
      this.editor.config.height = 350
      // 配置 onchange 回调函数
      this.editor.config.onchange = function (newHtml) {
        // console.log("change 之后最新的 html", newHtml);
        view.$emit('update:write-content', newHtml)
      };

      // 图片上传
      // 文件大小2M
      this.editor.config.uploadImgMaxSize = 2 * 1024 * 1024 
      // 类型
      this.editor.config.uploadImgAccept = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp']
      // 一次最多上传 5 张图片
      this.editor.config.uploadImgMaxLength = 5
      // 后台服务接收文件名
      this.editor.config.uploadFileName = 'imgFiles'
      // 自定义上传
      this.editor.config.customUploadImg = function (resultFiles, insertImgFn) {
        // resultFiles 是 input 中选中的文件列表
        // insertImgFn 是获取图片 url 后，插入到编辑器的方法

        // 上传图片，返回结果，将图片插入到编辑器中
        let img_path = view.uploadImage(resultFiles, insertImgFn)
        // console.log('图片上传--', img_path);
      }

      this.editor.create()

      // 默认内容
      this.editor.txt.append('<p>请在这里输入日记内容</p>')
    },
    watch: {
      clear: function (newV, oldV) {
        if (newV == true) {
          this.editor.txt.html('')
        }
      }
    },
    methods: {
      uploadImage (fileList, insertImgFn) {
        let view = this
        var formData = new FormData();
        //循环添加到formData中
        fileList.forEach(function (file) {
          formData.append("imgFiles", file, file.name);
        })

        let img_path = this.$ajaxRequest({
          method: "post",
          url: '/chat/moreImgCache',
          data: formData,
          headers: {
            "Content-Type": "multipart/form-data",   //并且header中的Content-type必须是multipart/form-data类型
          },
        }).then(function (res) {
          // console.log('多图片发送结果--', JSON.stringify(res));
          if (res.code === 1) {
            let imgPaths = res.data;
            
            if (imgPaths.length > 0) {
              imgPaths.forEach(path => {
                let img_path_0 = process.env.BASE_URL + path.replace("\\", "/");
                insertImgFn(img_path_0)
              });
              return imgPaths
            }
          }
          return []
        })
        return img_path
      }
    }
  }
</script>

<style scoped>

</style>