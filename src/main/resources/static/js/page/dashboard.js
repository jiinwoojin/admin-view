$(function () {
    $('[data-toggle="tooltip"]').tooltip()
});

function event_refresh_server(dom){
    var value = dom.value;
    var id = '';

    if(dom.id.startsWith('B1')){
        id = 'B1';
    } else if(dom.id.startsWith('U3')){
        id = 'U3';
    } else {
        id = 'GOC';
    }

    if(!value) {
        value = document.getElementById(id + "_change_server").value;
    }

    $.ajax({
        url: CONTEXT + '/server/api/dashboard/performance/' + value,
        type: 'GET',
        contentType : 'application/json',
        success: function(res){
            if(res != null){
                for(var key in res){
                    if(!key.endsWith('status')) {
                        document.getElementById(id + '_' + key).innerText = res[key];
                    } else {
                        switch(res[key]){
                            case "ON" :
                                document.getElementById(id + '_serverTitle').className = 'text-sm font-weight-bold text-uppercase mb-1 text-success';
                                document.getElementById(id + '_serverStatus').className = 'text-sm font-weight-bold text-uppercase mb-1 text-success';
                                document.getElementById(id + '_serverStatus').innerHTML = '<i class="h1 fas fa-check-circle"></i>';
                                break;
                            case "OFF" :
                                document.getElementById(id + '_serverTitle').className = 'text-sm font-weight-bold text-uppercase mb-1 text-danger';
                                document.getElementById(id + '_serverStatus').className = 'text-sm font-weight-bold text-uppercase mb-1 text-danger';
                                document.getElementById(id + '_serverStatus').innerHTML = '<i class="h1 fas fa-check-times"></i>';
                                break;
                            case "ERROR" :
                                document.getElementById(id + '_serverTitle').className = 'text-sm font-weight-bold text-uppercase mb-1 text-danger';
                                document.getElementById(id + '_serverStatus').className = 'text-sm font-weight-bold text-uppercase mb-1 text-danger';
                                document.getElementById(id + '_serverStatus').innerHTML = '<i class="h1 fas fa-exclamation-triangle"></i>';
                                break;
                        }
                    }
                }
            }
        },
        error: function(e){
            console.log(e);
        },
        beforeSend: function(){
            jiCommon.showLoading();
        },
        complete: function(){
            jiCommon.hideLoading();
        }
    });
}