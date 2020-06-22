// [JS 로직] 서비스 캐시 관리 - 설정 페이지

var selectedLayers = [];
var selectedCaches = [];
var selectedSources = [];

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

function submit_selected_data(){
    if(confirm('현재 선택한 목록들을 Map Proxy 설정에 저장합니다. 계속 진행 하시겠습니까?')){
        var form = document.createElement("form");
        form.setAttribute("charset", "UTF-8");
        form.setAttribute("method", "POST");
        form.setAttribute("action", CONTEXT + "/view/proxy/checking-save");

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

function onclick_remove_button(id, data){
    var arr = window[id];
    var idx = arr.indexOf(data);
    if (idx > -1) arr.splice(idx, 1);
    window[id] = arr.slice();
    rendering_selected_data(id, window[id]);
}

window.onload = function() {
    // 선택 데이터 초기화 (서버에서 받음)
    initialize_selected_data();

    // 입력 Form 초기화
    $('#layer-name').change(function() {
        $('#layer-name').data("check-duplicate",false);
        $('#duplicate-check-message-proxy-layer').text("중복확인을 해주세요.");
    });

    $('#source-name').change(function() {
        $('#source-name').data("check-duplicate",false);
        $('#duplicate-check-message-proxy-source').text("중복확인을 해주세요.");
    });

    $('#cache-name').change(function() {
        $('#cache-name').data("check-duplicate",false);
        $('#duplicate-check-message-proxy-cache').text("중복확인을 해주세요.");
    });

    // Source 데이터 중 모달 생성 시 MapServer 데이터 추출.
    $('#sourceModal').on('show.bs.modal', function (event) {
        var mapFile = document.getElementById('mapFile');
        var mapLayer = document.getElementById('mapLayer');

        if (mapFile.options.length === 1) {
            $.ajax({
                url: CONTEXT + '/server/api/map/list',
                success: function (data) {
                    for (var i = 0; i < data.length; i++) {
                        var option1 = document.createElement('option');
                        option1.value = JSON.stringify({requestMap: data[i].mapFilePath, mapId: data[i].id});
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

    // Source 데이터 중 모달 파기 시 원상 복귀.
    $('#sourceModal').on('hide.bs.modal', function (event) {
        $("select#mapFile option").remove();
        $("select#mapLayer option").remove();

        $('input[name="requestMap"]').val('[none]');

        var mapFile = document.getElementById('mapFile');
        var option = document.createElement('option');
        option.value = JSON.stringify( { requestMap : "[none]", mapId : -1 });
        option.text = '-- Map 파일 선택 --';
        mapFile.options.add(option);

        var mapLayer = document.getElementById('mapLayer');
        option = document.createElement('option');
        option.value = '[none]';
        option.text = '-- 레이어 선택 --';
        mapLayer.options.add(option);

        $('#mapLayer_multiple').tagsinput('removeAll');
        $('input[name="requestLayers"]').val('[none]');
    });

    // CACHE 이름이 바뀔 때 마다 경로 설정을 위한 이벤트
    document.getElementById('cache-name').onchange = function(e){
        var cacheDirectory = document.getElementById('cache-cacheDirectory');
        cacheDirectory.value = cacheDirectoryPath + e.target.value + '/';
    }

    // 각 데이터 별로 DataTables 초기화
    $('#list_table_source,#list_table_layer,#list_table_cache').each(function(){
        initialize_dataTable(this.id);
    });

    if(message){
        toastr.info(message);
    }
}

function preSubmit(form){
    var method = form['method'].value;
    var confirm = true;
    if(method === 'INSERT') {
        confirm = nameValidation(form, 'layer-name');
    }
    switch($(form).data('title')){
        case 'layer-form' :
            return confirm && layerRelationValidation();
        case 'source-form' :
            return confirm && requestValidation();
        case 'cache-form' :
            return confirm && cacheRelationValidation();
    }
    return false;
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

function layerRelationValidation(){
    var proxySources = $('#layer-sources').val();
    var proxyCaches = $('#layer-caches').val();
    if(proxySources.length > 0 || proxyCaches.length > 0) {
        if(proxySources.filter(o => proxyCaches.includes(o)).length > 0){
            toastr.warning('리소스 이름과 캐시 이름이 같은 값이 있습니다. 각각 다른 이름으로 저장하시길 바랍니다.');
            return false;
        } else return true;
    } else {
        toastr.warning('레이어 연계 소스 및 캐시의 값을 최소한 1개 선택 하시길 바랍니다.');
        return false;
    }
}

function requestValidation(){
    var requestMap = $('input[name="requestMap"]').val();
    var requestLayers = $('input[name="requestLayers"]').val();

    if(requestMap === '[none]'){
        toastr.warning('Map 파일 주소를 설정하세요.');
        return false;
    } else if(requestLayers === '[none]'){
        toastr.warning('요청 Layer 를 설정하세요.');
        return false;
    } else return true;
}

function cacheRelationValidation(){
    var values = $('#cache-sources').val();
    if(values.length > 0) return true;
    else {
        toastr.warning('캐시 연계 소스의 값을 최소한 1개 선택 하시길 바랍니다.');
        return false;
    }
}

// 생성 버튼 클릭
function onclick_insert_data(type) {
    document.getElementById(type + '-name').readOnly = false;
    document.getElementById(type + '-validate-btn').style.display = 'block';
    document.getElementById(type + '-validate-text').style.display = 'block';

    $('#' + type + 'Modal').modal('show');
    $('#' + type + '-id').val(0);
    $('#' + type + '-method').val('INSERT');

    if (type === 'source'){
        $('#source-type').val('mapserver');
        $('#source-requestMap').val('[none]');
        $('#source-requestLayers').val('[none]');
        $('#source-mapServerBinary').val(mapServerBinary);
        $('#source-mapServerWorkDir').val(dataDirPath + '/temp');
    }

    if(type === 'cache') {
        $('#cache-cacheType').val('file');
        $('#cache-cacheDirectory').val(cacheDirectoryPath);
        $('#directoryEdit').val(false);
        document.getElementById('cache-cacheDirectory').readOnly = true;
    }
}

// 수정 버튼 클릭
function onclick_update_data(btn){
    var data = $(btn).data();

    document.getElementById(data.obj + '-name').readOnly = true;
    document.getElementById(data.obj + '-validate-btn').style.display = 'none';
    document.getElementById(data.obj + '-validate-text').style.display = 'none';

    $('#' + data.obj + 'Modal').modal('show');
    for(var key in data) {
        if (key !== 'obj') {
            $('#' + data.obj + '-' + key).val(data[key]);
        }
        if (key === 'requestLayers'){
            $('#mapLayer_multiple').tagsinput('add', data[key]);
        }
    }

    if(data.obj === 'cache'){
        $('#directoryEdit').val(false);
        document.getElementById('cache-cacheDirectory').readOnly = true;
    }
}

// 모달 닫기 버튼 클릭
function onclick_close(context){
    $(`[id^='${context}']`).val('');
    $(`#${context}-name`).data("check-duplicate", false);
    $(`#duplicate-check-message-proxy-${context}`).text("중복확인을 해주세요.");
    if(context === 'cache'){
        var cacheDirectory = document.getElementById("cache-cacheDirectory");
        cacheDirectory.value = cacheDirectoryPath;
    }
}

function onchange_mapFile_value(){
    var mapFile = document.getElementById("mapFile").value;
    var obj = JSON.parse(mapFile);
    if(obj.requestMap === '[none]'){
        toastr.warning('MAP 파일을 선택하세요.');
        $('input[name="requestMap"]').val('[none]');
    } else {
        $('input[name="requestMap"]').val(dataDirPath + obj.requestMap);
    }
}

function onchange_mapLayer_value() {
    var mapLayer = $('#mapLayer_multiple').tagsinput('items');
    var selectData = document.getElementById("mapLayer").value;
    if(selectData !== '[none]' && !mapLayer.includes(selectData)) {
        $('#mapLayer_multiple').tagsinput('add', selectData);
        $('input[name="requestLayers"]').val($('#mapLayer_multiple').val());
    }
}

function onchange_mapLayer_multiple_value(){
    $('input[name="requestLayers"]').val($('#mapLayer_multiple').val());
}

function onchange_cache_directory_checkbox(){
    var checkbox = document.getElementById('directoryEdit');
    var directory = document.getElementById('cache-cacheDirectory');
    var name = document.getElementById('cache-name');
    directory.readOnly = !checkbox.checked;
    if(directory.readOnly){
        if(name.value.trim() !== '')
            directory.value = cacheDirectoryPath + name.value + '/';
    }
}