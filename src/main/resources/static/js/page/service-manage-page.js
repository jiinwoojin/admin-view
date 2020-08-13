// [JS 로직] 서버 - 서비스 관리 페이지
$(document).ready(function() {
    if (!jQuery.isEmptyObject(message)) {
        toastr.info(message);
    }

    initialize_dataTable('list_table_shutdown', { 'order' : [[ 4, 'desc' ]] });

    if (connections.length > 0) {
        $('#service-carousel-0').carousel();
    }

    $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
        var id = e.target && e.target.id;
        var idx = id.replace('tab', '');
        $('#service-carousel-' + idx).carousel();
    });

    var tabEvent = function(dom) {
        var data = $(dom).data();
        var ip = data.ip;
        var index = data.index;
        $.ajax({
            url: `${CONTEXT}/server/api/service/remote-docker-initialize-check`,
            type: 'POST',
            data: JSON.stringify({
                ip: ip,
            }),
            contentType: 'application/json',
            success: function (status) {
                for (var key in status) {
                    successFunc(key, index, { result : status[key] });
                }
            },
            error: function (e) {
                for (var key in services) {
                    var service = services[key];
                    if (service.port === 0) {
                        display_initialize(service.name, 'ERROR', index);
                    }
                }
            },
            beforeSend: function () {
                for (var key in services) {
                    var service = services[key];
                    if (service.port === 0) {
                        display_initialize(service.name, 'LOADING', index);
                    }
                }
            }
        });

        $.ajax({
            url: `${CONTEXT}/server/api/service/extension-initialize-check`,
            type: 'POST',
            data: JSON.stringify({
                ip: ip,
            }),
            contentType: 'application/json',
            success: function (status) {
                for (var key in status) {
                    successFunc(key, index, { result : status[key] });
                }
            },
            error: function (e) {
                for (var key in services) {
                    var service = services[key];
                    if (service.port !== 0) {
                        display_initialize(service.name, 'ERROR', index);
                    }
                }
            },
            beforeSend: function () {
                for (var key in services) {
                    var service = services[key];
                    if (service.port !== 0) {
                        display_initialize(service.name, 'LOADING', index);
                    }
                }
            }
        });
    };

    $("a[data-toggle='tab']").on('shown.bs.tab', function(e) {
        tabEvent(e.target);
    });

    if (document.getElementById('service-exists') != null) {
        var tabId = 'tab0';
        if(sessionStorage['tabId']){
            tabId = sessionStorage['tabId'];
            sessionStorage.removeItem('tabId');
        }
        $('#' + tabId).click();
        tabEvent(document.getElementById(tabId));
    }
});

function display_initialize(service, type, idx){
    var dom_icon = document.getElementById('service-status-icon-' + service + '-' + idx);
    var dom_text = document.getElementById('service-status-text-' + service + '-' + idx);
    var dom_title = document.getElementById('service-status-title-' + service + '-' + idx);

    dom_text.innerText = type;
    dom_text.className = (type === 'LOADING') ? '' : 'text-danger';
    dom_title.className = (type === 'LOADING') ? '' : 'text-danger';
    dom_icon.className = (type === 'LOADING') ? 'h1 fas fa-spin fa-spinner' : 'h1 fas fa-exclamation-triangle text-danger';

    if(type === 'ERROR') {
        $(`#service-refresh-${service}-${idx}`).prop('disabled', true);
        $(`#service-start-${service}-${idx}`).prop('disabled', true);
        $(`#service-stop-${service}-${idx}`).prop('disabled', true);
        $(`#service-restart-${service}-${idx}`).prop('disabled', true);
    }
}

