var milSymbolLoader = {}
// 군대부호 관련 JS 라이브러리 폴더
milSymbolLoader.path = "../../js/mil-symbol-lib"
milSymbolLoader.isComplete = false
milSymbolLoader.param = null
milSymbolLoader.callback = null
milSymbolLoader.loadScript = function(urls,idx){
    if(idx === undefined){
        idx = 0
    }
    var url = milSymbolLoader.path + "/" + urls[idx++]
    var script = document.createElement('script');
    script.src = url;
    script.onload =  function(a) {
        if(idx === urls.length){
            milSymbolLoader.isComplete = true
            milSymbolLoader.init(
                milSymbolLoader.param,
                milSymbolLoader.callback
            )
        }else{
            milSymbolLoader.loadScript(urls,idx)
        }
    };
    document.getElementsByTagName('head')[0].appendChild(script);
};
milSymbolLoader.init = function(param, callback){
    milSymbolLoader.param = param
    milSymbolLoader.callback = callback
    if(milSymbolLoader.isComplete === false){
        milSymbolLoader.loadScript([
            "milsymbol.development.js",
            "savm-bc.js",
            "symbol.js",
            "symbolCommon.js",
            "graphics.js",
            "html2canvas.min.js",
            "mb-milSymbol.js",
            "cmm-milSymbol.js"])
    }else{
        if (!jiCommon.checkMapKind()) {
            // mapbox
            milSymbolLoader.map = milSymbolLoader.param.map;
            milSymbolLoader.redeclareBasegeometry();
            milSymbolLoader.declare2DdrawControl();
            milSymbolLoader.map.addControl(milSymbolLoader.drawControl);
            milSymbolLoader.map.on('draw.update', milSymbolLoader.milsymbolsPreview);
            milSymbolLoader.map.on('draw.create', milSymbolLoader.milsymbolsGenerator);
        } else {
            // cesium
            milSymbolLoader.map = milSymbolLoader.param.map;
        }
        //
        if (milSymbolLoader.callback) {
            milSymbolLoader.callback();
        }
    }
};
/**
 * ms(milsymbol.js) basegeometry 재선언
 */
