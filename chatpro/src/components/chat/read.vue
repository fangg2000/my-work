<template>
  <div id='Read' class="pulldown">
    <ul class="pulldown-list demoScroll sc1" ref="bsWrapper">
      <li class="read-more" ref="loadMore" >
        <a href="javascript:void(0)" @click="loadMoreInfo" v-if="moreVisible">--查看更多--</a>
        <div style="color: red;" v-else>--已经是最前面了--</div>
      </li>
      <transition-group name="expand-info" >
        <li :key="chatInfo.client+index" class="pulldown-list-item" v-for="(chatInfo, index) of getDataList">
          <div v-if="rightType == '2' || rightType == '3'">
            <div class="container-info-right" v-if="chatInfo.client == userInfo.userCode">
              <div class="info-right">
                <label class="info-label" v-html='chatInfo.content'></label>
                <span>({{timeFormat.timeAgo(chatInfo.initTime)}})</span>
              </div>
              <div class="candy-right">
                <img v-if="userInfo.profilePicture && userInfo.profilePicture!=''" :src="getPath+userInfo.profilePicture"/>
                <img v-else src="@static/img/user/002.jpg"/>
              </div>
            </div>
            <div class="container-info-left" v-else >
              <div class="candy">
                <img v-if="chatInfo.profilePicture!=''" :src="getPath+chatInfo.profilePicture"/>
                <img v-else src="@static/img/user/001.jpg"/>
              </div>
              <div class="info">
                <div style="font-size: 16;font-weight: bolder;margin-bottom: 10px;color: #909399;">{{chatInfo.username}}</div>
                <div>
                  <label class="info-label" v-html='chatInfo.content'></label>
                  <span>({{timeFormat.timeAgo(chatInfo.initTime)}})</span>
                </div>
              </div>
            </div>
          </div>
          <div v-else>
            <div class="container-info-right" v-if="chatInfo.type == rightType">
              <div class="info-right">
                <label class="info-label" v-html='chatInfo.content'></label>
                <span>({{timeFormat.timeAgo(chatInfo.initTime)}})</span>
              </div>
              <div class="candy-right">
                <img v-if="userInfo.profilePicture && userInfo.profilePicture!=''" :src="getPath+userInfo.profilePicture"/>
                <img v-else src="@static/img/user/002.jpg"/>
              </div>
              <div class="unRead-right" v-if="chatInfo.status=='0'">
                <span>未读</span>
              </div>
            </div>
            <div class="container-info-left" v-else >
              <div class="candy">
                <img v-if="selectUser.profilePicture && selectUser.profilePicture!=''" :src="getPath+selectUser.profilePicture"/>
                <img v-else src="@static/img/user/001.jpg"/>
              </div>
              <div class="info">
                <label class="info-label" v-html='chatInfo.content'></label>
                <span>({{timeFormat.timeAgo(chatInfo.initTime)}})</span>
              </div>
              <div class="unRead" v-if="chatInfo.status=='0'">
                <span>未读</span>
              </div>
            </div>
          </div>
        </li>
      </transition-group>
      <li class="empty-c">
        <svg xmlns="http://www.w3.org/2000/svg" version="1.1">
          <defs>
            <filter id="goo">
              <feGaussianBlur in="SourceGraphic" stdDeviation="10" result="blur" />
              <feColorMatrix in="blur" mode="matrix" values="1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 35 -10" result="goo" />
              <feBlend in="SourceGraphic" in2="goo" operator="atop" />
            </filter>
          </defs>
        </svg>
        <div class="read-loader" v-show="loaderStart">
          <div></div>
          <div></div>
          <div></div>
          <div></div>
          <div></div>
        </div>
      </li>
    </ul>

    <!-- 图片放大 -->
    <div id="previewImg"  class="img_preview">
      <el-image style="opacity: 0;"
        :src="previewUrl" 
        :preview-src-list="previewUrlList"
        @click="previewImgClick">
      </el-image>
    </div>
  </div>
</template>

<script>
import Read from './js/read.js'

export default Read

