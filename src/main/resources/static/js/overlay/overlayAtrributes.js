'use strict';
/*
//SVG캔버스 shapeObj(신규)
/*var shapeOjb = {
        id : null,
        shape : null,
        g_group : null,
        drawInfo : {
            x
            y:
            x1
            y1
            width
            height
        },
}*/
//SVG캔버스 디폴트 속성(신규)
var type_common = {
        svgState : {
            color : 'red'
            ,stroke : 'white'
            ,strokeWidth : '3'
        }

        ,svgShapeNum : {
            line             : 0 //라인
            ,rect            : 0 //사각형
            ,roundrect  : 0 //둥근사각형
            ,triangle      : 0 //삼각형
            ,circle          : 0 //원
            ,ellipse        : 0 //타원
            ,arc              : 0 //호
            ,pie              : 0 //피자파이
            ,hexagon     : 0 //육각형
            ,text              : 0 //텍스트
            ,g                  : 0 //그룹개수
            ,marker        : 0 //마커개수
            ,milsymbol  : 0  //마커개수
            ,editor         : 0  //에디터 개수
            ,point         : 0  //점 개수
        }

        ,commonAttr : {
/*            stroke             : '#000000' //선 색상
                ,strokeWidth  : 3         //선 굵기
                ,color              : '#00ff0000' //채움 색상
*/
                    stroke             : 'blue' //선 색상
                    ,strokeWidth  : 4         //선 굵기
                    ,color              : '#00ff0000' //채움 색상
        }
};

//투명도 기본속성 ( 선(type_lp), 채움(type_fp), 문자열(type_tp) )
var overlayAttr = {
     type_lp : {
         la   : enumType.enum_lineAlignment.center    //선 치우침 상태
         ,lc  : 'black'  //선 색상
         ,ls  : null   //겹선 정보
         ,lj  : 1    //선 연결 모양
         ,ml  : 1    //선 연결 부위 처리 길이
         ,ft  : 1    //선 채움 방법
         ,lw  : 1    //선 굵기
         ,lcs : null //시작점 선 끝 모양
         ,lce : null //끝점 선 끝 모양
         ,ds  : null //점선 모양
         ,hb  : null //음영
         ,pgb : null //경로형 그라데이션
         ,lgb : null //선형 그라데이션
     }
     ,type_fp : {
         la   : enumType.enum_lineAlignment.center    //선 치우침 상태
         ,lc  : 'black'  //선 색상
         ,ls  : null   //겹선 정보
         ,lj  : 1    //선 연결 모양
         ,ml  : 1    //선 연결 부위 처리 길이
         ,ft  : 1    //선 채움 방법
         ,lw  : 1    //선 굵기
         ,lcs : null //시작점 선 끝 모양
         ,lce : null //끝점 선 끝 모양
         ,ds  : null //점선 모양
         ,hb  : {
                              hs :  enumType.enum_hatchStyle['0'],   //음영 모양
                              fg  : '#FF000000',                                  //전경색(검정)
                             bg  : 'none',                                               //배경색(없음)
                   } //음영 속성 정보(2차속성) : type_hatchBrush
         ,pgb : null //경로형 그라데이션
         ,lgb : null //선형 그라데이션
     }
     ,type_tp : {
         "tx"   : null//문자열
        ,"ha"   : null//좌우정렬
        ,"va"   : null//상하정렬
        ,"fn"   : null//글꼴
        ,"fs"   : null//문자크기
        ,"tc"   : null//문자색상
        ,"bd"   : null//굵게
        ,"it"   : null//기울임
        ,"drl"  : null//우에서 좌로 문자 나열
        ,"dv"   : null//세로 방향으로
    }
};


/* OVS 하위 속성 <xml 변환 데이터
<!-- spt : point symbol - 점 부호 -->
<!-- srt : rectangle symbol - 사각형 부호 -->
<!-- sel : ellipse symbol - 타원 부호 -->
<!-- sar : arc symbol - 호 부호 -->
<!-- spl : polyline symbol - 다각형 부호 -->
<!-- sts : tactical symbol - 전술부호 -->
<xs:element name="spt" type="type_ols_point" />
<xs:element name="srt" type="type_ols_rect" />
<xs:element name="sel" type="type_ols_ellipse" />
<xs:element name="sar" type="type_ols_arc" />
<xs:element name="spl" type="type_ols_polyline" />
<xs:element name="sts" type="type_ols_tacsym" />
*/
// 투명도 부호별 속성 (  )

