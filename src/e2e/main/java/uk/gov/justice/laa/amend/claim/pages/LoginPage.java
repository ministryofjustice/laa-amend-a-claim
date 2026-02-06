package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Page;
import java.nio.ByteBuffer;
import java.time.Instant;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import uk.gov.justice.laa.amend.claim.config.EnvConfig;

public class LoginPage {
    private final Page page;

    private final String userField = "input[name='loginfmt'], input[type='email']";
    private final String nextButton =
            "input[type='submit'], button[type='submit'], button:has-text('Next'), button:has-text('Continue'), button:has-text('Sign in')";
    private final String passwordField = "input[name='passwd']";
    private final String signInButton = "input[type='submit'], button:has-text('Sign in')";
    private final String otcInput = "input[name='otc']";
    private final String verifyButton = "input[id='idSubmit_SAOTCC_Continue']";

    public LoginPage(Page page) {
        this.page = page;
    }

    public LoginPage navigate() {
        String url = EnvConfig.baseUrl();
        System.out.println("[INFO] Navigating to: " + url);
        page.navigate(url, new Page.NavigateOptions().setTimeout(120_000));
        return this;
    }

    public void login() {
        System.out.println("[INFO] Starting login flow...");

        page.waitForSelector(userField, new Page.WaitForSelectorOptions().setTimeout(120_000));
        page.fill(userField, EnvConfig.username());
        page.click(nextButton);

        page.waitForSelector(passwordField, new Page.WaitForSelectorOptions().setTimeout(120_000));
        page.fill(passwordField, EnvConfig.password());
        page.click(signInButton);

        String mfaSecret = EnvConfig.mfaSecret();
        if (mfaSecret != null && !mfaSecret.isBlank()) {
            handleMfa(mfaSecret);
        }

        page.waitForSelector("h1:has-text('Search for a claim')", new Page.WaitForSelectorOptions().setTimeout(60_000));

        System.out.println("[INFO] Login successful!");
    }

    private void handleMfa(String secret) {
        System.out.println("[INFO] Performing MFA step...");
        page.waitForSelector(otcInput, new Page.WaitForSelectorOptions().setTimeout(120_000));

        // MFA secret is securely loaded from the environment via EnvConfig
        String code = generateTOTP(secret);
        System.out.println("[INFO] Generated MFA code securely.");

        page.fill(otcInput, code);
        page.click(verifyButton);

        page.waitForSelector("h1:has-text('Search for a claim')", new Page.WaitForSelectorOptions().setTimeout(60_000));
    }

    /**
     * Generates a 6-digit TOTP code using a Base32-encoded secret (RFC 6238 compliant).
     * The secret must be provided securely from environment variables.
     */
    private String generateTOTP(String base32Secret) {
        try {
            // Derive key dynamically, not hardcoded
            byte[] keyBytes = decodeBase32(base32Secret);
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes.clone(), "HmacSHA1");

            long timeWindow = Instant.now().getEpochSecond() / 30;
            ByteBuffer buffer = ByteBuffer.allocate(8).putLong(timeWindow);

            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);

            byte[] hash = mac.doFinal(buffer.array());
            int offset = hash[hash.length - 1] & 0xF;
            int binary = ((hash[offset] & 0x7F) << 24)
                    | ((hash[offset + 1] & 0xFF) << 16)
                    | ((hash[offset + 2] & 0xFF) << 8)
                    | (hash[offset + 3] & 0xFF);
            int otp = binary % 1_000_000;

            return String.format("%06d", otp);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate TOTP code", e);
        }
    }

    /**
     * Decodes a Base32 string into bytes for TOTP key derivation.
     * This method avoids static, embedded cryptographic material.
     */
    private byte[] decodeBase32(String secret) {
        secret = secret.replace(" ", "").toUpperCase();
        String base32Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
        ByteBuffer buffer = ByteBuffer.allocate(secret.length() * 5 / 8);
        int bits = 0, value = 0;
        for (char c : secret.toCharArray()) {
            int index = base32Chars.indexOf(c);
            if (index < 0) continue;
            value = (value << 5) | index;
            bits += 5;
            if (bits >= 8) {
                buffer.put((byte) ((value >> (bits - 8)) & 0xFF));
                bits -= 8;
            }
        }
        buffer.flip();
        byte[] out = new byte[buffer.remaining()];
        buffer.get(out);
        return out;
    }
}
