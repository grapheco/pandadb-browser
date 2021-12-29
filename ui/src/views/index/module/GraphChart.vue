<template>
  <div class="chart-warp">
    <div class="top-list">
      <div class="node-list">
        <ul>
          <!-- <li>*{{ nodeTotal }}</li> -->
          <li
            v-for="item in columns"
            :key="item.type"
            :style="{ background: item.color }"
          >
            {{ item.type }}({{ item.count }})
          </li>
        </ul>
      </div>
      <div class="link-list">
        <ul>
          <!-- <li>*{{ linkTotal }}</li> -->
          <li v-for="item in linkType" :key="item.type">
            {{ item.type }}({{ item.count }})
          </li>
        </ul>
      </div>
    </div>

    <div
      class="chart"
      @click="isNodeClick = false"
      :style="{ cursor: isZoom ? 'move' : '' }"
    >
      <div ref="pdbd3"></div>
      <!-- <svg class="svg" ref="svg" height="600"></svg> -->

      <div class="search-minu">
        <div
          class="zoom"
          @click="handleOpenZoom"
          :class="{ open: isZoom }"
          v-html="svg.zoomSvg"
        ></div>
        <!-- <i class="fa fa-search-plus" @click="handleSearchPlus"></i>
        <i class="fa fa-search-minus"></i> -->
      </div>
    </div>
    <div class="mouseover-bar" @click="isNodeClick = false">
      <div class="node-warp" v-if="mouseover.eventType">
        <ul>
          <li
            :style="{ background: mouseover.color }"
            :class="`${mouseover.eventType}-label`"
          >
            {{ mouseover.type }}
          </li>
          <li v-for="item in mouseover.params" :key="item.key">
            <span>{{ item.key }}：</span>{{ item.value }}
          </li>
        </ul>
      </div>
      <div v-else>
        <ul>
          <li>
            Displaying {{ mouseover.nodes ? mouseover.nodes.length : 0 }} nodes,
            {{ mouseover.links ? mouseover.links.length : 0 }} relationships.
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
// import initChart from "@/graph/index.js";
import Pdbd3 from "@/graph/pdbd3.js";
import svg from "../../../svg/index";

