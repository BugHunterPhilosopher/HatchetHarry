package org.alienlabs.hatchetharry.serverSideTest.util;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.Game;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.service.DataGenerator;
import org.alienlabs.hatchetharry.service.ImportDeckService;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.zone.PlayCardFromHandBehavior;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.atmosphere.EventBus;
import org.apache.wicket.atmosphere.config.AtmosphereLogLevel;
import org.apache.wicket.atmosphere.config.AtmosphereTransport;
import org.apache.wicket.atmosphere.tester.AtmosphereTester;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringContextLoaderBaseTest
{
	static final ClassPathXmlApplicationContext CLASS_PATH_XML_APPLICATION_CONTEXT = new ClassPathXmlApplicationContext(
			new String[] { "applicationContext.xml", "applicationContextTest.xml" });
	protected static transient ApplicationContext context;
	protected static AtmosphereTester waTester;
	protected static transient WicketTester tester;
	static HatchetHarryApplication webApp;
	protected static PersistenceService persistenceService;
	private static HatchetHarrySession session;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		webApp = new HatchetHarryApplication()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void init()
			{
				context = CLASS_PATH_XML_APPLICATION_CONTEXT;
				this.getComponentInstantiationListeners().add(
						new SpringComponentInjector(this, context, true));

				this.eventBus = new EventBus(this);
				this.eventBus.addRegistrationListener(this);
				this.eventBus.getParameters().setTransport(AtmosphereTransport.WEBSOCKET);
				this.eventBus.getParameters().setLogLevel(AtmosphereLogLevel.INFO);

				this.getMarkupSettings().setStripWicketTags(false);
				this.getDebugSettings().setOutputComponentPath(true);
			}
		};

		// start and render the test page
		tester = new WicketTester(webApp);
		persistenceService = context.getBean(PersistenceService.class);
		waTester = new AtmosphereTester(tester, new HomePage(new PageParameters()));
		session = HatchetHarrySession.get();
	}

	@After
	public void tearDown()
	{
		webApp.newSession(tester.getRequestCycle().getRequest(), tester.getRequestCycle()
				.getResponse());
	}

	@AfterClass
	public static void tearDownAfterClass()
	{
		persistenceService.resetDb();
	}

	public void startAGameAndPlayACard() throws Exception
	{
		// Create game
		this.tester.startPage(new HomePage(new PageParameters()));
		this.tester.assertRenderedPage(HomePage.class);

		this.tester.assertComponent("createGameLink", AjaxLink.class);
		this.tester.clickLink("createGameLink", true);

		final FormTester createGameForm = this.tester
				.newFormTester("createGameWindow:content:form");
		createGameForm.setValue("name", "Zala");
		createGameForm.setValue("sideInput", "1");
		createGameForm.setValue("deckParent:decks", "1");
		createGameForm.setValue("formats", "1");
		createGameForm.setValue("numberOfPlayers", "2");
		createGameForm.submit();

		Player p = this.persistenceService.getAllPlayersOfGame(
				HatchetHarrySession.get().getGameId()).get(0);
		p.setDeck(this.persistenceService.getDeck(p.getDeck().getDeckId()));
		Assert.assertEquals(60, p.getDeck().getCards().size());

		// Retrieve PlayCardFromHandBehavior
		this.tester.assertComponent("galleryParent:gallery:handCards:0", ListItem.class);
		final ListItem playCardLink = (ListItem)this.tester
				.getComponentFromLastRenderedPage("galleryParent:gallery:handCards:0");
		final PlayCardFromHandBehavior pcfhb = (PlayCardFromHandBehavior)playCardLink
				.getBehaviors().get(0);

		// For the moment, we should have no card in the battlefield
		final Long gameId = HatchetHarrySession.get().getGameId();
		final List<MagicCard> allCardsInBattlefield = this.persistenceService
				.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertEquals(0, allCardsInBattlefield.size());

		// Play a card
		this.tester.getRequest().setParameter("card",
				HatchetHarrySession.get().getFirstCardsInHand().get(0).getUuid());
		this.tester.executeBehavior(pcfhb);

		// One card on the battlefield, 6 in the hand
		Assert.assertEquals(1, this.persistenceService.getAllCardsInBattlefieldForAGame(gameId)
				.size());
		Assert.assertEquals(
				6,
				this.persistenceService.getAllCardsInHandForAGameAndAPlayer(gameId, p.getId(),
						p.getDeck().getDeckId()).size());

		// We still should not have more cards that the number of cards in the
		// deck
		p = this.persistenceService.getAllPlayersOfGame(HatchetHarrySession.get().getGameId()).get(
				0);
		Assert.assertEquals(60, p.getDeck().getCards().size());
	}
}