milSymbolLoader.redeclareBasegeometry = function(){
    if(ms.___original_basegeometry === undefined){
        ms.___original_basegeometry = ms.getSymbolParts()[0]
        var symbolParts = ms.getSymbolParts();
        symbolParts.splice(0, 1, function(){
            var obj = ms.___original_basegeometry.call(this,ms)
            var percent = parseFloat(this.options.fillPercent);
            console.log(percent);
            if(!isNaN(percent) && percent < 1){
                var drawArray2 = obj.post
                var newDrawArray2 = []
                var exec = false
                jQuery.each(drawArray2, function(idx, draw){
                    if(draw.type === 'path' && exec === false) {
                        exec = true
                        var override = Object.assign({}, draw);
                        var background = Object.assign({}, draw);
                        var fillcolor = Object.assign({}, draw);
                        var boundry = Object.assign({}, draw);
                        boundry.fill = "rgba(0,0,0,0)"
                        boundry.fillopacity = 0
                        var path = fillcolor.d
                        var xmin = 99999
                        var ymin = 99999
                        var xmax = 0
                        var ymax = 0
                        // 쉼표구분안됨...
                        if(path === "M 45,150 L 45,30,155,30,155,150"){
                            path = "M 45,150 L 45,30 155,30 155,150"
                        }
                        var pathcoords = path.split(" ")
                        var prevx, prevy
                        var startLine = false
                        var percentRate = 1
                        var clipPath = boundry.d
                        // 수중학적 밑으로 꺼짐 현상
                        // 상단 라인 잔상 현상
                        if(path === "m 45,50 c 0,100 40,120 55,120 15,0 55,-20 55,-120"){
                            percentRate = 0.43
                            clipPath = "m 45,49 c 0,100 40,120 55,120 15,0 55,-20 55,-120"
                        }
                        if(path === "M45,50 L45,130 100,180 155,130 155,50"){
                            clipPath = "M45,49 L45,130 100,180 155,130 155,49"
                        }

                        jQuery.each(pathcoords, function (idx, pathcoord) {
                            var xy = pathcoord.split(",")
                            if (/[lc]/.test(xy[0])) {
                                startLine = true
                            }
                            var x = parseInt(xy[0].replace(/[LCMVHl]/, ""))
                            var y = parseInt(xy[1])
                            if (startLine) {
                                x = prevx + x
                                y = prevy + y
                            }
                            if (x < xmin) xmin = x
                            if (y < ymin) ymin = y
                            if (x > xmax) xmax = x
                            if (y > ymax) ymax = y
                            if (!isNaN(x) && !isNaN(y)) {
                                prevx = x
                                prevy = y
                            }
                        })
                        override.d = "M " + xmin + "," + ymin + " H " + xmax + " V " + ymax + " H " + xmin + " V " + ymin + " Z"
                        override.clipPath = clipPath
                        override.fill = "rgb(255,255,255)"
                        override.stroke = "rgba(0,0,0,0)"
                        override.fillopacity = 1
                        if(ymin < 0){
                            ymin = 0
                        }
                        ymin = ymin - 3
                        ymax = Math.round(ymin + (((ymax * percentRate) - ymin) * (1 - percent)))
                        fillcolor.d = "M " + xmin + "," + ymin + " H " + xmax + " V " + ymax + " H " + xmin + " V " + ymin + " Z"
                        fillcolor.clipPath = clipPath
                        fillcolor.fill = "rgb(255,255,255)"
                        fillcolor.stroke = "rgba(0,0,0,0)"
                        fillcolor.fillopacity = 1
                        newDrawArray2.push(override);
                        newDrawArray2.push(background);
                        newDrawArray2.push(fillcolor);
                        newDrawArray2.push(boundry);
                    }else if(draw.type === 'circle' && exec === false){
                        exec = true
                        var centerx = draw.cx
                        var centery = draw.cy
                        var radius = draw.r
                        var clipPath = "M "+(centerx-radius)+", "+centery+" a "+radius+","+radius+" 0 1,0 "+(radius*2)+",0 a "+radius+","+radius+" 0 1,0 -"+(radius*2)+",0"
                        var xmin = centerx - radius
                        var ymin = centery - radius
                        var xmax = centerx + radius
                        var ymax = centery + radius
                        var background = Object.assign({}, draw);
                        var boundry = Object.assign({}, draw);
                        boundry.fill = "rgb(128,224,255,0)"
                        var override = {}
                        override.type = "path"
                        override.clipPath = clipPath
                        override.fill = "rgb(255,255,255)"
                        override.fillopacity = 1
                        override.stroke = "rgba(0,0,0,0)"
                        override.strokewidth = 0
                        override.d = "M " + xmin + "," + ymin + " H " + xmax + " V " + ymax + " H " + xmin + " V " + ymin + " Z"
                        var fillcolor = {}
                        fillcolor.type = "path"
                        fillcolor.clipPath = clipPath
                        fillcolor.fill = "rgb(255,255,255)"
                        fillcolor.fillopacity = 1
                        fillcolor.stroke = "rgba(0,0,0,0)"
                        fillcolor.strokewidth = 0
                        ymax = Math.round(ymin + ((ymax - ymin) * (1 - percent)))
                        fillcolor.d = "M " + xmin + "," + ymin + " H " + xmax + " V " + ymax + " H " + xmin + " V " + ymin + " Z"
                        newDrawArray2.push(override);
                        newDrawArray2.push(background);
                        newDrawArray2.push(fillcolor);
                        newDrawArray2.push(boundry);
                    }else{
                        newDrawArray2.push(draw)
                    }
                })
                obj.post = newDrawArray2
                return obj
            }else{
                return obj
            }
        })
        ms.setSymbolParts(symbolParts);
    }
}
milSymbolLoader.declare2DdrawControl = function(){
    milSymbolLoader.drawControl = new MapboxDraw({
        displayControlsDefault: false,
        userProperties: true,
        // 그리기 완료 후 inactive 심볼의 경우 투명 스타일 적용 위함.
        styles: [
            // default themes provided by MB Draw
            {
                'id': 'gl-draw-polygon-fill-inactive',
                'type': 'fill',
                'filter': ['all', ['==', 'active', 'false'],
                    ['==', '$type', 'Polygon'],
                    ['!=', 'mode', 'static']
                ],
                'paint': {
                    'fill-color': 'rgba(0, 0, 0, 0)', /* 투명적용 */
                    'fill-outline-color': 'rgba(0, 0, 0, 0)', /* 투명적용 */
                    'fill-opacity': 0.1
                }
            },
            {
                'id': 'gl-draw-polygon-fill-active',
                'type': 'fill',
                'filter': ['all', ['==', 'active', 'true'],
                    ['==', '$type', 'Polygon']
                ],
                'paint': {
                    'fill-color': '#fbb03b',
                    'fill-outline-color': '#fbb03b',
                    'fill-opacity': 0.1
                }
            },
            {
                'id': 'gl-draw-polygon-midpoint',
                'type': 'circle',
                'filter': ['all', ['==', '$type', 'Point'],
                    ['==', 'meta', 'midpoint']
                ],
                'paint': {
                    'circle-radius': 3,
                    'circle-color': '#fbb03b'
                }
            },
            {
                'id': 'gl-draw-polygon-stroke-inactive',
                'type': 'line',
                'filter': ['all', ['==', 'active', 'false'],
                    ['==', '$type', 'Polygon'],
                    ['!=', 'mode', 'static']
                ],
                'layout': {
                    'line-cap': 'round',
                    'line-join': 'round'
                },
                'paint': {
                    'line-color': '#3bb2d0',
                    'line-width': 2
                }
            },
            {
                'id': 'gl-draw-polygon-stroke-active',
                'type': 'line',
                'filter': ['all', ['==', 'active', 'true'],
                    ['==', '$type', 'Polygon']
                ],
                'layout': {
                    'line-cap': 'round',
                    'line-join': 'round'
                },
                'paint': {
                    'line-color': '#fbb03b',
                    'line-dasharray': [0.2, 2],
                    'line-width': 2
                }
            },
            {
                'id': 'gl-draw-line-inactive',
                'type': 'line',
                'filter': ['all', ['==', 'active', 'false'],
                    ['==', '$type', 'LineString'],
                    ['!=', 'mode', 'static']
                ],
                'layout': {
                    'line-cap': 'round',
                    'line-join': 'round'
                },
                'paint': {
                    'line-color': 'rgba(0, 0, 0, 0)', /* 투명적용 */
                    'line-width': 2
                }
            },
            {
                'id': 'gl-draw-line-active',
                'type': 'line',
                'filter': ['all', ['==', '$type', 'LineString'],
                    ['==', 'active', 'true']
                ],
                'layout': {
                    'line-cap': 'round',
                    'line-join': 'round'
                },
                'paint': {
                    'line-color': '#fbb03b',
                    'line-dasharray': [0.2, 2],
                    'line-width': 2
                }
            },
            {
                'id': 'gl-draw-polygon-and-line-vertex-stroke-inactive',
                'type': 'circle',
                'filter': ['all', ['==', 'meta', 'vertex'],
                    ['==', '$type', 'Point'],
                    ['!=', 'mode', 'static']
                ],
                'paint': {
                    'circle-radius': 5,
                    'circle-color': '#fff'
                }
            },
            {
                'id': 'gl-draw-polygon-and-line-vertex-inactive',
                'type': 'circle',
                'filter': ['all', ['==', 'meta', 'vertex'],
                    ['==', '$type', 'Point'],
                    ['!=', 'mode', 'static']
                ],
                'paint': {
                    'circle-radius': 3,
                    'circle-color': '#fbb03b'
                }
            },
            {
                'id': 'gl-draw-point-point-stroke-inactive',
                'type': 'circle',
                'filter': ['all', ['==', 'active', 'false'],
                    ['==', '$type', 'Point'],
                    ['==', 'meta', 'feature'],
                    ['!=', 'mode', 'static']
                ],
                'paint': {
                    'circle-radius': 5,
                    'circle-opacity': 1,
                    'circle-color': 'rgba(0, 0, 0, 0)', /* 투명적용 */
                }
            },
            {
                'id': 'gl-draw-point-inactive',
                'type': 'circle',
                'filter': ['all', ['==', 'active', 'false'],
                    ['==', '$type', 'Point'],
                    ['==', 'meta', 'feature'],
                    ['!=', 'mode', 'static']
                ],
                'paint': {
                    'circle-radius': 3,
                    'circle-color': 'rgba(0, 0, 0, 0)', /* 투명적용 */
                }
            },
            {
                'id': 'gl-draw-point-stroke-active',
                'type': 'circle',
                'filter': ['all', ['==', '$type', 'Point'],
                    ['==', 'active', 'true'],
                    ['!=', 'meta', 'midpoint']
                ],
                'paint': {
                    'circle-radius': 7,
                    'circle-color': '#fff'
                }
            },
            {
                'id': 'gl-draw-point-active',
                'type': 'circle',
                'filter': ['all', ['==', '$type', 'Point'],
                    ['!=', 'meta', 'midpoint'],
                    ['==', 'active', 'true']
                ],
                'paint': {
                    'circle-radius': 5,
                    'circle-color': '#fbb03b'
                }
            },
            {
                'id': 'gl-draw-polygon-fill-static',
                'type': 'fill',
                'filter': ['all', ['==', 'mode', 'static'],
                    ['==', '$type', 'Polygon']
                ],
                'paint': {
                    'fill-color': '#404040',
                    'fill-outline-color': '#404040',
                    'fill-opacity': 0.1
                }
            },
            {
                'id': 'gl-draw-polygon-stroke-static',
                'type': 'line',
                'filter': ['all', ['==', 'mode', 'static'],
                    ['==', '$type', 'Polygon']
                ],
                'layout': {
                    'line-cap': 'round',
                    'line-join': 'round'
                },
                'paint': {
                    'line-color': '#404040',
                    'line-width': 2
                }
            },
            {
                'id': 'gl-draw-line-static',
                'type': 'line',
                'filter': ['all', ['==', 'mode', 'static'],
                    ['==', '$type', 'LineString']
                ],
                'layout': {
                    'line-cap': 'round',
                    'line-join': 'round'
                },
                'paint': {
                    'line-color': '#404040',
                    'line-width': 2
                }
            },
            {
                'id': 'gl-draw-point-static',
                'type': 'circle',
                'filter': ['all', ['==', 'mode', 'static'],
                    ['==', '$type', 'Point']
                ],
                'paint': {
                    'circle-radius': 5,
                    'circle-color': '#404040'
                }
            },
            // end default themes provided by MB Draw
        ]
    });
}
/**
 * 군대부호 변경
 * @param evt
 */
