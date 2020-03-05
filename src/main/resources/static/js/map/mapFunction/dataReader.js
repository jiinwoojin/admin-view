/**
 * save Transparent Map JSON Function
 */
function createTransparentMap(file_idx){
	var transparentMapJSON = writeTransparentMapJSON();
	
	var file_info = {}
	file_info.file_name = document.getElementById('file_name').value;
	file_info.save_user = 'Tester'; //파일 저장 : 접속자 명
	
	if(file_idx == undefined){
		file_idx = '';
	} 
	
	$.ajax({
		url : '../TMap/setTMap.do',
		type : 'POST',
		data : {
			file_idx : file_idx,
			file_info : JSON.stringify(file_info),
			TMapJSON : transparentMapJSON
		},
		beforeSend: function(){
			var width = 0;
			var height = 0;
			var left = 0;
			var top = 0;

			width = 50;
			height = 50;
			
			var window_x = $(window).width();
			var window_y = $(window).height();
			
			top = ( $(window).height() - height ) / 2 + $(window).scrollTop();
			left = ( $(window).width() - width ) / 2 + $(window).scrollLeft();

			if($("#div_ajax_load_image").length != 0) {
				$("#div_ajax_load_image").css({
					"top": "0",
					"left": "0"
				});
				$("#div_ajax_load_image").show();
			}
			else {
				$('body').append('<div id="div_ajax_load_image" style="position:absolute; top: 0; left: 0; width:' + window_x + 'px; height:' + window_y + 'px; z-index:9999; padding:0; background-color : #000; opacity: 0.5;"><div class="loader" style="margin-top:'+top+'px;"></div></div>');
			}
		},
		complete: function(){
			$("#div_ajax_load_image").hide();
		},
		success: function(result){
			$('#saveTransMapWindow').hide();

			if(file_idx == ''){
				roadTMapFileList();

				var save_file = $('#list_box').children('ul').children('li')[0];
				var idx = save_file.childNodes[0].value;
				var name = save_file.childNodes[2].innerHTML;

				var new_file = $('#select_list_box').children('ul').children('li')[0];
				var newCheckboxEl = new_file.childNodes[0];
				newCheckboxEl.value = idx;
				var newLabelEl = new_file.childNodes[2];
				newLabelEl.innerHTML = name;
			}
		}
	});
}

/**
 * show Transparent Map Function
 */
function showTransparentMap(layer_name, file_idx){
	createTransparentMapLayer(layer_name, 'road');

	if(file_idx != undefined){
		$.ajax({
			url : '../TMap/getTMapData.json',
			type : 'POST',
			data : {
				file_idx : file_idx
			},
			beforeSend: function(){
				var width = 0;
				var height = 0;
				var left = 0;
				var top = 0;

				width = 50;
				height = 50;
				
				var window_x = $(window).width();
				var window_y = $(window).height();

				top = ( $(window).height() - height ) / 2 + $(window).scrollTop();
				left = ( $(window).width() - width ) / 2 + $(window).scrollLeft();

				if($("#div_ajax_load_image").length != 0) {
					$("#div_ajax_load_image").css({
						"top": "0",
						"left": "0"
					});
					$("#div_ajax_load_image").show();
				}
				else {
					$('body').append('<div id="div_ajax_load_image" style="position:absolute; top: 0; left: 0; width:' + window_x + 'px; height:' + window_y + 'px; z-index:9999; padding:0; background-color : #000; opacity: 0.5;"><div class="loader" style="margin-top:'+top+'px;"></div></div>');
				}
			},
			complete: function(){
				$("#div_ajax_load_image").hide();
			},
			success: function(data){
				var transparentMapJSON = JSON.parse(data);
				readTransparentMap(transparentMapJSON);
			}
		});
	}
}

/**
 * read and make features Transparent Map JSON Data Function
 */