//투명도 부호별 속성(ovs 하위 속성)
var overlayShapeAttr = {
    type_ols_point : {
         sz   : 5        //크기
         ,ps  : enumType.enum_pointShape.pointTypeRect
         ,url : null
    }
    ,type_ols_rect : {
         rot  : 0        //회전값
         ,rad : 0        //둥근사각형 꼭지점 반경
         ,url : null
    }
    ,type_ols_ellipse : {
         rot  : 0        //회전값
         ,url : null
    }
    ,type_ols_arc : {
         rot  : 0        //회전값
         ,rad : 0        //둥근사각형 꼭지점 반경
         ,alt : enumType.enum_arcType.arcTypeArc //원호 선 모양
         ,aft : enumType.enum_arcType.arcTypePie //원호 채움 모양
         ,url : null
    }
    ,type_ols_polyline : {
         rot  : 0        //회전값
         ,lt  : enumType.enum_polylineType.polylineTypeStraight  //직선 연결
         ,cls : 0        //닫힘여부 (0 - 열림, 1 = 닫힘)
         ,url : null
    }
    ,type_ols_tacsym : {
         sym  : null // 군대부호 조립정보(필수)
         ,sz  : 7    // 부호크기
         ,cm  : enumType.enum_tacsymColorMode.autoAffiliation // 색상 설정 방법
         ,lc  : null // 사용자 지정 선색상 또는 아이콘 색상
         ,fc  : null // 사용자 지정 채움색상 또는 외형채움 색상
         ,shi : 1 // 기능부호 표시 여부(0 : 표시하지 않음, 1 : 표시)
         ,shf : 1 // 외형부호 표시 여부(0 : 표시하지 않음, 1 : 표시)
         ,shc : 0 // 민간부호 색상 채움 여부(0 – 민간부호 구분 표시하지 않음, 1 - 민간부호 구분 표시)
         ,ff  : 1 // 외형부호 채움 상태(0 – 채우지 않음, 1 - 채움)
         ,ocp : enumType.enum_opPos.opPosNone // 운용조건 표시 위치
         ,tp  : 255 // 투명값
         ,ifr : 0 // 외형 물 채움 설정 여부 (0 – 기본도시, 1 - 물채움)
         ,fr  : 0 // 물채움 값
    }
}

//도형별 속성정보
//scwin.attrObject = {};

//셀렉터의 정보
var selectorObj = function selectorObj(params) {
    this.id = params.id;      //셀렉터 id
    this.gid = params.gid;      //셀렉터 그룹id
    this.canvasId = params.canvasId;      //canvas Id
    this.shapeId = params.shapeId;      //도형ID
    this.centerX = params.centerX;      //중심좌표X
    this.centerY = params.centerY;      //중심좌표Y
    this.active = params.active;    //active 여부
    //시작점 끝점 좌표(selector)
    this.startX = params.startX;
    this.startY = params.startY;
    this.endX = params.endX;
    this.endY = params.endY;
    this.pointX0 = params.pointX0;
    this.pointY0 = params.pointY0;
    this.pointX1 = params.pointX1;
    this.pointY1 = params.pointY1;
    this.pointX2 = params.pointX2;
    this.pointY2 = params.pointY2;
    //리사이징을 위한 8개 포인트
    this.selPoints = [
        {
            num     : 0,
            id      : "selectorGrip_resize_nw",
            style   : "cursor:nw-resize",
            cx      : this.pointX0,
            cy      : this.pointY0,
            method  : {
                canvasId : this.canvasId,
                shapeId :  this.shapeId,
                position :"nw",
            },
        },
        {
            num     : 1,
            id      : "selectorGrip_resize_n",
            style   : "cursor:n-resize",
            cx      : this.pointX1,
            cy      : this.pointY0,
            method  : {
                canvasId : this.canvasId,
                shapeId :  this.shapeId,
                position :"n",
            },
        },
        {
            num     : 2,
            id      : "selectorGrip_resize_ne",
            style   : "cursor:ne-resize",
            cx      : this.pointX2,
            cy      : this.pointY0,
            method  : {
                canvasId : this.canvasId,
                shapeId :  this.shapeId,
                position :"ne",
            },
        },
        {
            num     : 3,
            id      : "selectorGrip_resize_w",
            style   : "cursor:w-resize",
            cx      : this.pointX0,
            cy      : this.pointY1,
            method  : {
                canvasId : this.canvasId,
                shapeId :  this.shapeId,
                position :"w",
            },
        },
        {
            num     : 4,
            id      : "selectorGrip_resize_e",
            style   : "cursor:e-resize",
            cx      : this.pointX2,
            cy      : this.pointY1,
            method  : {
                canvasId : this.canvasId,
                shapeId :  this.shapeId,
                position :"e",
            },
        },
        {
            num     : 5,
            id      : "selectorGrip_resize_sw",
            style   : "cursor:sw-resize",
            cx      : this.pointX0,
            cy      : this.pointY2,
            method  : {
                canvasId : this.canvasId,
                shapeId :  this.shapeId,
                position :"sw",
            },
        },
        {
            num     : 6,
            id      : "selectorGrip_resize_s",
            style   : "cursor:s-resize",
            cx      : this.pointX1,
            cy      : this.pointY2,
            method  : {
                canvasId : this.canvasId,
                shapeId :  this.shapeId,
                position :"s",
            },
        },
        {
            num     : 7,
            id      : "selectorGrip_resize_se",
            style   : "cursor:se-resize",
            cx      : this.pointX2,
            cy      : this.pointY2,
            method  : {
                canvasId : this.canvasId,
                shapeId :  this.shapeId,
                position :"se",
            },
        },
    ];
};

