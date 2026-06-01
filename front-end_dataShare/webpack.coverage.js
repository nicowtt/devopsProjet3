const path = require('path');

module.exports = {
  module: {
    rules: [
      {
        test: /\.(ts|js)$/,
        loader: 'babel-loader',
        options: {
          plugins: ['babel-plugin-istanbul'],
        },
        enforce: 'post',
        include: path.join(__dirname, 'src'),
        exclude: [/\.spec\.ts$/, /\.cy\.ts$/],
      },
    ],
  },
};
