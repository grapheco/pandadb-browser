<template>
  <div class="table-warp">
    <ul>
      <li v-for="(item, i) in content" :key="i + '-pre'">
        <p>[</p>
        <pre v-if="item[0]">{{ item[0] }}</pre>
        <pre v-if="item[1]">{{ item[1] }}</pre>
        <pre v-if="item[2]">{{ item[2] }}</pre>
        <p>]</p>
      </li>
    </ul>
  </div>
</template>
<script>
export default {
  data() {
    return {
      content: [["{}", "{}", "{}"]],
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

   this.content = content
  },
  methods: {
    handleData(data) {
      // let { links, nodes } = data;
      // let newArrData = [];
      // // console.log(data);
      // if (links && links.length) {
      //   links.forEach((link) => {
      //     let source = {},
      //       target = {};
      //     nodes.forEach((item) => {
      //       if (link.target === item.id) {
      //         target = item;
      //       }
      //       if (link.source === item.id) {
      //         source = item;
      //       }
      //     });
      //     newArrData.push([source, link, target]);
      //   });
      // } else {
      //   nodes.forEach((item) => {
      //     newArrData.push(["", item, ""]);
      //   });
      // }
      this.content = newArrData;
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
    color: rgb(113, 113, 114);
    font-size: 13px;
    li {
      border-bottom: 1px solid #eee;
      margin-bottom: 10px;
      padding-bottom: 10px;
      pre {
        padding: 10px;
        border-radius: 5px;
        padding-left: 15px;
        background: rgba(238, 238, 238, 0.507);
        white-space: pre-wrap; /* css-3 */
        white-space: -moz-pre-wrap; /* Mozilla, since 1999 */
        white-space: -pre-wrap; /* Opera 4-6 */
        white-space: -o-pre-wrap; /* Opera 7 */
        word-wrap: break-word; /* Internet Explorer 5.5+ */
        overflow: auto;
        word-break: break-all;
        word-wrap: break-word;
      }
    }
  }
  overflow-x: auto;
}
</style>