selectorObj.prototype = {
        getId : function getId() {
            return this.id;
        },
        getCenterX : function() {
            return  this.centerX;
        },
        getCenterY : function() {
            return this.centerY;
        },
        getStartX   : function (){
            return this.startX;
        },
        getStartY   : function (){
            return this.startY;
        },
        getEndX : function (){
            return this.endX;
        },
        getEndY : function (){
            return this.endY;
        },
}

//속성 반영(도형 update)
var updateShapeAttrs = function ( paramObj , xmlToShapeId ){
    //xmlToShapeId => xml 파싱후 자동 생성된 경우는 패스
    //xml정보를 통한 도형 생성에만 해당 데이터가 들어옴 xmlToShapeId
    if( xmlToShapeId !== undefined ) {
        shapeId = xmlToShapeId;
    } else {
        var shapeId = d3Canvas.getCurrSelectorId()[0].id;
    }

    var type = 'selector';
    var id = ('selectedBox_' +shapeId);
    var selector = d3Canvas.getSvgOjbInfo(type, id);
    //Todo
    //0) 기존 속성이 있는지 여부 있으면 세팅
    //1) 셀렉터 정보 함수생성
    //2) 위의 좌표를 이용하여 text문자열 위치 잡아서 그리기
    //3) 다른 속성들과 함께 draw 오브젝트 만들어서 그리도록 유도
    //4) 속성 오브젝트 넘겨서 저장하도록 유도
    var mode = null; //insert, update 구분을 위해

    //문자열 속성
    if(paramObj.hasOwnProperty('type_tp')){
        var textId = null;

        // 해당 객체 내의 text 객체를 찾아서 ID 가져오는 로직
        // 굵기 기울임 요소 빠지는거 체크 로직 필요함
        d3Canvas.svg.selectAll("#"+shapeId).node().parentNode.childNodes.forEach(
            function(e,i){
                if( e.id.indexOf("text") > -1){
                    textId = e.id;
                }
            }
        )
        if( textId === null ){
            textId = idMaker(d3Canvas,'text');
            if( paramObj.type_tp.hasOwnProperty('tx') ) {
                //문자열 text
                var makeText = addSvgElementFromJson({
                        element: 'text',
                        attr: {
                            id: textId,
                            x: selector.centerX,
                            y: selector.centerY,
                          stroke: 'black',
                          'stroke-width': '2',
                          //'stroke-dasharray': '5,5',
                          // need to specify this so that the rect is not selectable
                          style: 'pointer-events:none'
                        },
                    });
                d3Canvas.addDrawObj(makeText, shapeId, textId, paramObj.type_tp.tx);
                d3Canvas.addTextObj(textId, paramObj.type_tp.tx);
            }
        } else {
            d3Canvas.addTextObj(textId, paramObj.type_tp.tx);
        }

        if( paramObj.type_tp.hasOwnProperty('ha') ) {
            //좌우정렬  enum
            var pointNum = paramObj.type_tp.ha;

            var pointX = null;
            if( pointNum == "0" ){
                pointX = selector.pointX0;
            }
            if( pointNum == "1" ){
                pointX = selector.pointX1;
            }
            if( pointNum == "2" ){
                pointX = selector.pointX2;
            }
            d3Canvas.updateAttrObj(textId,"x",pointX);
        }
        if( paramObj.type_tp.hasOwnProperty('va') ) {
            //상하정렬  enum
            var pointNum = paramObj.type_tp.va;

            var pointY = null;
            if( pointNum == "0" ){
                pointY = selector.pointY0;
            }
            if( pointNum == "1" ){
                pointY = selector.pointY1;
            }
            if( pointNum == "2" ){
                pointY = selector.pointY2;
            }
            d3Canvas.updateAttrObj(textId,"y",pointY);
        }
        if( paramObj.type_tp.hasOwnProperty('fn') ) {
            //글꼴
            d3Canvas.updateStyleObj(textId,"font-family",paramObj.type_tp.fn);
        }
        if( paramObj.type_tp.hasOwnProperty('fs') ) {
            //문자크기
            d3Canvas.updateStyleObj(textId,"font-size",paramObj.type_tp.fs);
        }
        if( paramObj.type_tp.hasOwnProperty('tc') ) {
            //문자색상
            d3Canvas.updateAttrObj(textId,"fill",paramObj.type_tp.tc);
            d3Canvas.updateAttrObj(textId,"stroke",paramObj.type_tp.tc);
        }
        if( paramObj.type_tp.hasOwnProperty('bd') ) {
            //굵게
            d3Canvas.updateStyleObj(textId,"font-weight",paramObj.type_tp.bd);
        }
        if( paramObj.type_tp.hasOwnProperty('it') ) {
            //기울임
            d3Canvas.updateStyleObj(textId,"font-style",paramObj.type_tp.it);
        }
        if( paramObj.type_tp.hasOwnProperty('drl') ) {
            //우에서 좌로 문자 나열 bool
        }
        if( paramObj.type_tp.hasOwnProperty('dv') ) {
            //세로 방향으로 bool
        }
    } else {
        //현재는 속성에 없지만 생성되었다 사용이 취소되었을경우
        //xmlToShapeId => xml 파싱후 자동 생성된 경우는 패스
        if( xmlToShapeId !== undefined ) {
            d3Canvas.svg.selectAll("#"+shapeId).node().parentNode.childNodes.forEach(
                function(e,i){
                    if( e.id.indexOf("text") > -1){
                        textId = e.id;
                    }
                }
            )
            d3Canvas.svg.selectAll("#"+textId).remove();
        }
    }

    //선 속성
    if(paramObj.hasOwnProperty('type_lp')){
        if( paramObj.type_lp.hasOwnProperty('lc') ) {
            //선색상
            d3Canvas.updateStyleObj(shapeId,"stroke",paramObj.type_lp.lc);
        }

        if( paramObj.type_lp.hasOwnProperty('lw') ) {
            //선 굵기
            d3Canvas.updateStyleObj(shapeId,"stroke-width",paramObj.type_lp.lw);
        }

        if( paramObj.type_lp.hasOwnProperty('ds') ) {
            var _typeLp_Ds = paramObj.type_lp.ds;
                //속성명 , 속성값 배열로 리턴[ a, b ]
                var lineDashAttr = enumType.enum_lineDashStyle[_typeLp_Ds];
                //선 굵기
                d3Canvas.updateStyleObj(shapeId,lineDashAttr[0],lineDashAttr[1]);
        } else {
            //속성 제거(실선으로 표시)
            d3Canvas.updateStyleObj(shapeId,'stroke-dasharray',null);
        }

        if( paramObj.type_lp.hasOwnProperty('lcs') ) {
            //선끝 시작 모양
            var attrData = enumType.enum_lineCap[paramObj.type_lp.lcs];
            var markerCol = null;
            var reverseYn = "Y";

            //마커 컬러 = 선 색상과 동일
            if( paramObj.type_lp.hasOwnProperty('lc') ) {
                markerCol = paramObj.type_lp.lc;
            } else {
                markerCol = 'black';
            }

            //마커 생성
            var markerId  = d3Canvas.makeMarker(attrData, markerCol, shapeId, reverseYn);

            var makerForm = "url(#"+markerId+")";
            d3Canvas.updateAttrObj(shapeId,"marker-start",makerForm);
        } else {
            //속성 제거(마커 제거)
            d3Canvas.updateAttrObj(shapeId,'marker-start',null);
        }

        if( paramObj.type_lp.hasOwnProperty('lce') ) {
            //선끝 종료 모양
            var attrData = enumType.enum_lineCap[paramObj.type_lp.lce];
            var markerCol = null;
            var reverseYn = "N";

            //마커 컬러 = 선 색상과 동일
            if( paramObj.type_lp.hasOwnProperty('lc') ) {
                markerCol = paramObj.type_lp.lc;
            } else {
                markerCol = 'black';
            }
            //마커 생성
            var markerId  = d3Canvas.makeMarker(attrData, markerCol, shapeId, reverseYn);

            var makerForm = "url(#"+markerId+")";
            d3Canvas.updateAttrObj(shapeId,"marker-end",makerForm);
        } else {
            //속성 제거(마커 제거)
            d3Canvas.updateAttrObj(shapeId,'marker-end',null);
        }
    }

    //채움 속성
    if(paramObj.hasOwnProperty('type_fp')){
        if( paramObj.type_fp.hasOwnProperty('ft') ) {
            if( paramObj.type_fp.ft === "0" ){
                //단색 처리
                if( paramObj.type_fp.hasOwnProperty('fc') ) {
                    //채움색상
                    d3Canvas.updateStyleObj(shapeId,"fill",paramObj.type_fp.fc);
                }
            }

            if( paramObj.type_fp.ft === "1" ){
                //음영 처리
                //0. 음영처리 파라메터 세팅
                var _attrHatchOjb = paramObj.type_fp.hb;

                var hatchCode = _attrHatchOjb.hs;
                var hatchId = enumType.enum_hatchStyle[hatchCode].id;
                var patternId = shapeId+"_"+hatchId;
                var hatchIdForm = "url(#"+patternId+")";

                //1. 먼저 패턴 등록
                d3Canvas.fillHatchInit(shapeId, _attrHatchOjb);

                //2. 등록된 패턴에 색상변경 및 적용
                d3Canvas.hatchSelect(shapeId, _attrHatchOjb);

                //3. 도형에 적용
                d3Canvas.updateStyleObj(shapeId,"fill",hatchIdForm);
            }

            if( paramObj.type_fp.ft === "3" ){
                //경로형 그라데이션 처리
            }

            if( paramObj.type_fp.ft === "4" ){
                //선형 그라데이션 처리
            }
        }

    } else {
        d3Canvas.updateStyleObj(shapeId,"fill",'none');
    }

    //수정된 속성 저장
    d3Canvas.saveAttrObj(shapeId, paramObj);
}

