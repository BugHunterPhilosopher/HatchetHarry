package org.alienlabs.hatchetharry.view.component;

import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

public class TeamInfoModalWindow extends Panel
{
	private static final long serialVersionUID = -4905521543219537705L;

	public TeamInfoModalWindow(final String id)
	{
		super(id);


		final Image img1 = new Image("img1",
				new ResourceReference(HomePage.class, "image/logo.png"));

		final Form<String> form = new Form<String>("form");

		final Label founder = new Label("founder", new Model<String>(
				"Founder: Zala \"AlienQueen\" Goupil (goupilpierre@gmail.com)"));
		final Label lead = new Label("lead", new Model<String>(
				"Lead developer: Zala \"AlienQueen\" Goupil (goupilpierre@gmail.com)"));
		final Label tester1 = new Label("tester1", new Model<String>(
				"Tester: Jean Leherle (jean.ravnica@hotmail.fr)"));

		final AjaxButton submit = new AjaxButton("submit", new Model<String>("OK"))
		{
			private static final long serialVersionUID = 181448499864L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				ModalWindow.closeCurrent(target);
			}

		};
		submit.setOutputMarkupId(true);

		form.add(founder, lead, tester1, submit);
		this.add(img1, form);
	}

}
