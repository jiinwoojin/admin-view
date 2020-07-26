'use strict';

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
    // 현재 map extents 와 겹치는지 부분만 update
    // 원본 좌표는 e 객체에 들어 있음

    jiCommon.overlay.updateLine();
    jiCommon.overlay.updateRectangle();
    jiCommon.overlay.updateTriangle();
}

JimapOverlay.prototype.updateLine = function updateLine() {
    if (jiCommon.overlay._lines.size() > 0) {
        jiCommon.overlay._lines.values().forEach(function (e) {
            if (!e.feature.isScreen()) {
                e.svg.attr('d', e.feature.getPath());
            }
        });
    }
}

JimapOverlay.prototype.updateRectangle = function updateRectangle() {
    if (jiCommon.overlay._rectangles.size() > 0) {
        jiCommon.overlay._rectangles.values().forEach(function (e) {
            if (!e.feature.isScreen()) {
                e.svg.attr('d', e.feature.getPath());
            }
        });
    }
}

JimapOverlay.prototype.updateTriangle = function updateTriangle() {
    if (jiCommon.overlay._triangles.size() > 0) {
        jiCommon.overlay._triangles.values().forEach(function (e) {
            if (!e.feature.isScreen()) {
                e.svg.attr('d', e.feature.getPath());
            }
        });
    }
}

JimapOverlay.prototype.updatePath = function updatePath(svg, feature) {
    svg.attr('d', feature.getPath());
}

JimapOverlay.prototype.drawStart = function drawStart(e) {
    jiCommon.overlay.keep = true;
    jiCommon.disableDragPan();

    jiCommon.overlay.feature = {};
    jiCommon.overlay.feature.id = jiCommon.overlay.idMaker();
    jiCommon.overlay.feature.x1 = e.lngLat.lng;
    jiCommon.overlay.feature.y1 = e.lngLat.lat;
    switch (jiCommon.overlay._drawMode) {
        case jiConstant.OVERLAY_DRAW_MODE.LINE.CD :
        case jiConstant.OVERLAY_DRAW_MODE.RECTANGLE.CD :
        case jiConstant.OVERLAY_DRAW_MODE.TRIANGLE.CD :
            jiCommon.addMapEvent(jiConstant.EVENTS.MOUSEMOVE, jiCommon.overlay.drawDrag);
            break;
    }
}

JimapOverlay.prototype.drawDrag = function drawDrag(e) {
    if (jiCommon.overlay.keep) {
        jiCommon.overlay.feature.x2 = e.lngLat.lng;
        jiCommon.overlay.feature.y2 = e.lngLat.lat;
        switch (jiCommon.overlay._drawMode) {
            case jiConstant.OVERLAY_DRAW_MODE.LINE.CD :
                break;
            case jiConstant.OVERLAY_DRAW_MODE.RECTANGLE.CD :
                break;
            case jiConstant.OVERLAY_DRAW_MODE.TRIANGLE.CD :
                break;
        }
    }
}

JimapOverlay.prototype.drawEnd = function drawEnd(e) {
    switch (jiCommon.overlay._drawMode) {
        case jiConstant.OVERLAY_DRAW_MODE.LINE.CD :
        case jiConstant.OVERLAY_DRAW_MODE.RECTANGLE.CD :
        case jiConstant.OVERLAY_DRAW_MODE.TRIANGLE.CD :
            jiCommon.removeMapEvent(jiConstant.EVENTS.MOUSEMOVE, jiCommon.overlay.drawDrag);
            break;
    }

    jiCommon.overlay.updatePath(jiCommon.overlay.line.svg, jiCommon.overlay.line.feature);
    jiCommon.overlay.updateLine();

    jiCommon.removeMapEvent(jiConstant.EVENTS.MOUSEDOWN, jiCommon.overlay.drawStart);
    jiCommon.removeMapEvent(jiConstant.EVENTS.MOUSEMOVE, jiCommon.overlay.drawDrag);
    jiCommon.removeMapEvent(jiConstant.EVENTS.MOUSEUP, jiCommon.overlay.drawEnd);

    jiCommon.overlay.isDraw = false;

    jiCommon.enableDragPan();
}

