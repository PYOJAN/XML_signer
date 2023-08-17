package dev.pyojan.service;

/*    */

import org.w3c.dom.Document;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class InstantiateSignXML {

    public String instantiatexml(Document doc, X509Certificate cert, PrivateKey pk) throws IOException, MarshalException, XMLSignatureException, TransformerException, InvalidAlgorithmParameterException, NoSuchAlgorithmException {

        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

        Reference ref = fac.newReference("",
                fac.newDigestMethod(DigestMethod.SHA256, null),
                Collections.singletonList(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)), null, "PYOJAN_SIGNER");
        SignedInfo si = fac.newSignedInfo(
                fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE,
                        (C14NMethodParameterSpec) null), fac.newSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", null),
                Collections.singletonList(ref));

        KeyInfoFactory kif = fac.getKeyInfoFactory();
        List<Object> x509Content = new ArrayList<>();
        x509Content.add(cert.getSubjectX500Principal().getName());
        x509Content.add(cert);

        X509Data xd = kif.newX509Data(x509Content);
        KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setNamespaceAware(true);
        StringWriter os = null;

        DOMSignContext dsc = new DOMSignContext(pk, doc.getDocumentElement());
        XMLSignature signature = fac.newXMLSignature(si, ki);

        signature.sign(dsc);

        os = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        trans.transform(new DOMSource(doc), new StreamResult(os));

        os.flush();
        os.close();

        return os.toString().replaceAll("\\r?\\n|&#13;", "");
    }

}