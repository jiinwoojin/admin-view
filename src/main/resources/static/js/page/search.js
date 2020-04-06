window.onload = onload_func;

function render_search_layers(list){
    var layer_infos = '';
    for(var i = 0; i < list.length; i++){
        layer_infos += `
            <div class="col-lg-4 col-md-6 col-sm-12 mb-2 layer-item">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">${ list[i].name }</h5>
                        <p class="card-text">
                            <i class="fas fa-calendar"></i> ${ list[i].registTime }<br/>
                            <i class="fas fa-user"></i> ${ list[i].registorName }<br/>
                            <i class="fas fa-atlas"></i> ${ list[i].description }<br/>
                            ${ list[i].isDefault ? '<i class="fas fa-layer-group"></i> <span>기본레이어(편집불가)</span>' : '' }
                        </p>
                        <div class="text-right">
                            <button data-toggle="modal" data-target="#layer-create" class="btn btn-primary"
                                data-id="${ list[i].id }"
                                data-name="${ list[i].name }"
                                data-description="${ list[i].description }"
                                data-projection="${ list[i].projection }"
                                data-middleFolder="${ list[i].middleFolder }"
                            >
                                <i class="fas fa-cog"></i>
                            </button>
                            ${ 
                                !list[i].isDefault ? 
                                `
                                    <span class="btn btn-danger" onclick="delLayer(this, '${list[i].name}', ${list[i].id})">
                                        <i class="fas fa-trash"></i>
                                    </span>
                                ` : '' 
                            }
                        </div>
                    </div>
                </div>
            </div>
        `;
    }

    var dom = document.getElementById('search_layers');
    dom.innerHTML = layer_infos;
}

function render_search_counts(count) {
    var text = `검색 결과 : 총 ${count} 건`;

    var dom = document.getElementById('search_count');
    dom.innerHTML = text;
}

function render_pagination_bar(pg, count){
    var pageCount = Math.ceil(count / 6);

    var first = Math.floor(((pg - 1) * 1) / 5) * 5 + 1;
    var last = Math.floor(((pg - 1) * 1) / 5) * 5 + 5;

    var pagination = `<ul class="pagination justify-content-center">`;

    if(first > 1){
        pagination += `
            <li class="page-item">
                <a class="page-link" href="${ '/admin-view/view/manage/layer-manage?pg=' + (first - 1) + "&" + convertObjectToQueryStringWithoutPage() }">&lt;</a>
            </li>
        `;
    }

    for(var i = first; i <= last && i <= pageCount; i++){
        pagination += `
            ${ i == pg ? `<li class="page-item active">` : `<li class="page-item">` }
                <a class="page-link" href="${ '/admin-view/view/manage/layer-manage?pg=' + i + "&" + convertObjectToQueryStringWithoutPage() }">${ i }</a>
            </li>
        `;
    }

    if(last < pageCount){
        pagination += `
            <li class="page-item">
                <a class="page-link" href="${ '/admin-view/view/manage/layer-manage?pg=' + (last + 1) + "&" + convertObjectToQueryStringWithoutPage() }">&gt;</a>
            </li>
        `;
    }

    pagination += `</ul>`;

    var dom = document.getElementById('pagination_bar');
    dom.innerHTML = pagination;
}

function ajax_option_load(name){
    $.ajax({
        url: '/admin-view/server/api/layer/options/' + name,
        type: 'get',
        success: function(d){
            var select = document.getElementById(name);
            while(select.firstChild){
                select.removeChild(select.lastChild);
            }

            d.forEach(function(data){
                var option = document.createElement('option');
                option.setAttribute("value", data.value);
                option.innerText = data.label;
                select.appendChild(option);
            });
        },
        error: function(e){
            console.log(e);
        }
    });
}

function ajax_result_load(){
    var qs = getQueryStringObject();
    $.ajax({
        url: `/admin-view/server/api/layer/list?pg=${ (qs.pg || 1) }&${ convertObjectToQueryStringWithoutPage() }`,
        type: 'get',
        success: function(d){
            render_search_layers(d.data);
            render_search_counts(d.count);
            render_pagination_bar((qs.pg || 1), d.count);
        },
        error: function(e){
            console.log(e);
        }
    });
}

function onload_func(){
    ajax_option_load('ob');
    ajax_option_load('sb');

    setTimeout(function() {
        initialize_form_with_query();
    }, 500);

    setTimeout(function() {
        ajax_result_load();
    }, 1000);
}

function onclick_day_radio(e){
    var sDate = document.getElementById("sDate");
    var eDate = document.getElementById("eDate");
    if(eDate.value == ""){
        var minus = new Date();
        minus.setDate(minus.getDate() - e.value);
        sDate.value = minus.toISOString().slice(0, 10);
        eDate.value = new Date().toISOString().slice(0, 10);
    } else {
        var minus = new Date(eDate.value);
        minus.setDate(minus.getDate() - e.value);
        sDate.value = minus.toISOString().slice(0, 10);
    }
}

function getQueryStringObject() {
    var a = window.location.search.substr(1).split('&');
    if (a == "") return {};
    var b = {};
    for (var i = 0; i < a.length; ++i) {
        var p = a[i].split('=', 2);
        if (p.length == 1)
            b[p[0]] = "";
        else
            b[p[0]] = decodeURIComponent(p[1].replace(/\+/g, " "));
    }
    return b;
}

function convertObjectToQueryStringWithoutPage(){
    var obj = {}
    var qs = getQueryStringObject();
    obj.ob = qs.ob || 0;
    obj.sb = qs.sb || 0;
    obj.st = qs.st || '';
    obj.sDate = qs.sDate || '';
    obj.eDate = qs.eDate || '';
    obj.lType = qs.lType || 'ALL';

    var str = '';
    for(var key in obj){
        str += `${key}=${obj[key]}&`;
    }

    str = str.substring(0, str.length - 1);
    return str;
}

function initialize_form_with_query(){
    var qs = getQueryStringObject();
    document.getElementById("ob").value = qs.ob || 0;
    document.getElementById("sb").value = qs.sb || 0;
    document.getElementById("st").value = qs.st || '';
    document.getElementById("sDate").value = qs.sDate || '';
    document.getElementById("eDate").value = qs.eDate || '';

    if(qs.lType != undefined) {
        var types = document.getElementsByName("lType");
        for(var i = 0; i < types.length; i++){
            if(types[i].value == qs.lType){
                types[i].checked = true;
            }
        }
    }
}

function onsubmit_func(){
    window.location.href = '/admin-view/view/manage/layer-manage';
}