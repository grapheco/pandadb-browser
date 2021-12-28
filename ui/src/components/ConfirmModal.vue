<template>
  <div class="confirm-modal">
    <div class="inner-warp">
      <div class="title">
        {{ title || "Operation tips" }}<slot class="title"></slot>
      </div>
      <div class="content">
        <div v-html="content"></div>
        <slot v-if="!content"></slot>
      </div>
      <div class="footer">
        <span v-if="!type" @click="handleConcel">Cancel</span>
        <span type="primary" @click="handleOnOk">Ok</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "confirm-modal",
  // data() {
  //   return {
  //     isOpen: false,
  //   };
  // },
  props: ["title", "content", "type"],
  // model: {
  //   prop: "value",
  //   event: "change",
  // },
  // watch: {
  //   isOpen() {
  //     this.$emit("change", this.isOpen);
  //   },
  //   value: {
  //     immediate: true,
  //     handler() {
  //       if (typeof this.value === "boolean") {
  //         this.isOpen = this.value;
  //       } else {
  //         this.isOpen = true;
  //       }
  //     },
  //   },
  // },
  methods: {
    handleConcel() {
      // if (this.callBack) this.callBack(false);
      this.$emit("on-cancel");
      // this.isOpen = false;
    },
    handleOnOk() {
      // if (this.callBack) this.callBack(true);
      this.$emit("on-ok");
      // this.isOpen = false;
    },
  },
};
</script>

<style lang="scss" scoped>
// 确认弹出框

.confirm-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
  .inner-warp {
    width: 520px !important;
    padding: 30px;
    box-shadow: 0 0 15px 0px #00000038;
    margin: auto;
    margin-top: 200px;
    background-color: #fff;
  }
  .title {
    text-align: center;
    padding: 20px;
    color: #fd8300;
    font-size: 20px;
    font-weight: 600;
  }
  .content {
    padding: 20px;
    text-align: center;
    font-size: 16px;
  }
  .footer {
    display: flex;
    justify-content: center;
    padding: 20px;
    
    span {
      margin: 0 10px;
      width: 100px;
      text-align: center;
      padding: 10px;
      border: 1px solid #ccc;
      cursor: pointer;
      font-size: 13px;
      border-radius: 3px;
    }
  }
}
</style>
