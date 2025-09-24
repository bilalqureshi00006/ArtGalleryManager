package com.example.demo.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Artwork;
import com.example.demo.service.ArtworkService;

@RestController
@RequestMapping("/api/artworks")
public class ArtworkRestController {

	private final ArtworkService artworkService;

	public ArtworkRestController(ArtworkService artworkService) {
		this.artworkService = artworkService;
	}

	@GetMapping
	public List<Artwork> allArtworks() {
		return artworkService.getAllArtworks();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Artwork> getArtwork(@PathVariable long id) {
		Artwork artwork = artworkService.getArtworkById(id);
		return (artwork == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(artwork);
	}

	@PostMapping("/new")
	public ResponseEntity<Artwork> create(@RequestBody Artwork artwork) {
		Artwork created = artworkService.insertNewArtwork(artwork);
		return ResponseEntity.ok(created);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Artwork> update(@PathVariable long id, @RequestBody Artwork replacement) {
		Artwork updated = artworkService.updateArtworkById(id, replacement);
		return (updated == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(updated);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable long id) {
		boolean deleted = artworkService.deleteArtworkById(id);
		return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
	}
}