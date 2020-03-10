
/**
 * General Shape Page Event
 * */

function shapeViewer(){
	var viewer = $('#shape_info');
	
	viewer.fadeIn();
	
	var divTop = $('#topMenu').outerHeight() + $('.content-header').outerHeight();
	
	viewer.css({
		'top': divTop,
		'right': '10px',
		'position': 'absolute',
		'padding' : '3px',
		'background-color': 'rgba(250, 250, 255, 1)', 
		'border-radius': '5px;',
		'z-index': '999'
	}).show();
	
	$('#symbol_info').css('top', divTop + 39);
}


$('#general_shape_btn').on('click', function(event) {
	shapeViewer();
	shapeInfoViewer();
});

var draw_sp;
var select_sp_id;
function draw_shape(e){
	$('#text_box').hide();
	
	if(draw_sp != undefined)
		map.removeInteraction(draw_sp);
	
	var draw_type = e.id;
	var geometryFunction;
	var maxPoints;
	if(draw_type == 'Triangle'){
		draw_type = 'Circle';
		geometryFunction = ol.interaction.Draw.createRegularPolygon(3);
	} else if(draw_type == 'Square'){
		draw_type = 'Circle';
		geometryFunction = ol.interaction.Draw.createRegularPolygon(4);
	} else if(draw_type == 'Box'){
		draw_type = 'Circle';
		geometryFunction = ol.interaction.Draw.createBox();
	} else if(draw_type == 'Line'){
		draw_type = 'LineString';
		maxPoints = 2;
	}
	
	if(geometryFunction != undefined){
		draw_sp = new ol.interaction.Draw({
			source : selectLayer.getSource(),
			type : draw_type,
			geometryFunction : geometryFunction
		});
	} else {
		if(maxPoints != undefined) {
			draw_sp = new ol.interaction.Draw({
				source: selectLayer.getSource(),
				type: draw_type,
				maxPoints: maxPoints
			});
		} else {
			draw_sp = new ol.interaction.Draw({
				source: selectLayer.getSource(),
				type: draw_type
			});
		}
	}
	
	var draw_id = 'S' + symbol_serial;
	symbol_serial ++;
	
	draw_sp.on('drawend', function(e) {
		shapeStyle(e.feature);
		e.feature.setId(draw_id);
		
		select_sp_id = draw_id;
		$('#shape_style_info').show();
		
		map.removeInteraction(draw_sp);
	});
	map.addInteraction(draw_sp);
}

$('#shape_close').on('click', function(){
	$('#text_box').hide();
	$('#shape_info').hide();
	$('#shape_style_info').hide();
	
	if($('#shape_info').css('top') != 'auto'){
		$('#symbol_info').css('top', ($('#shape_info').css('top').replace(/[A-z]/gi,'')*1));
	}
});

$('#shape_style').on('click', function(){
	$('#shape_style_info').toggle();
});

$('#Text').on('click', function(){
	if(draw_sp != undefined)
		map.removeInteraction(draw_sp);
	
	var viewer = $('#text_box');
	
	viewer.css({
		'width' : '350px',
		'padding' : '3px',
		'background-color': '#ffffff'
	}).show();
});

function setText(){
	draw_sp = new ol.interaction.Draw({
		source : selectLayer.getSource(),
		type : 'Point'
	});
	
	var draw_id = 'S' + symbol_serial;
	symbol_serial ++;
	
	draw_sp.on('drawend', function(e) {
		var textVal = $('#shapeText').val();

		shapeStyle(e.feature, textVal);
		e.feature.setProperties({'text':textVal});
		e.feature.setId(draw_id);
		
		select_sp_id = draw_id;
		$('#shape_style_info').show();
		
		map.removeInteraction(draw_sp);
	});
	map.addInteraction(draw_sp);
}



// ol-ext source
/** Style the transform handles for the current interaction
*/
function setHandleStyle(){
	if (!interaction instanceof ol.interaction.Transform) return;
	if ($("#style").prop('checked'))
	{	// Style the rotate handle
		var circle = new ol.style.RegularShape({
						fill: new ol.style.Fill({color:[255,255,255,0.01]}),
						stroke: new ol.style.Stroke({width:1, color:[0,0,0,0.01]}),
						radius: 8,
						points: 10
					});
		interaction.setStyle('rotate',
				new ol.style.Style(
				{	text: new ol.style.Text (
						{	text:'\uf0e2', 
							font:"16px Fontawesome",
							textAlign: "left",
							fill:new ol.style.Fill({color:'red'})
						}),
					image: circle
				}));
		// Center of rotation
		interaction.setStyle('rotate0',
				new ol.style.Style(
				{	text: new ol.style.Text (
						{	text:'\uf0e2', 
							font:"20px Fontawesome",
							fill: new ol.style.Fill({ color:[255,255,255,0.8] }),
							stroke: new ol.style.Stroke({ width:2, color:'red' })
						}),
				}));
		// Style the move handle
		interaction.setStyle('translate',
				new ol.style.Style(
				{	text: new ol.style.Text (
						{	text:'\uf047', 
							font:"20px Fontawesome", 
							fill: new ol.style.Fill({ color:[255,255,255,0.8] }),
							stroke: new ol.style.Stroke({ width:2, color:'red' })
						})
				}));
		// Style the strech handles
		/* uncomment to style * /
		interaction.setStyle ('scaleh1', 
				new ol.style.Style(
				{	text: new ol.style.Text (
						{	text:'\uf07d', 
							font:"bold 20px Fontawesome", 
							fill: new ol.style.Fill({ color:[255,255,255,0.8] }),
							stroke: new ol.style.Stroke({ width:2, color:'red' })
						})
				}));
		interaction.style.scaleh3 = interaction.style.scaleh1;
		interaction.setStyle('scalev',
				new ol.style.Style(
				{	text: new ol.style.Text (
						{	text:'\uf07e', 
							font:"bold 20px Fontawesome", 
							fill: new ol.style.Fill({ color:[255,255,255,0.8] }),
							stroke: new ol.style.Stroke({ width:2, color:'red' })
						})
				}));
		interaction.style.scalev2 = interaction.style.scalev;
		/**/
	}
	else{
		interaction.setDefaultStyle();
	}
	// Refresh
	interaction.set('translate', interaction.get('translate'));
};

/** Set properties
*/
function setAspectRatio (p){	
	interaction.set("keepAspectRatio", function(e){ return e.originalEvent.shiftKey });
}

