'use strict';

var JimapOverlay = function JimapOverlay(options) {
    if (!(this instanceof JimapOverlay)) {
        throw new Error('new 로 객체를 생성해야 합니다.')
    }

    this.lineDraw = false;          // line draw 여부
    this.rectDraw = false;          // rect draw 여부

    this._svg = d3.select(options.canvas).append('svg');
    this._transform = d3.geoTransform({point : this.projectPoint});
    this._path = d3.geoPath().projection(this._transform);
    this._lines = new jsMap();
    this._rects = new jsMap();

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
    if (jiCommon.overlay._lines.size() > 0) {
        jiCommon.overlay._lines.values().forEach(function (e) {
            if (!e.feature.isScreen()) {
                e.svg.attr('d', e.feature.getPath());
            }
        });
    }

    if (jiCommon.overlay._rects.size() > 0) {
        jiCommon.overlay._rects.values().forEach(function (e) {
            if (!e.feature.isScreen()) {
                e.svg.attr('d', e.feature.getPath());
            }
        });
    }
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
                    .append('path')
                    .attr('d', jiCommon.overlay.line.feature.getPath())
                    .style('stroke', overlayCommon.commonAttr.stroke)
                    .style('stroke-dasharray', '5 5')
                    .style('opacity', 0.8)
                    .style('stroke-width', overlayCommon.commonAttr.strokeWidth);
            } else {
                jiCommon.overlay.line.svg.attr('d', jiCommon.overlay.line.feature.getPath());
            }

            if (!jiCommon.overlay._lines.containsKey(jiCommon.overlay.line.id)) {
                jiCommon.overlay._lines.put(jiCommon.overlay.line.id, jiCommon.overlay.line);
            }
            jiCommon.overlay.update();
        }
    }
}

JimapOverlay.prototype.lineDrawEnd = function lineDrawEnd(e) {
    jiCommon.overlay.keep = false;
    jiCommon.overlay.line.feature.setX2(e.lngLat.lng);
    jiCommon.overlay.line.feature.setY2(e.lngLat.lat);
    jiCommon.overlay.line.svg.attr('d', jiCommon.overlay.line.feature.getPath());
    jiCommon.overlay.update();

    jiCommon.removeMapEvent('mousedown', jiCommon.overlay.lineDrawStart);
    jiCommon.removeMapEvent('mousemove', jiCommon.overlay.lineDrawDrag);
    jiCommon.removeMapEvent('mouseup', jiCommon.overlay.lineDrawEnd);

    jiCommon.overlay.lineDraw = false;

    overlayCommon.svgShapeNum.line++;

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
                    .append('path')
                    .attr('d', jiCommon.overlay.rect.feature.getPath())
                    .style('stroke', overlayCommon.commonAttr.stroke)
                    .style('stroke-width', overlayCommon.commonAttr.strokeWidth)
                    .style('fill', overlayCommon.commonAttr.color).style('fill-opacity', 0.3).on('click', function(d) {
                        alert('Rect click event');
                    });
            } else {
                jiCommon.overlay.rect.svg.attr('d', jiCommon.overlay.rect.feature.getPath());
            }

            if (!jiCommon.overlay._rects.containsKey(jiCommon.overlay.rect.id)) {
                jiCommon.overlay._rects.put(jiCommon.overlay.rect.id, jiCommon.overlay.rect);
            }

            jiCommon.overlay.update();
        }
    }
}

JimapOverlay.prototype.rectDrawEnd = function rectDrawEnd(e) {
    jiCommon.overlay.keep = false;
    jiCommon.overlay.rect.svg.attr('d', jiCommon.overlay.rect.feature.getPath());
    jiCommon.overlay.update();

    jiCommon.removeMapEvent('mousedown', jiCommon.overlay.rectDrawStart);
    jiCommon.removeMapEvent('mousemove', jiCommon.overlay.rectDrawDrag);
    jiCommon.removeMapEvent('mouseup', jiCommon.overlay.rectDrawEnd);

    jiCommon.overlay.rectDraw = false;

    overlayCommon.svgShapeNum.rect++;

    jiCommon.enableDragPan();
}
// rect draw event

JimapOverlay.prototype.selectedShape = function selectedShape(shape, imageUrl) {
    if (shape === 'line') {
        if (this.lineDraw) {
            this.lineDraw = false;

            jiCommon.removeMapEvent('mousedown', this.lineDrawStart);
            jiCommon.removeMapEvent('mouseup', this.lineDrawEnd);

            jiCommon.enableDragPan();
        } else {
            this.lineDraw = true;
            this.keep = false;
            this.line = {};

            jiCommon.addMapEvent('mousedown', this.lineDrawStart);
            jiCommon.addMapEvent('mouseup', this.lineDrawEnd);
        }
    } else if (shape === 'rect') {
        if (this.rectDraw) {
            this.rectDraw = false;

            jiCommon.removeMapEvent('mousedown', this.rectDrawStart);
            jiCommon.removeMapEvent('mouseup', this.rectDrawEnd);

            jiCommon.enableDragPan();
        } else {
            this.rectDraw = true;
            this.keep = false;
            this.rect = {};

            jiCommon.addMapEvent('mousedown', this.rectDrawStart);
            jiCommon.addMapEvent('mouseup', this.rectDrawEnd);
        }
    }
}

JimapOverlay.prototype.idMaker = function idMaker(shapeNm) {
    var makeId;
    switch (shapeNm) {
        case 'line' :
            makeId = shapeNm + overlayCommon.svgShapeNum.line;
            break;
        case 'rect':
            makeId = shapeNm + overlayCommon.svgShapeNum.rect;
            break;
    }

    return makeId;
}