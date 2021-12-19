package com.sky.fodmapApp.ft.utility;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class Client {

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public HttpResponse sendHttpRequest(String endPoint, String... headersAndValues){

        if(headersAndValues.length > 0){

            HttpRequest httpRequest = HttpRequest.newBuilder().headers(headersAndValues).uri(createURIForRequest(endPoint)).build();
            return useClientToSendRequest(httpRequest);
        } else {

            HttpRequest httpRequest = HttpRequest.newBuilder().uri(createURIForRequest(endPoint)).build();
            return useClientToSendRequest(httpRequest);
        }

    }

    private URI createURIForRequest(String endPoint){
        String urlString = UriComponentsBuilder
                .fromUriString("http://localhost:8080")
                .path(endPoint)
                .build()
                .toString();
        try {
            URL url = new URL(urlString);
            return URI.create(url.toString());
        } catch (MalformedURLException e) {
            return null;
        }
    }

    private HttpResponse useClientToSendRequest(HttpRequest httpRequest){
        try {
            return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());


        } catch (IOException | InterruptedException e) {
            return null;
        }
    }
}
