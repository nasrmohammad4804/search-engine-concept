package com.nasr.crawlerservice.util;

import com.nasr.crawlerservice.domain.ExtractedData;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.jsoup.Connection.Response;

public class CrawlerExtractionUtility {

    private static final String LAST_MODIFIED_TIME = "last-modified";
    private static final List<Integer> UNAVAILABLE_WEB_PAGE_STATUS_CODE = List.of(404, 403, 410);

    public static boolean isHtmlType(Response response) {
        return response.header("content-type").contains("text/html");
    }

    public static boolean isValidForExtract(Response response) {
        return !UNAVAILABLE_WEB_PAGE_STATUS_CODE.contains(response.statusCode());
    }

    public static ExtractedData extract(Response response) throws IOException {

        Document doc = response.parse();
        ExtractedData data = new ExtractedData();

        data.setContent(doc.body().html());
        data.setTitle(doc.title());
        data.setResponseStatus(response.statusCode());
        data.setLastModifiedResponseHeader(response.header(LAST_MODIFIED_TIME));
        data.setLinks(extractLinks(doc));

        return data;
    }

    private static List<String> extractLinks(Document document) {
        Elements elements = document.select("a[href]");
        List<String> links = new ArrayList<>();

        elements
                .forEach(element -> {
                    links.add(element.attr("href"));
                });

        return links;
    }
}
