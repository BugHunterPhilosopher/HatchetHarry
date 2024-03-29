package org.alienlabs.hatchetharry.view.component.card;

import java.io.IOException;

import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CardTooltipBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(CardTooltipBehavior.class);

	public CardTooltipBehavior()
	{
		// Nothing to do here
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		final TextTemplate template = new PackageTextTemplate(HomePage.class,
				"script/draggableHandle/cardTooltip.js");

		response.render(JavaScriptHeaderItem.forScript(template.asString(), null));
		try
		{
			template.close();
		}
		catch (final IOException e)
		{
			CardTooltipBehavior.LOGGER.error(
					"unable to close template in CardTooltipBehavior#renderHead()!", e);
		}
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		// Nothing to do here
	}

}
