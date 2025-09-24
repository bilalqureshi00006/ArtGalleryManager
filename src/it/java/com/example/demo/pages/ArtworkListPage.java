package com.example.demo.pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ArtworkListPage {
	private final WebDriver driver;
	private final String url;
	private final WebDriverWait wait;

	public ArtworkListPage(WebDriver driver, int port) {
		this.driver = driver;
		this.url = "http://localhost:" + port + "/artworks";
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
	}

	public ArtworkListPage open() {
		driver.get(url);
		wait.until(d -> d.findElement(By.tagName("body")));
		return this;
	}

	public boolean isEmpty() {
		List<WebElement> rows = driver.findElements(By.cssSelector("#artwork_table tbody tr"));
		return rows.isEmpty();
	}

	public String tableText() {
		List<WebElement> tables = driver.findElements(By.id("artwork_table"));
		return tables.isEmpty() ? "" : tables.get(0).getText();
	}

	public ArtworkFormPage clickNew() {
		driver.findElement(By.cssSelector("a[href='/artworks/new']")).click();
		return new ArtworkFormPage(driver);
	}

	public ArtworkFormPage clickEdit(long id) {
		driver.findElement(By.cssSelector("a[href='/artworks/edit/" + id + "']")).click();
		return new ArtworkFormPage(driver);
	}

	public void clickDelete(long id) {
		driver.findElement(By.cssSelector("a[href='/artworks/delete/" + id + "']")).click();
	}
}