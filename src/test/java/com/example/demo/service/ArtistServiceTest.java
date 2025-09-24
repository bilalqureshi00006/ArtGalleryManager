package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.demo.model.Artist;
import com.example.demo.repositories.ArtistRepository;

class ArtistServiceTest {

	@Mock
	private ArtistRepository artistRepository;

	private ArtistService artistService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		artistService = new ArtistService(artistRepository);
	}

	@Test
	void testGetArtistById() {
		Artist artist = new Artist(1L, "Leonardo da Vinci", "Italian");
		when(artistRepository.findById(1L)).thenReturn(Optional.of(artist));

		Artist result = artistService.getArtistById(1L);

		assertNotNull(result);
		assertEquals("Leonardo da Vinci", result.getName());
		assertEquals("Italian", result.getNationality());
		verify(artistRepository, times(1)).findById(1L);
	}

	@Test
	void testGetArtistByIdNotFound() {
		when(artistRepository.findById(1L)).thenReturn(Optional.empty());

		Artist result = artistService.getArtistById(1L);

		assertNull(result);
		verify(artistRepository, times(1)).findById(1L);
	}

	@Test
	void testInsertNewArtist() {
		Artist artist = new Artist(null, "Van Gogh", "Dutch");
		Artist savedArtist = new Artist(1L, "Van Gogh", "Dutch");

		when(artistRepository.save(artist)).thenReturn(savedArtist);

		Artist result = artistService.insertNewArtist(artist);

		assertNotNull(result);
		assertEquals("Van Gogh", result.getName());
		assertEquals("Dutch", result.getNationality());
		verify(artistRepository, times(1)).save(artist);
	}

	@Test
	void testUpdateArtist() {
		Artist existing = new Artist(1L, "Van Gogh", "Dutch");
		Artist updated = new Artist(1L, "Van Gogh Updated", "Netherlands");

		when(artistRepository.findById(1L)).thenReturn(Optional.of(existing));
		when(artistRepository.save(updated)).thenReturn(updated);

		Artist result = artistService.updateArtistById(1L, updated);

		assertNotNull(result);
		assertEquals("Van Gogh Updated", result.getName());
		assertEquals("Netherlands", result.getNationality());
		verify(artistRepository, times(1)).save(updated);
	}

	@Test
	void testDeleteArtist() {
		long id = 1L;
		doNothing().when(artistRepository).deleteById(id);

		artistService.deleteArtistById(id);

		verify(artistRepository, times(1)).deleteById(id);
	}

	@Test
	void testGetAllArtists() {
		Artist a1 = new Artist(1L, "Leonardo", "Italian");
		Artist a2 = new Artist(2L, "Rembrandt", "Dutch");

		when(artistRepository.findAll()).thenReturn(List.of(a1, a2));

		List<Artist> result = artistService.getAllArtists();

		assertNotNull(result);
		assertEquals(2, result.size());
		verify(artistRepository, times(1)).findAll();
	}

	@Test
	void insertNewArtist_mustNullOutId_beforeCallingSave() {
		Artist input = new Artist(88L, "Michelangelo", "Italian");
		Artist saved = new Artist(1L, "Michelangelo", "Italian");

		when(artistRepository.save(any())).thenReturn(saved);

		Artist result = artistService.insertNewArtist(input);

		ArgumentCaptor<Artist> captor = ArgumentCaptor.forClass(Artist.class);
		verify(artistRepository).save(captor.capture());

		assertThat(captor.getValue().getId()).isNull();
		assertThat(result).isSameAs(saved);
	}

	@Test
	void updateArtistById_mustSetCorrectId_beforeCallingSave() {
		Artist input = new Artist(null, "Claude Monet", "French");
		Artist saved = new Artist(5L, "Claude Monet", "French");

		when(artistRepository.save(any())).thenReturn(saved);

		Artist result = artistService.updateArtistById(5L, input);

		ArgumentCaptor<Artist> captor = ArgumentCaptor.forClass(Artist.class);
		verify(artistRepository).save(captor.capture());

		assertThat(captor.getValue().getId()).isEqualTo(5L);
		assertThat(result).isSameAs(saved);
	}
}