package com.github.elections2021;

import com.github.elections2021.api.TvdChild;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class HttpClientWrapper {

    // one instance, reuse
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    public static final Charset WINDOWS_1251_CHARSET = Charset.forName("Windows-1251"); // no UTF-8, fuck yeah :facepalm:

    public static void main(String[] args) throws Exception {
        final String url = "http://www.vybory.izbirkom.ru/region/izbirkom?action=tvdTree&tvdchildren=true&vrn=100100225883172&tvd=27820001915986";

        loadChildren(url);
    }

    public static List<TvdChild> loadChildren(String url) {
        try { // todo: nicer code
            HttpClientWrapper clientWrapper = new HttpClientWrapper();

            try {
                final String childrenJson = clientWrapper.sendTikGet(url);

                final List<TvdChild> children = JacksonUtils.parseList(childrenJson, TvdChild.class);
                log.info("Total {} children parsed from url {}", children.size(), url);

                return children;
            } finally {
                clientWrapper.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void close() throws IOException {
        httpClient.close();
    }

    private String sendTikGet(String url) {
        System.out.printf("Loading GET from url %s ... %n", url);

        HttpGet request = new HttpGet(url);

        // add headers to receive data - fuck CIK!
        request.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:92.0) Gecko/20100101 Firefox/92.0");

        final HttpEntity entity;
        try (CloseableHttpResponse response = httpClient.execute(request)) {

            System.out.println("response: \n" + response.getStatusLine().toString());

            entity = response.getEntity();

            final String responseBody = EntityUtils.toString(entity, WINDOWS_1251_CHARSET);
            System.out.println("response body: \n" + responseBody);

            return responseBody;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void sendPost() throws Exception {

        HttpPost post = new HttpPost("https://httpbin.org/post");

        // add request parameter, form parameters
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("username", "abc"));
        urlParameters.add(new BasicNameValuePair("password", "123"));
        urlParameters.add(new BasicNameValuePair("custom", "secret"));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {

            System.out.println(EntityUtils.toString(response.getEntity()));
        }
    }
}
