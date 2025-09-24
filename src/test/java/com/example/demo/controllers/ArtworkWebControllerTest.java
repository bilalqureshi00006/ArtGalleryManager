package com.example.demo.controllers;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.model.Artist;
import com.example.demo.model.Artwork;
import com.example.demo.service.ArtistService;
import com.example.demo.service.ArtworkService;

@WebMvcTest(controllers = ArtworkWebController.class)
class ArtworkWebControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockitoBean
	private ArtworkService artworkService;

	@MockitoBean
	private ArtistService artistService;

	@Test
	void testStatus200_ListView() throws Exception {
		mvc.perform(get("/artworks")).andExpect(status().is2xxSuccessful());
	}

	@Test
	void testReturnArtworkView() throws Exception {
		ModelAndViewAssert.assertViewName(mvc.perform(get("/artworks")).andReturn().getModelAndView(), "artwork");
	}

	@Test
	void test_ListView_ShowsArtworks() throws Exception {
		List<Artwork> artworks = asList(new Artwork(1L, "A1", "Rome", 2025, null));
		when(artworkService.getAllArtworks()).thenReturn(artworks);

		mvc.perform(get("/artworks")).andExpect(view().name("artwork"))
				.andExpect(model().attribute("artworks", artworks)).andExpect(model().attribute("message", ""));
	}

	@Test
	void test_ListView_ShowsMessageWhenNoArtworks() throws Exception {
		when(artworkService.getAllArtworks()).thenReturn(emptyList());

		mvc.perform(get("/artworks")).andExpect(view().name("artwork"))
				.andExpect(model().attribute("artworks", emptyList()))
				.andExpect(model().attribute("message", "No artwork"));
	}

	@Test
	void test_EditArtwork_WhenFound() throws Exception {
		Artwork a = new Artwork(2L, "A2", "Venice", 2025, null);
		when(artworkService.getArtworkById(2L)).thenReturn(a);

		mvc.perform(get("/artworks/edit/2")).andExpect(view().name("edit_artwork"))
				.andExpect(model().attribute("artwork", a)).andExpect(model().attribute("message", ""));
	}

	@Test
	void test_EditArtwork_WhenNotFound() throws Exception {
		when(artworkService.getArtworkById(3L)).thenReturn(null);

		mvc.perform(get("/artworks/edit/3")).andExpect(view().name("edit_artwork"))
				.andExpect(model().attribute("artwork", nullValue()))
				.andExpect(model().attribute("message", "No artwork found with id: 3"));
	}

	@Test
	void test_EditNewArtwork() throws Exception {
		mvc.perform(get("/artworks/new")).andExpect(view().name("edit_artwork"))
				.andExpect(model().attribute("artwork", new Artwork())).andExpect(model().attribute("message", ""));
		verifyNoMoreInteractions(artworkService);
	}

	@Test
	void test_PostArtworkWithArtistId_ShouldLookupAndSetArtist() throws Exception {
		Artist artist = new Artist(10L, "Leonardo", "Italian");
		when(artistService.getArtistById(10L)).thenReturn(artist);


		mvc.perform(post("/artworks/save").param("title", "With Artist").param("medium", "Oil")
				.param("yearCreated", "1500").param("artist.id", "10"))
				.andExpect(view().name("redirect:/artworks"));

		verify(artistService).getArtistById(10L);
		verify(artworkService).insertNewArtwork(new Artwork(null, "With Artist", "Oil", 1500, artist));
	
	}

	@Test
	void test_PostArtworkWithId_ShouldUpdateExistingArtwork() throws Exception {
		mvc.perform(post("/artworks/save").param("id", "4").param("title", "A4").param("medium", "Acrylic")
				.param("yearCreated", "2025")).andExpect(view().name("redirect:/artworks"));

		verify(artworkService).updateArtworkById(4L, new Artwork(4L, "A4", "Acrylic", 2025, null));
	}

	@Test
	void test_DeleteArtwork() throws Exception {
		mvc.perform(get("/artworks/delete/5")).andExpect(status().isOk()).andExpect(view().name("delete_artwork"))
				.andExpect(model().attribute("deletedId", 5L));

		verify(artworkService).deleteArtworkById(5L);
	}
}