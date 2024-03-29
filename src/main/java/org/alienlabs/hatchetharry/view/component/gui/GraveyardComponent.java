package org.alienlabs.hatchetharry.view.component.gui;

import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.zone.PutToZonePanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.beans.factory.annotation.Required;

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = { "SE_INNER_CLASS",
		"SIC_INNER_SHOULD_BE_STATIC_ANON" }, justification = "In Wicket, serializable inner classes are common. And as the parent Page is serialized as well, this is no concern. This is no bad practice in Wicket")
public class GraveyardComponent extends Panel
{
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer graveyardCardsPlaceholder;
	private final ListView<MagicCard> allCards;
	private final WebMarkupContainer thumbsPlaceholder;
	@SpringBean
	private PersistenceService persistenceService;

	/**
	 * @param id
	 *            wicket:id
	 * @param ids
	 *            gameId, playerId, deckId
	 */
	public GraveyardComponent(final String id, final Long... ids)
	{
		super(id);

		this.setOutputMarkupId(true);
		this.setMarkupId("graveyardGallery");

		this.graveyardCardsPlaceholder = new WebMarkupContainer("graveyardCardsPlaceholder");
		this.graveyardCardsPlaceholder.setOutputMarkupId(true);

		final List<MagicCard> allCardsInGraveyard = this.persistenceService
				.getAllCardsInGraveyardForAGameAndAPlayer((ids.length == 0 ? HatchetHarrySession
						.get().getPlayer().getGame().getId() : ids[0]), (ids.length == 0
						? HatchetHarrySession.get().getPlayer().getId()
						: ids[1]), (ids.length == 0 ? HatchetHarrySession.get().getPlayer()
						.getDeck().getDeckId() : ids[2]));

		this.allCards = new ListView<MagicCard>("graveyardCards", allCardsInGraveyard)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<MagicCard> item)
			{
				final MagicCard card = item.getModelObject();

				final WebMarkupContainer wrapper = new WebMarkupContainer("wrapper");
				wrapper.setMarkupId("wrapper" + item.getIndex());
				wrapper.setOutputMarkupId(true);

				final ExternalImage handImagePlaceholder = new ExternalImage(
						"graveyardImagePlaceholder", card.getBigImageFilename());
				handImagePlaceholder.setMarkupId("placeholder" + card.getUuid().replace("-", "_"));
				handImagePlaceholder.setOutputMarkupId(true);

				wrapper.add(handImagePlaceholder);
				item.add(wrapper);

			}
		};
		this.graveyardCardsPlaceholder.add(this.allCards);
		this.add(this.graveyardCardsPlaceholder);

		this.thumbsPlaceholder = new WebMarkupContainer("thumbsPlaceholder");
		final ListView<MagicCard> thumbs = new ListView<MagicCard>("thumbs", allCardsInGraveyard)
		{
			private static final long serialVersionUID = -787466183866875L;

			@Override
			protected void populateItem(final ListItem<MagicCard> item)
			{
				final MagicCard card = item.getModelObject();

				final WebMarkupContainer crossLinkDiv = new WebMarkupContainer("crossLinkDiv");
				crossLinkDiv.setMarkupId("cross-link-div" + item.getIndex());
				crossLinkDiv.setOutputMarkupId(true);

				final WebMarkupContainer crossLink = new WebMarkupContainer("crossLink");
				crossLink.add(new AttributeModifier("href", "#" + (item.getIndex() + 1)));
				crossLink.setMarkupId("cross-link" + item.getIndex());
				crossLink.setOutputMarkupId(true);

				final ExternalImage thumb = new ExternalImage("thumbPlaceholder",
						card.getThumbnailFilename());
				thumb.setMarkupId("placeholder" + card.getUuid().replace("-", "_") + "_img");
				thumb.setOutputMarkupId(true);
				thumb.add(new AttributeModifier("name", card.getTitle()));

				crossLink.add(thumb);
				crossLinkDiv.add(crossLink);
				item.add(crossLinkDiv);
			}
		};
		thumbs.setOutputMarkupId(true);
		this.thumbsPlaceholder.setOutputMarkupId(true);

		this.thumbsPlaceholder.add(thumbs);
		this.add(this.thumbsPlaceholder);

		final PutToZonePanel putToZonePanel = new PutToZonePanel("putToZonePanel",
				CardZone.GRAVEYARD, this.persistenceService.getPlayer((ids.length == 0
						? HatchetHarrySession.get().getPlayer().getId()
						: ids[1])), false);
		this.add(putToZonePanel);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
