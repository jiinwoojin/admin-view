/**
 * Military Symbol SIDC Page Event
 * */

//20200206 검색 가능한 부호 코드로 변경
var milcodeChange = function(sidc){
	var sidc_val = '';
	if(sidc.charAt(0) == 'G' || sidc.charAt(0) == 'W'){
		sidc_val = SymbolUtilities.getBasicSymbolID(sidc);
	} else {
		var symbolID = sidc.substring(0,1);
		symbolID += '*';
		symbolID += sidc.substring(2,3);
		symbolID += '*';
		symbolID += sidc.substring(4,10);
		if( sidc.substring(10,11) != "-" && sidc.substring(10,11) != "*") {//20200211
			symbolID += sidc.substring(10,11);
		}else{
			symbolID += '*';
		}
		symbolID += '****';
		//symbolID += '*****';
		sidc_val = symbolID;
	}
	return sidc_val;
}
// page loading draw Symbol
var sidcKey = '';
$('#mil_body').ready(function(){
	sidcCreatorInit('SFG*IGA---H****');	//SFG*IGA---H-  WO--IC--------- WOS-HDS---P----
	
	drawSymbol();
	
	changeSIDC();
	//selectedSymbol( 'SFG*UCI----D');
	// 2018.04.17 추가
	createTransparentMapLayer(); //투명도 생성

	var searchSidcKey = function(searchData){
		var milCode = '';
		var searchOk = false;
		var careateSidc = '';
		var JSONfile = '/json/milsym/S.json';
			$.ajax({
				url: JSONfile,
				dataType: 'json',
				async: false,
				success: function(data){
					$.each(Object.keys(data), function(index, item){
						sidcKey = item;
						var milItem = data[item];	
						if(item === searchData){
							milCode = milItem.MIL_CODE;
							careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
							searchOk = true;
							return false;
						}else if(milItem.CODE_KOR_NAME === searchData){
							milCode = milItem.MIL_CODE;
							careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
							milCodeSearch = false;
							searchOk = true;
							return false;
						}else if(milItem.MIL_CODE === searchData){
							milCode = milItem.MIL_CODE;
							careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
							milCodeSearch = false;
							searchOk = true;
							return false;
						}else if(milItem.MIL_CODE === milcodeChange(searchData)){
							milCode = milItem.MIL_CODE;
							careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
							milCodeSearch = false;
							searchOk = true;
							return false;
						}
						
					});
				}
			});

			if(!searchOk){
				JSONfile = '/json/milsym/G.json';
				$.ajax({
					url: JSONfile,
					dataType: 'json',
					async: false,
					success: function(data){
						$.each(Object.keys(data), function(index, item){
							sidcKey = item;
							var milItem = data[item];	
							if(item === searchData){
								milCode = milItem.MIL_CODE;
								careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
								searchOk = true;
								return false;
							}else if(milItem.CODE_KOR_NAME === searchData){
								milCode = milItem.MIL_CODE;
								careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
								milCodeSearch = false;
								searchOk = true;
								return false;
							}else if(milItem.MIL_CODE === searchData){
								milCode = milItem.MIL_CODE;
								careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
								milCodeSearch = false;
								searchOk = true;
								return false;
							}else if(milItem.MIL_CODE === milcodeChange(searchData)){
								milCode = milItem.MIL_CODE;
								careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
								milCodeSearch = false;
								searchOk = true;
								return false;
							}
						});
					}
				});
			}

			if(!searchOk){
				JSONfile = '/json/milsym/W.json';
				$.ajax({
					url: JSONfile,
					dataType: 'json',
					async: false,
					success: function(data){
						$.each(Object.keys(data), function(index, item){
							sidcKey = item;
							var milItem = data[item];	
							if(item === searchData){
								milCode = milItem.MIL_CODE;
								careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
								searchOk = true;
								return false;
							}else if(milItem.CODE_KOR_NAME === searchData){
								milCode = milItem.MIL_CODE;
								careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
								milCodeSearch = false;
								searchOk = true;
								return false;
							}else if(milItem.MIL_CODE === searchData){
								milCode = milItem.MIL_CODE;
								careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
								milCodeSearch = false;
								searchOk = true;
								return false;
							}else if(milItem.MIL_CODE === milcodeChange(searchData)){
								milCode = milItem.MIL_CODE;
								careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
								milCodeSearch = false;
								searchOk = true;
								return false;
							}	
						});
					}
				});
			}

			if(!searchOk){
				JSONfile = '/json/milsym/I.json';
				$.ajax({
					url: JSONfile,
					dataType: 'json',
					async: false,
					success: function(data){
						$.each(Object.keys(data), function(index, item){
							sidcKey = item;
							var milItem = data[item];	
							if(item === searchData){
								milCode = milItem.MIL_CODE;
								careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
								searchOk = true;
								return false;
							}else if(milItem.CODE_KOR_NAME === searchData){
								milCode = milItem.MIL_CODE;
								careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
								milCodeSearch = false;
								searchOk = true;
								return false;
							}else if(milItem.MIL_CODE === searchData){
								milCode = milItem.MIL_CODE;
								careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
								milCodeSearch = false;
								searchOk = true;
								return false;
							}else if(milItem.MIL_CODE === milcodeChange(searchData)){
								milCode = milItem.MIL_CODE;
								careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
								milCodeSearch = false;
								searchOk = true;
								return false;
							}
						});
					}
				});
			}

			if(!searchOk){
				JSONfile = '/json/milsym/O.json';
				$.ajax({
					url: JSONfile,
					dataType: 'json',
					async: false,
					success: function(data){
						$.each(Object.keys(data), function(index, item){
							sidcKey = item;
							var milItem = data[item];	
							if(item === searchData){
								milCode = milItem.MIL_CODE;
								careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
								searchOk = true;
								return false;
							}else if(milItem.CODE_KOR_NAME === searchData){
								milCode = milItem.MIL_CODE;
								careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
								milCodeSearch = false;
								searchOk = true;
								return false;
							}else if(milItem.MIL_CODE === searchData){
								milCode = milItem.MIL_CODE;
								careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
								milCodeSearch = false;
								searchOk = true;
								return false;
							}else if(milItem.MIL_CODE === milcodeChange(searchData)){
								milCode = milItem.MIL_CODE;
								careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
								milCodeSearch = false;
								searchOk = true;
								return false;
							}
						});
					}
				});
			}

			if(!searchOk){
				JSONfile = '/json/milsym/E.json';
				$.ajax({
					url: JSONfile,
					dataType: 'json',
					async: false,
					success: function(data){
						$.each(Object.keys(data), function(index, item){
							sidcKey = item;
							var milItem = data[item];	
							if(item === searchData){
								milCode = milItem.MIL_CODE;
								careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
								searchOk = true;
								return false;
							}else if(milItem.CODE_KOR_NAME === searchData){
								milCode = milItem.MIL_CODE;
								careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
								milCodeSearch = false;
								searchOk = true;
								return false;
							}else if(milItem.MIL_CODE === searchData){
								milCode = milItem.MIL_CODE;
								careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
								milCodeSearch = false;
								searchOk = true;
								return false;
							}else if(milItem.MIL_CODE === milcodeChange(searchData)){
								milCode = milItem.MIL_CODE;
								careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
								milCodeSearch = false;
								searchOk = true;
								return false;
							}
						});
					}
				});
			}
			document.getElementById("SIDC").value = milCode;
			sidcCreatorInit(milCode);
			saveFuncSIDC(careateSidc);
	}

	$('#search_sidc_btn').click(function(){
		var searchData = $('input[name=search_sidc_text]').val();
		if(searchData != ''){
			searchSidcKey(searchData);
		}
	});

	$('input[name=search_sidc_text]').keydown(function(key){
		var searchData = $(this).val();
		if(key.keyCode == 13) {
			if(searchData != ''){
				searchSidcKey(searchData);
			}
		}
	});

    //Bootstrap multiple modal
    var count = 0; // 모달이 열릴 때 마다 count 해서 z-index값을 높여줌
    $(document).on('show.bs.modal', '.modal', function () {
        var zIndex = 1040 + (10 * count);
        $(this).css('z-index', zIndex);
        setTimeout(function() {
            $('.modal-backdrop').not('.modal-stack').css('z-index', zIndex - 1).addClass('modal-stack');
        }, 0);
        count = count + 1;
    });

    // multiple modal Scrollbar fix
    $(document).on('hidden.bs.modal', '.modal', function () {
        $('.modal:visible').length && $(document.body).addClass('modal-open');
    });

    //칼라피커 추가
    $(".mil-colorpicker").colorpicker({
        color:"#000000",
        horizontal: true
    });
    //투명배경 기본
    $(".mil-colorpicker--transparent").colorpicker({
        color:"transparent",
        horizontal: true,
        format:"hex"
    });


    //구역 표시 사용여부 체크박스
    $(".useChk").on("click",function(){
        var $this = $(this);
        var $tar = $this.closest(".modal-body").find(".useChk-content");
        if($this.is(":checked")){
            $tar.show();
        }else{
            $tar.hide();
        }
    });

    //색상코드표 지정색
    $(".colorList__box").on("click",function(){
        var $this = $(this);
        var $thisVal = $this.find("input").val();
        $this.addClass("-active").siblings().removeClass("-active");
        $this.closest(".colorList").find(".mil-colorpicker,.mil-colorpicker--transparent").colorpicker('setValue',$thisVal);
    });
    $(".colorList .input-group-addon").on("click",function(){
        $(this).closest(".colorList").find(".colorList__box").removeClass("-active");
    });
    $(".colorList__chkBox").on("click",function(){
        if($(this).is(":checked")){
            $(this).closest(".from-group").next(".colorListBox").show();
        }else{
            $(this).closest(".from-group").next(".colorListBox").hide();
        }
    });
});
	

