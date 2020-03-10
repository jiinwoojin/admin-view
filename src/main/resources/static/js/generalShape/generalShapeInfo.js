/**
 * general shape style Information javascript
 */

function shapeInfoViewer(){
	var viewer = $('#shape_style_info');
	
	viewer.fadeIn();
	
	var divTop = $('#topMenu').outerHeight() + $('.content-header').outerHeight() + $('#shape_info').outerHeight() + $('#text_box').innerHeight();
	
	viewer.css({
		'top': divTop,
		'right': '10px',
		'position': 'absolute',
		'padding' : '3px 7px',
		'background-color': 'rgba(250, 250, 255, 1)',
		//'background-color': '#ecf0f5',
		'border': '1px solid #000', 
		'border-radius': '5px;',
		'z-index': '999'
	}).show();
	
	$('#shape_fill_color').colorpicker();
	$('#shape_line_color').colorpicker();
	$('#shape_text_color').colorpicker();
	$('#shape_text_line_color').colorpicker();
	
	//default color
	$('#shape_fill_color').val('rgb(255, 255, 255)');
	$('#shape_line_color').val('rgb(0, 0, 0)');
	$('#shape_text_color').val('rgb(0, 0, 0)');
	$('#shape_text_line_color').val('rgb(255, 255, 255)');
	
	dragElement(document.getElementById('shape_style_info'), 'generalShapeInfo');
}

$('#shapeInfo_close').on('click', function(){
	$('#shape_style_info').hide();
});

// 채움색,크기
function shapeStyle(select_feature, text){
	//shape_fill
	var use = $('#shape_fill').is(':checked');
	//shape_fill_color
	var fill_color = $('#shape_fill_color').val();
	//shape_fill_opacity
	var fill_opacity = $('#shape_fill_opacity').val();
	
	//shape_line_color
	var line_color = $('#shape_line_color').val();
	//shape_line_size
	var line_size = $('#shape_line_size').val();
	//shape_line_opacity
	var line_opacity = $('#shape_line_opacity').val();
	
	//shape_line_type
	var line_typeJSON = {
			'solid':[1,0],'dot':[.1,10],'dash':[15,15],
			'dashDot':[10,0,10],'dashDotDot':[10,0,10,0,20]
	};
	
	var line_type = $('#shape_line_type').val();
	var line_offset = 0;
	if(line_type == 'none'){
		$('#shape_fill_color').val('rgb(255, 255, 255)');
		line_color = 'rgb(255, 255, 255)';
		$('#shape_fill_opacity').val(1);
		line_opacity = 1;
		$('#shape_fill').prop("checked", true);
		use = true;
	} else if (line_type == 'hidden'){
		$('#shape_line_opacity').val(0);
		$('#shape_line_opacity').parent().children('span').html(0);
		line_opacity = 0;
	} else {
		if(line_opacity == 0){
			$('#shape_line_opacity').val(1);
			line_opacity = 1;
		}
		
		line_type = line_typeJSON[line_type];
	}
	
	//shape_endLine_type
	var endLine_type = $('#shape_endLine_type').val();
	
	//shape_joinLine_type
	var joinLine_type = $('#shape_joinLine_type').val();
	
	//shape_text_color
	var text_color = $('#shape_text_color').val();
	//shape_text_size
	var text_size = $('#shape_text_size').val();
	//shape_text_line_color
	var text_line_color = $('#shape_text_line_color').val();
	//shape_text_line_size
	var text_line_size = $('#shape_text_line_size').val();
	
	var fillColor;
	if(fill_color.indexOf('a') == -1){
		fillColor = fill_color.replace(')', ','+fill_opacity+')').replace('rgb', 'rgba');
	}
	
	var lineColor;
	if(line_color.indexOf('a') == -1){
		lineColor = line_color.replace(')', ','+line_opacity+')').replace('rgb', 'rgba');
	}
	
	var feature;
	if(select_feature != undefined){
		feature = select_feature;
	} else if (select_sp_id != undefined) {
		feature = selectLayer.getSource().getFeatureById(select_sp_id);
	}
	
	var label = '';
	if(text != undefined){
		$('#shape_fill').prop("checked", false);
		use = false;
		label = text;
	} else if(feature != undefined){
		if(feature.getProperties().text != undefined){
			label = feature.getProperties().text;
		}
	}
	
	var style;
	if (label != '') {
		var align = 'center';
		var baseline = 'middle';
		text_line_size = parseInt(text_line_size, 10);

		style = new ol.style.Style({
			text : new ol.style.Text({
				textAlign: align,
				textBaseline: baseline,
				font: text_size +'px Calibri,sans-serif',
				text: label,
				fill: new ol.style.Fill({ color: text_color }),
				stroke: new ol.style.Stroke({
					color: text_line_color, width: text_line_size
				})
			})
		});
	}else if(use){
		if (line_type == 'none') {
			style = new ol.style.Style({
				fill: new ol.style.Fill({
					color: fillColor
				})
			});
		} else if (line_type == 'hidden'){
			style = new ol.style.Style({
				fill: new ol.style.Fill({
					color: fillColor
				}),
				stroke: new ol.style.Stroke({
					color: lineColor,
					width: line_size
				})
			});
		} else {
			style = new ol.style.Style({
				fill: new ol.style.Fill({
					color: fillColor
				}),
				stroke: new ol.style.Stroke({
					lineDash: line_type,
					lineCap: endLine_type,
					lineJoin: joinLine_type,
					color: lineColor,
					width: line_size
				})
			});
		}
	} else {
		if (line_type == 'hidden'){
			style = new ol.style.Style({
				stroke: new ol.style.Stroke({
					color: lineColor,
					width: line_size
				})
			});
		} else {
			style = new ol.style.Style({
				stroke: new ol.style.Stroke({
					lineDash: line_type,
					lineCap: endLine_type,
					lineJoin: joinLine_type,
					color: lineColor,
					width: line_size
				})
			});
		}
	} 
	
	if(feature != undefined){
		feature.setStyle(style);
	}
	
	if(feature.getStyle() != null){
		if(feature.getStyle().getText() != null){
			$('.text_style_box').show();
			$('.shape_style_box').hide();
		} else {
			$('.text_style_box').hide();
			$('.shape_style_box').show();
		}
	}
}

