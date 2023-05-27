<template>
  <div :id='"bbMessage_"+userCode' >
    <div speech-bubble pleft :class="leftType" :id="userCode" :ref="userCode" :style="{borderTop:borderTop}" >
      <div class="title">
        <span class="message-in" v-html="msg"></span>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'bbMessage',
    props: ['msg', 'userCode', 'createTime', 'borderTop', 'leftType']
  }
</script>

<style scoped>
  @import url('./message.css');

  .message {
    display: inline-block;
  }
</style>