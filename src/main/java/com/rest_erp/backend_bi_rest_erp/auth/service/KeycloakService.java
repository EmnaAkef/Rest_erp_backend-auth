package com.rest_erp.backend_bi_rest_erp.auth.service;

import com.rest_erp.backend_bi_rest_erp.auth.config.KeycloakProperties;
import jakarta.ws.rs.core.Response;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class KeycloakService {

    private final KeycloakProperties properties;

    public KeycloakService(KeycloakProperties properties) {
        this.properties = properties;
    }

    private Keycloak getKeycloakInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(properties.getServerUrl())
                .realm(properties.getRealm())
                .clientId(properties.getClientId())
                .clientSecret(properties.getClientSecret())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }

    public String createUser(
            String firstName,
            String lastName,
            String email,
            String companyName
    ) {
        String existingUserId = findUserIdByEmail(email);

        if (existingUserId != null) {
            return existingUserId;
        }

        UserRepresentation user = new UserRepresentation();

        user.setUsername(email);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);
        user.setEmailVerified(true);

        Response response = getKeycloakInstance()
                .realm(properties.getRealm())
                .users()
                .create(user);

        if (response.getStatus() != 201) {
            String error = response.readEntity(String.class);
            response.close();
            throw new RuntimeException("Failed to create user in Keycloak: " + error);
        }

        String location = response.getHeaderString("Location");
        response.close();

        return location.substring(location.lastIndexOf("/") + 1);
    }

    public Map<String, Object> login(String email, String password) {
        String tokenUrl = properties.getServerUrl()
                + "/realms/"
                + properties.getRealm()
                + "/protocol/openid-connect/token";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", properties.getClientId());
        form.add("client_secret", properties.getClientSecret());
        form.add("username", email);
        form.add("password", password);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(form, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(tokenUrl, request, Map.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Invalid login credentials");
        }

        return response.getBody();
    }

    public void sendUpdatePasswordEmail(String userId) {
        Keycloak keycloak = getKeycloakInstance();

        String clientId = properties.getClientId();
        String redirectUri = "http://localhost:4200/login";
        int lifespan = 3600;

        keycloak.realm(properties.getRealm())
                .users()
                .get(userId)
                .executeActionsEmail(
                        clientId,
                        redirectUri,
                        lifespan,
                        List.of("UPDATE_PASSWORD")
                );
    }

    public void assignRealmRoleToUser(String keycloakUserId, String roleName) {
        var realmResource = getKeycloakInstance().realm(properties.getRealm());

        var roleRepresentation = realmResource
                .roles()
                .get(roleName)
                .toRepresentation();

        var userResource = realmResource.users().get(keycloakUserId);

        var existingRoles = userResource
                .roles()
                .realmLevel()
                .listAll();

        boolean alreadyHasRole = existingRoles.stream()
                .anyMatch(role -> role.getName().equals(roleName));

        if (!alreadyHasRole) {
            userResource
                    .roles()
                    .realmLevel()
                    .add(List.of(roleRepresentation));
        }
    }

    public String findUserIdByEmail(String email) {
        var users = getKeycloakInstance()
                .realm(properties.getRealm())
                .users()
                .searchByEmail(email, true);

        if (users == null || users.isEmpty()) {
            return null;
        }

        return users.get(0).getId();
    }
}