// editor 저장내용 보관용(임시)
var shareEditorData = "";

//우클릭 서브 메뉴
var menu = [
    {
        title : '세부속성관리',
        action : function(elm, d, i ,attObj) {
            stmp.openSMTAlert("/ui/CF/SI/COM/CFSISMOverlayAttr.xml", "340", "투명도 속성 세부 관리", "OverlayAttr", false, 140, 1550, attObj);
//          d.action(activeNodes);
           d3.select('.d3-context-menu').style('display','none');
        }
    },
    {
        title : '객체복사',
        action : function(elm, d, i, attObj, shapeId) {
            d3Canvas.shapeCopy(attObj, shapeId);
        }
    },
    {
        title : '잘라내기',
        action : function(elm, d, i, attObj, shapeId) {
            d3Canvas.shapeCut(attObj, shapeId);
        }
    },
    {
        title : '객체삭제',
        action : function(elm, d, i, attObj, shapeId) {
            d3Canvas.shapeDelete(attObj, shapeId);
        }
    },
    {
        title : '화면고정',
        action : function(elm, d, i, attObj, shapeId) {
            d3Canvas.svg.selectAll('#'+shapeId).classed('fixed', true);
        }
    },
    {
        title : '화면고정해제',
        action : function(elm, d, i, attObj, shapeId) {
            d3Canvas.svg.selectAll('#'+shapeId).attr('class', null);
        }
    }
];

//에디터 서브 메뉴
var menuEditor = [
  {
      title : '편집',
      action : function(elm, d, i, attObj, shapeId) {
          d3Canvas.editorEdit('edit', shapeId);
      }
  },
  {
      title : '삭제',
      action : function(elm, d, i, attObj, shapeId) {
          d3Canvas.editorEdit('delete', shapeId);
      }
  },
];

// 군대부호 서브 메뉴
var menuMilSym = [
  {
      title : '편집',
      action : function(elm, d, i, attObj, shapeId) {
          d3Canvas.milSymEdit('edit', shapeId);
      }
  },
  {
      title : '삭제',
      action : function(elm, d, i, attObj, shapeId) {
          d3Canvas.milSymEdit('delete', shapeId);
      }
  },
];