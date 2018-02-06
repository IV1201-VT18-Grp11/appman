const path = require("path");
const ExtractTextPlugin = require("extract-text-webpack-plugin");

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
            { test: /\.jsx?$/, exclude: /node_modules/, use: 'babel-loader?cacheDirectory' },
            { test: /\.s?css$/, use: ExtractTextPlugin.extract({
                use: ['css-loader', 'sass-loader']
            })}
        ]
    },
    plugins: [
        new ExtractTextPlugin('appman.css')
    ]
};
