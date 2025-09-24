package com.example.demo.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;

import com.example.demo.model.Artist;
import com.example.demo.model.Artwork;
import com.example.demo.service.ArtworkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = com.example.demo.controllers.ArtworkRestController.class)
class ArtworkRestControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockitoBean
	private ArtworkService artworkService;

	@Test
	void testCreateArtwork() throws Exception {
		Artist artist = new Artist(1L, "Pablo Picasso", "Spanish");
		when(artworkService.insertNewArtwork(any(Artwork.class)))
				.thenReturn(new Artwork(3L, "New Piece", "Acrylic", 2025, artist));

		String newArtworkJson = """
					{
					  "title":"New Piece",
					  "medium":"Acrylic",
					  "yearCreated":2025,
					  "artist":{"id":1,"name":"Pablo Picasso","nationality":"Spanish"}
					}
				""";

		mvc.perform(post("/api/artworks/new").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.content(newArtworkJson)).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(3)))
				.andExpect(jsonPath("$.title", is("New Piece"))).andExpect(jsonPath("$.medium", is("Acrylic")))
				.andExpect(jsonPath("$.yearCreated", is(2025)))
				.andExpect(jsonPath("$.artist.name", is("Pablo Picasso")));
	}

	@Test
	void testUpdateArtworkExisting() throws Exception {
		Artist artist = new Artist(1L, "Pablo Picasso", "Spanish");
		when(artworkService.updateArtworkById(anyLong(), any(Artwork.class)))
				.thenReturn(new Artwork(1L, "Updated Piece", "Oil", 2024, artist));

		String updateJson = """
					{
					  "title":"Updated Piece",
					  "medium":"Oil",
					  "yearCreated":2024,
					  "artist":{"id":1,"name":"Pablo Picasso","nationality":"Spanish"}
					}
				""";

		mvc.perform(put("/api/artworks/1").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.content(updateJson)).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.title", is("Updated Piece"))).andExpect(jsonPath("$.medium", is("Oil")))
				.andExpect(jsonPath("$.yearCreated", is(2024)))
				.andExpect(jsonPath("$.artist.name", is("Pablo Picasso")));
	}

	@Test
	void testUpdateArtworkNotFound() throws Exception {
		when(artworkService.updateArtworkById(anyLong(), any(Artwork.class))).thenReturn(null);

		String updateJson = """
					{
					  "title":"Nonexistent",
					  "medium":"Ink",
					  "yearCreated":2023,
					  "artist":{"id":99,"name":"Unknown","nationality":"None"}
					}
				""";

		mvc.perform(put("/api/artworks/99").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.content(updateJson)).andExpect(status().isNotFound());
	}

	@Test
	void testDeleteArtwork() throws Exception {
		when(artworkService.deleteArtworkById(anyLong())).thenReturn(true);

		mvc.perform(delete("/api/artworks/1")).andExpect(status().isOk()).andExpect(content().string(""));
	}

	@Test
	void testGetAllArtworks() throws Exception {
		List<Artwork> artworks = List.of(new Artwork(1L, "Art1", "Oil", 2021, new Artist(1L, "Artist A", "Italy")),
				new Artwork(2L, "Art2", "Ink", 2022, new Artist(2L, "Artist B", "France")));
		when(artworkService.getAllArtworks()).thenReturn(artworks);

		mvc.perform(get("/api/artworks")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].title").value("Art1")).andExpect(jsonPath("$[1].title").value("Art2"));
	}

	@Test
	void testGetArtworkById() throws Exception {
		when(artworkService.getArtworkById(1L))
				.thenReturn(new Artwork(1L, "Art1", "Oil", 2021, new Artist(1L, "Artist A", "Italy")));

		mvc.perform(get("/api/artworks/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.title").value("Art1")).andExpect(jsonPath("$.artist.name").value("Artist A"));
	}

	@Test
	void testDeleteArtwork_NotFound() throws Exception {
		when(artworkService.deleteArtworkById(anyLong())).thenReturn(false);

		mvc.perform(delete("/api/artworks/99")).andExpect(status().isNotFound());
	}

	@Test
	void testGetArtworkById_NotFound() throws Exception {
		when(artworkService.getArtworkById(99L)).thenReturn(null);

		mvc.perform(get("/api/artworks/99")).andExpect(status().isNotFound());
	}
}