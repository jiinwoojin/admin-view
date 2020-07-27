'use strict';

// 작도는 2D 에서만 한다.
var JimapOverlay = function JimapOverlay(options) {
    if (!(this instanceof JimapOverlay)) {
        throw new Error('new 로 객체를 생성해야 합니다.')
    }

    this.isDraw = false;
    this._drawMode = 999;

    this._svg = d3.select(options.canvas).append('svg');
    this._transform = d3.geoTransform({point : this.projectPoint});
    this._path = d3.geoPath().projection(this._transform);

    this._lines = new jsMap();
    this._rectangles = new jsMap();
    this._triangles = new jsMap();

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
    jiCommon.overlay.updateLine();
    jiCommon.overlay.updateRectangle();
    jiCommon.overlay.updateTriangle();
}

JimapOverlay.prototype.updateLine = function updateLine() {
    if (jiCommon.overlay._lines.size() > 0) {
        jiCommon.overlay._lines.values().forEach(function (e) {
            if (!e.feature.isScreen()) {
                // 현재 map extents 와 겹치는지 부분만 update
                // 원본 좌표는 e 객체에 들어 있음
                var _lineSvg = e.feature.getSvg();
                e.svg.attr('x1', _lineSvg.x1).attr('y1', _lineSvg.y1)
                    .attr('x2', _lineSvg.x2).attr('y2', _lineSvg.y2);
                //e.svg.attr('d', e.feature.getPath());
            }
        });
    }
}

JimapOverlay.prototype.updateRectangle = function updateRectangle() {
    if (jiCommon.overlay._rectangles.size() > 0) {
        jiCommon.overlay._rectangles.values().forEach(function (e) {
            if (!e.feature.isScreen()) {
                // 현재 map extents 와 겹치는지 부분만 update
                // 원본 좌표는 e 객체에 들어 있음
                var _rectSvg = e.feature.getSvg();
                e.svg.attr('x', _rectSvg.x).attr('y', _rectSvg.y)
                    .attr('width', _rectSvg.w).attr('height', _rectSvg.h);
                //e.svg.attr('d', e.feature.getPath());
            }
        });
    }
}

JimapOverlay.prototype.updateTriangle = function updateTriangle() {
    if (jiCommon.overlay._triangles.size() > 0) {
        jiCommon.overlay._triangles.values().forEach(function (e) {
            if (!e.feature.isScreen()) {
                // 현재 map extents 와 겹치는지 부분만 update
                // 원본 좌표는 e 객체에 들어 있음
                e.svg.attr('d', e.feature.getPath());
            }
        });
    }
}

JimapOverlay.prototype.updatePath = function updatePath(svg, feature) {
    svg.attr('d', feature.getPath());
}

JimapOverlay.prototype.selectedShape = function selectedShape(shape) {
    if (this.isDraw) {
        this._drawMode = 999;
        this.isDraw = false;

        jiCommon.enableDragPan();
    } else {
        this._drawMode = shape;
        this.isDraw = true;
        this.keep = false;
    }

    this._eventsController();
}

JimapOverlay.prototype.idMaker = function idMaker() {
    var makeId;
    switch (this._drawMode) {
        case jiConstant.OVERLAY_DRAW_MODE.LINE.CD :
            makeId = jiConstant.OVERLAY_DRAW_MODE.LINE.NAME + (++overlayCommon.svgShapeNum.line);
            break;
        case jiConstant.OVERLAY_DRAW_MODE.RECTANGLE.CD :
            makeId = jiConstant.OVERLAY_DRAW_MODE.RECTANGLE.NAME + (++overlayCommon.svgShapeNum.rect);
            break;
        case jiConstant.OVERLAY_DRAW_MODE.ROUNDED_RECTANGLE.CD :
            makeId = jiConstant.OVERLAY_DRAW_MODE.ROUNDED_RECTANGLE.NAME + (++overlayCommon.svgShapeNum.roundrect);
            break;
        case jiConstant.OVERLAY_DRAW_MODE.TRIANGLE.CD :
            makeId = jiConstant.OVERLAY_DRAW_MODE.TRIANGLE.NAME + (++overlayCommon.svgShapeNum.triangle);
            break;
    }

    return makeId;
}

