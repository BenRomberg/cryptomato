package com.benromberg.cryptomato;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.DefaultCMSSignatureAlgorithmNameGenerator;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.cms.bc.BcRSASignerInfoVerifierBuilder;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.tsp.TimeStampToken;
import org.junit.Test;

public class TrustedTimestampProviderTest {
    private static final String PUBLIC_KEY = "public key";
    private static final String USERNAME = "username";

    @Test
    public void createdToken_HasCorrectValues() throws Exception {
        TimestampTokenResponse tokenResponse = new TrustedTimestampProvider()
                .createTimestampToken(USERNAME, PUBLIC_KEY);

        byte[] token = Base64.getDecoder().decode(tokenResponse.getEncodedToken());
        TimeStampToken readToken = new TimeStampToken(new CMSSignedData(token));

        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        X509Certificate cer = (X509Certificate) fact.generateCertificate(getClass().getClassLoader()
                .getResourceAsStream("freetsa.crt"));
        X509CertificateHolder holder = new X509CertificateHolder(cer.getEncoded());
        SignerInformationVerifier siv = new BcRSASignerInfoVerifierBuilder(
                new DefaultCMSSignatureAlgorithmNameGenerator(), new DefaultSignatureAlgorithmIdentifierFinder(),
                new DefaultDigestAlgorithmIdentifierFinder(), new BcDigestCalculatorProvider()).build(holder);

        readToken.validate(siv);
        assertThat(readToken.getTimeStampInfo().getMessageImprintDigest()).isEqualTo(createDigest());
        assertThat(tokenResponse.getUsername()).isEqualTo(USERNAME);
        assertThat(Duration.between(tokenResponse.getTime(), LocalDateTime.now(ZoneOffset.UTC))).isLessThan(
                Duration.ofMinutes(1));
    }

    private byte[] createDigest() throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(PUBLIC_KEY.getBytes(StandardCharsets.UTF_8));
        return md.digest(USERNAME.getBytes(StandardCharsets.UTF_8));
    }
}
