package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.example.demo.model.Artist;
import com.example.demo.pages.ArtistFormPage;
import com.example.demo.pages.ArtistListPage;
import com.example.demo.repositories.ArtistRepository;
import com.example.demo.repositories.ArtworkRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ArtistWebControllerIT {

	@Autowired
	private ArtistRepository artistRepository;

	@Autowired
	private ArtworkRepository artworkRepository;

	@LocalServerPort
	private int port;

	private WebDriver driver;
	private ArtistListPage listPage;

	@BeforeEach
	void setup() {
		driver = new HtmlUnitDriver();
		artistRepository.deleteAll();
		artworkRepository.deleteAll();
		listPage = new ArtistListPage(driver, port);
	}

	@AfterEach
	void tearDown() {
		driver.quit();
	}

	@Test
	void test_HomePageWithArtists_ShowsNameAndNationality() {
		artistRepository.save(new Artist(null, "Picasso", "Spanish"));
		listPage.open();
		assertThat(listPage.tableText()).contains("Picasso", "Spanish");
	}

	@Test
	void test_CanCreateArtistViaForm() {
		ArtistFormPage form = listPage.open().clickNew();
		form.setName("Da Vinci").setNationality("Italian").submit();

		listPage.open();
		assertThat(listPage.tableText()).contains("Da Vinci", "Italian");
	}

	@Test
	void test_CanUpdateArtist() {
		Artist a = artistRepository.save(new Artist(null, "Van Gogh", "Dutch"));

		ArtistFormPage form = listPage.open().clickEdit(a.getId());
		form.setName("Vincent").setNationality("Netherlands").submit();

		listPage.open();
		assertThat(listPage.tableText()).contains("Vincent", "Netherlands");
	}

	@Test
	void test_CanDeleteArtist() {
		Artist a = artistRepository.save(new Artist(null, "Rembrandt", "Dutch"));

		listPage.open().clickDelete(a.getId());
		assertThat(driver.findElement(By.tagName("h1")).getText())
				.contains("Artist with ID " + a.getId() + " was deleted.");

		driver.findElement(By.cssSelector("form[action='/artists'] button")).click();
		listPage.open();
		if (listPage.isEmpty()) {
			assertThat(driver.findElement(By.xpath("//*[text()='No artist']")).isDisplayed()).isTrue();
		} else {
			assertThat(listPage.tableText()).doesNotContain("Rembrandt", "Dutch");
		}
	}
}