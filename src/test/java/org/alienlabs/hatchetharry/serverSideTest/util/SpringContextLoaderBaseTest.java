package org.alienlabs.hatchetharry.serverSideTest.util;

import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.PlayCardFromHandBehavior;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.atmosphere.EventBus;
import org.apache.wicket.atmosphere.EventBusMock;
import org.apache.wicket.atmosphere.MapperContextMock;
import org.apache.wicket.atmosphere.config.AtmosphereLogLevel;
import org.apache.wicket.atmosphere.config.AtmosphereTransport;
import org.apache.wicket.core.request.mapper.IMapperContext;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringContextLoaderBaseTest
{
	public static final ClassPathXmlApplicationContext CLASS_PATH_XML_APPLICATION_CONTEXT = new ClassPathXmlApplicationContext(
			new String[] { "applicationContext.xml", "applicationContextTest.xml" });
	protected static transient WicketTester tester;
	public static HatchetHarryApplication webApp;
	public static transient ApplicationContext context;
	protected static String pageDocument;
	protected static PersistenceService persistenceService;

	@BeforeClass
	public static void setUpBeforeClass()
	{
		SpringContextLoaderBaseTest.webApp = new HatchetHarryApplication()
		{
			private static final long serialVersionUID = 1L;
			private MapperContextMock mapperContext;

			@Override
			public void init()
			{
				SpringContextLoaderBaseTest.context = SpringContextLoaderBaseTest.CLASS_PATH_XML_APPLICATION_CONTEXT;
				this.getComponentInstantiationListeners()
						.add(new SpringComponentInjector(this, SpringContextLoaderBaseTest.context,
								true));

				this.mapperContext = new MapperContextMock();
				this.eventBus = new EventBusMock(this, this.mapperContext);
				this.eventBus.addRegistrationListener(this);
				this.eventBus.getParameters().setTransport(AtmosphereTransport.WEBSOCKET);
				this.eventBus.getParameters().setLogLevel(AtmosphereLogLevel.DEBUG);
			}

			@Override
			public EventBus getEventBus()
			{
				return this.eventBus;
			}

			@Override
			protected IMapperContext newMapperContext()
			{
				return this.mapperContext;
			}

		};
		//
		// this.setPageManagerProvider(new IPageManagerProvider()
		// {
		// IPageManager mpm = new MockPageManager()
		// {
		// @Override
		// public void destroy()
		// {
		// }
		//
		// @Override
		// public void newSessionCreated()
		// {
		// }
		//
		// @Override
		// public void sessionExpired(final String sessionId)
		// {
		// }
		// };
		//
		// @Override
		// public IPageManager get(final IPageManagerContext _context)
		// {
		// return this.mpm;
		// }
		// });
		// }
		//
		// @Override
		// protected IPageManagerContext getPageManagerContext()
		// {
		// return ((EventBusMock)this.eventBus).pageManagerContext;
		// }
		// };
		SpringContextLoaderBaseTest.tester = new WicketTester(SpringContextLoaderBaseTest.webApp);
		// {
		//
		// IPageManagerProvider provider =
		// super.newTestPageManagerProvider();
		//
		// @Override
		// protected IPageManagerProvider newTestPageManagerProvider()
		// {
		// return this.provider;
		// }
		//
		// };

		SpringContextLoaderBaseTest.persistenceService = SpringContextLoaderBaseTest.context
				.getBean(PersistenceService.class);

		// start and render the test page
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);

		// assert rendered page class
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		SpringContextLoaderBaseTest.pageDocument = SpringContextLoaderBaseTest.tester
				.getLastResponse().getDocument();
	};

	@AfterClass
	public static void tearDownAfterClass()
	{
		SpringContextLoaderBaseTest.context.getBean(PersistenceService.class).resetDb();
	}

	public static Long startAGameAndPlayACard(final WicketTester _tester,
			final ApplicationContext _context)
	{
		// Create game
		_tester.assertComponent("createGameLink", AjaxLink.class);
		_tester.clickLink("createGameLink", true);

		final FormTester createGameForm = _tester.newFormTester("createGameWindow:content:form");
		createGameForm.setValue("name", "Zala");
		createGameForm.setValue("sideInput", "0");
		createGameForm.setValue("deckParent:decks", "0");
		createGameForm.submit();

		Player p = SpringContextLoaderBaseTest.persistenceService.getAllPlayersOfGame(
				HatchetHarrySession.get().getGameId()).get(0);
		Assert.assertEquals(60, p.getDeck().getCards().size());


		// Retrieve PlayCardFromHandBehavior
		_tester.assertComponent("playCardPlaceholder", WebMarkupContainer.class);
		_tester.assertComponent("playCardPlaceholder:playCardLink", WebMarkupContainer.class);
		final WebMarkupContainer playCardLink = (WebMarkupContainer)_tester
				.getComponentFromLastRenderedPage("playCardPlaceholder:playCardLink");
		final PlayCardFromHandBehavior pcfhb = (PlayCardFromHandBehavior)playCardLink
				.getBehaviors().get(0);

		// For the moment, we should have no card in the battlefield
		final Long gameId = HatchetHarrySession.get().getGameId();
		final List<MagicCard> allCardsInBattlefield = SpringContextLoaderBaseTest.persistenceService
				.getAllCardsInBattleFieldForAGame(gameId);
		Assert.assertEquals(0, allCardsInBattlefield.size());

		// Play a card
		_tester.getRequest().setParameter("card",
				HatchetHarrySession.get().getFirstCardsInHand().get(0).getUuid());
		_tester.executeBehavior(pcfhb);

		// We still should not have more cards that the number of cards in the
		// deck
		p = SpringContextLoaderBaseTest.persistenceService.getAllPlayersOfGame(
				HatchetHarrySession.get().getGameId()).get(0);
		Assert.assertEquals(60, p.getDeck().getCards().size());

		return gameId;
	}

}
