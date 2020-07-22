'use strict';

var JimapOverlay = function JimapOverlay(options) {
    if (!(this instanceof JimapOverlay)) {
        throw new Error('new 로 객체를 생성해야 합니다.')
    }

    this._svg = d3.select(options.canvas).append('svg');
    this._transform = d3.geoTransform({point : this.projectPoint});
    this._path = d3.geoPath().projection(this._transform);
    this._lines = [];

    jiCommon.addMapEvent('viewreset', this.update);
    jiCommon.addMapEvent('move', this.update);
    jiCommon.addMapEvent('moveend', this.update);
}

JimapOverlay.prototype.constructor = JimapOverlay;

JimapOverlay.prototype.projectPoint = function projectPoint(lon, lat) {
    var point = jiCommon.convert.lonLatToPixel({'lon' : lon, 'lat' : lat});
    this.stream.point(point.x, point.y);
}

JimapOverlay.prototype.update = function update() {
    if (jiCommon.overlay._lines.length > 0) {
        jiCommon.overlay._lines.forEach(function (e) {
            e.attr('d', jiCommon.overlay._path);
        });
    }
}

JimapOverlay.prototype.lineDrawStart = function lineDrawStart(e) {
    jiCommon.overlay.keep = true;
    jiCommon.addMapEvent('mousemove', jiCommon.overlay.lineDrawDrag);
    jiCommon.disableDragPan();
    jiCommon.overlay.keep = true;

    jiCommon.overlay.feature.coordinates[0] = [e.lngLat.lng, e.lngLat.lat];
}

JimapOverlay.prototype.lineDrawDrag = function lineDrawDrag(e) {

}

JimapOverlay.prototype.lineDrawEnd = function lineDrawEnd(e) {
    jiCommon.overlay.keep = false;
    jiCommon.overlay.feature.coordinates[1] = [e.lngLat.lng, e.lngLat.lat];
    jiCommon.overlay._lines.push(jiCommon.overlay._svg.append('g')
        .selectAll('path')
        .data([jiCommon.overlay.feature]).enter()
        .append('path')
        .attr('d', jiCommon.overlay._path).style('stroke', 'black').style('stroke-width', 4));
    jiCommon.overlay.update();

    jiCommon.removeMapEvent('mousedown', jiCommon.overlay.lineDrawStart);
    jiCommon.removeMapEvent('mousemove', jiCommon.overlay.lineDrawDrag);
    jiCommon.removeMapEvent('mouseup', jiCommon.overlay.lineDrawEnd);

    jiCommon.enableDragPan();
}

JimapOverlay.prototype.selectedShape = function selectedShape(shape, imageUrl) {
    if (shape === 'line') {
        this.keep = false;
        this.line = undefined;
        this.feature = {};
        this.feature.type = 'LineString';
        this.feature.coordinates = [];

        jiCommon.addMapEvent('mousedown', this.lineDrawStart);
        jiCommon.addMapEvent('mouseup', this.lineDrawEnd);

        //this._svg.call(d3.drag().on('start', this.makeAddLine.start).on('drag', this.makeAddLine().drag).on('end', this.makeAddLine.end));
    }
}