package com.nasr.crawlerservice.validator;

import com.nasr.crawlerservice.enumaration.ValidatorResult;
import com.nasr.crawlerservice.util.CrawlerExtractionUtility;
import org.jsoup.Connection;

import java.util.function.Function;

import static com.nasr.crawlerservice.enumaration.ValidatorResult.*;
import static com.nasr.crawlerservice.util.CrawlerExtractionUtility.*;

public interface CrawlValidator extends Function<Connection.Response, ValidatorResult> {

    static CrawlValidator verifyHtmlType(){
        return (response -> isHtmlType(response) ? OK : CONTENT_TYPE_IS_NOT_HTML);
    }
    static CrawlValidator verifyContentAccessible(){
        return (response -> {
           return isValidForExtract(response) ? OK : WEB_PAGE_NOT_ACCESSIBLE;
        });
    }
    default CrawlValidator and(CrawlValidator validator){
        return (response) -> {

            ValidatorResult result = this.apply(response);

            return result==OK ? validator.apply(response) : result;
        };
    }
}
