// [JS 로직] 서비스 캐시 관리 - 설정 페이지

var selectedLayers = [];
var selectedCaches = [];
var selectedSources = [];

var globalTable;

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
                    option.text = options[i].name;

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
    $('#list_table_layer,#list_table_source,#list_table_cache,#list_table_global').each(function(){
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

    globalTable = $('#list_table_global').DataTable({
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
        },
        pageLength: 5,
        destroy: true,
        "lengthMenu": [[5, -1], [5, "모두"]]
    });

    var savedTab = sessionStorage.getItem("tab");
    if (['layer', 'source', 'cache'].includes(savedTab)) {
        $(`#${savedTab}-tab`).click();
    }
    sessionStorage.removeItem("tab");
}

// 같은 ZONE, TYPE 에 있는 서버에게 동기화를 요청하는 REST API
function neighbor_synchronize_ajax(url, type, data, successMsg, failureMsg){
    for(var server of neighbors) {
        $.ajax({
            url: `${(window.location.protocol === 'https:') ? 'https://' : 'http://'}${server.ip}${(window.location.protocol === 'https:') ? '' : ':11110'}${CONTEXT}${url}`,
            type: type,
            data: JSON.stringify(data),
            contentType: "application/json",
            async: false,
            success: function (d) {
                var res = d && d.result;
                if (res) {
                    toastr.info('[' + server.name + '] 측 자료 동기화 : ' + successMsg);
                } else {
                    toastr.info('[' + server.name + '] 측 자료 동기화 : ' + successMsg); // 예비 처리. 동기화는 전부 정상적으로 진행되고 있음.
                }
            },
            error: function (e) {
                toastr.error('[' + server.name + '] PROXY 데이터 동기화 도중 오류가 발생 했습니다. - ' + e);
            }
        });
    }
}


