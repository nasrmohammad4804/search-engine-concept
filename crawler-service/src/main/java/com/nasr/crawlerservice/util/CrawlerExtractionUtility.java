package com.nasr.crawlerservice.util;

import com.nasr.crawlerservice.domain.ExtractedData;
import org.apache.kafka.common.protocol.types.Field;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static org.jsoup.Connection.Response;

public class CrawlerExtractionUtility {

    private static final Logger log = LoggerFactory.getLogger(CrawlerExtractionUtility.class);

    private static final String LAST_MODIFIED_TIME = "last-modified";
    private static final String WEB_PAGE_POINTER_CHARACTER = "#";
    private static final String WEB_PROTOCOL = "http";

    private static final List<Integer> UNAVAILABLE_WEB_PAGE_STATUS_CODE = List.of(404, 403, 410);

    public static boolean isHtmlType(Response response) {
        return Objects.requireNonNull(response.header("content-type")).contains("text/html");
    }

    public static boolean isValidForExtract(Response response) {
        return !UNAVAILABLE_WEB_PAGE_STATUS_CODE.contains(response.statusCode());
    }

    public static ExtractedData extract(Response response, String url) throws IOException {

        Document doc = response.parse();
        Element body = doc.body();
        ExtractedData data = new ExtractedData();

        data.setUrl(url);
        data.setContent(body.text());
        data.setTitle(doc.title());
        data.setResponseStatus(response.statusCode());
        data.setLastModifiedResponseHeader(response.header(LAST_MODIFIED_TIME));
        data.setLinks(extractLinks(doc,url));

        return data;
    }

    private static Set<String> extractLinks(Document document,String baseUrl) {
        Elements elements = document.select("a[href]");
        Set<String> links = new HashSet<>();

        elements
                .forEach(element -> {

                    String linkAddress = element.attr("href");

                    if (!linkAddress.startsWith(WEB_PROTOCOL) && !linkAddress.startsWith(WEB_PAGE_POINTER_CHARACTER)){
                        try {

                            links.add(new URI(baseUrl).resolve(linkAddress).toString());
                        } catch (Exception e) {
                            log.error("relative url : {} with base url : {} cant be extracted",linkAddress,baseUrl);
                        }
                    }
                    if (linkAddress.startsWith(WEB_PROTOCOL))
                        links.add(linkAddress);
                });

        return links;
    }
}
