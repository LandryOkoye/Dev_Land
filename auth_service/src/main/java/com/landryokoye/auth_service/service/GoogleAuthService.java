package com.landryokoye.auth_service.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class GoogleAuthService {

    @Value("${GOOGLE_CLIENT_ID}")
    private String client_id;

    public Optional<GoogleIdToken.Payload> verify(String token) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(client_id))
                    .build();

            GoogleIdToken idToken = verifier.verify(token);
            if (idToken != null) {
                return Optional.of(idToken.getPayload());
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unknown Error validating google id token", e.getCause());
        }
        return Optional.empty();
    }
}
