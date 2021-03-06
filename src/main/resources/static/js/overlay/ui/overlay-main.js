var scrollBarWidths = 40;

var widthOfList = function(){
    var itemsWidth = 0;
    $('.list li').each(function(){
        // var itemWidth = $(this).outerWidth();
        // itemsWidth += itemWidth;
        itemsWidth += 57;
    });
    return itemsWidth;
};

var widthOfHidden = function(){
    return (($('.wrapper').outerWidth()) - widthOfList() - getLeftPosi()) - scrollBarWidths;
};

var getLeftPosi = function(){
    return $('.list').position().left;
};

var reAdjust = function(){
    if (($('.wrapper').outerWidth()) < widthOfList()) {
        $('.scroller-right').show();
    }
    else {
        $('.scroller-right').hide();
    }

    if (getLeftPosi() < 0) {
        $('.scroller-left').show();
    }
    else {
        $('.item').animate({left:"-="+getLeftPosi() + "px"},'slow');
        $('.scroller-left').hide();
    }
};

reAdjust();

$(window).on('resize', function(){
    reAdjust();
});

$('.scroller-right').click(function() {

    $('.scroller-left').fadeIn('slow');
    $('.scroller-right').fadeOut('slow');

    $('.list').animate({left: "+=" + widthOfHidden() + "px"},'slow',function() {

    });
});

$('.scroller-left').click(function() {

    $('.scroller-right').fadeIn('slow');
    $('.scroller-left').fadeOut('slow');

    $('.list').animate({left: "-=" + getLeftPosi() + "px"},'slow',function() {

    });
});

$('.btn-overlay').click(function() {
    if (this.id) {
        var _id;
        switch (this.id.split('_')[1]) {
            case 'line' :
                _id = 2;
                break;
            case 'rect' :
                _id = 3;
                break;
            case 'roundrect' :
                _id = 4;
                break;
            case 'triangle' :
                _id = 5;
                break;
        }

        jiCommon.overlay.selectedShape(_id);
    }
});