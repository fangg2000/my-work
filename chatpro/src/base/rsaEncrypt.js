import { JSEncrypt } from 'jsencrypt'

// const publicKey = 'process.env.PUBLIC_KEY'
const publicKey = 'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhutV4Iu8LuD8hKDIh6oqMG0mL9JXaTi29+vU0MDo5wNRRqeyfqfwDjjNcoi+fsCyXSbAaXI16yoXGtEdAmD8Oj7cN19Yk0blxoCz39Tp87Zw3bG3eB5ScK2PhQjpfanMZM6jHOZSiybXPMEMZmf21+EFaUGYo5qFEGhX/+1oraT23xCaC4E+hf4RuibwcgzeEQC37bBkzUlXfvoZ0npx7ZJar4cDtido0MOs3sHw3w95dqIAmH/LzkBY+mAc28Ihy+o8ZA7o+GOvBSICc9yL5n+yV6gFROYyop/MhXywqtVV+OCl3IinDESJYL/FUsfFihWgVGoGDxpNY86Lq07VNwIDAQAB'

// 测试加密信息
let encrypt = new JSEncrypt();
encrypt.setPublicKey(publicKey);
// var encryptData = encrypt.encrypt("这是一段加密数据");   //data必须转成字符串
// console.log('加密结果--', encryptData);
// console.log('解密结果--', encrypt.decrypt(encryptData));

export default encrypt
