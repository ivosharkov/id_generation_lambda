package com.scalefocus.shutterfly.aws.idgeneration.services;

import static io.restassured.RestAssured.get;
import static org.assertj.core.api.Assertions.assertThat;

import com.scalefocus.shutterfly.aws.idgeneration.services.util.BaseTest;
import net.minidev.json.parser.ParseException;
import org.junit.Test;

import java.util.HashSet;

public class IdGenerationTest extends BaseTest {

    @Test
    public void generateSingleIds() {
        HashSet<String> generatedIds = new HashSet<String>();
        final int count = 50;
        for (int j = 0; j < count; j++) {
            String newId = get("/id")
                    .then()
                    .log().all()
                    .statusCode(200)
                    .extract().asString();
            generatedIds.add(newId);
        }
        assertThat(generatedIds.size()).isEqualTo(count);
    }

    @Test
    public void generateBulkIds() throws ParseException {
        final int count = 50;
        String responseBody = get("/id/bulk?count={count}", count)
                .then()
                .log().all()
                .statusCode(200)
                .extract().asString();
        assertThat(responseBodyToHashSet(responseBody, count).size()).isEqualTo(count);
    }

}