milSymbolLoader.milsymbolsPreview = function(evt){
    var source = milSymbolLoader.map.getSource('milsymbols-source-feature')
    if(source === undefined){
        return
    }
    var features = evt.features
    var sourceData = source._data
    jQuery.each(features, function(idx, feature){
        var drawId = feature.id
        var drawGeometry = feature.geometry
        sourceData = milSymbolLoader.milsymbolsChangeSourceData(sourceData,drawId,drawGeometry,"move")
    })
    if(sourceData !== null){
        source.setData(sourceData)
    }
}
/**
 * 군대부호 직접그리기
 * @param sidc
 * @param points [x,y] or [[x,y]] or [[x1,y1],[x2,y2],[x3,y3]]
 * ex)
 * milSymbolLoader.draw("SFG*IGA---H****",[127.02712249755984, 36.992322299618536])
 * milSymbolLoader.draw("SFG*IGA---H****",[[127.17818450927143, 37.06413332353205]])
 * milSymbolLoader.draw("GFT-K-----****X",[[126.85958099365331,37.07673435836169],[126.9701309204122,36.99396758638515],[127.05046844482507,37.0153530751237]])
 */
milSymbolLoader.draw = function(sidc,points){
    if(points === null || points === undefined || points.length === 0){
        console.warn("좌표정보를 입력해 주세요.")
        return
    }
    if(points instanceof Array && points.length === 1 && points[0] instanceof Array){
        points = [points[0][0], points[0][1]]
    }
    var geometryType = null
    if(points.length === 2 && typeof points[0] == 'number' && typeof points[1] == 'number'){
        geometryType =  "Point"
    }else if (points.length >= 2 && points[0] instanceof Array){
        geometryType =  "LineString"
    }
    if(geometryType === null){
        console.error("좌표정보가 잘못되었습니다.", points)
        return
    }
    //
    window.function_sidc = sidc
    //
    var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 3 | 8);
        return v.toString(16);
    })
    //
    var options = milSymbolLoader.getMilsymbolOptions()
    milSymbolLoader.drawMilsymbol(options,true)
    //
    var evt = {}
    evt.features = []
    evt.features[0] = {}
    evt.features[0].id = uuid
    evt.features[0].geometry = {}
    evt.features[0].geometry.type = geometryType
    evt.features[0].geometry.coordinates = []
    evt.features[0].geometry.coordinates = points
    milSymbolLoader.milsymbolsGenerator(evt)
}
milSymbolLoader.milsymbolsGenerator = function(evt){
    var features = evt.features
    var drawId = features[0].id
    var geometryType = features[0].geometry.type
    var coord = features[0].geometry.coordinates
    //
    var options = milSymbolLoader.map._drawing_milsymbol.options
    var constraint = options._constraint
    var coordinates = milSymbolLoader.map._drawing_milsymbol_coordinates
    var datas = []
    if(geometryType === "Point"){
        if(constraint === "milSym"){
            var imageData = milSymbolLoader.map._drawing_milsymbol.asCanvas().toDataURL('image/png')
            /*milSymbolLoader.map.loadImage(imageData,function(e,image){
                milSymbolLoader.map.addImage(drawId + "-image", image)
            })*/
            var image = new Image();
            image.crossOrigin = 'anonymous';
            image.addEventListener('load', function(e) {
                milSymbolLoader.map.addImage(drawId + "-image", image)
            });
            image.src = imageData;
        }else{
            var cs = milSymbolLoader.getCS(function_sidc)
            drawMsymbol(options._symbol_serial, 'SVG', null, symStd, cs, function_sidc);
            if(jQuery("#svg-draw").length == 0){
                jQuery("body").css("overflow",'hidden')
                jQuery("body").append("<div id='svg-draw'></div>")
            }
            jQuery("#svg-draw").empty()
            jQuery("#svg-draw").append(milSymbolLoader.map._drawing_milsymbol._svg_symbol.getSVG())
            var imageData = 'data:image/svg+xml;base64,' + btoa(unescape(encodeURIComponent(milSymbolLoader.map._drawing_milsymbol._svg_symbol.getSVG())));
            /*html2canvas(jQuery("#svg-draw svg")[0],{backgroundColor: "rgba(0,0,0,0)"}).then(function(canvas){
                milSymbolLoader.map.loadImage(canvas.toDataURL(),function(e,image){
                    milSymbolLoader.map.addImage(drawId + "-image", image)
                })
            })*/

            var image = new Image();
            image.crossOrigin = 'anonymous';
            image.addEventListener('load', function(e) {
                milSymbolLoader.map.addImage(drawId + "-image", image)
            });
            image.src = imageData;
        }
        datas.push({
            type: 'Feature',
            properties: {
                type: 'Image',
                imageId: drawId + "-image",
                drawId : drawId,
                _constraint : constraint
            },
            geometry: {
                type: 'Point',
                coordinates: coord
            }
        })
    }else if(geometryType === "LineString"){
        jQuery.each(coord, function(idx, point){
            coordinates.push({x:point[0],y:point[1]})
        })
        var cs = milSymbolLoader.getCS(function_sidc)
        drawMsymbol(options._symbol_serial, 'geoJSON', null, symStd, cs, function_sidc)
        var geojson = milSymbolLoader.map._drawing_milsymbol._geojson
        var data = JSON.parse(geojson)
        if(data.properties.symbolID === "GFG-GLA---****X"){
            // 작전활동부호-화력지원-선-적함선소실선
            var point1 = turf.point(coord[coord.length - 2]);
            var point2 = turf.point(coord[coord.length - 1]);
            var bearing = turf.bearing(point1, point2);
            var angle = -(-90 + bearing)
            var svg = '<svg width="120px" height="120px" preserveAspectRatio="none" xmlns="http://www.w3.org/2000/svg" version="1.1"><g transform="translate(60,60) scale(0.1,-0.1) rotate('+angle+' 0 0)" ><path d="M91.9 465.4c-40-10.4-68.3-28.5-87.2-56.5c-27.2-39.5-16.5-86.4 26.6-118.8c25.5-19.5 64.2-33.5 83.1-30.4 c4.9 0.8 12.3 0.5 16.5-0.8s16.2-4.4 26.6-6.9c84.2-20 102.9-87.5 35.9-128.1c-17-10.1-44.4-17.3-76.8-19.5 c-49.6-3.6-94.9-29.1-116-64.7C-6 28.2-6.9 24.6-6.9-0.3c0-29.9 3.6-40.3 19.7-57.6c10.1-11 37.9-31.5 42.5-31.5 c2.2 0 6-1.6 9.1-3.6c8.2-5.2 44.4-13.7 51.3-12.1c9.1 2.5 53.5-8.5 72.1-17.8c18.9-9.3 29.3-18.4 38.9-33.5 c9.3-14.5 9.3-38.1 0-52.9c-19.2-30.4-60.3-48.3-117.1-51.3c-41.4-2.2-87.2-28.3-108.6-62.3c-3.3-5.5-7.7-17.6-9.6-26.6 c-6.6-31.8 6-61.2 37.3-86.4c15.9-12.9 36.5-23.9 44.4-23.9c2.2 0 7.1-1.6 11.2-3.8c4.1-1.9 13.2-4.4 20.3-5.2 c15.6-2.2 22.2 2.7 20.8 15.6c-0.8 8-1.6 8.5-14.5 10.4c-46.6 6-83.1 30.7-93.3 63.1c-4.7 14.3-4.7 15.6 0 29.9 c3.6 11 8.8 18.7 20.3 30.2c24.1 23.6 53.2 34.6 99 37c48.3 2.7 99.8 32.4 115.7 66.7c8.2 17.6 8.2 48.8 0 66.4 c-6.6 14-32.4 39.5-47.5 46.9c-28.5 14-54.9 21.7-73.2 21.4c-16.2-0.3-27.2 1.4-40.6 5.5C71.9-69.4 46.9-56 36.8-46.1 c-9.9 9.6-23 34.6-23 44.2c0 12.9 9.9 32.4 23 45.5C51.6 58.4 82 75.1 94.1 75.1c3.8 0 12.6 1.6 18.9 3.8 c6.6 1.9 19.5 3.8 28.8 3.8c42.5 0.5 95.2 31.8 110.8 65.6c9.3 20.3 8.8 49.9-1.6 68.8c-19.7 36.5-68.3 62.8-119.3 65 c-40.9 1.6-85.3 20.8-103.1 43.9c-3.6 4.7-8.5 15.1-11 22.8c-4.1 12.1-4.1 16.2-1.4 26.1c5.5 17.8 13.2 28.3 29.9 41.1 c19.2 14.3 31.8 19.7 56.8 24.1c10.7 1.6 20.8 3.6 22.8 4.1c3.8 1.1 4.4 9.1 0.5 18.7C123.2 471.4 116.8 471.7 91.9 465.4z M318.2 464.6c-24.7-6.9-40-14-56.8-26.9c-16.5-12.3-34.8-34.3-37.3-44.4c-0.8-3.8-3-12.6-4.9-19.5 c-4.9-19.2 4.4-46.9 22.5-66.7c25.8-28.3 56.8-42.5 108.1-49.6c43.6-6.3 67.5-16.2 88-36.8c25-25 22.8-60.1-5.2-83.9 c-17.8-15.4-49.4-28.8-67.5-28.8c-12.9 0-55.7-9.3-72.4-15.6c-18.4-6.9-43.9-26.3-56.2-43.1c-7.1-9.3-12.3-20.6-15.6-33.2 c-4.9-18.7-4.9-19.7 0-35.7c6.3-20.3 16.7-35.9 33.5-50.2c13.2-11.5 37-24.4 51.6-28.3c4.7-1.1 13.2-3.6 19.2-4.9 c6-1.6 15.4-3 20.6-3c11 0 46.9-8.5 59-14c14.5-6.3 32.6-21.7 41.7-34.8c20.8-30.4 5.5-66.4-37.6-88.3c-15.4-8-24.4-10.4-46.6-13.7 c-48.3-6.9-57.1-9.1-75.2-17.8c-34.6-16.5-56.5-40.3-66.1-71c-5.5-18.1-3.6-33.7 7.1-56c15.1-31 59.2-58.7 108.3-67.7 c11.2-2.2 18.1 4.4 17 16.2c-0.8 8-1.6 8.5-14.5 10.1c-29.1 3.6-68.3 23.3-80.6 40.3c-20.8 29.3-18.4 56.8 7.7 82.8 c18.1 18.1 42 28.8 77.1 34.3c47.2 7.4 55.1 9.3 75.7 19.2c23.6 11.5 49.9 36.2 55.7 52.1c1.9 6 4.4 19.7 4.9 30.2 c1.4 24.7-5.2 40.6-25.5 60.6c-24.1 23.6-70.5 42-105.3 42.2c-8.2 0-18.7 1.9-23 4.1c-4.1 2.2-9.9 4.1-12.3 4.1 c-11.5 0.3-43.6 20.8-54 34.6c-21.7 28.3-18.4 59.2 8.5 84.5c21.7 20.6 41.7 28.3 94.9 35.7C433.6 91.8 480 131.1 480 181.2 c0 51.3-53.2 94.6-124 100.9c-37.3 3.6-67.7 15.4-89.1 35.1c-29.9 27.4-29.9 63.4-0.5 91.3c18.4 17.3 36.5 26.1 64.2 31l24.7 4.7 l0.8 9.1C357.7 470.9 349.4 473.4 318.2 464.6z" fill="#000000" /></g></svg>'
            var imageData = btoa(unescape(encodeURIComponent(svg)))
            var image = new Image();
            image.crossOrigin = 'anonymous';
            image.addEventListener('load', function(e) {
                milSymbolLoader.map.addImage(drawId + "-image", image)
            });
            image.src = "data:image/svg+xml;base64," + imageData;
            datas.push({
                type: 'Feature',
                properties: {
                    type: 'Image',
                    imageId: drawId + "-image",
                    drawId : drawId,
                    _constraint : "GFG-GLA---****X"
                },
                geometry: {
                    type: 'Point',
                    coordinates: coord[coord.length - 1]
                }
            })
        }
        jQuery.each(data.features, function(idx, feature){
            feature.properties._symStd = symStd
            feature.properties._cs = cs
            feature.properties._function_sidc = function_sidc
            feature.properties.drawId = drawId
            if(feature.geometry.type === "Point"){
                feature.properties.type = "Label"
                feature.properties.labelOffset = [feature.properties.labelXOffset,feature.properties.labelYOffset]
                feature.properties.labelAlign = (feature.properties.labelAlign === "right" ? "right" : feature.properties.labelAlign === "left" ? "left" : feature.properties.labelAlign === "center" ? "center" : "center")
            }
        })
        datas = datas.concat(data.features);
    }
    // draw milsymbol
    var source = milSymbolLoader.map.getSource('milsymbols-source-feature')
    if(source === undefined){
        milSymbolLoader.map.addSource('milsymbols-source-feature', {
            type: jiConstant.MAPBOX_SOURCE_TYPE.GEOJSON,
            data: {
                type: 'FeatureCollection',
                features: datas
            }
        })
        source = milSymbolLoader.map.getSource('milsymbols-source-feature')
    }else{
        var sourceData = source._data
        sourceData.features = sourceData.features.concat(datas)
        source.setData(sourceData)
    }
    // 단일심볼 레이어
    var imageLayer = milSymbolLoader.map.getLayer('milsymbols-layer-image')
    if(imageLayer === undefined){
        milSymbolLoader.map.addLayer({
            id: 'milsymbols-layer-image',
            type: 'symbol',
            source: source.id,
            layout: {
                'icon-image': ['get', 'imageId'],
                'icon-allow-overlap': true,
                'icon-ignore-placement' : true,
            },
            filter: ["all", ['==', '$type', 'Point'], ["==", "type", 'Image']]
        },"gl-draw-polygon-fill-inactive.cold")
    }
    // 라인 레이어
    var lineLayer = milSymbolLoader.map.getLayer('milsymbols-layer-line')
    if(lineLayer === undefined){
        milSymbolLoader.map.addLayer({
            id: 'milsymbols-layer-line',
            type: 'line',
            source: source.id,
            paint: {
                "line-color": ['get', 'strokeColor'],
                "line-width": ['get', 'strokeWidth']
            },
            filter: ['==', '$type', 'LineString']
        },"gl-draw-polygon-fill-inactive.cold")
    }
    // 면 레이어
    var fillLayer = milSymbolLoader.map.getLayer('milsymbols-layer-fill')
    if(fillLayer === undefined){
        milSymbolLoader.map.addLayer({
            id: 'milsymbols-layer-fill',
            type: 'fill',
            source: source.id,
            paint: {
                "fill-color": ['get', 'fillColor']
            },
            filter: ['==', '$type', 'Polygon']
        },"gl-draw-polygon-fill-inactive.cold")
    }

    // 라벨 레이어
    var labelLayerLeft = milSymbolLoader.map.getLayer('milsymbols-layer-label-left')
    if(labelLayerLeft === undefined){
        milSymbolLoader.map.addLayer({
            id: 'milsymbols-layer-label-left',
            type: 'symbol',
            source: source.id,
            layout: {
                'text-field': ['get', 'label'],
                'text-allow-overlap' : true,
                'text-ignore-placement' : true,
                "text-size": ['get', 'fontSize'],
                'text-rotate': ['get','angle'],
                'text-variable-anchor': ['left','top'],
                "text-font": ["Gosanja"]
            },
            paint: {
                "text-color": ['get', 'fontColor'],
                "text-halo-color": ['get', 'labelOutlineColor'],
                "text-halo-width": ['get', 'labelOutlineWidth'],
            },
            filter: ["all", ['==', '$type', 'Point'], ["==", "type", 'Label'], ["==", "labelAlign", 'left']]
        },"gl-draw-polygon-fill-inactive.cold")
    }
    var labelLayerRight = milSymbolLoader.map.getLayer('milsymbols-layer-label-right')
    if(labelLayerRight === undefined){
        milSymbolLoader.map.addLayer({
            id: 'milsymbols-layer-label-right',
            type: 'symbol',
            source: source.id,
            layout: {
                'text-field': ['get', 'label'],
                'text-allow-overlap' : true,
                'text-ignore-placement' : true,
                "text-size": ['get', 'fontSize'],
                'text-rotate': ['get','angle'],
                'text-variable-anchor': ['right','bottom'],
                "text-font": ["Gosanja"]
            },
            paint: {
                "text-color": ['get', 'fontColor'],
                "text-halo-color": ['get', 'labelOutlineColor'],
                "text-halo-width": ['get', 'labelOutlineWidth'],
            },
            filter: ["all", ['==', '$type', 'Point'], ["==", "type", 'Label'], ["==", "labelAlign", 'right']]
        },"gl-draw-polygon-fill-inactive.cold")
    }
    var labelLayerCenter = milSymbolLoader.map.getLayer('milsymbols-layer-label-center')
    if(labelLayerCenter === undefined){
        milSymbolLoader.map.addLayer({
            id: 'milsymbols-layer-label-center',
            type: 'symbol',
            source: source.id,
            layout: {
                'text-field': ['get', 'label'],
                'text-allow-overlap' : true,
                'text-ignore-placement' : true,
                "text-size": ['get', 'fontSize'],
                'text-rotate': ['get','angle'],
                'text-variable-anchor': ['center'],
                "text-font": ["Gosanja"]
            },
            paint: {
                "text-color": ['get', 'fontColor'],
                "text-halo-color": ['get', 'labelOutlineColor'],
                "text-halo-width": ['get', 'labelOutlineWidth'],
            },
            filter: ["all", ['==', '$type', 'Point'], ["==", "type", 'Label'], ["==", "labelAlign", 'center']]
        },"gl-draw-polygon-fill-inactive.cold")
    }
}
/**
 * 군대부호 소스 데이터 변경
 * @param sourceData
 * @param drawId
 * @param drawGeometry
 * @param type
 * @returns {null|*}
 */
