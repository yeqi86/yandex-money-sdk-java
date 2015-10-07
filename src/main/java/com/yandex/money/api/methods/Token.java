/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 NBCO Yandex.Money LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.yandex.money.api.methods;

import com.yandex.money.api.model.Error;
import com.yandex.money.api.net.HostsProvider;
import com.yandex.money.api.net.MethodResponse;
import com.yandex.money.api.net.PostRequest;
import com.yandex.money.api.typeadapters.RevokeTypeAdapter;
import com.yandex.money.api.typeadapters.TokenTypeAdapter;
import com.yandex.money.api.utils.Strings;

/**
 * Access token.
 *
 * @author Slava Yasevich (vyasevich@yamoney.ru)
 */
public class Token implements MethodResponse {

    public final String accessToken;
    public final Error error;

    /**
     * Constructor.
     *
     * @param accessToken access token itself
     * @param error       error code
     */
    public Token(String accessToken, Error error) {
        this.accessToken = accessToken;
        this.error = error;
    }

    @Override
    public String toString() {
        return "Token{" +
                "accessToken='" + accessToken + '\'' +
                ", error=" + error +
                '}';
    }

    /**
     * Request for access token.
     */
    public static class Request extends PostRequest<Token> {

        /**
         * Constructor.
         *
         * @param code        temporary code
         * @param clientId    application's client id
         * @param redirectUri redirect uri
         */
        public Request(String code, String clientId, String redirectUri) {
            this(code, clientId, redirectUri, null);
        }

        /**
         * Constructor.
         *
         * @param code         temporary code
         * @param clientId     application's client id
         * @param redirectUri  redirect uri
         * @param clientSecret a secret word for verifying application's authenticity.
         */
        public Request(String code, String clientId, String redirectUri, String clientSecret) {
            super(Token.class, TokenTypeAdapter.getInstance());
            if (Strings.isNullOrEmpty(code)) {
                throw new NullPointerException("code is null or empty");
            }
            if (Strings.isNullOrEmpty(clientId)) {
                throw new NullPointerException("clientId is null or empty");
            }
            addParameter("code", code);
            addParameter("client_id", clientId);
            addParameter("grant_type", "authorization_code");
            addParameter("redirect_uri", redirectUri);
            addParameter("client_secret", clientSecret);
        }

        @Override
        public String requestUrl(HostsProvider hostsProvider) {
            return hostsProvider.getSpMoney() + "/oauth/token";
        }
    }

    /**
     * Revokes access token.
     */
    public static final class Revoke extends PostRequest<Revoke> {

        /**
         * Revoke only one token.
         */
        public Revoke() {
            this(false);
        }

        /**
         * Revoke token.
         *
         * @param revokeAll if {@code true} all bound tokens will be also revoked
         */
        public Revoke(boolean revokeAll) {
            super(Revoke.class, RevokeTypeAdapter.getInstance());
            addParameter("revoke-all", revokeAll);
        }

        @Override
        public String requestUrl(HostsProvider hostsProvider) {
            return hostsProvider.getMoneyApi() + "/revoke";
        }
    }
}
