package com.example.demo.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.demo.model.Artist;

@DataJpaTest
class ArtistJpaTest {

	@Autowired
	private TestEntityManager entityManager;

	@Test
	void testJpaMapping() {
		Artist saved = entityManager.persistFlushFind(new Artist(null, "Pablo Picasso", "Spanish"));
		assertThat(saved.getName()).isEqualTo("Pablo Picasso");
		assertThat(saved.getNationality()).isEqualTo("Spanish");
		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getId()).isPositive();
		LoggerFactory.getLogger(ArtistJpaTest.class).info("Saved: {}", saved);
	}
}