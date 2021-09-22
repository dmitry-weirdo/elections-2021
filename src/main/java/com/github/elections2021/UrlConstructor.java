package com.github.elections2021;

public class UrlConstructor {

    public static final long ROOT = 782000048; // root - некий идендификатор корня. Возможно, для дерева

    public static final long ELECTIONS_ID = 100100225883172L; // vrn - текущие выборы - это значение в RuElectionsData
//    public static final long ELECTIONS_ID = 100100225883718L; // vrn - текущие выборы // todo: which one is correct?

    public static final int TYPE_ONE_MANDATE = 463; // type
    public static final int TYPE_FEDERAL = 242; // type
    public static final int PRVER = 0; // prver = 0, некий неважный параметр?


    public static String uikUrlOneMandate(long tvd) {
        return uikUrl(tvd, TYPE_ONE_MANDATE);
    }

    public static String uikUrlFederal(long tvd) {
        return uikUrl(tvd, TYPE_FEDERAL);
    }

    public static String uikUrl(long tvd, int type) { // ссылка на результаты по УИК
        // todo: use some URL constructor class
        return String.format(
            "http://www.vybory.izbirkom.ru/region/izbirkom?action=show&root=%s&tvd=%s&vrn=%s&prver=%s&type=%s",
            ROOT,
            tvd,
            ELECTIONS_ID,
            PRVER,
            type
        );
    }
}
