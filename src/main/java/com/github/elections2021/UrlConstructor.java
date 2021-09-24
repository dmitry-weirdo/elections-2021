package com.github.elections2021;

public class UrlConstructor {

    public static final String ACTION_SHOW = "show";
    public static final String ACTION_TVD_TREE = "tvdTree";

    public static final long ROOT = 782000048; // root - некий идендификатор корня. Возможно, для дерева

    public static final long ELECTIONS_ID = 100100225883172L; // vrn - текущие выборы - это значение в RuElectionsData
//    public static final long ELECTIONS_ID = 100100225883718L; // vrn - текущие выборы // todo: which one is correct?

    public static final int TYPE_ONE_MANDATE = 463; // type
    public static final int TYPE_FEDERAL = 242; // type
    public static final int PRVER = 0; // prver = 0, некий неважный параметр?

    public static String uikUrlOneMandate(long uikTvd) {
        return uikUrl(uikTvd, TYPE_ONE_MANDATE);
    }

    public static String uikUrlFederal(long uikTvd) {
        return uikUrl(uikTvd, TYPE_FEDERAL);
    }

    public static String uikUrl(long uikTvd, int type) { // ссылка на результаты по УИК
        // todo: use some URL constructor class
        return String.format(
            "http://www.vybory.izbirkom.ru/region/izbirkom?action=%s&root=%s&tvd=%s&vrn=%s&prver=%s&type=%s",
            ACTION_SHOW,
            ROOT,
            uikTvd,
            ELECTIONS_ID,
            PRVER,
            type
        );
    }

    public static String loadChildrenUrl(long tvd) {
        return String.format(
            "http://www.vybory.izbirkom.ru/region/izbirkom?action=%s&tvdchildren=%s&vrn=%s&tvd=%s",
            ACTION_TVD_TREE,
            true,
            ELECTIONS_ID,
            tvd
        );
    }

    public static void main(String[] args) {
        final long TIK_16_TVD = 27820001915986L;
        final long UIK_8418_TVD = 4784017309091L;

        final String loadTikChildrenUrl = loadChildrenUrl(TIK_16_TVD);

        final String oneMandateUrl = uikUrlOneMandate(UIK_8418_TVD);
        final String federalUrl = uikUrlFederal(UIK_8418_TVD);

        System.out.println("load TIK children url: " + loadTikChildrenUrl); // todo: replace with logger
        System.out.println("one mandate url: " + oneMandateUrl); // todo: replace with logger
        System.out.println("federal url: " + federalUrl); // todo: replace with logger
    }
}
