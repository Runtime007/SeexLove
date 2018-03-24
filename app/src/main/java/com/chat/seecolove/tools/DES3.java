package com.chat.seecolove.tools;

import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

/**
 * 3DEC加密
 */
public class DES3 {
    private static final int KEY_LENGTH = 26;//必须大于等于24
    private static final String KEY_MODEL = "DESede";
    private static final String DES_MODEL = "DESede/ECB/PKCS5Padding";

    public static final String IMG_SIZE_80 = "_80-";
    public static final String IMG_SIZE_100 = "_100-";
    public static final String IMG_SIZE_150 = "_150-";
    public static final String IMG_SIZE_200 = "_200-";
    public static final String IMG_SIZE_500 = "_500-";

    /**
     * 加密
     *
     * @param src 要加密的内容
     * @return
     * @throws Exception
     */
    public static String encryptThreeDES(String src) {
        try {
            String key = getKey(KEY_LENGTH);
            DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_MODEL);
            SecretKey securekey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(DES_MODEL);
            cipher.init(Cipher.ENCRYPT_MODE, securekey);
            byte[] b = cipher.doFinal(src.getBytes());
            String result = Base64.encode(b).replaceAll("\r", "").replaceAll("\n", "");
            return key + result;
        } catch (Exception e) {

        }

        return null;
    }

    /**
     * 解密
     *
     * @param src
     * @return
     * @throws Exception
     */
    //3DESECB解密,key必须是长度大于等于 3*8 = 24 位
    public static String decryptThreeDES(String src) {
        if (Tools.isEmpty(src)) {
            return "";
        }
        if (src.contains("http")&&src.startsWith("http")) {
            return src;
        }

        if (src.length() < KEY_LENGTH) {
            return "";
        }

        //--通过base64,将字符串转成byte数组
        //--解密的key
        String key = src.substring(0, KEY_LENGTH);
//        System.out.println("解密key:" + key);
        String content = src.substring(KEY_LENGTH);
//        System.out.println("需要解密内容:" + content);
        byte[] bytesrc = Base64.decode(content);

        try {
            DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_MODEL);
            SecretKey securekey = keyFactory.generateSecret(dks);

            //--Chipher对象解密
            Cipher cipher = Cipher.getInstance(DES_MODEL);
            cipher.init(Cipher.DECRYPT_MODE, securekey);
            byte[] retByte = cipher.doFinal(bytesrc);
            String path = new String(retByte);
//            LogTool.setLog("原图 path:",""+path);
            return path;
        } catch (Exception e) {

        }

        return "";
    }

    public static String decryptThreeDES(String src, String imgsize) {
        if (Tools.isEmpty(src)) {
            return "";
        }
        if (src.contains("http")&&src.startsWith("http")) {
            return zoomImg(src, imgsize);
        }

        if (src.length() < KEY_LENGTH) {
            return "";
        }
        //--通过base64,将字符串转成byte数组
        //--解密的key
        String key = src.substring(0, KEY_LENGTH);
//        System.out.println("解密key:" + key);
        String content = src.substring(KEY_LENGTH);
//        System.out.println("需要解密内容:" + content);
        byte[] bytesrc = Base64.decode(content);

        try {
            DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_MODEL);
            SecretKey securekey = keyFactory.generateSecret(dks);

            //--Chipher对象解密
            Cipher cipher = Cipher.getInstance(DES_MODEL);
            cipher.init(Cipher.DECRYPT_MODE, securekey);
            byte[] retByte = cipher.doFinal(bytesrc);
            String path = new String(retByte);

            return zoomImg(path, imgsize);
        } catch (Exception e) {

        }

        return "";
    }

    private static String zoomImg(String path, String size) {
        int index = path.lastIndexOf(".");
        if(index>0){
            String prefix = path.substring(index);
            String newPath = path + size + prefix;
            LogTool.setLog("zoomImg path:",""+newPath);
            return newPath;
        }
      return path;
    }


    /**
     * 随机获取一个加解密的key
     *
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    private static String getKey(int length) {
        if (length <= 0) {
            return "";
        }
        char[] randomChar = {'A', 'X', '1', 'C', 'T', 'E', 'B', 'F', '8', 'H', 'Q', 'G', 'I', '2', 'J', '5', 'K', 'L', '3', 'N',
                'O', 'M', 'P', '6', 'R', 'U', 'V', 'W', '4', 'Y', 'S', '7', 'Z', 'D', '0', '9', 'a', 'b', 'c', 'd', 'e', 'f',
                'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        Random random = new Random();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            stringBuffer.append(randomChar[Math.abs(random.nextInt()) % randomChar.length]);
        }
        return stringBuffer.toString();
    }
}
