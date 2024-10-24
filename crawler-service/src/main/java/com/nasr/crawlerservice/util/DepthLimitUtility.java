package com.nasr.crawlerservice.util;

import com.nasr.crawlerservice.domain.UrlData;

import java.net.URI;
import java.net.URISyntaxException;

import static com.nasr.crawlerservice.constant.ApplicationConstant.URL_MAX_EXTERNAL_DEPTH_LIMIT;
import static com.nasr.crawlerservice.constant.ApplicationConstant.URL_MAX_INTERNAL_DEPTH_LIMIT;

public class DepthLimitUtility {

    public static boolean isAllowed(UrlData urlData, String baseDomain) throws URISyntaxException {

        URI uri = new URI(baseDomain);
        String host = uri.getHost();


        int maxDepth = urlData.getUrl().contains(host) ? URL_MAX_INTERNAL_DEPTH_LIMIT : URL_MAX_EXTERNAL_DEPTH_LIMIT;

        return urlData.getDepth() < maxDepth;
    }
}
