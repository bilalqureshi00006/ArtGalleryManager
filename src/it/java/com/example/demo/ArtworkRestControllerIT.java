package com.example.demo;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.model.Artist;
import com.example.demo.model.Artwork;
import com.example.demo.repositories.ArtworkRepository;
import com.example.demo.repositories.ArtistRepository;

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

import io.restassured.response.Response;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = Replace.NONE)
class ArtworkRestControllerIT {

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

	@LocalServerPort
	private int port;

	@Autowired
	private ArtworkRepository artworkRepository;

	@Autowired
	private ArtistRepository artistRepository;

	@BeforeEach
	void setup() {
		io.restassured.RestAssured.port = port;
		artworkRepository.deleteAll();
		artistRepository.deleteAll();
		artworkRepository.flush();
		artistRepository.flush();
	}

	@Test
	void testNewArtwork() {
		Artist artist = artistRepository.save(new Artist(null, "Van Gogh", "Dutch"));
		Artwork artwork = new Artwork(null, "Starry Night", "Oil", 1889, null);
		artwork.setArtist(artist);

		Response response = given().contentType(MediaType.APPLICATION_JSON_VALUE).body(artwork).when()
				.post("/api/artworks/new");

		Artwork saved = response.getBody().as(Artwork.class);

		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getYearCreated()).isEqualTo(1889);
		assertThat(artworkRepository.findById(saved.getId())).contains(saved);
	}

	@Test
	void testGetArtworkById() {
		Artist artist = artistRepository.save(new Artist(null, "Da Vinci", "Italian"));
		Artwork saved = artworkRepository.save(new Artwork(null, "Mona Lisa", "Oil", 1503, null));
		saved.setArtist(artist);
		artworkRepository.save(saved);

		Artwork fetched = given().when().get("/api/artworks/" + saved.getId()).then().statusCode(200).extract()
				.as(Artwork.class);

		assertThat(fetched.getId()).isEqualTo(saved.getId());
		assertThat(fetched.getTitle()).isEqualTo("Mona Lisa");
		assertThat(fetched.getYearCreated()).isEqualTo(1503);
	}

	@Test
	void testUpdateArtwork() {
		Artist artist = artistRepository.save(new Artist(null, "Rembrandt", "Dutch"));
		Artwork saved = artworkRepository.save(new Artwork(null, "Old Title", "Oil", 1650, null));

		Artwork updated = new Artwork(null, "New Title", "Ink", 1651, null);
		updated.setArtist(artist);
		Artwork result = given().contentType(MediaType.APPLICATION_JSON_VALUE).body(updated).when()
				.put("/api/artworks/" + saved.getId()).then().statusCode(200).extract().as(Artwork.class);

		assertThat(result.getTitle()).isEqualTo("New Title");
		assertThat(result.getMedium()).isEqualTo("Ink");
		assertThat(result.getYearCreated()).isEqualTo(1651);
	}

	@Test
	void testDeleteArtwork() {
		Artwork saved = artworkRepository.save(new Artwork(null, "To Delete", "Charcoal", 1800, null));

		given().when().delete("/api/artworks/" + saved.getId()).then().statusCode(200);

		assertThat(artworkRepository.findById(saved.getId())).isEmpty();
	}
}