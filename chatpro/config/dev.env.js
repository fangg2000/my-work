'use strict'
const merge = require('webpack-merge')
const prodEnv = require('./prod.env')

module.exports = merge(prodEnv, {
  NODE_ENV: '"development"',
  BASE_URL: '"http://localhost:6969/myChat"',
  SOCKET_URL: '"ws://127.0.0.1:9898/chat"',
  PUBLIC_KEY: 'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAx/Cei/D56FuCm+vXhXCnXaMR2CvM/ChiBJ6WROV+4bwaDZqKvPuwTu/tbgXQq28xAjpIYI+HYbnH7AkdMwkmvi67tj3pNNDN4WfBOBouEJV9r7uhz8vISq1jFSRDO2SDlkdYskLsKFzJJ2+LYPHtxhb0OPkPPM9amIJ+/CyDbDwWVheocC8dmEdIVq5xxu/TlWkMcPKp8/ysMFWofJgM4/3dF9IstMpj1/owpRBEUZBaSlfdb2hpYZR2DcpHb11bE118ejYnFHiHaUZdVWI5HvdyqU6i5BLk45bYyfhHjLlvsubkJNdGQGKeNhiSRDK2EKh3PC07+pYudxdsIa8cLwIDAQAB',
  AES_KEY: 'JDkCaf76rm6C3NK9'
})
