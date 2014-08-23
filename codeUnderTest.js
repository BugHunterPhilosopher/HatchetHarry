jQuery(document).ready(function () {
    var myGallery = new gallery(jQuery('myGallery'), {timed: true, fadeDuration: 500, delay: 10000, useHistoryManager: true, thumbWidth: 23, thumbHeight: 32, showInfopane: true, embedLinks: true, thumbCloseCarousel: true, showCarouselLabel: true, textShowCarousel: 'Hand', showArrows: true, showCarousel: true });
    myGallery.showCarousel();
    HistoryManager.start();
});