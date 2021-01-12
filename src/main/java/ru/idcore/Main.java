package ru.idcore;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class Main {
    public static final ObjectMapper MAPPER = new ObjectMapper();
    public static final String REMOTE_SERVICE_URI = "api.nasa.gov";

    private static Image loadImage(String fileName, String url) {
        try {
            BufferedImage img = ImageIO.read(new URL(url));
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
                System.out.println("Загружено изображение:" + fileName);
            }
            ImageIO.write(img, "jpeg", file);
            return img;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) throws IOException, URISyntaxException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setUserAgent("JavaCoreHTTP")
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(3000)
                        .setRedirectsEnabled(false).build())
                .build();

        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder
                .setScheme("https")
                .setHost(REMOTE_SERVICE_URI)
                .setPath("/planetary/apod")
                .addParameter("api_key", "d5JwXKEhfjW4ZwCCpGZWx18duIpzWGpiL7eBDbU6");

        URI uri = uriBuilder.build();
        HttpGet request = new HttpGet(uri);

        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        CloseableHttpResponse response = httpClient.execute(request);

        NasaAnswer nasaAnswers = MAPPER.readValue(response.getEntity().getContent(), new TypeReference<>() {
        });

        loadImage(Paths.get(new URI(nasaAnswers.getUrl()).
                        getPath()).
                        getFileName().
                        toString(),
                nasaAnswers.getUrl());
    }
}
