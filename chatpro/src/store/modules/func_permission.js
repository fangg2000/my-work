/* 
自定义列表
注：code唯一
name为自定义的方法名（为空或重复则使用默认名称），判断是否有权限，默认"name+Limit"为其对应的数量限制，如getTestLimit
默认生成"getP4+code"判断权限方法，还有其对应的"getP4+code+Limit"
没有权限返回false，没有数量限制返回null

下面为测试
console.log('功能权限动态方法测试--', JSON.stringify(view.$store.getters['funcPer/getP4test01']))
console.log('功能权限动态方法测试--', JSON.stringify(view.$store.getters['funcPer/getP4test02']))
console.log('功能权限动态方法测试--', JSON.stringify(view.$store.getters['funcPer/getTest']))
console.log('功能权限动态方法测试--', JSON.stringify(view.$store.getters['funcPer/getP4test03']))
 */
const custom_arr = [
  { code: 'test01', title: '测试' },
  { name: '', code: 'test02', title: '测试' },
  { name: 'getTest', code: 'test03', title: '测试' },
  { name: 'getP4FUList', code: 'F0001', title: '查看用户的关注用户权限' },
  { name: 'getP4VNC', code: 'F0002', title: 'VIP用户最近联系人权限' },
  { name: 'getP4MNC', code: 'F0003', title: 'MVP用户最近联系人权限' },
  { name: 'getP4NNC', code: 'F0004', title: '一般用户最近联系人权限' }
]

// 列表类型，0或其他使用服务端返回的列表(即功能权限列表，只能使用默认方法)，1自定义列表
// 注：不能由1改为0或其他（可以由0或其他改成1），否则可能会出问题，因为服务端返回的列表中只有对应的code
// 推荐使用自定义列表，方便前端拓展
const arr_type = 1

const FuncPermission = {
  namespaced: true,
  state: () => ({
    funcPermission: null
  }),
  mutations: {
    updateFuncPermission(state, key) {
      // 这里的 `state` 对象是模块的局部状态
      state.funcPermission = key
    }
  },
  getters: {
    getRootPer(state, getters, rootState, rootGetters) {
      // console.log(rootState)
      return rootState.funcPermission
    },
    getPermission: (state, getters, rootState, rootGetters) => (code) => {
      // console.log('code为--', code)
      let permission = getters.getRootPer
      let perFlag = false
      if (permission != null) {
        permission.forEach(funcPer => {
          if (funcPer.code == code) {
            perFlag = true
          }
        });
      }
      return perFlag
    },
    getLimit: (state, getters, rootState, rootGetters) => (code) => {
      let permission = getters.getRootPer
      let limit = null
      if (permission != null) {
        permission.forEach(funcPer => {
          if (funcPer.code == code) {
            limit = funcPer.limit
          }
        });
      }
      return limit
    }
  }
}

function checkName(type) {
  if (type.name == undefined || type.name == null || type.name.trim == '') {
    return 'getP4' + type.code
  } else if (FuncPermission.getters[type.name] != undefined) {
    return 'getP4' + type.code
  }
  return type.name
}

function checkLimit(type) {
  if (type.name == undefined || type.name == null || type.name.trim == '') {
    return 'getP4' + type.code + 'Limit'
  } else if (FuncPermission.getters[type.name + 'Limit'] != undefined) {
    return 'getP4' + type.code + 'Limit'
  }
  return type.name + 'Limit'
}

const type_arr = arr_type == 1 ? custom_arr : FuncPermission.getters.getRootPer

// 动态添加方法
type_arr.forEach(type => {
  FuncPermission.getters[checkName(type)] = (state, getters, rootState, rootGetters) => {
    return getters.getPermission(type.code)
  },
    FuncPermission.getters[checkLimit(type)] = (state, getters, rootState, rootGetters) => {
      return getters.getLimit(type.code)
    }
  FuncPermission.getters['getP4' + type.code] = (state, getters, rootState, rootGetters) => {
    return getters.getPermission(type.code)
  },
    FuncPermission.getters['getP4' + type.code + 'Limit'] = (state, getters, rootState, rootGetters) => {
      return getters.getLimit(type.code)
    }
});

export default FuncPermission
