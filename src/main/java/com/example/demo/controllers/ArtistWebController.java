package com.example.demo.controllers;

import java.util.List;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Artist;
import com.example.demo.model.Artwork;
import com.example.demo.service.ArtistService;
import com.example.demo.service.ArtworkService;

@Controller
@RequestMapping("/artists")
public class ArtistWebController {

	
	private final ArtistService artistService;
	private final ArtworkService artworkService;
	
	public ArtistWebController(ArtistService artistService, ArtworkService artworkService) {
		this.artistService = artistService;
		this.artworkService = artworkService;
	}

	private static final String MESSAGE_ATTRIBUTE = "message";
	private static final String ARTIST_ATTRIBUTE = "artist";
	private static final String ARTISTS_ATTRIBUTE = "artists";

	@GetMapping
	public String listArtists(Model model) {
		List<Artist> allArtists = artistService.getAllArtists();
		model.addAttribute(ARTISTS_ATTRIBUTE, allArtists);
		model.addAttribute(MESSAGE_ATTRIBUTE, allArtists.isEmpty() ? "No artist" : "");
		return ARTIST_ATTRIBUTE;
	}

	@GetMapping("/edit/{id}")
	public String editArtist(@PathVariable long id, Model model) {
		Artist artist = artistService.getArtistById(id);
		List<Artwork> artworks = artworkService.getAllArtworks();

		if (artist != null) {
			model.addAttribute(ARTIST_ATTRIBUTE, artist);
			model.addAttribute(MESSAGE_ATTRIBUTE, "");
		} else {
			model.addAttribute(ARTIST_ATTRIBUTE, new Artist());
			model.addAttribute(MESSAGE_ATTRIBUTE, "No artist found with id: " + id);
		}

		model.addAttribute("artworks", artworks);
		return "edit_artist";
	}

	@GetMapping("/new")
	public String newArtist(Model model) {
		model.addAttribute(ARTIST_ATTRIBUTE, new Artist());
		List<Artwork> artworks = artworkService.getAllArtworks();
		model.addAttribute("artworks", artworks);
		model.addAttribute(MESSAGE_ATTRIBUTE, "");
		return "edit_artist";
	}

	@PostMapping("/save")
	public String saveArtist(Artist artist,
			@RequestParam(value = "artworkIds", required = false) List<Long> artworkIds) {
		if (artworkIds != null && !artworkIds.isEmpty()) {
			List<Artwork> artworks = artworkService.getAllArtworksByIds(artworkIds);
			artist.setArtworks(artworks);
		}

		if (artist.getId() == null) {
			artistService.insertNewArtist(artist);
		} else {
			artistService.updateArtistById(artist.getId(), artist);
		}
		return "redirect:/artists";
	}

	@GetMapping("/delete/{id}")
	public String deleteArtist(@PathVariable long id, Model model) {
		artistService.deleteArtistById(id);
		model.addAttribute("deletedId", id);
		return "delete_artist";
	}
}