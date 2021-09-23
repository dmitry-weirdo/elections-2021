package com.github.elections2021.api;

import com.github.elections2021.UrlParameters;
import lombok.extern.log4j.Log4j2;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Log4j2
public class UrlParser {

//    String url = "http://www.example.com/something.html?one=1&two=2&three=3&three=3a";

    public static void main(String[] args) {
        final String uik8418tikUrl = "http://www.vybory.izbirkom.ru/region/izbirkom?action=show&root=1000281&tvd=27820001915986&vrn=100100225883172&prver=0&pronetvd=null&region=78&sub_region=78";
        final long uik8418TikTvd = getTvd(uik8418tikUrl);
        log.info("Uik 8418 TIK tvd: {}", uik8418TikTvd);

//        String url = "http://www.vybory.izbirkom.ru/region/izbirkom?action=show&root=1000058&tvd=22220002523303&vrn=100100225883172&prver=0&pronetvd=null&region=22&sub_region=22";
//        getTvd(url);
    }

    public static long getTvd(String url) {
        final String paramValue = getParamValue(url, UrlParameters.TVD);
        return Long.parseLong(paramValue);
    }

    public static String getParamValue(String url, String paramName) {
        try {
            List<NameValuePair> params = URLEncodedUtils.parse(new URI(url), StandardCharsets.UTF_8);

            for (NameValuePair param : params) {
                System.out.println(param.getName() + ": " + param.getValue());
            }

            return params
                .stream()
                .filter(p -> p.getName().equals(paramName))
                .map(NameValuePair::getValue)
                .findFirst()
                .orElseThrow(() ->
                    new IllegalArgumentException(String.format("Param \"%s\" not found in URL \"%s\"", paramName, url))
                );
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