// Map Proxy YAML 파일에 등록된 데이터들을 렌더링한다.
function rendering_selected_data(id, data){
    var dom = '';

    if(data.length > 0) {
        for (var i = 0; i < data.length; i++) {
            switch(id){
                case 'selectedLayers':
                    dom += `
                        <li class="list-group-item d-flex justify-content-between align-items-center">
                            ${data[i].layer}
                            ${
                                (data[i].layer === 'world_layer') ? '' : 
                                    `<span class="badge badge-warning badge-pill" style="cursor: pointer;" id="remove_yaml_layer_${data[i].layer}" data-id="${id}" data-layer="${data[i].layer}" onclick="onclick_remove_button(this)">
                                        <i class="fas fa-trash"></i>
                                    </span>`
                            }
                        </li>
                    `;
                    break;
                case 'selectedSources':
                    dom += `
                        <li class="list-group-item d-flex justify-content-between align-items-center">
                            ${data[i]}
                            ${
                                (data[i] === 'world') ? '' : 
                                    `<span class="badge badge-warning badge-pill" style="cursor: pointer;" data-id="${id}" data-source="${data[i]}" onclick="onclick_remove_button(this)">
                                        <i class="fas fa-trash"></i>
                                    </span>`
                            }
                        </li>
                    `;
                    break;
                case 'selectedCaches':
                    dom += `
                        <li class="list-group-item d-flex justify-content-between align-items-center">
                            ${data[i].cache}
                            ${ 
                                (data[i].cache === 'world_cache') ? '' : 
                                    `<span class="badge badge-warning badge-pill" style="cursor: pointer;" id="remove_yaml_cache_${data[i].cache}" data-id="${id}" data-cache="${data[i].cache}" onclick="onclick_remove_button(this)">
                                        <i class="fas fa-trash"></i>
                                    </span>`
                            }    
                        </li>
                    `;
                    break;
            }
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
    if(selectedSources.length == 0){
        toastr.warning('최소한 1개 이상의 SOURCE 를 선택하세요.');
        return false;
    }

    if(selectedLayers.length == 0){
        toastr.warning('최소한 1개 이상의 LAYER 를 선택하세요.');
        return false;
    }

    if(selectedCaches.length == 0){
        toastr.warning('최소한 1개 이상의 CACHE 를 선택하세요.');
        return false;
    }

    if(confirm('현재 선택한 목록들을 Map Proxy 설정에 저장합니다. 계속 진행 하시겠습니까?')){
        var form = document.createElement("form");
        form.setAttribute("charset", "UTF-8");
        form.setAttribute("method", "POST");
        form.setAttribute("action", CONTEXT + "/view/proxy/checking-save");

        var layers = selectedLayers.map(o => o.layer);
        var sources = selectedSources;
        var caches = selectedCaches.map(o => o.cache);

        var hiddenField = document.createElement("input");
        hiddenField.setAttribute("type", "hidden");
        hiddenField.setAttribute("name", "layers");
        hiddenField.setAttribute("value", layers);
        form.appendChild(hiddenField);

        hiddenField = document.createElement("input");
        hiddenField.setAttribute("type", "hidden");
        hiddenField.setAttribute("name", "sources");
        hiddenField.setAttribute("value", sources);
        form.appendChild(hiddenField);

        hiddenField = document.createElement("input");
        hiddenField.setAttribute("type", "hidden");
        hiddenField.setAttribute("name", "caches");
        hiddenField.setAttribute("value", caches);
        form.appendChild(hiddenField);

        neighbor_synchronize_ajax('/server/api/proxy/sync/checking-save', 'POST', {
                layers: layers,
                sources: sources,
                caches: caches,
            },
            `선택된 설정에 대한 동기화를 진행 했습니다.`,
            `선택된 설정에 대한 동기화를 진행하지 못 했습니다.`,
        );

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
function onclick_add_button(btn){
    var data = $(btn).data();
    var arr = window[data.type];
    switch(data.type){
        case 'selectedLayers' :
            var sources = data.sources;
            var caches = data.caches;
            var layer = data.layer;
            if(arr.filter(o => o.layer === layer).length === 0){
                arr.push({
                    layer: layer,
                    sources: sources,
                    caches: caches
                });
                window[data.type] = arr.slice();

                var sourceText = '';
                for(var source of sources){
                    var tmpArr = window['selectedSources'];
                    if(!tmpArr.includes(source)){
                        tmpArr.push(source);
                    }
                    window['selectedSources'] = tmpArr.slice();
                    sourceText += source + " ";
                }
                if(sourceText !== ''){
                    toastr.info("LAYER 데이터인 " + data.layer + "와 관련된 SOURCE 데이터 " + sourceText + " (들)도 YAML 파일 설정에서 추가 되었습니다.");
                }

                var cacheText = '';
                for(var cache of caches){
                    var tmpArr = window['selectedCaches'];
                    if(tmpArr.filter(o => o.cache === cache).length === 0){
                        document.getElementById('add_button_' + cache).click();
                    }
                    cacheText += cache + ' ';
                }
                if(cacheText !== ''){
                    toastr.info("LAYER 데이터인 " + data.layer + "와 관련된 CACHE 데이터 " + cacheText + " (들)도 YAML 파일 설정에서 추가 되었습니다.");
                }

                rendering_selected_data('selectedLayers', window['selectedLayers']);
                rendering_selected_data('selectedSources', window['selectedSources']);
            } else {
                toastr.warning('이미 설정 목록에 추가 되어 있습니다.');
            }
            break;

        case 'selectedSources' :
            if(!arr.includes(data.source)){
                arr.push(data.source);
                window[data.type] = arr.slice();
                rendering_selected_data('selectedSources', window['selectedSources']);
            } else {
                toastr.warning('이미 설정 목록에 추가 되어 있습니다.');
            }
            break;

        case 'selectedCaches' :
            var sources = data.sources;
            var cache = data.cache;
            if(arr.filter(o => o.cache === cache).length === 0){
                arr.push({
                    cache : cache,
                    sources : sources
                });
                window[data.type] = arr.slice();

                var sourceText = '';
                for(var source of sources){
                    var tmpArr = window['selectedSources'];
                    if(!tmpArr.includes(source)){
                        tmpArr.push(source);
                    }
                    window['selectedSources'] = tmpArr.slice();
                    sourceText += source + " ";
                }
                if(sourceText !== ''){
                    toastr.info("CACHE 데이터인 " + data.cache + "와 관련된 SOURCE 데이터 " + sourceText + " (들)도 YAML 파일 설정에서 추가 되었습니다.");
                }

                rendering_selected_data('selectedSources', window['selectedSources']);
                rendering_selected_data('selectedCaches', window['selectedCaches']);
            } else {
                toastr.warning('이미 설정 목록에 추가 되어 있습니다.');
            }
            break;
    }
}

// MAp Proxy YAML 파일에 데이터를 삭제한다.
function onclick_remove_button(btn){
    var data = $(btn).data();
    var arr = window[data.id];
    switch(data.id){
        case 'selectedLayers' :
            var idx = arr.map(o => o.layer).indexOf(data.layer);
            if (idx > -1) arr.splice(idx, 1);
            window[data.id] = arr.slice();
            rendering_selected_data(data.id, window[data.id]);
            break;

        case 'selectedSources' :
            var idx = arr.indexOf(data.source);
            if (idx > -1) arr.splice(idx, 1);
            window[data.id] = arr.slice();

            rendering_selected_data(data.id, window[data.id]);

            var layerText = '';
            for(var layerData of window['selectedLayers']){
                if(layerData.sources.includes(data.source)){
                    document.getElementById('remove_yaml_layer_' + layerData.layer).click();
                    layerText += layerData.layer + ' '
                }
            }
            if(layerText !== '') {
                toastr.info("SOURCE 데이터인 " + data.source + "와 관련된 LAYER 데이터 " + layerText + " (들)도 YAML 파일 설정에서 해제 되었습니다.");
            }

            var cacheText = '';
            for(var cacheData of window['selectedCaches']){
                if(cacheData.sources.includes(data.source)){
                    document.getElementById('remove_yaml_cache_' + cacheData.cache).click();
                    cacheText += cacheData.cache + ' '
                }
            }
            if(cacheText !== '') {
                toastr.info("SOURCE 데이터인 " + data.source + "와 관련된 CACHE 데이터 " + cacheText + " (들)도 YAML 파일 설정에서 해제 되었습니다.");
            }
            break;

        case 'selectedCaches' :
            var idx = arr.map(o => o.cache).indexOf(data.cache);
            if (idx > -1) arr.splice(idx, 1);
            window[data.id] = arr.slice();

            rendering_selected_data(data.id, window[data.id]);

            var layerText = '';
            for(var layerData of window['selectedLayers']){
                if(layerData.caches.includes(data.cache)){
                    document.getElementById('remove_yaml_layer_' + layerData.layer).click();
                    layerText += layerData.layer + ' '
                }
            }
            if(layerText !== '') {
                toastr.info("CACHE 데이터인 " + data.cache + "와 관련된 LAYER 데이터 " + layerText + " (들)도 YAML 파일 설정에서 해제 되었습니다.");
            }
            break;
    }
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
                    neighbor_synchronize_ajax('/server/api/proxy/sync/layer-save', 'POST', {
                            id: $('#layer-id').val(),
                            name: $('#layer-name').val(),
                            method: $('#layer-method').val(),
                            title: $('#layer-title').val(),
                            sources: JSON.parse(form['sources'].value),
                            caches: JSON.parse(form['caches'].value)
                        },
                        `LAYER ${$('#layer-name').val()} 자료의 동기화를 진행 했습니다.`,
                         `LAYER ${$('#layer-name').val()} 자료의 동기화를 진행하지 못 했습니다.`,
                    );
                    form['sources'].value = JSON.parse(form['sources'].value);
                    form['caches'].value = JSON.parse(form['caches'].value);
                    sessionStorage.setItem('tab', 'layer');
                    return true;
                } else {
                    return false;
                }

            case 'source-form' :
                if(form.action.includes('wms')) {
                    neighbor_synchronize_ajax('/server/api/proxy/sync/source-wms-save', 'POST', {
                            id: $('#source-id').val(),
                            name: $('#source-name').val(),
                            type: $('#source-type').val(),
                            method: $('#source-method').val(),
                            concurrentRequests: $('#source-concurrentRequests').val(),
                            wmsOptsVersion: $('#source-wmsOptsVersion').val(),
                            httpClientTimeout: $('#source-httpClientTimeout').val(),
                            requestUrl: '',
                            requestMap: $('#source-requestMap-wms').val(),
                            requestLayers: $('#source-requestLayers-wms').val(),
                            requestTransparent: $('#source-requestTransparent').val(),
                            supportedSrs: $('#source-supportedSrs').val(),
                        },
                        `SOURCE ${$('#source-name').val()} 자료의 동기화를 진행 했습니다.`,
                        `SOURCE ${$('#source-name').val()} 자료의 동기화를 진행하지 못 했습니다.`,
                    );
                    sessionStorage.setItem('tab', 'source');
                    return true;
                }

                if(form.action.includes('mapserver')) {
                    if(sourceMapServerValidation()){
                        neighbor_synchronize_ajax('/server/api/proxy/sync/source-mapserver-save', 'POST', {
                                id: $('#source-id').val(),
                                name: $('#source-name').val(),
                                type: $('#source-type').val(),
                                method: $('#source-method').val(),
                                mapServerBinary: $('#source-mapServerBinary').val(),
                                mapServerWorkDir: $('#source-mapServerWorkDir').val(),
                                requestMap: $('#source-requestMap-mapserver').val(),
                                requestLayers: $('#source-requestLayers-mapserver').val(),
                            },
                            `SOURCE ${$('#source-name').val()} 자료의 동기화를 진행 했습니다.`,
                            `SOURCE ${$('#source-name').val()} 자료의 동기화를 진행하지 못 했습니다.`,
                        );
                        sessionStorage.setItem('tab', 'source');
                        return true;
                    } else {
                        return false;
                    }
                }

                return false;

            case 'cache-form' :
                if(cacheValidation()){
                    neighbor_synchronize_ajax('/server/api/proxy/sync/cache-save', 'POST', {
                            id: $('#cache-id').val(),
                            name: $('#cache-name').val(),
                            method: $('#cache-method').val(),
                            cacheType: '',
                            cacheDirectory: '',
                            metaSizeX: $('#cache-metaSizeX').val(),
                            metaSizeY: $('#cache-metaSizeY').val(),
                            metaBuffer: $('#cache-metaBuffer').val(),
                            format: $('#cache-format').val(),
                            grids: $('#cache-grids').val(),
                            sources: JSON.parse(form['sources'].value),
                        },
                        `CACHE ${$('#cache-name').val()} 자료의 동기화를 진행 했습니다.`,
                        `CACHE ${$('#cache-name').val()} 자료의 동기화를 진행하지 못 했습니다.`,
                    );
                    form['sources'].value = JSON.parse(form['sources'].value);
                    sessionStorage.setItem('tab', 'cache');
                    return true;
                } else {
                    return false;
                }

            default :
                return false;
        }
    } else {
        return false;
    }
}

// 공통으로 진행될 이름 확인
function nameValidation(form, field){
    if (jQuery.isEmptyObject(form[field].value)) {
        form[field].focus();
        toastr.warning("이름이 입력되지 않았습니다.");
        return false;
    } else if (jQuery(form[field]).data("check-duplicate") === false) {
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

// Cache 데이터 Validation 체크
function cacheValidation(){
    var values = JSON.stringify($('#cache-sources').val());
    if (values.length > 0) {
        return true;
    } else {
        toastr.warning('캐시 연계 소스의 값을 최소한 1개 선택 하시길 바랍니다.');
        return false;
    }
}

function onclick_delete_data(type, id, name){
    if(window.confirm(`${type.toUpperCase()} 데이터 [${name}] 자료 삭제를 진행 하시겠습니까?`)) {
        neighbor_synchronize_ajax(`/server/api/proxy/sync/${type}-delete`, 'POST', {
            name: name,
        },
`${type.toUpperCase()} 데이터 ${name} 자료의 삭제 동기화를 진행 했습니다.`,
`${type.toUpperCase()} 데이터 ${name} 자료의 삭제 동기화를 진행하지 못 했습니다.`,
        );
        window.location.href = `${CONTEXT}/view/proxy/${type}-delete?id=${id}&name=${name}`;
        sessionStorage.setItem('tab', type);
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
            $('#source-type').val('wms');
            $('#source-requestMap-mapserver').val('[none]');
            $('#source-requestLayers-mapserver').val('[none]');
            $('#source-requestMap-wms').val('[none]');
            $('#source-requestLayers-wms').val('[none]');
            for(var i = 1; i <= 3; i++){
                document.getElementById('src-grid' + i).checked = true;
            }
            onchange_checkbox_grid();
        }

        if (type === 'cache') {
            //$('#cache-cacheType').val('file');
            //$('#cache-cacheDirectory').val(cacheDirectoryPath);
            //$('#directoryEdit').val(false);
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
            if (key === 'requestLayers' || key === 'requestMap') {
                $('#' + data.obj + '-' + key + '-' + data['type']).val(data[key]);
            }
        }

        if (data.obj === 'layer') {
            var sources = data['sources'];
            var caches = data['caches'];

            document.getElementById('layer-sources').value = JSON.stringify(sources);
            document.getElementById('layer-caches').value = JSON.stringify(caches);

            sources.forEach(o => {
                var dom = document.getElementById('layerSources_' + o);
                if (dom) {
                    dom.checked = true;
                }
            });
            caches.forEach(o => {
                var dom = document.getElementById('layerCaches_' + o);
                if (dom) {
                    dom.checked = true;
                }
            });
        }

        if (data.obj === 'source') {
            onchange_source_type('#source-type');

            if(data['type'] === 'wms') {
                var hidden = document.getElementById('source-requestTransparent');
                var checkbox = document.getElementById('source-check');
                hidden.value = data['requestTransparent'];
                checkbox.checked = data['requestTransparent'];

                var split = data['supportedSrs'].split(',');
                document.getElementById('source-supportedSrs').value = split.join();
                for (var i = 1; i <= 3; i++){
                    var checkbox = document.getElementById('src-grid' + i);
                    if(split.includes(checkbox.value)){
                        checkbox.checked = true;
                    } else {
                        checkbox.checked = false;
                    }
                }
            }

            $('#mapFile' + '-' + data['type']).val(JSON.stringify({requestMap: data['requestMap'].replace(dataDirPath, ''), mapName: data['requestLayers']}));
        }

        if (data.obj === 'cache') {
            var sources = data['sources'];
            document.getElementById('cache-sources').value = JSON.stringify(sources);
            sources.forEach(o => {
                var dom = document.getElementById('cacheSources_' + o);
                if (dom) {
                    dom.checked = true;
                }
            });
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
        $('#source-type').val('wms');
        $('#source-requestTransparent').val(false);
        $('#source-check').prop("checked", false);
        for(var i = 1; i <= 3; i++){
            document.getElementById('src-grid' + i).checked = true;
        }
        onchange_checkbox_grid();

        document.getElementById('source-requestUrl').value = mapServerAddress;
        document.getElementById('source-supportedSrs').value = '';
        $('#source-httpClientTimeout').val(600);
        $('#source-concurrentRequests').val(4);
        $('#source-wmsOptsVersion').val('1.3.0');
        onchange_source_type('#source-type');
    }

    if(context === 'cache'){
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
        var checkbox = document.getElementById('src-grid' + i);
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

// GLOBAL 테이블 추가
function onclick_global_row_add(){
    if(globalTable){
        globalTable.row.add([
            '<input type="text" class="form-control" value="" />',
            '<input type="text" class="form-control" value="" />',
            `<button class="btn btn-danger btn-sm btn-circle" type="button" onclick="onclick_global_row_remove(this)">
                <i class="fas fa-trash"></i>
             </button>`
        ]).draw(false);
    }
}

// GLOBAL 테이블 삭제
function onclick_global_row_remove(btn){
    if(globalTable) {
        var tr = btn.parentNode.parentNode;
        if (tr && (tr.tagName === 'TR' || tr.tagName === 'tr')) {
            globalTable.row(btn.parentNode.parentNode).remove().draw(false);
        }
    }
}

// GLOBAL 테이블 제출
function onsubmit_global_table(form){
    if (globalTable) {
        var data = document.getElementById('global-row-list').children;

        var keys = [];
        var result = [];
        for (var tr of data) {
            var input = tr.querySelectorAll('input');
            if(input.length === 2) {
                var inputK = input[0];
                var inputV = input[1];
                if (keys.includes(inputK.value)) {
                    toastr.warning('GLOBAL KEY ' + inputK.value + ' 값이 중복 됩니다. KEY 값은 단일 값만 저장할 수 있습니다.');
                    return false;
                } else if (inputK.value === '' && inputK.value.trim() === ''){
                    toastr.warning('GLOBAL KEY 값은 빈 값으로 설정할 수 없습니다.');
                    return false;
                } else if (inputV.value === '' && inputV.value.trim() === ''){
                    toastr.warning('GLOBAL VALUE 값은 빈 값으로 설정할 수 없습니다.');
                    return false;
                } else {
                    result.push({
                        id: 0,
                        key: inputK.value,
                        value: inputV.value
                    });
                    keys.push(inputK.value);
                }
            }
        }

        var hidden = document.createElement('input');
        hidden.name = 'json';
        hidden.value = JSON.stringify(result);
        hidden.type = 'hidden';

        form.appendChild(hidden);

        neighbor_synchronize_ajax(`/server/api/proxy/sync/global-save`, 'POST', result,
            '모든 GLOBAL 데이터 자료의 동기화를 진행 했습니다.',
            '모든 GLOBAL 데이터 자료의 동기화를 진행하지 못 했습니다.',
        );

        return true;
    } else {
        return false;
    }
}

// GLOBAL 테이블 저장 취소
function onclick_global_cancel(){
    if(globalTable){
        globalTable.clear().draw();

        for(var global of globals){
            globalTable.row.add([
                `<input type="text" class="form-control" value="${global.key}" ${(global.key === 'cache.base_dir' || global.key === 'cache.lock_dir') ? 'readonly' : ''} />`,
                `<input type="text" class="form-control" value="${global.value}" />`,
                `<button class="btn btn-danger btn-sm btn-circle" type="button" onclick="onclick_global_row_remove(this)" ${(global.key === 'cache.base_dir' || global.key === 'cache.lock_dir') ? 'disabled' : ''}>
                <i class="fas fa-trash"></i>
             </button>`
            ]).draw(false);
        }
    }
}