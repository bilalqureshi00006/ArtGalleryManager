package com.example.demo.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.example.demo.model.Artwork;
import com.example.demo.model.Artist;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class ArtworkRepositoryTest {

	@Autowired
	private ArtworkRepository repository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	void firstLearningTest() {
		Artist artist = entityManager.persistFlushFind(new Artist(null, "Leonardo", "Italian"));
		Artwork artwork = new Artwork(null, "Mona Lisa", "Oil", 1503, artist);
		Artwork saved = repository.save(artwork);
		Collection<Artwork> artworks = repository.findAll();
		assertThat(artworks).containsExactly(saved);
	}

	@Test
	void secondLearningTest() {
		Artist artist = entityManager.persistFlushFind(new Artist(null, "Leonardo", "Italian"));
		Artwork artwork = new Artwork(null, "Mona Lisa", "Oil", 1503, artist);
		Artwork saved = entityManager.persistFlushFind(artwork);
		Collection<Artwork> artworks = repository.findAll();
		assertThat(artworks).containsExactly(saved);
	}

	@Test
	void test_findByTitle() {
		Artist artist = entityManager.persistFlushFind(new Artist(null, "Picasso", "Spanish"));
		Artwork saved = entityManager.persistFlushFind(new Artwork(null, "Guernica", "Oil", 1937, artist));
		Artwork found = repository.findByTitle("Guernica");
		assertThat(found).isEqualTo(saved);
	}

	@Test
	void test_findByTitleAndMedium() {
		Artist artist = entityManager.persistFlushFind(new Artist(null, "Van Gogh", "Dutch"));
		entityManager.persistFlushFind(new Artwork(null, "Starry Night", "Ink", 1889, artist));
		Artwork match = entityManager
				.persistFlushFind(new Artwork(null, "Starry Night", "Oil on canvas", 1889, artist));
		List<Artwork> found = repository.findByTitleAndMedium("Starry Night", "Oil on canvas");
		assertThat(found).containsExactly(match);
	}

	@Test
	void test_findByTitleOrMedium() {
		Artist artist = entityManager.persistFlushFind(new Artist(null, "Munch", "Norwegian"));
		Artwork a1 = entityManager.persistFlushFind(new Artwork(null, "The Scream", "Tempera", 1893, artist));
		Artwork a2 = entityManager.persistFlushFind(new Artwork(null, "Another", "Charcoal", 1900, artist));
		entityManager.persistFlushFind(new Artwork(null, "Ignore", "Pastel", 1910, artist));
		List<Artwork> found = repository.findByTitleOrMedium("The Scream", "Charcoal");
		assertThat(found).containsExactly(a1, a2);
	}

	@Test
	void test_findByYearCreatedBefore() {
		Artist artist = entityManager.persistFlushFind(new Artist(null, "Old Master", "Unknown"));
		Artwork a1 = entityManager.persistFlushFind(new Artwork(null, "Old1", "Oil", 1400, artist));
		Artwork a2 = entityManager.persistFlushFind(new Artwork(null, "Old2", "Fresco", 1450, artist));
		entityManager.persistFlushFind(new Artwork(null, "Modern", "Acrylic", 2000, artist));
		List<Artwork> found = repository.findAllByYearCreatedBefore(1500);
		assertThat(found).containsExactly(a1, a2);
	}

	@Test
	void testCreateArtwork() {
		Artist artist = entityManager.persistFlushFind(new Artist(null, "Klimt", "Austrian"));
		Artwork artwork = new Artwork(null, "The Kiss", "Oil and gold leaf", 1907, artist);
		Artwork saved = repository.save(artwork);
		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getTitle()).isEqualTo("The Kiss");
		assertThat(saved.getMedium()).isEqualTo("Oil and gold leaf");
	}

	@Test
	void testReadArtworkById() {
		Artist artist = entityManager.persistFlushFind(new Artist(null, "Vermeer", "Dutch"));
		Artwork artwork = entityManager
				.persistFlushFind(new Artwork(null, "Girl with a Pearl Earring", "Oil", 1665, artist));
		Optional<Artwork> foundOpt = repository.findById(artwork.getId());
		assertThat(foundOpt).isPresent();

		Artwork found = foundOpt.get();
		assertThat(found.getTitle()).isEqualTo("Girl with a Pearl Earring");
		assertThat(found.getMedium()).isEqualTo("Oil");
	}

	@Test
	void testUpdateArtwork() {
		Artist artist = entityManager.persistFlushFind(new Artist(null, "Rembrandt", "Dutch"));
		Artwork artwork = entityManager.persistFlushFind(new Artwork(null, "Draft", "Sketch", 1900, artist));
		artwork.setTitle("Final Version");
		artwork.setMedium("Oil");
		Artwork updated = repository.save(artwork);
		entityManager.flush();
		entityManager.clear();

		Artwork reloaded = repository.findById(updated.getId()).orElseThrow();
		assertThat(reloaded.getTitle()).isEqualTo("Final Version");
		assertThat(reloaded.getMedium()).isEqualTo("Oil");
	}

	@Test
	void testDeleteArtwork() {
		Artist artist = entityManager.persistFlushFind(new Artist(null, "Unknown", "Unknown"));
		Artwork artwork = entityManager.persistFlushFind(new Artwork(null, "To be deleted", "Pastel", 1990, artist));
		repository.deleteById(artwork.getId());
		entityManager.flush();

		boolean exists = repository.existsById(artwork.getId());
		assertThat(exists).isFalse();
	}
}