// line draw event
JimapOverlay.prototype.lineDrawStart = function lineDrawStart(e) {
    jiCommon.overlay.keep = true;
    jiCommon.addMapEvent('mousemove', jiCommon.overlay.lineDrawDrag);
    jiCommon.disableDragPan();

    jiCommon.overlay.line.id = jiCommon.overlay.idMaker('line');
    jiCommon.overlay.line.x1 = e.lngLat.lng;
    jiCommon.overlay.line.y1 = e.lngLat.lat;
}

JimapOverlay.prototype.lineDrawDrag = function lineDrawDrag(e) {
    if (jiCommon.overlay.keep) {
        jiCommon.overlay.line.x2 = e.lngLat.lng;
        jiCommon.overlay.line.y2 = e.lngLat.lat;

        if (jiCommon.overlay.line.x1 !== jiCommon.overlay.line.x2) {
            if (jiCommon.overlay.line.feature === undefined) {
                jiCommon.overlay.line.feature = new Line(jiCommon.overlay.line.x1,
                    jiCommon.overlay.line.y1, jiCommon.overlay.line.x2, jiCommon.overlay.line.y2);
            } else {
                jiCommon.overlay.line.feature.setX2(jiCommon.overlay.line.x2);
                jiCommon.overlay.line.feature.setY2(jiCommon.overlay.line.y2);
            }

            if (jiCommon.overlay.line.svg === undefined) {
                jiCommon.overlay.line.svg = jiCommon.overlay._svg.append('g').attr('id', 'g-line-' + jiCommon.overlay.line.id)
                    .append('path').attr('id', 'path-line-' + jiCommon.overlay.line.id)
                    .attr('d', jiCommon.overlay.line.feature.getPath())
                    .style('stroke', overlayCommon.commonAttr.stroke)
                    .style('stroke-dasharray', '5 5')
                    .style('opacity', 0.8)
                    .style('stroke-width', overlayCommon.commonAttr.strokeWidth).on('click', function() {
                        alert('Line Click Event.');
                    });
            } else {
                jiCommon.overlay.updatePath(jiCommon.overlay.line.svg, jiCommon.overlay.line.feature);
            }

            if (!jiCommon.overlay._lines.containsKey(jiCommon.overlay.line.id)) {
                jiCommon.overlay._lines.put(jiCommon.overlay.line.id, jiCommon.overlay.line);
            }
            jiCommon.overlay.updateLine();
        }
    }
}

JimapOverlay.prototype.lineDrawEnd = function lineDrawEnd(e) {
    jiCommon.overlay.keep = false;
    jiCommon.overlay.line.feature.setX2(e.lngLat.lng);
    jiCommon.overlay.line.feature.setY2(e.lngLat.lat);
    jiCommon.overlay.updatePath(jiCommon.overlay.line.svg, jiCommon.overlay.line.feature);
    jiCommon.overlay.updateLine();

    jiCommon.removeMapEvent('mousedown', jiCommon.overlay.lineDrawStart);
    jiCommon.removeMapEvent('mousemove', jiCommon.overlay.lineDrawDrag);
    jiCommon.removeMapEvent('mouseup', jiCommon.overlay.lineDrawEnd);

    jiCommon.overlay.isDraw = false;

    jiCommon.enableDragPan();
}
// line draw event

// rect draw event
JimapOverlay.prototype.rectDrawStart = function rectDrawStart(e) {
    jiCommon.overlay.keep = true;
    jiCommon.addMapEvent('mousemove', jiCommon.overlay.rectDrawDrag);
    jiCommon.disableDragPan();

    jiCommon.overlay.rect.id = jiCommon.overlay.idMaker('rect');
    jiCommon.overlay.rect.x1 = e.lngLat.lng;
    jiCommon.overlay.rect.y1 = e.lngLat.lat;
}

