<template>
  <div class="data-base">
    <div class="inner-warp">
      <div class="query-input">
        <div class="input-warp">
          <span>$</span>
          <!-- <input
            type="text"
            placeholder="例如：MATCH (n) RETURN n LIMIT 25"
            v-model="cypher"
          /> -->
          <CodeMirror ref="myCm" v-model="cypher" class="code"></CodeMirror>
        </div>
        <div class="button-warp">
          <i @click="handleOnQuery({})"></i>
        </div>
      </div>
      <div class="section-warp">
        <template v-if="isLogin">
          <template v-if="loading">
            <transition name="show">
              <div class="not-chart">
                <i
                  v-if="!error"
                  class="fa fa-spinner fa-pulse fa-3x fa-fw margin-bottom"
                ></i>
                <span v-else> {{ error }}</span>
              </div>
            </transition>
          </template>
          <template v-else>
            <ChartSection
              v-for="(item, i) in chartData"
              :key="item.key"
              :length="chartData.length"
              :data="item"
              @on-close="handleOnCloseChart(i)"
              @on-cypher="handleOnCypher"
            />
          </template>
        </template>
        <LoginCard v-else @on-login="handleOnLogin" />
      </div>
    </div>
  </div>
</template>
<script>
import ChartSection from "./ChartSection.vue";
import LoginCard from "./LoginCard.vue";

import CodeMirror from "../../../components/CodeMirror.vue";
// import { codemirror } from "vue-codemirror";
// import "codemirror/theme/ambiance.css"; // 这里引入的是主题样式，根据设置的theme的主题引入，一定要引入！！
// require("codemirror/mode/javascript/javascript"); // 这里引入的模式的js，根据设置的mode引入，一定要引入！！
// import codeMirror from "codemirror";
export default {
  name: "DataBase",
  components: {
    ChartSection,
    LoginCard,
    CodeMirror,
  },
  data() {
    return {
      isLogin: false,
      loading: false,
      key: 1,
      chartData: [],
      loginData: {},
      error: "",
      cypher: "MATCH (n) RETURN n LIMIT 25",
      cmOptions: {
        // value: "",
        mode: "text/javascript",
        theme: "ambiance",
        // readOnly: true,
      },
    };
  },
  mounted() {
    // console.log(_CodeMirror);

    // codeMirror(this.$refs.mycode, {});

    let loginData = sessionStorage.getItem("login-data");
    if (loginData) {
      this.loginData = JSON.parse(loginData);
      this.isLogin = true;
    }
    this.$mitt.on("on-query", this.handleOnQuery);
    this.handleOnQuery({});
  },
  methods: {
    handleOnCypher(val) {
      this.cypher = val;
      // console.log(this.$refs.myCm.coder);
      this.$refs.myCm.coder.setValue(val);
    },
    handleOnQuery({ type, query }) {
      this.loading = true;
      this.error = "";
      if (type == "node") {
        query = `MATCH (n:${query}) RETURN n LIMIT 25`;
      } else if (type == "link") {
        // query = `MATCH p=()-[r:${query}]->() RETURN p LIMIT 25`;
        query = `MATCH (start)-[r:${query}]->(end) RETURN start,r,end LIMIT 25`;
      } else {
        query = this.cypher;
      }
      this.cypher = query;
      this.$axios
        .post(
          "/pandadb/browser/executeCypher",
          this.$qs.stringify({
            pandadbUrl: this.loginData.pandadbUrl,
            username: this.loginData.username,
            password: this.loginData.password,
            cypher: query,
          })
        )
        .then((res) => {
          this.loading = false;
          if (res.data.error) {
            this.error = res.data.error;
          } else {
            this.handleAdd(res.data);
          }
        })
        .catch((error) => {
          this.error = error;
          this.loading = false;
        });
    },
    // editorInit: function () {
    //   // vue2-code-editor/node_modules/
    //   require("brace/ext/language_tools"); //language extension prerequsite...
    //   require("brace/mode/html"); //language
    //   require("brace/mode/javascript");
    //   require("brace/mode/less");
    //   require("brace/theme/monokai");
    //   require("brace/snippets/javascript"); //snippet
    // },
    handleOnLogin(data) {
      this.loginData = data;
      this.handleOnQuery({});
      this.isLogin = true;
      this.$mitt.emit("on-login", data);
    },
    handleOnCloseChart(index) {
      this.chartData.splice(index, 1);
    },
    handleAdd(data) {
      this.key = this.key + 1;
      this.chartData.unshift({ data, key: this.key, cypher: this.cypher });

      if (this.chartData.length > this.$store.state.config.historySize) {
        this.handleOnCloseChart(this.chartData.length - 1);
      }
    },
  },
  beforeDestroy() {
    this.$mitt.off("on-query", this.handleOnQuery);
  },
};
</script>
<style lang="scss" scoped>
.data-base {
  padding: 20px;
  height: calc(100% - 40px);
  .inner-warp {
    height: 100%;
    .query-input {
      background: #efefef;
      padding: 10px;
      display: flex;
      align-items: center;
      margin-bottom: 20px;
      .input-warp {
        flex: 1;
        display: flex;
        align-items: center;
        padding: 0 20px;
        // padding-top: 5px;
        // padding-bottom: 5px;
        background: #fffdee;
        border-radius: 5px;
        > span {
          width: 15px;
          color: #c1bfc1;
        }
        .code {
          flex: 1;
          height: auto;
        }
        input {
          flex: 1;
          border: 0;
          background: transparent;
          outline: none;
        }
      }
      .button-warp {
        i {
          margin-left: 20px;
          display: block;
          cursor: pointer;
          width: 32px;
          height: 32px;
          background: url("../../../assets/atlas/run1.png") center center no-repeat;
          background-size: 70%;
          &:hover {
            background: url("../../../assets/atlas/run2.png") center center no-repeat;
          }
        }
      }
    }
    .section-warp {
      height: calc(100% - 64px);
      overflow-y: auto;
      // &.show {
      //   opacity: 1;
      // }
      .not-chart {
        background-color: #fff;
        height: calc(100% - 90px);
        padding: 30px;
        margin-bottom: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #999;
      }
    }
  }
}
</style>
