<template>
  <div id='writeMsg' ref='writeMsg' style="width:500px;height:120px;min-width:500px;min-height:120px;">

  </div>
</template>

<script>
  import E from 'wangeditor'

  export default {
    name: 'writeMsg',
    data () {
      return {
        editor: null,
        wInfo: '',
        maxLength: 512
      }
    },
    props: ['clear'],
    watch: {
      /* wInfo: function (newV, oldV) {
        if (newV.length > this.maxLength) {
          this.editor.txt.html(newV.slice(0, this.maxLength))
        }
      } */
    },
    mounted () {
      let view = this
      this.editor = new E(this.$refs.writeMsg)
      // 或者 const editor = new E( document.getElementById('div1') )
      this.editor.config.height = 70
      // 配置 onchange 回调函数
      this.editor.config.onchange = function (newHtml) {
        // console.log("change 之后最新的 html", newHtml);
        view.$emit('update:write-msg-content', newHtml)
        // view.wInfo = view.editor.txt.text()
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
      // 配置全屏功能按钮是否展示
      this.editor.config.showFullScreen = false
      
      // 配置菜单栏，删减菜单，调整顺序
      this.editor.config.menus = [
        'head',
        'bold',
        // 'fontSize',
        'fontName',
        'italic',
        // 'underline',
        // 'strikeThrough',
        // 'indent',
        // 'lineHeight',
        'foreColor',
        // 'backColor',
        'link',
        // 'list',
        // 'todo',
        'justify',
        // 'quote',
        'emoticon',
        'image',
        'video',
        // 'table',
        'code',
        // 'splitLine',
        // 'undo',
        // 'redo',
      ]

      this.editor.create()

      // 默认内容
      // this.editor.txt.append('<p>请在这里输入日记内容</p>')

      // 输入框ID
      this.$emit('update:write-msg-id', this.editor.textElemId)
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

  .input-w-message >>> .w-e-text-container {
    min-height: 70px;
  }

</style>