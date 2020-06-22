/**
 * Military Multiple Point Symbol Event
 * */

var myGSymbol; // SVG Image
//var myGSymbolIcon; // Canvas Image
var mygeoJSON = null; // multiple point G/W
var mygeoKML = null;
var G_coordinates = new Array(); // 좌표 Array
var G_modifiers;


var rendererMP = sec.web.renderer.SECWebRenderer;

function drawMsymbol(id, format, sidc, symStd, cs, function_sidc){
	var modifiers = {}; // graphics 부호 속성 array
	var symbolCode = ''; // 부호 코드
	var controlPoints = '';
	var bbox = '';

	var ratio = window.devicePixelRatio || 1;
	var scale;

	var formatKML = 0;
	var formatGeoSVG = 6; // svg format code
	var formatGeoCanvas = 3;
	var formatGeoJSON = 2; // geoJSON format code

	var pixelWidth = 0; // 부호 가로 크기
	var pixelHeight = 0; // 부호 세로 크기

	if(symStd === undefined){
		symStd = window.symStd
	}
	if(cs === undefined){
		cs = document.getElementById('SIDCCODINGSCHEME').value
	}
	if(function_sidc === undefined){
		function_sidc = window.function_sidc
	}

	rendererMP.setDefaultSymbologyStandard(symStd);
	if(sidc == undefined) {
		sidc = '';
		if(cs.charAt(0) == 'W') {
			sidc = function_sidc;
		} else if (cs.charAt(0) == 'G') {
			//var SIDCCODINGSCHEME = cs;
			//var SIDCBATTLEDIMENSION = battle_sidc;
			var SIDCFUNCTIONID = function_sidc;
			if(SIDCFUNCTIONID.length != 15)
				return;

			sidc = buildSymbolID(function_sidc, null);
		}
		document.getElementById("SIDC").value = sidc;
		window.location.hash = sidc;
	}

	symbolCode = sidc;

	var code = SymbolUtilities.getBasicSymbolID(sidc);
	var coordinates = []
	if(milSymbolLoader && milSymbolLoader.map && milSymbolLoader.map._drawing_milsymbol_coordinates){
		coordinates = milSymbolLoader.map._drawing_milsymbol_coordinates
	}
	if(coordinates.length > 0) {
		if(SymbolDefTable.getSymbolDef(code, symStd)){
			var mtgs = SymbolDefTable.getSymbolDef(code, symStd);
			var mtgs_split = mtgs.modifiers.split('.');

			for(var i = 0; i < mtgs_split.length; i++){
				// C -> 숫자(특수한경우), H -> 문자(20), N -> ENY표시, T -> 문자(15), V -> 문자(20), W -> 숫자,기호 YY:MM:DD(16)
				// X -> 숫자(14), Y -> 문자(19) , AM -> 숫자(6), AN -> 숫자(3)
				if(mtgs_split[i] != ''){
					// 정보가 비어있을 경우 return;
					if(mtgs_split[i] == 'AM' || mtgs_split[i] == 'AN' || mtgs_split[i] == 'XN'){
						var data = [];
						var val = document.getElementById(mtgs_split[i]).value;
						if(val.indexOf(',') != -1){
							var values = val.split(',');
							for(var j = 0; j < values.length; j++){
								data.push(Number(values[j].trim()));
							}
						} else {
							data.push(val);
						}
						modifiers[mtgs_split[i]] = data;
					} else {
						var mtgs_input = document.getElementById(mtgs_split[i]);
						if ( mtgs_split[i] === "B") {//20200225 add
							mtgs_input = document.getElementById("SIDCSYMBOLMODIFIER12");
						}
						if(mtgs_input != undefined){
							modifiers[mtgs_split[i]] = mtgs_input.value;
						}
					}
				}
			}
		}

		if(format == 'SVG'){
			var size = document.getElementById("Size").value;
			var keepUnitRatio = false;
			var drawAsIcon = false;

			modifiers.SYMSTD = 1;
			modifiers.SIZE = size;
			modifiers.KEEPUNITRATIO = keepUnitRatio;
			modifiers.ICON = drawAsIcon;

			//var lineColor = document.getElementById("MonoColor")[document.getElementById("MonoColor").selectedIndex].value;
			var lineColor = SymbolUtilities.getLineColorOfAffiliation(code).toHexString(false);//20200303
			if(lineColor != ''){
				modifiers.LINECOLOR = lineColor;
			}
		}
		var coordData = setCoordinates(coordinates);
		controlPoints = coordData.controlPoints;
		// scale 유사값
		if(stmp.PRESENT_MAP_KIND == stmp.MAP_KIND.MAP_2D){
			// 맵박스
			var zoom = Math.floor(milSymbolLoader.map.getZoom())
			var res = {
				0: 78271.484,
				1: 39135.742,
				2: 19567.871,
				3: 9783.936,
				4: 4891.968,
				5: 2445.984,
				6: 1222.992,
				7: 611.496,
				8: 305.748,
				9: 152.874,
				10: 76.437,
				11: 38.218,
				12: 19.109,
				13: 9.555,
				14: 4.777,
				15: 2.389,
				16: 1.194,
				17: 0.597,
				18: 0.299,
				19: 0.149,
				20: 0.075
			}
			scale = (300000000 - 10000) / res[0] * res[(zoom)]
		}else if(stmp.PRESENT_MAP_KIND == stmp.MAP_KIND.MAP_3D){
			// 세슘
			var xmin = 999999999, ymin = 999999999, xmax = 0, ymax = 0
			for(var idx in coordinates){
				var x = coordinates[idx].x
				var y = coordinates[idx].y
				if (x < xmin) xmin = x
				if (y < ymin) ymin = y
				if (x > xmax) xmax = x
				if (y > ymax) ymax = y
			}
			var extent = [xmin,ymin,xmax,ymax]
			var res = (xmax - xmin) / milSymbolLoader.map.container.clientWidth
			var METERS_PER_DEGREES = 111194.87428468118
			var inchesPerMetre = 39.37
			var dpi = 96
			scale = res * METERS_PER_DEGREES * inchesPerMetre * dpi
			//console.log(coordinates, extent, res, dpi)
		}
	}
	if(format != undefined){
		milSymbolLoader.map._drawing_milsymbol._svg_symbol = null
		milSymbolLoader.map._drawing_milsymbol._geojson = null
		milSymbolLoader.map._drawing_milsymbol._modifiers = null
		if(format == 'SVG'){
			milSymbolLoader.map._drawing_milsymbol._svg_symbol = armyc2.c2sd.renderer.MilStdSVGRenderer.Render(symbolCode,modifiers); // Save Graphics symbol
			milSymbolLoader.map._drawing_milsymbol._geojson = rendererMP.RenderSymbol(id,"Name","Description", symbolCode, controlPoints, "clampToGround", scale, bbox, modifiers, formatGeoJSON);
		} else {
			milSymbolLoader.map._drawing_milsymbol._geojson = rendererMP.RenderSymbol(id,"Name","Description", symbolCode, controlPoints, "clampToGround", scale, bbox, modifiers, formatGeoJSON);
			if(symbolCode === "GFG-GLC---****X"){
				// 작전활동부호-지휘통제및일반작전-일반-선-접촉선
				// 색상변경
				milSymbolLoader.map._drawing_milsymbol._geojson = milSymbolLoader.map._drawing_milsymbol._geojson.replace("#FF0000","#000000")
			}
			milSymbolLoader.map._drawing_milsymbol._modifiers = modifiers;
		}
	} else {
		// 작전활동부호 미리보기 이미지
		var symbolDef = SymbolDefTable.getSymbolDef(code, symStd);
		if(symbolDef != null && symbolDef != undefined){
			if(symbolDef['minPoints'] > 0){
				modifiers.SIZE = document.getElementById("Size").value;
				var si = armyc2.c2sd.renderer.MilStdSVGRenderer.Render(symbolCode,modifiers);
				document.getElementById("ImageSymbol").style.width = si.getSVGBounds().getWidth()*(3/2) +'px';
				document.getElementById("ImageSymbol").style.height = si.getSVGBounds().getHeight()*(3/2) +'px';
				document.getElementById("ImageSymbol").src = si.getSVGDataURI();
			}
		}
	}
}

