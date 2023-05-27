<template>
  <div id='userCenter' class="user-center">
    <div ref="userInfo" class="user-info">
      <div class="info-title"><span class="fa fa-user-circle fa-lg pull-center "></span>用户中心</div>
      <div class="info-user">
        <el-avatar :size="120" >
          <img v-if="(getUserProPic!='')" :src="getUserProPic"/>
          <img v-else src="@static/img/user/003.jpg"/>
        </el-avatar>
        <div class="info-username">{{userInfo!=null?userInfo.username:''}}</div>
        <div class="info-sign" ref="sign" contentEditable="true" @blur="signBlur" @mousedown="signEdit" >
          {{userInfo!=null&&userInfo.userSign!=undefined?'‘‘'+userInfo.userSign+'’’':signEmpty}}
        </div>
        <div class="info-config">
          <el-row>
            <el-button type="primary" icon="el-icon-setting" size="mini" circle @click="configDrawer = true"></el-button>
            <el-button type="success" icon="el-icon-edit" size="mini" circle @click="updateUserDrawer = true"></el-button>
            <el-button type="info" icon="fa fa-folder fa-lg" size="mini" circle @click="showCollect"></el-button>
          </el-row>
          <li class="fa fa-exclamation-triangle fa-lg info-danger " title="未曾绑定，信息存在危险" v-if="bindFlag==0"></li>
        </div>
      </div>

      <!-- <div class="info-data">
        统计数据
      </div> -->
    </div>
    <div ref="searchInfo" class="search-info">
      <div class="search-header" >
        <div class="search-text">
          <input type="text" v-model="searchText" placeholder="输入用户名称或编号"/>
          <el-dropdown class="more-conditions" trigger="click" :hide-on-click="false" clearable >
            <el-button class="btn-conditions" type="text" icon="el-icon-zoom-in" size="mini" circle @click="moreConClick"></el-button>
            
            <el-dropdown-menu slot="dropdown" ref="conditions" style="display: none;">
              <el-dropdown-item >
                <el-menu
                  default-active="2"
                  class="el-menu-vertical-demo" 
                  @open="openMenu"
                  @select="selectMenu">
                  <el-submenu index="1">
                    <template slot="title">
                      <i class="el-icon-location"></i>
                      <span>性别</span>
                    </template>
                    <el-menu-item index="1-1" @click="changeSex(0, '女')">女</el-menu-item>
                    <el-menu-item index="1-2" @click="changeSex(1, '男')">男</el-menu-item>
                    <!-- <el-menu-item-group title="分组2">
                      <el-menu-item index="1-3">选项3</el-menu-item>
                    </el-menu-item-group> -->
                    <!-- <el-submenu index="1-4">
                      <template slot="title">选项4</template>
                      <el-menu-item index="1-4-1">选项1</el-menu-item>
                    </el-submenu> -->
                  </el-submenu>
                  <el-submenu index="2" >
                    <template slot="title">
                      <i class="el-icon-menu"></i>
                      <span>地址</span>
                    </template>
                    <el-submenu index="2-1" style="max-height: 350px;overflow-y: scroll;" ref="proMenu">
                      <template slot="title" style="position: fixed;" >省份</template>
                      <el-menu-item index="2-2-0" :key="item.pid" :ref="'pro_'+item.pid" v-for="item in provinceList" 
                      @click="changePro(item.pid, item.province)">{{item.province}}</el-menu-item>
                    </el-submenu>
                    <el-submenu index="2-2" style="max-height: 350px;overflow-y: scroll;">
                      <template slot="title">城市</template>
                      <el-menu-item index="(item.cid)" :ref="'city_'+item.cid" v-for="item in cityList" :key="item.cid"
                      @click="changeCity(item.cid, item.city)">{{item.city}}</el-menu-item>
                    </el-submenu>
                  </el-submenu>
                  <el-submenu index="3">
                    <template slot="title">
                      <i class="el-icon-coin"></i>
                      <span>年龄</span>
                    </template>
                    <el-menu-item index="3-1" @click="changeAge(10, 18)">10-18</el-menu-item>
                    <el-menu-item index="3-2" @click="changeAge(18, 25)">18-25</el-menu-item>
                    <el-menu-item index="3-3" @click="changeAge(25, 35)">25-35</el-menu-item>
                    <el-menu-item index="3-4" @click="changeAge(35, 65)">35-65</el-menu-item>
                  </el-submenu>
                  <!-- <el-menu-item index="4">
                    <i class="el-icon-setting"></i>
                    <span slot="title">导航四</span>
                  </el-menu-item> -->
                </el-menu>
              </el-dropdown-item>
              <!-- <el-dropdown-item icon="el-icon-circle-plus">狮子头</el-dropdown-item> -->
            </el-dropdown-menu>
          </el-dropdown>

          <el-button class="btn-search" icon="el-icon-search" size="mini" circle @click="searchUser(1)"></el-button>
          <span class="el-icon-plus add-condition" ref="addCondition"></span>
          <el-button class="btn-clear" type="text" icon="el-icon-refresh" size="mini" circle @click="clearData"></el-button>
        </div>
        
      </div>

      <div class="user-list">
        <el-collapse class="el-collapse-item__Bos" v-model="activeNames" accordion>
          <el-collapse-item name="1">
            <template slot="title">
              <i class="header-icon el-icon-info fulistfont">关注用户列表</i>
            </template>
            
            <div class="user-row" v-for="(user, index) in focusUserList" :key="index" :ref="'focus_user_'+index" @click="changeBorCol(index, '0')">
              <el-avatar class="user-row-img" :size="65" v-if="user.onlineStatus==1">
                <img v-if="user.profilePicture!=undefined && user.profilePicture!=''" :src="getPath+user.profilePicture"/>
                <img v-else src="@static/img/user/004.jpg"/>
              </el-avatar>
              <el-avatar class="user-row-img imgGray" :size="65" v-else>
                <img v-if="user.profilePicture!=undefined && user.profilePicture!=''" :src="getPath+user.profilePicture"/>
                <img v-else src="@static/img/user/004.jpg"/>
              </el-avatar>
              <div class="user-name">
                <label>{{user.username}}</label>
                <label class="user-nums">
                  <span class="fa fa-heart fa-lg" style="color: red;" ref="listUserLikeNum" aria-hidden="true">{{user.likeNum!=undefined?user.likeNum:0}}</span>
                  <span class="fa fa-star fa-lg" style="color: orange;" ref="listUserFansNum" size="medium">{{user.fansNum!=undefined?user.fansNum:0}}</span>
                </label>
              </div>
              <div class="user-sign">
                {{user.userSign | showTextByLength}}
              </div>
            </div>
          </el-collapse-item>
          <el-collapse-item title="查询用户列表" name="2">
            <div class="user-row" v-for="(user, index) in userList" :key="index" :ref="'user_'+index" @click="changeBorCol(index, '1')">
              <el-avatar class="user-row-img" :size="65" v-if="user.onlineStatus==1">
                <img v-if="user.profilePicture!=undefined && user.profilePicture!=''" :src="getPath+user.profilePicture"/>
                <img v-else src="@static/img/user/004.jpg"/>
              </el-avatar>
              <el-avatar class="user-row-img imgGray" :size="65" v-else>
                <img v-if="user.profilePicture!=undefined && user.profilePicture!=''" :src="getPath+user.profilePicture"/>
                <img v-else src="@static/img/user/004.jpg"/>
              </el-avatar>
              <div class="user-name">
                <label>{{user.username}}</label>
                <label class="user-nums">
                  <span class="glyphicon glyphicon-heart" ref="listUserLikeNum" aria-hidden="true">{{user.likeNum!=undefined?user.likeNum:0}}</span>
                  <span class="el-icon-star-on" ref="listUserFansNum" size="medium">{{user.fansNum!=undefined?user.fansNum:0}}</span>
                </label>
              </div>
              <div class="user-sign">
                {{user.userSign | showTextByLength}}
              </div>
            </div>
            <div class="load-more" v-if="total>(pageNum*pageSize)" >
              <a href="javascript:void(0)" @click="loadMoreInfo(pageNum+1)">--查看更多--</a>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>

      <!-- 其他人信息 -->
      <div class="other-info">
        <div class="other-user" v-for="(detail,index) in userDetail" :key="'other'+detail.userCode">
          <el-avatar class="other-user-img" v-if="detail.profilePicture!=undefined && detail.profilePicture!=''" :key="'oi'+detail.userCode" :size="65" >
            <img :src="detail.profilePicture"/>
          </el-avatar>
          <el-avatar class="other-user-img" :size="65" v-else><img src="@static/img/user/003.jpg"/></el-avatar>
          <label class="other-user-name">{{otherUsername}}</label>
          <span class="other-uf-img" v-for="(item,index) in getUserFocusList">
            <el-avatar v-if="item.profilePicture!=undefined && item.profilePicture!=''" :key="'pp_'+item.userCode" :size="35" >
              <img :src="getPath+item.profilePicture"/>
            </el-avatar>
          </span>

          <div class="other-doing" >
            <el-badge :value="detail.likeNum" >
              <el-button :class="heartClass" ref="heart" type="text" circle @click="giveALike" >💖</el-button>
            </el-badge>
            <el-badge :value="null" >
              <el-button type="success" icon="el-icon-chat-dot-round" circle @click="contactUser"></el-button>
            </el-badge>
            <!-- <el-badge :value="0" >
              <el-button type="info" icon="el-icon-message" circle></el-button>
            </el-badge> -->
            <el-badge :value="detail.fansNum" >
              <el-button :type="focusType" icon="el-icon-star-off" circle @click="focusUserClick"></el-button>
            </el-badge>
          </div>
        </div>

        <div class="info-daily-write">
          <span class="info-dw-b">
            <span class="fa fa-book fa-2x"></span><el-button type="text" @click="recordFormVisible = !recordFormVisible">写日记</el-button>
          </span>
        </div>

        <!-- 按月显示日记简要信息 -->
        <div class="daily-record-month">
          <el-timeline>
            <el-timeline-item
              v-for="(drList, key, index) in drDataForMonth" 
              :key="key"
              type="primary"
              color="cornflowerblue"
              size="large">
              <div class="dr-month">{{key}}</div>
              <el-collapse class="dr-collapse" @change="drChange">
                <el-collapse-item v-for="(item, dr_index) in drList" 
                  :key="item.drId" 
                  :title="item.title + (collectFlag?' (作者--'+item.username+')':'')" 
                  :name="item.drId">
                  <!-- 组件懒加载 -->
                  <vue-lazy-component>
                    <dr_view :drId.sync="item.drId" 
                    :createTime="item.createTime" 
                    :userInfo="selectUser" 
                    :replyInfo="replyInfo" 
                    :drData="drData" 
                    :reply-data.sync="replyData"></dr_view>
                  </vue-lazy-component>
                </el-collapse-item>
              </el-collapse>
            </el-timeline-item>
          </el-timeline>
          
          <!-- 回复框 -->
          <div class="record-footer" id="record-footer" >
            <label class="reply-un" v-text="replyData.replyUsername"></label>
            <div class="discuss-write">
              <input id="reply-content-w" type="text" placeholder="评论内容" @blur="replyWirteHide($event)" v-model="replyContent"/>
            </div>
            <div class="discuss-submit" @click="submitReply">
              <span class="fa fa-pencil fa-5x fa-fw"></span>
            </div>
          </div>
        </div>

      </div>
    </div>

    <!-- 用户配置 -->
    <el-drawer
      title="用户配置"
      size="380px"
      :visible.sync="configDrawer"
      :append-to-body="true"
      :direction="direction" 
      :with-header="false">
      <span>
        <!-- <h1 class="regist-title">聊天用户列表</h1> -->
        <UserConfigView :drawer.sync="configDrawer" :bindfromuc.sync="bindFlag" ></UserConfigView>
      </span>
    </el-drawer>



    <!-- 用户修改 -->
    <el-drawer
      title="用户修改"
      size="380px"
      :visible.sync="updateUserDrawer"
      :append-to-body="true"
      :direction="direction" 
      :with-header="false">
      <span>
        <h1 class="regist-title">用户信息修改</h1>
        <UesrUpdateView :drawer.sync="updateUserDrawer" :bindFlag2UE.sync="bindFlag" :detail.sync="userDetail"></UesrUpdateView>
      </span>
    </el-drawer>

    <!-- 日记编写 -->
    <el-dialog class="we-dialog"
    title="写日记" 
    :visible.sync="recordFormVisible" 
    :append-to-body="true"
    :close-on-click-modal="false"
    @open="openDialog">
      <el-form :model="recordForm">
        <el-form-item label="标题:" >
          <el-input v-model="recordForm.title" ></el-input>
        </el-form-item>
        <el-form-item label="日记分组:">
          <el-select class="rg-options" v-model="recordForm.recordGroupId" placeholder="请选择分组">
            <el-option v-for="item in recordGroup" :key="'re_gc_'+item.groupCode" :label="item.groupName" :value="item.recordGroupId">
            </el-option>
          </el-select>
          <div class="rg-new-group" ref="newReGroup" contentEditable="true" @focus="newRGFocus" @blur="newRGBlur($event)">新建分组</div>
          <div class="rg-new-ok" v-show="newRGVisible" >
            <el-button type="primary" size="small" round @mousedown.native="newRG">确定</el-button>
          </div>
        </el-form-item>
        <el-form-item label="日记类型:">
          <el-select v-model="recordForm.recordStatus" placeholder="请选择类型">
            <el-option v-for="item in recordType" :key="'re_status_'+item.recordStatus" :label="item.title" :value="item.recordStatus">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item class="record-content-title" label="内容:" >
          <div></div>
        </el-form-item>
        <wangEditor :write-content.sync="recordForm.content" :clear="recordFormClear"></wangEditor>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="recordFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitDR">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import userCenter from './js/user_center.js'
import 'font-awesome/css/font-awesome.min.css';

// 外部JS当作内部使用(避免内容过于臃肿)
export default userCenter

</script>

<style scoped>
  @import url('./css/user_center.css');
  @import url('https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css');
  @import "./css/scrollbar.css";


  .user-center {
    width: 100%;
    height: 100%;
  }
  
  .regist-title {
    text-align: center;
  }
</style>