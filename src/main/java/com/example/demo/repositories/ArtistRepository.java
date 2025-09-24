package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Artist;

public interface ArtistRepository extends JpaRepository<Artist, Long> {

	Artist findByName(String name);

	List<Artist> findByNameAndNationality(String name, String nationality);

	List<Artist> findByNameOrNationality(String name, String nationality);

	List<Artist> findByNationalityEndingWith(String suffix);
}