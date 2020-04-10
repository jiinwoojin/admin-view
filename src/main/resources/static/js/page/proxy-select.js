var selectedLayers = [];
var selectedCaches = [];
var selectedSources = [];

function onclick_remove_button(id, data){
    var arr = window[id];
    var idx = arr.indexOf(data);
    if (idx > -1) arr.splice(idx, 1);
    window[id] = arr.slice();
    rendering_selected_data(id, window[id]);
}

function onclick_add_button(id, data){
    var arr = window[id];
    if(arr.includes(data)){
        alert('이미 설정 목록에 추가 되어 있습니다.');
    } else {
        arr.push(data);
        window[id] = arr.slice();
        rendering_selected_data(id, window[id]);
    }
}

function rendering_selected_data(id, data){
    var dom = ''

    if(data.length > 0) {
        for (var i = 0; i < data.length; i++) {
            dom += `
                <li class="list-group-item d-flex justify-content-between align-items-center">
                    ${data[i]}
                    <span class="badge badge-warning badge-pill" style="cursor: pointer;" onclick="onclick_remove_button(\'${id}\', \'${data[i]}\')">
                        <i class="fas fa-trash"></i>
                    </span>
                </li>
            `;
        }
    } else {
        dom = `
            <li class="list-group-item d-flex justify-content-between align-items-center">
                선택된 데이터 없음
                <span class="badge badge-danger badge-pill">
                    <i class="fas fa-times"></i>
                </span>
            </li>
        `;
    }

    document.getElementById(id).innerHTML = dom;
}

function initialize_selected_data(){
    $.ajax({
        url: CONTEXT + '/server/api/proxy/form',
        type: 'get',
        success: function(data){
            window['selectedLayers'] = data.layers;
            window['selectedCaches'] = data.caches;
            window['selectedSources'] = data.sources;

            rendering_selected_data("selectedLayers",  window['selectedLayers']);
            rendering_selected_data("selectedSources",  window['selectedSources']);
            rendering_selected_data("selectedCaches",  window['selectedCaches']);
        },
        error: function(e){
            console.log(e);
        }
    });
}

function nameValidation(form, field){
    if(jQuery.isEmptyObject(form[field].value)) {
        form[field].focus();
        toastr.warning("이름이 입력되지 않았습니다.");
        return false;
    }else if(jQuery(form[field]).data("check-duplicate") === false) {
        toastr.warning("이름 중복확인이 되지 않았습니다.");
        return false;
    }
    return true;
}

function preSubmit(form){
    var action = form.action;
    if(action.indexOf('add') > -1){
        switch(form.id){
            case 'add-proxy-layer' :
                return nameValidation(form, 'proxyLayerName');
            case 'add-proxy-source' :
                return nameValidation(form, 'proxySourceName');
            case 'add-proxy-cache' :
                return nameValidation(form, 'proxyCacheName');
        }
    }
    return false;
}

function onclick_close(field, context){
    $(`#${field}`).val('');
    $(`#${field}`).data("check-duplicate",false);
    $(`#duplicate-check-message-proxy-${context}`).text("중복확인을 해주세요.");
}

window.onload = function() {
    initialize_selected_data();

    $('#proxyLayerName').change(function() {
        $('#proxyLayerName').data("check-duplicate",false);
        $('#duplicate-check-message-proxy-layer').text("중복확인을 해주세요.");
    });

    $('#proxySourceName').change(function() {
        $('#proxySourceName').data("check-duplicate",false);
        $('#duplicate-check-message-proxy-source').text("중복확인을 해주세요.");
    });

    $('#proxyCacheName').change(function() {
        $('#proxyCacheName').data("check-duplicate",false);
        $('#duplicate-check-message-proxy-cache').text("중복확인을 해주세요.");
    });
}

