package com.example.kafka.auth;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.callback.Callback;
import javax.security.auth.login.AppConfigurationEntry;

import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.credentials.AppServiceMSICredentials;
import com.microsoft.azure.credentials.AzureTokenCredentials;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import org.apache.kafka.common.security.auth.AuthenticateCallbackHandler;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerToken;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerTokenCallback;

public class MSICallbackHandler implements AuthenticateCallbackHandler {
    private final AzureTokenCredentials msiCredentials;

    private String resource;

    public MSICallbackHandler() {
        this.msiCredentials = new AppServiceMSICredentials(AzureEnvironment.AZURE);
    }

    @Override
    public void configure(Map<String, ?> map, String s, List<AppConfigurationEntry> list) {
        String server = ((List<String>) map.get("bootstrap.servers")).get(0);
        int idx = server.indexOf(":");
        String hostName = idx < 0 ? server : server.substring(0, idx);
        this.resource = String.format("https://%s", hostName);
//        this.resource = "https://eventhubs.azure.net/";
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException {
        for (Callback c : callbacks) {
            if (c instanceof OAuthBearerTokenCallback) {
                setToken((OAuthBearerTokenCallback) c);
            }
        }
    }

    private void setToken(OAuthBearerTokenCallback callback) throws IOException {
        String token = msiCredentials.getToken(resource);
        try {
            callback.token(new JWTBearerToken(token));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void close() {

    }
    
    private static final class JWTBearerToken implements OAuthBearerToken {
        private final String token;
        private final JWTClaimsSet claims;

        JWTBearerToken(String token) throws ParseException {
            this.token = token;
            JWT jwt = JWTParser.parse(token);
            this.claims = jwt.getJWTClaimsSet();
        }

        @Override
        public String value() {
            return this.token;
        }

        @Override
        public Set<String> scope() {
            return Collections.emptySet();
        }

        @Override
        public long lifetimeMs() {
            return this.claims.getExpirationTime().getTime();
        }

        @Override
        public String principalName() {
            return this.claims.getSubject();
        }

        @Override
        public Long startTimeMs() {
            return this.claims.getIssueTime().getTime();
        }
    }
}