JimapOverlay.prototype._eventsController = function _eventsController() {
    // Line | Rectangle | Rounded Rectangle | Triangle | Circle : drawStart, drawDrag, drawEnd
    if (this.isDraw) {
        switch (this._drawMode) {
            case jiConstant.OVERLAY_DRAW_MODE.LINE.CD :
            case jiConstant.OVERLAY_DRAW_MODE.RECTANGLE.CD :
            case jiConstant.OVERLAY_DRAW_MODE.TRIANGLE.CD :
                jiCommon.addMapEvent(jiConstant.EVENTS.MOUSEDOWN, this.drawStart);
                jiCommon.addMapEvent(jiConstant.EVENTS.MOUSEUP, this.drawEnd);
                break;
        }
    } else {
        switch (this._drawMode) {
            case jiConstant.OVERLAY_DRAW_MODE.LINE.CD :
            case jiConstant.OVERLAY_DRAW_MODE.RECTANGLE.CD :
            case jiConstant.OVERLAY_DRAW_MODE.TRIANGLE.CD :
                jiCommon.removeMapEvent(jiConstant.EVENTS.MOUSEDOWN, this.drawStart);
                jiCommon.removeMapEvent(jiConstant.EVENTS.MOUSEMOVE, this.drawDrag);
                jiCommon.removeMapEvent(jiConstant.EVENTS.MOUSEUP, this.drawEnd);
                break;
        }
    }
}

JimapOverlay.prototype.createGeometry = function createGeometry() {
    var feature;

    switch (jiCommon.overlay._drawMode) {
        case jiConstant.OVERLAY_DRAW_MODE.LINE.CD :
            feature = new Line(jiCommon.overlay.object.x1,
                jiCommon.overlay.object.y1, jiCommon.overlay.object.x2, jiCommon.overlay.object.y2);
            break;
        case jiConstant.OVERLAY_DRAW_MODE.RECTANGLE.CD :
            feature = new Rectangle(jiCommon.overlay.object.x1,
                jiCommon.overlay.object.y1, jiCommon.overlay.object.x2, jiCommon.overlay.object.y2);
            break;
        case jiConstant.OVERLAY_DRAW_MODE.TRIANGLE.CD :
            feature = new Triangle(jiCommon.overlay.object.x1,
                jiCommon.overlay.object.y1, jiCommon.overlay.object.x2, jiCommon.overlay.object.y2);
            break;
    }

    return feature;
}

JimapOverlay.prototype.createSvg = function createSvg() {
    var svg;
    var _id = jiCommon.overlay.object.id;
    var _feature = jiCommon.overlay.object.feature;

    switch (jiCommon.overlay._drawMode) {
        case jiConstant.OVERLAY_DRAW_MODE.LINE.CD :
            var _lineSvg = _feature.getSvg();
            svg = jiCommon.overlay._svg.append('g').attr('id', 'g-line-' + _id)
                /*.append('path').attr('id', 'path-line-' + _id)
                .attr('d', _feature.getPath())*/
                .append('line').attr('id', 'path-line-' + _id)
                .attr('x1', _lineSvg.x1).attr('y1', _lineSvg.y1)
                .attr('x2', _lineSvg.x2).attr('y2', _lineSvg.y2)
                .style('stroke', overlayCommon.commonAttr.stroke)
                .style('stroke-dasharray', '5 5')
                .style('opacity', 0.8)
                .style('stroke-width', overlayCommon.commonAttr.strokeWidth)
                .on('click', function() {alert('Line Click Event.')});
            break;
        case jiConstant.OVERLAY_DRAW_MODE.RECTANGLE.CD :
            var _rectSvg = _feature.getSvg();
            svg = jiCommon.overlay._svg.append('g').attr('id', 'g-rect-' + _id)
                /*.append('path').attr('id', 'path-rect-' + _id)
                .attr('d', _feature.getPath())*/
                .append('rect').attr('id', 'path-rect-' + _id)
                .attr('x', _rectSvg.x).attr('y', _rectSvg.y)
                .attr('width', _rectSvg.w)
                .attr('height', _rectSvg.h)
                .style('stroke', overlayCommon.commonAttr.stroke)
                .style('stroke-width', overlayCommon.commonAttr.strokeWidth)
                .style('fill', overlayCommon.commonAttr.color)
                .style('fill-opacity', 0.4)
                .on('click', function() {console.log(d3.select(this))});
            break;
        case jiConstant.OVERLAY_DRAW_MODE.TRIANGLE.CD :
            svg = jiCommon.overlay._svg.append('g').attr('id', 'g-triangle-' + _id)
                .append('path').attr('id', 'path-triangle-' + _id)
                .attr('d', _feature.getPath())
                .style('stroke', overlayCommon.commonAttr.stroke)
                .style('stroke-width', overlayCommon.commonAttr.strokeWidth)
                .style('fill', overlayCommon.commonAttr.color)
                .style('fill-opacity', 0.4)
                .on('click', function() {alert('Triangle Click Event.')});
            break;
    }

    return svg;
}

