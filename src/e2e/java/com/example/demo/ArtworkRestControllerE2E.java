package com.example.demo;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

class ArtworkRestControllerE2E {

	private static final String BASE_URI = "http://localhost";
	private static final int PORT = 8080;
	private static final String ARTWORK_ENDPOINT = "/api/artworks";

	@BeforeAll
	static void setup() {
		RestAssured.baseURI = BASE_URI;
		RestAssured.port = PORT;
	}

	@Test
	void test_FullCrudArtwork() {

		int id = given().contentType(ContentType.JSON).body("""
				{
				  "title": "Starry Night",
				  "medium": "Oil on canvas",
				  "yearCreated": 1889
				}
				""").when().post(ARTWORK_ENDPOINT + "/new").then().statusCode(200).contentType(ContentType.JSON)
				.body("id", notNullValue()).body("title", equalTo("Starry Night")).extract().path("id");

		given().when().get(ARTWORK_ENDPOINT + "/" + id).then().statusCode(200).body("title", equalTo("Starry Night"));

		given().contentType(ContentType.JSON).body("""
				{
				  "title": "Starry Night Updated",
				  "medium": "Oil on canvas",
				  "yearCreated": 1889
				}
				""").when().put(ARTWORK_ENDPOINT + "/" + id).then().statusCode(200).body("title",
				equalTo("Starry Night Updated"));

		given().when().delete(ARTWORK_ENDPOINT + "/" + id).then().statusCode(200);

		given().when().get(ARTWORK_ENDPOINT + "/" + id).then().statusCode(404);
	}
}