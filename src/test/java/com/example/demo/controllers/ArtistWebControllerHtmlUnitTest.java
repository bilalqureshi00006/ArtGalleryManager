package com.example.demo.controllers;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlTable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.demo.model.Artist;
import com.example.demo.service.ArtistService;
import com.example.demo.service.ArtworkService;

@WebMvcTest(controllers = ArtistWebController.class)
@Import(ArtistWebController.class)
class ArtistWebControllerHtmlUnitTest {

	@Autowired
	private WebClient webClient;

	@MockitoBean
	private ArtistService artistService;

	@MockitoBean
	private ArtworkService artworkService;

	@Test
	void test_HomePageTitle() throws Exception {
		when(artistService.getAllArtists()).thenReturn(emptyList());
		HtmlPage page = webClient.getPage("/artists");
		assertThat(page.getTitleText()).isEqualTo("Artists");
	}

	@Test
	void testHomePageWithNoArtists() throws Exception {
		when(artistService.getAllArtists()).thenReturn(emptyList());
		HtmlPage page = webClient.getPage("/artists");
		assertThat(page.getBody().getTextContent()).contains("No artist");
	}

	@Test
	void test_HomePage_ShouldProvideALinkForCreatingANewArtist() throws Exception {
		when(artistService.getAllArtists()).thenReturn(emptyList());
		HtmlPage page = webClient.getPage("/artists");
		assertThat(page.getAnchorByText("New artist").getHrefAttribute()).isEqualTo("/artists/new");
	}

	@Test
	void test_HomePageWithArtists_ShouldShowThemInATable() throws Exception {
		List<Artist> artists = asList(new Artist(1L, "Picasso", "Spanish"), new Artist(2L, "Da Vinci", "Italian"));

		when(artistService.getAllArtists()).thenReturn(artists);

		HtmlPage page = webClient.getPage("/artists");

		assertThat(page.getBody().getTextContent()).doesNotContain("No artist");

		HtmlTable table = page.getHtmlElementById("artist_table");
		String normalized = removeWindowsCR(table.asNormalizedText());

		assertThat(normalized).isEqualTo("""
				ID\tName\tNationality\tEdit\tDelete
				1\tPicasso\tSpanish\tEdit\tDelete
				2\tDa Vinci\tItalian\tEdit\tDelete""");

		page.getAnchorByHref("/artists/edit/1");
		page.getAnchorByHref("/artists/edit/2");
	}

	@Test
	void testEditNonExistentArtist() throws Exception {
		when(artistService.getArtistById(1L)).thenReturn(null);
		when(artworkService.getAllArtworks()).thenReturn(emptyList());
		HtmlPage page = webClient.getPage("/artists/edit/1");
		assertThat(page.getBody().getTextContent()).contains("No artist found with id: 1");
	}

	@Test
	void testEditExistentArtist() throws Exception {
		Artist original = new Artist(1L, "Original", "German");
		when(artistService.getArtistById(1L)).thenReturn(original);
		when(artworkService.getAllArtworks()).thenReturn(emptyList());

		HtmlPage page = webClient.getPage("/artists/edit/1");
		HtmlForm form = page.getFormByName("artist_record");

		form.getInputByName("name").setValueAttribute("Modified");
		form.getInputByName("nationality").setValueAttribute("Italian");
		form.getButtonByName("btn_submit").click();

		verify(artistService).updateArtistById(1L, new Artist(1L, "Modified", "Italian"));
	}

	@Test
	void testEditNewArtist() throws Exception {
		when(artworkService.getAllArtworks()).thenReturn(emptyList());

		HtmlPage page = webClient.getPage("/artists/new");
		HtmlForm form = page.getFormByName("artist_record");

		form.getInputByName("name").setValueAttribute("NewArtist");
		form.getInputByName("nationality").setValueAttribute("French");
		form.getButtonByName("btn_submit").click();

		verify(artistService).insertNewArtist(new Artist(null, "NewArtist", "French"));
	}

	@Test
	void testDeleteArtist_ShouldDisplayConfirmationMessage() throws Exception {
		doNothing().when(artistService).deleteArtistById(3L);

		HtmlPage page = webClient.getPage("/artists/delete/3");

		verify(artistService, times(1)).deleteArtistById(3L);

		String content = page.getBody().getTextContent();
		assertThat(content).contains("Artist with ID 3 was deleted.");

		HtmlButton newButton = page.getElementByName("btn_new_artist");
		assertThat(newButton).isNotNull();

		HtmlButton allButton = page.getElementByName("btn_all_artists");
		assertThat(allButton).isNotNull();
	}

	private String removeWindowsCR(String s) {
		return s.replace("\r", "");
	}
}