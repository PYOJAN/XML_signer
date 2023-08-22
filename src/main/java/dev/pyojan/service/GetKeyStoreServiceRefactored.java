package dev.pyojan.service;

import dev.pyojan.config.Config;
import dev.pyojan.config.Constant;
import dev.pyojan.entity.request.PKiRequest;
import dev.pyojan.entity.response.ApiResponse;
import dev.pyojan.gui.CertificateListChoose;
import dev.pyojan.gui.MessageGuiDialog;
import dev.pyojan.gui.PasswordDialog;
import dev.pyojan.util.Utils;
import lombok.Getter;
import sun.security.pkcs11.SunPKCS11;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.*;
import java.util.*;
import java.util.stream.Collectors;

public class GetKeyStoreServiceRefactored extends Config {

    private static Provider provider = null;
    private static char[] PIN = null;
    PKiRequest req = null;
    private Map<String, X509Certificate> allX509Certificate = new HashMap<>();
    private PasswordDialog passwordDialog = null;
    @Getter
    private KeyStore keyStore = null;
    @Getter
    private PrivateKey privateKey = null;
    @Getter
    private X509Certificate selectedCertificate = null;
    private Map<String, String> certAttribute = new HashMap<>();

    public GetKeyStoreServiceRefactored(Map<String, String> attributes, PKiRequest request) {

        if (request != null) {
            req = request;
        }
        certAttribute = attributes;
        initializeComponents();
    }

    private static boolean isUserCertificate(X509Certificate certificate) throws CertificateParsingException {
        boolean isCA = certificate.getBasicConstraints() != -1;
        boolean[] keyUsage = certificate.getKeyUsage();
        return !isCA && (certificate.getExtendedKeyUsage() != null || keyUsage[0] || keyUsage[1]);
    }

    private void initializeComponents() {
        loadProvider();
        loadUserCertificatesFromKeyStore();
        prepareCertificateForSigning();
    }

