package com.sky.fodmap.service.utilities;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class Client {

    public HttpResponse<String> sendHttpRequest(String urlString) throws IOException, InterruptedException {

        URL url = new URL(urlString);
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(url.toString())).timeout(Duration.ofSeconds(1)).build();
        HttpClient httpClient = HttpClient.newHttpClient();
        return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }
}
