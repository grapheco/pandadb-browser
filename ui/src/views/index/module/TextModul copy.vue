<template>
  <div class="table-warp">
    <ul>
      <li v-for="(item, i) in content" :key="i + '-pre'">
        {{ item }}
      </li>
    </ul>
  </div>
</template>
<script>
export default {
  data() {
    return {
      content: [],
    };
  },
  props: ["data"],
  // watch: {
  //   data: {
  //     handler(newVal) {
  //       this.handleData(newVal);
  //     },
  //     immediate: true,
  //   },
  // },
  created() {
    this.handleData();
  },
  methods: {
    handleData() {
      console.log(this.data);
      let { results, errors } = this.data;
      let nodesObj = {};
      let links = [];
      results.forEach((item) => {
        item.data.forEach((item) => {
          item.graph.nodes.forEach((item) => {
            nodesObj[item.id] = item;
          });
          item.graph.relationships.forEach((item) => {
            links.push(item);
          });
        });
      });

      let content = [];
      links.forEach((item) => {
        content.push([
          nodesObj[item.startNode].properties,
          item.properties,
          nodesObj[item.endNode].properties,
        ]);
      });

      this.content = content;
    },
  },
};
</script>
<style lang="scss" scoped>
.table-warp {
  height: 100%;
  overflow-y: auto;
  ul {
    padding: 20px;
    li {
      font-family: monospace, monospace;
      font-size: 13px;
      border-bottom: 1px dashed #999;
      border-left: 1px dashed #999;
      border-right: 1px dashed #999;
      padding-bottom: 6px;
      padding-left: 15px;
      padding-top: 6px;
      &:first-child {
        border-top: 1px dashed #999;
      }
    }
  }
}
</style>