milSymbolLoader.milsymbolsChangeSourceData = function(sourceData, drawId, drawGeometry, type){
    var features = sourceData.features
    var removeFeatures = []
    var symStd, cs, function_sidc
    jQuery.each(features, function(idx, feature){
        if(feature.properties.drawId !== drawId){
            return
        }
        var constraint = feature.properties._constraint
        if(feature.geometry.type === "Point" && feature.properties.type !== "Label") {
            if(type === "move"){
                if(constraint === "GFG-GLA---****X"){
                    if(milSymbolLoader.map.hasImage(drawId + "-image")){
                        milSymbolLoader.map.removeImage(drawId + "-image")
                    }
                    // 작전활동부호-화력지원-선-적함선소실선
                    var coord = drawGeometry.coordinates
                    var point1 = turf.point(coord[coord.length - 2]);
                    var point2 = turf.point(coord[coord.length - 1]);
                    var bearing = turf.bearing(point1, point2);
                    var angle = -(-90 + bearing)
                    var svg = '<svg width="120px" height="120px" preserveAspectRatio="none" xmlns="http://www.w3.org/2000/svg" version="1.1"><g transform="translate(60,60) scale(0.1,-0.1) rotate('+angle+' 0 0)" ><path d="M91.9 465.4c-40-10.4-68.3-28.5-87.2-56.5c-27.2-39.5-16.5-86.4 26.6-118.8c25.5-19.5 64.2-33.5 83.1-30.4 c4.9 0.8 12.3 0.5 16.5-0.8s16.2-4.4 26.6-6.9c84.2-20 102.9-87.5 35.9-128.1c-17-10.1-44.4-17.3-76.8-19.5 c-49.6-3.6-94.9-29.1-116-64.7C-6 28.2-6.9 24.6-6.9-0.3c0-29.9 3.6-40.3 19.7-57.6c10.1-11 37.9-31.5 42.5-31.5 c2.2 0 6-1.6 9.1-3.6c8.2-5.2 44.4-13.7 51.3-12.1c9.1 2.5 53.5-8.5 72.1-17.8c18.9-9.3 29.3-18.4 38.9-33.5 c9.3-14.5 9.3-38.1 0-52.9c-19.2-30.4-60.3-48.3-117.1-51.3c-41.4-2.2-87.2-28.3-108.6-62.3c-3.3-5.5-7.7-17.6-9.6-26.6 c-6.6-31.8 6-61.2 37.3-86.4c15.9-12.9 36.5-23.9 44.4-23.9c2.2 0 7.1-1.6 11.2-3.8c4.1-1.9 13.2-4.4 20.3-5.2 c15.6-2.2 22.2 2.7 20.8 15.6c-0.8 8-1.6 8.5-14.5 10.4c-46.6 6-83.1 30.7-93.3 63.1c-4.7 14.3-4.7 15.6 0 29.9 c3.6 11 8.8 18.7 20.3 30.2c24.1 23.6 53.2 34.6 99 37c48.3 2.7 99.8 32.4 115.7 66.7c8.2 17.6 8.2 48.8 0 66.4 c-6.6 14-32.4 39.5-47.5 46.9c-28.5 14-54.9 21.7-73.2 21.4c-16.2-0.3-27.2 1.4-40.6 5.5C71.9-69.4 46.9-56 36.8-46.1 c-9.9 9.6-23 34.6-23 44.2c0 12.9 9.9 32.4 23 45.5C51.6 58.4 82 75.1 94.1 75.1c3.8 0 12.6 1.6 18.9 3.8 c6.6 1.9 19.5 3.8 28.8 3.8c42.5 0.5 95.2 31.8 110.8 65.6c9.3 20.3 8.8 49.9-1.6 68.8c-19.7 36.5-68.3 62.8-119.3 65 c-40.9 1.6-85.3 20.8-103.1 43.9c-3.6 4.7-8.5 15.1-11 22.8c-4.1 12.1-4.1 16.2-1.4 26.1c5.5 17.8 13.2 28.3 29.9 41.1 c19.2 14.3 31.8 19.7 56.8 24.1c10.7 1.6 20.8 3.6 22.8 4.1c3.8 1.1 4.4 9.1 0.5 18.7C123.2 471.4 116.8 471.7 91.9 465.4z M318.2 464.6c-24.7-6.9-40-14-56.8-26.9c-16.5-12.3-34.8-34.3-37.3-44.4c-0.8-3.8-3-12.6-4.9-19.5 c-4.9-19.2 4.4-46.9 22.5-66.7c25.8-28.3 56.8-42.5 108.1-49.6c43.6-6.3 67.5-16.2 88-36.8c25-25 22.8-60.1-5.2-83.9 c-17.8-15.4-49.4-28.8-67.5-28.8c-12.9 0-55.7-9.3-72.4-15.6c-18.4-6.9-43.9-26.3-56.2-43.1c-7.1-9.3-12.3-20.6-15.6-33.2 c-4.9-18.7-4.9-19.7 0-35.7c6.3-20.3 16.7-35.9 33.5-50.2c13.2-11.5 37-24.4 51.6-28.3c4.7-1.1 13.2-3.6 19.2-4.9 c6-1.6 15.4-3 20.6-3c11 0 46.9-8.5 59-14c14.5-6.3 32.6-21.7 41.7-34.8c20.8-30.4 5.5-66.4-37.6-88.3c-15.4-8-24.4-10.4-46.6-13.7 c-48.3-6.9-57.1-9.1-75.2-17.8c-34.6-16.5-56.5-40.3-66.1-71c-5.5-18.1-3.6-33.7 7.1-56c15.1-31 59.2-58.7 108.3-67.7 c11.2-2.2 18.1 4.4 17 16.2c-0.8 8-1.6 8.5-14.5 10.1c-29.1 3.6-68.3 23.3-80.6 40.3c-20.8 29.3-18.4 56.8 7.7 82.8 c18.1 18.1 42 28.8 77.1 34.3c47.2 7.4 55.1 9.3 75.7 19.2c23.6 11.5 49.9 36.2 55.7 52.1c1.9 6 4.4 19.7 4.9 30.2 c1.4 24.7-5.2 40.6-25.5 60.6c-24.1 23.6-70.5 42-105.3 42.2c-8.2 0-18.7 1.9-23 4.1c-4.1 2.2-9.9 4.1-12.3 4.1 c-11.5 0.3-43.6 20.8-54 34.6c-21.7 28.3-18.4 59.2 8.5 84.5c21.7 20.6 41.7 28.3 94.9 35.7C433.6 91.8 480 131.1 480 181.2 c0 51.3-53.2 94.6-124 100.9c-37.3 3.6-67.7 15.4-89.1 35.1c-29.9 27.4-29.9 63.4-0.5 91.3c18.4 17.3 36.5 26.1 64.2 31l24.7 4.7 l0.8 9.1C357.7 470.9 349.4 473.4 318.2 464.6z" fill="#000000" /></g></svg>'
                    var imageData = btoa(unescape(encodeURIComponent(svg)))
                    var image = new Image();
                    image.crossOrigin = 'anonymous';
                    image.addEventListener('load', function(e) {
                        milSymbolLoader.map.addImage(drawId + "-image", image)
                    });
                    image.src = "data:image/svg+xml;base64," + imageData;
                    feature.geometry = {
                        type: 'Point',
                        coordinates: coord[coord.length - 1]
                    }
                }else{
                    feature.geometry = drawGeometry
                }
            }else if(type === "draw"){
                if(milSymbolLoader.map.hasImage(drawId + "-image")){
                    milSymbolLoader.map.removeImage(drawId + "-image")
                }
                if(constraint === "milSym") {
                    var imageData = milSymbolLoader.map._drawing_milsymbol.asCanvas().toDataURL()
                    milSymbolLoader.map.loadImage(imageData, function (e, image) {
                        milSymbolLoader.map.addImage(drawId + "-image", image)
                    })
                }else{
                    var _cs = milSymbolLoader.getCS(window.function_sidc)
                    drawMsymbol(-1, 'SVG', null, window.symStd, _cs, window.function_sidc);
                    if(jQuery("#svg-draw").length == 0){
                        jQuery("body").css("overflow",'hidden')
                        jQuery("body").append("<div id='svg-draw'></div>")
                    }
                    jQuery("#svg-draw").empty()
                    jQuery("#svg-draw").append(milSymbolLoader.map._drawing_milsymbol._svg_symbol.getSVG())
                    html2canvas(jQuery("#svg-draw svg")[0],{backgroundColor: "rgba(0,0,0,0)"}).then(function(canvas){
                        milSymbolLoader.map.loadImage(canvas.toDataURL(),function(e,image){
                            milSymbolLoader.map.addImage(drawId + "-image", image)
                        })
                    })
                }
                feature.properties.imageId = drawId + "-image"
            }
        }else{
            feature.position = idx
            symStd = feature.properties._symStd
            cs = feature.properties._cs
            function_sidc = feature.properties._function_sidc
            removeFeatures.splice( 0, 0, feature)
        }
    })
    if(removeFeatures.length > 0){
        jQuery.each(removeFeatures, function(idx, removeFeature){
            sourceData.features.splice(removeFeature.position, 1)
        })
        //
        var coord = drawGeometry.coordinates
        milSymbolLoader.map._drawing_milsymbol_coordinates = []
        var coordinates = milSymbolLoader.map._drawing_milsymbol_coordinates
        jQuery.each(coord, function(idx, point){
            coordinates.push({x:point[0],y:point[1]})
        })
        drawMsymbol(-1, 'geoJSON', null, symStd, cs, function_sidc)
        var data = JSON.parse(milSymbolLoader.map._drawing_milsymbol._geojson)
        jQuery.each(data.features, function(idx, feature){
            feature.properties.drawId = drawId
            feature.properties._symStd = symStd
            feature.properties._cs = cs
            feature.properties._function_sidc = function_sidc
            if(feature.geometry.type === "Point"){
                feature.properties.type = "Label"
                feature.properties.labelOffset = [feature.properties.labelXOffset,feature.properties.labelYOffset]
            }
        })
        sourceData.features = sourceData.features.concat(data.features)
    }
    return sourceData
}
/**
 * drawMilsymbol
 * @param options
 */