function readTransparentMap(transparentMapJSON){
	var features = new Array();
	
	var formatGeoJSON = 2; // geoJSON format code
	var formatGeoSVG = 6; // svg format code
	
	var scale = mapScale(96);
	
	var jsonParser;
	if(transparentMapJSON != undefined){
		jsonParser = transparentMapJSON;
	}
	
	rendererMP.setDefaultSymbologyStandard(symStd);
	if(jsonParser['multiPointFeature'].length > 0){
		var multiPointFeatures = jsonParser['multiPointFeature'];
		for(var i = 0; i < multiPointFeatures.length; i++){
			var data = multiPointFeatures[i];
			
			var id = symbol_serial;
			symbol_serial ++;
			
			G_coordinates = new Array();
			if(data['drawPoints'][0][0] == undefined){
				getCoordinates(data['drawPoints'][0],data['drawPoints'][1]);
			} else {
				for(var p = 0; p < data['drawPoints'].length; p++){
					getCoordinates(data['drawPoints'][p][0],data['drawPoints'][p][1]);
				}
			}
			
			var coordData = setCoordinates(G_coordinates);
			var controlPoints = coordData.controlPoints;
			
			var geoJSONSymbol = rendererMP.RenderSymbol(id,"Name","Description", data['symbolCode'], controlPoints, "clampToGround", scale, '', data['modifier'], formatGeoJSON);
			
			var color = data['color'];
			var size = data['size'];
			
			var geoJSONFormat = new ol.format.GeoJSON();
			var features1 = geoJSONFormat.readFeatures(geoJSONSymbol);
			for(var j = 0; j < features1.length; j++){
				var style = new ol.style.Style({
					fill: new ol.style.Fill({ color: color }),
					stroke: new ol.style.Stroke({ color: color, width: size }),
					text: new ol.style.Text({
						text:  features1[j].N.label,
						font: '17px Calibri,sans-serif',
						fill: new ol.style.Fill({ color: color }),
						stroke: new ol.style.Stroke({
							color: '#fff', width: 3
						})
					})
				});
				features1[j].setStyle(style);
				
				features1[j].setProperties({
					'options' : data['options'],
					'drawPoints' : data['drawPoints'],
					'modifier' : data['modifier']
				});
				
				features1[j].setId(id+'_'+features1.length+'_'+j);
				
				features.push(features1[j]);
			}
			
			if(data['type'] == 'Polygon'){
				var geom = new ol.geom.Polygon(null);
				geom.setCoordinates([data['drawPoints']]);
				var PointFeature = new ol.Feature(geom);
				PointFeature.setStyle([
					new ol.style.Style({
						visibility: 'hidden'
					})
				]);
				PointFeature.setId('P'+id);
				
				features.push(PointFeature);
			} else if (data['type'] == 'LineString' || data['type'] == 'MultiLineString'){
				var geom = new ol.geom.LineString(null);
				geom.setCoordinates(data['drawPoints']);
				var PointFeature = new ol.Feature(geom);
				PointFeature.setStyle([
					new ol.style.Style({
						visibility: 'hidden'
					})
				]);
				PointFeature.setId('P'+id);
				
				features.push(PointFeature);
			} else if (data['type'] == 'Point'){
				var geom = new ol.geom.Point([data['drawPoints'][0],data['drawPoints'][1]]);
				var PointFeature = new ol.Feature(geom);
				PointFeature.setStyle([
					new ol.style.Style({
						visibility: 'hidden'
					})
				]);
				PointFeature.setId('P'+id);
				
				features.push(PointFeature);
			}
		}
	}
	
	if(jsonParser['singlePointFeature'].length > 0){
		var singlePointFeatures = jsonParser['singlePointFeature'];
		for(var i = 0; i < singlePointFeatures.length; i++){
			var data = singlePointFeatures[i];
			
			var geom = new ol.geom.Point([data['drawPoints'][0],data['drawPoints'][1]]);
			var feature = new ol.Feature(geom);
			
			var id = symbol_serial;
			symbol_serial ++;
			
			if(data['symbolCode'].charAt(0) == 'G' || data['symbolCode'].charAt(0) == 'W'){
				var Symbol = armyc2.c2sd.renderer.MilStdSVGRenderer.Render(data['symbolCode'],modifiers); // Save Graphics symbol
				
				G_coordinates = new Array();
				if(data['drawPoints'][0][0] != undefined){
					for(var p = 0; p < drawPoints.length; p++){
						getCoordinates(data['drawPoints'][p][0],data['drawPoints'][p][1]);
					}
				} else {
					getCoordinates(data['drawPoints'][0],data['drawPoints'][1]);
				}
				
				var coordData = setCoordinates(G_coordinates);
				var controlPoints = coordData.controlPoints;
				
				var modifiers = '';
				if(data['modifier'] != undefined){
					modifiers = data['modifier'];
				}
				
				var geoJSONSymbol = rendererMP.RenderSymbol(id,"Name","Description", data['symbolCode'], controlPoints, "clampToGround", scale, '', modifiers, formatGeoJSON);
				
				var mysvg = new Image();
				//mysvg.src = 'data:image/svg+xml,' + escape(Symbol.getSVG());
				
				feature.setStyle([
					new ol.style.Style({
						image: new ol.style.Icon(({
							anchor: [Symbol.getAnchorPoint().x, Symbol.getAnchorPoint().y],
							anchorXUnits: 'pixels',
							anchorYUnits: 'pixels',
							imgSize: [Math.floor(Symbol.getSVGBounds().width), Math.floor(Symbol.getSVGBounds().height)],
							src: Symbol.getSVGDataURI()
						}))
					})
				]);
				
				feature.setProperties({
					'options' : data['options'],
					'drawPoints' : data['drawPoints']
				});
				
				feature.setId(id+'_1');
				
				features.push(feature);
			} else {
				var Symbol = new ms.Symbol('');
				Symbol.setOptions(data['options']);
				
				var mysvg = new Image();
				mysvg.src = 'data:image/svg+xml,' + escape(Symbol.asSVG());
				
				feature.setStyle([
					new ol.style.Style({
						image: new ol.style.Icon(({
							anchor: [Symbol.getAnchor().x, Symbol.getAnchor().y],
							anchorXUnits: 'pixels',
							anchorYUnits: 'pixels',
							imgSize: [Math.floor(Symbol.getSize().width), Math.floor(Symbol.getSize().height)],
							//img: (mysvg)
							img: (Symbol.asCanvas())
						}))
					})
				]);
				
				feature.setProperties({
					'options' : Symbol.getOptions()
				});
				
				feature.setId(id+'_0');
				
				features.push(feature);
			}
			
			var PointFeature = new ol.Feature(geom);
			PointFeature.setStyle([
				new ol.style.Style({
					visibility: 'hidden'
				})
			]);
			PointFeature.setId('P'+id);
			
			features.push(PointFeature);
		}
	}
	
	if(jsonParser['generalShapeFeature'].length > 0){
		var generalShapeFeatures = jsonParser['generalShapeFeature'];
		for(var i = 0; i < generalShapeFeatures.length; i++){
			var data = generalShapeFeatures[i];
			
			var id = symbol_serial;
			symbol_serial ++;
			
			if(data['type'] == 'Polygon'){
				var geom = new ol.geom.Polygon(null);
				geom.setCoordinates([data['drawPoints']]);
				var PointFeature = new ol.Feature(geom);
				
				var style;
				if(data['style']['fill'] != null){
					var fillColor = data['style']['fill']['color'];
					var fill_opacity = data['style']['fill']['opacity'];
					if(fillColor.indexOf('a') == -1){
						fillColor = fillColor.replace(')', ','+fill_opacity+')').replace('rgb', 'rgba');
					}
					
					if(data['style']['stroke'] != null){
						var lineColor = data['style']['stroke']['color'];
						var line_opacity = data['style']['stroke']['opacity'];
						if(lineColor.indexOf('a') == -1){
							lineColor = lineColor.replace(')', ','+line_opacity+')').replace('rgb', 'rgba');
						}
						
						if(data['style']['stroke']['opacity'] != 0){
							style = new ol.style.Style({
								fill: new ol.style.Fill({
									color: fillColor
								}),
								stroke: new ol.style.Stroke({
									lineDash: data['style']['stroke']['lineDash'],
									lineCap: data['style']['stroke']['lineCap'],
									lineJoin: data['style']['stroke']['lineJoin'],
									color: lineColor,
									width: data['style']['stroke']['width']
								})
							});
						} else {
							style = new ol.style.Style({
								fill: new ol.style.Fill({
									color: fillColor
								}),
								stroke: new ol.style.Stroke({
									color: lineColor,
									width: data['style']['stroke']['width']
								})
							});
						}
					} else {
						style = new ol.style.Style({
							fill: new ol.style.Fill({
								color: fillColor
							})
						});
					}
				} else if(data['style']['stroke'] != null){
					var lineColor = data['style']['stroke']['color'];
					var line_opacity = data['style']['stroke']['opacity'];
					if(lineColor.indexOf('a') == -1){
						lineColor = lineColor.replace(')', ','+line_opacity+')').replace('rgb', 'rgba');
					}
					
					if(data['style']['stroke']['opacity'] != 0){
						style = new ol.style.Style({
							stroke: new ol.style.Stroke({
								lineDash: data['style']['stroke']['lineDash'],
								lineCap: data['style']['stroke']['lineCap'],
								lineJoin: data['style']['stroke']['lineJoin'],
								color: lineColor,
								width: data['style']['stroke']['width']
							})
						});
					} else {
						style = new ol.style.Style({
							stroke: new ol.style.Stroke({
								color: lineColor,
								width: data['style']['stroke']['width']
							})
						});
					}
				} else {
					continue;
				}
				
				PointFeature.setStyle(style);
				PointFeature.setId('S'+id);
				
				features.push(PointFeature);
			} else if (data['type'] == 'LineString'){
				var geom = new ol.geom.LineString(null);
				geom.setCoordinates(data['drawPoints']);
				var PointFeature = new ol.Feature(geom);
				
				var style;
				if(data['style']['stroke'] != null){
					var lineColor = data['style']['stroke']['color'];
					var line_opacity = data['style']['stroke']['opacity'];
					if(lineColor.indexOf('a') == -1){
						lineColor = lineColor.replace(')', ','+line_opacity+')').replace('rgb', 'rgba');
					}
					
					if(data['style']['stroke']['opacity'] != 0){
						style = new ol.style.Style({
							stroke: new ol.style.Stroke({
								lineDash: data['style']['stroke']['lineDash'],
								lineCap: data['style']['stroke']['lineCap'],
								color: lineColor,
								width: data['style']['stroke']['width']
							})
						});
					} else {
						style = new ol.style.Style({
							stroke: new ol.style.Stroke({
								color: lineColor,
								width: data['style']['stroke']['width']
							})
						});
					}
				} else {
					continue;
				}
				
				PointFeature.setStyle(style);
				PointFeature.setId('S'+id);
				
				features.push(PointFeature);
			} else if (data['type'] == 'Point'){
				var align = 'center';
				var baseline = 'middle';
				var text_line_size = parseInt(data['style']['text']['strokeWidth'], 10);
				
				var geom = new ol.geom.Point([data['drawPoints'][0],data['drawPoints'][1]]);
				var PointFeature = new ol.Feature(geom);
				PointFeature.setStyle([
					new ol.style.Style({
						text : new ol.style.Text({
							textAlign: align,
							textBaseline: baseline,
							font: data['style']['text']['size'] +'px Calibri,sans-serif',
							text: data['text'],
							fill: new ol.style.Fill({ color: data['style']['text']['fillColor'] }),
							stroke: new ol.style.Stroke({
								color: data['style']['text']['strokeColor'], width: text_line_size
							})
						})
					})
				]);
				PointFeature.setProperties({'text':data['text']});
				PointFeature.setId('S'+id);
				
				features.push(PointFeature);
			} else if (data['type'] == 'Circle'){
				var geom = new ol.geom.Circle(data['center'], data['radius']);
				var PointFeature = new ol.Feature(geom);
				
				var style;
				if(data['style']['fill'] != null){
					var fillColor = data['style']['fill']['color'];
					var fill_opacity = data['style']['fill']['opacity'];
					if(fillColor.indexOf('a') == -1){
						fillColor = fillColor.replace(')', ','+fill_opacity+')').replace('rgb', 'rgba');
					}
					
					if(data['style']['stroke'] != null){
						var lineColor = data['style']['stroke']['color'];
						var line_opacity = data['style']['stroke']['opacity'];
						if(lineColor.indexOf('a') == -1){
							lineColor = lineColor.replace(')', ','+line_opacity+')').replace('rgb', 'rgba');
						}
						
						if(data['style']['stroke']['opacity'] != 0){
							style = new ol.style.Style({
								fill: new ol.style.Fill({
									color: fillColor
								}),
								stroke: new ol.style.Stroke({
									lineDash: data['style']['stroke']['lineDash'],
									lineCap: data['style']['stroke']['lineCap'],
									lineJoin: data['style']['stroke']['lineJoin'],
									color: lineColor,
									width: data['style']['stroke']['width']
								})
							});
						} else {
							style = new ol.style.Style({
								fill: new ol.style.Fill({
									color: fillColor
								}),
								stroke: new ol.style.Stroke({
									color: lineColor,
									width: data['style']['stroke']['width']
								})
							});
						}
					} else {
						style = new ol.style.Style({
							fill: new ol.style.Fill({
								color: fillColor
							})
						});
					}
				} else if(data['style']['stroke'] != null){
					var lineColor = data['style']['stroke']['color'];
					var line_opacity = data['style']['stroke']['opacity'];
					if(lineColor.indexOf('a') == -1){
						lineColor = lineColor.replace(')', ','+line_opacity+')').replace('rgb', 'rgba');
					}
					
					if(data['style']['stroke']['opacity'] != 0){
						style = new ol.style.Style({
							stroke: new ol.style.Stroke({
								lineDash: data['style']['stroke']['lineDash'],
								lineCap: data['style']['stroke']['lineCap'],
								lineJoin: data['style']['stroke']['lineJoin'],
								color: lineColor,
								width: data['style']['stroke']['width']
							})
						});
					} else {
						style = new ol.style.Style({
							stroke: new ol.style.Stroke({
								color: lineColor,
								width: data['style']['stroke']['width']
							})
						});
					}
				} else {
					continue;
				}
				
				PointFeature.setStyle(style);
				PointFeature.setId('S'+id);
				
				features.push(PointFeature);
			}
		}
	}
	
	if(jsonParser['saveCenter'] != undefined){
		drawTransparentMap(features, jsonParser['saveCenter'], jsonParser['saveZoom']);
	} else {
		drawTransparentMap(features);
	}
}

