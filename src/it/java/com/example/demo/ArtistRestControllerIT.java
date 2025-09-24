package com.example.demo;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.demo.model.Artist;
import com.example.demo.repositories.ArtistRepository;

import io.restassured.response.Response;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = Replace.NONE)
class ArtistRestControllerIT {

	@SuppressWarnings("resource")
	@Container
	static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0").withDatabaseName("testdb")
			.withUsername("testuser").withPassword("testpass");

	@DynamicPropertySource
	static void overrideProps(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mysql::getJdbcUrl);
		registry.add("spring.datasource.username", mysql::getUsername);
		registry.add("spring.datasource.password", mysql::getPassword);
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
	}

	@Autowired
	private ArtistRepository artistRepository;

	@LocalServerPort
	private int port;

	@BeforeEach
	void setup() {
		io.restassured.RestAssured.port = port;
		artistRepository.deleteAll();
		artistRepository.flush();
	}

	@Test
	void testNewArtist() {
		Artist artist = new Artist(null, "Van Gogh", "Dutch");

		Response response = given().contentType(MediaType.APPLICATION_JSON_VALUE).body(artist).when()
				.post("/api/artists/new");

		Artist saved = response.getBody().as(Artist.class);

		assertThat(saved.getId()).isNotNull();
		assertThat(artistRepository.findById(saved.getId())).contains(saved);
	}

	@Test
	void testGetArtistById() {
		Artist saved = artistRepository.save(new Artist(null, "Frida Kahlo", "Mexican"));

		Artist fetched = given().when().get("/api/artists/" + saved.getId()).then().statusCode(200).extract()
				.as(Artist.class);

		assertThat(fetched.getId()).isEqualTo(saved.getId());
		assertThat(fetched.getName()).isEqualTo("Frida Kahlo");
		assertThat(fetched.getNationality()).isEqualTo("Mexican");
	}

	@Test
	void testUpdateArtist() {
		Artist saved = artistRepository.save(new Artist(null, "Old Name", "Old Country"));
		Artist updated = new Artist(null, "New Name", "New Country");

		Artist result = given().contentType(MediaType.APPLICATION_JSON_VALUE).body(updated).when()
				.put("/api/artists/" + saved.getId()).then().statusCode(200).extract().as(Artist.class);

		assertThat(result.getName()).isEqualTo("New Name");
		assertThat(result.getNationality()).isEqualTo("New Country");
	}

	@Test
	void testDeleteArtist() {
		Artist saved = artistRepository.save(new Artist(null, "Delete Me", "To Be Deleted"));

		given().when().delete("/api/artists/" + saved.getId()).then().statusCode(200);

		assertThat(artistRepository.findById(saved.getId())).isEmpty();
	}
}