function milViewer(){
	var viewer = $('#symbol_info');
	
	viewer.fadeIn();
	
	var divTop = $('#topMenu').outerHeight() + $('.content-header').outerHeight();
	
	viewer.css({
		'top': divTop,
		'right': '10px',
		'position': 'absolute',
		'padding': '5px',
		'background-color': 'rgba(44, 59, 65, 0.6)', 
		'z-index': '999'
	}).show();
	
	var check = $('#shape_info').css('display');
	if(check != 'none'){
		$('#symbol_info').css('top', divTop + 39);
	}
}

$('#closer_x').on('click', function(){
	$('#symbol_info').hide();
});




/** 
 * Military Symbol Button click Event
 * */

// '군대 부호 적용' button click EVENT
$('#click_coord_btn').on('click', function(event) {
	drawSymbol();
	var pixelArray = new Array();
	pixelArray.push(layerX);
	pixelArray.push(layerY);
	
	var coord = map.getCoordinateFromPixel(pixelArray); // 마우스 pixel 기준으로 좌표값 가져오기
	
	// 수정 버튼 감추기
	if($('#mod').hasClass('hidden') === false){
		$('#mod').addClass('hidden');
	}
	
	$('#disabled_p').hide();
	$('#SIDCCODINGSCHEME').removeAttr('disabled');
	$('input[name=search_sidc_text]').removeAttr('readonly');
	
	if($('#add').hasClass('hidden') === true){
		$('#add').removeClass('hidden');
	}
	
	milViewer(); // military symbol viewer open
});

