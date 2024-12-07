package com.nasr.searchservice.enumeration;

import lombok.Getter;

@Getter
public enum HighlightFieldType {
    TITLE(50),BODY(120);

    private final int fragmentSize;

    HighlightFieldType(int fragmentSize) {
        this.fragmentSize = fragmentSize;
    }

}
