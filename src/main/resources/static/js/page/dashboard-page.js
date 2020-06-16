// [JS 로직] 홈 - 대시보드 페이지
$(function () {
    $('[data-toggle="tooltip"]').tooltip();
    find_by_center_refresh_server('B1');
    find_by_center_refresh_server('U3');
    find_by_center_refresh_server('GOC');
    find_by_center_refresh_sync('B1');
    find_by_center_refresh_sync('U3');
    find_by_center_refresh_sync('GOC');
});

// 센터 별 초기화를 위한 함수이다. - 서버 관련
function find_by_center_refresh_server(center){
    var dom = document.getElementById(`${center}_SERVER_STATUS`);
    var select = document.getElementById(center + "_change_server");
    if(dom && select){
        var options = select.options;
        if(options.length <= 1) return;
        $("#" + center + "_change_server").val(options[1].value);
        onchange_refresh_server(select);
    }
}

// 센터 별 초기화를 위한 함수이다. - 동기화 관련
function find_by_center_refresh_sync(center){
    var dom = document.getElementById(`${center}_SYNC_STATUS`);
    var select = document.getElementById(center + "_change_sync");
    if(dom && select){
        var options = select.options;
        if(options.length <= 1) return;
        $("#" + center + "_change_sync").val(options[1].value);
        onchange_refresh_sync(select);
    }
}

// 메시지 별 (LOADING, ERROR) 렌더링 : 서버 사양 정보
function msg_initialize_svr_info(zone, msg){
    document.getElementById(zone + '_usedCapacity').innerHTML = msg == 'LOADING' ? '<i class="fas fa-spin fa-spinner"></i>' : '<i class="fas fa-question"></i>';
    document.getElementById(zone + '_totalCapacity').innerHTML = msg == 'LOADING' ? '<i class="fas fa-spin fa-spinner"></i>' : '<i class="fas fa-question"></i>';
    document.getElementById(zone + '_usedMemory').innerHTML = msg == 'LOADING' ? '<i class="fas fa-spin fa-spinner"></i>' : '<i class="fas fa-question"></i>';
    document.getElementById(zone + '_totalMemory').innerHTML = msg == 'LOADING' ? '<i class="fas fa-spin fa-spinner"></i>' : '<i class="fas fa-question"></i>';
    document.getElementById(zone + '_availableMemory').innerHTML = msg == 'LOADING' ? '<i class="fas fa-spin fa-spinner"></i>' : '<i class="fas fa-question"></i>';
    document.getElementById(zone + '_usedCapacity').innerHTML = msg == 'LOADING' ? '<i class="fas fa-spin fa-spinner"></i>' : '<i class="fas fa-question"></i>';
    document.getElementById(zone + '_cpuUsage').innerHTML = msg == 'LOADING' ? '<i class="fas fa-spin fa-spinner"></i>' : '<i class="fas fa-question"></i>';
    document.getElementById(zone + '_connections').innerHTML = msg == 'LOADING' ? '<i class="fas fa-spin fa-spinner"></i>' : '<i class="fas fa-question"></i>';

    document.getElementById(zone + '_serverName').className = (msg === 'UNKNOWN') ? 'text-danger' : 'text-warning';
    document.getElementById(zone + '_serverName').innerText = msg == 'LOADING' ? 'LOADING...' : 'LOAD ERROR!';
    document.getElementById(zone + '_serverStatus').className = (msg === 'UNKNOWN') ? 'text-danger' : 'text-warning';
    document.getElementById(zone + '_serverStatus').innerHTML = msg == 'LOADING' ? '<i class="fas fa-spin fa-spinner"></i>' : '<i class="fas fa-times-circle"></i>';
}

// 메시지 별 (LOADING, ERROR) 렌더링 : 서비스 정보
function msg_initialize_geo_service(zone, msg){
    $.each($(`i[id^=${zone}_geo_service_icon_]`), function() {
        this.className = (msg === 'LOADING') ? 'fas fa-spin fa-spinner' : 'fas fa-question';
    });

    $.each($(`span[id^=${zone}_geo_service_text_]`), function() {
        this.innerText = msg;
    })
}

