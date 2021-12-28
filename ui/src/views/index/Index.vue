<template>
  <div class="pdb-index">
    <aside :style="{ width: isExtend ? '300px' : '62px' }">
      <menu>
        <ul class="up">
          <li
            @click="handleChangeMenu('database')"
            :class="{ active: active == 'database' }"
          >
            <i class="fa fa-database"></i>
          </li>
          <li
            @click="handleChangeMenu('gears')"
            :class="{ active: active == 'gears' }"
          >
            <i class="fa fa-gears"></i>
          </li>
        </ul>
        <ul class="down">
          <li></li>
        </ul>
      </menu>
      <NodeRelations v-if="active == 'database'"></NodeRelations>
      <SetUp v-if="active == 'gears'"></SetUp>
    </aside>
    <section>
      <ContentSection></ContentSection>
    </section>
  </div>
</template>
<script>
import NodeRelations from "./module/NodeRelations.vue";
import SetUp from "./module/SetUp.vue";
import ContentSection from "./module/ContentSection.vue";
export default {
  components: { NodeRelations, SetUp, ContentSection },
  data() {
    return {
      active: "database",
      isExtend: true,
      // asideData: {
      //   nodes: [
      //     {
      //       id: 0,
      //       label: "asfsf",
      //     },
      //     {
      //       id: 1,
      //       label: "asfsf",
      //     },
      //   ],
      // },
    };
  },
  // watch: {
  //   isFullscreen() {},
  // },
  // created() {
  //   this.$axios.post("http://10.0.82.142:9632/pandadb/browser/login",this.$qs.stringify({
  //     pandadbUrl:'',
  //     username:'',
  //     password:'', 
  //   })).then((res) => {
  //     console.log(res.data);
  //   });
  // },
  methods: {
    handleChangeMenu(active) {
      if (this.active == active) {
        this.isExtend = !this.isExtend;
      } else {
        this.isExtend = true;
      }
      this.active = active;
    },
  },
};
</script>
<style lang="scss" scoped>
.pdb-index {
  display: flex;
  height: 100vh;
  aside {
    display: flex;
    // width: 300px;
    // height: 100%;
    border-right: 1px solid #eee;
    background-color: rgb(48, 51, 58);
    // overflow-y: auto;
    padding: 0;
    transition: width .3s;
    menu {
      padding: 0;
      margin: 0;
      background-color: rgb(76, 73, 87);
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      width: 62px;
      ul {
        li {
          padding: 20px;
          cursor: pointer;
          border-bottom: 1px solid rgb(93, 99, 112);
          color: rgb(121, 121, 121);
          i {
            font-size: 20px;
          }
          &.active {
            background-color: rgb(48, 51, 58);
            color: rgb(76, 217, 80);
          }
        }
      }
      // .up{
      //   li{

      //   }
      // }
    }
    >div{
      // flex: 1;
      width: 200px;
      min-width: 200px;
    }
  }
  section {
    flex: 1;
    background-color: rgb(210, 213, 218);
    width: calc(100vw - 300px);
    height: 100%;
  }
}
</style>
