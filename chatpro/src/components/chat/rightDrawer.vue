<template>
  <div id='ChatUserList' class="right-drawer" @mousemove="mouseoutHandle" @contextmenu.prevent="">
    <el-collapse v-model="activeName" >
      <el-collapse-item class="el-collapse-item__Bos" name="1">
        <template class="contact-title" slot="title">
          <!-- <i class="el-icon-chat-dot-square"></i> -->最近联系人
        </template>
        <div class="contact-row" v-for="(user, index) in getNCListByPermission" :key="'near_user_'+user.userCode"
          @click="contactUser(user, index, 1)" 
          draggable="true" 
          :id="'drag_'+user.userCode"
          @mouseup="rightKeyMenu($event, user.userType, user.userCode)"
          @mouseout="rightKeyOut($event)"
          @dragend="userDragEnd($event, user.userCode, user.userType)">
          <el-avatar :ref="'near_user_'+index" :class="getPPClass" v-if="user.newFlag==1&&user.onlineStatus==1"
            :key="'near_user_'+index+user.userCode" :size="50">
            <img v-if="user.profilePicture!=undefined && user.profilePicture!=''" :src="getPath+user.profilePicture" draggable="false"/>
            <img v-else src="@static/img/user/004.jpg" draggable="false"/>
          </el-avatar>

          <el-avatar :ref="'near_user_'+index" :class="getPPNGClass" v-else-if="user.newFlag==1"
            :key="'near_user_'+index+user.userCode" :size="50">
            <img v-if="user.profilePicture!=undefined && user.profilePicture!=''" :src="getPath+user.profilePicture" draggable="false"/>
            <img v-else src="@static/img/user/004.jpg" draggable="false"/>
          </el-avatar>

          <el-avatar :ref="'near_user_'+index" :class="getPPONClass" v-else-if="user.onlineStatus==1"
            :key="'near_user_'+index+user.userCode" :size="50">
            <img v-if="user.profilePicture!=undefined && user.profilePicture!=''" :src="getPath+user.profilePicture" draggable="false"/>
            <img v-else src="@static/img/user/004.jpg" draggable="false"/>
          </el-avatar>

          <el-avatar :ref="'near_user_'+index" :class="getPPGClass" v-else :key="'near_user_'+index+user.userCode" :size="50">
            <img v-if="user.profilePicture!=undefined && user.profilePicture!=''" :src="getPath+user.profilePicture" draggable="false"/>
            <img v-else src="@static/img/user/004.jpg" draggable="false"/>
          </el-avatar>
          <div class="contact-name">{{user.username}}</div>
          <div class="contact-time">{{timeFormat.timeAgo(user.updateTime)}}</div>
        </div>
      </el-collapse-item>
      <el-collapse-item class="el-collapse-item__Bos" name="2" v-if="userInfo && userInfo.userType == 1">
        <template class="contact-title" slot="title">
          关注用户
        </template>
        <div class="contact-row" v-for="(user, index) in focusUserList" :key="'focus_user_'+user.userCode" 
          draggable="true"
          :id="'drag_'+user.userCode"
          @dragend="userDragEnd($event, user.userCode, 1)" 
          @click="contactUser(user, index, 2)">
          <el-avatar :ref="'focus_user_'+index" :class="getPPClass" v-if="user.newFlag==1&&user.onlineStatus==1"
            :key="'focus_user_'+index+user.userCode" :size="50">
            <img v-if="user.profilePicture!=undefined && user.profilePicture!=''" :src="getPath+user.profilePicture" draggable="false"/>
            <img v-else src="@static/img/user/004.jpg" draggable="false"/>
          </el-avatar>

          <el-avatar :ref="'focus_user_'+index" :class="getPPNGClass" v-else-if="user.newFlag==1"
            :key="'focus_user_'+index+user.userCode" :size="50">
            <img v-if="user.profilePicture!=undefined && user.profilePicture!=''" :src="getPath+user.profilePicture" draggable="false"/>
            <img v-else src="@static/img/user/004.jpg" draggable="false"/>
          </el-avatar>

          <el-avatar :ref="'focus_user_'+index" :class="getPPONClass" v-else-if="user.onlineStatus==1"
            :key="'focus_user_'+index+user.userCode" :size="50">
            <img v-if="user.profilePicture!=undefined && user.profilePicture!=''" :src="getPath+user.profilePicture" draggable="false"/>
            <img v-else src="@static/img/user/004.jpg" draggable="false"/>
          </el-avatar>

          <el-avatar :ref="'focus_user_'+index" :class="getPPGClass" v-else :key="'focus_user_'+index+user.userCode"
            :size="50">
            <img v-if="user.profilePicture!=undefined && user.profilePicture!=''" :src="getPath+user.profilePicture" draggable="false"/>
            <img v-else src="@static/img/user/004.jpg" draggable="false"/>
          </el-avatar>
          <div class="contact-name">{{user.username}}</div>
          <div class="contact-time">{{user.userSign | showTextByLength}}</div>
        </div>
      </el-collapse-item>
      <el-collapse-item class="el-collapse-item__Bos" name="3">
        <template class="contact-title" slot="title">
          陌生人来信
        </template>
        <div class="contact-row " v-for="(user, index) in newConUserList" :key="'new_con_'+user.userCode"
          draggable="false"
          @click="contactUser(user, index, 3)">
          <el-avatar :ref="'new_con_'+index" :class="getPPClass" v-if="user.newFlag==1" :key="'new_con_'+index+user.userCode"
            :size="50">
            <img v-if="user.profilePicture!=undefined && user.profilePicture!=''" :src="getPath+user.profilePicture" draggable="false"/>
            <img v-else src="@static/img/user/004.jpg" draggable="false"/>
          </el-avatar>
          <el-avatar :ref="'new_con_'+index" :class="ppAvtiveType==0?'contact-pp btn--wiggle':'contact-pp btn--jump'"
            v-else :key="'new_con_'+index+user.userCode" :size="50"><img src="@static/img/user/004.jpg" draggable="false"/></el-avatar>
          <div class="contact-name">{{user.username}}</div>
          <div class="contact-time">{{user.content}}</div>
        </div>
      </el-collapse-item>

      <!-- 群列表 -->
      <el-collapse-item class="el-collapse-item__Bos" name="4" v-if="userInfo && userInfo.userType == 1">
        <template class="contact-title" slot="title">
          群列表
        </template>
        <div class="contact-row " v-for="(group, index) in chatGroupList" :key="'cg_'+group.chatGroupCode" 
          draggable="true"
          @mouseup="rightKeyMenu($event, group.userType, group.chatGroupCode)"
          @mouseout="rightKeyOut($event)"
          @dragend="userDragEnd($event, group.chatGroupCode, 2)"
          @click="contactUser(group, index, 4)">
          <el-avatar :ref="'cg_'+index" :class="getPPClass" v-if="group.newFlag==1" :key="'cg_'+index+group.chatGroupCode"
            :size="50">
            <img v-if="group.profilePicture && group.profilePicture!=''" :src="getPath+group.profilePicture" draggable="false"/>
            <img v-else src="@static/img/user/004.jpg" draggable="false"/>
          </el-avatar>
          <el-avatar :ref="'cg_'+index" :class="ppAvtiveType==0?'contact-pp btn--wiggle':'contact-pp btn--jump'"
            v-else :key="'cg_'+index+group.chatGroupCode" :size="50"><img src="@static/img/user/004.jpg" draggable="false"/></el-avatar>
          <div class="contact-name">{{group.chatGroupName}}</div>
          <div class="contact-time">{{group.groupSign | showTextByLength}}</div>
        </div>
        <div class="chat-group-add" >
          <el-button icon="el-icon-search" size="mini" circle @click="searchChatGroup"></el-button>查找群
          <el-button type="primary" icon="el-icon-plus" size="mini" circle @click="newChatGroup"></el-button>创建群
        </div>
      </el-collapse-item>

      <!-- 客服列表 -->
      <el-collapse-item class="el-collapse-item__Bos" name="5" v-if="userInfo && userInfo.userType == 1 && csVisible == true">
        <template class="contact-title" slot="title">
          客服列表
        </template>
        <div class="contact-row " v-for="(item, index) in csList" :key="'cs_'+item.userCode" 
        draggable="true"
        @dragend="userDragEnd($event, item.userCode, 0)"
        @click="contactUser(item, index, 5)">
          <el-avatar :ref="'cs_'+index" class="contact-pp " v-if="item.onlineStatus && item.onlineStatus==1" :key="'cs_'+index+item.userCode" :size="50">
            <img src="@static/img/user/cs.jpg" draggable="false"/>
          </el-avatar>
          <el-avatar :ref="'cs_'+index" class="contact-pp imgGray" v-else :key="'cs_'+index+item.userCode" :size="50">
            <img src="@static/img/user/cs.jpg" draggable="false"/>
          </el-avatar>
          <div class="contact-name">{{item.username}}</div>
          <div class="contact-time">{{item.userSign}}</div>
        </div>
      </el-collapse-item>
    </el-collapse>

    <!-- 新增群-窗口 -->
    <el-dialog title="新增群" :visible.sync="cgFormVisible" width="650px" :append-to-body="true">
      <el-form :model="cgForm" :rules="rules" ref="cgForm">
        <el-form-item label="名称" :label-width="formLabelWidth" prop="chatGroupName">
          <el-input v-model="cgForm.chatGroupName" width="150px" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="入群条件" :label-width="formLabelWidth">
          <el-select v-model="cgForm.applyCondition" placeholder="请选择入群条件">
            <el-option v-for="(item,index) in condition" :label="item.name" :value="item.value" :key="'cgc_'+index"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="类型" :label-width="formLabelWidth">
          <el-select v-model="cgForm.groupType" placeholder="请选择群类型">
            <el-option v-for="(item,index) in groupType" :label="item.name" :value="item.value" :key="'cgt_'+index"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="头像" :label-width="formLabelWidth">
          <el-avatar icon="el-icon-user-solid"></el-avatar>
        </el-form-item>
        <el-form-item label="签名" :label-width="formLabelWidth">
          <el-input type="textarea" v-model="cgForm.groupSign"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cgFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitCGForm">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 查找群 -->
    <el-dialog title="查找群" :visible.sync="cgSearchVisible" width="1050px" :append-to-body="true" >
      <el-form class="demo-form-inline" :inline="true" :model="cgSearchForm" ref="cgSearchForm" >
        <el-form-item label="群名称/编号" label-width="100px" >
          <el-input v-model="cgSearchForm.chatGroupCode" width="150px" autocomplete="off" placeholder=""></el-input>
        </el-form-item>
        <el-form-item label="群类型" label-width="60px"">
          <el-select v-model="cgSearchForm.groupType" placeholder="请选择群类型">
            <el-option v-for="(item,index) in groupType" :label="item.name" :value="item.value" :key="'cgt_'+index"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchSubmit">查询</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="cgGridData" :row-class-name="rowClassNameFn">
        <el-table-column label="序号" width="50">
          <template slot-scope="scope">
            <span>{{ scope.row.index }}</span>
          </template>
        </el-table-column>
        <el-table-column property="chatGroupName" label="群名称" width="150"></el-table-column>
        <el-table-column label="类型" width="100">
          <template slot-scope="scope">
            <span >{{ groupTypeFilter(scope.row.groupType) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="数量" width="80">
          <template slot-scope="scope">
            <span >{{ scope.row.groupNum+'/'+scope.row.groupLimit }}</span>
          </template>
        </el-table-column>
        <el-table-column label="创建日期" >
          <template slot-scope="scope">
            <i class="el-icon-time"></i>
            <span style="margin-left: 5px">{{ timeFormat.format(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template slot-scope="scope">
            <el-button v-if="checkCGExist(scope.row) == false" @click="groupApply(scope.row)" type="text" size="small">申请加入</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="cg-search-more" v-show="cgSMoreVisible">
        <el-button type="text" @click="cgSearchMore($event)">查看更多</el-button>
      </div>
      <div class="cg-apply" v-show="cgApplyVisible">
        <el-input
          type="textarea"
          placeholder="请说明申请理由"
          v-model="descript"
          maxlength="100"
          show-word-limit >
        </el-input>
        <el-button @click="cgApplyVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitCGApply">确 定</el-button>
      </div>
    </el-dialog>
    
    <!-- 用户列表右键菜单 -->
    <div
      id="right-key-menu" 
      class="right-key-menu" 
      draggable="false"
      v-if="userInfo&&userInfo.userType!=0">
      <el-menu class="el-menu-demo" mode="vertical" >
        <el-menu-item index="1-1" @click="rmkClick(1)">加入黑名单</el-menu-item>
        <el-menu-item index="1-2" @click="rmkClick(2)">解除黑名单</el-menu-item>
        <el-menu-item index="1-3" @click="rmkClick(3)">禁止消息提示</el-menu-item>
        <el-menu-item index="1-4" @click="rmkClick(4)">允许消息提示</el-menu-item>
      </el-menu>
    </div>
  </div>
</template>

<script>
  import ChatUserList from './js/right_drawer.js'

  export default ChatUserList

</script>

<style scoped>
  @import url('./css/right_drawer.css');
  @import "./css/user_style.css";
</style>