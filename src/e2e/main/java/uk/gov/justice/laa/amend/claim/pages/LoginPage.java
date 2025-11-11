package uk.gov.justice.laa.amend.claim.pages;

import uk.gov.justice.laa.amend.claim.utils.EnvConfig;
import com.microsoft.playwright.Page;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Base64;

public class LoginPage {
    private final Page page;

    private final String userField = "input[name='loginfmt']";
    private final String nextButton = "button:has-text('Next')";
    private final String passwordField = "input[name='passwd']";
    private final String signInButton = "button:has-text('Sign in')";
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

        if (!EnvConfig.mfaSecret().isEmpty()) {
            handleMfa();
        }

        page.waitForSelector("h1:has-text('Submit a bulk claim')",
                new Page.WaitForSelectorOptions().setTimeout(60_000));

        System.out.println("[INFO] Login successful!");
    }

    private void handleMfa() {
        System.out.println("[INFO] Performing MFA step...");
        page.waitForSelector(otcInput, new Page.WaitForSelectorOptions().setTimeout(120_000));

        String secret = EnvConfig.mfaSecret();
        String code = generateTOTP(secret);
        System.out.println("[INFO] Using MFA code: " + code);

        page.fill(otcInput, code);
        page.click(verifyButton);

        // Wait for dashboard
        page.waitForSelector("h1:has-text('Submit a bulk claim')",
                new Page.WaitForSelectorOptions().setTimeout(60_000));
    }

    /**
     * Generates a 6-digit TOTP code using Base32 secret (RFC 6238 compliant)
     */
    private String generateTOTP(String base32Secret) {
        try {
            byte[] key = decodeBase32(base32Secret);
            long timeWindow = Instant.now().getEpochSecond() / 30;
            ByteBuffer buffer = ByteBuffer.allocate(8).putLong(timeWindow);
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            byte[] hash = mac.doFinal(buffer.array());
            int offset = hash[hash.length - 1] & 0xF;
            int binary =
                    ((hash[offset] & 0x7F) << 24)
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
     * Minimal Base32 decoder for TOTP secrets
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
