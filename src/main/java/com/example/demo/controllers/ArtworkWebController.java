package com.example.demo.controllers;

import java.util.List;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Artwork;
import com.example.demo.model.Artist;
import com.example.demo.service.ArtworkService;
import com.example.demo.service.ArtistService;

@Controller
@RequestMapping("/artworks")
public class ArtworkWebController {

	private static final String MESSAGE_ATTRIBUTE = "message";
	private static final String ARTWORK_ATTRIBUTE = "artwork";
	private static final String ARTWORKS_ATTRIBUTE = "artworks";
	private static final String ARTISTS_ATTRIBUTE = "allArtists";


	private ArtworkService artworkService;
	private ArtistService artistService;
	
	public ArtworkWebController(ArtworkService artworkService, ArtistService artistService) {
		this.artworkService = artworkService;
		this.artistService = artistService;
	}

	@GetMapping
	public String listArtworks(Model model) {
		List<Artwork> allArtworks = artworkService.getAllArtworks();
		model.addAttribute(ARTWORKS_ATTRIBUTE, allArtworks);
		model.addAttribute(MESSAGE_ATTRIBUTE, allArtworks.isEmpty() ? "No artwork" : "");
		return ARTWORK_ATTRIBUTE;
	}

	@GetMapping("/edit/{id}")
	public String editArtwork(@PathVariable long id, Model model) {
		Artwork artwork = artworkService.getArtworkById(id);
		List<Artist> artists = artistService.getAllArtists();
		model.addAttribute(ARTISTS_ATTRIBUTE, artists);
		if (artwork == null) {
			model.addAttribute(ARTWORK_ATTRIBUTE, null);
			model.addAttribute(MESSAGE_ATTRIBUTE, "No artwork found with id: " + id);
		} else {
			model.addAttribute(ARTWORK_ATTRIBUTE, artwork);
			model.addAttribute(MESSAGE_ATTRIBUTE, "");
		}
		return "edit_artwork";
	}

	@GetMapping("/new")
	public String newArtwork(Model model) {
		model.addAttribute(ARTWORK_ATTRIBUTE, new Artwork());
		model.addAttribute(ARTISTS_ATTRIBUTE, artistService.getAllArtists());
		model.addAttribute(MESSAGE_ATTRIBUTE, "");
		return "edit_artwork";
	}

	@PostMapping("/save")
	public String saveArtwork(@ModelAttribute(ARTWORK_ATTRIBUTE) Artwork artwork) {
		if (artwork.getArtist() != null && artwork.getArtist().getId() != null) {
			Artist realArtist = artistService.getArtistById(artwork.getArtist().getId());
			artwork.setArtist(realArtist);
		}
		if (artwork.getId() == null) {
			artworkService.insertNewArtwork(artwork);
		} else {
			artworkService.updateArtworkById(artwork.getId(), artwork);
		}
		return "redirect:/artworks";
	}

	@GetMapping("/delete/{id}")
	public String deleteArtwork(@PathVariable long id, Model model) {
		artworkService.deleteArtworkById(id);
		model.addAttribute("deletedId", id);
		return "delete_artwork";
	}
}