JimapOverlay.prototype.updateObject = function updateObject() {
    var _id = jiCommon.overlay.object.id;

    switch (jiCommon.overlay._drawMode) {
        case jiConstant.OVERLAY_DRAW_MODE.LINE.CD :
            if (!jiCommon.overlay._lines.containsKey(_id)) {
                jiCommon.overlay._lines.put(_id, jiCommon.overlay.object);
            }
            break;
        case jiConstant.OVERLAY_DRAW_MODE.RECTANGLE.CD :
            if (!jiCommon.overlay._rectangles.containsKey(_id)) {
                jiCommon.overlay._rectangles.put(_id, jiCommon.overlay.object);
            }
            break;
        case jiConstant.OVERLAY_DRAW_MODE.TRIANGLE.CD :
            if (!jiCommon.overlay._triangles.containsKey(_id)) {
                jiCommon.overlay._triangles.put(_id, jiCommon.overlay.object);
            }
            break;
    }
}

JimapOverlay.prototype.individualUpdate = function individualUpdate() {
    switch (jiCommon.overlay._drawMode) {
        case jiConstant.OVERLAY_DRAW_MODE.LINE.CD :
            jiCommon.overlay.updateLine();
            break;
        case jiConstant.OVERLAY_DRAW_MODE.RECTANGLE.CD :
            jiCommon.overlay.updateRectangle();
            break;
        case jiConstant.OVERLAY_DRAW_MODE.TRIANGLE.CD :
            jiCommon.overlay.updateTriangle();
            break;
    }
}

JimapOverlay.prototype.drawStart = function drawStart(e) {
    jiCommon.overlay.keep = true;
    jiCommon.disableDragPan();

    jiCommon.overlay.object = {};
    jiCommon.overlay.object.id = jiCommon.overlay.idMaker();
    jiCommon.overlay.object.x1 = e.lngLat.lng;
    jiCommon.overlay.object.y1 = e.lngLat.lat;

    switch (jiCommon.overlay._drawMode) {
        case jiConstant.OVERLAY_DRAW_MODE.LINE.CD :
        case jiConstant.OVERLAY_DRAW_MODE.RECTANGLE.CD :
        case jiConstant.OVERLAY_DRAW_MODE.TRIANGLE.CD :
            jiCommon.addMapEvent(jiConstant.EVENTS.MOUSEMOVE, jiCommon.overlay.drawDrag);
            break;
    }
}

// 입력되는 좌표열을 2개만 관리 할 경우
JimapOverlay.prototype.drawDrag = function drawDrag(e) {
    if (jiCommon.overlay.keep) {
        jiCommon.overlay.object.x2 = e.lngLat.lng;
        jiCommon.overlay.object.y2 = e.lngLat.lat;

        if (jiCommon.overlay.object.x1 !== jiCommon.overlay.object.x2) {
            if (jiCommon.overlay.object.feature === undefined) {
                jiCommon.overlay.object.feature = jiCommon.overlay.createGeometry();
            } else {
                jiCommon.overlay.object.feature.setX2(jiCommon.overlay.object.x2);
                jiCommon.overlay.object.feature.setY2(jiCommon.overlay.object.y2);
            }

            if (jiCommon.overlay.object.svg === undefined) {
                jiCommon.overlay.object.svg = jiCommon.overlay.createSvg();
            }

            jiCommon.overlay.updateObject();
            jiCommon.overlay.individualUpdate();
        }
    }
}

JimapOverlay.prototype.drawEnd = function drawEnd(e) {
    // jiCommon.overlay.updatePath(jiCommon.overlay.object.svg, jiCommon.overlay.object.feature);
    jiCommon.overlay.individualUpdate();

    jiCommon.overlay.isDraw = false;

    jiCommon.overlay._eventsController();

    jiCommon.enableDragPan();
}

JimapOverlay.prototype.selectObject = function selectObject() {

}