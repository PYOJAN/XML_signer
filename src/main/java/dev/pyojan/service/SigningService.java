package dev.pyojan.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import dev.pyojan.config.Constant;
import dev.pyojan.entity.request.Attribute;
import dev.pyojan.entity.request.PKiRequest;
import dev.pyojan.entity.response.ApiResponse;
import dev.pyojan.gui.MessageGuiDialog;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import spark.Request;
import spark.Response;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.ProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

public class SigningService {
    private static GetKeyStoreServiceRefactored ks = null;
    private static PrivateKey privateKey = null;
    private static X509Certificate x509Cert = null;
    private static PKiRequest request;

    private static Map<String, String> reqCertInfo;

    public static String signXml(Request req, Response res) {

        try {
            String requestBody = URLDecoder.decode(req.body(), "UTF-8");
            if (requestBody.startsWith("response=")) {
                requestBody = requestBody.split("response=")[1];
            }
            // Ensuring request body
            if (requestBody == null || requestBody.isEmpty() || !requestBody.trim().startsWith("<request>")) {
                throw new RuntimeException(ApiResponse.error("", "OT-03", "Request body is empty"));
            }


            XmlMapper xmlMapper = new XmlMapper();
            request = xmlMapper.readValue(requestBody, PKiRequest.class);

            // PKI COMMAND check
            if (!request.getCommand().equalsIgnoreCase(Constant.COMMAND)) {
                throw new RuntimeException(ApiResponse.error(request.getTxn(), "OT-03", "Invalid PKI command, Allowed command is [ " + Constant.COMMAND + " ]"));
            }

            //Only XML file allowed
            String isXml = request.getFile().getAttributes()[0].getValue();
            if (!Objects.equals(isXml, "xml")) {
                throw new RuntimeException(ApiResponse.error(request.getTxn(), "OT-03", "Only XML file type allowed"));
            }

            final Map<String, String> attributes = processAttributes(request.getCertificate().getAttributes());

            if (privateKey == null) {
                ks = new GetKeyStoreServiceRefactored(attributes, request);
                privateKey = ks.getPrivateKey();
                x509Cert = ks.getSelectedCertificate();
            }


            byte[] decodedXML = Base64.getDecoder().decode(request.getData());
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(decodedXML));

            assert x509Cert != null;
            String signedData = new InstantiateSignXML().instantiatexml(doc, ks.getSelectedCertificate(), ks.getPrivateKey());

            String signedXmlData = Base64.getEncoder().encodeToString(signedData.getBytes(StandardCharsets.UTF_8));
            ks.getKeyStore().load(null, null);
            return ApiResponse.success(request.getTxn(), new Date().toString(), signedXmlData);
        } catch (IOException | ParserConfigurationException e) {
            MessageGuiDialog.showErrorDialog("Initialization Error", "<html>Program Error: " + e.getLocalizedMessage() + "<html>");
            throw new RuntimeException(ApiResponse.error(request.getTxn(), "OT-03", e.getLocalizedMessage()));
        } catch (MarshalException | InvalidAlgorithmParameterException | NoSuchAlgorithmException |
                 XMLSignatureException | TransformerException | SAXException | ProviderException e) {
            performLogout();
            MessageGuiDialog.showErrorDialog("Initialization Error", "<html>Program Error: " + e.getLocalizedMessage() +
                    "<br/><br/>Use combination of <b>CTRL + ATL + Q</b> to stop the Application." +
                    "<br/><br/>Use combination of CTRL + ATL + I to update PKCS11 path." +
                    "<br/><br/>Please recheck PKCS11 path.<html>");
            throw new RuntimeException(ApiResponse.error(request.getTxn(), "OT-03", e.getLocalizedMessage()));
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, String> processAttributes(Attribute[] attrs) {

        Map<String, String> attributes = new HashMap<>();
        for (Attribute attr : attrs) {
            String value = attr.getValue();
            if (value != null && !value.equalsIgnoreCase("sg") && !value.equalsIgnoreCase("1")) {
                attributes.put(attr.getName(), value.trim());
            }
        }
        return attributes;
    }

    public static void performLogout() {
        if (ks != null) {
            ks.logoutKeyStore();
            ks = null;
            privateKey = null;
            x509Cert = null;
        }
    }

}
