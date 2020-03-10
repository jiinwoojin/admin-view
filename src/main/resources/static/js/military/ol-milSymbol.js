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

selectClick.on('select', function(e){
    var selectFeature;
    var selectClickFeature = selectClick.getFeatures().getArray()[0];
    var extent = [];

    if(selectClickFeature){
        var selectId = selectClickFeature.getId();
        if(selectId != undefined){
            if(selectId.split('_')[1] == 0){
                modifyCancel();
                return;
            } else if(selectFeatureId != undefined){
                if(selectFeatureId.substr(0,3) != selectId.substr(0,3)){
                    modifyCancel();
                }
            }

            if(selectId.charAt(0) == 'S'){
                return;
            } else {
                selectFeatureId = selectId;
                selectFeatureId_arr = selectFeatureId.split('_');

                if(selectFeatureId_arr.length > 2){
                    selectFeature = selectLayer.getSource().getFeatureById('P'+selectFeatureId_arr[0]);
                    if(selectFeature.getGeometry().getType() == 'Point'){
                        modifyCancel();
                        return;
                    }
                } else if(selectFeatureId_arr.length == 1){
                    selectFeature = selectLayer.getSource().getFeatureById(selectId); // general shape
                }
            }
        } else {
            selectClick.getFeatures().clear();
        }
    } else {
        modifyCancel();
    }

    if(selectFeature != undefined){
        $('#click_cancel_modify').removeClass('hidden');
        $('.milSymEvent').addClass('hidden');

        if(selectFeature.getGeometry().getType() == 'MultiLineString' || selectFeature.getGeometry().getType() == 'Polygon'){
            modify = new ol.interaction.Modify({
                features: new ol.Collection([selectFeature])
            });
        } else {
            modify = new ol.interaction.Modify({
                features: new ol.Collection([selectFeature]),
                insertVertexCondition: function(e){
                    return ol.events.condition.never();
                }
            });
        }

        snap = new ol.interaction.Snap({
            features: new ol.Collection([selectFeature])
        });

        selectFeature.setStyle(null);

        // Modify select Line to Red
        var selectStyle = new ol.style.Style({
            stroke : new ol.style.Stroke({
                color: '#FF0000', width: 3
            })
        });
        selectFeature.setStyle(selectStyle);

        modify.on('modifyend', function(e){
            G_coordinates = new Array();

            var coordinates = e.features.getArray()[0].getGeometry().getCoordinates();
            for(var i = 0; i < coordinates.length; i++){
                if(coordinates[0].length > 2){
                    coordinates = coordinates[0];
                    getCoordinates(coordinates[i][0], coordinates[i][1]);
                } else {
                    getCoordinates(coordinates[i][0], coordinates[i][1]);
                }
            }

            var source = selectLayer.getSource();
            var ids = selectFeatureId.split('_');
            var id = ids[0];
            var sidc = source.getFeatureById(selectFeatureId).getProperties().options['symbolID'];
            var modifiers = source.getFeatureById(selectFeatureId).getProperties().modifier;

            for(var j = 0; j < ids[1]; j++){
                deleteMarker(ids[0]+'_'+ids[1]+'_'+j);
            }

            createModifiers(sidc);
            if(modifiers != undefined){
                var code = SymbolUtilities.getBasicSymbolID(sidc);
                var mtgs = SymbolDefTable.getSymbolDef(code, symStd);
                var mtgs_split = mtgs.modifiers.split('.');
                for(var i = 0; i < mtgs_split.length; i++){
                    if(mtgs_split[i] != ''){
                        var input = document.getElementById(mtgs_split[i]);
                        if (input != undefined ) {  // 20200214
                            var modifData = modifiers[mtgs_split[i]];
                            var val = '';
                            if(mtgs_split[i] == 'AM' || mtgs_split[i] == 'AN' || mtgs_split[i] == 'XN'){//20200226 X-> XN
                                for(var k = 0; k < modifData.length; k++){
                                    val += modifData[k];
                                    if(k < (modifData.length-1)){
                                        val += ',';
                                    }
                                }
                            } else {
                                val = modifData;
                            }
                            input.value = val;
                        }
                    }
                }
            }

            drawMsymbol(id, 'geoJSON', sidc);
            addMarker(mygeoJSON, id, '', '', 'Msymbol');

            selectClick.getFeatures().clear();
        });

        map.addInteraction(modify);
        map.addInteraction(snap);
    }
});

// military Symbol DEL function
function deleteMarker(id){
    var id = selectLayer.getSource().getFeatureById(id);
    selectLayer.getSource().removeFeature(id);
}

//milirary Symbol ALL DEL function
function removeAllMarker(){
    modifyCancel();
    selectClick.getFeatures().clear();
    selectLayer.getSource().clear();
}