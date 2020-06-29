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
            "milsymbol.js",
            "savm-bc.js",
            "symbol.js",
            "symbolCommon.js",
            "graphics.js",
            "html2canvas.min.js",
            "mb-milSymbol.js",
            "cmm-milSymbol.js"])
    }else{
        if(stmp.PRESENT_MAP_KIND === stmp.MAP_KIND.MAP_2D) {
            milSymbolLoader.map = milSymbolLoader.param.map
            milSymbolLoader.redeclareBasegeometry()
            milSymbolLoader.declare2DdrawControl()
            milSymbolLoader.map.addControl(milSymbolLoader.drawControl);
            milSymbolLoader.map.on('draw.update', milSymbolLoader.milsymbolsPreview);
            milSymbolLoader.map.on('draw.create', milSymbolLoader.milsymbolsGenerator);
        }else if(stmp.PRESENT_MAP_KIND === stmp.MAP_KIND.MAP_3D) {
            milSymbolLoader.map = milSymbolLoader.param.map

        }
        //
        if(milSymbolLoader.callback){
            milSymbolLoader.callback()
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
            var percent = parseFloat(this.options.fillPercent)
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
                        console.log(boundry)
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
    var options = stmp.getMilsymbolOptions()
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
    //console.log(geometryType, constraint, coord)
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
        jQuery.each(data.features, function(idx, feature){
            feature.properties._symStd = symStd
            feature.properties._cs = cs
            feature.properties._function_sidc = function_sidc
            feature.properties.drawId = drawId
            if(feature.geometry.type === "Point"){
                feature.properties.type = "Label"
                feature.properties.labelOffset = [feature.properties.labelXOffset,feature.properties.labelYOffset]
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
    var labelLayer = milSymbolLoader.map.getLayer('milsymbols-layer-label')
    if(labelLayer === undefined){
        milSymbolLoader.map.addLayer({
            id: 'milsymbols-layer-label',
            type: 'symbol',
            source: source.id,
            layout: {
                'text-field': ['get', 'label'],
                'text-allow-overlap' : true,
                'text-ignore-placement' : true,
                "text-size": ['get', 'fontSize'],
                'text-rotate': ['get','angle'],
                //"text-font": ["Gosanja"]
            },
            paint: {
                "text-color": ['get', 'fontColor'],
                "text-halo-color": ['get', 'labelOutlineColor'],
                "text-halo-width": ['get', 'labelOutlineWidth'],
            },
            filter: ["all", ['==', '$type', 'Point'], ["==", "type", 'Label']]
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
        if(feature.geometry.type === "Point" && feature.properties.type !== "Label") {
            if(type === "move"){
                feature.geometry = drawGeometry
            }else if(type === "draw"){
                if(milSymbolLoader.map.hasImage(drawId + "-image")){
                    milSymbolLoader.map.removeImage(drawId + "-image")
                }
                var constraint = feature.properties._constraint
                if(constraint === "milSym"){
                    var imageData = milSymbolLoader.map._drawing_milsymbol.asCanvas().toDataURL()
                    milSymbolLoader.map.loadImage(imageData,function(e,image){
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
    //console.log(options)
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
            if(stmp.PRESENT_MAP_KIND === stmp.MAP_KIND.MAP_2D){
                // 맵박스
                milSymbolLoader.drawControl.changeMode(mode)
            }else if(stmp.PRESENT_MAP_KIND === stmp.MAP_KIND.MAP_3D){
                // 세슘
                stmp.mapObject.drawControlMode(mode,symbol.options._constraint)
            }
        }
    } else {
        symbol = new ms.Symbol(options) // 심볼생성
        symbol.options._min_point = 1
        symbol.options._max_point = 1
        symbol.options._draw_type = "Point"
        symbol.options._constraint = "milSym"
        if(onlySetValue !== true){
            if(stmp.PRESENT_MAP_KIND === stmp.MAP_KIND.MAP_2D){
                // 맵박스
                milSymbolLoader.drawControl.changeMode("draw_point")
            }else if(stmp.PRESENT_MAP_KIND === stmp.MAP_KIND.MAP_3D){
                // 세슘
                stmp.mapObject.drawControlMode("draw_point",symbol.options._constraint)
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
