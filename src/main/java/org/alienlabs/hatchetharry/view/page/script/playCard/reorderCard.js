window.setTimeout(function() {
	if ($('.battlefieldCardsForSide1.cards').sortable()) {
		$('.battlefieldCardsForSide1.cards').sortable('destroy');
	}
	jQuery('.battlefieldCardsForSide1.cards').sortable(
		{
				placeholder: "ui-state-highlight",
				update: function(e, ui) {
					// ui.item.sortable is the model but it is not updated until after update
					$(this).data("old_position", ui.item.sortable.index);
					$(this).data("new_position", ui.item.index());
					// new Index because the ui.item is the node and the visual element has been reordered
				},
				stop: function(event, ui) {
					if ($(this).data("old_position") !== ($(this).data("new_position")))
					{
						var myId = ui.item.children(":first").children(":first").children(":first").attr('id');
						var uuid=myId.slice(10, myId.length).replace(new RegExp("_", 'g'), "-");

						dontZoom = true;
						Wicket.Ajax.get({'u': '${url}&uuid=' + uuid + '&index=' + $(this).data("new_position")});
					}
				}
		});

	jQuery('.maximize').unbind('click').click(function() {
		var me = $(this).prevAll('.magicCard');
		if (me.hasClass('details')) {
			me.css('z-index', '');
		} else {
			me.css('z-index', ++zIndex);
		};
		$(this).parents('.cardContainer').toggleClass('details');
	});

	jQuery('.gallery .magicCard').unbind('click').click(function() {
		if ($(this).hasClass('details')) {
			$(this).css('z-index', '');
		} else {
			$(this).css('z-index', ++zIndex);
		};
		jQuery(this).parents('.cardContainer').toggleClass('details');
	});

}, 1500);