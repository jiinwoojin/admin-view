function createTransparentMapLayer(name, type) {
    console.log('Mapbox Layer 생성.');
}

function Draw_Symbol() {
    console.log('Mapbox test.');

    debugger;
    var sorG = document.querySelector('#SIDCCODINGSCHEME').value;

    if (sorG.charAt(0) === 'W' || sorG.charAt(0) === 'G') {
        var sidc = document.querySelector('#SIDC').value;
        var drawInfo = getDrawGraphicsInfo(sidc);

        if (drawInfo === undefined || drawInfo.draw_type === '') {
            document.querySelector('#symbol_info').style.setProperty('display', 'none');
            return;
        }
    } else {
        drawBaseSymbol();
    }

    document.querySelector('#symbol_info').style.setProperty('display', 'none');
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