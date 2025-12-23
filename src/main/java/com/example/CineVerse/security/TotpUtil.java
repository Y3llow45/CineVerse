package com.example.CineVerse.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Locale;

public final class TotpUtil {

    private static final String BASE32 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    private static final SecureRandom RNG = new SecureRandom();

    public static String generateSecret() {
        byte[] bytes = new byte[20];
        RNG.nextBytes(bytes);
        return base32Encode(bytes);
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
        if (secret == null || code == null) return false;

        long counter = Instant.now().getEpochSecond() / 30;
        for (long i = -1; i <= 1; i++) {
            if (generateTOTP(secret, counter + i).equals(code)) return true;
        }
        return false;
    }

    private static String generateTOTP(String secretBase32, long counter) {
        try {
            byte[] key = base32Decode(secretBase32);

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

            return String.format("%06d", binary % 1_000_000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String base32Encode(byte[] data) {
        StringBuilder sb = new StringBuilder();
        int index = 0, digit;
        int curr, next;
        int i = 0;

        while (i < data.length) {
            curr = data[i] & 0xff;

            if (index > 3) {
                next = (i + 1 < data.length) ? data[i + 1] & 0xff : 0;
                digit = curr & (0xff >> index);
                index = (index + 5) % 8;
                digit = (digit << index) | (next >> (8 - index));
                i++;
            } else {
                digit = (curr >> (8 - (index + 5))) & 0x1f;
                index = (index + 5) % 8;
                if (index == 0) i++;
            }

            sb.append(BASE32.charAt(digit));
        }

        return sb.toString();
    }

    private static byte[] base32Decode(String s) {
        s = s.replace("=", "").toUpperCase(Locale.ROOT);
        byte[] result = new byte[s.length() * 5 / 8];

        int buffer = 0, bitsLeft = 0, index = 0;

        for (char c : s.toCharArray()) {
            int val = BASE32.indexOf(c);
            if (val < 0) continue;

            buffer = (buffer << 5) | val;
            bitsLeft += 5;

            if (bitsLeft >= 8) {
                result[index++] = (byte) (buffer >> (bitsLeft - 8));
                bitsLeft -= 8;
            }
        }

        byte[] out = new byte[index];
        System.arraycopy(result, 0, out, 0, index);
        return out;
    }

    private static String urlEncode(String s) {
        return s.replace(" ", "%20");
    }
}
