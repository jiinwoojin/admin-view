$(function () {
    $('[data-toggle="tooltip"]').tooltip();
    find_by_center_refresh_server('B1');
    find_by_center_refresh_server('U3');
    find_by_center_refresh_server('GOC');
});

// 센터 별 초기화를 위한 함수이다.
function find_by_center_refresh_server(center){
    var dom = document.getElementById(`${center}_SERVER_STATUS`);
    if(dom){
        var select = document.getElementById(center + "_change_server");
        var options = select.options;
        if(options.length <= 1) return;
        $("#" + center + "_change_server").val(options[1].value);
        onchange_refresh_server(select);
    }
}

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

    document.getElementById(center + '_serverName').className = (msg === 'ERROR') ? 'text-danger' : 'text-warning';
    document.getElementById(center + '_serverName').innerText = msg == 'LOADING' ? 'LOADING...' : 'LOAD ERROR!';
    document.getElementById(center + '_serverStatus').className = (msg === 'ERROR') ? 'text-danger' : 'text-warning';
    document.getElementById(center + '_serverStatus').innerHTML = msg == 'LOADING' ? '<i class="fas fa-spin fa-spinner"></i>' : '<i class="fas fa-times-circle"></i>';
}

// 공통으로 부를 ajax
function ajax_request(ip, port, center){
    $.ajax({
        url: (port == null) ? `https://${ip}${CONTEXT}/server/api/dashboard/performance` : `http://${ip}:${port}${CONTEXT}/server/api/dashboard/performance`,
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
                                document.getElementById(center + '_serverName').className = 'text-info';
                                document.getElementById(center + '_serverStatus').className = 'text-info';
                                document.getElementById(center + '_serverStatus').innerHTML = '<i class="fas fa-check-circle"></i>';
                                break;
                            case "OFF" :
                                document.getElementById(center + '_serverName').className = 'text-danger';
                                document.getElementById(center + '_serverStatus').className = 'text-danger';
                                document.getElementById(center + '_serverStatus').innerHTML = '<i class="fas fa-check-times"></i>';
                                break;
                            case "ERROR" :
                                document.getElementById(center + '_serverName').className = 'text-danger';
                                document.getElementById(center + '_serverStatus').className = 'text-danger';
                                document.getElementById(center + '_serverStatus').innerHTML = '<i class="fas fa-exclamation-triangle"></i>';
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

        ajax_request(ip, 11110, center);
    }
}