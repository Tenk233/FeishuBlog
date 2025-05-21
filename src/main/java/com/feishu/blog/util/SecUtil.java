package com.feishu.blog.util;

import com.feishu.blog.exception.BizException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public final class SecUtil {

    private SecUtil() {}   // 工具类不允许实例化

    /* ===== 参数常量 ===== */
    private static final int SALT_LENGTH   = 16;          // 16 bytes = 128 bits
    private static final int ITERATIONS    = 64_000;      // OWASP 建议 >= 60k
    private static final int KEY_LENGTH    = 256;         // 256-bit
    private static final String ALGORITHM  = "PBKDF2WithHmacSHA256";

    private static final SecureRandom RANDOM = new SecureRandom();

    /* ---------- 1. 生成随机盐值 (Base64 字符串) ---------- */
    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /* ---------- 2. 对密码 + 盐做 PBKDF2 哈希，返回 Base64 ---------- */
    public static String hashPassword(String rawPassword, String base64Salt) {
        char[]  passwordChars = rawPassword.toCharArray();
        byte[]  saltBytes     = Base64.getDecoder().decode(base64Salt);

        PBEKeySpec spec = new PBEKeySpec(passwordChars, saltBytes, ITERATIONS, KEY_LENGTH);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new BizException(BizException.INTERNAL_ERROR, "PBKDF2 hashing failed");
        } finally {
            spec.clearPassword();                       // 清除密码字符数组
        }
    }

    /* ---------- 3. 验证密码：再算一次哈希比对 ---------- */
    public static boolean verifyPassword(String rawPassword, String base64Salt, String base64Hash) {
        String newHash = hashPassword(rawPassword, base64Salt);
        return newHash.equals(base64Hash);
    }

    public static boolean checkInviteCode(String code)
    {
        final String stdCode = "FeiShu";
        return stdCode.equals(code);
    }
}
