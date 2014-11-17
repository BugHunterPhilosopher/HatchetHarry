package org.alienlabs.hatchetharry.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ResourceBundle;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBaseTest;
import org.apache.wicket.atmosphere.EventBus;
import org.apache.wicket.atmosphere.config.AtmosphereLogLevel;
import org.apache.wicket.atmosphere.config.AtmosphereTransport;
import org.apache.wicket.atmosphere.tester.AtmosphereTester;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * Test of the ImportDeckService (it only uses the WicketTester in order to load
 * the Spring context).
 */
@ContextConfiguration(locations = { "classpath:applicationContext.xml",
		"classpath:applicationContextTest.xml" })
public class ImportDeckServiceTest extends SpringContextLoaderBaseTest
{
	@Test
	public void testImportDeck() throws FileNotFoundException, IOException
	{
		// Init
		final PersistenceService persistenceService = this.context
				.getBean(PersistenceService.class);
		final ImportDeckService importDeckService = this.context.getBean(ImportDeckService.class);

		final boolean auraBantAlreadyExists = (null != persistenceService
				.getDeckArchiveByName("Aura Bant"));

		final int initialNumberOfDeckArchives = persistenceService.countDeckArchives();
		final int initialNumberOfDecks = persistenceService.countDecks();
		final int initialNumberOfCollectibleCards = persistenceService.countCollectibleCards();
		final int initialNumberOfMagicCards = persistenceService.countMagicCards();

		final File deck = new File(ResourceBundle.getBundle(DataGenerator.class.getCanonicalName())
				.getString("AuraBantDeck"));
		final byte[] content = new byte[475];

		final FileInputStream fis = new FileInputStream(deck);
		if (fis.read(content) == -1)
		{
			fis.close();
			Assert.fail("Aura Bant.txt seems to be empty");
		}
		fis.close();

		final String deckContent = new String(content, "UTF-8");

		// Run
		importDeckService.importDeck(deckContent, "Aura Bant", true);

		// Verify
		final int finalNumberOfDeckArchives = persistenceService.countDeckArchives();
		final int finalNumberOfDecks = persistenceService.countDecks();
		final int finalNumberOfCollectibleCards = persistenceService.countCollectibleCards();
		final int finalNumberOfMagicCards = persistenceService.countMagicCards();

		if (auraBantAlreadyExists)
		{
			Assert.assertEquals(initialNumberOfDeckArchives, finalNumberOfDeckArchives);
			Assert.assertEquals(initialNumberOfDecks, finalNumberOfDecks);
			Assert.assertEquals(initialNumberOfCollectibleCards, finalNumberOfCollectibleCards);
			Assert.assertEquals(initialNumberOfMagicCards, finalNumberOfMagicCards);
		}
		else
		{
			Assert.assertEquals(initialNumberOfDeckArchives + 1, finalNumberOfDeckArchives);
			Assert.assertEquals(initialNumberOfDecks + 1, finalNumberOfDecks);
			Assert.assertEquals(initialNumberOfCollectibleCards + 60, finalNumberOfCollectibleCards);
			Assert.assertEquals(initialNumberOfMagicCards + 60, finalNumberOfMagicCards);
		}
	}

}
