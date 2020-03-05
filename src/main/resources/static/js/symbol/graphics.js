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

function drawMsymbol(id, format, sidc){
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
	var id = id;
	
	rendererMP.setDefaultSymbologyStandard(symStd);
	
	var cs = document.getElementById('SIDCCODINGSCHEME').value;
	
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
			
			sidc = buildSymbolID(function_sidc);
		}
		document.getElementById("SIDC").value = sidc;
		window.location.hash = sidc;
	}
	
	symbolCode = sidc;
	
	var code = SymbolUtilities.getBasicSymbolID(sidc);
	
	if(G_coordinates.length > 0) {
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
			
			var lineColor = document.getElementById("MonoColor")[document.getElementById("MonoColor").selectedIndex].value;
			if(lineColor != ''){
				modifiers.LINECOLOR = lineColor;
			}
		}
		var coordData = setCoordinates(G_coordinates);
		controlPoints = coordData.controlPoints;
		
		scale = mapScale(96);
	}
	
//    myGSymbolIcon = armyc2.c2sd.renderer.MilStdIconRenderer.Render(symbolCode,modifiers);
//    var center = ii.getCenterPoint();
    
	var json = '';
	mygeoJSON = null;
	G_modifiers = null;
	
	if(format != undefined){
		if(format == 'SVG'){
			myGSymbol = armyc2.c2sd.renderer.MilStdSVGRenderer.Render(symbolCode,modifiers); // Save Graphics symbol
			mygeoJSON = rendererMP.RenderSymbol(id,"Name","Description", symbolCode, controlPoints, "clampToGround", scale, bbox, modifiers, formatGeoJSON);
		} else {
			mygeoJSON = rendererMP.RenderSymbol(id,"Name","Description", symbolCode, controlPoints, "clampToGround", scale, bbox, modifiers, formatGeoJSON);
			G_modifiers = modifiers;
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


// datum_point: 1/점, 2/선, 3/면    over_point: boolean   constraint: 제약조건
// 마우스 EVENT 시 도형 View EVENT
var draw;
function drawGraphis(datum_point, over_point, draw_type, constraint){

	G_coordinates = new Array();
	
	var FigureType = draw_type;
	draw = new ol.interaction.Draw({
		source : selectLayer.getSource(),
		type : FigureType,
		minPoints : datum_point,
		maxPoints : over_point
	});
	
	var draw_id = 'P' + symbol_serial;
	draw.on('drawend', function(e) {
		// 그린 좌표에 ID 입력
		e.feature.setId(draw_id);
		G_coordinates = new Array(); // 좌표값 초기화
		var coordinate;
		if(FigureType == 'Polygon'){
			coordinate = draw.a[0];
		} else {
			coordinate = draw.a;
		}
		var id = symbol_serial;
		symbol_serial ++;
		
		if(FigureType == 'Point' && constraint == 'milSym'){
			getCoordinates(coordinate[0], coordinate[1]);
			
			if(mySymbol != undefined) {
				addMarker(mySymbol, id, draw.a[0], draw.a[1], 'Ssymbol'); // Make Marker
			}
		}else if(FigureType == 'Point' && constraint == ''){
			getCoordinates(coordinate[0], coordinate[1]);
			drawMsymbol(id, 'SVG');
			addMarker(myGSymbol, id, draw.a[0], draw.a[1], 'Msymbol');
		} else {
			if(constraint != ''){
				if(FigureType == 'Point'){
					getCoordinates(coordinate[0], coordinate[1]);
				} else {
					for(var i = 0; i < coordinate.length; i++){
						getCoordinates(coordinate[i][0], coordinate[i][1]);
					}
				}
				setAM_AN(constraint);
			} else {
				for(var i = 0; i < coordinate.length; i++){
					getCoordinates(coordinate[i][0], coordinate[i][1]);
				}
			}
			
			drawMsymbol(id, 'geoJSON');
			addMarker(mygeoJSON, id, '', '', 'Msymbol');
		}
		
		var draw_style = new ol.style.Style({
			visibility: 'hidden'
		});
		e.feature.setStyle(draw_style);
		
		map.removeInteraction(draw);
	});
	
	map.addInteraction(draw);
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