</script>
<style scoped>
  @import "./css/info.css";
  @import "./css/scrollbar.css";

  .pulldown {
    width: 100%;
    height: 100%;
  }

  .read-more {
    text-align: center;
    position: relative;
    height: 50px;
    margin-top: 50px;
    color: red;
  }
  
  .pulldown-list {
    padding: 0;
    height: 100%;
    overflow-y: scroll;
  }
    
  
  .pulldown-list-item {
    min-height: 50px;
    padding: 10px 0;
    margin: 0px 30px;
    list-style: none;
    border-bottom: 0px solid #fff;
    transition: all 3s ease;
    animation: expand-all 2s ease;
  }

  @keyframes expand-all {
    0%{  
      transform: scale(1,1.1);
      -o-transform: scale(1,1.1);
      -moz-transform: scale(1,1.1);
      -webkit-transform: scale(1,1.1);
    }
    100%{  
      transform: scale(1,1);
      -o-transform: scale(1,1);
      -moz-transform: scale(1,1);
      -webkit-transform: scale(1,1);
    }
  }

  .empty-c {
    width: 100%;
    height: 130px;
    position: relative;
    float: left;
  }

  .unRead {
    width: 50px;
    height: 25px;
    background-color: #E4E7ED;
    border: 5px solid #E4E7ED;
    text-align: center;
    position: absolute;
    bottom: 0px;
    right: 0px;
    border-radius: 50px 50px 0 0;
    border-bottom: none;
  }
  .unRead span {
    position: relative;
    top: 3px;
    left: 0px;
    font-family: 'Courier New', Courier, monospace;
    font-size: 18px;
    font-weight: bolder;
    color: #909399;
  }

  .unRead-right {
    width: 50px;
    height: 25px;
    background-color: #E4E7ED;
    border: 5px solid #E4E7ED;
    text-align: center;
    position: absolute;
    bottom: 0px;
    left: 0px;
    border-radius: 50px 50px 0 0;
    border-bottom: none;
  } 
  .unRead-right span {
    position: relative;
    top: 0px;
    left: 0px;
    font-family: 'Courier New', Courier, monospace;
    font-size: 18px;
    font-weight: bolder;
    color: #909399;
  }
  
  /* 信息动态展开 */
  /* .expand-info-enter-active {
    transition: all 5s ease-in;
  }
  .expand-info-leave-active {
    transition: all 1s cubic-bezier(1.0, 0.5, 0.8, 1.0);
  } */


  /* 信息加载 */
  @keyframes read-loader {
    50% {
      transform: translateY(-16px);
      background-color: #EBEEF5;
    }
  }

  .read-loader {
    filter: url("#goo");
    width: 100px;
    margin: 0 auto;
    position: absolute;
    top: 55px;
    right: 35px;
    transform: translateY(-10px);
  }
  .read-loader > div {
    float: left;
    height: 20px;
    width: 20px;
    border-radius: 100%;
    background-color: greenyellow;
    animation: read-loader 0.8s infinite;
  }

  .read-loader > div:nth-child(1) {
    animation-delay: 0.16s;
  }

  .read-loader > div:nth-child(2) {
    animation-delay: 0.32s;
  }

  .read-loader > div:nth-child(3) {
    animation-delay: 0.48s;
  }

  .read-loader > div:nth-child(4) {
    animation-delay: 0.64s;
  }

  .read-loader > div:nth-child(5) {
    animation-delay: 0.8s;
  }
    
  .el-image-viewer__wrapper {
    z-index: 9000;
  }

  .img_preview {
    position: fixed;
    width: 15px;
    height: 15px;
    cursor: zoom-in;
    z-index: 10005;
  }
  .img_preview >>> .el-image {
    width: 100%;
    height: 100%;
    cursor: zoom-in;
  }

  
  .info-right {
    margin-right: 35px;
  }
  .info-right span {
    margin-left: 30px;
    font-size: 12px;
    color: #909399;
  }

  .info-label {
    word-wrap:break-word;
    word-break:keep-all;
    overflow:hidden;
  }

  </style>