<template>
  <div class="pdb-aside">
    <div class="card">
      <div class="title">Node Labels</div>
      <ul class="node" v-if="nodes.length">
        <li v-for="item in nodes" :key="item" @click="handleOnClick('node', item)">
          {{ item }}
        </li>
      </ul>
      <div class="not-warp" v-else>Not nodes</div>
    </div>
    <div class="card">
      <div class="title">Relationship Types</div>
      <ul class="link" v-if="links.length">
        <li v-for="item in links" :key="item" @click="handleOnClick('link', item)">
          {{ item }}
        </li>
      </ul>
      <div class="not-warp" v-else>Not relationship</div>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      nodes: [],
      links: [],
    };
  },
  created() {
    this.$mitt.on("on-login", this.handleOnData);
  },
  mounted() {
    this.handleOnData();
  },
  methods: {
    handleOnClick(type, query) {
      this.$mitt.emit("on-query", { type, query });
    },
    handleOnData(data) {
      let loginData = sessionStorage.getItem("login-data");
      if (data) {
        loginData = data;
      } else if (loginData) {
        loginData = JSON.parse(loginData);
      } else {
        loginData = {};
      }
      this.nodes = loginData.nodeLabels || [];
      this.links = loginData.relTypes || [];
    },
  },
  beforeDestroy() {
    this.$mitt.off("on-login", this.handleOnData);
  },
};
</script>
<style lang="scss" scoped>
.pdb-aside {
  padding: 20px;
  .title {
    font-weight: 600;
    margin: 10px 0;
    color: #fff;
    font-size: 18px;
    border-bottom: 1px solid rgb(66, 70, 80);
    padding-bottom: 10px;
  }
  .card {
    .not-warp {
      color: #999;
    }
  }
  ul {
    height: calc(100% - 30px);
    overflow-y: auto;
    margin-bottom: 30px;
    display: flex;
    padding-bottom: 20px;
    &.node {
      li {
        border-radius: 20px;
      }
    }
    &.link {
      li {
        border-radius: 6px;
      }
    }
    li {
      background-color: rgb(145, 149, 160);
      color: rgb(48, 51, 58);
      margin-right: 5px;
      margin-bottom: 5px;
      // margin: 2px 0;
      font-size: 12px;
      padding: 5px 10px;

      cursor: pointer;
      &:hover {
        background: #fff;
      }
    }
  }
}
</style>
