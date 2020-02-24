var display2D = new Vue({
    el: '#map_block',
    data: {
        selected: 'pills-display2d',
        lat: 0, // 경도
        lot: 0, // 위도
    },
    methods: {
        onClickTab : function(event) {
            this.selected = event.target.id;
        }
    }
});

function modal_draggable(id){
    $(id).draggable({
        handle: ".modal-header"
    });
}

$(document).ready(function() {
    modal_draggable('#layer_modal');
    modal_draggable('#shape_modal');
    modal_draggable('#military_modal');

    $('.btn-overlay').tooltip()
});
