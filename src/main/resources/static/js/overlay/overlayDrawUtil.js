'use strict';

// json Drawing Method
const NS = {
    HTML: 'http://www.w3.org/1999/xhtml',
    MATH: 'http://www.w3.org/1998/Math/MathML',
    SE: 'http://svg-edit.googlecode.com',
    SVG: 'http://www.w3.org/2000/svg',
    XLINK: 'http://www.w3.org/1999/xlink',
    XML: 'http://www.w3.org/XML/1998/namespace',
    XMLNS: 'http://www.w3.org/2000/xmlns/' // see http://www.w3.org/TR/REC-xml-names/#xmlReserved
};

const SVG = document.createElementNS(NS.SVG,"svg");

let getElem = function(id){
    return SVG.querySelector("#" + id);
};

let assignAttributes = function(node, attrs, suspendLength, text){
    if(!suspendLength){
        suspendLength = 0;
    }

    SVG.suspendRedraw(suspendLength);

    var i;
    for(i in attrs){
        var ns = (i.substr(0,4) === 'xml:' ? NS.XML :
                i.substr(0,6) === 'xlink:' ? NS.XLINK : null);

        if(ns){
            node.setAttributeNS(ns, i, attrs[i]);
        } else {
            node.setAttribute(i, attrs[i]);
        }
    }
}

let addSvgElementFromJson = function(data){
    var shape = getElem(data.attr.id);

    if(shape && data.element != shape.tagName){
        shape = null;
    }

    if(!shape){
        shape = document.createElementNS(NS.SVG, data.element);
    }

    assignAttributes(shape, data.attr, 100, data.text);
    return shape;
}

/**
 * 도형 크기 조절 함수
 * @param {Object} paramOjb - 수정하고자 하는 정보 OJB
 */
//var shapeResizer = d3.behavior.drag().origin(Object).on("drag", shapeResize("a"));
let shapeResizeDir = function( param ){
    var _pointPosition = param.position;
    var _canvasId = param.canvasId;
    var _paramOjb = param;

    this.start = function() {
        console.log(2);
        return false;
    };

    this.drag = function() {
        console.log(3);
        return false;
    };

    this.end = function() {
        console.log(4);
        return false;
    };

    return {
        start: start,
        drag: drag,
        end: end,
    }

    /*
    //각 위치별 resize 메소드 호출
    if( _pointPosition === "nw" ){
        _canvasId.resizePointNW( _paramOjb );
    }
    if( _pointPosition === "n" ){
        console.log(222222222222);
        _canvasId.resizePointN( _paramOjb );
    }
    if( _pointPosition === "ne" ){
        _canvasId.resizePointNE( _paramOjb );
    }
    if( _pointPosition === "w" ){
        _canvasId.resizePointW( _paramOjb );
    }
    if( _pointPosition === "e" ){
        _canvasId.resizePointE( _paramOjb );
    }
    if( _pointPosition === "sw" ){
        _canvasId.resizePointSW( _paramOjb );
    }
    if( _pointPosition === "s" ){
        _canvasId.resizePointS( _paramOjb );
    }
    if( _pointPosition === "se" ){
        _canvasId.resizePointSE( _paramOjb );
    }*/
};

/**
 * 정규식을 이용하여 숫자만 제거
 * @param {string} string - 숫자만 제거 하고 싶은 문자열
 */
//정규식을 이용하여 숫자만 제거
function numberRemove(paramString){
    return paramString.replace(/[0-9]/g,"");
}

/**
 * 정규식을 이용하여 문자만 제거
 * @param {string} string - 문자만 제거 하고 싶은 문자열(숫자만 얻기)
 */
//정규식을 이용하여 문자만 제거
function strRemove(paramString){
    return paramString.replace(/[0-9]/g,"");
}