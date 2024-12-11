package com.nasr.crawlerservice.util;

import com.nasr.crawlerservice.domain.ExtractedData;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.jsoup.Connection.Response;

public class CrawlerExtractionUtility {

    private static final Logger log = LoggerFactory.getLogger(CrawlerExtractionUtility.class);

    private static final String LAST_MODIFIED_TIME = "last-modified";
    private static final String WEB_PAGE_POINTER_CHARACTER = "#";
    private static final String WEB_PROTOCOL = "http";
    private static final String CONTAINS_EMPTY_CHARACTER = ".*\\s.*";

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
        data.setSiteName(extractSiteName(doc));
        data.setIconUrl(extractIconUrl(doc));
        data.setResponseStatus(response.statusCode());
        data.setLastModifiedResponseHeader(response.header(LAST_MODIFIED_TIME));
        data.setLinks(extractLinks(doc,url));

        return data;
    }

    private static String extractIconUrl(Document doc) {
        Element iconLink = doc.selectFirst("link[rel=icon]");
        if (iconLink != null) {
            return iconLink.attr("abs:href");
        }

        Element shortcutIconLink = doc.selectFirst("link[rel=shortcut icon]");
        if (shortcutIconLink != null) {
            return shortcutIconLink.attr("abs:href");
        }
        return null;

    }

    private static String extractSiteName(Document document){

        Element siteNameElement = document.selectFirst("meta[property=og:site_name]");
        if (siteNameElement != null) {
            return siteNameElement.attr("content");
        }

        Element applicationNameElement = document.selectFirst("meta[name=application-name]");
        if (applicationNameElement != null) {
            return applicationNameElement.attr("content");
        }

        Element twitterSiteElement = document.selectFirst("meta[name=twitter:site]");
        if (twitterSiteElement != null) {
            return twitterSiteElement.attr("content");
        }
        return null;
    }

    private static Set<String> extractLinks(Document document,String baseUrl) {
        Elements elements = document.select("a[href]");
        Set<String> links = new HashSet<>();

        elements
                .forEach(element -> {

                    String linkAddress = element.attr("href");

                    if (!linkAddress.startsWith(WEB_PROTOCOL) && !linkAddress.startsWith(WEB_PAGE_POINTER_CHARACTER) && !linkAddress.matches(CONTAINS_EMPTY_CHARACTER)){
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
