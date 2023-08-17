package dev.pyojan.entity.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "request")
public class PKiRequest {
    @JacksonXmlProperty(localName = "command")
    private String command;

    @JacksonXmlProperty(localName = "ts")
    private String ts;

    @JacksonXmlProperty(localName = "txn")
    private String txn;

    @JacksonXmlProperty(localName = "certificate")
    private Certificate certificate;

    @JacksonXmlProperty(localName = "file")
    private File file;

    @JacksonXmlProperty(localName = "pdf")
    private PDF pdf;

    @JacksonXmlProperty(localName = "data")
    private String data;

    // Add constructor(s) and any other methods if needed
}
