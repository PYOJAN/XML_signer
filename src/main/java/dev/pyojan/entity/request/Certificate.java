package dev.pyojan.entity.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class Certificate {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "attribute")
    private Attribute[] attributes;
}