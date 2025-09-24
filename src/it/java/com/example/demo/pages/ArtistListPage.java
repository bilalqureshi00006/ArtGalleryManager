package com.example.demo.pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ArtistListPage {
	private final WebDriver driver;
	private final String url;
	private final WebDriverWait wait;

	public ArtistListPage(WebDriver driver, int port) {
		this.driver = driver;
		this.url = "http://localhost:" + port + "/artists";
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
	}

	public ArtistListPage open() {
		driver.get(url);
		wait.until(d -> d.findElement(By.tagName("body")));
		return this;
	}

	public boolean isEmpty() {
		return !driver.findElements(By.id("artist_table")).isEmpty() ? false
				: !driver.findElements(By.xpath("//*[text()='No artist']")).isEmpty();
	}

	public String tableText() {
		List<WebElement> tables = driver.findElements(By.id("artist_table"));
		return tables.isEmpty() ? "" : tables.get(0).getText();
	}

	public ArtistFormPage clickNew() {
		driver.findElement(By.cssSelector("a[href='/artists/new']")).click();
		return new ArtistFormPage(driver, url);
	}

	public ArtistFormPage clickEdit(long id) {
		driver.findElement(By.cssSelector("a[href='/artists/edit/" + id + "']")).click();
		return new ArtistFormPage(driver, url);
	}

	public void clickDelete(long id) {
		driver.findElement(By.cssSelector("a[href='/artists/delete/" + id + "']")).click();
	}
}