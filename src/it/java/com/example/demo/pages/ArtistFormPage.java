package com.example.demo.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ArtistFormPage {
	private final WebDriver driver;
	private final WebDriverWait wait;

	public ArtistFormPage(WebDriver driver, String cameFromUrl) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		wait.until(d -> d.findElement(By.name("artist_record")));
	}

	public ArtistFormPage setName(String name) {
		WebElement e = driver.findElement(By.id("name"));
		e.clear();
		e.sendKeys(name);
		return this;
	}

	public ArtistFormPage setNationality(String nationality) {
		WebElement e = driver.findElement(By.id("nationality"));
		e.clear();
		e.sendKeys(nationality);
		return this;
	}

	public ArtistFormPage selectArtwork(String title) {
		WebElement sel = driver.findElement(By.id("artworks"));
		new Select(sel).selectByVisibleText(title);
		return this;
	}

	public ArtistListPage submit() {
		driver.findElement(By.cssSelector("button[type=submit]")).click();

		return new ArtistListPage(driver, 0);
	}
}