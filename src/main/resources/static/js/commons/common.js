'use strict';
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
                toastr.error('중복체크 중 오류가 발생하였습니다.');
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