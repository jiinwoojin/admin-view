var map;    // openlayers Map Object
//var mapServerIp = '115.144.19.100'; // Map Server Ip
var mapServerIp = '192.168.0.10';

var baseZoom = 7; // 기본 Zoom 
var minZoom = 1; // 최소 Zoom
var maxZoom = 16; // 최대  Zoom

//우클릭 이벤트에서 저장할 pixel x,y 변수
var layerX;
var layerY;

var clickCoordOverlay; // mouse click Object

var symbol_serial = 1; // military symbol (id) serial number maker
var selectLayer;

/**
 * 마우스 X좌표
 */
function mouseX(event){
	if(event.pageX){
		return event.pageX;
	}else if(event.clientX){
		return event.clientX + (document.documentElement.scrollLeft ? document.documentElement.scrollLeft : document.body.scrollLeft);
	}else{
		return null;
	}
}

/**
 * 마우스 Y좌표
 */
function mouseY(event){
	if(event.pageY){
		return event.pageY;
	}else if(event.clientY){
		return event.clientY + (document.documentElement.scrollTop ? document.documentElement.scrollTop : document.body.scrollTop);
	}else{
		return null;
	}
}

