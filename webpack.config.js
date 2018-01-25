const path = require("path");

module.exports = {
    entry: './main.jsx',
    context: path.resolve(__dirname, "app/assets"),
    devtool: 'source-map',
    output: {
        path: path.join(__dirname, "target/web/webpack"),
        filename: "appman.js"
    },
    module: {
        rules: [
            { test: /\.jsx?$/, use: 'babel-loader' },
            { test: /\.css$/, use: ['style-loader', 'css-loader']}
        ]
    }
};
