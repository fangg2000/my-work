
export function DateTime(params) {
  this.pattern = params == undefined ? 'yyyy-MM-dd hh:mm:ss' : params
  this.patternDate = params == undefined ? 'yyyy-MM-dd' : params.split(' ')[0]
  this.patternTime = params == undefined ? 'hh:mm:ss' : params.split(' ')[1]
}

DateTime.prototype.format = function (date, t) {
  if (t == undefined) {
    t = this.pattern
  }

  var date = new Date(date);
  var o = {
    "M+": date.getMonth() + 1,                 //月份
    "d+": date.getDate(),                    //日
    "h+": date.getHours(),                   //小时
    "m+": date.getMinutes(),                 //分
    "s+": date.getSeconds(),                 //秒
    "q+": Math.floor((date.getMonth() + 3) / 3), //季度
    "S": date.getMilliseconds()             //毫秒
  }

  if (/(y+)/.test(t)) {
    t = t.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length))
  }
  for (var k in o) {
    if (new RegExp("(" + k + ")").test(t)) {
      t = t.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)))
    }
  }
  return t
}

DateTime.prototype.formatDate = function (date, t) {
  if (t == undefined) {
    t = this.patternDate
  }

  return this.format(date, t)
}

DateTime.prototype.formatTime = function (date, t) {
  if (t == undefined) {
    t = this.patternTime
  }

  return this.format(date, t)
}

DateTime.prototype.timeAgo = function (date) {
  if (date == undefined) {
    return ''
  }

  var date = new Date(date)
  var mistiming = Math.round(((new Date()).getTime() - date.getTime()) / 1000)
  var negativeFlag = mistiming >= 0 ? false : true
  mistiming = mistiming >= 0 ? mistiming : (-1*mistiming)
  var arrr = ['年', '个月', '周', '天', '小时', '分钟', '秒']
  var arrn = [31536000, 2592000, 604800, 86400, 3600, 60, 1]
  
  for (var i = 0; i < arrn.length; i++) {
    var inm = Math.floor(mistiming / arrn[i])
    if (inm != 0) {
      if (arrr[i] == '秒' && inm < 30 && negativeFlag == false) {
        return '刚刚'
      }
      return inm + arrr[i] + (negativeFlag?'后':'前')
    }
  }
  return '刚刚'
}

// 当前毫秒
DateTime.prototype.getMillSeconds = function (date) {
  if (date == undefined) {
    return new Date().getTime()
  }

  date = new Date(date);
  return date.getTime()
}