/**
 * draw Transparent Map Function
 */
function drawTransparentMap(features, center, zoom){
	selectLayer.getSource().addFeatures(features);
	
	if(center != undefined){
		map.getView().setCenter(center);
		map.getView().setZoom(zoom);
	}
}

/**
 * remove and create Transparent Map Function
 */
function modifyTransparentMap(file_idx){
	var file_info = {};
	file_info.file_name = document.getElementById('file_name').value;
	file_info.TMapJSON = writeTransparentMapJSON();
	
	if(file_info.file_name != ''){
		$.ajax({
			url : '../TMap/modTMap.do',
			type : 'POST',
			data : {
				file_idx : file_idx,
				file_info : JSON.stringify(file_info) 
			},
			beforeSend: function(){
				var width = 0;
				var height = 0;
				var left = 0;
				var top = 0;

				width = 50;
				height = 50;
				
				var window_x = $(window).width();
				var window_y = $(window).height();

				top = ( $(window).height() - height ) / 2 + $(window).scrollTop();
				left = ( $(window).width() - width ) / 2 + $(window).scrollLeft();

				if($("#div_ajax_load_image").length != 0) {
					$("#div_ajax_load_image").css({
						"top": "0",
						"left": "0"
					});
					$("#div_ajax_load_image").show();
				}
				else {
					$('body').append('<div id="div_ajax_load_image" style="position:absolute; top: 0; left: 0; width:' + window_x + 'px; height:' + window_y + 'px; z-index:9999; padding:0; background-color : #000; opacity: 0.5;"><div class="loader" style="margin-top:'+top+'px;"></div></div>');
				}
			},
			complete: function(){
				$("#div_ajax_load_image").hide();
			},
			success: function(data){
				roadTMapFileList();
				var checkList = $('#select_list_box>ul>li>input[type=checkbox]');
				
				for(var i=0; i < checkList.length; i++){
					if(checkList[i].value == file_idx){
						checkList[i].parentElement.children[2].innerText = file_info.file_name;
					}
				}
			}
		});
	} else {
		alert('파일명을 입력해주세요.');
	}
}

