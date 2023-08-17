package dev.pyojan.entity.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class PDF {
    @JacksonXmlProperty
    private int page;

    @JacksonXmlProperty
    private String cood;

    @JacksonXmlProperty
    private String size;

    // Add constructor(s) and any other methods if needed
}