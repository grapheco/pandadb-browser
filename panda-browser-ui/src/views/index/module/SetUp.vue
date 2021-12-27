<template>
  <div class="set-up">
    <div class="card">
      <div class="title">Graph Visualization</div>
      <ul>
        <li>
          <label>Node Size</label>
          <input title="Set node radius size." type="text" v-model="config.nodeRadius" />
        </li>
        <!-- <li>
          <label>Initial Node Display</label>
          <input
            title="Limit number of nodes displayed on first load of the graph visualization."
            type="text"
            v-model="config.nodeNumber"
          />
        </li> -->
        <!-- <li>
          <label>Max Neighbours</label>
          <input title="Limit exploratory queries to this limit." type="text"   v-model="config.initNodeSize"/>
        </li> -->
        <li>
          <label>Min Collision</label>
          <input title="Min Collision" type="text" v-model="config.minCollision" />
        </li>
        <li>
          <label>Min Scale</label>
          <input title="Min Scale" type="text" v-model="config.scaleExtent[0]" />
        </li>
        <li>
          <label>Max Scale</label>
          <input title="Min Collision" type="text" v-model="config.scaleExtent[1]" />
        </li>
        <!-- <li>
          <label>Max Rows</label>
          <input
            title="Max number of rows to render in 'Rows' result view"
            type="text"
            v-model="config.rowsSize"
          />
        </li> -->
        <!-- <li class="checkbox">
          <input
            type="checkbox"
            title="If this is checked, after a cypher query result is retrieved, a second query is executed to fetch relationships between result nodes."
            data-testid="autoComplete"
          />
          <label>Connect result nodes </label>
        </li> -->
      </ul>
    </div>
    <div class="card">
      <div class="title">Result Frames</div>
      <ul>
        <li>
          <label>Max History</label>
          <input
            title="Max number of history entries. When reached, old entries gets retired."
            type="text"
            v-model="config.historySize"
          />
        </li>
      </ul>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      config: this.$store.state.config,
    };
  },
  watch: {
    config: {
      handler(newVal) {
        this.$mitt.emit("on-config", newVal);
        this.$store.commit("setConfig", newVal);
      },
      deep: true,
    },
  },
};
</script>
<style lang="scss" scoped>
.set-up {
  padding: 20px;

  .title {
    font-weight: 600;
    margin: 10px 0;
    color: #fff;
    font-size: 18px;
    border-bottom: 1px solid rgb(66, 70, 80);
    padding-bottom: 10px;
  }
  ul {
    height: calc(100% - 30px);
    overflow-y: auto;
    margin-bottom: 10px;
    // display: flex;
    padding-bottom: 20px;

    li {
      //   background-color: rgb(145, 149, 160);
      // color: #999;
      margin-right: 5px;
      margin-bottom: 5px;
      padding-left: 2px;
      &.checkbox {
        display: flex;
        align-items: center;
        input {
          margin-top: 15px;
          margin-bottom: 5px;
          margin-right: 5px;
        }
      }
      label {
        display: block;
        font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
        font-weight: normal;
        color: rgb(188, 192, 201);
        font-size: 13px;
        margin-top: 15px;
        margin-bottom: 5px;
      }
      input {
        // height: 34px;
        color: rgb(85, 85, 85);
        // font-size: 14px;
        padding: 6px 12px;
        background-color: rgb(255, 255, 255);
        border: 1px solid rgb(204, 204, 204);
        border-radius: 4px;
        // width: 192px;
      }
    }
  }
}
</style>
