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
		if( sidc.substring(10,11) != "-" && sidc.substring(10,11) != "*"		//20200211
		    && sidc.substring(10,11) != "M" && sidc.substring(10,11) != "N") {  //20200304 
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
$('#mil_body').ready(function() {
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
		var JSONfile = CONTEXT + '/json/milsym/S.json';
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
				JSONfile = CONTEXT + '/json/milsym/G.json';
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
				JSONfile = CONTEXT + '/json/milsym/W.json';
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
				JSONfile = CONTEXT + '/json/milsym/I.json';
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
				JSONfile = CONTEXT + '/json/milsym/O.json';
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
				JSONfile = CONTEXT + '/json/milsym/E.json';
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
	};

	$('#search_sidc_btn').click(function() {
		var searchData = $('input[name=search_sidc_text]').val();
		if(searchData != ''){
			searchSidcKey(searchData);
		}
	});

	$('input[name=search_sidc_text]').keydown(function(key) {
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
    $(".useChk").on("click",function() {
        var $this = $(this);
        var $tar = $this.closest(".modal-body").find(".useChk-content");
        if($this.is(":checked")){
            $tar.show();
        }else{
            $tar.hide();
        }
    });

    //색상코드표 지정색
    $(".colorList__box").on("click",function() {
        var $this = $(this);
        var $thisVal = $this.find("input").val();
        $this.addClass("-active").siblings().removeClass("-active");
        $this.closest(".colorList").find(".mil-colorpicker,.mil-colorpicker--transparent").colorpicker('setValue',$thisVal);
    });

    $(".colorList .input-group-addon").on("click",function() {
        $(this).closest(".colorList").find(".colorList__box").removeClass("-active");
    });

    $(".colorList__chkBox").on("click",function() {
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
	var mapProperties = getMapProperties();//20200304

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
			'options' : symbol.getOptions(),
			'mapProperties' : mapProperties//20200304
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
				'drawPoints' : G_coordinates,
				'mapProperties' : mapProperties//20200304
			});

			if(id != null){
				feature.setId(id+'_1');
			}
			selectLayer.getSource().addFeature(feature);

		} else {
			/* // style
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
			selectLayer.getSource().addFeatures(features1); */

			// style
			var color = SymbolUtilities.getLineColorOfAffiliation(JSON.parse(mygeoJSON)["properties"]["symbolID"]).toHexString(false);//20200303
			var size = 4;

			var geoJSONFormat = new ol.format.GeoJSON();
			var features1 = geoJSONFormat.readFeatures(mygeoJSON);
			for(var i = 0; i < features1.length; i++){
				var color1 = null;
				var color2 = null;
				if ( $("input:checkbox[id='MonoColorChk']").is(":checked") ) {
					color1 = $('#MonoColor1').val();
					color2 = $('#MonoColor2').val();
				}
				var style = new ol.style.Style({
					fill: new ol.style.Fill({ color: (color2 === null ? color : color2)}),
					stroke: new ol.style.Stroke({ color: (color1 === null ? color : color1), width: 3 }),
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
					'modifier' : G_modifiers,
					'mapProperties' : mapProperties//20200304
				});
				if(id != null){
					features1[i].setId(id+'_'+features1.length+'_'+i);
				}
			}
			selectLayer.getSource().addFeatures(features1);
		}

	}
}

var selectFeatureId;
var selectFeatureId_arr;
var interaction;
var modify;
var snap;

function selectedSymbol(searchData) {//20200207
	var careateSidc = '';
	var milCode = '';
	var searchOk = false;
	if(searchData != ''){
		searchData = milcodeChange(searchData);
		var JSONfile = CONTEXT + '/json/milsym/S.json';
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
			JSONfile = CONTEXT + '/json/milsym/G.json';
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
			JSONfile = CONTEXT + '/json/milsym/W.json';
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
			JSONfile = CONTEXT + '/json/milsym/I.json';
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
			JSONfile = CONTEXT + '/json/milsym/O.json';
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
			JSONfile = CONTEXT + '/json/milsym/E.json';
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

function getMapProperties(){//20200304
	var propNm = ['SIDCAFFILIATION', 'SIDCSTATUS', 'Size', 'FillOpacity', 'MonoColorChk', 'MonoColor1', 'MonoColor2', 'StrokeWidth', 'input006', 'input007', 'DisplayIcon', 'Frame', 'Fill', 'CivilianColors'];
	var mapProperties = {};

	for (var i = 0; i < propNm.length; i++) {		
		if (propNm[i] === 'SIDCAFFILIATION' || propNm[i] === 'SIDCSTATUS' || propNm[i] === 'input006' ) {
			if ($('#'+propNm[i]) !== undefined) {
				mapProperties[propNm[i]] = $('#'+propNm[i] + ' option:selected').val();
			}
			//$('#'+propNm[i]).val("1").prop("selected", true);
		}else if ( propNm[i] === 'MonoColorChk' 
			|| propNm[i] === 'DisplayIcon' || propNm[i] === 'Frame' 
			|| propNm[i] === 'Fill' || propNm[i] === 'CivilianColors' ) {
			if ($('#'+propNm[i]) !== undefined) {
				mapProperties[propNm[i]] = $('input:checkbox[id='+propNm[i] +']').is(':checked');
			}
			//$('input:checkbox[id='+propNm[i] +']').prop("checked", true);
		}else{
			if ($('#'+propNm[i]) !== undefined) {
				mapProperties[propNm[i]] = $('#'+propNm[i]).val();					
			}
		}
	}
	return mapProperties;
}

function setMapProperties(mapProperties, milCode){//20200304
	for(var propNm in mapProperties) {
		propVal = mapProperties[propNm];
		if (propNm === 'SIDCAFFILIATION' || propNm === 'SIDCSTATUS' || propNm === 'input006' ) {
			if ($('#'+propNm) !== undefined) {
				$('#'+propNm).val(propVal).prop("selected", true);
			}			
		}else if ( propNm === 'MonoColorChk' 
			|| propNm === 'DisplayIcon' || propNm === 'Frame' 
			|| propNm === 'Fill' || propNm === 'CivilianColors' ) {
			if ($('#'+propNm) !== undefined) {
				$('input:checkbox[id='+propNm +']').prop("checked", propVal);
				if ( propNm === 'MonoColorChk' ) {
					if ( propVal) {
						if ( milCode === "G") {
							$('.MonoColor2').removeAttr('style');
						}else{// S
							$('.MonoColor2').attr('style', "display:none;");
						}
						$('#'+propNm).parent().parent().next().removeAttr('style');
					}else{
						$('#'+propNm).parent().parent().next().attr('style', "display:none;");
					}				
				}
			}
		}else if ( propNm === 'Size' || propNm === 'FillOpacity' || propNm === 'StrokeWidth') {
			if ($('#'+propNm) !== undefined) {
				$('#'+propNm).val(propVal);	
				$('#'+propNm).next("span").html(propVal);
			}
		}else if ( propNm === 'MonoColor1' || propNm === 'MonoColor2') {
			if ($('#'+propNm) !== undefined) {
				$('#'+propNm).val(propVal);	
				$('#'+propNm).closest(".colorList").find(".colorList__box").removeClass("-active");
				var colorCnt = $('#'+propNm).closest(".colorList").find(".colorList__box").length;
				for(var i=0; i<colorCnt; i++){  
					var $this = $('#'+propNm).closest(".colorList").find(".colorList__box").eq(i);                        
					var $thisVal = $this.find("input").val();
					if (  $thisVal === propVal) {
						$this.addClass("-active");
					}
				}
				$('#'+propNm).closest(".colorList").find(".mil-colorpicker,.mil-colorpicker--transparent").colorpicker('setValue',propVal);
			}
		}else{
			if ($('#'+propNm) !== undefined) {
				$('#'+propNm).val(propVal);					
			}
		}
	}
	drawSymbol();
}