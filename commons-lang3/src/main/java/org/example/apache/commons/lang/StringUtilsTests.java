package org.example.apache.commons.lang;

import org.apache.commons.lang3.StringUtils;

/**
 * <p> ¿‡√Ë ˆ: {@StringUtils} Tests
 *
 * @author liqxhx
 * @version 1.0
 * @date 2020/04/01 17:41
 * @since 2020/04/01 17:41
 */
public class StringUtilsTests {
    public static void main(String[] args) {
        System.out.println(StringUtils.isAllUpperCase("A_B"));
        System.out.println(StringUtils.isAllUpperCase(StringUtils.replace("A_B", "_", "")));
    }
}
