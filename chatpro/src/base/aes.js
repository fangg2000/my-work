import CryptoJS from 'crypto-js';

/**
 * 来自为CSDN博主「余温无痕」的原创文章
    原文链接：https://blog.csdn.net/u014678583/article/details/105157695/
 */

// 默认128位密钥
// const private_key = 'rhOtOu9ly0EZBWTrJigx0GFKoCURx4z86RecKbHpPQoRfeUJqWL54HxqKrzL7vi0ryL56mX63TloI5NVFKZN0AKP92T3CLaecfn91Z6vBSwUXaoVpZgtfwZmSe5uLueG'

// 默认的 KEY 与 IV
// const KEY = CryptoJS.enc.Utf8.parse(private_key);
// const IV = CryptoJS.enc.Utf8.parse(private_key);

/* 
export default {
    //随机生成指定数量的16进制key
    generatekey(num) {
        let size = num==undefined?16:num
        let library = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        let key = "";
        for (var i = 0; i < size; i++) {
            let randomPoz = Math.floor(Math.random() * library.length);
            key += library.substring(randomPoz, randomPoz + 1);
        }
        return key;
    },
    //加密
    encrypt(word, keyStr, ivStr) {
        let key = KEY
        let iv = IV

        if (keyStr && ivStr) {
            console.log('----自定义---key,iv')
            key = CryptoJS.enc.Utf8.parse(keyStr);
            iv = CryptoJS.enc.Utf8.parse(ivStr);
        }
        let srcs = CryptoJS.enc.Utf8.parse(word);
        var encrypted = CryptoJS.AES.encrypt(srcs, key, {
            iv: iv,
            mode: CryptoJS.mode.CBC,
            padding: CryptoJS.pad.ZeroPadding
        });
        return CryptoJS.enc.Base64.stringify(encrypted.ciphertext);
    },

    //解密
    decrypt(word, keyStr, ivStr) {
        let key = KEY
        let iv = IV

        if (keyStr && ivStr) {
            key = CryptoJS.enc.Utf8.parse(keyStr);
            iv = CryptoJS.enc.Utf8.parse(ivStr);
        }

        let base64 = CryptoJS.enc.Base64.parse(word);
        let src = CryptoJS.enc.Base64.stringify(base64);
        var decrypt = CryptoJS.AES.decrypt(src, key, {
            iv: iv,
            mode: CryptoJS.mode.CBC,
            padding: CryptoJS.pad.ZeroPadding
        })
        var decryptedStr = decrypt.toString(CryptoJS.enc.Utf8);
        return decryptedStr.toString();
    }

} */

/**
 * 
    动态获取加密key的方式，思路：

    加密算法有对称加密和非对称加密，AES是对称加密，RSA是非对称加密。之所以用AES加密数据是因为效率高，RSA运行速度慢,可以用于签名操作。

    我们可以用这2种算法互补，来保证安全性，用RSA来加密传输AES的秘钥，用AES来加密数据，两者相互结合，优势互补。

    其实大家理解了HTTPS的原理的话对于下面的内容应该是一看就懂的，HTTPS比HTTP慢的原因都是因为需要让客户端与服务器端安全地协商出一个对称加密算法。
    剩下的就是通信时双方使用这个对称加密算法进行加密解密。

    1、客户端启动，发送请求到服务端，服务端用RSA算法生成一对公钥和私钥，我们简称为pubkey1,prikey1，将公钥pubkey1返回给客户端。

    2、客户端拿到服务端返回的公钥pubkey1后，自己用RSA算法生成一对公钥和私钥，我们简称为pubkey2,prikey2，并将公钥pubkey2通过公钥pubkey1加密，
    加密之后传输给服务端。

    3、此时服务端收到客户端传输的密文，用私钥prikey1进行解密，因为数据是用公钥pubkey1加密的，通过解密就可以得到客户端生成的公钥pubkey2

    4、然后自己在生成对称加密，也就是我们的AES,其实也就是相对于我们配置中的那个16的长度的加密key,生成了这个key之后我们就用公钥pubkey2进行加密，
    返回给客户端，因为只有客户端有pubkey2对应的私钥prikey2，只有客户端才能解密，客户端得到数据之后，用prikey2进行解密操作，得到AES的加密key,
    最后就用加密key进行数据传输的加密，至此整个流程结束。

 */

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
        keyStr = keyStr ? keyStr : process.env.AES_KEY
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
        keyStr = keyStr ? keyStr : process.env.AES_KEY
        var key = CryptoJS.enc.Utf8.parse(keyStr);
        var decrypt = CryptoJS.AES.decrypt(info, key, { mode: CryptoJS.mode.ECB, padding: CryptoJS.pad.Pkcs7 });
        return CryptoJS.enc.Utf8.stringify(decrypt).toString();
    }
 
}


