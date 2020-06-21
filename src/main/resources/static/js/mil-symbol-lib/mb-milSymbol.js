function createTransparentMapLayer(name, type) {
    console.log('Mapbox Layer 생성.');
}

function Draw_Symbol() {
    var options = stmp.getMilsymbolOptions()
    milSymbolLoader.drawMilsymbol(options)
    $('#symbol_info').hide();
}

function Mod_Symbol() {
    if(milSymbolLoader.drawControl.getSelectedIds().length === 0){
        toastr.warning("군대부호가 선택되지 않았습니다.")
    }
    var source = milSymbolLoader.map.getSource('milsymbols-source-feature')
    var features = milSymbolLoader.drawControl.getSelected().features
    var sourceData = source._data
    jQuery.each(features, function(idx, feature){
        var drawId = feature.id
        var drawGeometry = feature.geometry
        sourceData = milSymbolLoader.milsymbolsChangeSourceData(sourceData,drawId,drawGeometry,"draw")
    })
    if(sourceData !== null){
        source.setData(sourceData)
    }
}

function drawBaseSymbol() {
    stmp.addEvent('click', baseSymbolCallBack);
}

function baseSymbolCallBack(e) {
    console.log(e);
    console.log(this);

    var _lngLat = e.lngLat;

    let featureParam = {
        id : 'test',
        layerId : 'TestLayer',
        type : stmp.DRAW_TYPE_KIND.BASE_MILSYMBOL,
        coordInfo : {
            type : stmp.COORDINATE_SYSTEM.WGS84,
            coords : [_lngLat.lng, _lngLat.lat]
        },
        styleInfo : {
            milsymbol : {

            }
        }
    };

    stmp.removeEvent('click', baseSymbolCallBack);
}
