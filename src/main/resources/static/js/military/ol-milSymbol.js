// draw Symbol
function Draw_Symbol() {
    var mil_symbol; // military symbol Object

    // Single(symbol) or Multiple(Graphics)
    var SorG = $('#SIDCCODINGSCHEME').val();
    if (SorG.charAt(0) == 'W' || SorG.charAt(0) == 'G') {
        // max, min, constraint 가져오는 방식 변경
        var sidc = document.getElementById('SIDC').value;
        var drawInfo = getDrawGraphicsInfo(sidc);
        if (drawInfo === undefined || drawInfo.draw_type == '') {
            $('#symbol_info').hide();
            return;
        }

        drawGraphis(drawInfo.min_point, drawInfo.max_point, drawInfo.draw_type, drawInfo.constraint);
    } else {
        drawGraphis(1, 1, 'Point', 'milSym');

    }
    $('#symbol_info').hide();
}

// modify Symbol
function Mod_Symbol() {
    var id = document.getElementById('mod_id'); // 수정할 ID

    if (id.value != '') {
        var format = 'geoJSON';
        // Single(symbol) or Multiple(Graphics)
        var SorG = $('#SIDCCODINGSCHEME').val();
        if (SorG.charAt(0) == 'W' || SorG.charAt(0) == 'G') {
            //multiple
            if (id.value.split('_')[1] != '0') {
                var ops = id.value.split('_');
                for (var i = 0; i < Number(ops[1]); i++) {
                    if (id.value.length < 4) {
                        deleteMarker(ops[0] + '_' + (i + 1));
                        format = 'SVG';
                    } else
                        deleteMarker(ops[0] + '_' + ops[1] + '_' + i);
                }
            } else {
                deleteMarker(id);
            }

            drawMsymbol(id.value.split('_')[0], format);
            if (format == 'SVG') {
                addMarker(myGSymbol, id.value.split('_')[0], G_coordinates[0].x, G_coordinates[0].y, 'Msymbol');
            } else
                addMarker(mygeoJSON, id.value.split('_')[0], '', '', 'Msymbol');

        } else {
            //single
            var changeFeature = selectLayer.getSource().getFeatureById(id.value);
            var mapProperties = getMapProperties();//20200304
            var mysvg = new Image();
            mysvg.src = 'data:image/svg+xml,' + encodeURIComponent(mySymbol.asSVG());

            var style = new ol.style.Style({
                image: new ol.style.Icon(({
                    anchor: [mySymbol.getAnchor().x, mySymbol.getAnchor().y],
                    anchorXUnits: 'pixels',
                    anchorYUnits: 'pixels',
                    imgSize: [Math.floor(mySymbol.getSize().width), Math.floor(mySymbol.getSize().height)],
//                             img: (mysvg)
                    //20200228 - ol.js SCRIPT5022: IndexSizeError
                    img: (mySymbol.asCanvas())
                }))
            });
            changeFeature.setStyle(style);
            changeFeature.setProperties({
                'options': mySymbol.getOptions(),
                'mapProperties' : mapProperties//20200304
            });
        }
        id.value = '';
    }

    selectClick.getFeatures().clear();
    $('#symbol_info').hide();
    $('#mod').addClass('hidden');
}

// Only Input Number
function InputNum(obj) {
    var keyValue = event.keyCode;
    if (((keyValue >= 48) && (keyValue <= 57)) || (keyValue == 46)) return true;
    else return false;
}

function tmsLayerCheck(){
    var check = true;
    map.getLayers().forEach(function (layer){
        if (layer.get('name') != undefined){
            if(layer.get('name').substring(0, 3) == 'tms'){
                check = false;
            }
        }
    });

    return check;
}

function createTransparentMapLayer(name, type){
    // milSymbol maker layer
    var TMapLayer = new ol.layer.Vector({
        source: new ol.source.Vector({
            features: [],
            projection: 'EPSG:4326'
        })
    });
    if(name == undefined){
        name = 'tms_00';
    }

    TMapLayer.set('name', name);

    if(type == undefined){
        var check = tmsLayerCheck();
        if(check){
            map.addLayer(TMapLayer);
            selectLayer = chooseLayer(name);

            var file_info = {};
            file_info.file_name = '새 레이어 0';
            var index = '00';
            selectTransparentMapList('', file_info, index);
        } else {
            alert('생성된 투명도가 있습니다.');
            return;
        }
    } else if (type == 'road') {
        map.addLayer(TMapLayer);
        selectLayer = chooseLayer(name);
    }
}