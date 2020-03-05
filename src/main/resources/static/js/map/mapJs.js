/**
 * Define a namespace for the application.
 */
window.app = {};
var app = window.app;

/**
 * @constructor
 * @extends {ol.interaction.Pointer}
 */
app.Drag = function() {

	ol.interaction.Pointer.call(this, {
		handleDownEvent: app.Drag.prototype.handleDownEvent,
		handleDragEvent: app.Drag.prototype.handleDragEvent,
		handleMoveEvent: app.Drag.prototype.handleMoveEvent,
		handleUpEvent: app.Drag.prototype.handleUpEvent
	});

	/**
	 * @type {ol.Pixel}
	 * @private
	 */
	this.coordinate_ = null;

	/**
	 * @type {string|undefined}
	 * @private
	 */
	this.cursor_ = 'pointer';

	/**
	 * @type {ol.Feature}
	 * @private
	 */
	this.feature_ = null;

	/**
	 * @type {string|undefined}
	 * @private
	 */
	this.previousCursor_ = undefined;

};
ol.inherits(app.Drag, ol.interaction.Pointer);


/**
 * @param {ol.MapBrowserEvent} evt Map browser event.
 * @return {boolean} `true` to start the drag sequence.
 */
app.Drag.prototype.handleDownEvent = function(evt) {
	var map = evt.map;

	var layer1;
	var feature1 = map.forEachFeatureAtPixel(evt.pixel,
			function(feature1, layer) {
		layer1 = layer;

		return feature1;
	});

	if(layer1){
		if(layer1.get('name') != selectLayer.get('name')){
			return;
		}
	}

	if (feature1) {
		this.coordinate_ = evt.coordinate;
		this.feature_ = feature1;

		if(feature1.getId() != undefined){
			if(feature1.getId().charAt(0) == 'S'){
				if(interaction != undefined && interaction.features_ != undefined){
					if(interaction.features_.getArray()[0].getId() == feature1.getId()){
						return;
					} else {
						map.removeInteraction(interaction);
					}
				}

				modifyCancel(); // 군대부호 수정 중 일반도형 수정모드를 선택할 경우 군대부호 수정모드 Cancel

				var generalFeature = selectLayer.getSource().getFeatureById(feature1.getId());
				select_sp_id = feature1.getId();

				if(generalFeature.getGeometry().getType() == 'Circle'){
					interaction = new ol.interaction.Transform({
						features: new ol.Collection([generalFeature]),
						stretch: false,
						rotate: false,
						translate: false
					});
				} else {
					interaction = new ol.interaction.Transform({
						features: new ol.Collection([generalFeature]),
						translate: false
					});
				}
				map.addInteraction(interaction);

				// draw Style
				getShapeStyle(generalFeature);

				// Style handles
				setHandleStyle();

				// Events handlers
				var startangle = 0;
				var d=[0,0];
				interaction.on (['rotatestart','translatestart'], function(e){
					// Rotation
					startangle = e.feature.get('angle')||0;
					// Translation
					d=[0,0];
				});

				interaction.on('rotating', function (e){	
					// Set angle attribute to be used on style !
					e.feature.set('angle', startangle - e.angle);
				});

				interaction.on('translating', function (e){
					d[0]+=e.delta[0];
					d[1]+=e.delta[1];
				});
			}
		}
	}
	return !!feature1;
};	


/**
 * @param {ol.MapBrowserEvent} evt Map browser event.
 */
