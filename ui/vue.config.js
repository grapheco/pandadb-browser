const CompressionPlugin = require("compression-webpack-plugin")
module.exports = {
  configureWebpack: config => {
    if (process.env.NODE_ENV === 'production') {  
      config.optimization.minimizer[0].options.terserOptions.compress.warnings = false
      config.optimization.minimizer[0].options.terserOptions.compress.drop_console = true
      config.optimization.minimizer[0].options.terserOptions.compress.drop_debugger = true
      config.optimization.minimizer[0].options.terserOptions.compress.pure_funcs = ['console.log']
    }

    if (process.env.NODE_ENV === 'production') {
      return {
        plugins: [
          new CompressionPlugin({
            test: /\.js$|\.html$|\.css/, 
            threshold: 10240, 
            deleteOriginalAssets: false  
          })
        ]
      }
    }
  },
  productionSourceMap: process.env.NODE_ENV === 'production' ? false : true,
  devServer: {
    open: true,
    // port: 8021,
    proxy: {
      '/pdb': {
        target: 'http://10.0.82.142:9632',
        changeOrigin: true,
        // ws: false, // 是否启用websockets
        // secure: false, // 使用的是http协议则设置为false，https协议则设置为true
        pathRewrite: {
          '^/pdb': ''
        }
      },
    }
  },
  lintOnSave: false,   
}