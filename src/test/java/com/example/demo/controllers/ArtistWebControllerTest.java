package com.example.demo.controllers;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import com.example.demo.model.Artist;
import com.example.demo.model.Artwork;
import com.example.demo.service.ArtistService;
import com.example.demo.service.ArtworkService;

@WebMvcTest(controllers = ArtistWebController.class)
class ArtistWebControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockitoBean
	private ArtistService artistService;

	@MockitoBean
	private ArtworkService artworkService;

	@Test
	void testStatus200_ListView() throws Exception {
		mvc.perform(get("/artists")).andExpect(status().is2xxSuccessful());
	}

	@Test
	void testReturnArtistView() throws Exception {
		ModelAndViewAssert.assertViewName(mvc.perform(get("/artists")).andReturn().getModelAndView(), "artist");
	}

	@Test
	void test_ListView_ShowsArtists() throws Exception {
		List<Artist> artists = asList(new Artist(1L, "Alice", "Italian"));
		when(artistService.getAllArtists()).thenReturn(artists);

		mvc.perform(get("/artists")).andExpect(view().name("artist")).andExpect(model().attribute("artists", artists))
				.andExpect(model().attribute("message", ""));
	}

	@Test
	void test_ListView_ShowsMessageWhenNoArtists() throws Exception {
		when(artistService.getAllArtists()).thenReturn(emptyList());

		mvc.perform(get("/artists")).andExpect(view().name("artist"))
				.andExpect(model().attribute("artists", emptyList()))
				.andExpect(model().attribute("message", "No artist"));
	}

	@Test
	void test_EditArtist_WhenArtistIsFound() throws Exception {
		Artist artist = new Artist(1L, "Bob", "Spanish");
		when(artistService.getArtistById(1L)).thenReturn(artist);

		mvc.perform(get("/artists/edit/1")).andExpect(view().name("edit_artist"))
				.andExpect(model().attribute("artist", artist)).andExpect(model().attribute("message", ""));
	}

	@Test
	void test_EditArtist_WhenArtistIsNotFound() throws Exception {
		when(artistService.getArtistById(1L)).thenReturn(null);

		mvc.perform(get("/artists/edit/1")).andExpect(view().name("edit_artist"))
				.andExpect(model().attributeExists("artist"))
				.andExpect(model().attribute("message", "No artist found with id: 1"));
	}

	@Test
	void test_EditNewArtist() throws Exception {
		mvc.perform(get("/artists/new")).andExpect(view().name("edit_artist"))
				.andExpect(model().attribute("artist", new Artist())).andExpect(model().attribute("message", ""));
		verifyNoMoreInteractions(artistService);
	}

	@Test
	void test_PostArtistWithoutId_ShouldInsertNewArtist() throws Exception {
		mvc.perform(post("/artists/save").param("name", "Charlie").param("nationality", "German"))
				.andExpect(view().name("redirect:/artists"));

		verify(artistService).insertNewArtist(new Artist(null, "Charlie", "German"));
	}

	@Test
	void test_PostArtistWithId_ShouldUpdateExistingArtist() throws Exception {
		mvc.perform(post("/artists/save").param("id", "2").param("name", "Charlie").param("nationality", "German"))
				.andExpect(view().name("redirect:/artists"));

		verify(artistService).updateArtistById(2L, new Artist(2L, "Charlie", "German"));
	}

	@Test
	void test_DeleteArtist() throws Exception {
		mvc.perform(get("/artists/delete/3")).andExpect(status().isOk()).andExpect(view().name("delete_artist"))
				.andExpect(model().attribute("deletedId", 3L));

		verify(artistService).deleteArtistById(3L);
	}

	@Test
	void saveArtist_withArtwork_performsLookupAndSetsRealArtwork() throws Exception {

		Artist artist = new Artist(1L, "Dummy", "Test");
		List<Artwork> selectedArtworks = List.of(new Artwork(2L, "Test Artwork", "Florence", 2025, artist));

		when(artworkService.getAllArtworksByIds(List.of(2L))).thenReturn(selectedArtworks);

		mvc.perform(post("/artists/save").contentType(MediaType.APPLICATION_FORM_URLENCODED).param("name", "Alice")
				.param("nationality", "Italian").param("artworkIds", "2")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/artists"));

		ArgumentCaptor<Artist> capt = ArgumentCaptor.forClass(Artist.class);
		verify(artistService).insertNewArtist(capt.capture());
		Artist saved = capt.getValue();

		assertThat(saved.getArtworks()).containsExactlyElementsOf(selectedArtworks);
	}

	@Test
	void saveArtist_withNullArtworkIds_shouldInsert() throws Exception {
		when(artistService.insertNewArtist(any(Artist.class))).thenReturn(new Artist(null, "Alice", "Italian"));

		mvc.perform(post("/artists/save").param("name", "Alice").param("nationality", "Italian"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/artists"));

		verify(artistService).insertNewArtist(any(Artist.class));
	}

	@Test
	void saveArtist_withEmptyArtworkIds_shouldInsertWithoutArtworks() throws Exception {
		when(artistService.insertNewArtist(any(Artist.class))).thenReturn(new Artist(null, "Charlie", "German"));

		mvc.perform(
				post("/artists/save").param("name", "Charlie").param("nationality", "German").param("artworkIds", ""))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/artists"));

		verify(artistService).insertNewArtist(any(Artist.class));
	}

}