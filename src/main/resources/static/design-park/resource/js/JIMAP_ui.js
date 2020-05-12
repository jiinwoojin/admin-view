/*
 *
 *   JIMAP Script
 *   [공통] : 자바스크립트 공통 로직
 *
 */

'use strict';

// [공통] CONTEXT Path 를 얻기 위한 문장
let ContextPath = '#ContextPath';
const CONTEXT = $(ContextPath).attr('data-contextPath') ? $(ContextPath).attr('data-contextPath') : '';

// [공통] 간단한 DataTables 생성을 위한 문장
function initialize_dataTable(id) {
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
        }
    });
}

// [공통] 체크박스 디자인 추가
$(document).ready(function () {
    // radio check
    $('.i-checks').iCheck({
        checkboxClass: 'icheckbox_square-green',
        radioClass: 'iradio_square-green',
    });
});

// [공통] 지도 크기 자동 조절
var mapSizeControl = function(){
    var	topBar_H 	= $('.navbar.navbar-static-top').outerHeight(),
        titleBar_H 	= $('.white-bg.page-heading').outerHeight(),
        footer_H	= $('.footer').outerHeight(),
        map_H		= $(document).outerHeight() - topBar_H - titleBar_H - footer_H;

    $('#wrapper-context').outerHeight(map_H+5);
    // $('.map_area').outerHeight(map_H+5);
}

// [공통] 지도 기능 RESIZE
$(window).on('resize', function() {
    mapSizeControl();
});

/**
 * 스크립트 공통
 */
var jiCommon = {
    showLoading : function() {
        jQuery("#loader-wrapper").show();
    },
    hideLoading : function() {
        jQuery("#loader-wrapper").hide();
    },
    reloadDocker : function() {
        if(confirm("Docker를 재시작 하시겠습니까?")){
            jiCommon.showLoading();
            jQuery.post({
                url: CONTEXT + "/server/api/docker/reload"
            }).done(function(result) {
                if (result) {
                    toastr.success('Docker Reload Complete.');
                } else {
                    toastr.error('Docker Reload Fail.');
                }
            }).fail(function() {
                toastr.error('Docker Reload Error.');
            }).always(function() {
                jiCommon.hideLoading();
            });
        }
    },
    duplicateCheck : function(type, targetId, messageId) {
        var name = jQuery(targetId).val();
        if (jQuery.isEmptyObject(name)) {
            jQuery(targetId).focus();
            return
        }
        jiCommon.showLoading();
        jQuery.post({
            url: CONTEXT + "/server/api/check/duplicate",
            data: {type : type, name : name}
        }).done(function(result) {
            if (!result) {
                jQuery(messageId).html("사용가능한 이름입니다.");
                jQuery(targetId).data("check-duplicate", true);
            } else {
                toastr.error('사용불가능한 이름입니다.');
                jQuery(targetId).data("check-duplicate",false);
            }
        }).fail(function() {
            toastr.error('중복체크에 실패하였습니다.');
            jQuery(targetId).data("check-duplicate",false);
        }).always(function() {
            jiCommon.hideLoading();
        });
    }
};



