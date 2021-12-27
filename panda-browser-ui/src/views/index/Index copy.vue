<template>
  <div class="neo4j-index">
    <Row :wrap="false">
      <Col flex="220px" class="aside">
        <div class="top">
          <div class="title">Node</div>
          <ul v-if="asideData.labels && asideData.labels.length">
            <li
              v-for="item in asideData.labels"
              :key="item"
              :class="{ active: asideIndex === item }"
              @click="handleClicksideIndex(item, 'node')"
            >
              {{ item }}
            </li>
          </ul>
          <Spin fix v-else> 暂无数据 </Spin>
        </div>
        <div class="bottom">
          <div class="title">ReloTion</div>
          <ul v-if="asideData.relationshipTypes && asideData.relationshipTypes.length">
            <li
              v-for="item in asideData.relationshipTypes"
              :key="item"
              :class="{ active: asideIndex === item }"
              @click="handleClicksideIndex(item, 'relation')"
            >
              {{ item }}
            </li>
          </ul>
          <Spin fix v-else> 暂无数据 </Spin>
        </div>
      </Col>
      <Col flex="auto" class="content">
        <div class="inner-warp">
          <div class="query-input">
            <div class="inner">
              <span>$</span>
              <input
                type="text"
                v-model="cypher"
                placeholder="例如：MATCH (n:conference) RETURN n.name as namme,n.language LIMIT 25"
              />
            </div>
            <div class="button">
              <i class="fa fa-play-circle" @click="handleExecuteCypher"></i>
            </div>
          </div>
          <div class="table-content" :class="{ fullscreen: isFullscreen }">
            <div class="frame-command">
              <div class="code">
                {{ cypher ? cypher : "暂无查询语句" }}
              </div>
              <div class="but-warp">
                <!-- <i class="fa fa-download"></i> -->
                <!-- <i class="fa fa-refresh"></i> -->
                <i
                  class="fa fa-compress"
                  v-if="isFullscreen"
                  @click="isFullscreen = false"
                ></i>
                <i class="fa fa-expand" v-else @click="isFullscreen = true"></i>
                <!-- <i class="fa fa-close"></i> -->
              </div>
            </div>
            <Row class="frame-body" :wrap="false">
              <Col flex="55px">
                <ul class="aside">
                  <li @click="tabIndex = 1" :class="{ active: tabIndex === 1 }">
                    <i></i>
                    <span>Graph</span>
                  </li>
                  <li @click="tabIndex = 2" :class="{ active: tabIndex === 2 }">
                    <i></i><span>Table</span>
                  </li>
                  <li @click="tabIndex = 3" :class="{ active: tabIndex === 3 }">
                    <i></i><span>Text</span>
                  </li>
                  <li @click="tabIndex = 4" :class="{ active: tabIndex === 4 }">
                    <i></i><span>Code</span>
                  </li>
                </ul>
              </Col>
              <Col flex="auto">
                <GraphChart v-if="tabIndex === 1" :data="chartData" ref="graphChart" />
                <!-- <pre>{{chartData}}</pre> -->
                <TableText v-else-if="tabIndex === 2" :data="chartData" />
                <TextModul v-else-if="tabIndex === 3" :data="chartData" />
                <json-viewer
                  v-else-if="tabIndex === 4"
                  :expand-depth="3"
                  :value="resData"
                ></json-viewer>
                <div class="loading" v-if="loading">
                  <Spin fix>
                    <Icon type="ios-loading" size="40" class="icon-load"></Icon>
                    <!-- <div>加載中，请稍后...</div> -->
                  </Spin>
                </div>
                <!-- <div class="loading"> -->
                <Spin fix v-if="chartData.nodes.length === 0">
                  暂无数据（可尝试选择节点或关系）
                </Spin>
                <!-- </div> -->
              </Col>
            </Row>
          </div>
        </div>
      </Col>
    </Row>
  </div>