// 좌표값을 받아 controlPoints
function setCoordinates(coordinates){
	var coordData = [];
	coordData.controlPoints = '';

	var minX = 0;
	var minY = 0;
	var maxX = 0;
	var maxY = 0;

	if(coordinates.length == 1){
		coordData.controlPoints += coordinates[0].x + ',' + coordinates[0].y;
	} else {
		for(var i = 0; i < coordinates.length; i ++){
			coordData.controlPoints += coordinates[i].x + ',' + coordinates[i].y + ' ';
			if(i == coordinates.length){
				coordData.controlPoints += coordinates[i].x + ',' + coordinates[i].y;
				break;
			}
		}
	}
	return coordData; // controlPoints
}

//좌표 입력
function getCoordinates(coordX, coordY){
	var coordinates = new Object();
	coordinates.x = coordX;
	coordinates.y = coordY;

	G_coordinates.push(coordinates);
}





function mapScale(dpi) {
    var unit = map.getView().getProjection().getUnits();
    var resolution = map.getView().getResolution();
    var inchesPerMetre = 39.37;

    return resolution * ol.proj.METERS_PER_UNIT[unit] * inchesPerMetre * dpi;
}

// requires AM, AN value
function setAM_AN(constraint){
	var SIDCCode = document.getElementById('SIDC').value;
	SIDCCode = SymbolUtilities.getBasicSymbolID(SIDCCode);

	var requireData;
	var modifInput;
	if(SymbolUtilities.hasAMmodifierRadius(SIDCCode, symStd)){
		requireData = constraint['AM'];
		modifInput = document.getElementById('AM');
		if(modifInput.value == ''){
			if(requireData == '1')
				modifInput.value = 500;
		}
	} else if (SymbolUtilities.hasAMmodifierWidth(SIDCCode, symStd)){
		requireData = constraint['AM'];
		modifInput = document.getElementById('AM');
		if(modifInput.value == ''){
			if(requireData == '1')
				modifInput.value = '0';
			else if (requireData == '2')
				modifInput.value = '300,1000';
		}
		if(SymbolUtilities.hasANmodifier(SIDCCode, symStd)){
			requireData = constraint['AN'];
			modifInput = document.getElementById('AN');
			if(modifInput.value == ''){
				if(requireData == '1')
					modifInput.value = '0';
				else if (requireData == '2')
					modifInput.value = '0,0';
			}
		}
	}
}

