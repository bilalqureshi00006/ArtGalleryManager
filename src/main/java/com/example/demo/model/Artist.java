package com.example.demo.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
public class Artist {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String nationality;

	@OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<Artwork> artworks = new ArrayList<>();

	public Artist() {
	}

	public Artist(Long id, String name, String nationality) {
		this.id = id;
		this.name = name;
		this.nationality = nationality;
	}

	// Getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public List<Artwork> getArtworks() {
		return artworks;
	}

	public void setArtworks(List<Artwork> artworks) {
		this.artworks = artworks;
	}

	@Override
	public String toString() {
		return "Artist [id=" + id + ", name=" + name + ", nationality=" + nationality + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, nationality);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Artist other = (Artist) obj;
		return Objects.equals(id, other.id) && Objects.equals(name, other.name)
				&& Objects.equals(nationality, other.nationality);
	}
}