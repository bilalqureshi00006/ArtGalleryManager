package com.example.demo.controllers;

import java.util.List;


import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Artist;
import com.example.demo.service.ArtistService;

@RestController
@RequestMapping("/api/artists")
public class ArtistRestController {

	private final ArtistService artistService;

	public ArtistRestController(ArtistService artistService) {
		this.artistService = artistService;
	}

	@GetMapping
	public List<Artist> allArtists() {
		return artistService.getAllArtists();
	}

	@GetMapping("/{id}")
	public Artist artist(@PathVariable long id) {
		return artistService.getArtistById(id);
	}

	@PostMapping("/new")
	public Artist newArtist(@RequestBody Artist artist) {
		return artistService.insertNewArtist(artist);
	}

	@PutMapping("/{id}")
	public Artist updateArtist(@PathVariable long id, @RequestBody Artist replacement) {
		return artistService.updateArtistById(id, replacement);
	}

	@DeleteMapping("/{id}")
	public void deleteArtist(@PathVariable long id) {
		artistService.deleteArtistById(id);
	}
}