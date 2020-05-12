$(function () {
    $('[data-toggle="tooltip"]').tooltip();
    find_by_center_refresh_server('B1');
    find_by_center_refresh_server('U3');
    find_by_center_refresh_server('GOC');
});

// 메시지 별 (LOADING, ERROR) 렌더링
function msg_initialize(center, msg){
    document.getElementById(center + '_usedCapacity').innerHTML = msg == 'LOADING' ? '<i class="fas fa-spin fa-spinner"></i>' : '<i class="fas fa-question"></i>';
    document.getElementById(center + '_totalCapacity').innerHTML = msg == 'LOADING' ? '<i class="fas fa-spin fa-spinner"></i>' : '<i class="fas fa-question"></i>';
    document.getElementById(center + '_usedMemory').innerHTML = msg == 'LOADING' ? '<i class="fas fa-spin fa-spinner"></i>' : '<i class="fas fa-question"></i>';
    document.getElementById(center + '_totalMemory').innerHTML = msg == 'LOADING' ? '<i class="fas fa-spin fa-spinner"></i>' : '<i class="fas fa-question"></i>';
    document.getElementById(center + '_availableMemory').innerHTML = msg == 'LOADING' ? '<i class="fas fa-spin fa-spinner"></i>' : '<i class="fas fa-question"></i>';
    document.getElementById(center + '_usedCapacity').innerHTML = msg == 'LOADING' ? '<i class="fas fa-spin fa-spinner"></i>' : '<i class="fas fa-question"></i>';
    document.getElementById(center + '_cpuUsage').innerHTML = msg == 'LOADING' ? '<i class="fas fa-spin fa-spinner"></i>' : '<i class="fas fa-question"></i>';
    document.getElementById(center + '_connections').innerHTML = msg == 'LOADING' ? '<i class="fas fa-spin fa-spinner"></i>' : '<i class="fas fa-question"></i>';

    document.getElementById(center + '_serverTitle').className = `text-sm font-weight-bold text-uppercase mb-1 ${(msg === 'ERROR') ? 'text-danger' : 'text-warning'}`;
    document.getElementById(center + '_serverName').innerText = msg == 'LOADING' ? 'LOADING...' : 'LOAD ERROR!';
    document.getElementById(center + '_serverStatus').className = `text-sm font-weight-bold text-uppercase mb-1 ${(msg === 'ERROR') ? 'text-danger' : 'text-warning'}`;
    document.getElementById(center + '_serverStatus').innerHTML = msg == 'LOADING' ? '<i class="h1 fas fa-spin fa-spinner"></i>' : '<i class="h1 fas fa-times-circle"></i>';
}

// 공통으로 부를 ajax
function ajax_request(key, center){
    $.ajax({
        url: CONTEXT + '/server/api/dashboard/connection/' + key,
        type: 'GET',
        contentType: 'application/json',
        success: function (res) {
            var ipAddr = res.ipAddress;
            var port = res.port;
            $.ajax({
                url: `http://${ipAddr}:${port}` + CONTEXT + '/server/api/dashboard/performance',
                type: 'GET',
                contentType: 'application/json',
                success: function (connection) {
                    if(connection){
                        for(var k in connection){
                            if(!k.endsWith('status')) {
                                document.getElementById(center + '_' + k).innerHTML = `<span>${connection[k]}</span>`;
                            } else {
                                switch(connection[k]){
                                    case "ON" :
                                        document.getElementById(center + '_serverTitle').className = 'text-sm font-weight-bold text-uppercase mb-1 text-success';
                                        document.getElementById(center + '_serverStatus').className = 'text-sm font-weight-bold text-uppercase mb-1 text-success';
                                        document.getElementById(center + '_serverStatus').innerHTML = '<i class="h1 fas fa-check-circle"></i>';
                                        break;
                                    case "OFF" :
                                        document.getElementById(center + '_serverTitle').className = 'text-sm font-weight-bold text-uppercase mb-1 text-danger';
                                        document.getElementById(center + '_serverStatus').className = 'text-sm font-weight-bold text-uppercase mb-1 text-danger';
                                        document.getElementById(center + '_serverStatus').innerHTML = '<i class="h1 fas fa-check-times"></i>';
                                        break;
                                    case "ERROR" :
                                        document.getElementById(center + '_serverTitle').className = 'text-sm font-weight-bold text-uppercase mb-1 text-danger';
                                        document.getElementById(center + '_serverStatus').className = 'text-sm font-weight-bold text-uppercase mb-1 text-danger';
                                        document.getElementById(center + '_serverStatus').innerHTML = '<i class="h1 fas fa-exclamation-triangle"></i>';
                                        break;
                                }
                            }
                        }
                    }
                },
                error: function(e){
                    msg_initialize(center, 'ERROR');
                },
                beforeSend: function(){
                    msg_initialize(center, 'LOADING');
                }
            });
        },
        error: function (e) {
            msg_initialize(center, 'ERROR');
        },
        beforeSend: function(){
            msg_initialize(center, 'LOADING');
        }
    });
}

// 센터 별 초기화를 위한 함수이다.
function find_by_center_refresh_server(center){
    var dom = document.getElementById(`${center}_SERVER_STATUS`);
    if(dom){
        var key = document.getElementById(center + "_change_server").value;
        if(key) ajax_request(key, center);
    }
}

// 버튼과 셀렉트 폼과 같이 사용.
function onchange_refresh_server(dom){
    var key = dom.value;
    var center = '';

    // 버튼과 셀렉트 둘 다 id 는 센터로 시작한다.
    if(dom.id.startsWith('B1')){
        center = 'B1';
    } else if(dom.id.startsWith('U3')){
        center = 'U3';
    } else {
        center = 'GOC';
    }

    // 버튼일 때를 대비한다.
    if(!key) {
        key = document.getElementById(center + "_change_server").value;
    }
    ajax_request(key, center);
}