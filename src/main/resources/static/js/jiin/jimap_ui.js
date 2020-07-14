'use strict';

/*
 *
 *   JIMAP Script
 *   [공통] : 자바스크립트 공통 UI
 *
 */

// [공통] 간단한 DataTables 생성을 위한 문장
function initialize_dataTable(id, _options) {
    var options = _options
    if(options === undefined || options === null){
        options = {}
    }
    options.language = {
        lengthMenu : "자료 길이 _MENU_",
            zeroRecords : "해당 자료가 존재하지 않습니다.",
            info : "_PAGES_ 페이지 중 _PAGE_ 페이지 / 총 _MAX_ 건",
            infoEmpty : "해당 자료가 존재하지 않습니다.",
            infoFiltered : "(총 _TOTAL_ 건)",
            search : "검색 키워드",
            paginate : {
            first : "<<",
                last : ">>",
                next : ">",
                previous : "<"
        }
    }
    $('#' + id).DataTable(options);
}

// [공통] 가장 작은 DataTables 생성을 위한 문장
function initialize_small_dataTable(id) {
    $('#' + id).DataTable({
        language : {
            lengthMenu : "자료 길이 _MENU_",
            zeroRecords : "해당 자료가 존재하지 않습니다.",
            info : "_PAGES_ 페이지 중 _PAGE_ 페이지 / 총 _MAX_ 건",
            infoEmpty : "해당 자료가 존재하지 않습니다.",
            infoFiltered : "(총 _TOTAL_ 건)",
            search : "검색 키워드",
            paginate : {
                first : "<<",
                last : ">>",
                next : ">",
                previous : "<"
            }
        },
        pageLength: 5,
        "lengthMenu": [[5, -1], [5, "모두"]]
    });
}

// [공통] 디자인 관련 함수 모음
$(document).ready(function () {
    // radio check
    $('.i-checks').iCheck({
        checkboxClass: 'icheckbox_square-green',
        radioClass: 'iradio_square-green',
    });

    uiDesign.mapSizeControl();	// map size
    uiDesign.serachControl();	// serach
    uiDesign.Skin();			// SKIN class

});

$(window).on('resize', function() {
    uiDesign.mapSizeControl();
});

var uiDesign = {
    // map size
    mapSizeControl : function(){
        var	topBar_H 	= $('.navbar.navbar-static-top').outerHeight(),
            titleBar_H 	= $('.wrapper.page-heading').outerHeight(),
            footer_H	= $('.footer').outerHeight(),
            map_H		= $(document).outerHeight() - topBar_H - titleBar_H - footer_H

        $('.map_area').outerHeight(map_H+5);
    },

    //serach
    serachControl : function(){
        $(document).on('kepress, click','.btn.searchMore', function(){
            $('#searchDetail').toggleClass("active");
        });
    },

    Skin : function(){
        var Skin=$('#wrapper').attr('class');
        $('body').addClass(Skin);
    }
}

// [공통] Tooltip 이 모두 가능하게끔.
$(function () {
    $('[data-toggle="tooltip"]').tooltip()
})
