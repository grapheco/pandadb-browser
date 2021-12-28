<template>
  <div class="text-modul">
    <div class="inner">
      <table border="0" cellpadding="0" cellspacing="0">
        <tr>
          <th v-for="item in columns" :key="item.key">{{ item.title }}</th>
        </tr>
        <tr v-for="(row, m) in content" :key="m">
          <td v-for="(item, n) in columns" :key="n + '_' + m">
            <div class="td-inner">
              {{ row[item.key].data }}
            </div>
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
.text-modul {
  height: 100%;
  overflow: auto;
  .inner {
    padding: 20px;
    color: rgb(113, 113, 114);
    font-size: 13px;
    table {
      border: 1px dashed #ccc;
      border-bottom: 0;
      font-family: monospace, monospace;
      font-size: 1em;
      tr {
        // border-bottom: 1px dashed #ccc;
        margin-bottom: 10px;
        padding-bottom: 10px;
        th {
          border-bottom: 1px dashed #ccc;
          padding: 10px;
          text-align: start;
          padding-left: 13px;
        }
        td {
          padding: 10px;
          border-bottom: 1px dashed #ccc;
        }
      }
    }
  }
}
</style>
