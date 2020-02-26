$(function () {
    $('[data-toggle="tooltip"]').tooltip()
});

function onMouseEnterStatusCard(){
    var obj = $.ajax({
        url: "/server/api/status/memory/" + event.target.id,
        type: 'GET',
        async: false
    }).responseText;
    $("#" + event.target.id)
        .attr('data-original-title', obj)
        .tooltip('show');
}