function getShapeStyle(select_feature){
	if(select_feature.getStyle().getText() != null){
		var textStyle;
		if(select_feature.getStyle().length != undefined){
			textStyle = select_feature.getStyle()[0];
		} else {
			textStyle = select_feature.getStyle();
		}
		//shape_text_color
		var textColor = textStyle.getText().getFill().getColor();
		$('#shape_text_color').val(textColor);
		//shape_text_size
		var size = textStyle.getText().getFont().split(' ')[0].replace(/[A-z]/gi, '');
		$('#shape_text_size').val(size);
		$('#shape_text_size').parent().children('span').html(size);
		//shape_text_line_color
		var textLineColor = textStyle.getText().getStroke().getColor();
		$('#shape_text_line_color').val(textLineColor);
		//shape_text_line_size
		var textLineSize = textStyle.getText().getStroke().getWidth();
		$('#shape_text_line_size').val(textLineSize);
		$('#shape_text_line_size').parent().children('span').html(textLineSize);
		
		$('.text_style_box').show();
		$('.shape_style_box').hide();
	} else {
		if(select_feature.getStyle().getFill() != null){
			//shape_fill
			$('#shape_fill').prop("checked", true);
			//shape_fill_color
			var fillColor = select_feature.getStyle().getFill().getColor();
			var fillColorOpacity = select_feature.getStyle().getFill().getColor().split(',')[3].replace(')','');
			
			if(fillColor.indexOf('a') != -1){
				fillColor = fillColor.replace(','+fillColorOpacity+')', ')').replace('rgba', 'rgb');
			}
			$('#shape_fill_color').val(fillColor);
			//shape_fill_opacity
			$('#shape_fill_opacity').val(fillColorOpacity);
			$('#shape_fill_opacity').parent().children('span').html(fillColorOpacity);
		}
		if(select_feature.getStyle().getStroke() != null){
			//shape_line_color
			var lineColor = select_feature.getStyle().getStroke().getColor();
			var lineColorOpacity = select_feature.getStyle().getStroke().getColor().split(',')[3].replace(')','');
			
			if(lineColor.indexOf('a') != -1){
				lineColor = lineColor.replace(','+lineColorOpacity+')', ')').replace('rgba', 'rgb');
			}
			$('#shape_line_color').val(lineColor);
			//shape_line_size
			var lineSize = select_feature.getStyle().getStroke().getWidth();
			$('#shape_line_size').val(lineSize);
			$('#shape_line_size').parent().children('span').html(lineSize);
			//shape_line_opacity
			$('#shape_line_opacity').val(lineColorOpacity);
			$('#shape_line_opacity').parent().children('span').html(lineColorOpacity);
			//shape_line_type
			var line_typeJSON = {
					'solid':[1,0],'dot':[.1,10],'dash':[15,15],
					'dashDot':[10,0,10],'dashDotDot':[10,0,10,0,20]
			};
			
			var lineType = '';
			for(var key in line_typeJSON){
				var lineDash = select_feature.getStyle().getStroke().getLineDash();
				if(line_typeJSON[key][0] == lineDash[0]){
					if(line_typeJSON[key][1] == lineDash[1]){
						lineType = key;
					}
				}
			}

			var lineTypeSelection = $('#shape_line_type');
			for(var i in lineTypeSelection){
				if(isNaN(i))break;
				var select_option = lineTypeSelection[0].children[i].value;
				if (select_option == lineType) {
					lineTypeSelection[0].selectedIndex = i;
					break;
				}
			}
			
			//shape_endLine_type
			var endLineType = select_feature.getStyle().getStroke().getLineCap();
			var endLineTypeSelection = $('#shape_endLine_type');
			for(var i in endLineTypeSelection){
				if(isNaN(i))break;
				var select_option = endLineTypeSelection[0].children[i].value;
				if (select_option == endLineType) {
					endLineTypeSelection[0].selectedIndex = i;
					break;
				}
			}
			
			//shape_joinLine_type
			var joinLineType = select_feature.getStyle().getStroke().getLineJoin();
			var joinLineTypeSelection = $('#shape_joinLine_type');
			for(var i in joinLineTypeSelection){
				if(isNaN(i))break;
				var select_option = joinLineTypeSelection[0].children[i].value;
				if (select_option == joinLineType) {
					joinLineTypeSelection[0].selectedIndex = i;
					break;
				}
			}
		}
		
		$('.text_style_box').hide();
		$('.shape_style_box').show();
	}
}