// '군대 부호 삭제' button click EVENT 
$('#click_coord_btn_del').on('click', function(evnet){
	var pixelArray = new Array;
	pixelArray.push(layerX);
	pixelArray.push(layerY);
	
	var feature = map.forEachFeatureAtPixel(pixelArray, function(feature, layer) {
		return feature;
	});
	
	if(feature){ 
		var id = feature.getId();
		
		modifyCancel();
		select_sp_id = undefined;
		
		if(id == undefined){
			if(interaction.getFeatureAtPixel_(pixelArray)){
				id = interaction.getFeatureAtPixel_(pixelArray).feature.getId();
			} else {
				return;
			}
		}
		
		if(id.charAt(0) == 'S'){
			map.removeInteraction(interaction);
			interaction = undefined;

			deleteMarker(id);
			selectClick.getFeatures().clear();
		} else {
			if(id.split('_')[1] != '0'){
				var ops = id.split('_');
				for(var i = 0; i < Number(ops[1]); i++){
					if(id.length < 4)
						deleteMarker(ops[0]+'_'+(i+1));
					else
						deleteMarker(ops[0]+'_'+ops[1]+'_'+i);
				}
				var mod_feature = 'P' + ops[0];
				deleteMarker(mod_feature);
			} else {
				deleteMarker(id);
			}
			
			selectClick.getFeatures().clear();
		}
	}
});

