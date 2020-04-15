function preSubmit(){
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