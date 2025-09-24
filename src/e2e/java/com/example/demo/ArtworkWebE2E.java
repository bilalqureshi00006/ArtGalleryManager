package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

class ArtworkWebE2E {

	private static int port = Integer.parseInt(System.getProperty("server.port", "8080"));
	private static String baseUrl = "http://localhost:" + port;
	private WebDriver driver;

	@BeforeAll
	static void setupClass() {
		WebDriverManager.chromedriver().setup();
	}

	@BeforeEach
	void setup() {
		driver = new ChromeDriver();
	}

	@AfterEach
	void cleanup() {
		if (driver != null) {
			driver.quit();
		}
	}

	@Test
	void test_CreateArtwork() {
		String title = "The Scream " + System.currentTimeMillis();

		driver.get(baseUrl + "/artworks/new");
		driver.findElement(By.name("title")).sendKeys(title);
		driver.findElement(By.name("medium")).sendKeys("Tempera on cardboard");

		WebElement yearField = driver.findElement(By.name("yearCreated"));
		((JavascriptExecutor) driver).executeScript("arguments[0].value='1893';", yearField);

		driver.findElement(By.name("btn_submit")).click();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("artwork_table"), title));

		WebElement artworkTable = driver.findElement(By.id("artwork_table"));
		assertThat(artworkTable.getText()).contains(title, "Tempera on cardboard", "1893");
	}

	@Test
	void test_DeleteArtwork() {
		String title = "The Scream " + System.currentTimeMillis();

		driver.get(baseUrl + "/artworks/new");
		driver.findElement(By.name("title")).sendKeys(title);
		driver.findElement(By.name("medium")).sendKeys("Tempera on cardboard");

		WebElement yearField = driver.findElement(By.name("yearCreated"));
		((JavascriptExecutor) driver).executeScript("arguments[0].value='1893';", yearField);
		driver.findElement(By.name("btn_submit")).click();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("artwork_table"), title));

		driver.get(baseUrl + "/artworks");
		String before = driver.findElement(By.tagName("body")).getText();
		assertThat(before).contains(title);

		WebElement screamRow = driver.findElement(By.xpath("//tr[td/text() = '" + title + "']"));
		screamRow.findElement(By.linkText("Delete")).click();

		String conf = driver.findElement(By.tagName("h1")).getText();
		assertThat(conf).contains("Artwork with ID");

		wait.until(ExpectedConditions.presenceOfElementLocated(By.name("btn_all_artworks"))).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("artwork_table")));

		String afterDelete = driver.findElement(By.tagName("body")).getText();
		assertThat(afterDelete).doesNotContain(title);
	}
}