// '군대 부호 수정' button click EVENT
//20200206 수정 
$('#click_coord_btn_mod').on('click', function(event){
	var pixelArray = new Array;
	pixelArray.push(layerX);
	pixelArray.push(layerY);
	
	var feature = map.forEachFeatureAtPixel(pixelArray, function(feature, layer) {
		return feature;
	});
	
	if(feature){
		//modifyCancel();
		var id = feature.getId();
		if(id.charAt(0) != 'S' && id.split('_')[1] != '0'){
			// graphics (multiple)
			// 부호 수정은 없이 style과 수식정보 수정만 가능
			var options = feature.getProperties().options;
			var modifiers = feature.getProperties().modifier;
			var drawPoints = selectLayer.getSource().getFeatureById('P'+id.split('_')[0]).getGeometry().getCoordinates();
			if(drawPoints[0][0] != undefined){
				if(drawPoints[0][0][0] != undefined){
					drawPoints = selectLayer.getSource().getFeatureById('P'+id.split('_')[0]).getGeometry().getCoordinates()[0];
				}
			}
			
			G_coordinates = new Array();
			if(drawPoints[0].length != undefined){
				for(var p = 0; p < drawPoints.length; p++){
					getCoordinates(drawPoints[p][0],drawPoints[p][1]);
				}
			} else {
				getCoordinates(drawPoints[0],drawPoints[1]);
			}
			//var drawPoints = feature.getProperties().drawPoints;
			//G_coordinates = new Array();
			//G_coordinates = drawPoints;
			
			document.getElementById('SIDC').value = options['symbolID'];
			/*sidcCreatorInit(); */
			selectedSymbol(options['symbolID']);//20200207
			
			if(modifiers != undefined){
				var code = SymbolUtilities.getBasicSymbolID(options['symbolID']);
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
		} else if (id.charAt(0) != 'S') {
			// symbol (single)
			var lat = feature.getGeometry().A[0]; // 위도
			var lon = feature.getGeometry().A[1]; // 경도
			
			//document.getElementById('SIDC').value = feature.getProperties().options['sidc'];
			var changSidc = feature.getProperties().options['sidc'];
			var symbolID = changSidc.substring(0,1);
	        symbolID += '*';
	        symbolID += changSidc.substring(2,3);
	        symbolID += '*';
			symbolID += changSidc.substring(4,10);
			if( changSidc.substring(10,11) != "-" && changSidc.substring(10,11) != "*") {//20200211
				symbolID += changSidc.substring(10,11);
			}else{
				symbolID += '-';
			}
			symbolID += '----';
			//symbolID += '-----';
			document.getElementById("SIDC").value =symbolID;
			//sidcCreatorInit(symbolID);
			selectedSymbol(symbolID);//20200207
			
			var options = feature.getProperties().options;
			var keys = Object.keys(options);
			for(var i = 0; i < keys.length; i ++){
				var value = options[keys[i]];
				if(value == null)
					value = '';
				
				var InputValDiv = $('#'+keys[i]);
				if(InputValDiv.length == 0 || InputValDiv == undefined){
					var UpperFirstStr = keys[i].substring(0, 1).toUpperCase() + keys[i].substring(1, keys[i].length);
					var modifierID = idMapping(keys[i]); 
					if ( modifierID !== "") {
						InputValDiv = $('#'+modifierID);
					}else{
						InputValDiv = $('#'+UpperFirstStr);
					}
					if (keys[i] == 'icon'){
						InputValDiv = $('#DisplayIcon');
						InputValDiv.val(value);
					} else if(keys[i] == 'sidc') {
						InputValDiv = $('#SIDC');
						InputValDiv.val(value);
					} else {
						InputValDiv.val(value);
					}
				} else {
					InputValDiv.val(value);
				}
			}
			
			drawSsymbol();
		} else {
			alert('군대부호가 아닙니다.');
			return;
		}
		
		$('#mod_id').val(id);
		
		// 수정 버튼 감추기
		if($('#mod').hasClass('hidden') === true){
			$('#mod').removeClass('hidden');
		}
		
		$('#SIDCCODINGSCHEME').attr("disabled", true);
		$('input[name=search_sidc_text]').attr("readonly", true);
		
		
		if($('#add').hasClass('hidden') === false){
			$('#add').addClass('hidden');
		}
		
		milViewer(); // military symbol viewer open
		
		// 부호 변경 막기
		$('#disabled_p').css({
			'position': 'absolute',
			'width': '98%',
			'height': $('#FUNCTIONS').outerHeight(),
			'background-color': 'rgba(210, 214, 222, 0.4)',
			'z-index': '9'
		}).show();
	}
});

function modifyCancel(){
	if(modify != undefined){
		map.removeInteraction(modify);
		
		$('#click_cancel_modify').addClass('hidden');
		$('.milSymEvent').removeClass('hidden');

		var draw_style = new ol.style.Style({
			visibility: 'hidden'
		});
		for(var i = 1; i < symbol_serial; i++){
			var feature = selectLayer.getSource().getFeatureById('P'+i);
			if(feature != null){
				feature.setStyle(draw_style);
			}
		}
		selectFeatureId = undefined;
	}
}



/**
 * Map Marker Single Point ADD/DEL function
 * */

// military Symbol ADD function
function addMarker(symbol, id, lon, lat, type){

	var geom;
	var feature;
	var ratio = window.devicePixelRatio || 1;
	
	if(type == 'Ssymbol'){
		// create a point
		geom = new ol.geom.Point([lon, lat]);
		feature = new ol.Feature(geom);

		// 하나의 CSS 픽셀에 대한 현재 디스플레이 장치 상의 하나의 물리적인 픽셀의 비율을 반환
		var mysvg = new Image();
		mysvg.src = 'data:image/svg+xml,' + escape(symbol.asSVG());
		
		feature.setStyle([
			new ol.style.Style({
				image: new ol.style.Icon(({
					anchor: [symbol.getAnchor().x, symbol.getAnchor().y],
					anchorXUnits: 'pixels',
					anchorYUnits: 'pixels',
					imgSize: [Math.floor(symbol.getSize().width), Math.floor(symbol.getSize().height)],
					//img: (mysvg)
					img: (symbol.asCanvas())
				}))
			})
		]);
		
		// Insert properties
		feature.setProperties({
			'options' : symbol.getOptions()
		});
		
		if(id != null){
			feature.setId(id+'_0');
		}
		
		selectLayer.getSource().addFeature(feature);
		
	} else if(type == 'Msymbol') {
		var properties = JSON.parse(mygeoJSON)['properties'];

		if(lat != '' && lon != ''){
			geom = new ol.geom.Point([lon, lat]);
			feature = new ol.Feature(geom);
			
			var mysvg = new Image();
			/*mysvg.src = 'data:image/svg+xml,' + escape(symbol.getSVG());*/
			
			feature.setStyle([
				new ol.style.Style({
					image: new ol.style.Icon(({
						anchor: [symbol.getAnchorPoint().x, symbol.getAnchorPoint().y],
						anchorXUnits: 'pixels',
						anchorYUnits: 'pixels',
						imgSize: [Math.floor(symbol.getSVGBounds().width), Math.floor(symbol.getSVGBounds().height)],
						src: symbol.getSVGDataURI()
					}))
				})
			]);
			
			// Insert properties
			feature.setProperties({
				'options' : properties,
				'drawPoints' : G_coordinates
			});
			
			if(id != null){
				feature.setId(id+'_1');
			}
			selectLayer.getSource().addFeature(feature);
			
		} else {
			// style
			var color = document.getElementById('MonoColor')[document.getElementById("MonoColor").selectedIndex].value;
			if(color == ''){
				//color = JSON.parse(mygeoJSON).features[0].properties.strokeColor;
			}
			var size = 4;
			
			var geoJSONFormat = new ol.format.GeoJSON();
			var features1 = geoJSONFormat.readFeatures(mygeoJSON);
			for(var i = 0; i < features1.length; i++){
				var style = new ol.style.Style({
					fill: new ol.style.Fill({ color: color }),
					stroke: new ol.style.Stroke({ color: color, width: 3 }),
					text: new ol.style.Text({
						text:  features1[i].N.label,
						font: '17px Calibri,sans-serif',
						fill: new ol.style.Fill({ color: color }),
						stroke: new ol.style.Stroke({
							color: '#fff', width: 3
						})
					})
				});
				features1[i].setStyle(style);
				
				features1[i].setProperties({
					'options' : properties,
					'drawPoints' : G_coordinates,
					'modifier' : G_modifiers
				});
				if(id != null){
					features1[i].setId(id+'_'+features1.length+'_'+i);
				}
			}
			selectLayer.getSource().addFeatures(features1);
		}

	}
}

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

var selectFeatureId;
var selectFeatureId_arr;
var interaction;
var modify;
var snap;
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

function selectedSymbol(searchData) {//20200207
	var careateSidc = '';
	var milCode = '';
	var searchOk = false;
	if(searchData != ''){
		searchData = milcodeChange(searchData);
		var JSONfile = '/json/milsym/S.json';
		$.ajax({
			url: JSONfile,
			dataType: 'json',
			async: false,
			success: function(data){
				$.each(Object.keys(data), function(index, item){
					var milItem = data[item];	
					if(milItem.MIL_CODE === searchData){
						milCode = milItem.MIL_CODE;
						careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
						milCodeSearch = false;
						searchOk = true;
						return false;
					}	
				});
			}
		});

		if(!searchOk){
			JSONfile = '/json/milsym/G.json';
			$.ajax({
				url: JSONfile,
				dataType: 'json',
				async: false,
				success: function(data){
					$.each(Object.keys(data), function(index, item){
						var milItem = data[item];	
						if(milItem.MIL_CODE === searchData){
							milCode = milItem.MIL_CODE;
							careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
							milCodeSearch = false;
							searchOk = true;
							return false;
						}	
					});
				}
			});
		}

		if(!searchOk){
			JSONfile = '/json/milsym/W.json';
			$.ajax({
				url: JSONfile,
				dataType: 'json',
				async: false,
				success: function(data){
					$.each(Object.keys(data), function(index, item){
						var milItem = data[item];	
						if(milItem.MIL_CODE === searchData){
							milCode = milItem.MIL_CODE;
							careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
							milCodeSearch = false;
							searchOk = true;
							return false;
						}	
					});
				}
			});
		}

		if(!searchOk){
			JSONfile = '/json/milsym/I.json';
			$.ajax({
				url: JSONfile,
				dataType: 'json',
				async: false,
				success: function(data){
					$.each(Object.keys(data), function(index, item){
						var milItem = data[item];	
						if(milItem.MIL_CODE === searchData){
							milCode = milItem.MIL_CODE;
							careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
							milCodeSearch = false;
							searchOk = true;
							return false;
						}	
					});
				}
			});
		}

		if(!searchOk){
			JSONfile = '/json/milsym/O.json';
			$.ajax({
				url: JSONfile,
				dataType: 'json',
				async: false,
				success: function(data){
					$.each(Object.keys(data), function(index, item){
						var milItem = data[item];	
						if(milItem.MIL_CODE === searchData){
							milCode = milItem.MIL_CODE;
							careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
							milCodeSearch = false;
							searchOk = true;
							return false;
						}	
					});
				}
			});
		}

		if(!searchOk){
			JSONfile = '/json/milsym/E.json';
			$.ajax({
				url: JSONfile,
				dataType: 'json',
				async: false,
				success: function(data){
					$.each(Object.keys(data), function(index, item){
						var milItem = data[item];	
						if(milItem.MIL_CODE === searchData){
							milCode = milItem.MIL_CODE;
							careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
							milCodeSearch = false;
							searchOk = true;
							return false;
						}	
					});
				}
			});
		}
		sidcCreatorInit(searchData);
		saveFuncSIDC(careateSidc);
	}
}

function idMapping(optionStr) { //20200227 modifiers id mapping
	optionStr = optionStr.toUpperCase();
	if ( optionStr === "SIDCSYMBOLMODIFIER12" ) {
		return "SIDCSYMBOLMODIFIER12";
	}else if ( optionStr === "QUANTITY" ) {
		return "C";
	}else if ( optionStr === "REINFORCEDREDUCED" ) {
		return "F";
	}else if ( optionStr === "STAFFCOMMENTS" ) {
		return "G";
	}else if ( optionStr === "ADDITIONALINFORMATION" ) {
		return "H";
	}else if ( optionStr === "EVALUATIONRATING" ) {
		return "J";
	}else if ( optionStr === "COMBATEFFECTIVENESS" ) {
		return "K";
	}else if ( optionStr === "SIGNATUREEQUIPMENT" ) {
		return "L";
	}else if ( optionStr === "HIGHERFORMATION" ) {
		return "M";
	}else if ( optionStr === "HOSTILE" ) {
		return "N";
	}else if ( optionStr === "IFFSIF" ) {
		return "P";
	}else if ( optionStr === "DIRECTION" ) {
		return "Q";
	}else if ( optionStr === "UNIQUEDESIGNATION" ) {
		return "T";
	}else if ( optionStr === "TYPE" ) {
		return "V";
	}else if ( optionStr === "DTG" ) {
		return "W";
	}else if ( optionStr === "ALTITUDEDEPTH" ) {
		return "X";
	}else if ( optionStr === "LOCATION" ) {
		return "Y";
	}else if ( optionStr === "SPEED" ) {
		return "Z";
	}else if ( optionStr === "SPECIALHEADQUARTERS" ) {
		return "AA";
	}else{
		return "";
	}
}