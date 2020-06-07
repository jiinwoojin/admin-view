'use strict';

// [공통] CONTEXT Path 를 얻기 위한 문장
var ContextPath = '#ContextPath';
var CONTEXT = $(ContextPath).attr('data-contextPath') ? $(ContextPath).attr('data-contextPath') : '';

/**
 * JIMAP Script
 * [공통] : 자바스크립트 공통 로직
 */
var jiCommon = {
    showLoading : function showLoading() {
        $('#loader-wrapper').show();
    },
    hideLoading : function hideLoading() {
        $('#loader-wrapper').hide();
    },
    // [공통] sleep 함수
    sleep : function sleep(t) {
        return new Promise(resolve => setTimeout(resolve, t));
    },
    // [공통] 유효성 체크 함수
    valid : {
        // [공통] MGRS 유효성 확인
        mgrs : function mgrs(mgrs) {

        },
        // [공통] UTM 유효성 확인
        utm : function utm(utm) {

        },
        // [공통] 경위도 유효성 확인
        lonLat : function lonLat(point) {

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
                url: CONTEXT + '/server/api/check/duplicate',
                data: {
                    type: type,
                    name: name
                }
            }).done(function (result) {
                if (!result) {
                    $(messageId).html('사용 가능한 이름입니다.');  // Message 로 통합
                    $(targetId).data('check-duplicate', true);
                } else {
                    toastr.error('사용 불가능한 이름입니다.');         // Message 로 이동
                    $(targetId).data('check-duplicate', false);
                }
            }).fail(function () {
                toastr.error('중복체크에 실패 하엿습니다.');
                $(targetId).data('check-duplicate', false);
            }).always(function () {
                jiCommon.hideLoading();
            });
        }
    }
};