function msg_initialize_sync_info(zone, msg){
    $.each($(`i[id^=${zone}_sync_basic_]`), function() {
        this.className = (msg === 'LOADING') ? 'fas fa-spin fa-spinner' : 'fas fa-question';
    });

    document.getElementById(zone + '_syncName').className = (msg === 'UNKNOWN') ? 'text-danger' : 'text-warning';
    document.getElementById(zone + '_syncName').innerText = msg == 'LOADING' ? 'LOADING...' : 'LOAD ERROR!';
    document.getElementById(zone + '_syncStatus').className = (msg === 'UNKNOWN') ? 'text-danger' : 'text-warning';
    document.getElementById(zone + '_syncStatus').innerHTML = msg == 'LOADING' ? '<i class="fas fa-spin fa-spinner"></i>' : '<i class="fas fa-times-circle"></i>';
}

// 공통으로 부를 ajax - server part
function ajax_request_server(ip, port, zone){
    $.ajax({
        url: (window.location.protocol === 'https:') ? `https://${ip}${CONTEXT}/server/api/dashboard/performance` : `http://${ip}:${port}${CONTEXT}/server/api/dashboard/performance`,
        type: 'GET',
        contentType: 'application/json',
        success: function (connection) {
            if(connection){
                for(var k in connection){
                    if(!k.endsWith('status')) {
                        document.getElementById(zone + '_' + k).innerHTML = `<span>${connection[k]}</span>`;
                    } else {
                        switch(connection[k]){
                            case "ON" :
                                document.getElementById(zone + '_serverName').className = 'text-info';
                                document.getElementById(zone + '_serverStatus').className = 'text-info';
                                document.getElementById(zone + '_serverStatus').innerHTML = '<i class="fas fa-check-circle"></i>';
                                break;
                            case "OFF" :
                                document.getElementById(zone + '_serverName').className = 'text-danger';
                                document.getElementById(zone + '_serverStatus').className = 'text-danger';
                                document.getElementById(zone + '_serverStatus').innerHTML = '<i class="fas fa-check-times"></i>';
                                break;
                            case "ERROR" :
                                document.getElementById(zone + '_serverName').className = 'text-danger';
                                document.getElementById(zone + '_serverStatus').className = 'text-danger';
                                document.getElementById(zone + '_serverStatus').innerHTML = '<i class="fas fa-exclamation-triangle"></i>';
                                break;
                        }
                    }
                }
            }
        },
        error: function(e){
            msg_initialize_svr_info(zone, 'UNKNOWN');
        },
        beforeSend: function(){
            msg_initialize_svr_info(zone, 'LOADING');
        }
    });

    $.ajax({
        url: (window.location.protocol === 'https:') ? `https://${ip}${CONTEXT}/server/api/dashboard/service-status` : `http://${ip}:${port}${CONTEXT}/server/api/dashboard/service-status`,
        type: 'GET',
        contentType: 'application/json',
        success: function (status) {
            if(status){
                var visited = [];
                for(var container in status){
                    visited.push(container);
                    var dom_icon = document.getElementById(zone + '_geo_service_icon_' + container);
                    var dom_text = document.getElementById(zone + '_geo_service_text_' + container);
                    dom_text.innerText = status[container].status.toUpperCase();
                    switch(status[container].status){
                        case 'created':
                            dom_icon.className = 'fas fa-plus-square text-info';
                            break;
                        case 'running':
                            dom_icon.className = 'fas fa-check text-info';
                            break;
                        case 'restarting':
                            dom_icon.className = 'fas fa-sync text-warning';
                            break;
                        case 'paused':
                            dom_icon.className = 'fas fa-pause text-warning';
                            break;
                        case 'removing':
                        case 'exited':
                        case 'dead':
                            dom_icon.className = 'fas fa-times-circle text-danger';
                            break;
                        default :
                            dom_icon.className = 'fas fa-question';
                            dom_text.innerText = 'UNKNOWN';
                            break;
                    }
                }

                // 나머지는 UNKNOWN 처리.
                $.each($(`i[id^=${zone}_geo_service_icon_]`), function() {
                    var key = this.id.replace(`${zone}_geo_service_icon_`, '');
                    if(!visited.includes(key)) this.className = 'fas fa-question';
                });
                $.each($(`span[id^=${zone}_geo_service_text_]`), function() {
                    var key = this.id.replace(`${zone}_geo_service_text_`, '');
                    if(!visited.includes(key)) this.innerText = 'UNKNOWN';
                });
            }
        },
        error: function(e){
            msg_initialize_geo_service(zone, 'UNKNOWN');
        },
        beforeSend: function(){
            msg_initialize_geo_service(zone, 'LOADING');
        }
    });
}

