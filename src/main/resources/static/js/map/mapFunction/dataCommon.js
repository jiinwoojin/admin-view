/**
 * save vector layer data
 */

function writeTransparentMapJSON(){
	//기본군대부호 (milsymbol 이용 데이터)
	/**
	 * 타입, 포인트 좌표, modifier, symbolCode, 사이즈
	 *  */
	
	//작전활동부호 (mission command 이용 데이터)
	/** 
	 * 타입, draw 좌표, modifier, symbolCode, format, 사이즈
	 * */
		
	//일반 도형 (openlayers draw 이용 데이터)
	/** 
	 * 타입, 좌표, 스타일
	 * */
	
	var source = selectLayer.getSource();
	var features = source.getFeatures();
	
	var multiPointFeatures = new Array();
	var singlePointFeatures = new Array();
	var generalShapeFeatures = new Array();
	
	for(var i = 0; i < features.length; i++)
	{
		var id = features[i].getId();
		if (id.split('_').length == 3) {
			var multiPointFeature = {};
			// 작전부호 or 기상 및 해양 부호
			if(id.split('_')[2] != 0)
				continue;//기본 중심점 이외의 draw point
			
			multiPointFeature.symbolCode = features[i].getProperties()['options']['symbolID'];
			var drawPoints = source.getFeatureById('P'+id.split('_')[0]).getGeometry().getCoordinates();
			if(drawPoints[0][0] != undefined){
				if(drawPoints[0][0][0] != undefined)
					drawPoints = source.getFeatureById('P'+id.split('_')[0]).getGeometry().getCoordinates()[0];
			}
			
			multiPointFeature.drawPoints = drawPoints;
			multiPointFeature.type = source.getFeatureById('P'+id.split('_')[0]).getGeometry().getType();
			multiPointFeature.size = features[i].getProperties()['strokeWidth'];
			multiPointFeature.color = features[i].getProperties()['strokeColor'];
			multiPointFeature.modifier = features[i].getProperties()['modifier'];
			multiPointFeature.options = features[i].getProperties()['options'];
			
			multiPointFeatures.push(multiPointFeature);
		} else if (id.split('_').length == 2) {
			// 포인트 좌표 (군대부호 ALL)
			var singlePointFeature = {};
			if(features[i].getProperties()['options']['sidc'] != undefined){
				singlePointFeature.symbolCode = features[i].getProperties()['options']['sidc'];
				singlePointFeature.drawPoints = source.getFeatureById('P'+id.split('_')[0]).getGeometry().getCoordinates();
				singlePointFeature.size = features[i].getProperties()['options']['size'];
				singlePointFeature.modifier = null;
				singlePointFeature.options = features[i].getProperties()['options'];
			} else if (features[i].getProperties()['options']['symbolID'] != undefined){
				singlePointFeature.symbolCode = features[i].getProperties()['options']['symbolID'];
				singlePointFeature.drawPoints = source.getFeatureById('P'+id.split('_')[0]).getGeometry().getCoordinates();
				singlePointFeature.size = features[i].getStyle()[0].getImage().getSize()[0]; 
				singlePointFeature.modifier = features[i].getProperties()['modifier'];
				singlePointFeature.options = features[i].getProperties()['options'];
			}
			
			singlePointFeatures.push(singlePointFeature);
		} else if (id.split('_').length == 1) {
			if (id.charAt(0) == 'S'){
				var generalShapeFeature = {};
				generalShapeFeature.style = {};
				// 일반도형
				if(features[i].getProperties().text != undefined){
					generalShapeFeature.drawPoints = features[i].getGeometry().getCoordinates();
					generalShapeFeature.type = features[i].getGeometry().getType();
					generalShapeFeature.style.fill = null;
					generalShapeFeature.style.stroke = null;
					generalShapeFeature.style.text = {};
					var textStyle;
					if(features[i].getStyle().length != undefined){
						textStyle = features[i].getStyle()[0].getText()
					} else {
						textStyle = features[i].getStyle().getText()
					}
					generalShapeFeature.style.text.size = textStyle.getFont().split(' ')[0].replace(/[A-z]/gi, '');
					generalShapeFeature.style.text.fillColor = textStyle.getFill().getColor();
					generalShapeFeature.style.text.strokeColor = textStyle.getStroke().getColor();
					generalShapeFeature.style.text.strokeWidth = textStyle.getStroke().getWidth();
					generalShapeFeature.text = features[i].getProperties().text;
				} else {
					generalShapeFeature.type = features[i].getGeometry().getType();
					if(generalShapeFeature.type == 'LineString'){
						generalShapeFeature.drawPoints = features[i].getGeometry().getCoordinates();
					} else if(generalShapeFeature.type == 'Circle'){
						generalShapeFeature.center = features[i].getGeometry().getCenter();
						generalShapeFeature.radius = features[i].getGeometry().getRadius();
					} else {
						generalShapeFeature.drawPoints = features[i].getGeometry().getCoordinates()[0];
					}
					if(features[i].getStyle().getFill() != null){
						generalShapeFeature.style.fill = {};
						var fillColor = features[i].getStyle().getFill().getColor();
						var fillColorOpacity = features[i].getStyle().getFill().getColor().split(',')[3].replace(')','');
						if(fillColor.indexOf('a') != -1){
							fillColor = fillColor.replace(','+fillColorOpacity+')', ')').replace('rgba', 'rgb');
						}
						generalShapeFeature.style.fill.color = fillColor;
						generalShapeFeature.style.fill.opacity = fillColorOpacity;
					} else {
						generalShapeFeature.style.fill = null;
					}
					if(features[i].getStyle().getStroke() != null){
						generalShapeFeature.style.stroke = {};
						var lineColor = features[i].getStyle().getStroke().getColor();
						var lineColorOpacity = features[i].getStyle().getStroke().getColor().split(',')[3].replace(')','');
						if(lineColor.indexOf('a') != -1){
							lineColor = lineColor.replace(','+lineColorOpacity+')', ')').replace('rgba', 'rgb');
						}
						generalShapeFeature.style.stroke.color = lineColor;
						generalShapeFeature.style.stroke.opacity = lineColorOpacity;
						generalShapeFeature.style.stroke.width = features[i].getStyle().getStroke().getWidth();
						generalShapeFeature.style.stroke.lineDash = features[i].getStyle().getStroke().getLineDash();
						generalShapeFeature.style.stroke.lineCap = features[i].getStyle().getStroke().getLineCap();
						generalShapeFeature.style.stroke.lineJoin = features[i].getStyle().getStroke().getLineJoin();
					} else {
						generalShapeFeature.style.stroke = null;
					}
					generalShapeFeature.style.text = null;
					generalShapeFeature.text = null;
				}
				
				generalShapeFeatures.push(generalShapeFeature);
			}
		}
	}
	
	var data = {};
	data.multiPointFeature = multiPointFeatures;
	data.singlePointFeature = singlePointFeatures;
	data.generalShapeFeature = generalShapeFeatures;
	data.saveCenter = map.getView().getCenter();
	data.saveZoom = map.getView().getZoom();
	
	var JSONdata = JSON.stringify(data);
	
	return JSONdata;
}


