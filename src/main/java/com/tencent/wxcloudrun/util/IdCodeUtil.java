package com.tencent.wxcloudrun.util;

/**
 * 文保 id 与对外 code 的互转。
 * code 可以还原出原来的 id。密钥变更后，已发出的 code 会失效。
 *
 * @author building-service
 */
public final class IdCodeUtil {

    private static final int ROUNDS = 4;

    private static final String ALPHABET = "M4K9Q2X7R8N3Y6P5T1V0ZJHUASCFEDGB";

    private static final int[] ALPHABET_INDEX = new int[128];

    private static final int[] ROUND_KEYS = new int[ROUNDS];

    static {
        for (int i = 0; i < ALPHABET_INDEX.length; i++) {
            ALPHABET_INDEX[i] = -1;
        }
        for (int i = 0; i < ALPHABET.length(); i++) {
            ALPHABET_INDEX[ALPHABET.charAt(i)] = i;
        }
        int state = 0x6E7A6F62;
        for (int i = 0; i < ROUNDS; i++) {
            state ^= state << 13;
            state ^= state >>> 17;
            state ^= state << 5;
            state += 0x9E3779B9 * (i + 1);
            ROUND_KEYS[i] = state;
        }
    }

    private IdCodeUtil() {
    }

    /**
     * 把 id 转成对外 code。
     *
     * @param id 原始 id
     * @return 16 位 code，id 为空时返回 null
     */
    public static String toCode(Long id) {
        if (id == null) {
            return null;
        }
        long mixed = mix(id);
        int checksum = checksum(mixed);
        byte[] bytes = new byte[10];
        for (int i = 7; i >= 0; i--) {
            bytes[i] = (byte) (mixed & 0xFF);
            mixed >>>= 8;
        }
        bytes[8] = (byte) ((checksum >>> 8) & 0xFF);
        bytes[9] = (byte) (checksum & 0xFF);
        return encode(bytes);
    }

    /**
     * 把前端传入的 code 还原成 id。
     *
     * @param code 对外 code
     * @return 原始 id，code 为空或无法还原时返回 null
     */
    public static Long toId(String code) {
        if (code == null) {
            return null;
        }
        String normalized = code.trim();
        if (normalized.length() != 16) {
            return null;
        }
        byte[] bytes = decode(normalized);
        if (bytes == null) {
            return null;
        }
        long mixed = 0L;
        for (int i = 0; i < 8; i++) {
            mixed = (mixed << 8) | (bytes[i] & 0xFFL);
        }
        int checksum = ((bytes[8] & 0xFF) << 8) | (bytes[9] & 0xFF);
        if (checksum != checksum(mixed)) {
            return null;
        }
        return unmix(mixed);
    }

    private static long mix(long value) {
        int left = (int) (value >>> 32);
        int right = (int) value;
        for (int round = 0; round < ROUNDS; round++) {
            int mixedRight = left ^ roundFunction(right, round);
            left = right;
            right = mixedRight;
        }
        return (((long) left) << 32) | (right & 0xFFFFFFFFL);
    }

    private static long unmix(long value) {
        int left = (int) (value >>> 32);
        int right = (int) value;
        for (int round = ROUNDS - 1; round >= 0; round--) {
            int previousRight = left;
            int previousLeft = right ^ roundFunction(previousRight, round);
            left = previousLeft;
            right = previousRight;
        }
        return (((long) left) << 32) | (right & 0xFFFFFFFFL);
    }

    private static int roundFunction(int right, int round) {
        int value = right ^ ROUND_KEYS[round];
        value = Integer.rotateLeft(value, 5 + round);
        value *= 0x45D9F3B;
        value ^= Integer.rotateRight(value, 11);
        value += ROUND_KEYS[(round + 1) & (ROUNDS - 1)];
        return value;
    }

    private static int checksum(long mixed) {
        int sum = 0xA35C;
        for (int i = 0; i < 8; i++) {
            int part = (int) ((mixed >>> ((7 - i) * 8)) & 0xFF);
            sum = (sum * 131 + part + (ROUND_KEYS[i & (ROUNDS - 1)] & 0xFF)) & 0xFFFF;
        }
        return sum;
    }

    private static String encode(byte[] data) {
        StringBuilder builder = new StringBuilder(16);
        int buffer = 0;
        int bits = 0;
        for (byte value : data) {
            buffer = (buffer << 8) | (value & 0xFF);
            bits += 8;
            while (bits >= 5) {
                bits -= 5;
                builder.append(ALPHABET.charAt((buffer >> bits) & 31));
            }
        }
        return builder.toString();
    }

    private static byte[] decode(String code) {
        byte[] data = new byte[10];
        int buffer = 0;
        int bits = 0;
        int index = 0;
        for (int i = 0; i < code.length(); i++) {
            char current = code.charAt(i);
            if (current >= ALPHABET_INDEX.length || ALPHABET_INDEX[current] < 0) {
                return null;
            }
            buffer = (buffer << 5) | ALPHABET_INDEX[current];
            bits += 5;
            if (bits >= 8) {
                bits -= 8;
                data[index++] = (byte) ((buffer >> bits) & 0xFF);
            }
        }
        if (index != data.length || bits != 0) {
            return null;
        }
        return data;
    }
}