milSymbolLoader.drawMilsymbol = function(options, onlySetValue){
    if(options === null || options === undefined){
        toastr.error("MilSymbol options 값이 없습니다.")
        return
    }
    var symbol = {}
    var sidc = options.SIDC
    if (sidc.charAt(0) === 'W' || sidc.charAt(0) === 'G') {
        var drawInfo = getDrawGraphicsInfo(sidc);
        if (drawInfo === undefined || drawInfo.draw_type === '') {
            toastr.warning("군대부호["+sidc+"] 의 정보를 가져올 수 없습니다.")
            return;
        }
        var mode = "draw_line_string"

        if(drawInfo.draw_type === 'Point'){
            mode = "draw_point"
            symbol = new ms.Symbol(options) // 심볼생성
        }else{
            symbol.options = {}
        }
        symbol.options._min_point = drawInfo.min_point
        symbol.options._max_point = drawInfo.max_point
        symbol.options._draw_type = drawInfo.draw_type
        symbol.options._constraint = drawInfo.constraint
        if(onlySetValue !== true){
            if (!jiCommon.checkMapKind()) {
                // 맵박스
                milSymbolLoader.drawControl.changeMode(mode)
            } else {
                // 세슘
                jiCommon.map.drawControlMode(mode,symbol.options._constraint)
            }
        }
    } else {
        symbol = new ms.Symbol(options) // 심볼생성
        symbol.options._min_point = 1
        symbol.options._max_point = 1
        symbol.options._draw_type = "Point"
        symbol.options._constraint = "milSym"
        if(onlySetValue !== true){
            if(!jiCommon.checkMapKind()) {
                // 맵박스
                milSymbolLoader.drawControl.changeMode("draw_point")
            }else {
                // 세슘
                jiCommon.map.drawControlMode("draw_point",symbol.options._constraint)
            }
        }
    }
    symbol.options._symbol_serial = (milSymbolLoader.map._drawing_milsymbol ? milSymbolLoader.map._drawing_milsymbol.options._symbol_serial + 1 : 0)
    // 심볼생성
    milSymbolLoader.map._drawing_milsymbol = symbol

    milSymbolLoader.map._drawing_milsymbol_coordinates = []
}
/**
 // S---------***** / 기본군대부호
 // G*--------****X / 작전활동부호
 // W-------------- / 기상 및 해양
 // I-------------- / 신호정보
 // O*--------***** / 안정화작전
 // E-------------- / 비상관리
 // S*Z*------***** / 미식별
 * @param sidc
 */