    private void loadProvider() {
        String pkcs11LibraryPath = getPropertyWithKey(Constant.PKCS11_LIB_KEY);

        if (pkcs11LibraryPath == null) {
            pkcs11LibraryPath = pkcs11Chooser();
        }

        byte[] libraryBytes = String.format("name=pyojan\nlibrary=%s", pkcs11LibraryPath).getBytes();
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(libraryBytes)) {
            provider = new SunPKCS11(inputStream);
            Security.addProvider(provider);
        } catch (IOException e) {
            MessageGuiDialog.showErrorDialog("Reading Token Error", "<html>PKCS#11 provider: " + e.getMessage() + "<br>Please ensure to login by token driver first.</html>");
            throw new RuntimeException(ApiResponse.error(req.getTxn(), "OT-02", e.getLocalizedMessage()));
        }
    }

    private void loadUserCertificatesFromKeyStore() {
        passwordDialog = new PasswordDialog("Token PIN prompt");

        while (true) {
            passwordDialog.resetField();
            if (!Utils.isRememberPin) {
                passwordDialog.setVisible(true);
            }
            if (passwordDialog.didUserCancel()) {
                logoutKeyStore();
                throw new RuntimeException(ApiResponse.error(req.getTxn(), "OT-02", "User canceled the request"));
            }

            try {
                keyStore = KeyStore.getInstance("PKCS11", provider);
                PIN = passwordDialog.getEnteredPassword();
                keyStore.load(null, PIN);

                allX509Certificate.clear();
                Enumeration<String> aliases = keyStore.aliases();
                while (aliases.hasMoreElements()) {
                    String alias = aliases.nextElement();
                    X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
                    if (isUserCertificate(certificate)) {
                        isCertificateExpire(certificate);
                        allX509Certificate.put(alias, certificate);

                    }
                }

                if (!certAttribute.isEmpty()) {
                    filterCertificatesByAttributes(certAttribute);
                }

                break;
            } catch (GeneralSecurityException | IOException e) {
                handleException(e);
            }
        }
    }

    private void filterCertificatesByAttributes(Map<String, String> attributes) {
        Map<String, X509Certificate> filteredCertificates = new HashMap<>();
        List<String> certAttrList = new ArrayList<>();

        allX509Certificate.forEach((alias, certificate) -> {
            attributes.forEach((attr, value) -> {
                if (attr.equalsIgnoreCase("E")) {
                    String subjectAlterNativeName = getSubjectAlterNativeName(certificate);
                    if (subjectAlterNativeName.equalsIgnoreCase(value)) {
                        filteredCertificates.put(alias, certificate);
                        certAttrList.add(subjectAlterNativeName);
                    }
                } else if (attr.equalsIgnoreCase("SN")) {
                    String serialNumber = certificate.getSerialNumber().toString(16);
                    if (serialNumber.equalsIgnoreCase(value)) {
                        filteredCertificates.put(alias, certificate);
                        certAttrList.add(serialNumber);
                    }
                } else {
                    if (certificate.getSubjectX500Principal().toString().contains(value)) {
                        filteredCertificates.put(alias, certificate);
                        certAttrList.add(value);
                    }
                }
            });
        });

        validateFilteredCertificates(certAttrList);
        allX509Certificate = filteredCertificates;
    }

    private void validateFilteredCertificates(List<String> attrList) {
        List<String> newParsedCertList = certAttribute.entrySet().stream()
                .filter(entry -> !entry.getKey().equalsIgnoreCase("SN"))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        if (!compareLists(newParsedCertList, attrList)) {
            logoutKeyStore();
            MessageGuiDialog.showErrorDialog("Error", "Certificate not found");
            throw new RuntimeException(ApiResponse.error(req.getTxn(), "OT-02", "Certificate not found!"));
        }
    }

    private void isCertificateExpire(X509Certificate certificate) {
        assert certificate != null;
        Date currentDate = new Date();
        if (currentDate.after(certificate.getNotAfter())) {
            MessageGuiDialog.showErrorDialog("Certificate Error", "Certificate is expired.");
            throw new RuntimeException(ApiResponse.error(req.getTxn(), "OT-02", "Certificate is expired."));
        }
    }

    private boolean isSelfSignCertificate(X509Certificate cert) {
        if (cert == null) {
            logoutKeyStore();
            throw new RuntimeException(ApiResponse.error(req.getTxn(), "OT-02", "User canceled the request"));
        }
        try {
            PublicKey key = cert.getPublicKey();
            cert.verify(key);
            return true;
        } catch (CertificateException | NoSuchProviderException | InvalidKeyException | NoSuchAlgorithmException |
                 SignatureException e) {
            return false;
        }
    }

    public void prepareCertificateForSigning() {
        if (allX509Certificate.size() == 1) {
            preparePrivateKey(allX509Certificate.values().iterator().next());

            final X509Certificate cert = allX509Certificate.values().iterator().next();

            if (isSelfSignCertificate(cert)) {
                MessageGuiDialog.showErrorDialog("Certificate Error", "Invalid certificate, [Self signed]");
                throw new RuntimeException(ApiResponse.error(req.getTxn(), "OT-05", "Invalid certificate, [Self signed]"));
            }

            selectedCertificate = cert;
        } else if (allX509Certificate.size() > 1) {
            CertificateListChoose certificateListChoose = new CertificateListChoose();
            certificateListChoose.setCertificateList(prepareCertificateList());

            String selectedCertificateSN = certificateListChoose.getSelectedCertificate();
            selectedCertificate = getCertificateBySerialNumber(selectedCertificateSN);

            assert selectedCertificate != null;
            if (isSelfSignCertificate(selectedCertificate)) {
                logoutKeyStore();
                MessageGuiDialog.showErrorDialog("Certificate Error", "Invalid certificate, [Self signed]");
                throw new RuntimeException(ApiResponse.error(req.getTxn(), "OT-05", "Invalid certificate, [Self signed]"));
            }


            if (selectedCertificate != null) {
                preparePrivateKey(selectedCertificate);
            }
        }
    }

    private List<Object[]> prepareCertificateList() {
        List<Object[]> list = new ArrayList<>();
        allX509Certificate.forEach((alias, cert) -> {
            String CN = cert.getSubjectX500Principal().toString().split(",")[0].split("=")[1];
            String SN = cert.getSerialNumber().toString(16);
            String expiry = cert.getNotAfter().toString();
            list.add(new Object[]{CN, SN, expiry});
        });
        return list;
    }

    private void preparePrivateKey(X509Certificate certificate) {
        String alias = getAliasByCertificate(certificate);
        try {
            privateKey = (PrivateKey) keyStore.getKey(alias, passwordDialog.getEnteredPassword());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            MessageGuiDialog.showErrorDialog("Certificate Error:", "<html>Error while reading PrivateKey:<b>" + e.getLocalizedMessage() + "</html>");
            throw new RuntimeException(ApiResponse.error(req.getTxn(), "OT-02", "<html>Error while reading PrivateKey:<b>" + e.getLocalizedMessage() + "</html>"));
        }
    }

    private String getAliasByCertificate(X509Certificate certificate) {
        for (Map.Entry<String, X509Certificate> entry : allX509Certificate.entrySet()) {
            if (entry.getValue().equals(certificate)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private X509Certificate getCertificateBySerialNumber(String serialNumber) {
        for (X509Certificate certificate : allX509Certificate.values()) {
            String SN = certificate.getSerialNumber().toString(16);
            if (SN.equals(serialNumber)) {
                return certificate;
            }
        }
        return null;
    }

    private String getSubjectAlterNativeName(X509Certificate X509cert) {
        Object subjectAlterNativeName = null;
        try {
            Collection<List<?>> subjectAlternativeNames = X509cert.getSubjectAlternativeNames();

            if (subjectAlternativeNames != null) {
                for (List<?> san : subjectAlternativeNames) {
                    int sanType = (Integer) san.get(0);
                    subjectAlterNativeName = san.get(1);
                }
            } else {
                System.out.println("No Subject Alternative Names found in the certificate.");
                logoutKeyStore();
                MessageGuiDialog.showErrorDialog("Certificate Error", "<html>Certificate is not match with Login Email<br/>" + certAttribute.get("E") + "</html>");
                throw new RuntimeException(ApiResponse.error(req.getTxn(), "OT-03", "Certificate Not found!"));
            }
        } catch (CertificateParsingException e) {
            throw new RuntimeException(ApiResponse.error(req.getTxn(), "OT-03", e.getLocalizedMessage()));
        }
        return (String) subjectAlterNativeName;
    }

    private <T> boolean compareLists(List<T> list1, List<T> list2) {
        // Step 1: Check Sizes
        if (list1.size() != list2.size()) {
            return false;
        }
        // Step 2: Compare Elements
        for (int i = 0; i < list1.size(); i++) {
            if (!list1.get(i).equals(list2.get(i))) {
                return false;
            }
        }

        return true;
    }

    private void handleException(Exception e) {
        if (e instanceof IOException) {
            MessageGuiDialog.showErrorDialog("User PIN Error", "PIN is Incorrect");
        } else if (e instanceof CertificateExpiredException || e instanceof CertificateNotYetValidException) {
            MessageGuiDialog.showErrorDialog("Certificate Error", "Certificate is expired or not yet valid.");
            throw new RuntimeException(ApiResponse.error(req.getTxn(), "OT-02", "Certificate is expired or not yet valid."));
        } else {
            MessageGuiDialog.showErrorDialog("Token Not found!", e.getLocalizedMessage());
            throw new RuntimeException(ApiResponse.error(req.getTxn(), "OT-02", e.getLocalizedMessage()));
        }
    }

    public void logoutKeyStore() {
        if (provider != null) {
            Security.removeProvider(provider.getName());
            provider = null;
            PIN = null;
            allX509Certificate = new HashMap<>();
            passwordDialog = null;
            keyStore = null;
            privateKey = null;
            selectedCertificate = null;
            certAttribute = new HashMap<>();
        }
    }
}
