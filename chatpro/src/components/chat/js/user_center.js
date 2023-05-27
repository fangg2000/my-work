import ajaxRequest from '@/axios/ajax'
import UserConfigView from '@/components/chat/UserConfig'
import UesrUpdateView from '@/components/chat/userEdit'
import record_mixin from '@/components/chat/js/mixin/record_mixin.js'

/* var test_data = [
  { username: '张三', userSign: 'Your application is running here: http://localhost:8080' },
  { username: '青蛙王子', userSign: '没有最好，只有更好,没有最好，只有更好,没有最好，只有更好,没有最好，只有更好,没有最好，只有更好' },
  { username: '王五六', userSign: 'Your application is ' },
  { username: '杨过', userSign: 'Your application is' },
  { username: '阿牛哥哥', userSign: 'Your application is run' }
] */

export default {
  name: 'userCenter',
  mixins: [record_mixin],
  components: {
    UserConfigView,
    UesrUpdateView
  },
  data() {
    return {
      userInfo: null,
      userProPic: '',
      otherUsername: '',
      searchText: '',
      selectUser: null,
      selectIndex: null,
      isLike: false,
      isActive: true,
      likeColor: '#bcddff',
      heartClass: 'imgGray heart',
      selectColor: '10px solid transparent',
      userList: [],
      focusUserList: [],
      userFocusList: [],
      listType: null,
      activeNames: ['1'],
      province: '',
      city: '',
      sex: null,
      startAge: null,
      endAge: null,
      provinceList: [],
      cityList: [],
      cityCacheList: [],
      isCollapse: false,
      focusType: 'info',
      userDetail: [{
        likeNum: 0,
        likeFlag: false,
        fansNum: 0,
        focusFlag: false
      }],
      configDrawer: false,
      bindFlag: null,
      direction: 'ltr',
      postParams: null,
      finalSearchParams: null,
      updateUserDrawer: false,  // 修改用户信息右边抽屉
      signCache: null,
      signEmpty: '没有签名',
      pageNum: 1,
      pageSize: 10,
      total: 0
    }
  },
  filters: {
    showTextByLength (str) {
      return str!=undefined&&str.length>28?(str.substring(0,28)+'...'):str
    }
  },
  methods: {
    clearData() {
      this.searchText = ''
      this.$refs.addCondition.style.display = 'none'
      this.$refs.addCondition.innerHTML = ''
      // 调用此方法清空之前选项
      this.selectMenu()
    },
    moreConClick() {
      let view = this
      // 这里可以让弹出框位置下移，但不知道什么时候出BUG显示不出
      /* setTimeout(function (params) {
        // console.log(view.$refs.addCondition.style)
        view.$refs.conditions.$el.style.cssText = 'top: 76px; position: fixed; left: 1350px; transform-origin: center top; z-index: 167;'
        view.$refs.conditions.$el.style.display = 'inline-block'
      }, 100) */
    },
    loadMoreInfo(page_num) {
      this.searchUser(page_num)
    },
    searchUser(page_num) {
      // this.$slideMsg.info('寻找--'+this.searchText)
      let json = null

      // 重置部分数据
      this.selectUser = null
      this.selectIndex = null
      Object.assign(this.$data.userDetail, this.$options.data.call(this).userDetail);
      this.heartClass = this.userDetail[0].likeFlag ? 'heart-red' : 'imgGray heart'
      this.otherUsername = ''
      // 重置所有data数据
      // Object.assign(this.$data, this.$options.data.call(this));
      this.pageNum = page_num
      this.activeNames = '2'

      let params = {
        province: this.province,
        city: this.city,
        startAge: this.startAge,
        endAge: this.endAge,
        sex: this.sex,
        content: this.searchText.trim(),
        pageNum: this.pageNum,
        pageSize: this.pageSize
      }
      let view = this

      // 判断重复查询
      this.postParams = params
      if (this.changeCheck == false) {
        // this.$slideMsg.warn('无效的操作');
        return false
      }

      // console.log('15-25'.match(/(^[0-9]{1,3}\-[0-9]{1,3}$)/g))

      ajaxRequest({
        url: '/user/getUserListByPage',
        method: 'POST',
        data: params
      }).then(res => {
        // console.log(res.data)
        if (res.code == 1) {
          let data = res.data
          if (view.pageNum > 1) {
            view.userList = view.userList.concat(data.rows)
          } else {
            view.userList = data.rows
          }
          view.total = data.total
          view.clearBColor(view)

          // 查询成功缓存最后查询条件
          view.finalSearchParams = params
        } else {
          view.$slideMsg.message(res.msg, 'error')
        }
      })
    },
    clearBColor (view) {
      // 延时半秒，等待DOM刷新完成
      setTimeout(function (params) {
        if (view.userList.length > 0) {
          view.userList.forEach((user, i) => {
            view.$refs['user_' + i][0].style.borderRight = '10px solid transparent'
            view.$refs['user_' + i][0].style.backgroundColor = '#fff'
          });
        }
        if (view.focusUserList.length > 0) {
          view.focusUserList.forEach((user, i) => {
            view.$refs['focus_user_' + i][0].style.borderRight = '10px solid transparent'
            view.$refs['focus_user_' + i][0].style.backgroundColor = '#fff'
          });
        }
      }, 500)
    },
    selectMenu(index, path) {
      // this.$slideMsg.info(index+'/'+path)
      
      try {
        let conditionText = this.$refs.addCondition.innerHTML
        if (conditionText != '') {
          json = JSON.parse(conditionText)

          // {"性别":"男","年龄":"25-35","城市":"广西","省份":"北京市"}
          if (json.hasOwnProperty('性别') && (json['性别'] === '女' || json['性别'] === '男')) {
            this.sex = (json['性别'] === '女') ? 0 : 1
          } else {
            this.sex = null
          }
          if (json.hasOwnProperty('年龄') && json['年龄'].match(/(^[0-9]{1,3}\-[0-9]{1,3}$)/g)) {
            let temp = json['年龄'].split('\-')
            this.startAge = parseInt(temp[0], 10)
            this.endAge = parseInt(temp[1], 10)
          } else {
            this.startAge = null
            this.endAge = null
          }
          if (json.hasOwnProperty('省份') && json['省份'] != '') {
            this.province = json['省份']
          } else {
            this.province = null
          }
          if (json.hasOwnProperty('城市') && json['城市'] != '') {
            this.city = json['城市']
          } else {
            this.city = null
          }
        } else {
          this.sex = null
          this.startAge = null
          this.endAge = null
          this.province = null
          this.city = null
        }
      } catch (error) {

      }
    },
    // 选中列表用户
    changeBorCol(index, listType) {
      // console.log(this.$refs['user_'+index][0].style.backgroundColor)
      // this.selectColor = '10px solid red'
      let view = this
      this.selectUser = null
      this.selectIndex = null
      this.isActive = false
      // 用户收藏标识
      this.collectFlag = false
      this.userDetail[0].likeFlag = false
      this.listType = listType
      let forList = listType=='0'?this.focusUserList:this.userList
      let ref_prefix = listType=='0'?'focus_user_':'user_'

      forList.forEach((user, i) => {
        if (i == index) {
          this.selectUser = user
          this.selectIndex = index
          view.$refs[ref_prefix + index][0].style.borderRight = '10px solid #00FFFF'
          view.$refs[ref_prefix + index][0].style.backgroundColor = '#F2F6FC'
        } else {
          view.$refs[ref_prefix + i][0].style.borderRight = '10px solid transparent'
          view.$refs[ref_prefix + i][0].style.backgroundColor = '#fff'
        }
      });

      if (listType == '0') {
        this.userList.forEach((user, i) => {
          view.$refs['user_' + i][0].style.borderRight = '10px solid transparent'
          view.$refs['user_' + i][0].style.backgroundColor = '#fff'
        });
      } else {
        this.focusUserList.forEach((user, i) => {
          view.$refs['focus_user_' + i][0].style.borderRight = '10px solid transparent'
          view.$refs['focus_user_' + i][0].style.backgroundColor = '#fff'
        });
      }

      // 点击用户列表取得用户对象信息
      this.otherUsername = this.selectUser.username
      // 是否允许陌生人联系
      this.userDetail[0].strangerContact = this.selectUser.strangerContact

      // 查看用户信息明细
      let params = {
        userId: this.selectUser.userId,
        userCode: this.selectUser.userCode,
        username: this.selectUser.username
      }
      ajaxRequest({
        url: '/user/getUserDetailInfo',
        method: 'POST',
        data: params
      }).then(res => {
        // console.log(res.data)
        if (res.code == 1) {
          view.userDetail[0] = res.data
          // 如果当前操作用户已经点过赞
          view.heartClass = view.userDetail[0].likeFlag ? 'heart-red' : 'imgGray heart'
          // 关注过用户
          view.focusType = view.userDetail[0].focusFlag?'warning':'info'
          console.log('pp--', view.selectUser.profilePicture);
          if (view.selectUser.profilePicture != undefined && view.selectUser.profilePicture != '') {
            view.userDetail[0].profilePicture = process.env.BASE_URL + view.selectUser.profilePicture
          }

          // 查询用户日记
          view.getDRListByMonth(view.selectUser.userCode)

          // 用户关注列表(需要VIP权限)
          view.userFocusList = res.data.userFocusList
        } else {
          view.$slideMsg.message(res.msg, 'error')
        }
      })
    },
    changePro(pro_id, pro_name) {
      // console.log('province!!', this.registForm.province);
      // let selectPro = this.province
      this.city = ''
      let cList = []
      this.province = pro_name
      this.isCollapse = true
      //console.log(this.$refs['proMenu'])

      /* this.provinceList.forEach(province => {
        if (province.province === selectPro) {
          pro_id = province.pid
        }
      }); */

      this.cityCacheList.forEach(city => {
        if (city.pid === pro_id) {
          cList.push(city)
        }
      });

      this.cityList = cList
      this.add2Search('省份', pro_name)
    },
    changeCity(cid, city_name) {
      this.city = city_name
      this.add2Search('城市', city_name)
    },
    changeSex(index, sex_name) {
      this.sex = index
      this.add2Search('性别', sex_name)
    },
    changeAge(start_age, end_age) {
      this.startAge = start_age
      this.endAge = end_age
      this.add2Search('年龄', start_age + '-' + end_age)
    },
    openMenu(index) {
      //this.$slideMsg.info('展开菜单--'+index)
      this.isCollapse = false
    },
    collapseMenu() {
      this.$slideMsg.info('error')
    },
    add2Search(key, value) {
      this.$refs.addCondition.style.display = 'block'
      try {
        let innerHtml = this.$refs.addCondition.innerHTML
        if (innerHtml != '') {
          let json = JSON.parse(innerHtml)
          json[key] = value
          //this.searchText = JSON.stringify(json)
          this.$refs.addCondition.innerHTML = JSON.stringify(json)
        } else {
          let json = {}
          json[key] = value
          //this.searchText = JSON.stringify(json)
          this.$refs.addCondition.innerHTML = JSON.stringify(json)
        }
      } catch (error) {
        this.$slideMsg.warn('输入格式有误')
      }
    },
    giveALike() {
      if (this.selectUser != null 
        && this.selectUser.userCode != '' 
        && this.selectUser.userCode != this.userInfo.userCode) {
        let detail = {}
        detail.focusFlag = this.userDetail[0].focusFlag
        detail.fansNum = this.userDetail[0].fansNum
        detail.byFocusFlag = this.userDetail[0].byFocusFlag
        detail.strangerContact = this.selectUser.strangerContact
        detail.likeFlag = this.userDetail[0].likeFlag ? false : true
        detail.likeNum = detail.likeFlag ? (this.userDetail[0].likeNum + 1) : (this.userDetail[0].likeNum - 1)

        // console.log(this.$refs.listUserLikeNum)
        // this.$refs.listUserLikeNum[this.selectIndex].innerHTML = detail.likeNum
        let forList = this.listType=='0'?this.focusUserList:this.userList
        forList[this.selectIndex].likeNum = detail.likeNum
        // forList[this.selectIndex].likeFlag = detail.likeFlag

        let view = this
        this.heartClass = detail.likeFlag ? 'heart-red' : 'imgGray heart'

        view.userDetail[0] = detail

        ajaxRequest({
          url: '/user/patchGiveALike',
          method: 'POST',
          data: { userCode: this.selectUser.userCode, likeNum: (this.userDetail[0].likeFlag ? 1 : 0) }
        }).then(res => {
          if (res.code != 1) {
            detail.likeFlag = detail.likeFlag ? false : true
            detail.likeNum = detail.likeFlag ? (detail.likeNum + 1) : (detail.likeNum - 1)
            view.$refs.listUserLikeNum[view.selectIndex].innerHTML = detail.likeNum
            forList[view.selectIndex].likeNum = detail.likeNum
            view.heartClass = detail.likeFlag ? 'heart-red' : 'imgGray heart'
            view.$slideMsg.message(res.msg, 'error')
            view.userDetail[0] = detail
          }

          //console.log('之后', JSON.stringify(detail))
        })
      }
    },
    focusUserClick() {
      // console.log('之前--', JSON.stringify(this.selectUser))
      if (this.selectUser != null 
        && this.selectUser.userCode != '' 
        && this.selectUser.userCode != this.userInfo.userCode) {
        let detail = {}
        let view = this

        detail.likeFlag = this.userDetail[0].likeFlag
        detail.likeNum = this.userDetail[0].likeNum
        detail.byFocusFlag = this.userDetail[0].byFocusFlag
        detail.strangerContact = this.selectUser.strangerContact
        detail.focusFlag = this.userDetail[0].focusFlag ? false : true
        detail.fansNum = detail.focusFlag ? (this.userDetail[0].fansNum + 1) : (this.userDetail[0].fansNum - 1)
        this.focusType = detail.focusFlag?'warning':'info'

        // this.$refs.listUserFansNum[this.selectIndex].innerHTML = detail.fansNum
        let forList = this.listType=='0'?this.focusUserList:this.userList
        forList[this.selectIndex].fansNum = detail.fansNum
        // forList[this.selectIndex].focusFlag = detail.focusFlag

        this.userDetail[0] = detail

        ajaxRequest({
          url: '/user/patchFocusUser',
          method: 'POST',
          data: { userCode: this.selectUser.userCode, username: this.selectUser.username, focusFlag: detail.focusFlag}
        }).then(res => {
          if (res.code != 1) {
            // 操作回退显示
            detail.focusFlag = detail.focusFlag ? false : true
            detail.fansNum = detail.focusFlag ? (detail.fansNum + 1) : (detail.fansNum - 1)
            view.$refs.listUserFansNum[view.selectIndex].innerHTML = detail.fansNum
            forList[view.selectIndex].fansNum = detail.fansNum
            view.focusType = detail.focusFlag?'warning':'info'
            view.$slideMsg.message(res.msg, 'error')
            view.userDetail[0] = detail
          } else {        
            view.afterFocus(view, detail)
          }

          // console.log('之后--', JSON.stringify(view.selectUser))
        })
      }
    }, 
    afterFocus (view, detail) {
      let haveFlag = false
      let user_code = view.selectUser.userCode
      let index = 0
      view.focusUserList.forEach((user, i) => {
        if (user_code == user.userCode) {
          haveFlag = true
          index = i
        }
      })

      if (detail.focusFlag) {
        if (haveFlag == false) {
          view.focusUserList = view.focusUserList.concat(view.selectUser)
        }
      } else {
        // 存在则从列表中删除
        if (haveFlag) {
          view.focusUserList.splice(index, 1)
        }
      }

      // 把用户信息传到主页面(如果是关注用户则添加到关注用户列表，反之删除)
      let user_params = {
        username: view.selectUser.username,
        userCode: view.selectUser.userCode,
        profilePicture: view.selectUser.profilePicture==undefined?'':view.selectUser.profilePicture,
        userType: view.selectUser.userType==undefined?null:view.selectUser.userType,
        userSign: view.selectUser.userSign==undefined?'':view.selectUser.userSign,
        onlineStatus: view.selectUser.onlineStatus==undefined?0:view.selectUser.onlineStatus,
        focusFlag: detail.focusFlag,
        byFocusFlag: detail.byFocusFlag,
        strangerContact: detail.strangerContact
      }
      console.log('关注参数--', JSON.stringify(user_params))
      view.$emit('afterFocusUser', user_params)
    },
    contactUser () {
      if (this.selectUser != null 
        && this.selectUser.userCode != undefined 
        && this.selectUser.userCode != this.userInfo.userCode) {
        // 对方禁止陌生人联系并且对方没有关注你时，你不能联系对方
        if (this.selectUser.strangerContact == 1 && this.userDetail[0].byFocusFlag == false) {
          this.$slideMsg.warn('对方不想和陌生人联系，换个好友吧')
          return false;
        }

        // 把用户信息传到主页面
        var user_params = {
          username: this.selectUser.username,
          userCode: this.selectUser.userCode,
          profilePicture: this.selectUser.profilePicture==undefined?null:this.selectUser.profilePicture,
          userType: this.selectUser.userType==undefined?null:this.selectUser.userType,
          focusFlag: this.userDetail[0].focusFlag
        }
        console.log('联系参数--', JSON.stringify(user_params))
        this.$emit('contactUser', user_params)
        this.$emit('update:drawer', false)
      }
    },
    signEdit (event) {
      let sign = this.$refs.sign
      // console.log(event);
      
      let oldSign = this.replaceAll(sign.innerHTML.trim(), '(‘|’)', '')
      if (oldSign == this.signEmpty) {
        sign.innerHTML = ''
      } else {
        sign.innerHTML = oldSign
      }

      this.signCache = oldSign
    }, 
    signBlur () {
      let sign = this.$refs.sign
      let newSign = this.replaceAll(sign.innerHTML.trim(), '(‘|’)', '')
      sign.innerHTML = newSign

      if (sign.innerHTML == '') {
        sign.innerHTML = this.signEmpty
      } else {
        sign.innerHTML = '‘‘'+sign.innerHTML+'’’'
      }
      // console.log(this.signCache);

      if (this.signCache != newSign) {
        this.$ajaxRequest({
          url: '/user/patchUserSign',
          method: 'POST',
          data: {
            userId: this.userInfo.userId,
            userSign: sign.innerHTML==this.signEmpty?'':newSign
          }
        }).then(res => {
          // console.log(res.data)
          if (res.code == 1) {
            let user = this.$store.getters.getUserInfo
            user.userSign = newSign
            this.$store.commit('updateUserInfo', user)
          } else {
            this.$slideMsg.message(res.msg, 'error')
          }
        })
      }
    }
  },
  created() {
    this.userInfo = this.$store.getters.getUserInfo
    this.userProPic = this.userInfo.profilePicture != undefined && this.userInfo.profilePicture != ''
      ? (process.env.BASE_URL + this.userInfo.profilePicture) : ''
    this.otherUsername = this.userInfo.username

  },
  mounted () {
    let view = this
    
    // 查看用户信息明细
    let params = {
      userId: this.userInfo.userId,
      userCode: this.userInfo.userCode,
      username: this.userInfo.username
    }
    ajaxRequest({
      url: '/user/getUserDetailInfo',
      method: 'POST',
      data: params
    }).then(res => {
      // console.log(res.data)
      if (res.code == 1) {
        view.userDetail[0] = res.data
        // 如果当前操作用户已经点过赞
        view.heartClass = 'heart-red'
        // view.userDetail[0].likeNum = res.data.likeNum
        view.userDetail[0].profilePicture = view.getUserProPic
        // 关注过用户
        view.focusType = 'warning'
        // 第一次打开时用户列表显示当前登录用户的关注用户列表
        view.focusUserList = res.data.userFocusList
        view.bindFlag = res.data.bindFlag
        // 当前选择用户为登录人
        view.selectUser = view.userInfo

        if (view.focusUserList.length > 0) {
          view.userDetail[0].fansNum = view.focusUserList.length
          view.focusType = 'warning'
        }
      } else {
        view.$slideMsg.message(res.msg, 'error')
      }
    })

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
  computed: {
    changeCheck: function () {
      // console.log('propsA--', JSON.stringify(this.configForm))
      // console.log('propsB--', JSON.stringify(this.oldForm))
      
      if (this.finalSearchParams != null) {
        for (const key in this.postParams) {
          if (Object.hasOwnProperty.call(this.postParams, key)) {
            // const element = object[key];
            if (this.postParams[key] !== this.finalSearchParams[key]) {
              return true
            }
          }
        }
        return false
      }
      return true
    },
    getUserProPic () {
      this.userInfo = this.$store.getters.getUserInfo
      return this.userInfo.profilePicture != undefined ? (process.env.BASE_URL + this.userInfo.profilePicture) : ''
    },
    getPath () {
      return process.env.BASE_URL
    },
    getUserFocusList () {
      let list = []
      if (this.selectUser != null && this.selectUser.userCode != this.userInfo.userCode) {
        list = this.userFocusList
      }
      return list
    }
  },
  watch: {

  }

}