package com.example.demo.model;

import jakarta.persistence.*;
import java.util.Objects;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class Artwork {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;
	private String medium;
	private int yearCreated;

	@ManyToOne
	@JoinColumn(name = "artist_id")
	@JsonIgnoreProperties("artworks")
	private Artist artist;

	public Artwork() {
	}

	public Artwork(Long id, String title, String medium, int yearCreated, Artist artist) {
		this.id = id;
		this.title = title;
		this.medium = medium;
		this.yearCreated = yearCreated;
		this.artist = artist;
	}

	// Getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMedium() {
		return medium;
	}

	public void setMedium(String medium) {
		this.medium = medium;
	}

	public int getYearCreated() {
		return yearCreated;
	}

	public void setYearCreated(int yearCreated) {
		this.yearCreated = yearCreated;
	}

	public Artist getArtist() {
		return artist;
	}

	public void setArtist(Artist artist) {
		this.artist = artist;
	}

	@Override
	public String toString() {
		return "Artwork [id=" + id + ", title=" + title + ", medium=" + medium + ", yearCreated=" + yearCreated + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, title, medium, yearCreated);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Artwork other = (Artwork) obj;
		return Objects.equals(id, other.id) && Objects.equals(title, other.title)
				&& Objects.equals(medium, other.medium) && yearCreated == other.yearCreated;
	}
}