// 공통으로 부를 ajax - sync part
function ajax_request_sync(ip, port, zone){
    $.ajax({
        url: (window.location.protocol === 'https:') ?
            `https://${ip}${CONTEXT}/server/api/dashboard/sync-basic-status?remoteIp=${ip}&remoteBasicDBPort=${pgSQLBasicPort}&remoteFilePort=${syncthingPort}` :
            `http://${ip}:${port}${CONTEXT}/server/api/dashboard/sync-basic-status?remoteIp=${ip}&remoteBasicDBPort=${pgSQLBasicPort}&remoteFilePort=${syncthingPort}`,
        type: 'GET',
        contentType: 'application/json',
        success: function (status) {
            if(status){
                for(var k in status){
                        if(!k.endsWith('serverName')) {
                            document.getElementById(zone + '_sync_basic_' + k).className = (status[k] === 'RUNNING') ? 'fas fa-check text-info' : (status[k] === 'DEAD') ? 'fas fa-times-circle text-danger' : 'fas fa-question';
                        } else {
                            document.getElementById(zone + '_syncStatus').className = 'text-info';
                            document.getElementById(zone + '_syncStatus').className = 'text-info';
                            document.getElementById(zone + '_syncStatus').innerHTML = '<i class="fas fa-check-circle"></i>';

                            document.getElementById(zone + '_syncName').className = 'text-info';
                            document.getElementById(zone + '_syncName').innerText = status[k];
                        }
                    }
                }
            },
        error: function(e){
            msg_initialize_sync_info(zone, 'UNKNOWN');
        },
        beforeSend: function(){
            msg_initialize_sync_info(zone, 'LOADING');
        }
    });
}

// 버튼과 셀렉트 폼과 같이 사용.
function onchange_refresh_server(dom){
    var val = dom.value;

    var center;
    // 버튼과 셀렉트 둘 다 id 는 센터로 시작한다.
    if(dom.id.startsWith('B1')){
        center = 'B1';
    } else if(dom.id.startsWith('U3')){
        center = 'U3';
    } else {
        center = 'GOC';
    }

    // 버튼일 때를 대비한다.
    if(!val){
        val = document.getElementById(center + "_change_server").value;
    }

    if(val !== '') {
        var split = val.split('|');
        var ip = split[2];

        $('#' + center + '_SERVER_PILL1').tab('show');

        ajax_request_server(ip, 11110, center);
    }
}

// 버튼과 셀렉트 폼과 같이 사용.
function onchange_refresh_sync(dom){
    var val = dom.value;

    var center;
    // 버튼과 셀렉트 둘 다 id 는 센터로 시작한다.
    if(dom.id.startsWith('B1')){
        center = 'B1';
    } else if(dom.id.startsWith('U3')){
        center = 'U3';
    } else {
        center = 'GOC';
    }

    // 버튼일 때를 대비한다.
    if(!val){
        val = document.getElementById(center + "_change_sync").value;
    }

    if(val !== '') {
        var split = val.split('|');
        var ip = split[2];

        $('#' + center + '_SYNC_PILL1').tab('show');

        ajax_request_sync(ip, 11110, center);
    }
}

// 여러 번 쓰는 함수가 점차 있는데, 이는 차주부터 시간 날 때 정리해 나가겠습니다...