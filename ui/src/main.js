import Vue from 'vue'
import App from './App.vue'
import './registerServiceWorker'
import router from './router'
import store from './store'
import "./style/init.scss" 
import 'font-awesome/css/font-awesome.css';

import axios from 'axios';
import qs from 'qs';
axios.defaults.baseURL = process.env.VUE_APP_URL;
Vue.prototype.$axios = axios
Vue.prototype.$qs = qs

import mitt from 'mitt'; 
Vue.prototype.$mitt = mitt()
Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#pdb')
