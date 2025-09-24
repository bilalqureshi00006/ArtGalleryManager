package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.model.Artist;
import com.example.demo.repositories.ArtistRepository;

@Service
public class ArtistService {

	private final ArtistRepository artistRepository;

	public ArtistService(ArtistRepository artistRepository) {
		this.artistRepository = artistRepository;
	}

	public Artist getArtistById(long id) {
		Optional<Artist> artist = artistRepository.findById(id);
		return artist.orElse(null);
	}

	public Artist insertNewArtist(Artist artist) {
		artist.setId(null);
		return artistRepository.save(artist);
	}

	public Artist updateArtistById(long id, Artist updatedArtist) {
		updatedArtist.setId(id);
		return artistRepository.save(updatedArtist);
	}

	public void deleteArtistById(long id) {
		artistRepository.deleteById(id);
	}

	public List<Artist> getAllArtists() {
		return artistRepository.findAll();
	}
}