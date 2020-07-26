'use strict';

var jiConstant = {
    BASE_MAP_ID : 'BASE_MAP',
    /**
     * map 종류
     */
    MAP_KIND : {
        MAP_3D : 0,
        MAP_2D : 1
    },
    /**
     * geometry 타입 정의
     */
    GEOMETRY_TYPE : {
        POINT : 'Point',
        MULTIPOINT : 'MultiPoint',
        LINESTRING : 'LineString',
        MULTILINESTRING : 'MultiLineString',
        POLYGON : 'Polygon',
        MULTIPOLYGON : 'MultiPolygon',
        CIRCLE : 'Circle'
    },
    /**
     * 좌표계 정의
     */
    COORDINATE_SYSTEM : {
        WGS84 : '0',
        MGRS : '1',
        UTM : '2',
        GEOREF : '3',
        GARS : '4',
        WEB_MERCATOR : '5'
    },
    /**
     * mapbox source 타입 정의
     */
    MAPBOX_SOURCE_TYPE : {
        GEOJSON : 'geojson',
        VECTOR : 'vector',
        RASTER : 'raster',
        RASTER_DEM : 'raster-dem',
        IMAGE : 'image',
        VIDEO : 'video',
        CANVAS : 'canvas'
    },
    /**
     * mapbox layer 타입 정의
     */
    MAPBOX_LAYER_TYPE : {
        BACKGROUND : 'background',
        FILL : 'fill',
        LINE : 'line',
        SYMBOL : 'symbol',
        RASTER : 'raster',
        CIRCLE : 'circle',
        FILL_EXTRUSION : 'fill-extrusion',
        HEATMAP : 'heatmap',
        HILLSHADE : 'hillshade'
    },
    DRAW_MODE : {

    },
    EVENTS : {
        MOUSEDOWN : 'mousedown',
        MOUSEMOVE : 'mousemove',
        MOUSEUP : 'mouseup',
        MOVE : 'move',
        MOVEEND : 'moveend',
        VIEWRESET : 'viewreset',
        CLICK : 'click',
        CONTEXTMENU : 'contextmenu',
        DBLCLICK : 'dblclick'
    },
    UNITS : {
        METERS : 'meters',
        KILOMETERS : 'kilometers',
        DEGREES : 'degrees',
        RADIANS : 'radians'
    },
    OVERLAY_DRAW_MODE : {
        SELECT : { CD : 0, NAME : 'select' },
        POINT : { CD : 1, NAME : 'point' },
        LINE : { CD : 2, NAME : 'line' },
        RECTANGLE : { CD : 3, NAME : 'rect' },
        ROUNDED_RECTANGLE : { CD : 4, NAME : 'round-rect' },
        TRIANGLE : { CD : 5, NAME : 'triangle' },
        CIRCLE : { CD : 6, NAME : 'circle' },
        ELLIPSE : { CD : 7, NAME : 'ellipse' },
        ARC : { CD : 8, NAME : 'arc' },
        PIE : { CD : 9, NAME : 'pie' },
        HEXAGON : { CD : 10, NAME : 'hexagon' },
        TEXT : { CD : 11, NAME : 'text' },
        IMAGE : { CD : 12, NAME : 'image' },
        MINIMAP : { CD : 13, NAME : 'minimap' },
        MARKER : { CD : 14, NAME : 'marker' }
    }
};