milSymbolLoader.getCS = function(sidc){
    var first = sidc.charAt(0)
    if(first === 'S') return 'S---------*****'
    else if(first === 'G') return 'G*--------****X'
    else if(first === 'W') return 'W--------------'
    else if(first === 'I') return 'I--------------'
    else if(first === 'O') return 'O*--------*****'
    else if(first === 'E') return 'E--------------'
    else return 'S*Z*------*****'
}

milSymbolLoader.getMilsymbolOptions = function() {
    if ( bChangeID ) { // 20200305 부호 변경 시 기존 수식정보 모두 초기화. 초기화가 되지 않을 경우 변경하지 않아야 할 정보도 자동으로 변경되어 버림.
        // A:기본부호지정, B:부대단위, C:장비수량, D:기동부대식별,
        // F:부대증감, G:군 및 국가 구분, H:추가사항, H1:추가사항, H2:추가사항,
        // J:평가등급, K:전투효과, L:신호정보장비, M:상급부대, N:적군표시,
        // P:피아식별모드/코드, Q:이동방향, R:이동수단, R2:신호정보 장비 이동성,
        // S:지휘소표시/실제위치표시, T:고유명칭, T1:고유명칭1, V:장비명, W:활동시각, W1:활동시각1,
        // X:고도/심도, X1:고도/심도1, XN:고도/심도[], Y:위치, Z:속도, AA:지휘통제소, AB:가장/가상식별부호,
        // AD:기반형태, AE:장비분해시간, AF:공통명칭, AG:보조장비 식별부호,
        // AH:불확정영역, AI:선위의 추측선, AJ:속도선, AM:거리(미터), AN:각도(도)
        var propNm = ['B', 'C', 'D', 'F', 'G', 'H', 'H1', 'H2', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'R2', 'S', 'T', 'T1', 'V', 'W', 'W1', 'X', 'X1', 'XN', 'Y', 'Z', 'AA', 'AB', 'AD', 'AE', 'AF', 'AG', 'AH', 'AI', 'AJ', 'AM', 'AN'];
        for (var j = 0; j < propNm.length; j++) {
            if (propNm[j] === 'B') {
                $('#SIDCSYMBOLMODIFIER12').find("option:eq(0)").prop("selected", true);
            }else if (propNm[j] === 'J' || propNm[j] === 'R' || propNm[j] === 'R2' || propNm[j] === 'AD' || propNm[j] === 'AG') {
                if ($('#'+propNm[j]) !== undefined) {
                    $('#'+propNm[j]).find("option:eq(0)").prop("selected", true);
                }
            }else{
                if ( propNm[j] !== '' && $('#'+propNm[j]) !== undefined) {
                    $('#'+propNm[j]).val("");
                }
            }
        }
    }
    var options = {
        colorMode: document.getElementById("ColorMode").value,
        monoColor1: jQuery("input:checkbox[id='MonoColorChk']").is(":checked") ? jQuery('#MonoColor1').val() : '',
        hqStafLength: document.getElementById("hqStafLength").value,
        infoColor: document.getElementById("InfoColor").value,
        infoSize: document.getElementById("infoSize").value,
        frame:  document.getElementById("Frame").checked,
        fill: document.getElementById("Fill").checked,
        fillPercent: document.getElementById("FillPercent").value,
        fillOpacity: document.getElementById("FillOpacity").value,
        icon: document.getElementById("DisplayIcon").checked,
        civilianColor: document.getElementById("CivilianColors").checked,
        infoFields: document.getElementById("infoFields").checked,
        outlineColor: document.getElementById("outlineColor").value,
        outlineWidth: document.getElementById("outlineWidth").value,
        size: document.getElementById("Size").value,
        strokeWidth: document.getElementById("StrokeWidth").value,
        sidcsymbolModifier12: document.getElementById("SIDCSYMBOLMODIFIER12").value,
        quantity: document.getElementById("C").value,
        reinforcedReduced: document.getElementById("F").value,
        staffComments: document.getElementById("G").value,
        additionalInformation: document.getElementById("H").value,
        evaluationRating: document.getElementById("J").value,
        combatEffectiveness: document.getElementById("K").value,
        signatureEquipment: document.getElementById("L").value,
        higherFormation: document.getElementById("M").value,
        hostile: document.getElementById("N").value,
        iffSif: document.getElementById("P").value,
        direction: document.getElementById("Q").value,
        R: document.getElementById("R").value,
        uniqueDesignation: document.getElementById("T").value,
        type: document.getElementById("V").value,
        dtg: document.getElementById("W").value,
        altitudeDepth: document.getElementById("X").value,
        location: document.getElementById("Y").value,
        speed: document.getElementById("Z").value,
        specialHeadquarters: document.getElementById("AA").value,
        AG: document.getElementById("AG").value,
    }
    options.SIDC = buildSymbolID(function_sidc, options);
    return options
}
