package org.alienlabs.hatchetharry.view.component;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
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

public class DrawModeBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(DrawModeBehavior.class);

	private final UUID uuid;

	private final MagicCard mc;

	public DrawModeBehavior(final UUID _uuid, final MagicCard _mc)
	{
		this.uuid = _uuid;
		this.mc = _mc;
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		StringBuilder js = new StringBuilder();

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("arrowDrawUrl", this.getCallbackUrl());
		variables.put("posX", this.mc.getX());
		variables.put("posY", this.mc.getY());
		variables.put("uuidValidForJs", this.uuid.toString().replace("-", "_"));
		variables.put("drawMode", HatchetHarrySession.get().isDrawMode().booleanValue());

		final TextTemplate template1 = new PackageTextTemplate(HomePage.class,
				"script/draggableHandle/drawMode.js");
		template1.interpolate(variables);
		js = js.append("\n" + template1.asString());

		response.render(JavaScriptHeaderItem.forScript(js.toString(), null));
		try
		{
			template1.close();
		}
		catch (final IOException e)
		{
			DrawModeBehavior.LOGGER.error(
					"unable to close template1 in CardTooltipBehavior#renderHead()!", e);
		}
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
	}

}