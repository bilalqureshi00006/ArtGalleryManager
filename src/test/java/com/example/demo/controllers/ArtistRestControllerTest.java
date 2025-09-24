package com.example.demo.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.example.demo.model.Artist;
import com.example.demo.service.ArtistService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ArtistRestController.class)
@Import(ArtistRestController.class)
class ArtistRestControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private ArtistService artistService;

	@Test
	void testCreateArtist() throws Exception {
		Artist artist = new Artist(3L, "Bob Brown", "American");
		when(artistService.insertNewArtist(any(Artist.class))).thenReturn(artist);

		String newArtistJson = objectMapper.writeValueAsString(new Artist(null, "Bob Brown", "American"));

		mvc.perform(post("/api/artists/new").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.content(newArtistJson)).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(3)))
				.andExpect(jsonPath("$.name", is("Bob Brown"))).andExpect(jsonPath("$.nationality", is("American")));
	}

	@Test
	void testUpdateArtistExisting() throws Exception {
		Artist updatedArtist = new Artist(1L, "John Doe Jr.", "Canadian");
		when(artistService.updateArtistById(anyLong(), any(Artist.class))).thenReturn(updatedArtist);

		String updateJson = objectMapper.writeValueAsString(new Artist(null, "John Doe Jr.", "Canadian"));

		mvc.perform(put("/api/artists/1").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.content(updateJson)).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.name", is("John Doe Jr."))).andExpect(jsonPath("$.nationality", is("Canadian")));
	}

	@Test
	void testUpdateArtistNotFound() throws Exception {
		when(artistService.updateArtistById(anyLong(), any(Artist.class))).thenReturn(null);

		String updateJson = objectMapper.writeValueAsString(new Artist(null, "Nobody", "None"));

		mvc.perform(put("/api/artists/99").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.content(updateJson)).andExpect(status().isOk()).andExpect(content().string(""));
	}

	@Test
	void testDeleteArtist() throws Exception {
		doNothing().when(artistService).deleteArtistById(anyLong());

		mvc.perform(delete("/api/artists/1")).andExpect(status().isOk()).andExpect(content().string(""));
	}

	@Test
	void testGetAllArtists() throws Exception {
		List<Artist> artists = List.of(new Artist(1L, "Alice", "Italian"), new Artist(2L, "Bob", "French"));

		when(artistService.getAllArtists()).thenReturn(artists);

		mvc.perform(get("/api/artists")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].name").value("Alice"))
				.andExpect(jsonPath("$[1].nationality").value("French"));
	}

	@Test
	void testGetArtistById() throws Exception {
		when(artistService.getArtistById(1L)).thenReturn(new Artist(1L, "Alice", "Italian"));

		mvc.perform(get("/api/artists/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Alice")).andExpect(jsonPath("$.nationality").value("Italian"));
	}
}