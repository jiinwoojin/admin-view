var hidWidth;
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
    return (($('.wrapper').outerWidth())-widthOfList()-getLeftPosi())-scrollBarWidths;
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

    if (getLeftPosi()<0) {
        $('.scroller-left').show();
    }
    else {
        $('.item').animate({left:"-="+getLeftPosi()+"px"},'slow');
        $('.scroller-left').hide();
    }
}

reAdjust();

$(window).on('resize',function(e){
    reAdjust();
});

$('.scroller-right').click(function() {

    $('.scroller-left').fadeIn('slow');
    $('.scroller-right').fadeOut('slow');

    $('.list').animate({left:"+="+widthOfHidden()+"px"},'slow',function(){

    });
});

$('.scroller-left').click(function() {

    $('.scroller-right').fadeIn('slow');
    $('.scroller-left').fadeOut('slow');

    $('.list').animate({left:"-="+getLeftPosi()+"px"},'slow',function(){

    });
});

// line click
$('#draw_line').click(function(e) {
    if (d3Canvas === null) {
        d3Canvas = new SVGCanvas(options);
    }

    SelectedShape(d3Canvas, "line");
});

$('#draw_circle').click(function(e) {
    if (d3Canvas === null) {
        d3Canvas = new SVGCanvas(options);
    }

    SelectedShape(d3Canvas, 'circle');
});

$('#draw_point').click(function(e) {
    if (d3Canvas === null) {
        d3Canvas = new SVGCanvas(options);
    }

    SelectedShape(d3Canvas, 'point');
});