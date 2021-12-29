<template>
  <div class="home">
    <!-- <img alt="Vue logo" src="../assets/logo.png"> -->
    <!-- <HelloWorld msg="Welcome to Your Vue.js App"/> -->
    <router-view />
    <ConfirmModal
      v-if="config.isOpen"
      @on-cancel="handleOnCancel"
      @on-ok="handleOnOk"
      :title="config.title"
      :content="config.content"
      :type="config.type"
    />
  </div>
</template>
 <script>
import ConfirmModal from "@/components/ConfirmModal";
export default {
  components: { ConfirmModal },
  data() {
    return {
      // isOpen: true,
      config: { isOpen: false, onCancel: () => {}, onOk: () => {} },
    };
  },
  created() {
    this.$mitt.on("confirm-modal", this.setConfirmModal);
  },
  methods: {
    handleOnOk() {
      this.config.isOpen = false;
      this.config.onOk();
    },
    handleOnCancel() {
      this.config.isOpen = false;
      this.config.onCancel();
    },
    setConfirmModal(config = {}) {
      this.config = config;

      // 使用方法
      // this.$mitt.emit("confirm-modal", {
      //   type: "info",
      //   content: "该操作将会删除此空间，空间数据无法恢复，确定删除吗？",
      // });
      // this.$mitt.emit("confirm-modal", {
      //   isOpen: true,
      //   content: "该操作将会删除此空间，空间数据无法恢复，确定删除吗？",
      //   callBack: (result) => {
      //     if (result) {
      //       this.handleDeleteSpace(itme);
      //     }
      //   },
      // });
    },
  },
  beforeDestroy() {
    this.$mitt.off("confirm-modal", this.setConfirmModal);
  },
};
</script>

