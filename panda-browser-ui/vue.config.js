module.exports = {
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
    lintOnSave: false,   // 取消 eslint 验证
}