function modal_draggable(id){
    $(id).draggable({
        handle: ".modal-header"
    });
}

$(document).ready(function() {
    modal_draggable('#layer_modal');
    modal_draggable('#shape_modal');
    modal_draggable('#military_modal');

    $('.btn-overlay').tooltip();
});