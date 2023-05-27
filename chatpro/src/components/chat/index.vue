<template>
  <div id='Chat' class="chat-main">
    <div id="container" class="container-box"></div>
    <div class="chat-read">
      <div class="chat-title">
        <!-- <div class="title-content">
          <span>{{selectUser!=null?selectUser.username:''}}</span>
        </div> -->
        <el-row>
          <div class="chat-login">
            <el-avatar key="login_user_code" :size="28">
              <img v-if="userInfo!=null && userInfo.profilePicture && userInfo.profilePicture!=''" :src="getPath+userInfo.profilePicture" draggable="false"/>
              <img v-else src="@static/img/user/001.jpg" draggable="false"/>
            </el-avatar>
            <span class="chat-login-username">{{userInfo!=null?userInfo.username:''}}</span>
          </div>
          <span class="chat-title-options" v-if="userInfo && userInfo.userType == 1">
            <div class="search-cs">
              <input type="text" v-model="searchCS" placeholder="查找企业或客服"/>
            </div>
            <el-button icon="el-icon-search" size="mini" circle @click="csVisible = true"></el-button>
            <el-button type="primary" icon="el-icon-setting" size="mini" circle @click="editDrawer = true"></el-button>
            <!-- <el-button type="primary" icon="el-icon-edit" size="mini" circle ></el-button>
            <el-button type="success" icon="el-icon-check" size="mini" circle @click="showUser"></el-button> -->
            
            <el-badge :value="getUCGApplyList" class="item" type="warning">
              <el-button type="info" icon="el-icon-news" size="mini" circle @click="waittingDo"></el-button>
            </el-badge>
            <!-- <el-button type="warning" icon="el-icon-star-off" size="mini" circle @click="changeCode('U0ee19d5e')"></el-button>
            <el-button type="danger" icon="el-icon-delete" size="mini" circle @click="getNotReadUserList"></el-button> -->
          </span>
          <el-button class="chat-logout" icon="el-icon-switch-button" size="mini" circle @click="loginOut"></el-button>
        </el-row>
      </div>

      <!-- 从:user.sync中把选中的用户信息转到子组件中去，并开启聊天连接 -->
      <read_view 
        v-show="selectUser != null"
        :userCode="selectCode" 
        :user.sync="selectUser" 
        :loader.sync="writeOver" 
        :socketStatus="status" 
        @socket="newRWS" 
        @initParams="initWriteParams" 
        @onlineUnread="remindOtherSide"></read_view>
    </div>

    <div v-if="selectUser != null" id="mainWirte" class="chat-footer" draggable="true">
      <div class="chat-write">
        <!-- <el-input type="textarea" placeholder="请输入内容(ctrl+Enter发送)" v-model="wirteInfo"
          maxlength="1000" rows="5" cols="150" resize="none" show-word-limit @keyup.enter.native="wirteHandler($event)"/> -->
        
        <div class="input-write">
          <!-- emoji表情 -->
          <div class="emoji">
            <div class="emoji-i">
              <el-button type="text" @click="showEmoji($event, 0)">
                <li class="fa fa-smile-o fa-lg"></li>&nbsp;表情
              </el-button>
            </div>
            <el-upload
              class="upload-img"
              ref="uploadImg"
              action="#"
              accept="image/*"
              :show-file-list="false"
              :auto-upload="false"
              :on-change="fileChange">
              <el-button type="text" >
                <li class="fa fa-file-image-o "></li>&nbsp;图片
              </el-button>
            </el-upload>
            <div class="write-close" @click="closeRWS">
              <el-button type="text" >
                <li class="fa fa-close fa-lg"></li>&nbsp;关闭
              </el-button>
            </div>
            <div class="input2-user-name" >
              <span>{{selectUser | showNameFilter}}</span>
            </div>
          </div>

          <!-- <textarea class="input-main-wirte" placeholder="请输入内容(ctrl+Enter发送)" v-model="wirteInfo"
          maxlength="1000" rows="5" cols="120" show-word-limit @keyup.enter="wirteHandler($event)"></textarea> -->
          <div id="writeInfo" ref="wirteInfo" class="input-main-wirte" 
            @input="changeText($event)" 
            v-html="wirteInfo"
            contenteditable="true" 
            style="width: 450px;min-width: 180px;height: 60px;text-align: left" @keyup.enter="wirteHandler($event)"></div>
          <div class="zoom-content">
            <div ref="writeLength" class="w-max-length">{{0+'/'+maxLength}}</div>
            <div id="zoomWrite" class="zoom-write">
              <div class="zoom-line1"></div>
              <div class="zoom-line2"></div>
            </div>
            <!-- <div class="input2-user-name" >{{selectUser?'@'+selectUser.username:''}}</div> -->
          </div>
          <div v-show="inputPholderVisible" 
          class="input-placeholder" 
          draggable="false"
          @click="holderClick($event)">请输入内容(Ctrl+Enter发送)</div>
        </div>
      </div>
      
      <div class="dip"></div>
    
      <!-- 表情框 -->
      <div id="emoji" v-show="emojiVisible" class="emoji-content">
        <el-button-group>
          <el-button size="mini" ref="normal" round @click="bqNameChange('normal')"><li class="fa fa-smile-o fa-lg"></li>&nbsp;一般</el-button>
          <el-button size="mini" @click="bqNameChange('animal')"><li class="fa fa-bug fa-lg"></li>&nbsp;动物</el-button>
          <el-button icon="el-icon-picture-outline-round" size="mini" @click="bqNameChange('nature')">自然</el-button>
          <el-button icon="el-icon-basketball" size="mini" @click="bqNameChange('sport')">运动</el-button>
          <el-button icon="el-icon-food" size="mini" @click="bqNameChange('food')">食物</el-button>
          <el-button size="mini" round @click="bqNameChange('flag')"><li class="fa fa-map-signs fa-lg"></li>&nbsp;标识</el-button>
        </el-button-group>
        <div class="emoji-icon-list">
          <!-- <Picker :include="['people','Smileys']" :showSearch="false" :showPreview="false" :showCategories="false" @select="addEmoji" /> -->
          <span v-for='(emoji,index) in emojiList' class="emoji-icon" :key="index" size="mini" @click="addEmoji(emoji.icon, 0)" round>{{emoji.icon}}</span>
        </div>
      </div>
    </div>

    <!-- <h1 class="gradient">hello.</h1> -->
    <div class="clock myClock">
      <span class="hours"></span>
      <span class="minutes"></span>
      <span class="seconds"></span>
    </div>

    <!-- 左边抽屉-用户相关操作 -->
    <el-drawer
      class="user-center-drawer"
      ref="user-center-drawer"
      size="1650px"
      v-if="userInfo && userInfo.userType == 1"
      :visible.sync="editDrawer"
      :direction="direction" 
      :with-header="false"
      @opened="opened">
      <span>
        <userCenter :drawer.sync="editDrawer" @afterFocusUser="afterFUClilk" @contactUser="changeUser"></userCenter>
      </span>
    </el-drawer>

    <!-- 右边抽屉-聊天用户列表 -->
    <div
      ref="rightListDrawer"
      class="right-user-drawer"
      :modal="false"
      :with-header="false"
      @open="rdrawerOpen">
      <span>
        <!-- <h1 class="regist-title">聊天用户列表</h1> -->
        <ChatUserListView 
        :drawer.sync="rightDrawer" 
        :drawer-move.sync="lockDrawer" 
        :newConUsers2RD="socketBackData" 
        :newFocusUser2RD="newFocusUser" 
        :add2RDNearContact="selectUser" 
        :newBB2RD="newBBUser" 
        :csVisible2RD="csVisible" 
        @moveFirstContact="remindOtherSide" 
        @notOnlineUserCheck2Idx="checkNotOnlineUser" 
        @contact2User="contactFromRD"></ChatUserListView>
      </span>
    </div>

    <!-- 泡泡显示内容 -->
    <el-popover
      id="bbPopoer"
      class="bbPopoer"
      draggable="false"
      placement="top">
      <!-- <div class="bb-title">
        <span>回复消息</span>
      </div>
      <p id="bb_info" style="margin-top: 0px;"></p>
      <textarea rows="1" cols="37" maxlength="512" style="resize: none;opacity: 0.5;" placeholder="请输入内容(ctrl+Enter发送)"></textarea>
      <div id="x0p-buttons" class="buttons">
        <div id="x0p-button-0" class="bb-button bb-button-cancel" style="width: 50.00%; width: calc(100% / 2);">Cancel</div>
        <div id="x0p-button-1" class="bb-button bb-button-ok" style="width: 50.00%; width: calc(100% / 2);">OK</div>
      </div> -->

      <div class="more-info">
        <div class="more-info-icon" @click="moreInfoRead">
          <li v-if="showMore==false" class="fa fa-angle-double-down fa-lg "></li>
          <li v-else class="fa fa-angle-double-up fa-lg "></li>
        </div>
        <transition name="expand-more" >
          <ul id="moreInfoList" ref="moreInfoList" class="more-info-li demoScroll sc2" v-show="showMore"></ul>
        </transition>
      </div>
      <div id="bb_info" ref="bbInfo" class="bb-info" for="letter"></div>
      <div class="input">

        <!-- emoji表情 -->
        <!-- <div class="emoji">
          <div class="emoji-i">
            <el-button type="text" @click="showEmoji($event, 1)">
              <li class="fa fa-smile-o fa-lg"></li>&nbsp;表情
            </el-button>
          </div>
        </div> -->
    
        <!-- 表情框 -->
        <!-- <div id="emojiMsg" v-show="emojiVisibleMsg" class="emoji-content-msg">
          <el-button-group>
            <el-button size="mini" ref="normalMsg" round @click="bqNameChange('normal')"><li class="fa fa-smile-o fa-lg"></li>&nbsp;一般</el-button>
            <el-button size="mini" @click="bqNameChange('animal')"><li class="fa fa-bug fa-lg"></li>&nbsp;动物</el-button>
            <el-button icon="el-icon-picture-outline-round" size="mini" @click="bqNameChange('nature')">自然</el-button>
            <el-button icon="el-icon-basketball" size="mini" @click="bqNameChange('sport')">运动</el-button>
            <el-button icon="el-icon-food" size="mini" @click="bqNameChange('food')">食物</el-button>
            <el-button size="mini" round @click="bqNameChange('flag')"><li class="fa fa-map-signs fa-lg"></li>&nbsp;标识</el-button>
          </el-button-group>
          <div class="emoji-icon-list-msg">
            <span v-for='(emoji,index) in emojiList' class="emoji-icon" :key="index" size="mini" @click="addEmoji(emoji.icon, 1)" round>{{emoji.icon}}</span>
          </div>
        </div> -->

        <!-- <textarea class="input-message" name="letter" rows="3" cols="38" ref="sendMsg" @keydown="msgKeydown"
        placeholder="请输入内容(Shift+Enter发送)"></textarea> -->
        <!-- 组件懒加载 -->
        <!-- <vue-lazy-component>
          <write_msg_view :write-msg-id.sync="writeMsgId"></write_msg_view>
        </vue-lazy-component> -->
        <write_msg_view 
        class="input-w-message" 
        name="letter" 
        ref="sendMsg" 
        :write-msg-id.sync="writeMsgId" 
        :write-msg-content.sync="writeMsgContent"></write_msg_view>

        <div class="buttons">
          <button id="cancel_btn" class="button" >关闭窗口</button>
          <button id="cancel_ball" class="button" @click="closeChat($event)">取关对话</button>
          <button id="send_btn" class="primary button" @click="sendClick($event)">发送</button>
        </div>

        <div class="zoom-content">
          <div ref="writeMsgLength" class="wm-max-length">{{0+'/'+maxLength}}</div>
          <div id="zoomWriteMsg" class="zoom-write zoom-write-msg">
            <div class="zoom-line1"></div>
            <div class="zoom-line2"></div>
          </div>
        </div>
      </div>
    </el-popover>
    
    <!-- 待办事项 -->
    <el-dialog title="待办事项" :visible.sync="waitDoVisible" width="1050px" :append-to-body="true" >
      <div class="fa fa-group fa-1g" style="font-weight: bolder;margin-left: 10px;color: #C0C4CC;">入群审核：</div>
      <el-table :data="ucgApplyInfoList" >
        <el-table-column property="uc" label="申请人" width="100"></el-table-column>
        <el-table-column property="gc" label="群编号" width="100"></el-table-column>
        <el-table-column property="gn" label="群名称" width="120"></el-table-column>
        <el-table-column property="de" label="说明" ></el-table-column>
        <el-table-column label="操作" width="220">
          <template slot-scope="scope">
            <el-button @click="checkUCGApply(scope.row, 0)" type="text" size="small" >拒绝</el-button>
            <el-button @click="checkUCGApply(scope.row, 1)" type="success" size="small" >通过</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import Chat from './js/index.js'
import './clock.js'

export default Chat


</script>

<style scoped>
  /* @import "@/base/bubble/index.css"; */
  @import "./css/dip.css";
  @import "./css/clock.css";
  @import "./css/index.css";
  @import "./css/scrollbar.css";
  
  
</style>