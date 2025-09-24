package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.example.demo.model.Artist;
import com.example.demo.model.Artwork;
import com.example.demo.repositories.ArtworkRepository;
import com.example.demo.repositories.ArtistRepository;
import com.example.demo.service.ArtworkService;

import org.junit.jupiter.api.BeforeEach;
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

@Testcontainers
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class ArtworkServiceRepositoryIT {

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
	private ArtworkService artworkService;

	@Autowired
	private ArtworkRepository artworkRepository;

	@Autowired
	private ArtistRepository artistRepository;

	private Artist defaultArtist;

	@BeforeEach
	void setup() {
		artworkRepository.deleteAll();
		artistRepository.deleteAll();
		artworkRepository.flush();
		artistRepository.flush();

		defaultArtist = artistRepository.save(new Artist(null, "Vincent Van Gogh", "Dutch"));
	}

	@Test
	void testInsertNewArtwork() {
		Artwork saved = artworkService
				.insertNewArtwork(new Artwork(null, "Starry Night", "Oil on Canvas", 1889, defaultArtist));
		assertThat(saved.getId()).isNotNull();
		assertThat(artworkRepository.findById(saved.getId())).isPresent();
	}

	@Test
	void testGetAllArtworks() {
		artworkRepository.deleteAll();

		artworkService.insertNewArtwork(new Artwork(null, "Mona Lisa", "Oil on Canvas", 1503, defaultArtist));
		artworkService
				.insertNewArtwork(new Artwork(null, "The Persistence of Memory", "Oil on Canvas", 1931, defaultArtist));

		List<Artwork> all = artworkService.getAllArtworks();
		assertThat(all).hasSize(2).extracting(Artwork::getTitle).containsExactlyInAnyOrder("Mona Lisa",
				"The Persistence of Memory");
	}

	@Test
	void testUpdateArtworkById() {
		Artwork original = artworkService
				.insertNewArtwork(new Artwork(null, "The Scream", "Oil on Canvas", 1893, defaultArtist));

		Artwork updatedArtwork = new Artwork(null, "The Scream", "Oil on Canvas", 1895, defaultArtist);
		updatedArtwork.setId(original.getId());

		Artwork updated = artworkService.updateArtworkById(original.getId(), updatedArtwork);

		assertThat(updated.getYearCreated()).isEqualTo(1895);

		Artwork fromDb = artworkRepository.findById(original.getId()).orElseThrow();
		assertThat(fromDb.getYearCreated()).isEqualTo(1895);
	}

	@Test
	void testDeleteArtworkById() {
		Artwork artwork = artworkService
				.insertNewArtwork(new Artwork(null, "Guernica", "Oil on Canvas", 1937, defaultArtist));

		artworkService.deleteArtworkById(artwork.getId());

		assertThat(artworkRepository.findById(artwork.getId())).isEmpty();
	}
}