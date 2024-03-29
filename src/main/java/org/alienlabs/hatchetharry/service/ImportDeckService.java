package org.alienlabs.hatchetharry.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.CollectibleCard;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.DeckArchive;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class ImportDeckService implements Serializable
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ImportDeckService.class);
	private static final long serialVersionUID = 1L;
	@SpringBean
	private PersistenceService persistenceService;

	public boolean importDeck(final String fileContent, final String deckName,
			final boolean testDeck)
	{
		DeckArchive deckArchive;

		if (testDeck)
		{
			deckArchive = this.persistenceService.getDeckArchiveByName(deckName);
			if (null != deckArchive)
			{
				return false;
			}
		}
		deckArchive = new DeckArchive();
		deckArchive.setDeckName(deckName);
		deckArchive = this.persistenceService.saveOrUpdateDeckArchive(deckArchive);

		Deck deck = new Deck();
		deck.setPlayerId(1L);
		deck.setDeckArchive(deckArchive);
		deck.setDeckId(-1L);

		deck = this.persistenceService.saveDeck(deck);

		if (fileContent.split("\n").length < 3)
		{
			return false;
		}

		final List<MagicCard> allCards = new ArrayList<>();

		for (final String line : fileContent.split("\n"))
		{
			ImportDeckService.LOGGER.info("line: " + line);

			if ("".equals(line.trim()))
			{
				break;
			}

			final int numberOfItems = Integer.parseInt(line.split("\\s+")[0]);
            String cardName = "";

            // There's a tab
            if (line.indexOf('\t') != -1)
			{
                // And no space
                if (line.indexOf(' ') == -1)
				{
                    cardName = line.split("\\s+")[1];
                }
                // And a space after the tab
                else if (line.indexOf('\t') < line.indexOf(' '))
				{
                    cardName = line.substring(line.indexOf('\t') + 1);
                }
                // And a space before the tab
                else
				{
                    cardName = line.substring(line.indexOf(' ') + 1);
                }
			}
			// There's no tab
            else
			{
                // And a space
                if (line.indexOf(' ') != -1)
				{
                    cardName = line.substring(line.indexOf(' ') + 1);
                }
            }

			ImportDeckService.LOGGER.info(numberOfItems + " x " + cardName);

			for (int i = 0; i < numberOfItems; i++)
			{
				final CollectibleCard cc = new CollectibleCard();
				cc.setTitle(cardName);
				cc.setDeckArchiveId(deckArchive.getDeckArchiveId());

				// A CollectibleCard can be duplicated: lands, normal cards
				// which may be present 4 times in a Deck...
				this.persistenceService.saveCollectibleCard(cc);

				final MagicCard card = new MagicCard("cards/" + cardName + "_small.jpg", "cards/"
						+ cardName + ".jpg", "cards/" + cardName + "Thumb.jpg", cardName, "", "",
						null, Integer.valueOf(0));
				card.setGameId(-1L);
				card.setUuidObject(UUID.randomUUID());
				card.setX(16L);
				card.setY(16L);
				card.setDeck(deck);
				card.setZone(CardZone.LIBRARY);

				deck.setDeckArchive(deckArchive);
				allCards.add(card);
			}
		}
		this.persistenceService.saveAllMagicCards(allCards);
		this.persistenceService.updateDeck(deck);
		this.persistenceService.updateDeckArchive(deckArchive);
		return true;
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