app.Drag.prototype.handleDragEvent = function(evt) {
	var map = evt.map;

	var layer1;
	var feature1 = map.forEachFeatureAtPixel(evt.pixel,
			function(feature1, layer) {
		layer1 = layer;

		return feature1;
	});

	if(layer1){
		if(layer1.get('name') != selectLayer.get('name'))
			return;
	}

	var deltaX = evt.coordinate[0] - this.coordinate_[0];
	var deltaY = evt.coordinate[1] - this.coordinate_[1];

//	var geometry = /** @type {ol.geom.SimpleGeometry} */
//	(this.feature_.getGeometry());

	var geoArr = [];

	var id = this.feature_.getId();

	if(id === undefined || id.charAt(0) == 'P'){
		return;
	} else if(id.charAt(0) == 'S'){
		/*var geo = selectLayer.getSource().getFeatureById(id).getGeometry();
    	  geoArr.push(geo);*/
		return;
	} else if(id.split('_').length < 3){
		var geo = selectLayer.getSource().getFeatureById(id).getGeometry();
		geoArr.push(geo);
	} else {
		for(var i = 0; i < this.feature_.getId().split('_')[1]; i++){
			var geo = selectLayer.getSource().getFeatureById(id.split('_')[0]+'_'+id.split('_')[1]+'_'+i).getGeometry();
			geoArr.push(geo);
		};
		var drawPointFeature = selectLayer.getSource().getFeatureById('P'+id.split('_')[0]);
		if(drawPointFeature != undefined){
			drawPointFeature = drawPointFeature.getGeometry();
			geoArr.push(drawPointFeature);
		}
	}

	var geoCollection = new ol.geom.GeometryCollection(geoArr);

	geoCollection.translate(deltaX, deltaY);
//	geometry.translate(deltaX, deltaY);

	this.coordinate_[0] = evt.coordinate[0];
	this.coordinate_[1] = evt.coordinate[1];
};


/**
 * @param {ol.MapBrowserEvent} evt Event.
 */
app.Drag.prototype.handleMoveEvent = function(evt) {
	if (this.cursor_) {
		var map = evt.map;

		var layer1;
		var feature1 = map.forEachFeatureAtPixel(evt.pixel,
				function(feature1, layer) {
			layer1 = layer;

			return feature1;
		});

		if(layer1){
			if(layer1.get('name') != selectLayer.get('name'))
				return;
		}

		var element = evt.map.getTargetElement();
		if (feature1) {
			if (element.style.cursor != this.cursor_) {
				this.previousCursor_ = element.style.cursor;
				element.style.cursor = this.cursor_;
			}
		} else if (this.previousCursor_ !== undefined) {
			element.style.cursor = this.previousCursor_;
			this.previousCursor_ = undefined;
		} else {
			element.style.cursor = 'default';
		}
	}
};


/**
 * @param {ol.MapBrowserEvent} evt Map browser event.
 * @return {boolean} `false` to stop the drag sequence.
 */
app.Drag.prototype.handleUpEvent = function(evt) {
	this.coordinate_ = null;
	this.feature_ = null;
	return false;
};

//--------------------------------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------------------------------

//base layer
var baseLayer = new ol.layer.Tile({
	/*source: new ol.source.XYZ({
		url: 'http://' + mapServerIp + '/osm_tiles/{z}/{x}/{y}.png'
	})*/
	source: new ol.source.OSM()
});

var overlayLayer = new ol.layer.Tile();

//milSymbol maker layer
/*var makerLayer = new ol.layer.Vector({
	source: new ol.source.Vector({
		feature: [],
		projection: 'EPSG:4326'
	})
});*/

//mouse control
var mousePositionControl = new ol.control.MousePosition({
	coordinateFormat: ol.coordinate.createStringXY(4),
	projection: 'EPSG:4326',
	className: 'custom-mouse-position',
	target: document.getElementById('mouse-position'),
	undefinedHTML: '&nbsp;'
});

var zoomslider = new ol.control.ZoomSlider();

var scaleLine = new ol.control.ScaleLine();

map = new ol.Map({
	interactions: ol.interaction.defaults().extend([new app.Drag()]),
	controls: ol.control.defaults({
		attributionOptions: {
			collapsible: false
		}
	}).extend([mousePositionControl,zoomslider,scaleLine]),
	layers: [/*new ol.layer.Tile({
	       source: new ol.source.OSM()
	    })*/ baseLayer, overlayLayer/*, makerLayer*/],
	    target: 'map',
	    view: new ol.View({
	    	center: [126.4842, 36.7042],
	    	projection: 'EPSG:4326',
	    	maxZoom: maxZoom,
	    	zoom: baseZoom,
	    	minZoom : minZoom
	    })	
});

