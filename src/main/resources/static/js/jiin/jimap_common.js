'use strict';

var jiCommon = {};

jiCommon.prototype = {
    showLoading : function showLoading() {
        $('#loader-wrapper').show();
    },
    hideLoading : function hideLoading() {
        $('#loader-wrapper').hide();
    },
    // [공통] 중복 체크 함수
    duplicateCheck : function duplicateCheck(type, targetId, messageId) {
        var name = $(targetId).val();
        if ($.isEmptyObject(name)) {
            $(targetId).focus();
            return
        }

        jiCommon.showLoading();

        $.post({
            url : CONTEXT + '/server/api/check/duplicate',
            data : {
                type : type,
                name : name
            }
        }).done(function(result) {
            if (!result) {
                $(messageId).html('사용 가능한 이름입니다.');  // Message 로 통합
                $(targetId).data('check-duplicate', true);
            } else {
                toastr.error('사용 불가능한 이름입니다.');         // Message 로 이동
                $(targetId).data('check-duplicate', false);
            }
        }).fail(function() {
            toastr.error('중복체크에 실패 하엿습니다.');
            $(targetId).data('check-duplicate', false);
        }).always(function() {
            jiCommon.hideLoading();
        });
    },
    // [공통] sleep 함수
    sleep : function sleep(t) {
        return new Promise(resolve => setTimeout(resolve, t));
    }
}