window.setTimeout(function () {
	jQuery('#galleryParent').find('.cards').sortable({ placeholder: "ui-state-highlight"});

    function tooltips() {
        $(this).parents('.cardContainer').toggleClass('details');
    }
    $('.magicCard').unbind('click').click(tooltips);
}, 1000);