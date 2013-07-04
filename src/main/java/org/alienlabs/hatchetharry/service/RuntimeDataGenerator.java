package org.alienlabs.hatchetharry.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.CollectibleCard;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.DeckArchive;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.persistence.dao.MagicCardDao;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

public class RuntimeDataGenerator implements Serializable
{
	private static final long serialVersionUID = 1L;

	private static final String[] TITLES1 = { "Goblin Guide", "Goblin Guide", "Goblin Guide",
			"Goblin Guide", "Vampire Lacerator", "Vampire Lacerator", "Vampire Lacerator",
			"Vampire Lacerator", "Bloodchief Ascension", "Bloodchief Ascension",
			"Bloodchief Ascension", "Bloodchief Ascension", "Mindcrank", "Mindcrank", "Mindcrank",
			"Lightning Bolt", "Lightning Bolt", "Lightning Bolt", "Lightning Bolt", "Arc Trail",
			"Arc Trail", "Arc Trail", "Arc Trail", "Staggershock", "Staggershock", "Staggershock",
			"Staggershock", "Volt Charge", "Volt Charge", "Volt Charge", "Volt Charge",
			"Tezzeret's Gambit", "Tezzeret's Gambit", "Tezzeret's Gambit", "Tezzeret's Gambit",
			"Hideous End", "Hideous End", "Hideous End", "Blackcleave Cliffs",
			"Blackcleave Cliffs", "Blackcleave Cliffs", "Blackcleave Cliffs", "Mountain",
			"Mountain", "Mountain", "Mountain", "Mountain", "Mountain", "Mountain", "Mountain",
			"Mountain", "Swamp", "Swamp", "Swamp", "Swamp", "Swamp", "Swamp", "Swamp", "Swamp",
			"Swamp" };

	private static final String[] TITLES2 = { "Goblin Guide", "Goblin Guide", "Goblin Guide",
			"Goblin Guide", "Spikeshot Elder", "Spikeshot Elder", "Spikeshot Elder",
			"Spikeshot Elder", "Kiln Fiend", "Kiln Fiend", "Kiln Fiend", "Kiln Fiend",
			"Shrine of Burning Rage", "Shrine of Burning Rage", "Shrine of Burning Rage",
			"Shrine of Burning Rage", "Gut Shot", "Gut Shot", "Gut Shot", "Gut Shot",
			"Lightning Bolt", "Lightning Bolt", "Lightning Bolt", "Lightning Bolt",
			"Burst Lightning", "Burst Lightning", "Burst Lightning", "Burst Lightning",
			"Searing Blaze", "Searing Blaze", "Searing Blaze", "Searing Blaze", "Arc Trail",
			"Arc Trail", "Arc Trail", "Arc Trail", "Staggershock", "Staggershock", "Staggershock",
			"Staggershock", "Teetering Peaks", "Teetering Peaks", "Teetering Peaks",
			"Teetering Peaks", "Mountain", "Mountain", "Mountain", "Mountain", "Mountain",
			"Mountain", "Mountain", "Mountain", "Mountain", "Mountain", "Mountain", "Mountain",
			"Mountain", "Mountain", "Mountain", "Mountain" };

	@SpringBean
	private MagicCardDao magicCardDao;
	@SpringBean
	private PersistenceService persistenceService;
	@SpringBean
	private ImportDeckService importDeckService;

