package com.example.demo.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.demo.model.Artwork;
import com.example.demo.model.Artist;

@DataJpaTest
class ArtworkJpaTest {

	@Autowired
	private TestEntityManager entityManager;

	@Test
	void testJpaMapping() {
		Artist artist = entityManager.persistFlushFind(new Artist(null, "Pablo Picasso", "Spanish"));
		Artwork artwork = new Artwork(null, "Guernica", "Oil on canvas", 1937, artist);
		Artwork saved = entityManager.persistFlushFind(artwork);

		assertThat(saved.getTitle()).isEqualTo("Guernica");
		assertThat(saved.getMedium()).isEqualTo("Oil on canvas");
		assertThat(saved.getYearCreated()).isEqualTo(1937);
		assertThat(saved.getArtist().getName()).isEqualTo("Pablo Picasso");
		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getId()).isPositive();

		LoggerFactory.getLogger(ArtworkJpaTest.class).info("Saved: {}", saved);
	}
}