package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Artwork;

public interface ArtworkRepository extends JpaRepository<Artwork, Long> {

	Artwork findByTitle(String title);

	List<Artwork> findByTitleAndMedium(String title, String medium);

	List<Artwork> findByTitleOrMedium(String title, String medium);

	List<Artwork> findAllByYearCreatedBefore(int year);
}