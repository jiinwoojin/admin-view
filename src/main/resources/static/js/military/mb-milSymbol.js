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

    }

    document.querySelector('#symbol_info').style.setProperty('display', 'none');
}

var drawMilitary = function() {

};