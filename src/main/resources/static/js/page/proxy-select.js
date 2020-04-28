var selectedLayers = [];
var selectedCaches = [];
var selectedSources = [];

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

    $('#sourceDataInsert,#sourceDataUpdate').each(function(){
        $('#' + this.id).on('show.bs.modal', function (event) {
            var mapFile = document.getElementById(this.id === 'sourceDataInsert' ? 'mapFile' : 'mapFile_u');
            var mapLayer = document.getElementById(this.id === 'sourceDataInsert' ? 'mapLayer' : 'mapLayer_u');

            if(mapFile.options.length === 1) {
                $.ajax({
                    url: CONTEXT + '/server/api/map/list',
                    success: function (data) {
                        for (var i = 0; i < data.length; i++) {
                            var option1 = document.createElement('option');
                            option1.value = JSON.stringify( { requestMap : data[i].mapFilePath, mapId : data[i].id });
                            option1.text = data[i].name + `(${data[i].mapFilePath})`;

                            var option2 = document.createElement('option');
                            option2.value = data[i].name;
                            option2.text = data[i].name;

                            mapFile.options.add(option1);
                            mapLayer.options.add(option2);
                        }
                    },
                    error: function (e) {
                        console.log(e);
                    }
                });
            }
        });

        $('#' + this.id).on('hide.bs.modal', function (event) {
            if(this.id === 'sourceDataInsert') {
                $("select#mapFile option").remove();
                $("select#mapLayer option").remove();
            } else {
                $("select#mapFile_u option").remove();
                $("select#mapLayer_u option").remove();
            }

            $('input[name="requestMap"]').val('[none]');

            var mapFile = document.getElementById(this.id === 'sourceDataInsert' ? 'mapFile' : 'mapFile_u');
            var option = document.createElement('option');
            option.value = JSON.stringify( { requestMap : "[none]", mapId : -1 });
            option.text = '-- Map 파일 선택 --';
            mapFile.options.add(option);

            var mapLayer = document.getElementById(this.id === 'sourceDataInsert' ? 'mapLayer' : 'mapLayer_u');
            option = document.createElement('option');
            option.value = '[none]';
            option.text = '-- 레이어 선택 --';
            mapLayer.options.add(option);

            $(this.id === 'sourceDataInsert' ? '#mapLayer_multiple' : '#mapLayer_multiple_u').tagsinput('removeAll');
            $('input[name="requestLayers"]').val('[none]');
        });
    });

    document.getElementById('proxyCacheName').onchange = function(e){
        var cacheDirectory = document.getElementById('proxyCacheDirectory');
        cacheDirectory.value = cacheDirectoryPath + e.target.value + '/';
    }
}

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

function submit_selected_data(){
    if(confirm('현재 선택한 목록들을 Map Proxy 설정에 저장합니다. 계속 진행 하시겠습니까?')){
        var form = document.createElement("form");
        form.setAttribute("charset", "UTF-8");
        form.setAttribute("method", "POST");
        form.setAttribute("action", CONTEXT + "/view/cache/checking-save");

        var hiddenField = document.createElement("input");

        hiddenField.setAttribute("type", "hidden");
        hiddenField.setAttribute("name", "layers");
        hiddenField.setAttribute("value", selectedLayers);
        form.appendChild(hiddenField);

        hiddenField = document.createElement("input");
        hiddenField.setAttribute("type", "hidden");
        hiddenField.setAttribute("name", "sources");
        hiddenField.setAttribute("value", selectedSources);
        form.appendChild(hiddenField);

        hiddenField = document.createElement("input");
        hiddenField.setAttribute("type", "hidden");
        hiddenField.setAttribute("name", "caches");
        hiddenField.setAttribute("value", selectedCaches);
        form.appendChild(hiddenField);

        document.body.appendChild(form);
        form.submit();
    }
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

function layerRelationValidation(method){
    var proxySources = $(method === 'INSERT' ? '#proxySources' : '#proxySources_u').val();
    var proxyCaches = $(method === 'INSERT' ? '#proxyCaches' : '#proxyCaches_u').val();
    if(proxySources.length > 0) {
        if(proxySources.filter(o => proxyCaches.includes(o)).length > 0){
            toastr.warning('리소스 이름과 캐시 이름이 같은 값이 있습니다. 각각 다른 이름으로 저장하시길 바랍니다.');
            return false;
        } else return true;
    } else {
        toastr.warning('레이어 연계 소스의 값을 최소한 1개 선택 하시길 바랍니다.');
        return false;
    }
}

function requestValidation(type){
    var requestMap = $('input[name="requestMap"]').val();
    var requestLayers = $('input[name="requestLayers"]').val();

    if(requestMap === '[none]' && type === 'INSERT'){
        toastr.warning('Map 파일 주소를 설정하세요.');
        return false;
    } else if(requestLayers === '[none]' && type === 'INSERT'){
        toastr.warning('요청 Layer 를 설정하세요.');
        return false;
    } else return true;
}

function cacheRelationValidation(){
    var values = $('#proxySourcesWithCaches').val();
    if(values.length > 0) return true;
    else {
        toastr.warning('캐시 연계 소스의 값을 최소한 1개 선택 하시길 바랍니다.');
        return false;
    }
}

function preSubmit(form){
    var action = form.action;
    if(action.indexOf('add') > -1){
        switch(form.id){
            case 'add-proxy-layer' :
                return nameValidation(form, 'proxyLayerName') && layerRelationValidation('INSERT');
            case 'add-proxy-source' :
                return nameValidation(form, 'proxySourceName') && requestValidation('INSERT');
            case 'add-proxy-cache' :
                return nameValidation(form, 'proxyCacheName') && cacheRelationValidation();
        }
    }
    return false;
}

function onclick_close(field, context){
    $(`#${field}`).val('');
    $(`#${field}`).data("check-duplicate",false);
    $(`#duplicate-check-message-proxy-${context}`).text("중복확인을 해주세요.");
    if(context === 'cache'){
        var cacheDirectory = document.getElementById("proxyCacheDirectory");
        cacheDirectory.value = cacheDirectoryPath;
    }
}

function onchange_mapFile_value(method){
    var mapFile = document.getElementById(method === 'INSERT' ? "mapFile" : "mapFile_u").value;
    var obj = JSON.parse(mapFile);
    if(obj.requestMap === '[none]'){
        alert('MAP 파일을 선택하세요.')
        $('input[name="requestMap"]').val('[none]');
    } else {
        $('input[name="requestMap"]').val(dataDirPath + obj.requestMap);
    }
}

function onchange_mapLayer_value(method) {
    var mapLayer = $(method === 'INSERT' ? '#mapLayer_multiple' : '#mapLayer_multiple_u').tagsinput('items');
    var selectData = document.getElementById(method === 'INSERT' ? "mapLayer" : "mapLayer_u").value;
    if(selectData !== '[none]' && !mapLayer.includes(selectData)) {
        $(method === 'INSERT' ? '#mapLayer_multiple' : '#mapLayer_multiple_u').tagsinput('add', selectData);
        $('input[name="requestLayers"]').val($(method === 'INSERT' ? '#mapLayer_multiple' : '#mapLayer_multiple_u').val());
    }
}

function onchange_mapLayer_multiple_value(method){
    $('input[name="requestLayers"]').val($(method === 'INSERT' ? '#mapLayer_multiple' : '#mapLayer_multiple_u').val());
}