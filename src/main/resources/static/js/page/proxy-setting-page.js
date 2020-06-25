// [JS 로직] 서비스 캐시 관리 - 설정 페이지

var selectedLayers = [];
var selectedCaches = [];
var selectedSources = [];

// Map Proxy YAML 파일에 등록된 데이터들을 렌더링한다.
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

// Map Proxy YAML 파일에 등록할 데이터들을 저장한다.
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

// Map Proxy YAML 파일에 저장된 내용들을 불러온다.
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

// Map Proxy YAML 파일에 데이터를 추가한다.
function onclick_add_button(id, data){
    var arr = window[id];
    if(arr.includes(data)){
        toastr.warning('이미 설정 목록에 추가 되어 있습니다.');
    } else {
        arr.push(data);
        window[id] = arr.slice();
        rendering_selected_data(id, window[id]);
    }
}

// MAp Proxy YAML 파일에 데이터를 삭제한다.
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
        var options;
        $.ajax({
            url: CONTEXT + '/server/api/map/list',
            async: false,
            success: function (data) {
                options = data;
            },
            error: function (e) {
                console.log(e);
            }
        });

        for(var type of ['mapserver', 'wms']) {
            var mapFile = document.getElementById('mapFile-' + type);
            if (options) {
                for (var i = 0; i < options.length; i++) {
                    var option = document.createElement('option');
                    option.value = JSON.stringify({requestMap: options[i].mapFilePath, mapName: options[i].name});
                    option.text = options[i].name + `(${options[i].mapFilePath})`;

                    mapFile.options.add(option);
                }
            }
        }
    });

    // Source 데이터 중 모달 파기 시 원상 복귀.
    $('#sourceModal').on('hide.bs.modal', function (event) {
        $('input[name="requestMap"]').val('[none]');

        for(var type of ['mapserver', 'wms']) {
            $(`select#mapFile-${type} option`).remove();
            var mapFile = document.getElementById('mapFile-' + type);
            var option = document.createElement('option');
            option.value = JSON.stringify({requestMap: "[none]", mapId: -1});
            option.text = '-- Map 파일 선택 --';
            mapFile.options.add(option);
        }

        $('#source-requestLayers-mapserver').val('[none]');
        // wms 에서는 바뀔 수 있으니 우선은 내비둔다.

        onclick_close('source');
    });

    $('#layerModal').on('hide.bs.modal', function (event) {
        onclick_close('layer');
    });

    $('#cacheModal').on('hide.bs.modal', function (event) {
        onclick_close('cache');
    });

    // CACHE 이름이 바뀔 때 마다 경로 설정을 위한 이벤트
    var dom = document.getElementById('cache-name');
    if(dom) {
        dom.onchange = function (e) {
            var cacheDirectory = document.getElementById('cache-cacheDirectory');
            cacheDirectory.value = cacheDirectoryPath + e.target.value + '/';
        }
    }

    // 각 데이터 별로 DataTables 초기화
    $('#list_table_layer,#list_table_source,#list_table_cache').each(function(){
        initialize_dataTable(this.id);
    });

    // DataTables 초기화 (2)
    $('#cache_sources,#layer_sources,#layer_caches').each(function(){
        initialize_small_dataTable(this.id);
    });

    // SESSION 에 데이터가 있으면, toastr 로 메시지를 띄운다.
    if(message){
        toastr.info(message);
    }

    onchange_source_type('#source-type');
}

// 입력 form Validation
function preSubmit(form){
    var method = form['method'].value;
    var confirm = true;
    if(method === 'INSERT') {
        switch($(form).data('title')){
            case 'layer-form' :
                confirm = nameValidation(form, 'layer-name');
                break;
            case 'source-form' :
                confirm = nameValidation(form, 'source-name');
                break;
            case 'cache-form' :
                confirm = nameValidation(form, 'cache-name');
                break;
            default :
                confirm = false;
        }
    }

    if(confirm) {
        switch ($(form).data('title')) {
            case 'layer-form' :
                if(layerValidation()) {
                    form['sources'].value = JSON.parse(form['sources'].value);
                    form['caches'].value = JSON.parse(form['caches'].value);
                    return true;
                } else return false;
            case 'source-form' :
                if(form.action.includes('mapserver')) {
                    return sourceMapServerValidation();
                }
                if(form.action.includes('wms')) {
                    return sourceMapWMSValidation();
                }
                return false;
            case 'cache-form' :
                if(cacheValidation()){
                    form['sources'].value = JSON.parse(form['sources'].value);
                    return true;
                } else return false;
            default :
                return false;
        }
    } else return false;
}