	@Required
	public void setMagicCardDao(final MagicCardDao _magicCardDao)
	{
		this.magicCardDao = _magicCardDao;
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

	@Required
	public void setImportDeckService(final ImportDeckService _importDeckService)
	{
		this.importDeckService = _importDeckService;
	}

	@Transactional
	public Deck generateData(final Long playerId) throws IOException
	{
		if (null == this.persistenceService.getCardFromUuid(UUID
				.fromString("249c4f0b-cad0-4606-b5ea-eaee8866a347")))
		{
			final MagicCard baldu = new MagicCard("cards/Balduvian Horde_small.jpg",
					"cards/Balduvian Horde.jpg", "cards/Balduvian HordeThumb.jpg",
					"Balduvian Horde", "Isn't it a spoiler?");
			baldu.setUuidObject(UUID.fromString("249c4f0b-cad0-4606-b5ea-eaee8866a347"));
			final Deck fake = new Deck();
			fake.setDeckArchive(null);
			fake.setCards(null);
			fake.setPlayerId(-1l);

			baldu.setDeck(fake);
			baldu.setGameId(-1l);
			baldu.setX(350l);
			baldu.setY(350l);
			baldu.setZone(CardZone.BATTLEFIELD);
			this.persistenceService.saveDeck(fake);
			this.persistenceService.saveCard(baldu);
		}

		if (null == this.persistenceService.getDeckArchiveByName("Aura Bant"))
		{
			final File _deck = new File(ResourceBundle.getBundle(
					RuntimeDataGenerator.class.getCanonicalName()).getString("AuraBantDeck"));
			final byte[] content = new byte[475];

			final FileInputStream fis = new FileInputStream(_deck);
			if (fis.read(content) == -1)
			{
				fis.close();
			}
			fis.close();

			final String deckContent = new String(content, "UTF-8");
			this.importDeckService.importDeck(deckContent, "Aura Bant", false);
		}

		final Deck deckToReturn;

		if ((null == this.persistenceService.getDeckArchiveByName("aggro-combo Red / Black"))
				&& (null == this.persistenceService.getDeckArchiveByName("burn mono-Red")))
		{
			DeckArchive deckArchive1 = new DeckArchive();
			deckArchive1.setDeckName("aggro-combo Red / Black");
			deckArchive1 = this.persistenceService.saveDeckArchive(deckArchive1);

			Deck deck1 = new Deck();
			deck1.setPlayerId(1l);
			deck1.setDeckArchive(deckArchive1);

			DeckArchive deckArchive2 = new DeckArchive();
			deckArchive2.setDeckName("burn mono-Red");
			deckArchive2 = this.persistenceService.saveDeckArchive(deckArchive2);

			Deck deck2 = new Deck();
			deck2.setPlayerId(2l);
			deck2.setDeckArchive(deckArchive2);

			final List<Deck> decks = new ArrayList<Deck>();
			deck1 = this.persistenceService.saveDeck(deck1);
			deck2 = this.persistenceService.saveDeck(deck2);
			decks.add(0, deck1);
			decks.add(1, deck2);

			for (int j = 1; j < 3; j++)
			{
				for (int i = 0; i < 60; i++)
				{

					final CollectibleCard c = new CollectibleCard();
					c.setTitle((j == 1
							? RuntimeDataGenerator.TITLES1[i]
							: RuntimeDataGenerator.TITLES2[i]));
					c.setDeckArchiveId(j == 1 ? deckArchive1.getDeckArchiveId() : deckArchive2
							.getDeckArchiveId());
					// A CollectibleCard can be duplicated: lands, normal cards
					// which may be present 4 times in a Deck...
					this.persistenceService.saveCollectibleCard(c);

					if (j == 1l)
					{
						MagicCard card = new MagicCard("cards/" + RuntimeDataGenerator.TITLES1[i]
								+ "_small.jpg",
								"cards/" + RuntimeDataGenerator.TITLES1[i] + ".jpg", "cards/"
										+ RuntimeDataGenerator.TITLES1[i] + "Thumb.jpg",
								RuntimeDataGenerator.TITLES1[i], "");
						card.setGameId(1l);
						card.setDeck(decks.get(j - 1));
						card.setUuidObject(UUID.randomUUID());
						card.setZone(CardZone.LIBRARY);
						card = this.magicCardDao.save(card);

						final List<MagicCard> cards = decks.get(j - 1).getCards();
						cards.add(card);
						decks.get(j - 1).setCards(cards);
					}
					else
					{
						MagicCard card = new MagicCard("cards/" + RuntimeDataGenerator.TITLES1[i]
								+ "_small.jpg",
								"cards/" + RuntimeDataGenerator.TITLES1[i] + ".jpg", "cards/"
										+ RuntimeDataGenerator.TITLES1[i] + "Thumb.jpg",
								RuntimeDataGenerator.TITLES1[i], "");
						card.setGameId(1l);
						card.setDeck(decks.get(j - 1));
						card.setUuidObject(UUID.randomUUID());
						card.setX(16l);
						card.setY(16l);
						card.setZone(CardZone.LIBRARY);
						card = this.magicCardDao.save(card);

						final List<MagicCard> cards = decks.get(j - 1).getCards();
						cards.add(card);
						decks.get(j - 1).setCards(cards);
					}
				}

				this.persistenceService.updateDeck(decks.get(j - 1));
				this.persistenceService.updateDeckArchive(deckArchive1);
				this.persistenceService.updateDeckArchive(deckArchive2);

			}

			decks.get(0).setDeckArchive(deckArchive1);
			decks.get(1).setDeckArchive(deckArchive2);
			this.persistenceService.updateDeck(decks.get(0));
			this.persistenceService.updateDeck(decks.get(1));
			this.persistenceService.updateDeckArchive(deckArchive1);
			this.persistenceService.updateDeckArchive(deckArchive2);

			deckToReturn = deck2;
		}
		else
		{
			final DeckArchive deckArchive = this.persistenceService
					.getDeckArchiveByName("aggro-combo Red / Black");

			if (null == deckArchive)
			{
				throw new RuntimeException("no such deck in DB!");
			}

			Deck deck = this.persistenceService.getDeckByDeckArchiveIdAndPlayerId(
					deckArchive.getDeckArchiveId(), playerId);

			if (null == deck)
			{
				deck = new Deck();
				deck.setPlayerId(playerId);
				deck.setDeckArchive(deckArchive);
				this.persistenceService.saveDeck(deck);
			}

			deck.setPlayerId(playerId);
			deck.setDeckArchive(deckArchive);
			deck = this.persistenceService.saveDeck(deck);

			for (int i = 0; i < 60; i++) // TODO count number of cards in deck
			{
				final MagicCard card = new MagicCard("cards/" + RuntimeDataGenerator.TITLES1[i]
						+ "_small.jpg", "cards/" + RuntimeDataGenerator.TITLES1[i] + ".jpg",
						"cards/" + RuntimeDataGenerator.TITLES1[i] + "Thumb.jpg",
						RuntimeDataGenerator.TITLES1[i], "");
				card.setGameId(1l);
				card.setDeck(deck);
				card.setUuidObject(UUID.randomUUID());
				card.setX(16l);
				card.setY(16l);
				card.setZone(CardZone.LIBRARY);
				this.persistenceService.saveCard(card);

				final List<MagicCard> cards = deck.getCards();
				cards.add(card);
				deck.setCards(cards);
				this.persistenceService.updateDeck(deck);
			}

			deck.setDeckArchive(deckArchive);
			this.persistenceService.updateDeck(deck);

			deckToReturn = deck;
		}
		return deckToReturn;
	}
}
