package org.alienlabs.hatchetharry.view.component;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.channel.NotifierAction;
import org.alienlabs.hatchetharry.model.channel.NotifierCometChannel;
import org.alienlabs.hatchetharry.model.channel.PutToGraveyardCometChannel;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class PutToGraveyardFromBattlefieldBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PutToGraveyardFromBattlefieldBehavior.class);
	private final UUID uuid;

	@SpringBean
	private PersistenceService persistenceService;

	public PutToGraveyardFromBattlefieldBehavior(final UUID _uuid)
	{
		Injector.get().inject(this);
		this.uuid = _uuid;
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		PutToGraveyardFromBattlefieldBehavior.LOGGER.info("respond");

		final String uniqueid = this.uuid.toString();
		MagicCard mc = null;

		try
		{
			mc = this.persistenceService.getCardFromUuid(UUID.fromString(uniqueid));
		}
		catch (final IllegalArgumentException e)
		{
			PutToGraveyardFromBattlefieldBehavior.LOGGER.error("error parsing UUID of card", e);
		}

		if (null == mc)
		{
			return;
		}

		final HatchetHarrySession session = HatchetHarrySession.get();
		PutToGraveyardFromBattlefieldBehavior.LOGGER.info("playerId in respond(): "
				+ session.getPlayer().getId());

		mc.setZone(CardZone.GRAVEYARD);
		mc.setTapped(false);
		this.persistenceService.updateCard(mc);

		final Long gameId = session.getPlayer().getGame().getId();

		final Player p = this.persistenceService.getPlayer(session.getPlayer().getId());
		final Deck d = p.getDeck();
		final List<MagicCard> graveyard = d.reorderMagicCards(this.persistenceService
				.getAllCardsInGraveyardForAGameAndAPlayer(gameId, p.getId(), d.getDeckId()));
		this.persistenceService.updateAllMagicCards(graveyard);
		final List<MagicCard> battlefield = d.reorderMagicCards(this.persistenceService
				.getAllCardsInBattlefieldForAGameAndAPlayer(gameId, p.getId(), d.getDeckId()));
		this.persistenceService.updateAllMagicCards(battlefield);

		final List<BigInteger> allPlayersInGame = PutToGraveyardFromBattlefieldBehavior.this.persistenceService
				.giveAllPlayersFromGame(gameId);

		for (int i = 0; i < allPlayersInGame.size(); i++)
		{
			final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();

			final String _pageUuid = HatchetHarryApplication.getCometResources().get(
					playerToWhomToSend);

			final Player targetPlayer = this.persistenceService.getPlayer(mc.getDeck()
					.getPlayerId());
			final String targetPlayerName = targetPlayer.getName();
			final Long targetDeckId = mc.getDeck().getDeckId();

			final PutToGraveyardCometChannel _ptgcc = new PutToGraveyardCometChannel(gameId, mc,
					session.getPlayer().getName(), targetPlayerName, targetPlayer.getId(),
					targetDeckId, (allPlayersInGame.get(i).longValue() == targetPlayer.getId()
							.longValue()));
			final NotifierCometChannel _ncc = new NotifierCometChannel(
					NotifierAction.PUT_CARD_TO_GRAVGEYARD_FROM_BATTLEFIELD_ACTION, gameId, session
							.getPlayer().getId(), session.getPlayer().getName(), "", "",
					mc.getTitle(), null, targetPlayerName);

			HatchetHarryApplication.get().getEventBus().post(_ptgcc, _pageUuid);
			HatchetHarryApplication.get().getEventBus().post(_ncc, _pageUuid);

			if (allPlayersInGame.get(i).longValue() == targetPlayer.getId().longValue())
			{
				targetPlayer.setGraveyardDisplayed(true);
				this.persistenceService.updatePlayer(targetPlayer);
			}
		}
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