JimapOverlay.prototype.rectDrawDrag = function rectDrawDrag(e) {
    if (jiCommon.overlay.keep) {
        jiCommon.overlay.rect.x2 = e.lngLat.lng;
        jiCommon.overlay.rect.y2 = e.lngLat.lat;

        if (jiCommon.overlay.rect.x1 !== jiCommon.overlay.rect.x2) {
            if (jiCommon.overlay.rect.feature === undefined) {
                jiCommon.overlay.rect.feature = new Rectangle(jiCommon.overlay.rect.x1, jiCommon.overlay.rect.y1,
                    jiCommon.overlay.rect.x2, jiCommon.overlay.rect.y2);
            } else {
                jiCommon.overlay.rect.feature.setX2(jiCommon.overlay.rect.x2);
                jiCommon.overlay.rect.feature.setY2(jiCommon.overlay.rect.y2);
            }

            if (jiCommon.overlay.rect.svg === undefined) {
                jiCommon.overlay.rect.svg = jiCommon.overlay._svg.append('g').attr('id', 'g-rect-' + jiCommon.overlay.rect.id)
                    .append('path').attr('id', 'path-rect-' + jiCommon.overlay.rect.id)
                    .attr('d', jiCommon.overlay.rect.feature.getPath())
                    .style('stroke', overlayCommon.commonAttr.stroke)
                    .style('stroke-width', overlayCommon.commonAttr.strokeWidth)
                    .style('fill', overlayCommon.commonAttr.color).style('fill-opacity', 0.3).on('click', function(d) {
                        alert('Rect Click Event.');
                    });
            } else {
                jiCommon.overlay.updatePath(jiCommon.overlay.rect.svg, jiCommon.overlay.rect.feature);
            }

            if (!jiCommon.overlay._rectangles.containsKey(jiCommon.overlay.rect.id)) {
                jiCommon.overlay._rectangles.put(jiCommon.overlay.rect.id, jiCommon.overlay.rect);
            }

            jiCommon.overlay.updateRectangle();
        }
    }
}

JimapOverlay.prototype.rectDrawEnd = function rectDrawEnd(e) {
    jiCommon.overlay.keep = false;
    jiCommon.overlay.updatePath(jiCommon.overlay.rect.svg, jiCommon.overlay.rect.feature);
    jiCommon.overlay.updateRectangle();

    jiCommon.removeMapEvent('mousedown', jiCommon.overlay.rectDrawStart);
    jiCommon.removeMapEvent('mousemove', jiCommon.overlay.rectDrawDrag);
    jiCommon.removeMapEvent('mouseup', jiCommon.overlay.rectDrawEnd);

    jiCommon.overlay.isDraw = false;

    jiCommon.enableDragPan();
}
// rect draw event

// triangle draw event
JimapOverlay.prototype.triangleDrawStart = function triangleDrawStart(e) {
    jiCommon.overlay.keep = true;
    jiCommon.addMapEvent('mousemove', jiCommon.overlay.triangleDrawDrag);
    jiCommon.disableDragPan();

    jiCommon.overlay.triangle.id = jiCommon.overlay.idMaker('triangle');
    jiCommon.overlay.triangle.x1 = e.lngLat.lng;
    jiCommon.overlay.triangle.y1 = e.lngLat.lat;
}

JimapOverlay.prototype.triangleDrawDrag = function triangleDrawDrag(e) {
    if (jiCommon.overlay.keep) {
        jiCommon.overlay.triangle.x2 = e.lngLat.lng;
        jiCommon.overlay.triangle.y2 = e.lngLat.lat;

        if (jiCommon.overlay.triangle.x1 !== jiCommon.overlay.triangle.x2) {
            if (jiCommon.overlay.triangle.feature === undefined) {
                jiCommon.overlay.triangle.feature = new Triangle(jiCommon.overlay.triangle.x1,
                    jiCommon.overlay.triangle.y1, jiCommon.overlay.triangle.x2, jiCommon.overlay.triangle.y2);
            } else {
                jiCommon.overlay.triangle.feature.setX2(jiCommon.overlay.triangle.x2);
                jiCommon.overlay.triangle.feature.setY2(jiCommon.overlay.triangle.y2);
            }

            if (jiCommon.overlay.triangle.svg === undefined) {
                jiCommon.overlay.triangle.svg = jiCommon.overlay._svg.append('g')
                    .attr('id', 'g-triangle-' + jiCommon.overlay.triangle.id)
                    .append('path').attr('id', 'path-triangle-' + jiCommon.overlay.triangle.id)
                    .attr('d', jiCommon.overlay.triangle.feature.getPath())
                    .style('stroke', overlayCommon.commonAttr.stroke)
                    .style('stroke-width', overlayCommon.commonAttr.strokeWidth)
                    .style('fill', overlayCommon.commonAttr.color).on('click', function() {
                        alert('Triangle Click Event.');
                    });
            } else {
                jiCommon.overlay.updatePath(jiCommon.overlay.triangle.svg, jiCommon.overlay.triangle.feature);
            }

            if (!jiCommon.overlay._triangles.containsKey(jiCommon.overlay.triangle.id)) {
                jiCommon.overlay._triangles.put(jiCommon.overlay.triangle.id, jiCommon.overlay.triangle);
            }

            jiCommon.overlay.updateTriangle();
        }
    }
}

