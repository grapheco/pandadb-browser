const express = require('express')
const app = express()
const paths = require('path')
const http_proxy = require("http-proxy-middleware");
const history = require('connect-history-api-fallback');
const { proxy, path } = require('../../config')

app.use(history());

const compression = require('compression')
app.use(compression());

const bodyParser = require('body-parser');
app.use(bodyParser.json({ limit: '50mb' }));
app.use(bodyParser.urlencoded({ limit: '50mb', extended: true }));

for (const key in proxy) {
    app.use(key,
        http_proxy.createProxyMiddleware(proxy[key])
    );
}

app.use(express.static(paths.join(__dirname, `../../${path}`)))

module.exports = app

