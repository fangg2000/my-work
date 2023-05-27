<template>
  <div id="app" >
    <!-- <svg xmlns="http://www.w3.org/2000/svg" style="width:100%;height:100%;background:linear-gradient(#135,#fbc,#135)">
      <filter id="filter">
          <feTurbulence type="fractalNoise" baseFrequency=".005 0" numOctaves="5"/>
          <feDisplacementMap in="SourceAlpha" scale="99"/>
          <feColorMatrix values="0 0 0 0 .01
                                 0 0 0 0 .02
                                 0 0 0 0 .02
                                 0 0 0 -1 1"/>
      </filter>
      <use href="#e" y="-100%" transform="scale(1 -1)" filter="blur(3px)"/>
      <ellipse id="e" cx="50%" rx="63%" ry="43%" filter="url(#filter)"/>
    </svg> -->
    <router-view />
  </div>
</template>

<script>
  // import '@/components/bg.js'

export default {
  name: 'App',
  data () {
    return {
      
    }
  }
}
</script>

<style>
  #app {
    margin: 0px;
    padding: 0px;
    width: 100%;
    height: 100%;
  }

  .b-design {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: -1
  }

  body {
    overflow: hidden;
    margin: 0px;
    padding: 0px;
    width: 100%;
    height: 100%;
  }
</style>
