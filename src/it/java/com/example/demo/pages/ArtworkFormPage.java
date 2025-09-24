package com.example.demo.pages;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ArtworkFormPage {
	private final WebDriver driver;
	private final WebDriverWait wait;

	public ArtworkFormPage(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		wait.until(d -> d.findElement(By.name("artwork_record")));
	}

	public ArtworkFormPage setTitle(String title) {
		WebElement e = driver.findElement(By.id("title"));
		e.clear();
		e.sendKeys(title);
		return this;
	}

	public ArtworkFormPage setMedium(String medium) {
		WebElement e = driver.findElement(By.id("medium"));
		e.clear();
		e.sendKeys(medium);
		return this;
	}

	public ArtworkFormPage setYearCreated(String year) {
		WebElement e = driver.findElement(By.id("yearCreated"));
		e.clear();
		e.sendKeys(year);
		return this;
	}

	public ArtworkFormPage selectArtistByName(String artistName) {
		WebElement dropdown = driver.findElement(By.id("artist"));
		List<WebElement> options = dropdown.findElements(By.tagName("option"));

		for (WebElement option : options) {
			if (option.getText().trim().equals(artistName)) {
				option.click();
				break;
			}
		}
		return this;
	}

	public ArtworkListPage submit() {
		driver.findElement(By.cssSelector("button[type=submit]")).click();
		return new ArtworkListPage(driver, 0);
	}

	public String contributingArtistsText() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div > ul > li")));

		List<WebElement> items = driver.findElements(By.cssSelector("div > ul > li"));
		if (items.isEmpty()) {
			return "";
		}
		return items.stream().map(WebElement::getText).collect(Collectors.joining("\n"));
	}
}