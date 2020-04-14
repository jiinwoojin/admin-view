function preSubmit(form){
    if(jQuery.isEmptyObject(form['username'].value)) {
        form['username'].focus();
        toastr.warning("이름이 입력되지 않았습니다.");
        return false;
    } else if(jQuery(form['username']).data("check-duplicate") === false) {
        toastr.warning("이름 중복확인이 되지 않았습니다.");
        return false;
    } else {
        var passwd1 = document.getElementById("password1");
        var passwd2 = document.getElementById("password2");
        if(jQuery.isEmptyObject(passwd1.value) || jQuery.isEmptyObject(passwd2.value)){
            toastr.warning('비밀번호를 입력하세요.');
            return false;
        } else {
            if(passwd1.value != passwd2.value) {
                toastr.warning('비밀번호 확인이 일치하지 않습니다.');
                return false;
            }
        }

        var name = document.getElementById('name');
        var email = document.getElementById('email');
        if(jQuery.isEmptyObject(name.value)){
            toastr.warning('이름을 입력하세요.');
            return false;
        } else if(jQuery.isEmptyObject(email.value)){
            toastr.warning('E-Mail 를 입력하세요.');
            return false;
        } else return true;
    }

    return false;
}

window.onload = function() {
    $('#username').change(function() {
        $('#username').data("check-duplicate",false);
        $('#duplicate-check-username').text("중복확인을 해주세요.");
    });
}