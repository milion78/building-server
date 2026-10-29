package com.tencent.wxcloudrun.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IdCodeUtilTest {

    @Test
    void toCodeFor123() {
        String code = IdCodeUtil.toCode(1234567890123L);
        System.out.println("id=123 code=" + code);
        assertEquals(123L, IdCodeUtil.toId(code));
    }

    @Test
    void toIdForCode() {
        String code = "U8BNQV1R2MJDPFZA";
        Long id = IdCodeUtil.toId(code);
        System.out.println("code=" + code + " id=" + id);
    }
}
