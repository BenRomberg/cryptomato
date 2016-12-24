package com.benromberg.cryptomato.web;

import com.amazonaws.services.lambda.runtime.Context;
import com.benromberg.cryptomato.core.TrustedTimestampProvider;
import com.benromberg.cryptomato.core.dao.UserDao;
import com.benromberg.cryptomato.model.User;
import com.benromberg.cryptomato.web.view.ColdstartResponse;
import com.benromberg.cryptomato.web.view.TimestampTokenRequest;
import com.benromberg.cryptomato.web.view.TimestampTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class CryptomatoResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(CryptomatoResource.class);
    private static int counter = 0;
    private final TrustedTimestampProvider trustedTimestampProvider;
    private final Context lambdaContext;
    private final UserDao userDao;

    @Inject
    public CryptomatoResource(TrustedTimestampProvider trustedTimestampProvider, Context lambdaContext, UserDao userDao) {
        this.trustedTimestampProvider = trustedTimestampProvider;
        this.lambdaContext = lambdaContext;
        this.userDao = userDao;
    }

    @GET
    @Path("/coldstart")
    public ColdstartResponse getColdstart() {
        counter++;
        LOGGER.info("Counter: {}", counter);
        return new ColdstartResponse(counter);
    }

    @POST
    @Path("/createTimestampToken")
    public TimestampTokenResponse createTimestampToken(TimestampTokenRequest request) throws Exception {
        TimestampTokenResponse timestampToken = trustedTimestampProvider.createTimestampToken(request.getUsername(), request.getPublicKey());
        userDao.insert(new User(request.getUsername(), request.getPublicKey(), timestampToken.getEncodedToken(), timestampToken.getTime()));
        return timestampToken;
    }

    @GET
    @Path("/context")
    public Map<String, Object> getContext() throws IOException {
        Map<String, Object> output = new HashMap<>();
        output.put("awsRequestId", lambdaContext.getAwsRequestId());
        output.put("functionName", lambdaContext.getFunctionName());
        output.put("functionVersion", lambdaContext.getFunctionVersion());
        output.put("identity.identityId", lambdaContext.getIdentity().getIdentityId());
        output.put("invokedFunctionArn", lambdaContext.getInvokedFunctionArn());
        output.put("logGroupName", lambdaContext.getLogGroupName());
        output.put("logStreamName", lambdaContext.getLogStreamName());
        output.put("memoryLimitInMB", lambdaContext.getMemoryLimitInMB());
        output.put("remainingTimeInMillis", lambdaContext.getRemainingTimeInMillis());
        output.put("/proc/uptime", Files.readAllLines(Paths.get("/proc/uptime")));
        output.put("/proc/cpuinfo", Files.readAllLines(Paths.get("/proc/cpuinfo")));
        output.put("/proc/version", Files.readAllLines(Paths.get("/proc/version")));
        output.put("/proc/meminfo", Files.readAllLines(Paths.get("/proc/meminfo")));
        output.put("hostname", execCmd("hostname"));
        output.put("hostname -i", execCmd("hostname -i"));
        output.put("env", System.getenv());
        output.put("properties", System.getProperties());
        return output;
    }

    public static String execCmd(String cmd) throws IOException {
        java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}