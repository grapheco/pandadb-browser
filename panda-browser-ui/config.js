
module.exports = {
    port: 9633, //端口号
    path: '/dist', //前端路径
    proxy: {  //代理配置
        '/pdb': {
            target: 'http://127.0.0.1:9632',
            changeOrigin: true,
            // ws: false, // 是否启用websockets
            // secure: false, // 使用的是http协议则设置为false，https协议则设置为true
            pathRewrite: {
                '^/pdb': ''
            }
        }
    }
}