function successFunc(service, idx, status) {
    var dom_icon = document.getElementById('service-status-icon-' + service + '-' + idx);
    var dom_text = document.getElementById('service-status-text-' + service + '-' + idx);
    var dom_title = document.getElementById('service-status-title-' + service + '-' + idx);

    var res = status.result && status.result.toUpperCase();
    dom_text.innerText = res ? res : 'UNKNOWN';
    switch(res){
        case 'CREATED':
        case 'RUNNING':
        case 'RESTARTING':
            dom_icon.className = (res === 'CREATED') ? 'h1 fas fa-plus-square text-info' : (res === 'RUNNING') ? 'h1 fas fa-check text-info' : 'h1 fas fa-sync text-warning';
            dom_text.className = 'text-info';
            dom_title.className = 'text-info';
            $(`#service-start-${service}-${idx}`).prop('disabled', true);
            $(`#service-stop-${service}-${idx}`).prop('disabled', false);
            break;
        case 'PAUSED':
            dom_icon.className = 'h1 fas fa-pause text-warning';
            dom_text.className = 'text-warning';
            dom_title.className = 'text-warning';
            $(`#service-start-${service}-${idx}`).prop('disabled', true);
            $(`#service-stop-${service}-${idx}`).prop('disabled', false);
            break;
        case 'REMOVING':
        case 'EXITED':
        case 'DEAD':
            dom_icon.className = 'h1 fas fa-times-circle text-danger';
            dom_text.className = 'text-danger';
            dom_title.className = 'text-danger';
            $(`#service-start-${service}-${idx}`).prop('disabled', false);
            $(`#service-stop-${service}-${idx}`).prop('disabled', true);
            break;
        default :
            dom_icon.className = 'h1 fas fa-question';
            dom_text.innerText = 'UNKNOWN';
            dom_text.className = '';
            dom_title.className = '';
            $(`#service-start-${service}-${idx}`).prop('disabled', true);
            $(`#service-stop-${service}-${idx}`).prop('disabled', true);
            $(`#service-restart-${service}-${idx}`).prop('disabled', true);
            break;
    }
}

function onclick_status_refresh(service, ip, port, idx){
    if(port === 0) {
        $.ajax({
            url: `${CONTEXT}/server/api/service/remote-docker-check`,
            type: 'POST',
            data: JSON.stringify({
                ip: ip,
                name: service
            }),
            contentType: 'application/json',
            success: function (status) {
                successFunc(service, idx, status);
            },
            error: function (e) {
                display_initialize(service, 'ERROR', idx);
            },
            beforeSend: function () {
                display_initialize(service, 'LOADING', idx);
            }
        });
    } else {
        $.ajax({
            url: `${CONTEXT}/server/api/service/extension-check`,
            type: 'POST',
            data: JSON.stringify({
                ip: ip,
                port: port
            }),
            contentType: 'application/json',
            success: function (status) {
                successFunc(service, idx, status)
            },
            error: function (e) {
                display_initialize(service, 'ERROR', idx);
            },
            beforeSend: function () {
                display_initialize(service, 'LOADING', idx);
            }
        });
    }
}

function onclick_service_execute(btn){
    var method = $(btn).data('method');
    var index = $(btn).data('index');
    var service = $(btn).data('service');
    var serviceKey = $(btn).data('serviceKey');
    var hostname = $(btn).data('hostname');

    var question = service + " 서비스를 " + (method === 'START' ? '가동' : method === 'RESTART' ? '재가동' : '정지') + '합니다. 계속 하시겠습니까?';
    if(confirm(question)){
        $.ajax({
            url: `${CONTEXT}/view/server/remote-service-execute`,
            type: 'POST',
            data: JSON.stringify({
                ip: connections[index].ip,
                name: serviceKey,
                method: method,
                hostname : hostname
            }),
            contentType: 'application/json',
            success: function (res) {
                if(res && res.result){
                    sessionStorage.setItem("tabId", $('a.nav-link.active').attr('id'));
                    window.location = window.location.pathname;
                }
            },
            error: function (e) {
                display_initialize(serviceKey, 'ERROR', index);
            },
            beforeSend: function () {
                display_initialize(serviceKey, 'LOADING', index);
            }
        });
    }
}

function onclick_remove_all_history(){
    if (confirm('현재까지 생성된 작동 내역들을 전부 삭제합니다. 계속 하시겠습니까?')) {
        window.location.href = CONTEXT + '/view/server/history-clean';
    }
}

function onchange_manage_zone(dom) {
    var value = dom.value;
    window.location.href = CONTEXT + '/view/server/service-manage?zone=' + value;
}