export default {
  name: "chart",
  data() {
    return {
      svg,
      isZoom: false,
      isNodeClick: false,
      // myEcharts: null,
      linkType: [],
      columns: [],
      mouseover: {
        type: "",
        label: "",
        data: {},
      },
      content: {
        nodes: [],
        links: [],
      },
    };
  },
  props: ["data"],
  created() {
    this.option = {
      // highlight: [
      //   {
      //     class: "Project",
      //     property: "name",
      //     value: "pdbd3",
      //   },
      //   {
      //     class: "User",
      //     property: "userId",
      //     value: "eisman",
      //   },
      // ],
      name: {
        show: true,
      },
      infoPanel: false,
      pdbData: JSON.parse(JSON.stringify(this.data || "{}")),
      minCollision: 60,
      nodeRadius: 20,

      onNodeDoubleClick: (node) => {
        // console.log(node);
        this.loginData = {};
        let loginData = sessionStorage.getItem("login-data");
        if (loginData) {
          this.loginData = JSON.parse(loginData);
        }

        // let { nodes, relationships } = this.Pdbd3.getGraphData();
        // console.log(nodes);
        this.$axios
          .post(
            "/pandadb/browser/getOtherRelationByNodeId",
            this.$qs.stringify({
              pandadbUrl: this.loginData.pandadbUrl,
              username: this.loginData.username,
              password: this.loginData.password,
              nodeId: node.id,
            })
          )
          .then((res) => {
            // console.log(isEx);

            res.data.results.forEach((item) => {
              item.data.forEach((each) => {
                let newNodes = [];
                let relationships = [];
                each.graph.nodes.forEach((nodeItem) => {
                  let isEx = this.content.nodes.find(
                    (node) => node.id == nodeItem.id
                  );
                  if (!isEx) {
                    this.content.nodes.push(nodeItem);
                    newNodes.push(nodeItem);
                  }
                });
                each.graph.nodes = newNodes;
                each.graph.relationships.forEach((linkItem) => {
                  let isEx = this.content.relationships.find((link) => {
                    // return (
                    //   (link.endNode == linkItem.endNode &&
                    //     link.startNode == linkItem.startNode) ||
                    //   (link.endNode == linkItem.startNode &&
                    //     link.startNode == linkItem.endNode)
                    // );
                    return link.id == linkItem.id;
                  });
                  if (!isEx) {
                    this.content.relationships.push(linkItem);
                    relationships.push(linkItem);
                  }
                });
                each.graph.relationships = relationships;
                // relationships = [...relationships, ...each.graph.relationships];
              });
            });
            // console.log({ nodes, relationships });
            // console.log(res.data);
            this.Pdbd3.updateWithPdbData(res.data);
          });

        // // var maxNodes = 5,
        // let data = this.Pdbd3.randomD3Data(node, 5);
        // console.log(data);
        // this.Pdbd3.updateWithD3Data({});
      },
      onNodeClick: (d) => {
        this.isNodeClick = false;
        d.eventType = "node";
        this.onMouseEnter(d);
        this.isNodeClick = true;
      },
      onNodeMouseLeave: (d) => {
        d.eventType = "node";
        this.onMouseLeave(d);
      },
      onNodeMouseEnter: (d) => {
        d.eventType = "node";
        this.onMouseEnter(d);
      },
      onLinkMouseLeave: (d) => {
        d.eventType = "link";
        this.onMouseLeave(d);
      },
      onLinkMouseEnter: (d) => {
        d.eventType = "link";
        this.onMouseEnter(d);
      },
      zoomFit: false,
    };
    this.onMouseLeave();

    this.$mitt.on("on-config", this.initPdbd3);
  },
  mounted() {
    if (this.data) {
      this.Pdbd3 = new Pdbd3(this.$refs.pdbd3, this.option);
      this.content = this.Pdbd3.getGraphData();
      this.handleColumnsData(this.content || {});
      this.handleLinkTypeData(this.content || {});
      this.onMouseLeave();
    }
  },
  methods: {
    initPdbd3(option) {
      option = option || this.$store.state.config;
      this.Pdbd3.init(this.$refs.pdbd3, {
        ...this.option,
        isZoom: this.isZoom,
        ...option,
      });
    },
    handleOpenZoom(e) {
      this.isZoom = !this.isZoom;
      this.initPdbd3();
    },
    onMouseEnter(d) {
      if (!this.isNodeClick) {
        let params = [];
        if (d.id) {
          params = [
            {
              key: "<id>",
              value: d.id,
            },
          ];
        }
        for (const key in d.properties) {
          if (d.properties[key]) {
            params.push({
              key,
              value: JSON.stringify(d.properties[key]),
            });
          }
        }
        this.mouseover = {
          ...d,
          type: d.labels ? d.labels[0] : d.type,
          params,
        };
      }
      // console.log(this.mouseover);
    },
    onMouseLeave() {
      if (!this.isNodeClick) {
        if (this.Pdbd3) {
          let { nodes, relationships } = this.Pdbd3.getGraphData();
          this.mouseover = {
            nodes,
            links: relationships,
          };
        }
      }
    },
    handleColumnsData({ nodes }) {
      let columns = [];
      nodes.forEach((item) => {
        columns.push({
          color: item.color,
          type: item.labels[0],
        });
      });
      let newObj = {};

      columns.forEach((item) => {
        if (newObj[item.type]) {
          newObj[item.type].count = newObj[item.type].count + 1;
        } else {
          newObj[item.type] = { ...item, count: 1 };
        }
      });

      columns = [
        {
          color: "#ff928c",
          count: nodes.length,
          type: "*",
        },
      ];

      for (const key in newObj) {
        columns.push(newObj[key]);
      }
      this.columns = columns;
    },
    handleLinkTypeData({ relationships }) {
      let linkType = [];
      relationships.forEach((item) => {
        linkType.push({
          color: item.color,
          type: item.type,
        });
      });
      let newObj = {};

      linkType.forEach((item) => {
        if (newObj[item.type]) {
          newObj[item.type].count = newObj[item.type].count + 1;
        } else {
          newObj[item.type] = { ...item, count: 1 };
        }
      });

      linkType = [
        {
          color: "#ff928c",
          count: relationships.length,
          type: "*",
        },
      ];

      for (const key in newObj) {
        linkType.push(newObj[key]);
      }
      // console.log(linkType);
      this.linkType = linkType;
    },
  },
  beforeDestroy() {
    this.$mitt.off("on-config", this.initPdbd3);
  },
};
</script>
<style lang="scss">
.svg {
  .node {
    cursor: pointer;
    .cir-bot {
      fill: none;
    }
    &:hover {
      .cir-bot {
        fill: rgba(106, 198, 255, 0.3);
      }
    }
    // .cir-top {

    // }
  }

  /* .link {
} */
}

