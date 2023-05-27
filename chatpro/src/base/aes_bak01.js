import CryptoJS from 'crypto-js';
 
/**
 * 来自为CSDN博主「余温无痕」的原创文章
    原文链接：https://blog.csdn.net/u014678583/article/details/105157695/
 */

// 默认128位密钥
const private_key = 'rhOtOu9ly0EZBWTrJigx0GFKoCURx4z86RecKbHpPQoRfeUJqWL54HxqKrzL7vi0ryL56mX63TloI5NVFKZN0AKP92T3CLaecfn91Z6vBSwUXaoVpZgtfwZmSe5uLueG'


export default {
    //随机生成指定数量的16进制key
    generatekey(num) {
        let size = num==undefined?128:num
        let library = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        let key = "";
        for (var i = 0; i < size; i++) {
            let randomPoz = Math.floor(Math.random() * library.length);
            key += library.substring(randomPoz, randomPoz + 1);
        }
        return key;
    },
    
    //加密
    encrypt(word, keyStr) {
        if (word == undefined || word == null || word.length == 0) {
            return ''
        }
        let info = (typeof(word)=='string')?word:JSON.stringify(word)
        keyStr = keyStr ? keyStr : private_key
        var key = CryptoJS.enc.Utf8.parse(keyStr);
        var srcs = CryptoJS.enc.Utf8.parse(info);
        var encrypted = CryptoJS.AES.encrypt(srcs, key, { mode: CryptoJS.mode.ECB, padding: CryptoJS.pad.Pkcs7 });
        return encrypted.toString();
    },
    //解密
    decrypt(word, keyStr) {
        if (word == undefined || word == null || word.length == 0) {
            return ''
        }
        let info = (typeof(word)=='string')?word:JSON.stringify(word)
        keyStr = keyStr ? keyStr : private_key
        var key = CryptoJS.enc.Utf8.parse(keyStr);
        var decrypt = CryptoJS.AES.decrypt(info, key, { mode: CryptoJS.mode.ECB, padding: CryptoJS.pad.Pkcs7 });
        return CryptoJS.enc.Utf8.stringify(decrypt).toString();
    }
 
}
