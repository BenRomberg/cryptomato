package com.benromberg.cryptomato;

import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.tsp.TimeStampResp;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.tsp.TSPAlgorithms;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.TimeStampToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrustedTimestampProvider {
    private static final String TSA_URL = "https://freetsa.org/tsr";
    private static final SecureRandom SECURE_RANDOM = createSecureRandom();
    private static final Logger LOGGER = LoggerFactory.getLogger(TrustedTimestampProvider.class);

    static {
        // Default Java Security Provider cannot connect to https://freetsa.org/ on AWS Lambda.
        int providerInsertPosition = Security.insertProviderAt(new BouncyCastleProvider(), 1);
        LOGGER.info("Adding Bouncy Castle Security Provider at position {}", providerInsertPosition);
    }

    public TimestampTokenResponse createTimestampToken(String username, String publicKey) throws Exception {
        byte[] digest = createDigest(username, publicKey);
        TimeStampRequest timeStampRequest = new TimeStampRequestGenerator().generate(TSPAlgorithms.SHA512, digest,
                BigInteger.valueOf(SECURE_RANDOM.nextInt()));
        TimeStampToken token = createTimestampToken(timeStampRequest);

        LOGGER.info("Timestamp: {}", token.getTimeStampInfo().getGenTime());
        LOGGER.info("TSA: {}", token.getTimeStampInfo().getTsa());
        LOGGER.info("Serial number: {}", token.getTimeStampInfo().getSerialNumber());
        LOGGER.info("Policy: {}", token.getTimeStampInfo().getPolicy());
        LOGGER.info("Signature: {}", token.getSignedAttributes().toHashtable());
        String encodedToken = Base64.getEncoder().encodeToString(token.getEncoded());
        return new TimestampTokenResponse(username, LocalDateTime.ofInstant(token.getTimeStampInfo().getGenTime()
                .toInstant(), ZoneOffset.UTC), encodedToken);
    }

    private TimeStampToken createTimestampToken(TimeStampRequest timeStampRequest) throws Exception {
        TimeStampResponse response = sendRequest(timeStampRequest);
        response.validate(timeStampRequest);

        if (response.getFailInfo() != null) {
            LOGGER.info("TSA failed with value {}: {}", response.getFailInfo().intValue(), response.getStatusString());
            throw new WebApplicationException(Status.SERVICE_UNAVAILABLE);
        }
        return response.getTimeStampToken();
    }

    private TimeStampResponse sendRequest(TimeStampRequest timeStampRequest) throws Exception {
        byte[] request = timeStampRequest.getEncoded();

        Client client = ClientBuilder.newClient();
        Response response = client.target(TSA_URL).request()
                .post(Entity.entity(request, "application/timestamp-query"));
        int status = response.getStatus();

        if (status != HttpURLConnection.HTTP_OK) {
            LOGGER.warn("TSA returned HTTP status {}: {}", status, response.readEntity(String.class));
            throw new WebApplicationException(Status.SERVICE_UNAVAILABLE);
        }
        try (InputStream in = (InputStream) response.getEntity();
                ASN1InputStream asn1InputStream = new ASN1InputStream(in)) {
            TimeStampResp resp = TimeStampResp.getInstance(asn1InputStream.readObject());
            return new TimeStampResponse(resp);
        }
    }

    private byte[] createDigest(String username, String publicKey) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(publicKey.getBytes(StandardCharsets.UTF_8));
        return md.digest(username.getBytes(StandardCharsets.UTF_8));
    }

    private static SecureRandom createSecureRandom() {
        try {
            return SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
