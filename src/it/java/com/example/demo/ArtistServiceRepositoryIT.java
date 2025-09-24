package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.demo.model.Artist;
import com.example.demo.repositories.ArtistRepository;
import com.example.demo.service.ArtistService;

@Testcontainers
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class ArtistServiceRepositoryIT {

	@SuppressWarnings("resource")
	@Container
	static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>("mysql:8.0").withDatabaseName("testdb")
			.withUsername("testuser").withPassword("testpass");

	@DynamicPropertySource
	static void overrideProps(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", MYSQL_CONTAINER::getJdbcUrl);
		registry.add("spring.datasource.username", MYSQL_CONTAINER::getUsername);
		registry.add("spring.datasource.password", MYSQL_CONTAINER::getPassword);
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
	}

	@Autowired
	private ArtistService artistService;

	@Autowired
	private ArtistRepository artistRepository;

	@Test
	void testInsertNewArtist() {
		Artist saved = artistService.insertNewArtist(new Artist(null, "Picasso", "Spanish"));
		assertThat(saved.getId()).isNotNull();
		assertThat(artistRepository.findById(saved.getId())).isPresent();
	}

	@Test
	void testGetAllArtists() {
		artistRepository.deleteAll();

		artistService.insertNewArtist(new Artist(null, "Da Vinci", "Italian"));
		artistService.insertNewArtist(new Artist(null, "Van Gogh", "Dutch"));

		List<Artist> all = artistService.getAllArtists();
		assertThat(all).hasSize(2).extracting(Artist::getName).containsExactlyInAnyOrder("Da Vinci", "Van Gogh");
	}

	@Test
	void testUpdateArtistById() {
		Artist original = artistService.insertNewArtist(new Artist(null, "Rembrandt", "Dutch"));

		Artist updated = artistService.updateArtistById(original.getId(), new Artist(null, "Rembrandt", "Netherlands"));

		assertThat(updated.getNationality()).isEqualTo("Netherlands");
		Artist fromDb = artistRepository.findById(original.getId()).orElseThrow();
		assertThat(fromDb.getNationality()).isEqualTo("Netherlands");
	}

	@Test
	void testDeleteArtistById() {
		Artist artist = artistService.insertNewArtist(new Artist(null, "Matisse", "French"));

		artistService.deleteArtistById(artist.getId());

		assertThat(artistRepository.findById(artist.getId())).isEmpty();
	}
}