//click to get Coordinate EVENT
clickCoordOverlay = new ol.Overlay({
	autoPan: true,
	autoPanAnimation: {
		duration: 250
	}
});

//Right click EVENT
map.getViewport().addEventListener('contextmenu', function(event) {
	var check = tmsLayerCheck();
	if(check){
		alert('투명도를 생성해 주세요.');
	} else {
		if(event.preventDefault) {
			event.preventDefault();
		} else {
			event.preventDefault = false;
		}
		
		clickCoordOverlay.setPosition(undefined);

		var coord_Panel = document.getElementById('click_coord');

		tempX = 0; //$("#leftSide").css("width").replace(/[^-\d\.]/g, '');
		tempY = 0; //$("#topMenu").css("height").replace(/[^-\d\.]/g, '');
		var bottom_tempY = 0; //$("#bottomMenu").css("height").replace(/[^-\d\.]/g, '');

		layerX = event.layerX;
		layerY = event.layerY;
		
		var pixelArray = new Array();
		pixelArray.push(layerX);
		pixelArray.push(layerY);
		
		var feature = map.forEachFeatureAtPixel(pixelArray, function(feature, layer) {
			return feature;
		});
		
		if(feature){
			if(feature.getId() != undefined){
				if(feature.getId().charAt(0) == 'S'){
					$('#click_coord_btn_mod').hide();
					$('#click_coord_btn_del').show();
				} else if(feature.getId().charAt(0) != 'P'){
					$('#click_coord_btn_mod').show();
					$('#click_coord_btn_del').show();
				}
			} else {
				$('#click_coord_btn_mod').hide();
				$('#click_coord_btn_del').hide();
			}
			coord_Panel.style.display = 'block';
			coord_Panel.style.top = ((mouseY(event)-tempY) - bottom_tempY) + 'px';
			coord_Panel.style.left = (mouseX(event)-tempX) + 'px';
		}

		if(window.event.preventDefault) {
			window.event.preventDefault();
		} else {
			window.event.returnValue = false;
		}
	}

});

//Right click EVENT END
$(document).bind('click', function(){
	document.getElementById('click_coord').style.display = 'none';
});

//selector 
var selectClick = new ol.interaction.Select({
	condition: ol.events.condition.doubleClick,
	toggleCondition: ol.events.condition.shiftKeyOnly
});
map.addInteraction(selectClick);



function changeBaseLayer(e, type){
	var base_layer = map.getLayers().item(0);
	var overlay_layer = map.getLayers().item(1);

	if(type != 'WebServerTMS'){
		map.getView().setCenter([126.4842, 36.7042]);
		map.getView().setZoom(baseZoom);
	}

	if(type == 'WebServerTMS'){
		map.getView().setCenter([126.9130, 37.3580]);
		map.getView().setZoom(13);

		var changeSource = new ol.source.XYZ({
			url: 'http://'+mapServerIp+':20008/tile/TMAS_Incheon/{z}/{x}/{y}.png'
		});
		base_layer.setSource(changeSource);

		if(overlay_layer.getSource() != null){
			overlay_layer.setSource(null);
		}
	} else if(type == 'LocalTMS'){
		var changeSource =  new ol.source.XYZ({
			projection: 'EPSG:4326',
			url: 'http://192.168.1.122:8081/map_tms/world/{z}/{x}/{-y}.png'
		});
		base_layer.setSource(changeSource);
	} else if(type == 'OpenlayersOMS'){
		var changeSource = new ol.source.OSM();
		base_layer.setSource(changeSource);

		if(overlay_layer.getSource() != null){
			overlay_layer.setSource(null);
		}
	} else if(type == 'BingMapOMS'){
		var changeSource = new ol.source.BingMaps({
			key: '5ZhyU93Qx2w0FLyE15OD~HbVyBnoWXoUKxbH3MyWqjg~Ah3V7zfZOFnyhsJaKZPutldoznBzMZDI2UxmxQafWErUiB_buf1uXL-xVfjStyYG',
			imagerySet: 'AerialWithLabels'
		})
		base_layer.setPreload(Infinity);
		base_layer.setSource(changeSource);

		if(overlay_layer.getSource() != null){
			overlay_layer.setSource(null);
		}
	} else {
		var changeSource = new ol.source.XYZ({
			url: 'http://'+mapServerIp+'/osm_tiles/{z}/{x}/{y}.png'
		});
		base_layer.setSource(changeSource);

		if(overlay_layer.getSource() != null){
			overlay_layer.setSource(null);
		}
	}

	var base_map_name = document.getElementById('base_map_name');
	base_map_name.innerHTML = e.children[1].innerHTML;
	
	map.updateSize();
}

