package no.difi.move.common.oauth;

import lombok.Synchronized;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("unused")
public class SyncedOauth2AuthorizedClientManager implements OAuth2AuthorizedClientManager {

    private static final OAuth2AuthorizedClientProvider DEFAULT_AUTHORIZED_CLIENT_PROVIDER = OAuth2AuthorizedClientProviderBuilder
        .builder().clientCredentials().build();

    private final ClientRegistrationRepository clientRegistrationRepository;

    private final OAuth2AuthorizedClientService authorizedClientService;

    private OAuth2AuthorizedClientProvider authorizedClientProvider;

    private Function<OAuth2AuthorizeRequest, Map<String, Object>> contextAttributesMapper;

    private OAuth2AuthorizationSuccessHandler authorizationSuccessHandler;

    private OAuth2AuthorizationFailureHandler authorizationFailureHandler;

    /**
     * Constructs an {@code SyncedOauth2AuthorizedClientManager} using
     * the provided parameters.
     *
     * @param clientRegistrationRepository the repository of client registrations
     * @param authorizedClientService      the authorized client service
     */
    public SyncedOauth2AuthorizedClientManager(
        ClientRegistrationRepository clientRegistrationRepository,
        OAuth2AuthorizedClientService authorizedClientService) {
        Assert.notNull(clientRegistrationRepository, "clientRegistrationRepository cannot be null");
        Assert.notNull(authorizedClientService, "authorizedClientService cannot be null");
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.authorizedClientService = authorizedClientService;
        this.authorizedClientProvider = DEFAULT_AUTHORIZED_CLIENT_PROVIDER;
        this.contextAttributesMapper = new AuthorizedClientServiceOAuth2AuthorizedClientManager.DefaultContextAttributesMapper();
        this.authorizationSuccessHandler = (authorizedClient, principal, attributes) -> authorizedClientService
            .saveAuthorizedClient(authorizedClient, principal);
        this.authorizationFailureHandler = new RemoveAuthorizedClientOAuth2AuthorizationFailureHandler(
            (clientRegistrationId, principal, attributes) -> authorizedClientService
                .removeAuthorizedClient(clientRegistrationId, principal.getName()));
    }

    @Nullable
    @Synchronized
    @Override
    public OAuth2AuthorizedClient authorize(OAuth2AuthorizeRequest authorizeRequest) {
        Assert.notNull(authorizeRequest, "authorizeRequest cannot be null");
        String clientRegistrationId = authorizeRequest.getClientRegistrationId();
        OAuth2AuthorizedClient authorizedClient = authorizeRequest.getAuthorizedClient();
        Authentication principal = authorizeRequest.getPrincipal();
        OAuth2AuthorizationContext.Builder contextBuilder;
        if (authorizedClient != null) {
            contextBuilder = OAuth2AuthorizationContext.withAuthorizedClient(authorizedClient);
        } else {
            ClientRegistration clientRegistration = this.clientRegistrationRepository
                .findByRegistrationId(clientRegistrationId);
            Assert.notNull(clientRegistration,
                "Could not find ClientRegistration with id '" + clientRegistrationId + "'");
            authorizedClient = this.authorizedClientService.loadAuthorizedClient(clientRegistrationId,
                principal.getName());
            if (authorizedClient != null) {
                contextBuilder = OAuth2AuthorizationContext.withAuthorizedClient(authorizedClient);
            } else {
                contextBuilder = OAuth2AuthorizationContext.withClientRegistration(clientRegistration);
            }
        }
        OAuth2AuthorizationContext authorizationContext = buildAuthorizationContext(authorizeRequest, principal,
            contextBuilder);
        try {
            authorizedClient = this.authorizedClientProvider.authorize(authorizationContext);
        } catch (OAuth2AuthorizationException ex) {
            this.authorizationFailureHandler.onAuthorizationFailure(ex, principal, Collections.emptyMap());
            throw ex;
        }
        if (authorizedClient != null) {
            this.authorizationSuccessHandler.onAuthorizationSuccess(authorizedClient, principal,
                Collections.emptyMap());
        } else {
            // In the case of re-authorization, the returned `authorizedClient` may be
            // null if re-authorization is not supported.
            // For these cases, return the provided
            // `authorizationContext.authorizedClient`.
            if (authorizationContext.getAuthorizedClient() != null) {
                return authorizationContext.getAuthorizedClient();
            }
        }
        return authorizedClient;
    }

