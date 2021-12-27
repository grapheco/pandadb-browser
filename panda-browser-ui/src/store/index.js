import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    config: {
      nodeRadius: 20,
      nodeNumber: 100,
      rowsSize: 10,
      minCollision: 60,
      historySize: 5,
      scaleExtent: [0.3, 5],
    }
  },
  mutations: {
    setConfig(state, data) { 
      state.config = data
    }
  },
  actions: {
  },
  modules: {
  }
})
