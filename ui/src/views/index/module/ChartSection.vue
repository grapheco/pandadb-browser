<template>
  <div class="chart-section" :class="{ 'full-screen': isFullScreen }">
    <div class="graph-top">
      <label @click="handleOnCypherClick(data.cypher)"
        ><span>{{ data.cypher }}</span></label
      >
      <ul>
        <li @click="$emit('on-close')" v-if="length > 1">
          <i class="fa fa-close"></i>
        </li>
        <!-- <li>
              <i class="fa fa-thumb-tack"></i>
            </li> -->
        <li @click="handlefaDownload">
          <i class="fa fa-download"></i>
        </li>
        <li @click="handleRefresh">
          <i class="fa fa-refresh"></i>
        </li>
        <!-- <li>
              <i class="fa fa-chevron-down"></i>
            </li> -->
        <li @click="isFullScreen = !isFullScreen">
          <i class="fa fa-arrows-alt"></i>
        </li>
        <li @click="isStretch = !isStretch">
          <i :class="`fa ${isStretch ? 'fa-chevron-down' : 'fa-chevron-up'}`"></i>
        </li>
      </ul>
    </div>
    <div class="graph-main" :class="{ stretch: isStretch }">
      <div class="tab-warp">
        <ul>
          <li @click="handleClickTab('Graph')" :class="{ active: active == 'Graph' }">
            <i></i><span>Graph</span>
          </li>
          <li @click="handleClickTab('Table')" :class="{ active: active == 'Table' }">
            <i></i><span>Table</span>
          </li>
          <li @click="handleClickTab('Text')" :class="{ active: active == 'Text' }">
            <i></i><span>Text</span>
          </li>
          <li @click="handleClickTab('Code')" :class="{ active: active == 'Code' }">
            <i></i><span>Code</span>
          </li>
        </ul>
      </div>
      <div class="content-warp">
        <GraphChart ref="graphChart" :data="data.data" v-if="active == 'Graph'" />
        <TableText :data="data.data" v-else-if="active == 'Table'" />
        <TextModul :data="data.data" v-else-if="active == 'Text'" />
        <JsonViewer :data="data.data" v-show="active == 'Code'" />
      </div>
    </div>
  </div>
</template>
<script>
import GraphChart from "./GraphChart.vue";
import TableText from "./TableText.vue";
import TextModul from "./TextModul.vue";
import JsonViewer from "./JsonViewer.vue"; 
import exportCsv from "@/utils/csv.js";
export default {
  name: "ChartSection",
  components: { GraphChart, TableText, TextModul, JsonViewer },
  data() {
    return {
      active: "Graph",
      isStretch: true,
      isFullScreen: false, 
    };
  },
  props: ["data", "length"],
  methods: {
    handleOnCypherClick(value) {
      this.$emit("on-cypher", value);
    },
    handleClickTab(active) {
      this.active = active;
    },
    handlefaDownload() {
      let { columns, mapData } = this.data.data;

      let content = [];
      mapData.forEach((item) => {
        let obj = {};
        columns.forEach((key) => {
          obj[key] = item[key] && item[key].data ? JSON.stringify(item[key].data) : null;
        });
        content.push(obj);
      });

      let headers = [];
      columns.forEach((key) => {
        headers.push({
          column: key,
          title: key, //表头标题
        });
      });

      exportCsv(headers, content);
    },
    handleRefresh() {
      let graphChart = this.$refs.graphChart;
      if (graphChart) graphChart.initPdbd3();
    },
  },
  created() {
    console.log();
  },
};
</script>
<style lang="scss" scoped>
.chart-section {
  // height: 100%;
  //   opacity: 0;
  margin-bottom: 20px;
  .graph-top {
    display: flex;
    position: relative;
    height: 32px;
    border: 1px solid #eee;
    // border-top: 0;
    background-color: #fff;
    // border-bottom: 0;
    font-size: 12px;
    user-select: none;
    transition: all 0.3s ease-out;
    label {
      flex: 1;
      padding-left: 30px;
      display: flex;

      align-items: center;
      font-family: Inconsolata, Monaco, "Lucida Console", Courier, monospace;
      color: rgb(113, 113, 114);
      &:hover {
        span {
          cursor: pointer;
          border-bottom: 1px dotted rgb(182, 173, 173);
          // padding-bottom: 2px;
        }
      }
      &::before {
        content: "$";
        display: block;
        position: absolute;
        left: 15px;
        top: 10px;
        font-weight: 600;
      }
    }
    ul {
      display: flex;
      align-items: center;
      li {
        display: flex;
        align-items: center;
        justify-content: center;
        height: 100%;
        border-left: 1px solid #eee;
        width: 32px;
        cursor: pointer;
        color: #a5abb6;
        i {
          font-size: 16px;
        }
        &:hover {
          background: #a5abb6;
          color: #fff;
        }
      }
    }
  }
  .graph-main {
    display: flex;
    height: 0;
    // border: 1px solid #eee;
    overflow: hidden;
    border-top: 0;
    background-color: #fff;
    // background-color: rgb(248, 249, 251);
    //  transition: all 0.3s;
    transition: height 0.2s;
    &.stretch {
      height: calc(100vh - 160px);
    }
    .tab-warp {
      border-right: 1px solid #eee;
      background-color: rgb(248, 249, 251);
      ul {
        width: 55px;
        li {
          padding: 10px 0;
          border-bottom: 1px solid #ddd;
          text-align: center;
          cursor: pointer;
          i {
            display: block;
            width: 25px;
            height: 25px;
            margin: 0 15px;
          }
          span {
            font-size: 12px;
          }
          &:nth-child(1) {
            i {
              background: url("../../../assets/atlas/Graph-1.png") center center no-repeat;
            }
            &:hover,
            &.active {
              background: #ff4b37;
              i {
                background: url("../../../assets/atlas/Graph-2.png") center center
                  no-repeat;
              }
              span {
                color: #efefef;
              }
            }
          }

          &:nth-child(2) {
            i {
              background: url("../../../assets/atlas/table-1.png") center center no-repeat;
            }
            &:hover,
            &.active {
              background: #ff4b37;
              i {
                background: url("../../../assets/atlas/table-2.png") center center
                  no-repeat;
              }
              span {
                color: #efefef;
              }
            }
          }

          &:nth-child(3) {
            i {
              background: url("../../../assets/atlas/txt-1.png") center center no-repeat;
            }
            &:hover,
            &.active {
              background: #ff4b37;
              i {
                background: url("../../../assets/atlas/txt-2.png") center center no-repeat;
              }
              span {
                color: #efefef;
              }
            }
          }

          &:nth-child(4) {
            i {
              background: url("../../../assets/atlas/code-1.png") center center no-repeat;
            }
            &:hover,
            &.active {
              background: #ff4b37;
              i {
                background: url("../../../assets/atlas/code-2.png") center center
                  no-repeat;
              }
              span {
                color: #efefef;
              }
            }
          }
        }
      }
    }
    .content-warp {
      width: calc(100% - 55px);
      .header {
        border-bottom: 1px solid #eee;
        padding: 10px 20px;
      }
    }
  }
}
.full-screen {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  top: 0;
  z-index: 20;
  background-color: #fff;
  height: 100% !important;
  .graph-main {
    height: calc(100% - 36px) !important;
  }
}
</style>
