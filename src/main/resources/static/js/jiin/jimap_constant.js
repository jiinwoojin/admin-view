'use strict';

var jiConstant = {
    BASE_MAP_ID : 'BASE_MAP',
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

    }
};