/**
 * checked file list to delete Transparent Map Function
 */
function deleteTransparentMap(file_idx){
	if(confirm('선택한 파일을 삭제하시겠습니까?')){
		var files_idx = {};
		files_idx.file_idx = [];
		if(file_idx == undefined){
			var checkList = $('#list_box>ul>li>input[type=checkbox]');
			
			for(var i = 0; i < checkList.length; i++){
				if(checkList[i].checked){
					var checkData = checkList[i].value;
					files_idx.file_idx.push(checkData);
				}
			}
		} else {
			files_idx.file_idx.push(file_idx);
		}
		
		if(files_idx.file_idx.length > 0){
			$.ajax({
				url : '../TMap/delTMap.do',
				type : 'POST',
				data : {
					file_idx : JSON.stringify(files_idx),
					del_user : 'Tester'//파일 삭제 : 접속자 명
				},
				beforeSend: function(){
					var width = 0;
					var height = 0;
					var left = 0;
					var top = 0;

					width = 50;
					height = 50;
					
					var window_x = $(window).width();
					var window_y = $(window).height();

					top = ( $(window).height() - height ) / 2 + $(window).scrollTop();
					left = ( $(window).width() - width ) / 2 + $(window).scrollLeft();

					if($("#div_ajax_load_image").length != 0) {
						$("#div_ajax_load_image").css({
							"top": "0",
							"left": "0"
						});
						$("#div_ajax_load_image").show();
					}
					else {
						$('body').append('<div id="div_ajax_load_image" style="position:absolute; top: 0; left: 0; width:' + window_x + 'px; height:' + window_y + 'px; z-index:9999; padding:0; background-color : #000; opacity: 0.5;"><div class="loader" style="margin-top:'+top+'px;"></div></div>');
					}
				},
				complete: function(){
					$("#div_ajax_load_image").hide();
				},
				success: function(data){
					roadTMapFileList();
				}
			});
		}
	}
}