</template>
<script>
import {
  getAllLabelAndRelation,
  getNodeData,
  getRelationTypeData,
  getExecuteCypher,
} from "@/api/knowledgeGraph";
import GraphChart from "./module/GraphChart.vue";
import TableText from "./module/TableText.vue";
import TextModul from "./module/TextModul.vue";
export default {
  components: { GraphChart, TableText, TextModul },
  data() {
    return {
      loading: false,
      isFullscreen: false,
      asideData: {},
      asideIndex: "",
      cypher: "",
      tabIndex: 1,
      chartData: { nodes: [] },
      resData: {},
    };
  },
  watch: {
    isFullscreen() {
      if (this.$refs.graphChart) {
        console.log(this.$refs.graphChart);
        this.$nextTick(() => {
          this.$refs.graphChart.resize();
        });
      }
    },
  },
  created() {
    getAllLabelAndRelation(this.$route.query.id).then((res) => {
      if (res.data.code === 200) {
        this.asideData = res.data.data;
      }
    });
  },
  methods: {
    handleExecuteCypher() {
      this.loading = true;
      getExecuteCypher({ mapId: this.$route.query.id, cypher: this.cypher }).then(
        (res) => {
          this.resData = res.data;
          if (res.data.code === 200) {
            this.chartData = this.handleResData(res.data.data.path);
            this.loading = false;
          }
        }
      );
    },

    handleClicksideIndex(item, type) {
      this.asideIndex = item;
      this.loading = true;
      if (type === "relation") {
        getRelationTypeData({ mapId: this.$route.query.id, relationName: item }).then(
          (res) => {
            this.resData = res.data;
            if (res.data.code === 200) {
              this.chartData = this.handleResData(res.data.data.path);
              this.loading = false;
            }
          }
        );
      } else if (type === "node") {
        getNodeData({ mapId: this.$route.query.id, nodeName: item }).then((res) => {
          this.resData = res.data;
          if (res.data.code === 200) {
            this.chartData = this.handleResData(res.data.data.path);
            this.loading = false;
          }
        });
      }
    },
    handleResData(data) {
      return {
        nodes: data.nodes.map((item) => {
          item.id = item.id.toString();
          item.category = data.category.indexOf(item.category);
          return item;
        }),
        links: data.relations.map((item) => {
          return {
            source: item.start.toString(),
            target: item.end.toString(),
            name: item.type,
            type: item.type,
          };
        }),
        categories: data.category.map((item) => {
          return {
            name: item,
          };
        }),
      };
    },
  },
};
</script>
<style lang="scss" scoped>
.neo4j-index {
  height: 100%;
  ::v-deep .ivu-row {
    height: 100%;
    background: #fff;
  }
  .aside {
    border-right: 1px solid #eee;
    height: 100%;
    > div {
      height: 50%;
      padding: 10px 15px;
      position: relative;
      .title {
        font-weight: 600;
        margin-bottom: 10px;
      }
      &.top {
        border-bottom: 1px solid #eee;
      }
      ul {
        height: calc(100% - 30px);
        overflow-y: auto;
        li {
          background: #ffebea;
          margin: 2px 0;
          padding: 3px 5px;
          border-radius: 3px;
          cursor: pointer;

          &.active,
          &:hover {
            background: #f7bab5;
            color: #ff4b37;
          }
        }
      }
    }
  }
  .content {
    flex: 1 !important;
    padding: 10px;
    height: 100%;
    .inner-warp {
      height: 100%;
      border: 1px solid #eee;
      .table-content {
        height: calc(100% - 61px);
      }
      .fullscreen {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        z-index: 9999;
        height: auto !important;
      }
    }
    .query-input {
      background: #efefef;
      padding: 10px;
      display: flex;
      align-items: center;
      .inner {
        flex: 1;
        display: flex;
        align-items: center;
        padding: 10px;
        background: #fffdee;
        border-radius: 5px;
        > span {
          width: 15px;
          color: #c1bfc1;
        }
        input {
          flex: 1;
          border: 0;
          background: transparent;
          outline: none;
        }
      }
      .button {
        width: 60px;
        font-size: 20px;
        text-align: center;
        cursor: pointer;
        &:hover {
          color: #ff4b37;
        }
      }
    }
    .frame-command {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 5px 20px;
      background: #fcfcfc;
      border-bottom: 1px solid #eee;
      //   .code {
      //   }
      .but-warp {
        display: inline-block;
        padding: 5px;
        i {
          margin-left: 20px;
          cursor: pointer;
          &:hover {
            color: #ff4b37;
          }
        }
      }
    }
    .frame-body {
      height: calc(100% - 42px);
      ::v-deep .ivu-col {
        height: 100%;
        overflow-y: auto;
      }
      ul.aside {
        li {
          //   height: 55px;
          padding: 10px 0;
          border-bottom: 1px solid #eee;
          //   display: flex;
          text-align: center;
          cursor: pointer;
          &:last-child {
            border-bottom: 0;
          }
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
              background: url("../../../../assets/images/atlas/Graph-1.png") center center
                no-repeat;
            }
            &:hover,
            &.active {
              background: #ff4b37;
              i {
                background: url("../../../../assets/images/atlas/Graph-2.png") center
                  center no-repeat;
              }
              span {
                color: #efefef;
              }
            }
          }

          &:nth-child(2) {
            i {
              background: url("../../../../assets/images/atlas/table-1.png") center center
                no-repeat;
            }
            &:hover,
            &.active {
              background: #ff4b37;
              i {
                background: url("../../../../assets/images/atlas/table-2.png") center
                  center no-repeat;
              }
              span {
                color: #efefef;
              }
            }
          }

          &:nth-child(3) {
            i {
              background: url("../../../../assets/images/atlas/txt-1.png") center center
                no-repeat;
            }
            &:hover,
            &.active {
              background: #ff4b37;
              i {
                background: url("../../../../assets/images/atlas/txt-2.png") center center
                  no-repeat;
              }
              span {
                color: #efefef;
              }
            }
          }

          &:nth-child(4) {
            i {
              background: url("../../../../assets/images/atlas/code-1.png") center center
                no-repeat;
            }
            &:hover,
            &.active {
              background: #ff4b37;
              i {
                background: url("../../../../assets/images/atlas/code-2.png") center
                  center no-repeat;
              }
              span {
                color: #efefef;
              }
            }
          }
        }
      }
      ::v-deep .jv-container {
        border: 0;
        height: 100%;
        //   overflow-y: auto;
        &.boxed:hover {
          box-shadow: none;
        }
      }
    }
  }
  .loading {
    .icon-load {
      animation: ani-demo-spin 1s linear infinite;
    }
    ::v-deep .ivu-spin {
      color: #999;
      //   color: #ff4b37;
    }
    @keyframes ani-demo-spin {
      from {
        transform: rotate(0deg);
      }
      50% {
        transform: rotate(180deg);
      }
      to {
        transform: rotate(360deg);
      }
    }
  }
}
</style>
