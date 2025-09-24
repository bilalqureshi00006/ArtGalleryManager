package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.model.Artwork;
import com.example.demo.repositories.ArtistRepository;
import com.example.demo.repositories.ArtworkRepository;

@Service
public class ArtworkService {

	private final ArtworkRepository artworkRepository;
	private final ArtistRepository artistRepository;

	public ArtworkService(ArtworkRepository artworkRepository, ArtistRepository artistRepository) {
		this.artworkRepository = artworkRepository;
		this.artistRepository = artistRepository;
	}

	public Artwork getArtworkById(long id) {
		return artworkRepository.findById(id).orElse(null);
	}

	public List<Artwork> getAllArtworksByIds(List<Long> ids) {
		return artworkRepository.findAllById(ids);
	}

	public Artwork insertNewArtwork(Artwork artwork) {
		if (artwork.getArtist() != null && artwork.getArtist().getId() != null) {
			artistRepository.findById(artwork.getArtist().getId()).ifPresent(artwork::setArtist);
		}
		return artworkRepository.save(artwork);
	}

	public Artwork updateArtworkById(long id, Artwork updatedArtwork) {
		Optional<Artwork> existingOpt = artworkRepository.findById(id);
		if (existingOpt.isPresent()) {
			Artwork existing = existingOpt.get();
			existing.setTitle(updatedArtwork.getTitle());
			existing.setMedium(updatedArtwork.getMedium());
			existing.setYearCreated(updatedArtwork.getYearCreated());

			if (updatedArtwork.getArtist() != null && updatedArtwork.getArtist().getId() != null) {
				artistRepository.findById(updatedArtwork.getArtist().getId()).ifPresent(existing::setArtist);
			}
			return artworkRepository.save(existing);
		}
		return null;
	}

	public boolean deleteArtworkById(long id) {
		if (artworkRepository.existsById(id)) {
			artworkRepository.deleteById(id);
			return true;
		}
		return false;
	}

	public List<Artwork> getAllArtworks() {
		return artworkRepository.findAll();
	}
}