// XML Data Maker
function wirteXMLTransparentMap(){
	var source = selectLayer.getSource();
	var features = source.getFeatures();
	
	var multiPointFeatures = new Array();
	var singlePointFeatures = new Array();
	var generalShapeFeatures = new Array();
	
	for(var i = 0; i < features.length; i++)
	{
		var id = features[i].getId();
		if (id.split('_').length == 3) {
			var multiPointFeature = {};
			// 작전부호 or 기상 및 해양 부호
			if(id.split('_')[2] != 0)
				continue;//기본 중심점 이외의 draw point
			
			multiPointFeature.symbolCode = features[i].getProperties()['options']['symbolID'];
			var drawPoints = source.getFeatureById('P'+id.split('_')[0]).getGeometry().getCoordinates();
			if(drawPoints[0][0] != undefined){
				if(drawPoints[0][0][0] != undefined)
					drawPoints = source.getFeatureById('P'+id.split('_')[0]).getGeometry().getCoordinates()[0];
			}
			
			multiPointFeature.drawPoints = drawPoints;
			multiPointFeature.type = source.getFeatureById('P'+id.split('_')[0]).getGeometry().getType();
			multiPointFeature.size = features[i].getProperties()['strokeWidth'];
			multiPointFeature.color = features[i].getProperties()['strokeColor'];
			multiPointFeature.modifier = features[i].getProperties()['modifier'];
			multiPointFeature.options = features[i].getProperties()['options'];
			
			multiPointFeatures.push(multiPointFeature);
		} else if (id.split('_').length == 2) {
			// 포인트 좌표 (군대부호 ALL)
			var singlePointFeature = {};
			if(features[i].getProperties()['options']['sidc'] != undefined){
				singlePointFeature.symbolCode = features[i].getProperties()['options']['sidc'];
				singlePointFeature.drawPoints = source.getFeatureById('P'+id.split('_')[0]).getGeometry().getCoordinates();
				singlePointFeature.size = features[i].getProperties()['options']['size'];
				singlePointFeature.modifier = null;
				singlePointFeature.options = features[i].getProperties()['options'];
			} else if (features[i].getProperties()['options']['symbolID'] != undefined){
				singlePointFeature.symbolCode = features[i].getProperties()['options']['symbolID'];
				singlePointFeature.drawPoints = source.getFeatureById('P'+id.split('_')[0]).getGeometry().getCoordinates();
				singlePointFeature.size = features[i].getStyle()[0].getImage().getSize()[0]; 
				singlePointFeature.modifier = features[i].getProperties()['modifier'];
				singlePointFeature.options = features[i].getProperties()['options'];
			}
			
			singlePointFeatures.push(singlePointFeature);
		} else if (id.split('_').length == 1) {
			if (id.charAt(0) == 'S'){
				var generalShapeFeature = {};
				generalShapeFeature.style = {};
				// 일반도형
				if(features[i].getProperties().text != undefined){
					generalShapeFeature.drawPoints = features[i].getGeometry().getCoordinates();
					generalShapeFeature.type = features[i].getGeometry().getType();
					generalShapeFeature.style.fill = null;
					generalShapeFeature.style.stroke = null;
					generalShapeFeature.style.text = {};
					var textStyle;
					if(features[i].getStyle().length != undefined){
						textStyle = features[i].getStyle()[0].getText()
					} else {
						textStyle = features[i].getStyle().getText()
					}
					generalShapeFeature.style.text.size = textStyle.getFont().split(' ')[0].replace(/[A-z]/gi, '');
					generalShapeFeature.style.text.fillColor = textStyle.getFill().getColor();
					generalShapeFeature.style.text.strokeColor = textStyle.getStroke().getColor();
					generalShapeFeature.style.text.strokeWidth = textStyle.getStroke().getWidth();
					generalShapeFeature.text = features[i].getProperties().text;
				} else {
					generalShapeFeature.type = features[i].getGeometry().getType();
					if(generalShapeFeature.type == 'LineString'){
						generalShapeFeature.drawPoints = features[i].getGeometry().getCoordinates();
					} else if(generalShapeFeature.type == 'Circle'){
						var extent = features[i].getGeometry().getExtent();
						var circleExtent = new Array();
						var circleXY = new Array();
							circleXY.push(extent[0]);
							circleXY.push(extent[1]);
							circleExtent.push(circleXY);
							circleXY = new Array();
							circleXY.push(extent[2]);
							circleXY.push(extent[3]);
							circleExtent.push(circleXY);
						generalShapeFeature.extent = circleExtent;
					} else {
						generalShapeFeature.drawPoints = features[i].getGeometry().getCoordinates()[0];
					}
					if(features[i].getStyle().getFill() != null){
						generalShapeFeature.style.fill = {};
						var fillColor = features[i].getStyle().getFill().getColor();
						var fillColorOpacity = features[i].getStyle().getFill().getColor().split(',')[3].replace(')','');
						if(fillColor.indexOf('a') != -1){
							fillColor = fillColor.replace(','+fillColorOpacity+')', ')').replace('rgba', 'rgb');
						}
						generalShapeFeature.style.fill.color = fillColor;
						generalShapeFeature.style.fill.opacity = fillColorOpacity;
					} else {
						generalShapeFeature.style.fill = null;
					}
					if(features[i].getStyle().getStroke() != null){
						generalShapeFeature.style.stroke = {};
						var lineColor = features[i].getStyle().getStroke().getColor();
						var lineColorOpacity = features[i].getStyle().getStroke().getColor().split(',')[3].replace(')','');
						if(lineColor.indexOf('a') != -1){
							lineColor = lineColor.replace(','+lineColorOpacity+')', ')').replace('rgba', 'rgb');
						}
						generalShapeFeature.style.stroke.color = lineColor;
						generalShapeFeature.style.stroke.opacity = lineColorOpacity;
						generalShapeFeature.style.stroke.width = features[i].getStyle().getStroke().getWidth();
						//shape_line_type
						var line_typeJSON = {
								'solid':[1,0],'dot':[.1,10],'dash':[15,15],
								'dashDot':[10,0,10],'dashDotDot':[10,0,10,0,20]
						};
						var lineDash = features[i].getStyle().getStroke().getLineDash();
						var line_dash = '';
						if(lineDash != undefined){
							for(var key in line_typeJSON){
								if(line_typeJSON[key][0] == lineDash[0]){
									if(line_typeJSON[key][1] == lineDash[1]){
										line_dash = key;
									}
								}
							}
						}
						
						$.ajax({
						       url: CONTEXT + '/json/makeXML/shapeStyle.json',
						       dataType: 'json',
						       async: false,
						       success: function(data){
						    	   if(line_dash != ''){
						    		   var strokeDash = data['lineDashStyle'][line_dash]['code'];
						    		   if(strokeDash != undefined){
						    			   generalShapeFeature.style.stroke.lineDash = strokeDash;
						    		   }
						    	   } else {
						    		   generalShapeFeature.style.stroke.lineDash = null;
						    	   }
									var line_cap = features[i].getStyle().getStroke().getLineCap();
									if(line_cap != undefined){
										var strokeCap = data['lineDashCap'][line_cap]['code'];
										if(strokeCap != undefined){
											generalShapeFeature.style.stroke.lineCap = strokeCap;
										}
									} else {
										generalShapeFeature.style.stroke.lineCap = null;
									}
									var line_join = features[i].getStyle().getStroke().getLineJoin();
									if(line_join != undefined){
										var strokeJoin = data['lineJoin'][line_join]['code'];
										if(strokeJoin != undefined){
											generalShapeFeature.style.stroke.lineJoin = strokeJoin;
										}
									} else {
										generalShapeFeature.style.stroke.lineJoin = null;
									}
						       }
						});
						//generalShapeFeature.style.stroke.lineDash = line_dash;
						//generalShapeFeature.style.stroke.lineCap = features[i].getStyle().getStroke().getLineCap();
						//generalShapeFeature.style.stroke.lineJoin = features[i].getStyle().getStroke().getLineJoin();
					} else {
						generalShapeFeature.style.stroke = null;
					}
					generalShapeFeature.style.text = null;
					generalShapeFeature.text = null;
				}
				
				generalShapeFeatures.push(generalShapeFeature);
			}
		}
	}
	
	var data = {};
	data.multiPointFeature = multiPointFeatures;
	data.singlePointFeature = singlePointFeatures;
	data.generalShapeFeature = generalShapeFeatures;
	
	var JSONdata = JSON.stringify(data);
	
	return JSONdata;
}
