function createTransparentMapLayer(name, type) {
    console.log('Mapbox Layer 생성.');
}

function Draw_Symbol() {
    var options = stmp.getMilsymbolOptions()
    stmp.drawMilsymbol(options)
    $('#symbol_info').hide();
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