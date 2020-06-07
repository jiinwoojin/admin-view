'use strict';

var jiConstant = {
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
    }
};