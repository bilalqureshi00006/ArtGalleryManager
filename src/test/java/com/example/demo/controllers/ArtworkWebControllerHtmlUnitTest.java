package com.example.demo.controllers;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlTable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.demo.model.Artwork;
import com.example.demo.service.ArtistService;
import com.example.demo.service.ArtworkService;

@WebMvcTest(controllers = ArtworkWebController.class)
class ArtworkWebControllerHtmlUnitTest {

	@Autowired
	private WebClient webClient;

	@MockitoBean
	private ArtworkService artworkService;

	@MockitoBean
	private ArtistService artistService;

	@Test
	void test_HomePageTitle() throws Exception {
		HtmlPage page = webClient.getPage("/artworks");
		assertThat(page.getTitleText()).isEqualTo("Artworks");
	}

	@Test
	void testHomePageWithNoArtworks() throws Exception {
		when(artworkService.getAllArtworks()).thenReturn(emptyList());
		HtmlPage page = webClient.getPage("/artworks");
		assertThat(page.getBody().getTextContent()).contains("No artwork");
	}

	@Test
	void test_HomePage_ShouldProvideALinkForCreatingANewArtwork() throws Exception {
		HtmlPage page = webClient.getPage("/artworks");
		HtmlAnchor newLink = page.getAnchorByText("New artwork");
		assertThat(newLink.getHrefAttribute()).isEqualTo("/artworks/new");
	}

	@Test
	void test_HomePageWithArtworks_ShouldShowThemInATable() throws Exception {
		Artwork a1 = new Artwork(1L, "A1", "Oil", 2025, null);
		Artwork a2 = new Artwork(2L, "A2", "Acrylic", 2025, null);
		when(artworkService.getAllArtworks()).thenReturn(asList(a1, a2));

		HtmlPage page = webClient.getPage("/artworks");

		assertThat(page.getBody().getTextContent()).doesNotContain("No artwork");

		HtmlTable table = page.getHtmlElementById("artwork_table");
		String normalized = removeWindowsCR(table.asNormalizedText());

		assertThat(normalized).isEqualTo("""
				ID\tTitle\tMedium\tYear\tArtist\tEdit\tDelete
				1\tA1\tOil\t2025\tNo artist\tEdit\tDelete
				2\tA2\tAcrylic\t2025\tNo artist\tEdit\tDelete""");

		page.getAnchorByHref("/artworks/edit/1");
		page.getAnchorByHref("/artworks/edit/2");
	}

	@Test
	void testEditNonExistentArtwork() throws Exception {
		when(artworkService.getArtworkById(1L)).thenReturn(null);
		HtmlPage page = webClient.getPage("/artworks/edit/1");
		assertThat(page.getBody().getTextContent()).contains("No artwork found with id: 1");
	}

	@Test
	void testEditExistentArtwork() throws Exception {
		Artwork original = new Artwork(1L, "Original", "Milan", 2025, null);
		when(artworkService.getArtworkById(1L)).thenReturn(original);

		HtmlPage page = webClient.getPage("/artworks/edit/1");
		HtmlForm form = page.getFormByName("artwork_record");

		form.getInputByName("title").setValueAttribute("Modified");
		form.getInputByName("medium").setValueAttribute("Turin");
		form.getInputByName("yearCreated").setValueAttribute("2025");

		form.getButtonByName("btn_submit").click();

		verify(artworkService).updateArtworkById(1L, new Artwork(1L, "Modified", "Turin", 2025, null));
	}

	@Test
	void testEditNewArtwork() throws Exception {
		HtmlPage page = webClient.getPage("/artworks/new");
		final HtmlForm form = page.getFormByName("artwork_record");

		form.getInputByName("title").setValueAttribute("NewTitle");
		form.getInputByName("medium").setValueAttribute("Florence");
		form.getInputByName("yearCreated").setValueAttribute("2025");

		form.getButtonByName("btn_submit").click();

		verify(artworkService).insertNewArtwork(new Artwork(null, "NewTitle", "Florence", 2025, null));
	}

	@Test
	void testDeleteArtwork_ShouldDisplayConfirmationMessage() throws Exception {
		when(artworkService.deleteArtworkById(3L)).thenReturn(true);

		HtmlPage page = webClient.getPage("/artworks/delete/3");

		verify(artworkService, times(1)).deleteArtworkById(3L);

		String content = page.getBody().getTextContent();
		assertThat(content).contains("Artwork with ID 3 was deleted.");

		HtmlButton newButton = page.getElementByName("btn_new_artwork");
		assertThat(newButton).isNotNull();

		HtmlButton allButton = page.getElementByName("btn_all_artworks");
		assertThat(allButton).isNotNull();
	}

	private String removeWindowsCR(String s) {
		return s.replace("\r", "");
	}
}