//map Zoom control
map.on('moveend', function(event) {
	if(baseLayer.getSource().getUrls() != null){
		if(baseLayer.getSource().getUrls()[0].split('/')[3] == 'map_tms'){
			var zoomLevel = map.getView().getZoom();
			var url;

			if(zoomLevel>=5 && zoomLevel<=15){
				if(zoomLevel>=13){
					//url = 'http://192.168.1.122:8081/map_tms/tms_korea1m/{z}/{x}/{-y}.png';
					url = 'http://localhost:8081/map_tms/tms_korea1m/{z}/{x}/{-y}.png';
				} else if(zoomLevel>=5 && zoomLevel<=12){
					//url = 'http://192.168.1.122:8081/map_tms/tms_korea30m/{z}/{x}/{-y}.png';
					url = 'http://localhost:8081/map_tms/tms_korea30m/{z}/{x}/{-y}.png';
				}

				var setSource =  new ol.source.XYZ({
					projection: 'EPSG:4326',
					url: url
				});
				overlayLayer.setSource(setSource);
			} else {
				if(overlayLayer.getSource() != null){
					overlayLayer.setSource(null);
				}
			}
		}
	}

	var layers = map.getLayers();
	for (var i = 2; i < layers.getLength(); i++){
		if(layers.item(i).getSource().getFeatures() != undefined){
			var features = layers.item(i).getSource().getFeatures();

			for(var j = 0; j < features.length; j++){
				if(features[j].getId() != undefined){
					var feature_id = features[j].getId();
					var feature_type = features[j].getGeometry().getType();

					if(feature_id.split('_').length == 2){
						if(feature_id.split('_')[1] == '1'|| feature_id.split('_')[1] == '0'){
							var scale = scaleLine.D.split(' ')[0];
							var symScale = getSymbolScale(scale);
							if (features[j].getStyle()[0] != undefined ) { // 20200205
								features[j].getStyle()[0].getImage().setScale(symScale);
							}
						}
					} else if(feature_id.split('_').length == 3){
						/*if(feature_type == 'Point'){
							if(feature_id.split('_')[1] != '0'){
								var scale = scaleLine.D.split(' ')[0];
								var symScale = getSymbolScale(scale);
								features[j].getStyle().getText().setScale(symScale);
							}
						}*/
					}
				}
			}
		}
	}
});

function getSymbolScale(mapScale){
	var symScale = 1;
	if(mapScale > 0 && mapScale <= 75000)
	{
		/*symScale = 7/10;*/symScale = 10/10;
	}
	else if (mapScale > 75000 && mapScale <= 175000)
	{
		/*symScale = 6/10;*/symScale = 9/10;
	}
	else if (mapScale > 175000 && mapScale <= 375000)
	{
		/*symScale = 5/10;*/symScale = 8/10;
	}
	else if (mapScale > 375000 && mapScale <= 750000)
	{
		/*symScale = 4/10;*/symScale = 7/10;
	}
	else if (mapScale > 750000 && mapScale <= 1500000)
	{
		/*symScale = 3/10;*/symScale = 6/10;
	}
	else if (mapScale > 1500000 && mapScale <= 3000000)
	{
		/*symScale = 2/10;*/symScale = 5/10;
	}
	else if (mapScale > 3000000)
	{
		/*symScale = 1/10;*/symScale = 4/10;
	}
	return symScale;
}

