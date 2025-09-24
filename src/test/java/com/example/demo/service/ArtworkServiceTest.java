package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.demo.model.Artwork;
import com.example.demo.model.Artist;
import com.example.demo.repositories.ArtworkRepository;
import com.example.demo.repositories.ArtistRepository;

class ArtworkServiceTest {

	@Mock
	private ArtworkRepository artworkRepository;
	@Mock
	private ArtistRepository artistRepository;
	private ArtworkService artworkService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		artworkService = new ArtworkService(artworkRepository, artistRepository);
	}

	@Test
	void testGetArtworkById() {
		Artist artist = new Artist(1L, "Da Vinci", "Italian");
		Artwork artwork = new Artwork(1L, "Mona Lisa", "Oil", 1503, artist);
		when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));

		Artwork result = artworkService.getArtworkById(1L);

		assertNotNull(result);
		assertEquals("Mona Lisa", result.getTitle());
		assertEquals("Oil", result.getMedium());
		assertEquals(1503, result.getYearCreated());
		verify(artworkRepository, times(1)).findById(1L);
	}

	@Test
	void testGetArtworkByIdNotFound() {
		when(artworkRepository.findById(1L)).thenReturn(Optional.empty());

		Artwork result = artworkService.getArtworkById(1L);

		assertNull(result);
		verify(artworkRepository, times(1)).findById(1L);
	}

	@Test
	void testInsertNewArtwork() {
		Artist artist = new Artist(2L, "Van Gogh", "Dutch");
		Artwork artwork = new Artwork(null, "Starry Night", "Oil on canvas", 1889, artist);
		Artwork savedArtwork = new Artwork(1L, "Starry Night", "Oil on canvas", 1889, artist);

		when(artworkRepository.save(artwork)).thenReturn(savedArtwork);

		Artwork result = artworkService.insertNewArtwork(artwork);

		assertNotNull(result);
		assertEquals("Starry Night", result.getTitle());
		assertEquals("Oil on canvas", result.getMedium());
		assertEquals(1889, result.getYearCreated());
		verify(artworkRepository, times(1)).save(artwork);
	}

	@Test
	void testUpdateArtwork() {
		Artist artist = new Artist(3L, "Rembrandt", "Dutch");
		Artwork existingArtwork = new Artwork(1L, "Starry Night", "Oil on canvas", 1889, artist);
		Artwork updatedArtwork = new Artwork(1L, "Starry Night Updated", "Oil on canvas", 1890, artist);

		when(artworkRepository.findById(1L)).thenReturn(Optional.of(existingArtwork));
		when(artworkRepository.save(existingArtwork)).thenReturn(existingArtwork);

		Artwork result = artworkService.updateArtworkById(1L, updatedArtwork);

		assertNotNull(result);
		assertEquals("Starry Night Updated", result.getTitle());
		assertEquals(1890, result.getYearCreated());
		verify(artworkRepository, times(1)).save(existingArtwork);
	}

	@Test
	void testDeleteArtwork() {
		long id = 1L;
		when(artworkRepository.existsById(id)).thenReturn(true); // ✅ simulate entity exists
		doNothing().when(artworkRepository).deleteById(id);

		boolean result = artworkService.deleteArtworkById(id);

		assertEquals(true, result); // ✅ check return
		verify(artworkRepository, times(1)).deleteById(id);
	}

	@Test
	void testDeleteArtwork_whenNotFound_returnsFalse() {
		long id = 99L;
		when(artworkRepository.existsById(id)).thenReturn(false); // ✅ nothing exists

		boolean result = artworkService.deleteArtworkById(id);

		assertEquals(false, result); // ✅ returns false
		verify(artworkRepository, never()).deleteById(anyLong());
	}

	@Test
	void testGetAllArtworks() {
		Artist artist = new Artist(4L, "Michelangelo", "Italian");
		Artwork artwork1 = new Artwork(1L, "Mona Lisa", "Oil", 1503, artist);
		Artwork artwork2 = new Artwork(2L, "Starry Night", "Oil on canvas", 1889, artist);

		when(artworkRepository.findAll()).thenReturn(List.of(artwork1, artwork2));

		List<Artwork> artworks = artworkService.getAllArtworks();

		assertNotNull(artworks);
		assertEquals(2, artworks.size());
		verify(artworkRepository, times(1)).findAll();
	}

	@Test
	void testUpdateArtwork_whenNotFound_returnsNull() {
		long nonExistentId = 999L;
		Artwork updatedArtwork = new Artwork(nonExistentId, "Imaginary", "Mixed", 2025, new Artist());

		when(artworkRepository.findById(nonExistentId)).thenReturn(Optional.empty());

		Artwork result = artworkService.updateArtworkById(nonExistentId, updatedArtwork);

		assertNull(result);
		verify(artworkRepository, never()).save(any());
	}

	@Test
	void testInsertNewArtwork_whenArtistIsNull() {
		Artwork artwork = new Artwork(null, "Untitled", "Digital", 2021, null);
		Artwork savedArtwork = new Artwork(1L, "Untitled", "Digital", 2021, null);

		when(artworkRepository.save(artwork)).thenReturn(savedArtwork);

		Artwork result = artworkService.insertNewArtwork(artwork);

		assertNotNull(result);
		assertEquals("Untitled", result.getTitle());
		assertNull(result.getArtist());
		verify(artistRepository, never()).findById(any());
		verify(artworkRepository).save(artwork);
	}

	@Test
	void testInsertNewArtwork_whenArtistIdIsNull() {
		Artist artist = new Artist(null, "Unknown", "Nowhere");
		Artwork artwork = new Artwork(null, "Lost Artwork", "Oil", 2020, artist);
		Artwork savedArtwork = new Artwork(1L, "Lost Artwork", "Oil", 2020, artist);

		when(artworkRepository.save(artwork)).thenReturn(savedArtwork);

		Artwork result = artworkService.insertNewArtwork(artwork);

		assertNotNull(result);
		assertEquals("Lost Artwork", result.getTitle());
		verify(artistRepository, never()).findById(any());
		verify(artworkRepository).save(artwork);
	}

	@Test
	void testInsertNewArtwork_whenArtistNotFound_setsNullArtist() {
		Artist artist = new Artist(99L, "Ghost", "Hidden");
		Artwork artwork = new Artwork(null, "Mystery", "Mixed", 1999, artist);
		Artwork savedArtwork = new Artwork(1L, "Mystery", "Mixed", 1999, artist);

		when(artistRepository.findById(99L)).thenReturn(Optional.empty());
		when(artworkRepository.save(any())).thenReturn(savedArtwork);

		Artwork result = artworkService.insertNewArtwork(artwork);

		assertNotNull(result);
		assertEquals("Mystery", result.getTitle());
		assertEquals("Ghost", result.getArtist().getName());
		verify(artistRepository).findById(99L);
		verify(artworkRepository).save(any());
	}

	@Test
	void testUpdateArtwork_whenArtistIsNull() {
		long id = 5L;
		Artist existingArtist = new Artist(5L, "Dali", "Spanish");
		Artwork existingArtwork = new Artwork(id, "Dream", "Surrealism", 1931, existingArtist);
		Artwork updatedArtwork = new Artwork(id, "Dream Updated", "Surrealism", 1932, null);

		when(artworkRepository.findById(id)).thenReturn(Optional.of(existingArtwork));
		when(artworkRepository.save(existingArtwork)).thenReturn(existingArtwork);
		;

		Artwork result = artworkService.updateArtworkById(id, updatedArtwork);

		assertNotNull(result);
		assertEquals("Dream Updated", result.getTitle());
		verify(artistRepository, never()).findById(any());
		verify(artworkRepository).save(existingArtwork);
	}

	@Test
	void testUpdateArtwork_whenArtistIdIsNull() {
		long id = 6L;
		Artist existingArtist = new Artist(6L, "Matisse", "French");
		Artwork existingArtwork = new Artwork(id, "Harmony", "Oil", 1905, existingArtist);
		Artist newArtist = new Artist(null, "Unknown", "None");
		Artwork updatedArtwork = new Artwork(id, "Harmony Updated", "Oil", 1906, newArtist);

		when(artworkRepository.findById(id)).thenReturn(Optional.of(existingArtwork));
		when(artworkRepository.save(existingArtwork)).thenReturn(existingArtwork);

		Artwork result = artworkService.updateArtworkById(id, updatedArtwork);

		assertNotNull(result);
		assertEquals("Harmony Updated", result.getTitle());
		verify(artistRepository, never()).findById(any());
		verify(artworkRepository).save(existingArtwork);
	}

	@Test
	void testUpdateArtwork_whenArtistNotFound() {
		long id = 7L;
		Artist existingArtist = new Artist(7L, "Rodin", "French");
		Artwork existingArtwork = new Artwork(id, "The Thinker", "Bronze", 1902, existingArtist);
		Artist newArtist = new Artist(999L, "Ghost", "Nowhere");
		Artwork updatedArtwork = new Artwork(id, "The Thinker Updated", "Bronze", 1903, newArtist);

		when(artworkRepository.findById(id)).thenReturn(Optional.of(existingArtwork));
		when(artistRepository.findById(999L)).thenReturn(Optional.empty());
		when(artworkRepository.save(existingArtwork)).thenReturn(existingArtwork);

		Artwork result = artworkService.updateArtworkById(id, updatedArtwork);

		assertNotNull(result);
		assertEquals("The Thinker Updated", result.getTitle());
		verify(artistRepository).findById(999L);
		verify(artworkRepository).save(existingArtwork);
	}

	@Test
	void testGetAllArtworksByIds() {
		Artist artist = new Artist(1L, "Test Artist", "Testland");
		Artwork artwork1 = new Artwork(1L, "Art1", "Oil", 2000, artist);
		Artwork artwork2 = new Artwork(2L, "Art2", "Ink", 2001, artist);

		List<Long> ids = List.of(1L, 2L);
		when(artworkRepository.findAllById(ids)).thenReturn(List.of(artwork1, artwork2));

		List<Artwork> result = artworkService.getAllArtworksByIds(ids);

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("Art1", result.get(0).getTitle());
		assertEquals("Art2", result.get(1).getTitle());
		verify(artworkRepository, times(1)).findAllById(ids);
	}
}
