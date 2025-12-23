package com.example.CineVerse.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

public final class TotpUtil {

    private static final SecureRandom RNG = new SecureRandom();

    public static String generateSecret() {
        byte[] bytes = new byte[20];
        RNG.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String getOtpAuthUrl(String issuer, String account, String secret) {
        String label = issuer + ":" + account;
        return String.format(
                "otpauth://totp/%s?secret=%s&issuer=%s&algorithm=SHA1&digits=6&period=30",
                urlEncode(label),
                secret,
                urlEncode(issuer)
        );
    }

    public static boolean verifyCode(String secret, String code) {
        if (code == null || secret == null) return false;

        long now = Instant.now().getEpochSecond() / 30;
        for (long i = -1; i <= 1; i++) {
            if (generateTOTP(secret, now + i).equals(code)) return true;
        }
        return false;
    }

    private static String generateTOTP(String secretBase64, long counter) {
        try {
            byte[] key = Base64.getDecoder().decode(secretBase64);

            ByteBuffer buffer = ByteBuffer.allocate(8);
            buffer.putLong(counter);
            byte[] data = buffer.array();

            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            byte[] hash = mac.doFinal(data);

            int offset = hash[hash.length - 1] & 0x0F;
            int binary =
                    ((hash[offset] & 0x7f) << 24) |
                            ((hash[offset + 1] & 0xff) << 16) |
                            ((hash[offset + 2] & 0xff) << 8) |
                            (hash[offset + 3] & 0xff);

            int otp = binary % 1_000_000;
            return String.format("%06d", otp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String urlEncode(String s) {
        return s.replace(" ", "%20");
    }
}
