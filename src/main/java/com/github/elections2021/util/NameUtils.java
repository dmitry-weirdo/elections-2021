package com.github.elections2021.util;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class NameUtils { // todo: add tests for this class

    public static final String UIK_NAME_PREFIX = "УИК №";
    public static final int MIN_FOREIGN_UIK_NUMBER = 8000;
    public static final int MAX_FOREIGN_UIK_NUMBER = 8999; // UIKs 9001, 9002, 9003 and 9004 are Moscow

    public static final int BAIKONUR_MIN_UIK_NUMBER = 8140;
    public static final int BAIKONUR_MAX_UIK_NUMBER = 8146;

    public static void main(String[] args) {
//        final String uikName = "УИК №596";
        final String uikName = "УИК №8418";
        final int uikNumber = getUikNumber(uikName);

        log.info("UIK name: {}, UIK number: {}", uikName, uikNumber);
    }

    public static int getUikNumber(String uikName) {
        final String numberString = uikName.trim().substring(UIK_NAME_PREFIX.length());
        return Integer.parseInt(numberString);
    }

    public static boolean isForeign(String uikName) {
        final int uikNumber = getUikNumber(uikName);
        return isForeign(uikNumber);
    }

    public static boolean isForeign(int uikNumber) {
        return (MIN_FOREIGN_UIK_NUMBER <= uikNumber) && (uikNumber <= MAX_FOREIGN_UIK_NUMBER) && !isBaikonur(uikNumber);
    }

    public static boolean isBaikonur(int uikNumber) {
        return (BAIKONUR_MIN_UIK_NUMBER <= uikNumber) && (uikNumber <= BAIKONUR_MAX_UIK_NUMBER);
    }
}
