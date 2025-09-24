package com.example.demo.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.example.demo.model.Artist;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class ArtistRepositoryTest {

	@Autowired
	private ArtistRepository repository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	void firstLearningTest() {
		Artist artist = new Artist(null, "test", "Spanish");
		Artist saved = repository.save(artist);
		Collection<Artist> artists = repository.findAll();
		assertThat(artists).containsExactly(saved);
	}

	@Test
	void secondLearningTest() {
		Artist artist = new Artist(null, "test", "Spanish");
		Artist saved = entityManager.persistFlushFind(artist);
		Collection<Artist> artists = repository.findAll();
		assertThat(artists).containsExactly(saved);
	}

	@Test
	void test_findByName() {
		Artist saved = entityManager.persistFlushFind(new Artist(null, "test", "Spanish"));
		Artist found = repository.findByName("test");
		assertThat(found).isEqualTo(saved);
	}

	@Test
	void test_findByNameAndNationality() {
		entityManager.persistFlushFind(new Artist(null, "test", "Italian"));
		Artist a = entityManager.persistFlushFind(new Artist(null, "test", "Spanish"));
		List<Artist> found = repository.findByNameAndNationality("test", "Spanish");
		assertThat(found).containsExactly(a);
	}

	@Test
	void test_findByNameOrNationality() {
		Artist a1 = entityManager.persistFlushFind(new Artist(null, "test", "French"));
		Artist a2 = entityManager.persistFlushFind(new Artist(null, "another", "Spanish"));
		entityManager.persistFlushFind(new Artist(null, "noMatch", "German"));
		List<Artist> found = repository.findByNameOrNationality("test", "Spanish");
		assertThat(found).containsExactly(a1, a2);
	}

	@Test
	void test_findByNationalityEndingWith() {
		entityManager.persistFlushFind(new Artist(null, "test", "French"));
		Artist a2 = entityManager.persistFlushFind(new Artist(null, "another", "Spanish"));
		entityManager.persistFlushFind(new Artist(null, "no", "German"));
		List<Artist> found = repository.findByNationalityEndingWith("sh");
		assertThat(found).containsExactly(a2);
	}

	@Test
	void testCreateArtist() {
		Artist artist = new Artist(null, "Leonardo", "Italian");
		Artist saved = repository.save(artist);

		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getName()).isEqualTo("Leonardo");
		assertThat(saved.getNationality()).isEqualTo("Italian");
	}

	@Test
	void testReadArtistById() {
		Artist artist = entityManager.persistFlushFind(new Artist(null, "Michelangelo", "Italian"));

		Optional<Artist> foundOpt = repository.findById(artist.getId());
		assertThat(foundOpt).isPresent();

		Artist found = foundOpt.get();
		assertThat(found.getName()).isEqualTo("Michelangelo");
		assertThat(found.getNationality()).isEqualTo("Italian");
	}

	@Test
	void testUpdateArtist() {
		Artist artist = entityManager.persistFlushFind(new Artist(null, "Rafael", "Spanish"));

		artist.setName("Raphael");
		artist.setNationality("Italian");
		Artist updated = repository.save(artist);
		entityManager.flush();
		entityManager.clear();

		Artist reloaded = repository.findById(updated.getId()).orElseThrow();
		assertThat(reloaded.getName()).isEqualTo("Raphael");
		assertThat(reloaded.getNationality()).isEqualTo("Italian");
	}

	@Test
	void testDeleteArtist() {
		Artist artist = entityManager.persistFlushFind(new Artist(null, "Donatello", "Italian"));

		repository.deleteById(artist.getId());
		entityManager.flush();

		boolean exists = repository.existsById(artist.getId());
		assertThat(exists).isFalse();
	}
}