// The dock: a jQuery plugin
jQuery(document).ready(function() {
	jQuery('#dock').jqDock({
		align : 'middle'
	});
});

// The menubar, a jQuery plugin
jQuery(document).ready(function() {

	jQuery(".myMenu").buildMenu({
		additionalData : "pippo=1",
		menuWidth : 200,
		openOnRight : false,
		menuSelector : ".menuContainer",
		iconPath : "/image/",
		hasImages : true,
		fadeInTime : 100,
		fadeOutTime : 300,
		adjustLeft : 0,
		minZindex : "auto",
		adjustTop : 0,
		opacity : .95,
		shadow : true,
		shadowColor : "#ccc",
		hoverIntent : 0,
		openOnClick : false,
		closeOnMouseOut : false,
		closeAfter : 1000,
		submenuHoverIntent : 200
	});
});

// The website tour, a jQuery plugin
jQuery(document)
		.ready(
				function() {
					if (!(jQuery.Storage.get("tour") == "true")) {
						var config = {
							mainTitle : "First time here?",
							saveCookie : true,
							steps : [
									{
										"name" : "tour_1",
										"bgcolor" : "#444444",
										"color" : "white",
										"position" : "BOTTOM",
										"text" : "NEW! You can untap all your permanents at once using the toolbar button.",
										"time" : 10000
									},
									{
										"name" : "tour_2",
										"bgcolor" : "#444444",
										"color" : "white",
										"position" : "BOTTOM",
										"text" : "NEW! When you've joined a game, you'll be able to draw cards!",
										"time" : 10000
									},
									{
										"name" : "tour_3",
										"bgcolor" : "#444444",
										"color" : "white",
										"position" : "BOTTOM",
										"text" : "NEW! You can play cards and the opponent will see them on the battlefield.",
										"time" : 10000
									},
									{
										"name" : "tour_4",
										"bgcolor" : "#444444",
										"color" : "white",
										"position" : "TL",
										"text" : "This is the menubar, where you'll find most of the options of HatchetHarry, logically grouped into menu entries.",
										"time" : 10000
									},
									{
										"name" : "tour_5",
										"bgcolor" : "#444444",
										"color" : "white",
										"text" : "This is a card. You can move it by drag and drop using the small, green handle. If you do so, the opponent will see its move matched in its own browser. Additionaly, you can call a context menu by right-clicking of the card itself.",
										"position" : "TL",
										"time" : 15000
									},
									{
										"name" : "tour_6",
										"bgcolor" : "#444444",
										"color" : "white",
										"text" : "This is the reference clock. It displays the hour on the server, so it's the same hour for every player.",
										"position" : "BL",
										"time" : 10000
									},
									{
										"name" : "tour_7",
										"bgcolor" : "#444444",
										"color" : "white",
										"text" : "This is your hand. You can browse the cards in it and play one of them.",
										"position" : "TL",
										"time" : 10000
									},
									{
										"name" : "tour_8",
										"bgcolor" : "#444444",
										"color" : "white",
										"text" : "This is the dock. You can show the different zones of Magic using its icons.",
										"position" : "BL",
										"time" : 10000
									},
									{
										"name" : "tour_9",
										"bgcolor" : "#444444",
										"color" : "white",
										"text" : "Hide or display your hand using this button.",
										"position" : "L",
										"time" : 10000
									},
									{
										"name" : "tour_10",
										"bgcolor" : "#444444",
										"color" : "white",
										"text" : "Browse through your graveyard using this button. Your opponent will be notified of this action.",
										"position" : "L",
										"time" : 10000
									},
									{
										"name" : "tour_11",
										"bgcolor" : "#444444",
										"color" : "white",
										"text" : "Browse through the exilded cards using this button. Your opponent will be notified of this action.",
										"position" : "L",
										"time" : 10000
									},
									{
										"name" : "tour_12",
										"bgcolor" : "#444444",
										"color" : "white",
										"text" : "Hide every zone of the game except the battlefield using this button. Only the permanents remain, so that you can have a vista of the game. If you click here again, the previously displayed zones will be restored, at the same places.",
										"position" : "L",
										"time" : 15000
									},
									{
										"name" : "tour_13",
										"bgcolor" : "#444444",
										"color" : "white",
										"text" : "Browse through your library using this button. Your opponent will be notified of this action. Beware: you're supposed to play fair! Don't forget the rules of the game.",
										"position" : "L",
										"time" : 10000
									},
									{
										"name" : "tour_14",
										"bgcolor" : "#444444",
										"color" : "white",
										"text" : "This is a chat. Its behavior is standard and you DON'T need to refresh your browser in order to receive new messages.",
										"position" : "TL",
										"time" : 10000
									} ]
						};
						jQuery.tour.start(config);
					}
				});

// The toolbar, a jQuery plugin
jQuery(function() {

	jQuery('#floatingbar').css({
		height : 0
	}).animate({
		height : '38'
	}, 'slow');
	jQuery('.toolbarLink').tipsy({
		gravity : 's'
	});

});

jQuery(document).ready(function() {
	jQuery(".gallery a[rel^='prettyPhoto']").prettyPhoto({});
});

jQuery(document).ready(function() {
	var cometd = jQuery.cometd;
	cometd.addListener('/meta/handshake', function(message) {
		if (message.successful)
			var subscription = cometd.subscribe('/joinGame', function() {
			});
	});
	cometd.init('http://localhost:8080/cometd');
});