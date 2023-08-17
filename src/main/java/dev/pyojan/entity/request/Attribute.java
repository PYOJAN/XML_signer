package dev.pyojan.entity.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Attribute {
    @JacksonXmlProperty(isAttribute = true)
    private String name;

//    @JacksonXmlProperty
    @JacksonXmlText
    private String value;
}