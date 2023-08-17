package dev.pyojan;

import javax.smartcardio.*;
import java.io.ByteArrayInputStream;
import java.security.Security;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PKCS11EventDetection {
    public static void main(String[] args) throws CardException {
        String pkcs11Config = "name=mtoken\nlibrary=/home/cispl/Desktop/libcryptoid_pkcs11_mToken.so";
        byte[] pkcs11ConfigBytes = pkcs11Config.getBytes();
        String pkcs11ConfigBase64 = Base64.getEncoder().encodeToString(pkcs11ConfigBytes);

        Security.addProvider(new sun.security.pkcs11.SunPKCS11(new ByteArrayInputStream(pkcs11ConfigBytes)));

        // Get the default terminal factory
        TerminalFactory factory = TerminalFactory.getDefault();

        // List all available card terminals
        CardTerminals terminals = factory.terminals();
        List<CardTerminal> terminalList = terminals.list();

        // Loop through terminals and listen for card events
        for (CardTerminal terminal : terminalList) {
            terminal.waitForCardPresent(0);  // Blocks until a card is inserted
            System.out.println("Card Inserted");

            terminal.waitForCardAbsent(0);   // Blocks until the card is removed
            System.out.println("Card Removed");
        }
        AtomicInteger integer = new AtomicInteger(0);
        while (true) {
            integer.getAndIncrement();
        }

    }
}
