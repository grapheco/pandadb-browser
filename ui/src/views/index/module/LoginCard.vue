<template>
  <div class="logincard">
    <div class="graph-top">
      <label>:server connect</label>
      <ul>
        <!-- <li>
          <i class="fa fa-close"></i>
        </li>
        -->
        <li @click="isStretch = !isStretch">
          <i
            :class="`fa ${isStretch ? 'fa-chevron-down' : 'fa-chevron-up'}`"
          ></i>
        </li>
      </ul>
    </div>
    <div class="graph-main" v-if="isStretch">
      <div class="left">
        <h1>Connect to PandaDB</h1>
        <p>Database access requires an authenticated connection.</p>
      </div>
      <div class="right">
        <ul>
          <li>
            <label>Connect URL</label>
            <input type="text" v-model="url" />
          </li>
          <li>
            <label>Username</label>
            <input type="text" v-model="username" />
          </li>
          <li>
            <label>Password</label>
            <input type="text" v-model="password" />
          </li>
        </ul>
        <button @click="handleButton(false)">
          <i v-if="loading" class="fa fa-circle-o-notch"></i> Connect
        </button>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: "LoginCard",
  data() {
    return {
      isStretch: true,
      loading: false,
      url: "",
      username: "",
      password: "",
      // url: "panda://10.0.82.148:9989",
      // username: "z",
      // password: "z",
    };
  },
  methods: {
    handleButton(params) {
      if (!params) {
        this.loading = true;
      }

      let data = params || {
        pandadbUrl: this.url,
        username: this.username,
        password: this.password,
      };
      this.$axios
        .post("/pandadb/browser/login", this.$qs.stringify(data))
        .then((res) => {
          if (res.data.error) {
            this.$mitt.emit("confirm-modal", {
              isOpen: true,
              content: res.data.error,
            });
          } else {
            data = { ...res.data, ...data };
            sessionStorage.setItem("login-data", JSON.stringify(data));
            this.loading = false;
            this.$emit("on-login", data);
          }
        })
        .catch((error) => {
          console.log(error);
          this.loading = false;
          this.$mitt.emit("confirm-modal", {
            isOpen: true,
            content: error,
          });
        });
      // this.$emit("on-login", {
      //   pandadbUrl: this.url,
      //   username: this.username,
      //   password: this.password,
      // });
    },
  },
  // created() {
  //   console.log();
  // },
};
</script>
<style lang="scss" scoped>
.logincard {
  // padding: 20px;
  background-color: #f9fbfd;
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
    label {
      flex: 1;
      padding-left: 30px;
      display: flex;
      align-items: center;
      font-family: Inconsolata, Monaco, "Lucida Console", Courier, monospace;
      color: rgb(113, 113, 114);
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
    height: calc(100% - 55px);
    // border: 1px solid #eee;
    border-top: 0;
    background-color: #fff;
    padding: 80px;
    .left {
      width: 360px;
      margin-right: 80px;
    }
    .right {
      flex: 1;
      ul {
        li {
          display: flex;
          flex-direction: column;
          margin-bottom: 20px;
          label {
            margin-bottom: 5px;
            font-weight: 600;
          }
          input {
            width: 70%;
            min-width: 350px;
            outline-style: none;
            border: 1px solid #ccc;
            border-radius: 3px;
            padding: 6px 10px;
            font-size: 18px;
          }
        }
      }
      button {
        border: 1px solid #ccc;
        background-color: #fff;
        padding: 10px 20px;
        cursor: pointer;
        border-radius: 3px;
        .fa-circle-o-notch {
          font-size: 12px;
          color: #aaa;
        }
      }
    }
  }
}
</style>