// 공통으로 진행될 이름 확인
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

function layerValidation(){
    var proxySources = JSON.parse($('#layer-sources').val());
    var proxyCaches = JSON.parse($('#layer-caches').val());
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

// Map Server 기반 Source Validation 체크
function sourceMapServerValidation(){
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

// WMS 기반 Source Validation 체크
function sourceMapWMSValidation(){
    var values = $('#source-requestUrl').val();
    if(values){
        var expUrl = /(http(s)?:\/\/)([a-z0-9\w]+\.*)+[a-z0-9]{2,4}/gi;
        if(expUrl.test(values)) {
            if (values.endsWith("?")) return true;
            else {
                toastr.warning('URL 뒤에는 반드시 ? 를 입력하시길 바랍니다.');
                return false;
            }
        } else {
            toastr.warning('URL 형식을 확인하시길 바랍니다.');
            return false;
        }
    } else {
        toastr.warning('URL 를 반드시 입력하세요.');
        return false;
    }
}

// Cache 데이터 Validation 체크
function cacheValidation(){
    var values = JSON.stringify($('#cache-sources').val());
    if(values.length > 0) return true;
    else {
        toastr.warning('캐시 연계 소스의 값을 최소한 1개 선택 하시길 바랍니다.');
        return false;
    }
}

// 생성 버튼 클릭
function onclick_insert_data(type) {
    // 아래 입력 요소가 없다는 것은, 즉 등록된 SOURCE 가 없다는 뜻.
    var nameDOM = document.getElementById(type + '-name');

    if(nameDOM) {
        nameDOM.readOnly = false;
        document.getElementById(type + '-validate-btn').style.display = 'block';
        document.getElementById(type + '-validate-text').style.display = 'block';

        $('#' + type + 'Modal').modal('show');
        $('#' + type + '-id').val(0);
        $('#' + type + '-method').val('INSERT');

        if (type === 'layer') {
            $('#layer-sources').val('[]');
            $('#layer-caches').val('[]');
        }

        if (type === 'source') {
            $('#source-type').val('mapserver');
            $('#source-requestMap').val('[none]');
            $('#source-requestLayers').val('[none]');
            $('#source-mapServerBinary').val(mapServerBinary);
            $('#source-mapServerWorkDir').val(dataDirPath + '/temp');
        }

        if (type === 'cache') {
            $('#cache-cacheType').val('file');
            $('#cache-cacheDirectory').val(cacheDirectoryPath);
            $('#directoryEdit').val(false);
            $('#cache-sources').val('[]');
            document.getElementById('cache-cacheDirectory').readOnly = true;
        }
    } else {
        toastr.warning('SOURCE 를 YAML 파일에 등록하시길 바랍니다.');
    }
}

// 수정 버튼 클릭
function onclick_update_data(btn){
    var data = $(btn).data();

    // 아래 입력 요소가 없다는 것은, 즉 등록된 SOURCE 가 없다는 뜻.
    var nameDOM = document.getElementById(data.obj + '-name');
    if(nameDOM) {
        nameDOM.readOnly = true;
        document.getElementById(data.obj + '-validate-btn').style.display = 'none';
        document.getElementById(data.obj + '-validate-text').style.display = 'none';

        $('#' + data.obj + 'Modal').modal('show');
        for (var key in data) {
            if (key !== 'obj') {
                $('#' + data.obj + '-' + key).val(data[key]);
            }
            if (key === 'requestLayers') {
                $('#' + data.obj + '-' + key + '-' + data['type']).val(data[key]);
            }
        }

        if (data.obj === 'layer') {
            var sources = data['sources'];
            var caches = data['caches'];

            document.getElementById('layer-sources').value = JSON.stringify(sources);
            document.getElementById('layer-caches').value = JSON.stringify(caches);

            sources.forEach(o => document.getElementById('layerSources_' + o).checked = true);
            caches.forEach(o => document.getElementById('layerCaches_' + o).checked = true);
        }

        if (data.obj === 'cache') {
            $('#directoryEdit').val(false);
            document.getElementById('cache-cacheDirectory').readOnly = true;

            var sources = data['sources'];
            document.getElementById('cache-sources').value = JSON.stringify(sources);
            sources.forEach(o => document.getElementById('cacheSources_' + o).checked = true);
        }

        if (data.obj === 'source') {
            onchange_source_type('#source-type');

            if(data['type'] === 'wms'){
                var hidden = document.getElementById('source-requestTransparent');
                var checkbox = document.getElementById('source-check');
                hidden.value = data['requestTransparent'];
                checkbox.checked = data['requestTransparent'];

                var split = data['supportedSrs'].split(',');
                document.getElementById('source-supportedSrs').value = split.join();
                for(var i = 0; i < split.length; i++){
                    switch(split[i]){
                        case 'EPSG:4326' :
                            document.getElementById('source-grid1').checked = true;
                            break;
                        case 'EPSG:3857' :
                            document.getElementById('source-grid2').checked = true;
                            break;
                        case 'EPSG:900913' :
                            document.getElementById('source-grid3').checked = true;
                            break;
                    }
                }
            }
        }
    } else {
        toastr.warning('SOURCE 를 YAML 파일에 등록하시길 바랍니다.');
    }
}

// Modal 닫기 버튼 클릭
function onclick_close(context){
    $(`[id^='${context}']`).val('');
    $(`#${context}-name`).data("check-duplicate", false);
    $(`#duplicate-check-message-proxy-${context}`).text("중복확인을 해주세요.");

    if(context === 'layer'){
        for(var dom of $('[id^=layerSources_]')) {
            dom.checked = false;
        }
        for(var dom of $('[id^=layerCaches_]')) {
            dom.checked = false;
        }
    }

    if(context === 'source') {
        $('#source-type').val('mapserver');
        $('#source-requestTransparent').val(false);
        $('#source-check').prop("checked", false);
        for(var i = 1; i <= 3; i++){
            document.getElementById('source-grid' + i).checked = false;
        }
        document.getElementById('source-supportedSrs').value = '';
        onchange_source_type('#source-type');
    }

    if(context === 'cache'){
        var cacheDirectory = document.getElementById("cache-cacheDirectory");
        cacheDirectory.value = cacheDirectoryPath;
        for(var dom of $('[id^=cacheSources_]')) {
            dom.checked = false;
        }
    }
}

// Map Server 파일 (abc.map) 선택 시 MAP 과 LAYER 데이터 채우기.
function onchange_mapFile_value(id){
    var mapFile = document.getElementById(id).value;
    var obj = JSON.parse(mapFile);
    if(obj.requestMap === '[none]'){
        toastr.warning('MAP 파일을 선택하세요.');
        $('input[name="requestMap"]').val('[none]');
        $('input[name="requestLayers"]').val('[none]');
    } else {
        $('input[name="requestMap"]').val(dataDirPath + obj.requestMap);
        $('input[name="requestLayers"]').val(obj.mapName);
    }
}

// Cache 디렉토리 임의 변경 기능
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

// Source 타입이 변경될 때 실행되는 메소드
function onchange_source_type(dom){
    var val = $(dom).val();
    if(val === 'wms'){
        $('.mapserver').prop('disabled', true);
        $('.wms').prop('disabled', false);
        $('.wms').show();
        $('.mapserver').hide();
        document.getElementById('source-form').action = CONTEXT + '/view/proxy/source-wms-save';
    } else {
        $('.wms').prop('disabled', true);
        $('.mapserver').prop('disabled', false);
        $('.mapserver').show();
        $('.wms').hide();
        document.getElementById('source-form').action = CONTEXT + '/view/proxy/source-mapserver-save';
    }
}

// 체크박스 체크
function onchange_checkbox_checked(dom){
    var hidden = document.getElementById('source-requestTransparent');
    hidden.value = dom.checked;
}

// GRID 체크박스 체크
function onchange_checkbox_grid(){
    var arr = [];
    for(var i = 1; i <= 3; i++){
        var checkbox = document.getElementById('source-grid' + i);
        if(checkbox.checked){
            arr.push(checkbox.value);
        }
    }

    document.getElementById('source-supportedSrs').value = arr.join();
}

// 소스 목록 체크
function onchange_checkbox_select_data(type, dom, fieldId){
    var id = dom.id;
    var name = id.replace(type + '_', '');

    var field = document.getElementById(fieldId);
    var arr = JSON.parse(field.value);
    if(!dom.checked) {
        arr.splice(arr.indexOf(name), 1);
    } else {
        arr.push(name);
    }
    field.value = JSON.stringify(arr);
}