.pdbd3 {
  .pdbd3-info {
    display: none;
  }
  .pdbd3-graph {
    // border: 1px solid #ddd;
    border-radius: 5px;
  }

  .pdbd3-info {
    font-size: 16px;
    padding: 10px;
    position: absolute;
  }
  .pdbd3-info a {
    border: 1px solid;
    display: inline-block;
    font-size: 14px;
    margin-left: 5px;
    margin-top: 5px;
    padding: 6px 12px;
  }
  .pdbd3-info a.class {
    color: white;
  }
  .pdbd3-info a.property {
    background-color: #fff;
    border-color: #ccc;
    color: #333;
  }
  .pdbd3-info a.btn {
    margin-left: 5px;
    margin-top: 5px;
    opacity: 1;
  }
  .pdbd3-info a.info {
    background-color: #a5abb6;
    border: 1px solid #9aa1ac;
    color: white;
  }

  .node.node-highlighted .ring {
    -ms-filter: "progid:DXImageTransform.Microsoft.Alpha(Opacity=50)";
    filter: alpha(opacity=50);
    opacity: 0.5;
    stroke: #888;
    stroke-width: 12px;
  }
  .node .outline {
    cursor: pointer;
    fill: #a5abb6;
    pointer-events: all;
    stroke: #9aa1ac;
    stroke-width: 2px;
  }
  .node .ring {
    fill: none;
    -ms-filter: "progid:DXImageTransform.Microsoft.Alpha(Opacity=0)";
    filter: alpha(opacity=0);
    opacity: 0;
    stroke: #6ac6ff;
    stroke-width: 8px;
  }
  .node .text.icon {
    font-family: FontAwesome;
  }

  .node.selected .ring,
  .node:hover .ring {
    -ms-filter: "progid:DXImageTransform.Microsoft.Alpha(Opacity=30)";
    filter: alpha(opacity=30);
    opacity: 0.3;
  }

  .relationship {
    cursor: default;
  }
  .relationship line {
    stroke: #aaa;
  }
  .relationship .outline {
    cursor: default;
  }
  .relationship .overlay {
    cursor: default;
    fill: #6ac6ff;
    -ms-filter: "progid:DXImageTransform.Microsoft.Alpha(Opacity=0)";
    filter: alpha(opacity=0);
    opacity: 0;
  }
  .relationship text {
    cursor: default;
  }

  .relationship.selected .overlay,
  .relationship:hover .overlay {
    -ms-filter: "progid:DXImageTransform.Microsoft.Alpha(Opacity=30)";
    filter: alpha(opacity=30);
    opacity: 0.3;
  }

  // svg {
  //   cursor: move;
  // }
}
</style>
<style lang="scss" scoped>
.chart-warp {
  height: 100%;
  position: relative;
  ul {
    display: flex;
    padding: 6px 8px;
    flex-wrap: wrap;
    li {
      padding: 3px 10px;
      font-size: 12px;
      margin-right: 5px;
      line-height: 12px;
      margin-bottom: 2px;
      cursor: pointer;
      color: rgb(113, 113, 114);
    }
  }
  .top-list {
    background: #fff;
    left: 0;
    right: 0;
    position: absolute;
    top: 0;
    z-index: 10;
    ul {
      border-bottom: 1px solid #eee;
    }
  }
  .node-list {
    ul {
      li {
        border-radius: 20px;
        color: #fff;
        // &:nth-child(1) {
        //   background-color: rgb(201, 144, 192);
        //   color: rgb(255, 255, 255);
        // }
        // &:nth-child(2) {
        //   background-color: rgb(86, 148, 128);
        //   color: rgb(255, 255, 255);
        // }
        // &:nth-child(3) {
        //   background-color: rgb(255, 196, 84);
        //   color: rgb(96, 74, 14);
        // }
      }
    }
  }
  .link-list {
    ul {
      li {
        background-color: rgb(165, 171, 182);
        color: rgb(255, 255, 255);
        border-radius: 3px;
      }
    }
  }
  .chart {
    padding-top: 60px;
    height: calc(100% - 60px);
    width: 100%;
    position: relative;
    background-color: rgb(249, 251, 253);
    svg,
    .pdbd3 {
      width: 100%;
      height: 100%;
      user-select: none;
    }
    .search-minu {
      position: absolute;
      right: 30px;
      top: 90px;
      .zoom {
        cursor: pointer;
        fill: #a5abb6;
        svg {
          width: 20px;
          height: 20px;
        }
        &.open {
          fill: #ff4b37;
        }
      }
      // i {
      //   font-size: 20px;
      //   display: flex;
      //   flex-direction: column;
      //   margin: 20px 0;
      //   color: #999;
      //   cursor: pointer;
      // }
    }
  }
  .mouseover-bar {
    position: absolute;
    bottom: 0;
    background: #fff;
    left: 0;
    right: 0;
    z-index: 10;
    ul {
      // width: 100%;
      display: flex;
      flex-wrap: wrap;
      border-top: 1px solid #eee;
      li {
        span:nth-child(1) {
          font-weight: 800;
        }
      }
    }
    .node-label {
      border-radius: 20px;
      color: #fff;
    }
    .link-label {
      border-radius: 3px;
      background-color: #a5abb6;
      color: #fff;
    }
  }
}
</style>
