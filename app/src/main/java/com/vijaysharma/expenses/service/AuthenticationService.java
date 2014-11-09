package com.vijaysharma.expenses.service;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

public interface AuthenticationService {
    @POST("/login") public Observable<Token> login(@Body Credentials credentials);
    @GET("/logout") public Observable<Void> logout(@Query("token") String token);

    public static class Token {
        private final String username;
        private final String token;

        public Token(String username, String token) {
            this.username = username;
            this.token = token;
        }

        public String getToken() {
            return token;
        }

        public String getUsername() {
            return username;
        }

        @Override
        public String toString() {
            return "Token {" + "username='" + username + '\'' + ", token='" + token + '\'' + '}';
        }
    }

    public static class Credentials {
        private final String username;
        private final String password;

        public Credentials(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}