JimapOverlay.prototype.triangleDrawEnd = function triangleDrawEnd(e) {
    jiCommon.overlay.keep = false;
    jiCommon.overlay.updatePath(jiCommon.overlay.triangle.svg, jiCommon.overlay.triangle.feature);
    jiCommon.overlay.updateTriangle();

    jiCommon.removeMapEvent('mousedown', jiCommon.overlay.triangleDrawStart);
    jiCommon.removeMapEvent('mousemove', jiCommon.overlay.triangleDrawDrag);
    jiCommon.removeMapEvent('mouseup', jiCommon.overlay.triangleDrawEnd);

    jiCommon.overlay.isDraw = false;

    jiCommon.enableDragPan();
}
// triangle draw event

JimapOverlay.prototype.selectedShape = function selectedShape(shape, imageUrl) {
    if (this.isDraw) {
        this._drawMode = 999;
        this.isDraw = false;

        jiCommon.removeMapEvent(jiConstant.EVENTS.MOUSEDOWN, this.drawStart);
        jiCommon.removeMapEvent(jiConstant.EVENTS.MOUSEUP, this.drawEnd);

        jiCommon.enableDragPan();
    } else {
        this._drawMode = shape;
        this.isDraw = true;
        this.keep = false;

        jiCommon.addMapEvent(jiConstant.EVENTS.MOUSEDOWN, this.drawStart);
        jiCommon.addMapEvent(jiConstant.EVENTS.MOUSEUP, this.drawEnd);
    }
    /*if (shape === 'line') {
        if (this.isDraw) {
            this.isDraw = false;

            jiCommon.removeMapEvent('mousedown', this.lineDrawStart);
            jiCommon.removeMapEvent('mouseup', this.lineDrawEnd);

            jiCommon.enableDragPan();
        } else {
            this.isDraw = true;
            this.keep = false;
            this.line = {};

            jiCommon.addMapEvent('mousedown', this.lineDrawStart);
            jiCommon.addMapEvent('mouseup', this.lineDrawEnd);
        }
    } else if (shape === 'rect') {
        if (this.isDraw) {
            this.isDraw = false;

            jiCommon.removeMapEvent('mousedown', this.rectDrawStart);
            jiCommon.removeMapEvent('mouseup', this.rectDrawEnd);

            jiCommon.enableDragPan();
        } else {
            this.isDraw = true;
            this.keep = false;
            this.rect = {};

            jiCommon.addMapEvent('mousedown', this.rectDrawStart);
            jiCommon.addMapEvent('mouseup', this.rectDrawEnd);
        }
    } else if (shape === 'triangle') {
        if (this.isDraw) {
            this.isDraw = false;

            jiCommon.removeMapEvent('mousedown', this.triangleDrawStart);
            jiCommon.removeMapEvent('mouseup', this.triangleDrawEnd);
        } else {
            this.isDraw = true;
            this.keep = false;
            this.triangle = {};

            jiCommon.addMapEvent('mousedown', this.triangleDrawStart);
            jiCommon.addMapEvent('mouseup', this.triangleDrawEnd);
        }
    }*/
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
}
