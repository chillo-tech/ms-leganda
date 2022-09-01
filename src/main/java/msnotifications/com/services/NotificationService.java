package msnotifications.com.services;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class NotificationService {
    public void sendMessage() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://d7sms.p.rapidapi.com/messages/v1/send"))
                .header("content-type", "application/json")
                .header("Token", "undefined")
                .header("X-RapidAPI-Key", "SIGN-UP-FOR-KEY")
                .header("X-RapidAPI-Host", "d7sms.p.rapidapi.com")
                .method("POST", HttpRequest.BodyPublishers.ofString(
                        "{\r\n    \"messages\": [\r\n        {\r\n            \"channel\": \"sms\",\r\n            \"originator\": \"SMS\",\r\n            \"recipients\": [\r\n                \"+971509752655\",\r\n                \"+9715097526xx\"\r\n            ],\r\n            \"content\": \"Greetings from D7 API \",\r\n            \"msg_type\": \"text\"\r\n        }\r\n    ]\r\n}"))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }

}
