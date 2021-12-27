<template>
  <div class="table-warp">
    <div class="inner">
      <table border="0" cellpadding="0" cellspacing="0">
        <tr>
          <th v-for="item in columns" :key="item.key">{{ item.title }}</th>
        </tr>
        <tr v-for="(row, m) in content" :key="m">
          <td v-for="(item, n) in columns" :key="n + '_' + m">
            <!-- <div v-if="!row[item.key]" class="td-inner">null</div> -->
            <div v-if="row[item.key].type == 'array'" class="td-inner">
              <p>[</p>
              <pre>{{ row[item.key].data[0] }}</pre>
              <pre>{{ row[item.key].data[1] }}</pre>
              <pre>{{ row[item.key].data[2] }}</pre>
              <p>]</p>
            </div>
            <div v-else-if="row[item.key].type == 'object'" class="td-inner">
              <pre>{{ row[item.key].data }}</pre>
            </div>
            <div v-else-if="row[item.key].type == 'string'" class="td-inner">
              {{ row[item.key].data }}
            </div>
            <div v-else class="td-inner">{{ row[item.key].data }}</div>
          </td>
        </tr>
      </table>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      content: [],
      columns: [
        {
          title: "Name",
          key: "name",
        },
      ],
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
    let { mapData, columns } = this.data;
    columns = columns || [];
    let newColumns = [];
    columns.forEach((key) => {
      newColumns.push({
        title: key,
        key: key,
      });
    });

    this.columns = newColumns;
    this.content = mapData;
  },
};
</script>
<style lang="scss" scoped>
.table-warp {
  height: 100%;
  overflow: auto;
  .inner {
    padding: 20px;
    color: rgb(113, 113, 114);
    font-size: 13px;
    table {
      border: 1px solid #eee;
      border-bottom: 0;
      // padding: 10px;
       font-family: monospace, monospace;
      tr {
        // border-bottom: 1px solid #eee;
        margin-bottom: 10px;
        padding-bottom: 10px;
        th {
          border-bottom: 1px solid #eee;
          padding: 10px;
          text-align: start;
          padding-left: 13px;
        }
        td {
          padding: 10px;
          border-bottom: 1px solid #eee;
        }
        pre {
          padding: 10px;
          margin: 0;
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
  }
}
</style>