    private OAuth2AuthorizationContext buildAuthorizationContext(OAuth2AuthorizeRequest authorizeRequest,
                                                                 Authentication principal, OAuth2AuthorizationContext.Builder contextBuilder) {
        return contextBuilder.principal(principal)
            .attributes(attributes -> {
                Map<String, Object> contextAttributes = this.contextAttributesMapper.apply(authorizeRequest);
                if (!CollectionUtils.isEmpty(contextAttributes)) {
                    attributes.putAll(contextAttributes);
                }
            })
            .build();
    }

    /**
     * Sets the {@link OAuth2AuthorizedClientProvider} used for authorizing (or
     * re-authorizing) an OAuth 2.0 Client.
     *
     * @param authorizedClientProvider the {@link OAuth2AuthorizedClientProvider} used for
     *                                 authorizing (or re-authorizing) an OAuth 2.0 Client
     */
    public void setAuthorizedClientProvider(OAuth2AuthorizedClientProvider authorizedClientProvider) {
        Assert.notNull(authorizedClientProvider, "authorizedClientProvider cannot be null");
        this.authorizedClientProvider = authorizedClientProvider;
    }

    /**
     * Sets the {@code Function} used for mapping attribute(s) from the
     * {@link OAuth2AuthorizeRequest} to a {@code Map} of attributes to be associated to
     * the {@link OAuth2AuthorizationContext#getAttributes() authorization context}.
     *
     * @param contextAttributesMapper the {@code Function} used for supplying the
     *                                {@code Map} of attributes to the {@link OAuth2AuthorizationContext#getAttributes()
     *                                authorization context}
     */
    public void setContextAttributesMapper(
        Function<OAuth2AuthorizeRequest, Map<String, Object>> contextAttributesMapper) {
        Assert.notNull(contextAttributesMapper, "contextAttributesMapper cannot be null");
        this.contextAttributesMapper = contextAttributesMapper;
    }

    /**
     * Sets the {@link OAuth2AuthorizationSuccessHandler} that handles successful
     * authorizations.
     *
     * <p>
     * The default saves {@link OAuth2AuthorizedClient}s in the
     * {@link OAuth2AuthorizedClientService}.
     *
     * @param authorizationSuccessHandler the {@link OAuth2AuthorizationSuccessHandler}
     *                                    that handles successful authorizations
     * @since 5.3
     */
    public void setAuthorizationSuccessHandler(OAuth2AuthorizationSuccessHandler authorizationSuccessHandler) {
        Assert.notNull(authorizationSuccessHandler, "authorizationSuccessHandler cannot be null");
        this.authorizationSuccessHandler = authorizationSuccessHandler;
    }

    /**
     * Sets the {@link OAuth2AuthorizationFailureHandler} that handles authorization
     * failures.
     *
     * <p>
     * A {@link RemoveAuthorizedClientOAuth2AuthorizationFailureHandler} is used by
     * default.
     *
     * @param authorizationFailureHandler the {@link OAuth2AuthorizationFailureHandler}
     *                                    that handles authorization failures
     * @see RemoveAuthorizedClientOAuth2AuthorizationFailureHandler
     * @since 5.3
     */
    public void setAuthorizationFailureHandler(OAuth2AuthorizationFailureHandler authorizationFailureHandler) {
        Assert.notNull(authorizationFailureHandler, "authorizationFailureHandler cannot be null");
        this.authorizationFailureHandler = authorizationFailureHandler;
    }

    /**
     * The default implementation of the {@link #setContextAttributesMapper(Function)
     * contextAttributesMapper}.
     */
    public static class DefaultContextAttributesMapper
        implements Function<OAuth2AuthorizeRequest, Map<String, Object>> {

        @Override
        public Map<String, Object> apply(OAuth2AuthorizeRequest authorizeRequest) {
            Map<String, Object> contextAttributes = Collections.emptyMap();
            String scope = authorizeRequest.getAttribute(OAuth2ParameterNames.SCOPE);
            if (StringUtils.hasText(scope)) {
                contextAttributes = new HashMap<>();
                contextAttributes.put(OAuth2AuthorizationContext.REQUEST_SCOPE_ATTRIBUTE_NAME,
                    StringUtils.delimitedListToStringArray(scope, " "));
            }
            return contextAttributes;
        }

    }
}
