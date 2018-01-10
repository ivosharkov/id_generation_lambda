package com.scalefocus.shutterfly.aws.idgeneration.services.util;

import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import net.minidev.json.JSONArray;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.junit.BeforeClass;

import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Optional;

import javax.net.ssl.SSLContext;

public abstract class BaseTest {

    @BeforeClass
    public static void setUpBaseApiGateway() throws NoSuchAlgorithmException {

        Optional<String> baseUri = Optional.ofNullable(System.getProperty("baseUri"));
        Optional<String> port = Optional.ofNullable(System.getProperty("port"));
        Optional<String> basePath = Optional.ofNullable(System.getProperty("basePath"));

        // Set the host, port, and base path
        RestAssured.baseURI = baseUri.isPresent()
                ? baseUri.get()
                : "https://aavztyxxof.execute-api.eu-west-1.amazonaws.com";
        RestAssured.port = port.isPresent()
                ? Integer.parseInt(port.get())
                : 443;
        RestAssured.basePath = basePath.isPresent()
                ? basePath.get()
                : "beta";

        // Use our custom socket factory
        SSLSocketFactory customSslFactory = new GatewaySslSocketFactory(
                SSLContext.getDefault(), SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        RestAssured.config = RestAssured.config().sslConfig(
                SSLConfig.sslConfig().sslSocketFactory(customSslFactory));
        RestAssured.config.getHttpClientConfig().reuseHttpClientInstance();
    }

    public static HashSet<String> responseBodyToHashSet(String responseBody, int count) throws ParseException {
        HashSet<String> output = new HashSet<String>();
        JSONArray array = (JSONArray) new JSONParser(JSONParser.MODE_PERMISSIVE).parse(responseBody);
        for (int j = 0; j < count; j++) {
            output.add(array.get(j).toString());
        }
        return output;
    }

}
