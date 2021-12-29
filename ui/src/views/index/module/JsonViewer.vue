<template>
  <div class="json-viewer">
    <div class="inner">
      <div class="cord">
        <div class="title">Server address</div>
        <div class="json-document text">{{ loginData.pandadbUrl }}</div>
      </div>
      <!-- <div class="cord">
        <div class="title">Query</div>
        <div class="json-document text">
          {{ data.cypher }}
        </div>
      </div> -->
      <div class="cord">
        <div class="title">Response</div>
        <div ref="jsonRenderer1"></div>
      </div>
    </div>
  </div>
</template>
<script>
import $ from "@/utils/json-viewer.js";
export default {
  data() {
    return {
      loginData: {},
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
  mounted() {
    let loginData = sessionStorage.getItem("login-data");
    if (loginData) {
      this.loginData = JSON.parse(loginData);
    }
    this.handleData();
  },
  methods: {
    handleData() {
      $(this.$refs.jsonRenderer).jsonViewer(this.data, {
        collapsed: true,
        withQuotes: true,
        rootCollapsable: true,
        withLinks: true,
      });
      $(this.$refs.jsonRenderer1).jsonViewer(this.data, {
        collapsed: true,
        withQuotes: true,
        rootCollapsable: true,
        withLinks: true,
      });
    },
  },
};
</script>
<style lang="scss">
.json-viewer {
  height: 100%;
  overflow-y: auto;
  /* Root element */
  font-size: 13px;
  .inner {
    margin: 20px;
    border: 1px solid #eee;
    border-bottom: 0;
    .cord {
      display: flex;
      border-bottom: 1px solid #eee;
      .title {
        font-size: 14px;
        font-weight: 600;
        color: #666;
        padding: 10px 10px;
        border-right: 1px solid #eee;
        width: 120px;
      }
    }
  }
  .text {
    color: #aaa;
  }
  .json-document {
    padding: 0rem 2rem;
    margin-top: 10px;
    // border-bottom: 1px solid #eee;
    padding-bottom: 10px;
  }
  li {
    margin: 5px 0;
  }
  /* Syntax highlighting for JSON objects */
  ul.json-dict,
  ol.json-array {
    list-style-type: none;
    margin: 0 0 0 1px;
    border-left: 1px dotted #ccc;
    padding-left: 2rem;
  }
  .json-string {
    color: #0b7500;
  }
  .json-literal {
    color: #1a01cc;
    font-weight: bold;
  }

  /* Toggle button */
  a.json-toggle {
    position: relative;
    color: inherit;
    text-decoration: none;
  }
  a.json-toggle:focus {
    outline: none;
  }
  a.json-toggle:before {
    font-size: 0.1rem;
    color: #c0c0c0;
    content: "\25BC"; /* down arrow */
    position: absolute;
    display: inline-block;
    width: 1rem;
    text-align: center;
    line-height: 1rem;
    left: -1.2rem;
  }
  a.json-toggle:hover:before {
    color: #aaa;
  }
  a.json-toggle.collapsed:before {
    /* Use rotated down arrow, prevents right arrow appearing smaller than down arrow in some browsers */
    transform: rotate(-90deg);
  }

  /* Collapsable placeholder links */
  a.json-placeholder {
    color: #aaa;
    padding: 0 1rem;
    text-decoration: none;
  }
  a.json-placeholder:hover {
    text-decoration: underline;
  }
}
</style>
