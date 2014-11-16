package org.alienlabs.hatchetharry.view.component.card;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.PlayerAndCard;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.gui.DrawModeBehavior;
import org.alienlabs.hatchetharry.view.component.gui.ExternalImage;
import org.alienlabs.hatchetharry.view.component.zone.PutToExileFromBattlefieldBehavior;
import org.alienlabs.hatchetharry.view.component.zone.PutToGraveyardFromBattlefieldBehavior;
import org.alienlabs.hatchetharry.view.component.zone.PutToHandFromBattlefieldBehavior;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = { "SE_INNER_CLASS",
		"SIC_INNER_SHOULD_BE_STATIC_ANON" }, justification = "In Wicket, serializable inner classes are common. And as the parent Page is serialized as well, this is no concern. This is no bad practice in Wicket")
public class CardPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(CardPanel.class);

	private final PutToHandFromBattlefieldBehavior putToHandFromBattlefieldBehavior;
	private final PutToGraveyardFromBattlefieldBehavior putToGraveyardFromBattlefieldBehavior;
	private final PutToExileFromBattlefieldBehavior putToExileFromBattlefieldBehavior;
	private final DestroyTokenBehavior destroyTokenBehavior;

	@SpringBean
	PersistenceService persistenceService;

	public CardPanel(final String id, final IModel<PlayerAndCard> playerAndCard)
	{
		super(id, playerAndCard);
		Injector.get().inject(this);
		this.setOutputMarkupId(true);

		this.add(new Behavior()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void renderHead(final Component component, final IHeaderResponse response)
			{
				super.renderHead(component, response);
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/contextmenu/jquery.contextMenu.js")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/contextmenu/jquery.contextMenu.css")));
			}
		});

		final MagicCard myCard = this.persistenceService.getCardFromUuid(playerAndCard.getObject()
				.getCard().getUuidObject());

		final WebMarkupContainer cardHandle = new WebMarkupContainer("cardHandle");
		cardHandle.setOutputMarkupId(true);
		final String uuidValidForJs = playerAndCard.getObject().getCard().getUuidObject()
				.toString().replace("-", "_");
		cardHandle.setMarkupId("cardHandle" + uuidValidForJs);
		cardHandle.add(new AttributeModifier("name", myCard.getTitle()));

		if ("baldu".equals(id))
		{
			cardHandle.add(new AttributeModifier("class", "baldu"));
		}

		final WebMarkupContainer menutoggleButton = new WebMarkupContainer("menutoggleButton");
		menutoggleButton.setOutputMarkupId(true);
		menutoggleButton.setMarkupId("menutoggleButton" + uuidValidForJs);

		final Form<String> form = new Form<String>("form");
		form.setOutputMarkupId(true);

		this.putToHandFromBattlefieldBehavior = new PutToHandFromBattlefieldBehavior(playerAndCard
				.getObject().getCard().getUuidObject());
		menutoggleButton.add(this.putToHandFromBattlefieldBehavior);

		this.putToGraveyardFromBattlefieldBehavior = new PutToGraveyardFromBattlefieldBehavior(
				playerAndCard.getObject().getCard().getUuidObject());
		menutoggleButton.add(this.putToGraveyardFromBattlefieldBehavior);

		this.putToExileFromBattlefieldBehavior = new PutToExileFromBattlefieldBehavior(
				playerAndCard.getObject().getCard().getUuidObject());
		menutoggleButton.add(this.putToExileFromBattlefieldBehavior);

		this.destroyTokenBehavior = new DestroyTokenBehavior(playerAndCard.getObject().getCard()
				.getUuidObject());
		menutoggleButton.add(this.destroyTokenBehavior);

		final CardRotateBehavior cardRotateBehavior = new CardRotateBehavior(playerAndCard
				.getObject().getCard().getUuidObject());

		final DrawModeBehavior drawModeBehavior = new DrawModeBehavior(playerAndCard.getObject()
				.getCard().getUuidObject(), myCard, playerAndCard.getObject().getPlayer());
		menutoggleButton.add(cardRotateBehavior, drawModeBehavior);

		final ArrowDrawBehavior arrowDrawBehavior = new ArrowDrawBehavior("cardHandle"
				+ uuidValidForJs);
		menutoggleButton.add(arrowDrawBehavior);

		final String requestedSessionId = this.getHttpServletRequest().getRequestedSessionId();
		final TextField<String> jsessionid = new TextField<String>("jsessionid", new Model<String>(
				requestedSessionId));
		jsessionid.setMarkupId("jsessionid" + playerAndCard.getObject().getCard().getUuidObject());
		jsessionid.setOutputMarkupId(true);

		CardPanel.LOGGER.info("jsessionid: " + requestedSessionId);
		CardPanel.LOGGER.info("uuid: " + playerAndCard.getObject().getCard().getUuidObject());
		final TextField<String> mouseX = new TextField<String>("mouseX", new Model<String>("0"));
		final TextField<String> mouseY = new TextField<String>("mouseY", new Model<String>("0"));
		mouseX.setMarkupId("mouseX" + playerAndCard.getObject().getCard().getUuidObject());
		mouseY.setMarkupId("mouseY" + playerAndCard.getObject().getCard().getUuidObject());
		mouseX.setOutputMarkupId(true);
		mouseY.setOutputMarkupId(true);

		final WebMarkupContainer bullet = new WebMarkupContainer("bullet");
		bullet.setOutputMarkupId(true).setMarkupId("bullet" + uuidValidForJs);

		final ExternalImage cardImage = new ExternalImage("cardImage", playerAndCard.getObject()
				.getCard().getBigImageFilename());
		cardImage.setOutputMarkupId(true);

		final ExternalImage cardRotate = new ExternalImage("cardRotate", "/image/rightArrow.png");
		cardRotate.setOutputMarkupId(true);
		cardRotate.setMarkupId("cardRotate" + uuidValidForJs);

		if ("baldu".equals(id))
		{
			cardImage.setMarkupId("baldu");
		}
		else
		{
			cardImage.setMarkupId("card" + uuidValidForJs);
		}

		if (null != playerAndCard.getObject().getPlayer())
		{
			if ("infrared".equals(playerAndCard.getObject().getPlayer().getSide().getSideName()))
			{
				cardImage.add(new AttributeModifier("style", "border: 1px solid red;"));
			}
			else if ("ultraviolet".equals(playerAndCard.getObject().getPlayer().getSide()
					.getSideName()))
			{
				cardImage.add(new AttributeModifier("style", "border: 1px solid purple;"));
			}
		}
		else
		{
			cardImage.add(new AttributeModifier("style", "border: 1px solid yellow;"));
		}

		final CardInBattlefieldContextMenu contextMenu = new CardInBattlefieldContextMenu(
				"contextMenu", playerAndCard.getObject().getCard().getUuidObject(), myCard);

		form.add(jsessionid, mouseX, mouseY, bullet, cardImage, cardRotate, contextMenu);
		menutoggleButton.add(form);

		final WebMarkupContainer side = new WebMarkupContainer("side");
		if (playerAndCard.getObject().getPlayer().getSide().getSideName()
				.equals(HatchetHarrySession.get().getPlayer().getSide().getSideName()))
		{
			side.add(new AttributeModifier("class", "battlefieldCardsForSide1"));
		}
		else
		{
			side.add(new AttributeModifier("class", "battlefieldCardsForSide2"));
		}

		side.add(menutoggleButton);
		cardHandle.add(side);
		this.add(cardHandle);

		final CardTooltipBehavior ctb = new CardTooltipBehavior(playerAndCard.getObject().getCard()
				.getUuidObject());
		this.add(ctb);

		if (HatchetHarrySession.get().isDisplayTooltips())
		{
			this.add(new MagicCardTooltipPanel("tooltip", myCard.getUuidObject(), myCard
					.getBigImageFilename(), myCard.getOwnerSide(), myCard));
		}
		else
		{
			this.add(new WebMarkupContainer("tooltip"));
		}
	}

	public HttpServletRequest getHttpServletRequest()
	{
		final Request servletWebRequest = this.getRequest();
		return (HttpServletRequest)servletWebRequest.getContainerRequest();
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

	public PutToGraveyardFromBattlefieldBehavior getPutToGraveyardFromBattlefieldBehavior()
	{
		return this.putToGraveyardFromBattlefieldBehavior;
	}

	public PutToHandFromBattlefieldBehavior getPutToHandFromBattlefieldBehavior()
	{
		return this.putToHandFromBattlefieldBehavior;
	}

	public PutToExileFromBattlefieldBehavior getPutToExileFromBattlefieldBehavior()
	{
		return this.putToExileFromBattlefieldBehavior;
	}

}