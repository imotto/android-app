package net.imotto.imottoapp.utils;

import android.util.Base64;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Created by sunht on 16/10/28.
 */

public class Encryptor {
    public static final String ALGORITHM = "DES";

    public static byte[] decryptBASE64(String key) throws Exception {
        return Base64.decode(key, Base64.DEFAULT);
    }

    public static String encryptBASE64(byte[] key) throws Exception {
        return Base64.encodeToString(key, Base64.DEFAULT);
    }

    private static Key toKey(byte[] key) throws Exception {
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey secretKey = keyFactory.generateSecret(dks);

        return secretKey;
    }

    public static String decrypt(String value) throws Exception {
        byte[] data = decryptBASE64(value);
        byte[] keyData = new byte[8];
        byte[] encryptedData = new byte[data.length-8];
        System.arraycopy(data, 0, keyData, 0, 8);
        System.arraycopy(data, 8, encryptedData, 0, encryptedData.length);

        Key k = toKey(keyData);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, k);

        byte[] decryptedData = cipher.doFinal(encryptedData);

        return new String(decryptedData);
    }

    public static String encrypt(String value) throws Exception {
        SecretKey k = initKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, k);

        byte[] data = value.getBytes();
        byte[] encryptedData = cipher.doFinal(data);
        byte[] keyData = k.getEncoded();

        byte[] datas = new byte[encryptedData.length+keyData.length];
        System.arraycopy(keyData, 0, datas, 0, keyData.length);
        System.arraycopy(encryptedData, 0, datas, keyData.length, encryptedData.length);

        return encryptBASE64(datas);
    }

    public static SecretKey initKey() throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);
        kg.init(secureRandom);

        return kg.generateKey();
    }
}
