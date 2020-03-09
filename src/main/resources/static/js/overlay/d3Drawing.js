/**
 * 작도 메소드
 * 작도를 위한 SVGCanvas 생성 및 도형 작도을 위한 리스너 등록
 */

'use strict';
//테스트용 삭제예정
//scwin.m = com.top();

//WebSquare.layer.hideProcessMessage();
// 편집중 맵 이동시 맵에 임시로 그려주는 Layer id
const canvasToMapNm = 'canvasToMap';

// D3 function
function SVGCanvas(options) {
  // An SVG-based drawing
  var self = this;
  // Define the global SVG options
  this.options = options || {};
  this.options.h = options.h || 250; // SVG Height and Width
  this.options.w = options.w || 250;
  this.options.addTo = options.addTo || 'body'; // Where to add the SVG (a css selector)
  this.options.addBorderRect = options.addBorderRect || true; // Whether to add a border around the SVG.
  this.options.rectOpt = {
    dbWidth: 3,
    dsRadius: 2
  };

 //전체 도형 관리
 this.Shapes = {
         //도형들
         shape : [],
         //셀렉터들
         selector : [],
         //그룹들
         group : [],
 }; // Collection

  // Make the SVG
  this.svg = d3.select(this.options.addTo)
    .append('svg')
    .attr('height', this.options.h)
    .attr('width', this.options.w)
    .attr('class', 'display-svg')
    .attr('id', "svgMain");

  // Add border if requested
  if (this.options.addBorderRect) {
    this.svg.append('rect')
      .attr('height', this.options.h)
      .attr('width', this.options.w)
      .attr('stroke', 'black')
      .attr('stroke-width', 1)
      .attr('opacity', 0.25)
      .attr('fill-opacity', 0.0)
      .attr('class', 'border-rect');
  }

  //마커 초기화
  this.markerInit();
  // Keydown events

  d3.select('body').on('click', this.keydownEventHandlers);
}

SVGCanvas.prototype.markerInit = function () {
    console.log(' 마커 초기화(선 속성) ');
    // Methods for adding Line to the svg.
    var self = this;

    var defs = this.svg.append('defs')
        .attr('id', 'makers');

    return 0;
};

SVGCanvas.prototype.keydownEventHandlers = function () {
    //com.alert("작도할 도형을 선택하시기 바랍니다.");
    //return 0;
  // Event handler for keydown events
  // Press 'Delete' to remove all active groups.
//    console.log(d3.event.key);

    if (d3.event.key === 'Delete') {
        d3.selectAll('g.active').remove();
      }
    if (d3.event.key === 'Enter') {
        alert("Enter");
      }
    if (d3.event.key === 'Escape') {
        alert("Escape");
      }
    if (d3.event.key === 'Shift') {
        alert("Shift");
      }
}

SVGCanvas.prototype.mouseOffset = function() {
  // Get the current location of mouse along with other info (to be added to later)
  var m = d3.event;
  return m;
}


//0.셀렉터 구현( 버그발견 회전값에 대한 )
/*
SVGCanvas.prototype.selectorMover = function() {
  var self = this;
  var shapeId = null;
  var selectorId = null;
  var selectorGid = null;
  var selectorInfo = null;
  var shapeType =  null;
  var dragCenter;
  var resultOjb =  null;

  function getDragCenter(shapeId, m) {
      return {
        line    : function (shapeId, m) {
            var shapeInfo = stmp.cloneDeep(self.Shapes.shape[shapeId]);
            var x1 = shapeInfo.x1;
            var y1 = shapeInfo.y1;
            var x2 = shapeInfo.x2;
            var y2 = shapeInfo.y2;
            var centerX = shapeInfo.cx;
            var centerY = shapeInfo.cy;

            var distX = null;
            var distY = null;

           distX = m.x - centerX;
           distY = m.y - centerY;

            self.updateAttrObj(shapeId, "x1", x1 + distX);
            self.updateAttrObj(shapeId, "y1", y1 + distY);
            self.updateAttrObj(shapeId, "x2", x2 + distX);
            self.updateAttrObj(shapeId, "y2", y2 + distY);

            shapeInfo.x1 = x1 + distX;
            shapeInfo.y1 = y1 + distY;
            shapeInfo.x2 = x2 + distX;
            shapeInfo.y2 = y2 + distY;

            return shapeInfo;
        },
        triangle     : function (shapeId, m) {
            var shapeInfo = stmp.cloneDeep(self.Shapes.shape[shapeId]);
            var nx  = parseInt(shapeInfo.nx);
            var ny  = parseInt(shapeInfo.ny);
            var swx = parseInt(shapeInfo.swx);
            var swy = parseInt(shapeInfo.swy);
            var sex = parseInt(shapeInfo.sex);
            var sey = parseInt(shapeInfo.sey);
            var centerX = shapeInfo.cx;
            var centerY = shapeInfo.cy;

            var distX = null;
            var distY = null;

           distX = m.x - centerX;
           distY = m.y - centerY;

           nx  = nx+distX;
           ny  = ny+distY;
           swx = swx+distX;
           swy = swy+distY;
           sex = sex+distX;
           sey = sey+distY;

           var points = nx+","+ny+" ";
               points += swx+","+swy+" ";
               points += sex+","+sey;

            self.updateAttrObj(shapeId, "points", points);

            shapeInfo.points = points;
            shapeInfo.nx    = nx  ;
            shapeInfo.ny    = ny  ;
            shapeInfo.swx  = swx ;
            shapeInfo.swy  = swy ;
            shapeInfo.sex  = sex ;
            shapeInfo.sey  = sey ;

            return shapeInfo;
        },
        pie    : function (shapeId, m) {
            var shapeInfo = stmp.cloneDeep(self.Shapes.shape[shapeId]);
            // 중심값 구하기(x1, y1)
            var x1 = parseInt(shapeInfo.cx);
            var y1 = parseInt(shapeInfo.cy);
            var x0 = parseInt(shapeInfo.left.split(' ')[0]);
            var y0 = parseInt(shapeInfo.top.split(' ')[1]);
            var x2 = parseInt(shapeInfo.right.split(' ')[0]);
            var y2 = parseInt(shapeInfo.bottom.split(' ')[1]);

            var distX = null;
            var distY = null;

           distX = m.x - x1;
           distY = m.y - y1;

            //좌표 차이 만큼 좌표이동
            x1 = x1 + distX;
            y1 = y1 + distY;
            x0 = x0 + distX;
            x2 = x2 + distX;
            y0 = y0 + distY;
            y2 = y2 + distY;

            //도형 정보 get
            var top     = x1 + " " + y0;
            var middle  = x1 + " " + y1;
            var bottom  = x1 + " " + y2;
            var left    = x0 + " " + y1;
            var right   = x2 + " " + y1;


           var makeD = "M "+left+
           " C "+left+" "+x0+" "+y0+" "+ top+
           " C "+top+" "+x2+" "+y0+" "+ right+
           " L "+right+" "+ bottom+
           " Z ";

            self.updateAttrObj(shapeId, "d", makeD);

            shapeInfo.top        =    top;
            shapeInfo.middle  =   middle;
            shapeInfo.bottom  =   bottom;
            shapeInfo.left         =   left;
            shapeInfo.right      =   right;
            shapeInfo.d           =   makeD;
            return shapeInfo;
        },
        hexagon     : function (shapeId, m) {
            var shapeInfo = stmp.cloneDeep(self.Shapes.shape[shapeId]);

            // 중심값 구하기(x1, y1)
            var cx = parseInt(shapeInfo.cx);
            var cy = parseInt(shapeInfo.cy);
            var x0 = parseInt(shapeInfo.left.split(' ')[0]);
            var x1 = parseInt(shapeInfo.top1.split(' ')[0]);
            var x2 = parseInt(shapeInfo.top2.split(' ')[0]);
            var x3 = parseInt(shapeInfo.right.split(' ')[0]);
            var y0 = parseInt(shapeInfo.bottom1.split(',')[1]);
            var y1 = parseInt(shapeInfo.left.split(',')[1]);
            var y2 = parseInt(shapeInfo.top1.split(',')[1]);

            var distX = null;
            var distY = null;

           distX = m.x - cx;
           distY = m.y - cy;

            //좌표 차이 만큼 좌표이동
           cx = cx + distX;
           cy = cy + distY;
           x1 = x1 + distX;
           y1 = y1 + distY;
           x0 = x0 + distX;
           x2 = x2 + distX;
           y0 = y0 + distY;
           y2 = y2 + distY;
           x3 = x3 + distX;

            //도형 정보 get
            var top1     = x1 + "," + y2 + " ";
            var top2     = x2 + "," + y2 + " ";
            var bottom1  = x1 + "," + y0 + " ";
            var bottom2  = x2 + "," + y0 + " ";
            var left     = x0 + "," + y1 + " ";
            var right    = x3 + "," + y1 + " ";

            var makeP = left + top1 + top2 + right + bottom2 + bottom1;

            self.updateAttrObj(shapeId, "points", makeP);
            shapeInfo.points   = makeP;
            shapeInfo.cx      = cx;
            shapeInfo.cy      = cy;
            shapeInfo.top2      = top2;
            shapeInfo.top1      = top1;
            shapeInfo.bottom1  = bottom1;
            shapeInfo.bottom2  = bottom2;
            shapeInfo.left         = left;
            shapeInfo.right      = right;
            return shapeInfo;
        },
      };
    }
  start = function() {
      shapeId = d3Canvas.svg.selectAll(".active").node().firstElementChild.id;
      selectorId = ('selectedBox_'+shapeId);
  }

  drag = function() {
      var m = self.mouseOffset();
      var rBB = d3.selectAll("#"+shapeId).node().getBBox();
      var sBB = d3.selectAll("#"+selectorId).node().getBBox();
      var p = {
          x: sBB.x,
          y: sBB.y,
          w: sBB.width,
          h: sBB.height,
      };
      g = p;

      shapeType = numberRemove(shapeId);
      //모양별 함수 바인딩
      dragCenter = getDragCenter()[shapeType];
      if(  shapeType === "line"  ) {
          resultOjb = dragCenter(shapeId, m);
      } else if(  shapeType === "rect"  ) {
          var shapeInfo = stmp.cloneDeep(self.Shapes.shape[shapeId]);
          self.updateAttrObj(shapeId, "x", m.x);
          self.updateAttrObj(shapeId, "y", m.y);
          shapeInfo.x = m.x;
          shapeInfo.y = m.y;
          resultOjb = shapeInfo;
      } else if(  shapeType === "roundrect"  ) {
          var shapeInfo = stmp.cloneDeep(self.Shapes.shape[shapeId]);
          self.updateAttrObj(shapeId, "x", m.x);
          self.updateAttrObj(shapeId, "y", m.y);
          shapeInfo.x = m.x;
          shapeInfo.y = m.y;
          resultOjb = shapeInfo;
      } else if(  shapeType === "triangle"  ) {
          resultOjb = dragCenter(shapeId, m);
      } else if(  shapeType === "circle"  ) {
          var shapeInfo = stmp.cloneDeep(self.Shapes.shape[shapeId]);
          self.updateAttrObj(shapeId, "cx", m.x);
          self.updateAttrObj(shapeId, "cy", m.y);
          shapeInfo.cx = m.x;
          shapeInfo.cy = m.y;
          resultOjb = shapeInfo;
      } else if(  shapeType === "ellipse"  ) {
          var shapeInfo = stmp.cloneDeep(self.Shapes.shape[shapeId]);
          self.updateAttrObj(shapeId, "cx", m.x);
          self.updateAttrObj(shapeId, "cy", m.y);
          shapeInfo.cx = m.x;
          shapeInfo.cy = m.y;
          resultOjb = shapeInfo;
      } else if(  shapeType === "arc"  ) {

      } else if(  shapeType === "pie"  ) {
          resultOjb = dragCenter(shapeId, m);
      } else if(  shapeType === "hexagon"  ) {
          resultOjb = dragCenter(shapeId, m);
      }

      selectorInfo = self.Shapes.selector[selectorId];
      self.tempSelectorDraw(g, shapeId, selectorInfo);

      selectorGid = selectorInfo.gid;
  }

  end = function() {
      self.delSelection( selectorGid );
      //이동 후 도형정보 업데이트
      self.Shapes.shape[shapeId] =  resultOjb;
      // Make it active.
      self.setActive(shapeId);
  }

   return {
       start: start,
       drag: drag,
       end: end,
   }
}
*/
//0.셀렉터 구현( 다른방법 구현 )
SVGCanvas.prototype.selectorMover = function() {
    var self = this;
    var shapeId = null;
    var shapeInfo = null;
    var selectorId = null;
    var selectorGid = null;
    var selectorInfo = null;
    var cx = null;
    var cy = null;
    var resultOjb =  null;
    var attrTranslate = null;
    var attrRotate = null;
    var rBB = null;
    var sBB = null;
    var moveMode = false;

    this.start = function() {   // ohhk this 추가
        shapeId = d3Canvas.svg.selectAll(".active").node().firstElementChild.id;

        selectorId = "selectedBox_" + shapeId;
        selectorInfo = self.Shapes.selector[selectorId];
        /*
        shapeInfo = self.Shapes.shape[shapeId];

        if( shapeInfo.hasOwnProperty('rotateInfo') ){
            attrRotate = shapeInfo.rotateInfo;
        }

        if( shapeInfo.hasOwnProperty('translateInfo') ){
            attrTranslate = shapeInfo.translateInfo;
        }*/

        var transformInfo = self.svg.selectAll('#'+shapeId).node().getAttribute('transform');
        if( transformInfo != null ) {
            var transDivid =transformInfo.split(' ');

            // 이동 및 회전 정보를 체크하여 가져오기
            transDivid.forEach(
                function(d, i){
                    if( d.indexOf('translate') > -1) {
                        attrTranslate = d;
                    }
                    if( d.indexOf('rotate') > -1) {
                        attrRotate = d;
                    }
                }
            )
        }

        cx = selectorInfo.centerX;
        cy = selectorInfo.centerY;


        var m = self.mouseOffset();
        var transX = 0;
        var transY = 0;

        sBB = d3.selectAll("#"+selectorId).node().getBBox();
        var p = {
            x: sBB.x,
            y: sBB.y,
            w: sBB.width,
            h: sBB.height,
        };

        //이동된 중심 좌표
        var transCxy = null;

        //이전에 이동한 이력이 있으면
        if( attrTranslate != null ){
            transCxy = self.convertTransToCoords( attrTranslate, cx, cy );
            transX = Number(transCxy[0]);
            transY = Number(transCxy[1]);
        }

        // 현재 도형의 셀렉터 영역 내에서면 드래그를 통한 이동이 가능
        if( sBB.x + transX <= m.x && m.x <= sBB.x + sBB.width + transX ){
            if( sBB.y + transY <= m.y && m.y <= sBB.y + sBB.height + transY ){
                //drag 가 발생해야 이동이 시작된다.
                moveMode = true;
            }
        }

        //의미 없는 클릭이 발생시
        if ( !moveMode ) {
            self.svg.selectAll('g.active').classed('active', false);

            // Add 'active' class to any 'g' element with id = id passed.
            //1 모든 셀렉터 삭제
            $('[id^=selectorGroup]').each(function(e,i) {
                var selectorId = i.id;
                self.delSelection( selectorId );
            });

            SelectedShape(self,"select");
        }
    };

    this.drag = function() {      // ohhk this 추가
        if( moveMode ){
            var m = self.mouseOffset();
            var transX = 0;
            var transY = 0;

            sBB = d3.selectAll("#"+selectorId).node().getBBox();
            var p = {
                x: sBB.x,
                y: sBB.y,
                w: sBB.width,
                h: sBB.height,
            };

            //임의 셀렉터 표시를 위한 좌표정보 넘기기 위하여
            var g = p;

            //이동한 마우스 좌표 만큼의 거리
            var distX = null;
            var distY = null;
            var attrTransform = null;

            distX = m.x - cx;
            distY = m.y - cy;

            attrTranslate = "translate("+distX+","+distY+")";

            if( attrRotate != null ) {
                attrTransform = attrTranslate + " " + attrRotate;
            } else {
                attrTransform = attrTranslate;
            }

            self.svg.selectAll("#"+shapeId).attr("transform", attrTransform);
            self.tempSelectorDraw(g, shapeId, selectorInfo, attrTransform);

            selectorGid = selectorInfo.gid;
        }
    };

    this.end = function() {   // ohhk this 추가
        if( moveMode ){
            self.delSelection( selectorGid );
            //이동 후 도형정보 업데이트
            self.Shapes.shape[shapeId]['translateInfo'] =  attrTranslate;
            // Make it active.
            self.setActive(shapeId);
            moveMode = false;
        }
    };

    return {
        start: this.start,
        drag: this.drag,
        end: this.end,
    }
};

// 1.btn_icons2s-라인
SVGCanvas.prototype.makeAddLine = function() {
    // Methods for adding Line to the svg.
    var self = this;
    //var lineNum =  type_common.shape.line;
    this.start = function() {
        // 0. Get Id
        self.Line.id = idMaker(self,"line");
        var shapeId = self.Line.id;
        //Add a Line
        // 1. Get mouse location in SVG
        var m = self.mouseOffset();
        self.Line.x1 = m.x;
        self.Line.y1 = m.y;

        // 2. Make a rectangle
        self.Line.r = self.svg
          .append('g')
          .attr('class', 'g-line ' + self.Line.id)
          .append('line') // An SVG element
          .attr('x1', self.Line.x1) // Position at mouse location
          .attr('y1', self.Line.y1)
          .attr('x2', self.Line.x1+1) // Make it tiny
          .attr('y2', self.Line.y1+1)
          .attr('id',self.Line.id)
          .style('stroke', comAttr.commonAttr.stroke)
          .style('stroke-width', comAttr.commonAttr.strokeWidth)
          .style('fill', comAttr.commonAttr.color)
          .on("click",
                  function (d){
                      self.selectShape(shapeId);
                  }
              )
          .on("contextmenu",
                  function (d){
                      //우클릭시 서브 메뉴바 띄우기 예제
                  d3.contextMenu(menu, self, shapeId);
                  }
              );
      };

     this.drag = function() {
            // What to do when mouse is dragged
            // 1. Get the new mouse position
            var m = self.mouseOffset();
            self.Line.x2 = m.x;
            self.Line.y2 = m.y;

            // 2. Update the attributes of the Line
            self.Line.r.attr('x1', self.Line.x1)
              .attr('y1', self.Line.y1)
              .attr('x2', m.x)
              .attr('y2', m.y);

            self.Line.x1 = self.Line.x1;
            self.Line.y1 = self.Line.y1;
            self.Line.x2 = m.x;
            self.Line.y2 = m.y;
            self.Line.cx = (self.Line.x1+self.Line.x2) / 2;
            self.Line.cy = (self.Line.y1+self.Line.y2) / 2;
      };

    this.end = function() {
        // 1. Get the new mouse position
        var m = self.mouseOffset();

        // What to do on mouseup
        self.Shapes.shape[self.Line.id] =  self.Line;
        comAttr.svgShapeNum.line += 1;

        // Make it active.
        self.setActive(self.Line.id);
    };

//    type_common.shape.line = lineNum++;
    return {
        start: this.start,
        drag: this.drag,
        end: this.end
    };
}


//현재 selected(active 상태인) selector Id 리턴 및 확인
SVGCanvas.prototype.getCurrSelectorId = function(){
 var self = this;
 return d3.select(".active").node().childNodes;
};

//선택된 도형에서 우클릭시 속성 세부관리 메뉴 띄우기
d3.contextMenu = function(menu, svgCanvasId, shapeId){
    var showOverlayAttr = scwin.m.OverlayAttr_wframe;

    if( showOverlayAttr !== undefined ){
        scwin.m.OverlayAttr.close();
    }

 //속성 팝업창에 전달할 오브젝트 가져오기(첫 생성시 없을수도있음)
    var attObj = svgCanvasId.Shapes.shape[shapeId];

 // create the div element that will hold the context menu
    d3.select('body').selectAll('.d3-contexst-menu').data([1])
        .enter()
        .append('div')
        .attr('class','d3-context-menu');

  //close menu
    d3.select('body').on('click.d3-context-menu', function() {
        d3.select('.d3-context-menu').style('display','none');
    });

    //this get executed when a contextmenu event occurs
     var elm = this;

     d3.selectAll('.d3-context-menu').html('');
     var list = d3.selectAll('.d3-context-menu').append('ul');
         list.selectAll('li').data(menu).enter()
         .append('li')
         .html(function(d){
             return d.title;
         })
         .on('click',function(d,i){
             d.action( elm, d, i, attObj, shapeId );
         });

     //display context menu
     d3.select('.d3-context-menu')
         .style('left', (d3.event.pageX - 2) + 'px')
         .style('top', (d3.event.pageY - 2) + 'px')
         .style('display', 'block');

     d3.event.preventDefault();
}

SVGCanvas.prototype.selectShape = function (shapeId){
    var self = this;

    var showOverlayAttr = scwin.m.OverlayAttr_wframe;
    if( showOverlayAttr !== undefined ){
        scwin.m.OverlayAttr.close();
    }

    self.setActive(shapeId);
}

SVGCanvas.prototype.selectEditor = function (editorId){
    var self = this;

    var showOverlayAttr = scwin.m.OverlayAttr_wframe;
    if( showOverlayAttr !== undefined ){
        scwin.m.OverlayAttr.close();
    }

    self.setActive(editorId, false);
}

// 도형의 기본 기능을 가지는 셀렉터
SVGCanvas.prototype.addSelector = function (id, startX, startY, endX, endY, isfnc) {
    var self = this;
    // selector 좌표 왼쪽 상단 -> 오른쪽 상단 -> 오른쪽 하단 -> 왼쪽 하단 순서
    // start, bottom 좌표만 알면 생성 가능하도록 함수 만들것
    var maxx = Math.max(startX, endX);
    var maxy = Math.max(startY, endY);
    var minx  = Math.min(startX, endX);
    var miny  = Math.min(startY, endY);

    var nbax = minx;
    var nbay = miny;
    var nbaw = (maxx - minx);
    var nbah = (maxy - miny);


    // selector 객체 생성
    var params = {};
    params.id = ('selectedBox_' +id);
    params.gid = ('selectorGroup_' +id);
    params.shapeId = id;
    params.canvasId = self;
    var centerX = minx+(maxx- minx)/2;
    var centerY = miny+(maxy- miny)/2;
    params.centerX = centerX;
    params.centerY = centerY;
    params.active = true;
    params.startX = startX;
    params.startY = startY;
    params.endX = endX;
    params.endY = endY;
    params.pointX0 = startX;
    params.pointY0 = startY;
    params.pointX1 = centerX;
    params.pointY1 = centerY;
    params.pointX2 = endX;
    params.pointY2 = endY;

    var selector = new selectorObj(params);
    //SVG 정보저장
    self.Shapes.selector[selector.getId()] = selector;


    // path 로 만든 selector 일단 보류
    /*const dstr = 'M' + nbax + ',' + nbay +
      ' L' + (nbax + nbaw) + ',' + nbay +
      ' ' + (nbax + nbaw) + ',' + (nbay + nbah) +
      ' ' + nbax + ',' + (nbay + nbah) + 'z';*/

    // rect로 만든 selector  bbox 이용
    var selectorRect = self.svg.selectAll("#"+id).node().getBBox();

    //셀렉터 요소 추가
    selectorRect.id = ('selectedBox_' +id);
    selectorRect.fill = "none";
    selectorRect.stroke =  "yellow";
    selectorRect['stroke-width'] = "1";
    selectorRect['stroke-dasharray'] = "5,5";
    selectorRect.style = "pointer-events:none";

    // this holds a reference to the <g> element that holds all visual elements of the selector
    var selectorGroup = addSvgElementFromJson({
      element: 'g',
      attr: {id: ('selectorGroup_' +id)}
    });

    // this holds a reference to the path rect Rect로 만든 셀렉터
    var selectorRect = selectorGroup.appendChild(
            addSvgElementFromJson({
            element: 'rect',
            attr: selectorRect
          })
      );

//    if( isfnc || isfnc === undefined ) {       //정의하지 않았을경우
    var gripRadius = 6;
    var rotateGripPixelX1 = nbax + (nbaw) / 2;
    var rotateGripPixelY1 = nbay;
    var rotateGripPixelX2 = nbax + (nbaw) / 2;
    var rotateGripPixelY2 = nbay - (gripRadius * 5);
    var rotateGripPixelCx = nbax + (nbaw) / 2;
    var rotateGripPixelCy = nbay - (gripRadius * 5);

    // 회전 꼭지점 생성
    var rotateGripConnector = selectorGroup.appendChild(
           addSvgElementFromJson({
                element: 'line',
                attr: {
                  id: ('selectorGrip_rotateconnector'),
                  stroke: 'yellow',
                  x1: rotateGripPixelX1,
                  y1: rotateGripPixelY1,
                  x2: rotateGripPixelX2,
                  y2: rotateGripPixelY2,
                  'stroke-width': '1'
                }
           })
    );

    var rotateGrip = selectorGroup.appendChild(
            addSvgElementFromJson({
                element: 'circle',
                attr: {
                  id: 'selectorGrip_rotate',
                  fill: 'lime',
                  cx: rotateGripPixelCx,
                  cy: rotateGripPixelCy,
                  r: gripRadius,
                  stroke: '#22C',
                  'stroke-width': 2,
                  style: 'cursor:url(/images/CF/SI/png/overlay/rotate.png) 12 12, auto;'
                }
          })
    );

    selector.selPoints.forEach(
            function(d, i) {
                selectorGroup.appendChild(
                        addSvgElementFromJson({
                            element: 'circle',
                            attr: {
                              id: d.id,
                              fill: 'yellow',
                              cx: d.cx,
                              cy: d.cy,
                              r: 3,
                              stroke: 'yellow',
                              'stroke-width': 3,
                              style: d.style,
                            }
                      })
                );
            }
    );
    // SVG에 추가
    document.getElementById("svgMain").appendChild(selectorGroup);

    var shapeId = id;

    // On 함수 추가(리사이즈를 위한 메소드 추가)
    selector.selPoints.forEach(
        function(d, i) {
            var resize = null;
            var shapeType = numberRemove(shapeId);

            if(  shapeType === "line"  ) {
                resize = self.resizeLine();
            } else if(  shapeType === "rect"  ) {
                resize = self.resizeRect();
            } else if(  shapeType === "roundrect"  ) {
                resize = self.resizeRect();
            } else if(  shapeType === "triangle"  ) {
                resize = self.resizeTriangle();
            } else if(  shapeType === "circle"  ) {
                resize = self.resizeCircle();
            } else if(  shapeType === "ellipse"  ) {
                resize = self.resizeEllipse();
            } else if(  shapeType === "arc"  ) {
                //resize = self.resizeArc();
            } else if(  shapeType === "pie"  ) {
                resize = self.resizePie();
            } else if(  shapeType === "hexagon"  ) {
                resize = self.resizeHexagon();
            } else if(  shapeType === "text"  ) {
                //resize = self.resizeText();
            } else if(  shapeType === "g"  ) {
                //resize = self.resizeG();
            } else if(  shapeType === "marker"  ) {
                //resize = self.resizeMarker();
            }

            if( resize != null ){
                d3Canvas.svg.selectAll("#"+d.id).call(
                        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!Todo text 요소 이동 계산로직 만들것 !!!!!!!!!!!!!!!!!!!
                        resize.makeContainer(d, shapeId, selector)
                );
            }
        }
    );

    var rotate = null;
    rotate = self.shapeRotate();
    // 회전 메소드 추가
    d3Canvas.svg.selectAll("#selectorGrip_rotate").call(
            rotate.makeContainer(shapeId, selector, rotateGripPixelCx, rotateGripPixelCy)
    );
/*    } else {        //도형의 이동기능을 제외한 이외의 기능이 없는 셀렉터 생성 (ex : 표 전용 selector)
        // SVG에 추가
        document.getElementById("svgMain").appendChild(selectorGroup);
    }*/

    // 모드 초기화(그리기 모드 => 셀렉트 모드)
    SelectedShape(self,"select");

    return ('selectorGroup_' +id);
}

//도형의 회전을 위한 메소드
SVGCanvas.prototype.shapeRotate = function() {
    var self = this;
    var p1 = [];
    var angle = null;
    var cx = null;
    var cy = null;
    var attrRotate = null;
    var attrTranslate = " ";
    var shapeInfo = null;
    var gripCx;     // ohhk 추가
    var gripCy;     // ohhk 추가
    var attrTransform;  // ohhk 추가

    function calcAngleDeg(cx, cy, x1, y1) {
        return (Math.atan2(y1 - cy, x1 - cx) * 180 ) / Math.PI;
    }

    var makeContainer = function (shapeId, selectorInfo, rotateGripPixelCx, rotateGripPixelCy) {
        var selectorId = selectorInfo.id;
        var selectorGid = selectorInfo.gid;
        shapeInfo = self.Shapes.shape[shapeId];

        if ( shapeInfo.hasOwnProperty('translateInfo') ){
            attrTranslate = shapeInfo.translateInfo;
        }

        var rBB = d3.selectAll("#"+shapeId).node().getBBox();
        var sBB = d3.selectAll("#"+selectorId).node().getBBox();
        var p = {
            x: sBB.x,
            y: sBB.y,
            w: sBB.width,
            h: sBB.height,
        };
        var g = p;
        var resultOjb = {
                shapeOjb : null,
                reSelectorInfo : null,
        };

        var start = function () {
            cx = selectorInfo.centerX;
            cy = selectorInfo.centerY;
            gripCx = rotateGripPixelCx;
            gripCy =rotateGripPixelCy;
        };

        var drag = function () {
            var m = self.mouseOffset();
            var distanceX = gripCx - m.x;
            var distanceY = gripCy - m.y;

            // 좌표 간의 거리를 구하는 공식 직각삼각형 빗변 계산 공식 (피타고라스 정리)
            var distance = Math.abs(Math.sqrt(Math.pow(Math.abs(distanceX),2)+Math.pow(Math.abs(distanceY),2)));
            if( distance > 50 ){
                angle = calcAngleDeg(gripCx, gripCy, m.x, m.y);

                if( angle < 0 ){
                    angle += 360;
                }

                attrRotate = "rotate("+angle+","+cx+","+cy+")";
                attrTransform = attrTranslate + " " + attrRotate;

                self.svg.selectAll("#"+shapeId).attr("transform", attrTransform);
            }
        };

        var end = function () {
            //기존 셀렉터 삭제
            self.delSelection( selectorGid );

            //도형 정보에 회전 정보 추가
            self.Shapes.shape[shapeId]['rotateInfo'] =  attrRotate;

            // Make it active.
            self.setActive(shapeId);
        }

        // return the drag container
        return d3.drag()
        .on('start', start)
        .on('drag', drag)
        .on('end', end);
    }
    // Make drag containers for each
    return {
      makeContainer: makeContainer,
    }
}

SVGCanvas.prototype.resizeRect = function( param ) {
    var self = this;
    function setCoordsData(d, shapeId , selectorInfo) {
        var setResultOjb = {}; //resize 결과를 보내줄 오브젝트(도형갱신정보, 셀렉트 갱신 정보)
        // Set the coordinates of a rectangle-group
        var children = d3.selectAll('#' + shapeId);
        // Main Rectangle
        children.attr('x', d.x)
          .attr('y', d.y)
          .attr('width', d.w)
          .attr('height', d.h);

        //도형 및 셀렉터 갱신을 위한 결과 오브젝트
        setResultOjb = self.getShapeData(shapeId);
        setResultOjb.x = d.x;
        setResultOjb.y = d.y;
        setResultOjb.width = d.w;
        setResultOjb.height = d.h;
        setResultOjb.cx = (d.x+d.w) / 2;
        setResultOjb.cy = (d.y+d.w) / 2;

        return setResultOjb;
    }

    // Resize the rectangle by dragging the corners
    function getDragCorners() {
        return {
          nw    : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x + bb0.width, m.x));
            d.y = Math.max(0, Math.min(bb0.y + bb0.height, m.y));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x + bb0.width - m.x)) : d.w;
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y + bb0.height - m.y)) : d.h;
          },
          n     : function (d, bb0, m) {
            d.y = Math.max(0, Math.min(bb0.y + bb0.height, m.y));
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y + bb0.height - m.y)) : d.h;
          },
          ne    : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x, m.x));
            d.y = Math.max(0, Math.min(bb0.y + bb0.height, m.y));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x - m.x)) : d.w;
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y + bb0.height - m.y)) : d.h;
          },
          w     : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x + bb0.width, m.x));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x + bb0.width - m.x)) : d.w;
          },
          e     : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x, m.x));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x - m.x)) : d.w;
          },
          sw    : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x + bb0.width, m.x));
            d.y = Math.max(0, Math.min(bb0.y, m.y));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x + bb0.width - m.x)) : d.w;
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y - m.y)) : d.h;
          },
          s     : function (d, bb0, m) {
            d.y = Math.max(0, Math.min(bb0.y, m.y));
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y - m.y)) : d.h;
          },
          se    : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x, m.x));
            d.y = Math.max(0, Math.min(bb0.y, m.y));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x - m.x)) : d.w;
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y - m.y)) : d.h;
          }
        };
      }

      var makeContainer = function ( paramOjb, shapeId, selectorInfo ) {
          // Make a container, which depends on the corner (specified by `id`)
          var dragCorners
                   ,cursor  = null
                   ,bb0 = null
                   ,g = null;
          var  resultOjb = {
                  shapeOjb : null,
                  reSelectorInfo : null,
          };

          var _selectorOjb = paramOjb;
          var selectorId = selectorInfo.id;
          var selectorGid = selectorInfo.gid;
          var rBB = d3.selectAll("#"+shapeId).node().getBBox();
          var sBB = d3.selectAll("#"+selectorId).node().getBBox();
          var p = {
              x: sBB.x,
              y: sBB.y,
              w: sBB.width,
              h: sBB.height,
          };
          g = p;

        // Get the correct transformation function
        dragCorners = getDragCorners()[_selectorOjb.method.position];
        var start = function () {
            //도형의 현재 좌표들 가지고 있기
            bb0 = rBB;
        }

        var drag = function () {
            var m = d3Canvas.mouseOffset();
            // 각 resize포지션 별로 정해진 함수를 호출 하여 계산값 가져오기
            dragCorners(g, bb0, m);
            // 위에서 계산된 값을 가지고 도형 및 셀렉터 다시 그리고 해당 데이터 가져오기
            resultOjb.shapeOjb = setCoordsData(g, shapeId, selectorInfo);
            resultOjb.reSelectorInfo = self.tempSelectorDraw(g, shapeId, selectorInfo);
        }

        var end = function () {
          //기존 셀렉터 삭제
            self.delSelection( selectorGid );
          //셀렉터 재생성
            var reSelOjb = resultOjb.reSelectorInfo;
            self.addSelector(reSelOjb.shapeId, reSelOjb.startX, reSelOjb.startY, reSelOjb.endX, reSelOjb.endY);
          //도형데이터 업데이트
            self.Shapes.shape[shapeId] =  resultOjb.shapeOjb;
          // Make it active.
          self.setActive(reSelOjb.shapeId);
        }

        // return the drag container
        return d3.drag()
          .on('start', start)
          .on('drag', drag)
          .on('end', end);
      }
      // Make drag containers for each
      return {
        makeContainer: makeContainer,
      }
}

SVGCanvas.prototype.resizeLine = function( param ) {
    var self = this;
    function setCoordsData(d, shapeId , selectorInfo, posit ) {
        var setResultOjb = {}; //resize 결과를 보내줄 오브젝트(도형갱신정보, 셀렉트 갱신 정보)

        var setResultOjb = stmp.cloneDeep(self.Shapes.shape[shapeId]);
        var x1 = setResultOjb.x1;
        var y1 = setResultOjb.y1;
        var x2 = setResultOjb.x2;
        var y2 = setResultOjb.y2;

        // 정점별 계산 로직 선택
        // N으로 시작하는 애들은 삼각형의 가장 상위 좌표에 영향
        // S로 시작하는 애들은 하위 변의 좌표들에 영향
        // E를 포함하면 오른쪽 끝점, W를 포함하면 왼쪽 끝점
        if( posit === "nw" ){
            if( y1 < y2 ){
                y1 = d.y;
                if( d.y == y2 ){
                    y1 = y2 + d.h;
                }
            } else {
                y2 = d.y;
                if( d.y == y1 ){
                    y2 = y1 + d.h;
                }
            }

            if( x1 > x2 ){
                x2 = d.x+d.w;
                if( x1 == d.x+d.w ){
                    x2 = d.x;
                }
            }
            else {
                x1 = d.x+d.w;
                if( x2 == d.x+d.w ){
                    x1 = d.x;
                }
            }
        } else if ( posit === "n" ) {
            if( y1 < y2 ){
                y1 = d.y;
                if( d.y == y2 ){
                    y1 = y2 + d.h;
                }
            } else {
                y2 = d.y;
                if( d.y == y1 ){
                    y2 = y1 + d.h;
                }
            }
        } else if ( posit === "ne" ) {
            if( y1 < y2 ){
                y1 = d.y;
                if( d.y == y2 ){
                    y1 = y2 + d.h;
                }
            } else {
                y2 = d.y;
                if( d.y == y1 ){
                    y2 = y1 + d.h;
                }
            }

            if( x1 < x2 ){
                x2 = d.x+d.w;
                if( x1 == d.x+d.w ){
                    x2 = d.x;
                }
            }
            else {
                x1 = d.x+d.w;
                if( x2 == d.x+d.w ){
                    x1 = d.x;
                }
            }
        } else if ( posit === "w" ) {
            if( x1 > x2 ){
                x2 = d.x+d.w;
                if( x1 == d.x+d.w ){
                    x2 = d.x;
                }
            }
            else {
                x1 = d.x+d.w;
                if( x2 == d.x+d.w ){
                    x1 = d.x;
                }
            }
        } else if ( posit === "e" ) {
            if( x1 < x2 ){
                x2 = d.x+d.w;
                if( x1 == d.x+d.w ){
                    x2 = d.x;
                }
            }
            else {
                x1 = d.x+d.w;
                if( x2 == d.x+d.w ){
                    x1 = d.x;
                }
            }
        } else if ( posit === "sw" ) {
            if( y1 < y2 ){
                y2 = d.y + d.h;
                if( d.y+d.h == y1 ){
                    y2 = d.y;
                }
            } else {
                y1 = d.y + d.h;
                if( d.y+d.h == y2 ){
                    y1 = d.y;
                }
            }

            if( x1 > x2 ){
                x2 = d.x+d.w;
                if( x1 == d.x+d.w ){
                    x2 = d.x;
                }
            }
            else {
                x1 = d.x+d.w;
                if( x2 == d.x+d.w ){
                    x1 = d.x;
                }
            }
        } else if ( posit === "s" ) {
            if( y1 < y2 ){
                y2 = d.y + d.h;
                if( d.y+d.h == y1 ){
                    y2 = d.y;
                }
            } else {
                y1 = d.y + d.h;
                if( d.y+d.h == y2 ){
                    y1 = d.y;
                }
            }
        } else if ( posit === "se" ) {
            if( y1 < y2 ){
                y2 = d.y + d.h;
                if( d.y+d.h == y1 ){
                    y2 = d.y;
                }
            } else {
                y1 = d.y + d.h;
                if( d.y+d.h == y2 ){
                    y1 = d.y;
                }
            }

            if( x1 < x2 ){
                x2 = d.x+d.w;
                if( x1 == d.x+d.w ){
                    x2 = d.x;
                }
            }
            else {
                x1 = d.x+d.w;
                if( x2 == d.x+d.w ){
                    x1 = d.x;
                }
            }
        }

        // Set the coordinates of shape
        var children = d3.selectAll('#' + shapeId);

        children.attr('x1', x1)
          .attr('y1', y1)
          .attr('x2', x2)
          .attr('y2', y2);

        //도형 및 셀렉터 갱신을 위한 결과 오브젝트
        setResultOjb.x1 = d.x;
        setResultOjb.y1 = d.y;
        setResultOjb.x2 = d.x + d.w;
        setResultOjb.y2 = d.y + d.h;
        setResultOjb.cx = (d.x+d.w) / 2;
        setResultOjb.cy = (d.y+d.w) / 2;

        return setResultOjb;
    }

    // Resize the rectangle by dragging the corners
    function getDragCorners() {
        return {
          nw    : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x + bb0.width, m.x));
            d.y = Math.max(0, Math.min(bb0.y + bb0.height, m.y));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x + bb0.width - m.x)) : d.w;
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y + bb0.height - m.y)) : d.h;
          },
          n     : function (d, bb0, m) {
            d.y = Math.max(0, Math.min(bb0.y + bb0.height, m.y));
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y + bb0.height - m.y)) : d.h;
          },
          ne    : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x, m.x));
            d.y = Math.max(0, Math.min(bb0.y + bb0.height, m.y));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x - m.x)) : d.w;
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y + bb0.height - m.y)) : d.h;
          },
          w     : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x + bb0.width, m.x));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x + bb0.width - m.x)) : d.w;
          },
          e     : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x, m.x));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x - m.x)) : d.w;
          },
          sw    : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x + bb0.width, m.x));
            d.y = Math.max(0, Math.min(bb0.y, m.y));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x + bb0.width - m.x)) : d.w;
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y - m.y)) : d.h;
          },
          s     : function (d, bb0, m) {
            d.y = Math.max(0, Math.min(bb0.y, m.y));
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y - m.y)) : d.h;
          },
          se    : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x, m.x));
            d.y = Math.max(0, Math.min(bb0.y, m.y));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x - m.x)) : d.w;
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y - m.y)) : d.h;
          }
        };
      }

      var makeContainer = function ( paramOjb, shapeId, selectorInfo ) {
          // Make a container, which depends on the corner (specified by `id`)
          var dragCorners
                   ,cursor  = null
                   ,bb0 = null
                   ,g = null
                   ,posit = paramOjb.method.position;
          var  resultOjb = {
                  shapeOjb : null,
                  reSelectorInfo : null,
          };

          var _selectorOjb = paramOjb;
          var selectorId = selectorInfo.id;
          var selectorGid = selectorInfo.gid;
          var rBB = d3.selectAll("#"+shapeId).node().getBBox();
          var sBB = d3.selectAll("#"+selectorId).node().getBBox();
          var p = {
              x: sBB.x,
              y: sBB.y,
              w: sBB.width,
              h: sBB.height,
          };
          g = p;

        // Get the correct transformation function
        dragCorners = getDragCorners()[_selectorOjb.method.position];
        var start = function () {
            //도형의 현재 좌표들 가지고 있기
            bb0 = rBB;
        }

        var drag = function () {
            var m = d3Canvas.mouseOffset();
            // 각 resize포지션 별로 정해진 함수를 호출 하여 계산값 가져오기
            dragCorners(g, bb0, m);
            // 위에서 계산된 값을 가지고 도형 및 셀렉터 다시 그리고 해당 데이터 가져오기
            resultOjb.shapeOjb = setCoordsData(g, shapeId, selectorInfo, posit);
            resultOjb.reSelectorInfo = self.tempSelectorDraw(g, shapeId, selectorInfo);
        }

        var end = function () {
          //기존 셀렉터 삭제
            self.delSelection( selectorGid );
          //셀렉터 재생성
            var reSelOjb = resultOjb.reSelectorInfo;
            self.addSelector(reSelOjb.shapeId, reSelOjb.startX, reSelOjb.startY, reSelOjb.endX, reSelOjb.endY);
          //도형데이터 업데이트
            self.Shapes.shape[shapeId] =  resultOjb.shapeOjb;
          // Make it active.
          self.setActive(reSelOjb.shapeId);
        }

        // return the drag container
        return d3.drag()
          .on('start', start)
          .on('drag', drag)
          .on('end', end);
      }
      // Make drag containers for each
      return {
        makeContainer: makeContainer,
      }
}

// 도형 resize
SVGCanvas.prototype.tempSelectorDraw = function (d, shapeId , selectorInfo, transform) {
    var self = this;
    //결과 반환을 위한 Object
    var resultOjb = this;
    var resizeSelectorOjb = d3.selectAll('#' + selectorInfo.id);
    var shapeBBox = d3.selectAll('#' + shapeId);

    /*var baseCoords = resizeSelectorOjb.node().getBBox();*/
    var baseCoords = shapeBBox.node().getBBox();

    var startX  = baseCoords.x;
    var startY  = baseCoords.y;
    var endX  = baseCoords.x+baseCoords.width;
    var endY  = baseCoords.y+baseCoords.height;

    var transformYn = false;

    if( transform !== undefined ) {
        transformYn = true;
    }

    // Main Rectangle       //변화중일때는 점선 삭제
    resizeSelectorOjb.attr('x', startX)
      .attr('y', startY)
      .attr('width', 0)
      .attr('height', 0);

    var maxx = Math.max(startX, endX);
    var maxy = Math.max(startY, endY);
    var minx  = Math.min(startX, endX);
    var miny  = Math.min(startY, endY);

    var centerX = minx+(maxx- minx)/2;
    var centerY = miny+(maxy- miny)/2;

    var nbax = minx;
    var nbay = miny;
    var nbaw = (maxx - minx);
    var nbah = (maxy - miny);

    var gripRadius = 6;
    var rotateGripPixelX1 = nbax + (nbaw) / 2;
    var rotateGripPixelY1 = nbay;
    var rotateGripPixelX2 = nbax + (nbaw) / 2;
    var rotateGripPixelY2 = nbay - (gripRadius * 5);
    var rotateGripPixelCx = nbax + (nbaw) / 2;
    var rotateGripPixelCy = nbay - (gripRadius * 5);

    self.svg.selectAll("#"+'selectorGrip_rotateconnector')
        .attr("x1", rotateGripPixelX1)
        .attr("y1", rotateGripPixelY1)
        .attr("x2", rotateGripPixelX2)
        .attr("y2", rotateGripPixelY2);

    self.svg.selectAll("#"+'selectorGrip_rotate')
        .attr("cx", rotateGripPixelCx)
        .attr("cy", rotateGripPixelCy);

    var pointX0 = startX;
    var pointY0 = startY;
    var pointX1 = centerX;
    var pointY1 = centerY;
    var pointX2 = endX;
    var pointY2 = endY;

    var resizePoints = [
        {
            id : "selectorGrip_resize_nw",
            cx : pointX0,
            cy : pointY0,
        },
        {
            id : "selectorGrip_resize_n",
            cx : pointX1,
            cy : pointY0,
        },
        {
            id : "selectorGrip_resize_ne",
            cx : pointX2,
            cy : pointY0,
        },
        {
            id : "selectorGrip_resize_w",
            cx : pointX0,
            cy : pointY1,
        },
        {
            id : "selectorGrip_resize_e",
            cx : pointX2,
            cy : pointY1,

        },
        {
            id : "selectorGrip_resize_sw",
            cx : pointX0,
            cy : pointY2,
        },
        {
            id : "selectorGrip_resize_s",
            cx : pointX1,
            cy : pointY2,
        },
        {
            id : "selectorGrip_resize_se",
            cx : pointX2,
            cy : pointY2,
        },
    ];

    // resizePoints 위치 바꾸기
    resizePoints.forEach(function(d, i){
        self.svg.selectAll('#'+selectorInfo.gid).selectAll('#'+d.id)
        .attr("cx", d.cx)
        .attr("cy", d.cy)
        .attr("fill", 'red')
        .attr("stroke", 'red');
    });


    if( transformYn ) {
        self.svg.selectAll('#'+selectorInfo.gid)
        .attr("transform", transform);
    }

    //셀렉터 갱신을 위한 결과 오브젝트
    return resultOjb = {
            shapeId : shapeId,
            startX : startX,
            startY : startY,
            endX : endX,
            endY : endY,
    };
}

SVGCanvas.prototype.getShapeData = function (shapeId) {
    var self = this;

    return self.Shapes.shape[shapeId];
}

SVGCanvas.prototype.delSelection = function (Id) {
    var self = this;

    self.svg.selectAll("#"+Id).remove();
}

SVGCanvas.prototype.setActive = function (id, isfnc) {
    var self = this;
    // Sets class to active for selected groups.
    var deactivate = false;
    var objectId = id;

    // When should all other groups be deactivated?
    //  1.A If the ctrl key is not pressed
    //  1.B If the present element isn't already active
    //  (Use De Morgan's Rules for this one.)
    //  deactivate = deactivate || !(d3.event.sourceEvent.ctrlKey || d3.selectAll('g.' + id).classed('active'));
    /*deactivate = deactivate || d3.selectAll('g.' + id).classed('active');*/

    //  2. If we didn't force it to be.
    /*  deactivate = deactivate || force_clear;

    this.svg.selectAll('g.active').classed('active', false);*/

    // If any of these conditions met, clear the active elements.
    /*  if (deactivate) {
    this.svg.selectAll('g.active').classed('active', false);
    }*/

    this.svg.selectAll('g.active').classed('active', false);

    // Add 'active' class to any 'g' element with id = id passed.
    //1 모든 셀렉터 삭제
    $('[id^=selectorGroup]').each(function(e,i) {
        var selectorId = i.id;
        self.delSelection( selectorId );
    });

    //2 선택된 객체 셀렉터 생성
    // Add Selector

    // BBox 를 이용하여 해당 객체 시작과 끝 구해오기
    var selectorRect = self.svg.selectAll("#"+id).node().getBBox();

    var startX = selectorRect.x;
    var startY = selectorRect.y;
    var endX  = selectorRect.x + selectorRect.width;
    var endY  = selectorRect.y + selectorRect.height;

    var selectorId = self.addSelector(id, startX, startY, endX, endY, isfnc);
    d3.selectAll('g.' + id).classed('active', true);

    var rotateInfo = null;
    var translateInfo = " ";
    /*******
    var shapeInfo = self.Shapes.shape[id];

    if( shapeInfo.hasOwnProperty('translateInfo') ){
        translateInfo = shapeInfo.translateInfo;
        self.svg.selectAll("#"+selectorId).attr("transform", translateInfo);
    }

    if( shapeInfo.hasOwnProperty('rotateInfo') ){
        rotateInfo = translateInfo +" "+shapeInfo.rotateInfo;
        self.svg.selectAll("#"+selectorId).attr("transform", rotateInfo);
    }
    **********/

    //임시수정
    // 이동 및 회전 체크를 위한 데이터 가져오기
    var transformInfo = self.svg.selectAll('#'+id).node().getAttribute('transform');
    if( transformInfo != null ) {
        var transDivid =transformInfo.split(' ');

        // 이동 및 회전 정보를 체크하여 가져오기
        transDivid.forEach(
            function(d, i){
                if( d.indexOf('translate') > -1) {
                    translateInfo = d;
                    self.svg.selectAll("#"+selectorId).attr("transform", translateInfo);
                }
                if( d.indexOf('rotate') > -1) {
                    rotateInfo = translateInfo +" "+d;
                    self.svg.selectAll("#"+selectorId).attr("transform", rotateInfo);
                }
            }
        )
    }

    SelectedShape(self,"select");
}

//3.btn_icons4s-둥근사각형
SVGCanvas.prototype.makeAddRoundrect = function() {

    // Methods for adding rectangles to the svg.
    var self = this;

    start = function() {
        // 0. Get Id
       self.Rect.id = idMaker(self,"rect");
       var shapeId = self.Rect.id;

      // 1. Get mouse location in SVG
      var m = self.mouseOffset();
      self.Rect.x = m.x;
      self.Rect.y = m.y;

      // 2. Make a rectangle
      self.Rect.g = self.svg
        .append('g')
        .attr('class', 'g-rect '+shapeId);

      // 3. Make a rectangle
      self.Rect.r = self.Rect.g //self.zoomG
        .append('rect') // An SVG element
        .attr('x', self.Rect.x) // Position at mouse location
        .attr('y', self.Rect.y)
        .attr('width', 1) // Make it tiny
        .attr('height', 1)
        .attr('rx', 30)
        .attr('ry', 30)
        .attr('id', shapeId)
        .style('stroke', comAttr.commonAttr.stroke)
        .style('stroke-width', comAttr.commonAttr.strokeWidth)
        .style('fill', comAttr.commonAttr.color)
        .on("click",
                function (d){
                    self.selectShape(shapeId);
                }
            )
        .on("contextmenu",
                function (d){
                    //우클릭시 서브 메뉴바 띄우기 예제
                    d3.contextMenu(menu, self, shapeId);
                }
            );
    }

    drag = function() {
      // What to do when mouse is dragged
      // 1. Get the new mouse position
      var m = self.mouseOffset();
      // 2. Update the attributes of the rectangle
      self.Rect.r.attr('x', Math.min(self.Rect.x, m.x))
        .attr('y', Math.min(self.Rect.y, m.y))
        .attr('width', Math.abs(self.Rect.x - m.x))
        .attr('height', Math.abs(self.Rect.y - m.y));

      self.Rect.x = Math.min(self.Rect.x, m.x);
      self.Rect.y = Math.min(self.Rect.y, m.y);
      self.Rect.width = Math.abs(self.Rect.x - m.x);
      self.Rect.height =  Math.abs(self.Rect.y - m.y);
      self.Rect.cx =  (self.Rect.x+self.Rect.width) / 2;
      self.Rect.cy =  (self.Rect.y+self.Rect.height) / 2;
    }

    end = function() {
        // 1. Get the new mouse position
        var m = self.mouseOffset();
        // What to do on mouseup
        self.Shapes.shape[self.Rect.id] =  self.Rect;
        /*self.Shapes.push(self.Roundrect);*/
        comAttr.svgShapeNum.rect += 1;

        // selector 좌표 왼쪽 상단 -> 오른쪽 상단 -> 오른쪽 하단 -> 왼쪽 하단 순서
        // start, bottom 좌표만 알면 생성 가능하도록 함수 만들것
        var startX = self.Rect.x;
        var startY = self.Rect.y;
        var endX  = m.x;
        var endY  = m.y;

        var selectorId = self.addSelector(self.Rect.id, startX, startY, endX, endY);

        // Make it active.
        self.setActive(self.Rect.id);
    }

    return {
      start: start,
      drag: drag,
      end: end
    };
  }

SVGCanvas.prototype.keydownEventHandlers = function () {
  // Event handler for keydown events
  // Press 'Delete' to remove all active groups.
  if (d3.event.key === 'Delete') {
    d3.selectAll('g.active').remove();
  }
}

// 2.btn_icons3s-사각형
SVGCanvas.prototype.makeAddRect = function() {
    // Methods for adding rectangles to the svg.
    var self = this;

    start = function() {
        // 0. Get Id
       self.Rect.id = idMaker(self,"rect");
       var shapeId = self.Rect.id;

      // 1. Get mouse location in SVG
      var m = self.mouseOffset();
      self.Rect.x = m.x;
      self.Rect.y = m.y;

      // 2. Make a rectangle
      self.Rect.g = self.svg
        .append('g')
        .attr('class', 'g-rect '+shapeId);

      // 3. Make a rectangle
      self.Rect.r = self.Rect.g //self.zoomG
        .append('rect') // An SVG element
        .attr('x', self.Rect.x) // Position at mouse location
        .attr('y', self.Rect.y)
        .attr('width', 1) // Make it tiny
        .attr('height', 1)
        .attr('id', shapeId)
        .style('stroke', comAttr.commonAttr.stroke)
        .style('stroke-width', comAttr.commonAttr.strokeWidth)
        .style('fill', comAttr.commonAttr.color)
        .on("click",
                function (d){
                    self.selectShape(shapeId);
                }
            )
        .on("contextmenu",
                function (d){
                    //우클릭시 서브 메뉴바 띄우기 예제
                    d3.contextMenu(menu, self, shapeId);
                }
            );
    }

    drag = function() {
      // What to do when mouse is dragged
      // 1. Get the new mouse position
      var m = self.mouseOffset();
      // 2. Update the attributes of the rectangle
      self.Rect.r.attr('x', Math.min(self.Rect.x, m.x))
        .attr('y', Math.min(self.Rect.y, m.y))
        .attr('width', Math.abs(self.Rect.x - m.x))
        .attr('height', Math.abs(self.Rect.y - m.y));

      self.Rect.x = Math.min(self.Rect.x, m.x);
      self.Rect.y = Math.min(self.Rect.y, m.y);
      self.Rect.width = Math.abs(self.Rect.x - m.x);
      self.Rect.height =  Math.abs(self.Rect.y - m.y);
      self.Rect.cx =  (self.Rect.x+self.Rect.width) / 2;
      self.Rect.cy =  (self.Rect.y+self.Rect.height) / 2;
    }

    end = function() {
        // 1. Get the new mouse position
        var m = self.mouseOffset();
        // What to do on mouseup
        self.Shapes.shape[self.Rect.id] =  self.Rect;
        /*self.Shapes.push(self.Roundrect);*/
        comAttr.svgShapeNum.rect += 1;

        // selector 좌표 왼쪽 상단 -> 오른쪽 상단 -> 오른쪽 하단 -> 왼쪽 하단 순서
        // start, bottom 좌표만 알면 생성 가능하도록 함수 만들것
        var startX = self.Rect.x;
        var startY = self.Rect.y;
        var endX  = m.x;
        var endY  = m.y;

        var selectorId = self.addSelector(self.Rect.id, startX, startY, endX, endY);

        // Make it active.
        self.setActive(self.Rect.id);
    }

    return {
      start: start,
      drag: drag,
      end: end
    };
  }

//3.btn_icons5s-삼각형
SVGCanvas.prototype.makeAddTriangle = function() {
  // Methods for adding Triangle to the svg.
  var self = this;
  var startX = null;
  var startY = null;

  start = function() {
      // 0. Get Id
      self.Triangle.id = idMaker(self,"triangle");
      var shapeId = self.Triangle.id;
    //Add a rectangle
    // 1. Get mouse location in SVG
    var m = self.mouseOffset();
    startX = m.x;
    startY = m.y;
    // 2. Make a triangle
    self.Triangle.r = self.svg //self.zoomG
      .append('g')
      .attr('class', 'g-triangle ' +shapeId )
      .append('polygon') // An SVG `rect` element
      .attr('points', startX+","+startY+" ") // Position at mouse location
      .attr('id',self.Triangle.id)
      .style('stroke', comAttr.commonAttr.stroke)
      .style('stroke-width', comAttr.commonAttr.strokeWidth)
      .style('fill', comAttr.commonAttr.color)
      .on("click",
              function (d){
                  self.selectShape(shapeId);
              }
          )
      .on("contextmenu",
              function (d){
                  //우클릭시 서브 메뉴바 띄우기 예제
                  d3.contextMenu(menu, self, shapeId);
              }
          );
  }

  drag = function() {
    // What to do when mouse is dragged
    // 1. Get the new mouse position
    var m = self.mouseOffset();
    // 2. Update the attributes of the rectangle
    var dist = Math.sqrt(
      Math.pow(Math.abs(startX - m.x),2)
    + Math.pow(Math.abs(startY - m.y),2)
    );

    self.Triangle.dist = dist;

    // 셀렉터를 기준으로 좌표값을 미리 가지고있는다.
    var nx = null;
    var ny = null;
    var swx = null;
    var swy = null;
    var sex = null;
    var sey = null;
    // 중심값 구하기
    var centerX = nx;
    var centerY = (ny+sey) / 2;

    nx    = startX;
    ny    = startY-dist;
    swx = startX-dist;
    swy = startY;
    sex = startX+dist;
    sey = startY;

    var points = startX+","+(startY-dist)+" ";
        points += (startX-dist)+","+startY+" ";
        points += (startX+dist)+","+startY;
    var r = self.Triangle.r.attr('points');

    self.Triangle.r
      .attr('points',points)
      .style('stroke', comAttr.commonAttr.stroke)
      .style('stroke-width', comAttr.commonAttr.strokeWidth)
      .style('fill', comAttr.commonAttr.color);

    self.Triangle.points = points;
    self.Triangle.nx    = nx  ;
    self.Triangle.ny    = ny  ;
    self.Triangle.swx  = swx ;
    self.Triangle.swy  = swy ;
    self.Triangle.sex  = sex ;
    self.Triangle.sey  = sey ;
    self.Triangle.cx  = centerX ;
    self.Triangle.cy  = centerY ;
  }

  end = function() {
      // 1. Get the new mouse position
      var m = self.mouseOffset();
      // What to do on mouseup
      self.Shapes.shape[self.Triangle.id] =  self.Triangle;
      comAttr.svgShapeNum.triangle += 1;

      // Make it active.
      self.setActive(self.Triangle.id);
  }

  return {
    start: start,
    drag: drag,
    end: end
  };
}

SVGCanvas.prototype.resizeTriangle = function( param ) {
    var self = this;
    function setCoordsData(d, shapeId , selectorInfo, posit) {
        var setResultOjb = stmp.cloneDeep(self.Shapes.shape[shapeId]);
        var cx = setResultOjb.cx;
        var cy = setResultOjb.cy;

        var nx = setResultOjb.nx;
        var ny = setResultOjb.ny;
        var swx = setResultOjb.swx;
        var swy = setResultOjb.swy;
        var sex = setResultOjb.sex;
        var sey = setResultOjb.sey;
        var points = "";

        // 정점별 계산 로직 선택
        // N으로 시작하는 애들은 삼각형의 가장 상위 좌표에 영향
        // S로 시작하는 애들은 하위 변의 좌표들에 영향
        // E를 포함하면 오른쪽 끝점, W를 포함하면 왼쪽 끝점
        if( posit === "nw" ){
            ny = d.y;
            if(swy == d.y){
                ny = swy + d.h;
            }
            swx = d.x;
            nx = swx+((sex - swx) / 2);
            if( d.x - sex > -1 ){
                sex = d.x+d.w;
                nx = swx+((sex - swx) / 2);
            }
        } else if ( posit === "n" ) {
            ny = d.y;
            if(swy == d.y){
                ny = swy + d.h;
            }
        } else if ( posit === "ne" ) {
            ny = d.y;
            if(swy == d.y){
                ny = swy + d.h;
            }
            sex = d.x+d.w;
            nx = swx+((sex - swx) / 2);
            if( (swx - d.x) > 1 ){
                swx = d.x;
                nx = swx+((sex - swx) / 2);
            }
        } else if ( posit === "w" ) {
            swx = d.x;
            nx = swx+((sex - swx) / 2);
            if( d.x - sex > -1 ){
                sex = d.x+d.w;
                nx = swx+((sex - swx) / 2);
            }
        } else if ( posit === "e" ) {
            sex = d.x+d.w;
            nx = swx+((sex - swx) / 2);
            if( (swx - d.x) > 1 ){
                swx = d.x;
                nx = swx+((sex - swx) / 2);
            }
        } else if ( posit === "sw" ) {
            swy = d.y+d.h;
            sey = d.y+d.h;
            if(ny > d.y){
                swy = swy-d.h;
                sey = sey-d.h;
            }
            swx = d.x;
            nx = swx+((sex - swx) / 2);
            if( d.x - sex > -1 ){
                sex = d.x+d.w;
                nx = swx+((sex - swx) / 2);
            }
        } else if ( posit === "s" ) {
            swy = d.y+d.h;
            sey = d.y+d.h;
            if(ny > d.y){
                swy = swy-d.h;
                sey = sey-d.h;
            }
        } else if ( posit === "se" ) {
            swy = d.y+d.h;
            sey = d.y+d.h;
            if(ny > d.y){
                swy = swy-d.h;
                sey = sey-d.h;
            }
            sex = d.x+d.w;
            nx = swx+((sex - swx) / 2);
            if( (swx - d.x) > 1 ){
                swx = d.x;
                nx = swx+((sex - swx) / 2);
            }
        }

        var points = nx + "," + ny + " "+
                        swx + "," + swy + " "+
                        sex + "," + sey + " ";

        // Set the coordinates of shape
        var children = d3.selectAll('#' + shapeId);

        children.attr('points', points);

        //도형 및 셀렉터 갱신을 위한 결과 오브젝트
        setResultOjb.nx = nx;
        setResultOjb.ny = ny;
        setResultOjb.swx = swx;
        setResultOjb.swy = swy;
        setResultOjb.sex = sex;
        setResultOjb.sey = sey;
        setResultOjb.points = points;
        setResultOjb.cx = nx;
        setResultOjb.cy = (ny+sey) / 2;

        return setResultOjb;
    }

    // Resize the rectangle by dragging the corners
    function getDragCorners() {
        return {
          nw    : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x + bb0.width, m.x));
            d.y = Math.max(0, Math.min(bb0.y + bb0.height, m.y));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x + bb0.width - m.x)) : d.w;
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y + bb0.height - m.y)) : d.h;
          },
          n     : function (d, bb0, m) {
            d.y = Math.max(0, Math.min(bb0.y + bb0.height, m.y));
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y + bb0.height - m.y)) : d.h;
          },
          ne    : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x, m.x));
            d.y = Math.max(0, Math.min(bb0.y + bb0.height, m.y));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x - m.x)) : d.w;
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y + bb0.height - m.y)) : d.h;
          },
          w     : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x + bb0.width, m.x));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x + bb0.width - m.x)) : d.w;
          },
          e     : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x, m.x));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x - m.x)) : d.w;
          },
          sw    : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x + bb0.width, m.x));
            d.y = Math.max(0, Math.min(bb0.y, m.y));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x + bb0.width - m.x)) : d.w;
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y - m.y)) : d.h;
          },
          s     : function (d, bb0, m) {
            d.y = Math.max(0, Math.min(bb0.y, m.y));
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y - m.y)) : d.h;
          },
          se    : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x, m.x));
            d.y = Math.max(0, Math.min(bb0.y, m.y));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x - m.x)) : d.w;
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y - m.y)) : d.h;
          }
        };
      }

      var makeContainer = function ( paramOjb, shapeId, selectorInfo ) {
          // Make a container, which depends on the corner (specified by `id`)
          var dragCorners
                   ,cursor  = null
                   ,bb0 = null
                   ,g = null
                   ,posit = paramOjb.method.position;
          var  resultOjb = {
                  shapeOjb : null,
                  reSelectorInfo : null,
          };

          var _selectorOjb = paramOjb;
          var selectorId = selectorInfo.id;
          var selectorGid = selectorInfo.gid;
          var rBB = d3.selectAll("#"+shapeId).node().getBBox();
          var sBB = d3.selectAll("#"+selectorId).node().getBBox();
          var p = {
              x: sBB.x,
              y: sBB.y,
              w: sBB.width,
              h: sBB.height,
          };
          g = p;

        // Get the correct transformation function
        dragCorners = getDragCorners()[posit];
        var start = function () {
            //도형의 현재 좌표들 가지고 있기
            bb0 = rBB;
        }

        var drag = function () {
            var m = d3Canvas.mouseOffset();
            // 각 resize포지션 별로 정해진 함수를 호출 하여 계산값 가져오기
            dragCorners(g, bb0, m);
            // 위에서 계산된 값을 가지고 도형 및 셀렉터 다시 그리고 해당 데이터 가져오기
            resultOjb.shapeOjb = setCoordsData(g, shapeId, selectorInfo, posit);
            resultOjb.reSelectorInfo = self.tempSelectorDraw(g, shapeId, selectorInfo);
        }

        var end = function () {
            //기존 셀렉터 삭제
            self.delSelection( selectorGid );
            //셀렉터 재생성
            var reSelOjb = resultOjb.reSelectorInfo;
            self.addSelector(reSelOjb.shapeId, reSelOjb.startX, reSelOjb.startY, reSelOjb.endX, reSelOjb.endY);
            //도형데이터 업데이트
            self.Shapes.shape[shapeId] =  resultOjb.shapeOjb;
            // Make it active.
            self.setActive(reSelOjb.shapeId);
        }

        // return the drag container
        return d3.drag()
          .on('start', start)
          .on('drag', drag)
          .on('end', end);
      }
      // Make drag containers for each
      return {
        makeContainer: makeContainer,
      }
}

//5.btn_icons6s-원
SVGCanvas.prototype.makeAddCircle = function() {
    // Methods for adding rectangles to the svg.
    var self = this;

    start = function() {
    // 0. Get Id
    self.Circle.id = idMaker(self,"circle");
    var shapeId = self.Circle.id;
     // 1. Get mouse location in SVG
     var m = self.mouseOffset();
     self.Circle.cx = m.x;
     self.Circle.cy = m.y;

     // 2. Make a rectangle
     self.Circle.r = self.svg //self.
       .append('g')
       .attr('class', 'g-circle ' + shapeId)
       .append('circle')
       .attr('cx', self.Circle.cx) // Position at mouse location
       .attr('cy', self.Circle.cy)
       .attr('r', 1) // Make it tiny
         .attr('id',shapeId)
         .style('stroke', comAttr.commonAttr.stroke)
         .style('stroke-width', comAttr.commonAttr.strokeWidth)
         .style('fill', comAttr.commonAttr.color)
         .on("click",
                 function (d){
                     self.selectShape(shapeId);
                 }
             )
         .on("contextmenu",
                 function (d){
                     //우클릭시 서브 메뉴바 띄우기 예제
                     d3.contextMenu(menu, self, shapeId);
                 }
             );
       /*.attr('class', 'circle-main ' + self.state.class + ' ' + self.state.id)*/
    }

    drag = function() {
     // What to do when mouse is dragged
     // 1. Get the new mouse position
     var m = self.mouseOffset();

     var distanceX = self.Circle.cx - m.x;
     var distanceY = self.Circle.cy - m.y;

     if( distanceX == 0 ){
         distanceX = 1;
     }

     if( distanceY == 0 ){
         distanceY = 1;
     }

     // 좌표 간의 거리를 구하는 공식 직각삼각형 빗변 계산 공식 (피타고라스 정리)
     var distance = Math.abs(Math.sqrt(Math.pow(Math.abs(distanceX),2)+Math.pow(Math.abs(distanceY),2)));
     self.Circle.distance = distance;
     // 2. Update the attributes of the Circle
     self.Circle.r.attr('cx', self.Circle.cx)
       .attr('cy', self.Circle.cy)
       .attr('r', distance)
       /*.attr('class', 'rect-main' + self.state.class + ' ' + self.state.id)*/
      .style('stroke', comAttr.commonAttr.stroke)
      .style('stroke-width', comAttr.commonAttr.strokeWidth)
      .style('fill', comAttr.commonAttr.color)

     self.Circle.cx = self.Circle.cx;
     self.Circle.cy = self.Circle.cy;
     self.Circle.distance = distance;
    }

    end = function() {
        // 1. Get the new mouse position
        var m = self.mouseOffset();
        // What to do on mouseup
        self.Shapes.shape[self.Circle.id] =  self.Circle;
        /*self.Shapes.push(self.Roundrect);*/
        comAttr.svgShapeNum.circle += 1;

        // Make it active.
        self.setActive(self.Circle.id);
    }

    return {
     start: start,
     drag: drag,
     end: end
    };
}

SVGCanvas.prototype.resizeCircle = function( param ) {
    var self = this;
    function setCoordsData(d, shapeId , selectorInfo, posit) {
        var setResultOjb = stmp.cloneDeep(self.Shapes.shape[shapeId]);
        var cx = setResultOjb.cx;
        var cy = setResultOjb.cy;

        // 정점별 계산 로직 선택
        // N으로 시작하는 애들은 삼각형의 가장 상위 좌표에 영향
        // S로 시작하는 애들은 하위 변의 좌표들에 영향
        // E를 포함하면 오른쪽 끝점, W를 포함하면 왼쪽 끝점
        if( posit === "nw" ){
            distanceX = Math.abs(cx - d.x);
            distanceY = Math.abs(cy - d.y);
         } else if ( posit === "n" ) {
             distanceX = Math.abs(cx - d.x);
             distanceY = Math.abs(cy - d.y);
         } else if ( posit === "ne" ) {
             distanceX = Math.abs(d.x+d.w-cx);
             distanceY = Math.abs(cy - d.y);
         } else if ( posit === "w" ) {
             distanceX = Math.abs(cx - d.x);
             distanceY = Math.abs(cy - d.y);
         } else if ( posit === "e" ) {
             distanceX = Math.abs(d.x+d.w-cx);
             distanceY = Math.abs(d.y+d.h-cy);
         } else if ( posit === "sw" ) {
             distanceX = Math.abs(cx - d.x);
             distanceY = Math.abs(d.y+d.h-cy);
         } else if ( posit === "s" ) {
             distanceX = Math.abs(d.x+d.w-cx);
             distanceY = Math.abs(d.y+d.h-cy);
         } else if ( posit === "se" ) {
             distanceX = Math.abs(d.x+d.w-cx);
             distanceY = Math.abs(d.y+d.h-cy);
         }

        // 좌표 간의 거리를 구하는 공식 직각삼각형 빗변 계산 공식 (피타고라스 정리)
        var distance = Math.abs(Math.sqrt(Math.pow(Math.abs(distanceX),2)+Math.pow(Math.abs(distanceY),2)));

        // Set the coordinates of shape
        var children = d3.selectAll('#' + shapeId);

        children.attr('cx', cx)
        .attr('cy', cy)
        .attr('r', distance);

        //도형 및 셀렉터 갱신을 위한 결과 오브젝트
        setResultOjb.cx = cx;
        setResultOjb.cy = cy;
        setResultOjb.distance = distance;

        return setResultOjb;
    }

    // Resize the rectangle by dragging the corners
    function getDragCorners() {
        return {
          nw    : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x + bb0.width, m.x));
            d.y = Math.max(0, Math.min(bb0.y + bb0.height, m.y));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x + bb0.width - m.x)) : d.w;
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y + bb0.height - m.y)) : d.h;
          },
          n     : function (d, bb0, m) {
            d.y = Math.max(0, Math.min(bb0.y + bb0.height, m.y));
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y + bb0.height - m.y)) : d.h;
          },
          ne    : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x, m.x));
            d.y = Math.max(0, Math.min(bb0.y + bb0.height, m.y));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x - m.x)) : d.w;
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y + bb0.height - m.y)) : d.h;
          },
          w     : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x + bb0.width, m.x));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x + bb0.width - m.x)) : d.w;
          },
          e     : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x, m.x));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x - m.x)) : d.w;
          },
          sw    : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x + bb0.width, m.x));
            d.y = Math.max(0, Math.min(bb0.y, m.y));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x + bb0.width - m.x)) : d.w;
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y - m.y)) : d.h;
          },
          s     : function (d, bb0, m) {
            d.y = Math.max(0, Math.min(bb0.y, m.y));
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y - m.y)) : d.h;
          },
          se    : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x, m.x));
            d.y = Math.max(0, Math.min(bb0.y, m.y));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x - m.x)) : d.w;
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y - m.y)) : d.h;
          }
        };
      }

      var makeContainer = function ( paramOjb, shapeId, selectorInfo ) {
          // Make a container, which depends on the corner (specified by `id`)
          var dragCorners
                   ,cursor  = null
                   ,bb0 = null
                   ,g = null
                   ,posit = paramOjb.method.position;
          var  resultOjb = {
                  shapeOjb : null,
                  reSelectorInfo : null,
          };

          var _selectorOjb = paramOjb;
          var selectorId = selectorInfo.id;
          var selectorGid = selectorInfo.gid;
          var rBB = d3.selectAll("#"+shapeId).node().getBBox();
          var sBB = d3.selectAll("#"+selectorId).node().getBBox();
          var p = {
              x: sBB.x,
              y: sBB.y,
              w: sBB.width,
              h: sBB.height,
          };
          g = p;

        // Get the correct transformation function
        dragCorners = getDragCorners()[posit];
        var start = function () {
            //도형의 현재 좌표들 가지고 있기
            bb0 = rBB;
        }

        var drag = function () {
            var m = d3Canvas.mouseOffset();
            // 각 resize포지션 별로 정해진 함수를 호출 하여 계산값 가져오기
            dragCorners(g, bb0, m);
            // 위에서 계산된 값을 가지고 도형 및 셀렉터 다시 그리고 해당 데이터 가져오기
            resultOjb.shapeOjb = setCoordsData(g, shapeId, selectorInfo, posit);
            resultOjb.reSelectorInfo = self.tempSelectorDraw(g, shapeId, selectorInfo);
        }

        var end = function () {
            //기존 셀렉터 삭제
            self.delSelection( selectorGid );
            //셀렉터 재생성
            var reSelOjb = resultOjb.reSelectorInfo;
            self.addSelector(reSelOjb.shapeId, reSelOjb.startX, reSelOjb.startY, reSelOjb.endX, reSelOjb.endY);
            //도형데이터 업데이트
            self.Shapes.shape[shapeId] =  resultOjb.shapeOjb;
            // Make it active.
            self.setActive(reSelOjb.shapeId);
        }

        // return the drag container
        return d3.drag()
          .on('start', start)
          .on('drag', drag)
          .on('end', end);
      }
      // Make drag containers for each
      return {
        makeContainer: makeContainer,
      }
}

//6.btn_icons7s-타원
SVGCanvas.prototype.makeAddEllipse = function() {
    var self = this;

    start = function() {
        // 0. Get Id
        self.Ellipse.id = idMaker(self,"ellipse");
        var shapeId = self.Ellipse.id;

         var m = self.mouseOffset();
         self.Ellipse.cx = m.x;
         self.Ellipse.cy = m.y;
         self.Ellipse.r = self.svg //self
           .append('g')
           .attr('class', 'g-ellipse ' + self.Ellipse.id)
           .append('ellipse')
           .attr('cx', self.Ellipse.cx) // Position at mouse location
           .attr('cy', self.Ellipse.cy)
           .attr('rx', 1)
           .attr('ry', 1)
           .attr('id',self.Ellipse.id)
           .style('stroke', comAttr.commonAttr.stroke)
           .style('stroke-width', comAttr.commonAttr.strokeWidth)
           .style('fill', comAttr.commonAttr.color)
           .on("click",
                   function (d){
                       self.selectShape(shapeId);
                   }
               )
           .on("contextmenu",
                   function (d){
                       //우클릭시 서브 메뉴바 띄우기 예제
                   d3.contextMenu(menu, self, shapeId);
                   }
               );
    }

    drag = function() {
         // What to do when mouse is dragged
         // 1. Get the new mouse position
         var m = self.mouseOffset();

         var distanceX = self.Ellipse.cx - m.x;
         var distanceY = self.Ellipse.cy - m.y;

         self.Ellipse.rx = Math.abs(distanceX);
         self.Ellipse.ry = Math.abs(distanceY);

         if( distanceX == 0 ){
             distanceX = 1;
         }

         if( distanceY == 0 ){
             distanceY = 1;
         }

         // 2. Update the attributes of the Ellipse
         self.Ellipse.r.attr('cx', Math.min(self.Ellipse.cx, m.x))
           .attr('cy', Math.min(self.Ellipse.cy, m.y))
           .attr('rx', Math.abs(distanceX))
           .attr('ry', Math.abs(distanceY));

         self.Ellipse.cx = Math.min(self.Ellipse.cx, m.x);
         self.Ellipse.cy = Math.min(self.Ellipse.cy, m.y);
         self.Ellipse.rx = Math.abs(distanceX);
         self.Ellipse.ry = Math.abs(distanceY);
    }

    end = function() {
        // 1. Get the new mouse position
        var m = self.mouseOffset();

        // What to do on mouseup
        self.Shapes.shape[self.Ellipse.id] =  self.Ellipse;
        comAttr.svgShapeNum.ellipse += 1;

        // Make it active.
        self.setActive(self.Ellipse.id);
    }

    return {
         start: start,
         drag: drag,
         end: end
    };
}

SVGCanvas.prototype.resizeEllipse = function( param ) {
    var self = this;
    function setCoordsData(d, shapeId , selectorInfo, posit) {
        var setResultOjb = stmp.cloneDeep(self.Shapes.shape[shapeId]);
        var cx = setResultOjb.cx;
        var cy = setResultOjb.cy;
        var rx = setResultOjb.rx;
        var ry = setResultOjb.ry;
        var roundY = cy + ry;       //원의 아랫부분 y 좌표
        var roundX = cx + rx;       //원의 오른부분 x 좌표

        // 정점별 계산 로직 선택
        // N으로 시작하는 애들은 삼각형의 가장 상위 좌표에 영향
        // S로 시작하는 애들은 하위 변의 좌표들에 영향
        // E를 포함하면 오른쪽 끝점, W를 포함하면 왼쪽 끝점
        if( posit === "nw" ){
            var distY = Math.abs(((cy+ry-d.y) / 2));
            cy = d.y + distY;
            ry  = distY;
            if( roundY == d.y ){
                distY = Math.abs(((d.y+d.h-d.y) / 2));
                cy = d.y + distY;
                ry  = distY;
            }

            var distX = Math.abs(((cx+rx-d.x) / 2));
            cx = d.x + distX;
            rx  = distX;

            if( roundX == d.x ){
                distX = Math.abs(((d.x+d.w-d.x) / 2));
                cx = d.x + distX;
                rx  = distX;
            }
        } else if ( posit === "n" ) {
            var distY = Math.abs(((cy+ry-d.y) / 2));
            cy = d.y + distY;
            ry  = distY;
            if( roundY == d.y ){
                distY = Math.abs(((d.y+d.h-d.y) / 2));
                cy = d.y + distY;
                ry  = distY;
            }
        } else if ( posit === "ne" ) {
            var distY = Math.abs(((cy+ry-d.y) / 2));
            cy = d.y + distY;
            ry  = distY;
            if( roundY == d.y ){
                distY = Math.abs(((d.y+d.h-d.y) / 2));
                cy = d.y + distY;
                ry  = distY;
            }

            var distX = Math.abs(((d.x+d.w-d.x) / 2));
            cx = d.x + distX;
            rx  = distX;
        } else if ( posit === "w" ) {
            var distX = Math.abs(((cx+rx-d.x) / 2));
            cx = d.x + distX;
            rx  = distX;

            if( roundX == d.x ){
                distX = Math.abs(((d.x+d.w-d.x) / 2));
                cx = d.x + distX;
                rx  = distX;
            }
        } else if ( posit === "e" ) {
            var distX = Math.abs(((d.x+d.w-d.x) / 2));
            cx = d.x + distX;
            rx  = distX;
        } else if ( posit === "sw" ) {
            var distY = Math.abs(((d.y+d.h-d.y) / 2));
            cy = d.y + distY;
            ry  = distY;

            var distX = Math.abs(((cx+rx-d.x) / 2));
            cx = d.x + distX;
            rx  = distX;

            if( roundX == d.x ){
                distX = Math.abs(((d.x+d.w-d.x) / 2));
                cx = d.x + distX;
                rx  = distX;
            }
        } else if ( posit === "s" ) {
            var distY = Math.abs(((d.y+d.h-d.y) / 2));
            cy = d.y + distY;
            ry  = distY;
        } else if ( posit === "se" ) {
            var distY = Math.abs(((d.y+d.h-d.y) / 2));
            cy = d.y + distY;
            ry  = distY;

            var distX = Math.abs(((d.x+d.w-d.x) / 2));
            cx = d.x + distX;
            rx  = distX;
        }

        // Set the coordinates of shape
        var children = d3.selectAll('#' + shapeId);

        //Update the attributes of the Ellipse
        children.attr('cx',cx)
        .attr('cy', cy)
        .attr('rx', rx)
        .attr('ry', ry);

        //도형 및 셀렉터 갱신을 위한 결과 오브젝트
        setResultOjb.cx = cx;
        setResultOjb.cy = cy;
        setResultOjb.rx = rx;
        setResultOjb.ry = ry;

        return setResultOjb;
    }

    // Resize the rectangle by dragging the corners
    function getDragCorners() {
        return {
          nw    : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x + bb0.width, m.x));
            d.y = Math.max(0, Math.min(bb0.y + bb0.height, m.y));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x + bb0.width - m.x)) : d.w;
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y + bb0.height - m.y)) : d.h;
          },
          n     : function (d, bb0, m) {
            d.y = Math.max(0, Math.min(bb0.y + bb0.height, m.y));
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y + bb0.height - m.y)) : d.h;
          },
          ne    : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x, m.x));
            d.y = Math.max(0, Math.min(bb0.y + bb0.height, m.y));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x - m.x)) : d.w;
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y + bb0.height - m.y)) : d.h;
          },
          w     : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x + bb0.width, m.x));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x + bb0.width - m.x)) : d.w;
          },
          e     : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x, m.x));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x - m.x)) : d.w;
          },
          sw    : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x + bb0.width, m.x));
            d.y = Math.max(0, Math.min(bb0.y, m.y));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x + bb0.width - m.x)) : d.w;
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y - m.y)) : d.h;
          },
          s     : function (d, bb0, m) {
            d.y = Math.max(0, Math.min(bb0.y, m.y));
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y - m.y)) : d.h;
          },
          se    : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x, m.x));
            d.y = Math.max(0, Math.min(bb0.y, m.y));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x - m.x)) : d.w;
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y - m.y)) : d.h;
          }
        };
      }

      var makeContainer = function ( paramOjb, shapeId, selectorInfo ) {
          // Make a container, which depends on the corner (specified by `id`)
          var dragCorners
                   ,cursor  = null
                   ,bb0 = null
                   ,g = null
                   ,posit = paramOjb.method.position;
          var  resultOjb = {
                  shapeOjb : null,
                  reSelectorInfo : null,
          };

          var _selectorOjb = paramOjb;
          var selectorId = selectorInfo.id;
          var selectorGid = selectorInfo.gid;
          var rBB = d3.selectAll("#"+shapeId).node().getBBox();
          var sBB = d3.selectAll("#"+selectorId).node().getBBox();
          var p = {
              x: sBB.x,
              y: sBB.y,
              w: sBB.width,
              h: sBB.height,
          };
          g = p;

        // Get the correct transformation function
        dragCorners = getDragCorners()[posit];
        var start = function () {
            //도형의 현재 좌표들 가지고 있기
            bb0 = rBB;
        }

        var drag = function () {
            var m = d3Canvas.mouseOffset();
            // 각 resize포지션 별로 정해진 함수를 호출 하여 계산값 가져오기
            dragCorners(g, bb0, m);
            // 위에서 계산된 값을 가지고 도형 및 셀렉터 다시 그리고 해당 데이터 가져오기
            resultOjb.shapeOjb = setCoordsData(g, shapeId, selectorInfo, posit);
            resultOjb.reSelectorInfo = self.tempSelectorDraw(g, shapeId, selectorInfo);
        }

        var end = function () {
            //기존 셀렉터 삭제
            self.delSelection( selectorGid );
            //셀렉터 재생성
            var reSelOjb = resultOjb.reSelectorInfo;
            self.addSelector(reSelOjb.shapeId, reSelOjb.startX, reSelOjb.startY, reSelOjb.endX, reSelOjb.endY);
            //도형데이터 업데이트
            self.Shapes.shape[shapeId] =  resultOjb.shapeOjb;
            // Make it active.
            self.setActive(reSelOjb.shapeId);
        }

        // return the drag container
        return d3.drag()
          .on('start', start)
          .on('drag', drag)
          .on('end', end);
      }
      // Make drag containers for each
      return {
        makeContainer: makeContainer,
      }
}

//7.btn_icons8s-호
SVGCanvas.prototype.makeAddArc = function() {
  var self = this;

  start = function() {
      // 0. Get Id
      self.Arc.id = idMaker(self,"arc");
      var shapeId = self.Arc.id;

       var m = self.mouseOffset();
       self.Arc.x1 = m.x;
       self.Arc.y1 = m.y;
       self.Arc.r = self.svg //self.
         .append('g')
         .attr('class', 'g-arc ' + self.Arc.id)
         .append('path')
         .attr('id',self.Arc.id);
  }

  drag = function() {
   // What to do when mouse is dragged
   // 1. Get the new mouse position
   var m = self.mouseOffset();

   self.Arc.x1 = self.Arc.x1;
   self.Arc.y1 = self.Arc.y1;
   self.Arc.x2 = m.x;
   self.Arc.y2 = m.y;

   makeD = "M "+self.Arc.x1+" "+self.Arc.y1+" "+ self.Arc.x2 + " "+self.Arc.y2;
   self.Arc.d = makeD;

   // 2. Update the attributes of the Ellipse
       self.Arc.r.attr('d', makeD)
       .style('stroke', comAttr.commonAttr.stroke)
       .style('stroke-width', comAttr.commonAttr.strokeWidth)
       .style('fill', comAttr.commonAttr.color)
  }

  end = function() {
      makeAngle(self, "a",  self.Arc.x1,  self.Arc.y1,  self.Arc.x2,  self.Arc.y2 );

      self.Shapes.shape[self.Arc.id] =  self.Arc;
      comAttr.svgShapeNum.arc += 1;
  }

  return {
   start: start,
   drag: drag,
   end: end
  };
}

// 호 그릴때 각도 조절  (각도를 만들 제 3의 점의 좌표를 선택시 곡선 그리기)
function makeAngle(self, id, a, b, c, d){
    self.svg.call(
        d3.drag()
        /*.on('start', function(){
            var m = self.mouseOffset();

           var makeD = "M "+a+" "+b+" Q "+m.x+" "+m.y+" "+c+ " "+d;
              self.Arc.r.attr('d', makeD)
              .attr('class', 'rect-main' + self.state.class + ' ' + self.state.id)
              .style('stroke', self.state.color)
              .style('fill', 'none');
        })*/
        .on('drag', function(){
            var m = self.mouseOffset();
            var makeD = "M "+a+" "+b+" Q "+m.x+" "+m.y+" "+c+ " "+d;
            self.Arc.r.attr('d', makeD)
            .style('stroke', comAttr.commonAttr.stroke)
            .style('stroke-width', comAttr.commonAttr.strokeWidth)
            .style('fill', comAttr.commonAttr.color)
        })
        .on('end', function(){
            self.Shapes.push(self.Arc);
        })
    )
    return;
}

//8.btn_icons9s-파이
SVGCanvas.prototype.makeAddPie = function() {
    // Methods for adding Line to the svg.
    var self = this;
    var point1 = null;
    var point2 = null;
    var point3 = null;
    var point4 = null;

    start = function() {
        // 0. Get Id
        self.Pie.id = idMaker(self,"pie");
        var shapeId = self.Pie.id;
       // 1. Get mouse location in SVG
       var m = self.mouseOffset();
       self.Pie.x1 = m.x;
       self.Pie.y1 = m.y;

       // 2. Make a rectangle
       self.Pie.r = self.svg //self.zoomG
         .append('g')
         .attr('class', 'g-pie ' + self.Pie.id)
         .append('path') // An SVG element
         .attr('id',self.Pie.id)
        .style('stroke', comAttr.commonAttr.stroke)
        .style('stroke-width', comAttr.commonAttr.strokeWidth)
        .style('fill', comAttr.commonAttr.color)
        .on("click",
                function (d){
                    self.selectShape(shapeId);
                }
            )
        .on("contextmenu",
                function (d){
                    //우클릭시 서브 메뉴바 띄우기 예제
                d3.contextMenu(menu, self, shapeId);
                }
            );
     }

    drag = function() {
           // What to do when mouse is dragged
           // 1. Get the new mouse position
           var m = self.mouseOffset();

           var distanceX = Math.abs(self.Pie.x1 - m.x);
           var distanceY = Math.abs(self.Pie.y1 - m.y);
           /*사용 좌표 (x1, y1) 중심좌표 */
           var x1  = self.Pie.x1;
           var y1  = self.Pie.y1;
           var x0  = x1 - Math.abs(distanceX);
           var x2  = x1 + Math.abs(distanceX);
           var y0  = y1 - Math.abs(distanceY);
           var y2  = y1 + Math.abs(distanceY);

           var top     = x1 + " " + y0;
           var middle  = x1 + " " + y1;
           var bottom  = x1 + " " + y2;
           var left    = x0 + " " + y1;
           var right   = x2 + " " + y1;

           // 2. Update the attributes of the Line
           var makeD = "M "+left+
           " C "+left+" "+x0+" "+y0+" "+ top+
           " C "+top+" "+x2+" "+y0+" "+ right+
           " L "+right+" "+ bottom+
           " Z ";
           self.Pie.d = makeD;
           self.Pie.r.attr('d', makeD);

           self.Pie.top        =    top;
           self.Pie.middle  =   middle;
           self.Pie.bottom  =   bottom;
           self.Pie.left         =   left;
           self.Pie.right      =   right;
           self.Pie.d           =   makeD;
           self.Pie.cx      =   x1;
           self.Pie.cy      =   y1;
     }

    end = function() {
        // 1. Get the new mouse position
        var m = self.mouseOffset();
        // What to do on mouseup
        self.Shapes.shape[self.Pie.id] =  self.Pie;
        comAttr.svgShapeNum.pie += 1;

        // Make it active.
        self.setActive(self.Pie.id);
    }

    return {
       start: start,
       drag: drag,
       end: end
    };
}

SVGCanvas.prototype.resizePie = function( param ) {
    var self = this;
    function setCoordsData(d, shapeId , selectorInfo, posit) {
        var setResultOjb = stmp.cloneDeep(self.Shapes.shape[shapeId]);

        /*중심좌표*/
        var cx = setResultOjb.middle.split(' ')[0];
        var cy = setResultOjb.middle.split(' ')[1];
        var distanceX = null;
        var distanceY =  null;

        // 정점별 계산 로직 선택
        // N으로 시작하는 애들은 삼각형의 가장 상위 좌표에 영향
        // S로 시작하는 애들은 하위 변의 좌표들에 영향
        // E를 포함하면 오른쪽 끝점, W를 포함하면 왼쪽 끝점
       if( posit === "nw" ){
           distanceX = Math.abs(cx - d.x);
           distanceY = Math.abs(cy - d.y);
        } else if ( posit === "n" ) {
            distanceX = Math.abs(cx - d.x);
            distanceY = Math.abs(cy - d.y);
        } else if ( posit === "ne" ) {
            distanceX = Math.abs(d.x+d.w-cx);
            distanceY = Math.abs(cy - d.y);
        } else if ( posit === "w" ) {
            distanceX = Math.abs(cx - d.x);
            distanceY = Math.abs(cy - d.y);
        } else if ( posit === "e" ) {
            distanceX = Math.abs(d.x+d.w-cx);
            distanceY = Math.abs(d.y+d.h-cy);
        } else if ( posit === "sw" ) {
            distanceX = Math.abs(cx - d.x);
            distanceY = Math.abs(d.y+d.h-cy);
        } else if ( posit === "s" ) {
            distanceX = Math.abs(d.x+d.w-cx);
            distanceY = Math.abs(d.y+d.h-cy);
        } else if ( posit === "se" ) {
            distanceX = Math.abs(d.x+d.w-cx);
            distanceY = Math.abs(d.y+d.h-cy);
        }

        // 2. Update the attributes of the shape
       var x1  = parseInt(cx);
       var y1  = parseInt(cy);
       var x0  = x1 - Math.abs(distanceX);
       var x2  = x1 + Math.abs(distanceX);
       var y0  = y1 - Math.abs(distanceY);
       var y2  = y1 + Math.abs(distanceY);

       var top     = x1 + " " + y0;
       var middle  = x1 + " " + y1;
       var bottom  = x1 + " " + y2;
       var left    = x0 + " " + y1;
       var right   = x2 + " " + y1;

       var makeD = "M "+left+
       " C "+left+" "+x0+" "+y0+" "+ top+
       " C "+top+" "+x2+" "+y0+" "+ right+
       " L "+right+" "+ bottom+
       " Z ";

        // Set the coordinates of shape
        var children = d3.selectAll('#' + shapeId);

        children.attr('d', makeD);

        //도형 및 셀렉터 갱신을 위한 결과 오브젝트
        var middleP  = cx +" " + cy;
        setResultOjb.top       = top;
        setResultOjb.middle = middle;
        setResultOjb.bottom = bottom;
        setResultOjb.left        = left;
        setResultOjb.right     = right;
        setResultOjb.d = makeD;

        return setResultOjb;
    }

    // Resize the rectangle by dragging the corners
    function getDragCorners() {
        return {
          nw    : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x + bb0.width, m.x));
            d.y = Math.max(0, Math.min(bb0.y + bb0.height, m.y));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x + bb0.width - m.x)) : d.w;
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y + bb0.height - m.y)) : d.h;
          },
          n     : function (d, bb0, m) {
            d.y = Math.max(0, Math.min(bb0.y + bb0.height, m.y));
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y + bb0.height - m.y)) : d.h;
          },
          ne    : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x, m.x));
            d.y = Math.max(0, Math.min(bb0.y + bb0.height, m.y));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x - m.x)) : d.w;
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y + bb0.height - m.y)) : d.h;
          },
          w     : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x + bb0.width, m.x));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x + bb0.width - m.x)) : d.w;
          },
          e     : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x, m.x));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x - m.x)) : d.w;
          },
          sw    : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x + bb0.width, m.x));
            d.y = Math.max(0, Math.min(bb0.y, m.y));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x + bb0.width - m.x)) : d.w;
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y - m.y)) : d.h;
          },
          s     : function (d, bb0, m) {
            d.y = Math.max(0, Math.min(bb0.y, m.y));
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y - m.y)) : d.h;
          },
          se    : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x, m.x));
            d.y = Math.max(0, Math.min(bb0.y, m.y));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x - m.x)) : d.w;
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y - m.y)) : d.h;
          }
        };
      }

      var makeContainer = function ( paramOjb, shapeId, selectorInfo ) {
          // Make a container, which depends on the corner (specified by `id`)
          var dragCorners
                   ,cursor  = null
                   ,bb0 = null
                   ,g = null
                   ,posit = paramOjb.method.position;
          var  resultOjb = {
                  shapeOjb : null,
                  reSelectorInfo : null,
          };

          var _selectorOjb = paramOjb;
          var selectorId = selectorInfo.id;
          var selectorGid = selectorInfo.gid;
          var rBB = d3.selectAll("#"+shapeId).node().getBBox();
          var sBB = d3.selectAll("#"+selectorId).node().getBBox();
          var p = {
              x: sBB.x,
              y: sBB.y,
              w: sBB.width,
              h: sBB.height,
          };
          g = p;

        // Get the correct transformation function
        dragCorners = getDragCorners()[posit];
        var start = function () {
            //도형의 현재 좌표들 가지고 있기
            bb0 = rBB;
        }

        var drag = function () {
            var m = d3Canvas.mouseOffset();
            // 각 resize포지션 별로 정해진 함수를 호출 하여 계산값 가져오기
            dragCorners(g, bb0, m);
            // 위에서 계산된 값을 가지고 도형 및 셀렉터 다시 그리고 해당 데이터 가져오기
            resultOjb.shapeOjb = setCoordsData(g, shapeId, selectorInfo, posit);
            resultOjb.reSelectorInfo = self.tempSelectorDraw(g, shapeId, selectorInfo);
        }

        var end = function () {
            //기존 셀렉터 삭제
            self.delSelection( selectorGid );
            //셀렉터 재생성
            var reSelOjb = resultOjb.reSelectorInfo;
            self.addSelector(reSelOjb.shapeId, reSelOjb.startX, reSelOjb.startY, reSelOjb.endX, reSelOjb.endY);
            //도형데이터 업데이트
            self.Shapes.shape[shapeId] =  resultOjb.shapeOjb;
            // Make it active.
            self.setActive(reSelOjb.shapeId);
        }

        // return the drag container
        return d3.drag()
          .on('start', start)
          .on('drag', drag)
          .on('end', end);
      }
      // Make drag containers for each
      return {
        makeContainer: makeContainer,
      }
}

//9.btn_icons10s-육각형
SVGCanvas.prototype.makeAddHexagon = function() {
    var self = this;

    start = function() {
            // 0. Get Id
            self.Hexagon.id = idMaker(self,"hexagon");
            var shapeId = self.Hexagon.id;
           // 1. Get mouse location in SVG
           var m = self.mouseOffset();
           self.Hexagon.cx = m.x;
           self.Hexagon.cy = m.y;
           // 2. Make a rectangle
           self.Hexagon.r = self.svg //self.zoomG
             .append('g')
             .attr('class', 'g-hexagon ' + self.Hexagon.id)
             .append('polygon') // An SVG element
             .attr('id',self.Hexagon.id)
             .style('stroke', comAttr.commonAttr.stroke)
             .style('stroke-width', comAttr.commonAttr.strokeWidth)
             .style('fill', comAttr.commonAttr.color)
             .on("click",
                     function (d){
                         self.selectShape(shapeId);
                     }
                 )
             .on("contextmenu",
                     function (d){
                         //우클릭시 서브 메뉴바 띄우기 예제
                     d3.contextMenu(menu, self, shapeId);
                     }
                 );
     }

    drag = function() {
           // What to do when mouse is dragged
           // 1. Get the new mouse position
           var m = self.mouseOffset();

           var distanceX = Math.abs(self.Hexagon.cx - m.x);
           var distanceY = Math.abs(self.Hexagon.cy - m.y);
           // 좌표 간의 거리를 구하는 공식 직각삼각형 빗변 계산 공식 (피타고라스 정리)
           var cr = Math.abs(Math.sqrt(Math.pow(Math.abs(distanceX),2)+Math.pow(Math.abs(distanceY),2)));
           var startX = Math.sqrt((2 * cr * cr),2);
           var disX = startX / 2

           /*사용 좌표 중심좌표 */
           var x0  = self.Hexagon.cx - startX ;
           var x1  = x0 + disX;
           var x2  = x1 + disX;
           var x3  = x2 + disX;
           var y0  = self.Hexagon.cy - distanceY;
           var y1  = self.Hexagon.cy;
           var y2  = y1+distanceY;

           var top1     = x1 + "," + y2 + " ";
           var top2     = x2 + "," + y2 + " ";
           var bottom1  = x1 + "," + y0 + " ";
           var bottom2  = x2 + "," + y0 + " ";
           var left     = x0 + "," + y1 + " ";
           var right    = x3 + "," + y1 + " ";

           // 2. Update the attributes of the Points
           var makeP = left + top1 + top2 + right + bottom2 + bottom1;
           self.Hexagon.r.attr('points', makeP);

           self.Hexagon.points   = makeP;
           self.Hexagon.cx      = self.Hexagon.cx;
           self.Hexagon.cy      = self.Hexagon.cy;
           self.Hexagon.top2      = top2;
           self.Hexagon.top1      = top1;
           self.Hexagon.bottom1  = bottom1;
           self.Hexagon.bottom2  = bottom2;
           self.Hexagon.left         = left;
           self.Hexagon.right      = right;
     }

    end = function() {
            // 1. Get the new mouse position
            var m = self.mouseOffset();

            // What to do on mouseup
            self.Shapes.shape[self.Hexagon.id] =  self.Hexagon;
            comAttr.svgShapeNum.hexagon += 1;

            // Make it active.
            self.setActive(self.Hexagon.id);
    }

    return {
           start: start,
           drag: drag,
           end: end
    };
}


//10.btn_icons11s-점 생성
SVGCanvas.prototype.makeAddPoint = function() {
      var self = this;
      const deSize = 10;       //포인트의 기본 사이즈가 5 이기 떄문에 그 절반

      function start(){
          // 0. Get Id
          self.Point.id = idMaker(self,"point");
          var shapeId = self.Point.id;
          // 1. Get mouse location in SVG
          var m = self.mouseOffset();
          // 중심 좌표
          var cx = m.x;
          var cy = m.y;

          // 처음 생성시 기본 모양은 사각형
          // 점 부호는 사이즈 기본값이 5px 이므로 중심점을 기준으로 -2.5 씩 한 값이 시작점
          var pointA = (cx-deSize) + "," + (cy-deSize) + " ";
          var pointB = (cx+deSize) + "," + (cy-deSize) + " ";
          var pointC = (cx+deSize) + "," + (cy+deSize) + " ";
          var pointD = (cx-deSize) + "," + (cy+deSize) + " ";

          var points = pointA + pointB + pointC + pointD;
          // 2. Make a rectangle
          self.Point.r = self.svg //self.zoomG
              .append('g')
              .attr('class', 'g-point ' + self.Point.id)
              .append('polygon') // An SVG element
              .attr('id',shapeId)
              .attr('points', points)
              //.style('stroke', comAttr.commonAttr.stroke)
              //.style('stroke-width', comAttr.commonAttr.strokeWidth)
              .style('fill', "black")
              .on("click",
                  function (d){
                      self.selectShape(shapeId);
                  }
              )
              .on("contextmenu",
                  function (d){
                      //우클릭시 서브 메뉴바 띄우기 예제
                      d3.contextMenu(menu, self, shapeId);
                  }
              );
      }

      function drag(){
          // 1. Get mouse location in SVG
          var m = self.mouseOffset();
          // 중심 좌표
          var cx = m.x;
          var cy = m.y;
          var pointA = (cx-deSize) + "," + (cy-deSize) + " ";
          var pointB = (cx+deSize) + "," + (cy-deSize) + " ";
          var pointC = (cx+deSize) + "," + (cy+deSize) + " ";
          var pointD = (cx-deSize) + "," + (cy+deSize) + " ";

          var points = pointA + pointB + pointC + pointD;
          self.Point.r.attr('points',points);
      }

      function end(){
          // 1. Get the new mouse position
          var m = self.mouseOffset();

          // What to do on mouseup
          self.Shapes.shape[self.Point.id] =  self.Point;
          comAttr.svgShapeNum.point += 1;

          // Make it active.
          self.setActive(self.Point.id);
      }

      return {
          start: start,
          drag: drag,
          end: end
      };
}


SVGCanvas.prototype.resizeHexagon = function( param ) {
    var self = this;
    function setCoordsData(d, shapeId , selectorInfo, posit) {
        var setResultOjb = stmp.cloneDeep(self.Shapes.shape[shapeId]);

        /*중심좌표*/
        var cx = setResultOjb.cx;
        var cy = setResultOjb.cy;

        // 정점별 계산 로직 선택
        // N으로 시작하는 애들은 삼각형의 가장 상위 좌표에 영향
        // S로 시작하는 애들은 하위 변의 좌표들에 영향
        // E를 포함하면 오른쪽 끝점, W를 포함하면 왼쪽 끝점
       if( posit === "nw" ){
           distanceX = Math.abs(cx - d.x);
           distanceY = Math.abs(cy - d.y);
        } else if ( posit === "n" ) {
            distanceX = Math.abs(cx - d.x);
            distanceY = Math.abs(cy - d.y);
        } else if ( posit === "ne" ) {
            distanceX = Math.abs(d.x+d.w-cx);
            distanceY = Math.abs(cy - d.y);
        } else if ( posit === "w" ) {
            distanceX = Math.abs(cx - d.x);
            distanceY = Math.abs(cy - d.y);
        } else if ( posit === "e" ) {
            distanceX = Math.abs(d.x+d.w-cx);
            distanceY = Math.abs(d.y+d.h-cy);
        } else if ( posit === "sw" ) {
            distanceX = Math.abs(cx - d.x);
            distanceY = Math.abs(d.y+d.h-cy);
        } else if ( posit === "s" ) {
            distanceX = Math.abs(d.x+d.w-cx);
            distanceY = Math.abs(d.y+d.h-cy);
        } else if ( posit === "se" ) {
            distanceX = Math.abs(d.x+d.w-cx);
            distanceY = Math.abs(d.y+d.h-cy);
        }

        // 2. Update the attributes of the shape
       // 좌표 간의 거리를 구하는 공식 직각삼각형 빗변 계산 공식 (피타고라스 정리)
       var cr = Math.abs(Math.sqrt(Math.pow(Math.abs(distanceX),2)+Math.pow(Math.abs(distanceY),2)));
       var startX = Math.sqrt((2 * cr * cr),2);
       var disX = startX / 2

       /*사용 좌표 중심좌표 */
       var x0  = cx - startX ;
       var x1  = x0 + disX;
       var x2  = x1 + disX;
       var x3  = x2 + disX;
       var y0  = cy - distanceY;
       var y1  = cy;
       var y2  = y1+distanceY;

       var top1     = x1 + "," + y2 + " ";
       var top2     = x2 + "," + y2 + " ";
       var bottom1  = x1 + "," + y0 + " ";
       var bottom2  = x2 + "," + y0 + " ";
       var left     = x0 + "," + y1 + " ";
       var right    = x3 + "," + y1 + " ";

       // 2. Update the attributes of the Points
       var makeP = left + top1 + top2 + right + bottom2 + bottom1;
        // Set the coordinates of shape
        var children = d3.selectAll('#' + shapeId);

        children.attr('points', makeP);

        //도형 및 셀렉터 갱신을 위한 결과 오브젝트
        setResultOjb.points   = makeP;
        setResultOjb.cx      = cx;
        setResultOjb.cy      = cy;
        setResultOjb.top2      = top2;
        setResultOjb.top1      = top1;
        setResultOjb.bottom1  = bottom1;
        setResultOjb.bottom2  = bottom2;
        setResultOjb.left         = left;
        setResultOjb.right      = right;

        return setResultOjb;
    }

    // Resize the rectangle by dragging the corners
    function getDragCorners() {
        return {
          nw    : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x + bb0.width, m.x));
            d.y = Math.max(0, Math.min(bb0.y + bb0.height, m.y));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x + bb0.width - m.x)) : d.w;
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y + bb0.height - m.y)) : d.h;
          },
          n     : function (d, bb0, m) {
            d.y = Math.max(0, Math.min(bb0.y + bb0.height, m.y));
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y + bb0.height - m.y)) : d.h;
          },
          ne    : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x, m.x));
            d.y = Math.max(0, Math.min(bb0.y + bb0.height, m.y));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x - m.x)) : d.w;
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y + bb0.height - m.y)) : d.h;
          },
          w     : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x + bb0.width, m.x));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x + bb0.width - m.x)) : d.w;
          },
          e     : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x, m.x));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x - m.x)) : d.w;
          },
          sw    : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x + bb0.width, m.x));
            d.y = Math.max(0, Math.min(bb0.y, m.y));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x + bb0.width - m.x)) : d.w;
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y - m.y)) : d.h;
          },
          s     : function (d, bb0, m) {
            d.y = Math.max(0, Math.min(bb0.y, m.y));
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y - m.y)) : d.h;
          },
          se    : function (d, bb0, m) {
            d.x = Math.max(0, Math.min(bb0.x, m.x));
            d.y = Math.max(0, Math.min(bb0.y, m.y));
            d.w = (m.x > 0) ? Math.min(Math.abs(self.options.w - d.x), Math.abs(bb0.x - m.x)) : d.w;
            d.h = (m.y > 0) ? Math.min(Math.abs(self.options.h - d.y), Math.abs(bb0.y - m.y)) : d.h;
          }
        };
      }

      var makeContainer = function ( paramOjb, shapeId, selectorInfo ) {
          // Make a container, which depends on the corner (specified by `id`)
          var dragCorners
                   ,cursor  = null
                   ,bb0 = null
                   ,g = null
                   ,posit = paramOjb.method.position;
          var  resultOjb = {
                  shapeOjb : null,
                  reSelectorInfo : null,
          };

          var _selectorOjb = paramOjb;
          var selectorId = selectorInfo.id;
          var selectorGid = selectorInfo.gid;
          var rBB = d3.selectAll("#"+shapeId).node().getBBox();
          var sBB = d3.selectAll("#"+selectorId).node().getBBox();
          var p = {
              x: sBB.x,
              y: sBB.y,
              w: sBB.width,
              h: sBB.height,
          };
          g = p;

        // Get the correct transformation function
        dragCorners = getDragCorners()[posit];
        var start = function () {
            //도형의 현재 좌표들 가지고 있기
            bb0 = rBB;
        }

        var drag = function () {
            var m = d3Canvas.mouseOffset();
            // 각 resize포지션 별로 정해진 함수를 호출 하여 계산값 가져오기
            dragCorners(g, bb0, m);
            // 위에서 계산된 값을 가지고 도형 및 셀렉터 다시 그리고 해당 데이터 가져오기
            resultOjb.shapeOjb = setCoordsData(g, shapeId, selectorInfo, posit);
            resultOjb.reSelectorInfo = self.tempSelectorDraw(g, shapeId, selectorInfo);
        }

        var end = function () {
            //기존 셀렉터 삭제
            self.delSelection( selectorGid );
            //셀렉터 재생성
            var reSelOjb = resultOjb.reSelectorInfo;
            self.addSelector(reSelOjb.shapeId, reSelOjb.startX, reSelOjb.startY, reSelOjb.endX, reSelOjb.endY);
            //도형데이터 업데이트
            self.Shapes.shape[shapeId] =  resultOjb.shapeOjb;
            // Make it active.
            self.setActive(reSelOjb.shapeId);
        }

        // return the drag container
        return d3.drag()
          .on('start', start)
          .on('drag', drag)
          .on('end', end);
      }
      // Make drag containers for each
      return {
        makeContainer: makeContainer,
      }
}

//10.btn_icons11s-Text
SVGCanvas.prototype.makeAddText = function() {
  var self = this;

  start = function() {
      var m = self.mouseOffset();
      var x = m.x;
      var y = m.y;
      var id = idMaker(self,"text");
      var inputbox ="<input type='text' id='"+id+"' style='width: 100px; height: 20px;'>";
/*
      var inputbox = "<foreignObject x='"+x+"' y='"+y+"' width='100' height='20'" +
          " transform='translate(0,0)' style='overflow: visible;'>" +
          "<input type='text' id='"+id+"' style='width: 100px; height: 20px;'></foreignObject>";*/
      self.Text.r= self.svg
      .append("g")
      .append("foreignObject")
      .attr("x",x)
      .attr("y",y)
      .attr("width","100px")
      .attr("height","20px")
      .style("overflow","visible")
      .html(inputbox);

      focus(id);
 }

  focus = function( objId ){
      document.getElementById(objId).focus();
  }

  return {
      start: start
  };
}

//20.btn_icons24s-이미지
SVGCanvas.prototype.makeAddImage = function() {
    var self = this;

    start = function() {
         var m = self.mouseOffset();
         self.Image.r = self.svg //self.
           .append('g')
           .append('image')
           .attr('x', m.x) // Position at mouse location
           .attr('y', m.y)
           .text("image")
           .attr('xlink:href',self.Image.href);
    }

    drag = function() {
     // What to do when mouse is dragged
     // 1. Get the new mouse position
     var m = self.mouseOffset();


     // 2. Update the attributes of the Ellipse
     self.Image.r.attr('x', m.x)
       .attr('y', m.y)
       .style('stroke', comAttr.commonAttr.stroke)
       .style('stroke-width', comAttr.commonAttr.strokeWidth)
       .style('fill', comAttr.commonAttr.color);
    }

    end = function() {
         // What to do on mouseup
         self.Shapes.push(self.Image);
    }

    return {
         start: start,
         drag: drag,
         end: end
    };
}

//@@@ 미니 맵용 영역 그리기(rect)
SVGCanvas.prototype.makeMiniMapRect = function() {
 // Methods for adding rectangles to the svg.
 var self = this;
 var x0, y0;

 start = function() {
   self.svg.selectAll('g').remove();
   //Add a rectangle
   // 1. Get mouse location in SVG
   var m = self.mouseOffset();
   self.mapRect.x0 = m.x;
   self.mapRect.y0 = m.y;

   // 3. Make a rectangle
   self.mapRect.r = self.svg //self.
     .append('g')
     .append('rect') // An SVG element
     .attr('x', self.mapRect.x0) // Position at mouse location
     .attr('y', self.mapRect.y0)
     .attr('width', 1) // Make it tiny
     .attr('height', 1)
     .style('stroke', "black")
     .style('stroke-width', "2")
     .style('fill', "none");
 }

 drag = function() {
   var m = self.mouseOffset();
   // 2. Update the attributes of the rectangle
   self.mapRect.r.attr('x', Math.min(self.mapRect.x0, m.x))
     .attr('y', Math.min(self.mapRect.y0, m.y))
     .attr('width', Math.abs(self.mapRect.x0 - m.x))
     .attr('height', Math.abs(self.mapRect.y0 - m.y));
 }

 end = function() {
     // What to do on mouseup
     self.Shapes.push(self.mapRect);
 }

 return {
   start: start,
   drag: drag,
   end: end
 };
}

//임시 삭제예정
SVGCanvas.prototype.selectorMode = function() {
    // Methods for adding rectangles to the svg.
    var self = this;
    var x0, y0;

    this.start = function() {
        var m = self.mouseOffset();
    };

    this.drag = function() {
        var m = self.mouseOffset();
    };

    this.end = function() {
    };

    return {
     start: this.start,
     drag: this.drag,
     end: this.end
    };
}

// SVGCanvas에 작도 메소드 연결(그리기 타입별)
function SelectedShape(canvasId, shape, imageUrl) {
    //Select mode
    if(shape=="select"){
        var curMod = canvasId.svg.selectAll(".active").node();
        if( curMod === null ){
            canvasId.selector = canvasId.selectorMode();
        } else {
            canvasId.selector = canvasId.selectorMover();
        }

        //리스너 초기화
        canvasId.svg.call(d3.drag().on('start',canvasId.selector.start).on('drag',canvasId.selector.drag).on('end',canvasId.selector.end));
    } else {
        //active 모드 모두 초기화(작도 모드)
        canvasId.svg.selectAll('g.active').classed('active', false);
    }

    if(shape=="line"){
        // Actions/Listeners
        canvasId.addLine = canvasId.makeAddLine(); // Methods for adding Lines

        // Line 도형 요소 삽입
        canvasId.Line = { // Current Selection
                'r': null,
                'g': null,
                'x1': null,
                'x2': null,
                'y1': null,
                'y2': null,
                'id': null,
                'geo': null,
                'attr': null,
                'cx': null,
                'cy': null
          };

        canvasId.svg.call(d3.drag().on('start',canvasId.addLine.start).on('drag',canvasId.addLine.drag).on('end',canvasId.addLine.end));
    }

    if(shape=="rect"){
        // Rectangles 도형 요소 삽입
        canvasId.Rect = { // Current Selection
          'r': null,
          'g': null,
          'x': null,
          'y': null,
          'width': null,
          'height': null,
          'rx': null,
          'ry': null,
          'id': null,
          'geo': null,
          'attr': null,
          'cx': null,
          'cy': null
        };

        // Actions/Listeners
        canvasId.addRect = canvasId.makeAddRect(); // Methods for adding rectangles
        //canvasId.makeDragBehavior

        canvasId.svg.call(d3.drag().on('start',canvasId.addRect.start).on('drag',canvasId.addRect.drag).on('end',canvasId.addRect.end));
    }

    if(shape=="roundrect"){
        // Actions/Listeners
        canvasId.addRoundrect = canvasId.makeAddRoundrect(); // Methods for adding rectangles

        // Rectangles 도형 요소 삽입
        canvasId.Rect = { // Current Selection
          'r': null,
          'g': null,
          'x': null,
          'y': null,
          'width': null,
          'height': null,
          'rx': null,
          'ry': null,
          'id': null,
          'geo': null,
          'attr': null,
          'cx': null,
          'cy': null
        };

        canvasId.svg.call(d3.drag().on('start',canvasId.addRoundrect.start).on('drag',canvasId.addRoundrect.drag).on('end',canvasId.addRoundrect.end));
    }

    if(shape=="triangle"){
        // Actions/Listeners
        canvasId.addTriangle = canvasId.makeAddTriangle();

        // Triangles 도형 요소 삽입
        canvasId.Triangle = { // Current Selection
          'points': null,
          'g': null,
          'r': null,
          'cx': null,
          'cy': null,
          // 셀렉터를 기준으로 좌표값을 미리 가지고있는다.
          'nx': null,
          'ny': null,
          'swx': null,
          'swy': null,
          'sex': null,
          'sey': null,
          'dist': null,
          'id': null,
          'geo': null,
          'attr': null,
        };

        canvasId.svg.call(d3.drag().on('start',canvasId.addTriangle.start).on('drag',canvasId.addTriangle.drag).on('end',canvasId.addTriangle.end));
    }

    if(shape=="circle"){
        // Actions/Listeners
        canvasId.addCircle = canvasId.makeAddCircle();

        // Circle 도형 요소 삽입
        canvasId.Circle = { // Current Selection
          'r': null,
          'g': null,
          'cx': null,
          'cy': null,
          'id': null,
          'attr': null,
          'distance': null,
          'id': null,
          'geo': null,
          'attr': null,
        };

        canvasId.svg.call(d3.drag().on('start',canvasId.addCircle.start).on('drag',canvasId.addCircle.drag).on('end',canvasId.addCircle.end));
    }

    if(shape=="ellipse"){
        // Actions/Listeners
        canvasId.addEllipse = canvasId.makeAddEllipse();

        // ellipse 도형 요소 삽입
        canvasId.Ellipse = { // Current Selection
                'r': null,
                'g': null,
                'cx': null,
                'cy': null,
                'rx': null,
                'ry': null,
                'id': null,
                'geo': null,
                'attr': null,
        };

        canvasId.svg.call(d3.drag().on('start',canvasId.addEllipse.start).on('drag',canvasId.addEllipse.drag).on('end',canvasId.addEllipse.end));
    }

    if(shape=="arc"){
        // Actions/Listeners
        canvasId.addArc = canvasId.makeAddArc();

        // Arc 도형 요소 삽입
        canvasId.Arc = { // Current Selection
                'r':null,
                'g': null,
                'd': null,
                'x1': null,
                'x2': null,
                'y1': null,
                'y2': null,
                'id': null,
                'geo': null,
                'attr': null,
                'cx': null,
                'cy': null
        };

        canvasId.svg.call(d3.drag().on('start',canvasId.addArc.start).on('drag',canvasId.addArc.drag).on('end',canvasId.addArc.end));
    }

    if(shape=="pie"){
        // Actions/Listeners
        canvasId.addPie = canvasId.makeAddPie();

        // pie 도형 요소 삽입
        canvasId.Pie = { // Current Selection
                'r': null,
                'g': null,
                'd': null,
                'top': null,
                'middle': null,
                'bottom': null,
                'left': null,
                'right': null,
                'id': null,
                'geo': null,
                'attr': null,
                'cx': null,
                'cy': null
        };

        canvasId.svg.call(d3.drag().on('start',canvasId.addPie.start).on('drag',canvasId.addPie.drag).on('end',canvasId.addPie.end));
    }

    if(shape=="hexagon"){
        // Actions/Listeners
        canvasId.addHexagon = canvasId.makeAddHexagon();

        canvasId.Hexagon = { // Current Selection
                'r': null,
                'g': null,
                'points': null,
                'cx': null,
                'cy': null,
                'top1': null,
                'top2': null,
                'bottom1': null,
                'bottom2': null,
                'left': null,
                'right': null,
                'd': null,
                'id': null,
                'geo': null,
                'attr': null,
        };

        canvasId.svg.call(d3.drag().on('start',canvasId.addHexagon.start).on('drag',canvasId.addHexagon.drag).on('end',canvasId.addHexagon.end));
    }

    if(shape=="point"){
        // Actions/Listeners
        canvasId.addPoint = canvasId.makeAddPoint();

        canvasId.Point = { // Current Selection
                'r': null,
                'g': null,
                'points': null,
                'cx': null,
                'cy': null,
                'top1': null,
                'top2': null,
                'bottom1': null,
                'bottom2': null,
                'left': null,
                'right': null,
                'd': null,
                'id': null,
                'geo': null,
                'attr': null,
        };

        canvasId.svg.call(d3.drag().on('start',canvasId.addPoint.start).on('drag',canvasId.addPoint.drag).on('end',canvasId.addPoint.end));
    }

    if(shape=="text"){
        // Actions/Listeners
        canvasId.addText = canvasId.makeAddText();

        // pie 도형 요소 삽입
        canvasId.Text = { // Current Selection
                'x': null,
                'g': null,
                'y': null,
                'text': null,
                'id': null,
                'geo': null,
                'attr': null,
                'cx': null,
                'cy': null
        };

      /*  // Keydown events
        //d3.select('body').on('keydown', this.keydownEventHandlers);
      SVGCanvas.prototype.keydownEventHandlers = function () {
        // Event handler for keydown events
        // Press 'Delete' to remove all active groups.
          if (d3.event.key === 'Delete') {
              d3.selectAll('g.active').remove();
            }
          if (d3.event.key === 'Enter') {
              alert("Enter");
            }
          if (d3.event.key === 'Escape') {
              alert("Escape");
            }
          if (d3.event.key === 'Shift') {
              alert("Shift");
            }
      }*/

        /*canvasId.svg.call(d3.drag().on('start',canvasId.addText.start));*/
        canvasId.svg.on("click",canvasId.addText.start, "aa");
        /*document.getElementById("fuck").focus();*/
        /*
        on('drag',canvasId.addText.drag).on('end',canvasId.addText.end));*/
    }

    if(shape=="image"){
        // Actions/Listeners
        canvasId.addImage = canvasId.makeAddImage();

        // Circle 도형 요소 삽입
        canvasId.Image = { // Current Selection
                'r': null,
                'g': null,
                'x': null,
                'y': null,
                'height': null,
                'width': null,
                'href': imageUrl,
                'id': null,
                'geo': null,
                'attr': null,
                'cx': null,
                'cy': null
        };

        canvasId.svg.call(d3.drag().on('start',canvasId.addImage.start).on('drag',canvasId.addImage.drag).on('end',canvasId.addImage.end));
    }

    // minimap 전용 사각 그리기(투명도 작도 아님!!!!!!!!!!!!!!!)
    if(shape=="minimap"){
        // Actions/Listeners
        canvasId.addMiniMap = canvasId.makeMiniMapRect();

        // Rectangles 도형 요소 삽입
        canvasId.mapRect = { // Current Selection
          'r': null,
          'g': null,
          'x0': null,
          'y0': null,
        };

        canvasId.svg.call(d3.drag().on('start',canvasId.addMiniMap.start).on('drag',canvasId.addMiniMap.drag).on('end',canvasId.addMiniMap.end));
    }

}

// 생성된 객체의 ID를 만들어 주는 메소드 ( 도형타입 + 현재 도형의 개수)
function idMaker(svgCanvas,shapeNm) {
    var svgOjb = comAttr;
    var makeId = null;

    if( shapeNm === "line" ){
        makeId = shapeNm + svgOjb.svgShapeNum.line;
    }
    if( shapeNm === "point" ){
        makeId = shapeNm + svgOjb.svgShapeNum.point;
    }
    if( shapeNm === "rect" ){
        makeId = shapeNm + svgOjb.svgShapeNum.rect;
    }
    if( shapeNm === "roundrect" ){
        makeId = shapeNm + svgOjb.svgShapeNum.roundrect;
    }
    if( shapeNm === "triangle" ){
        makeId = shapeNm + svgOjb.svgShapeNum.triangle;
    }
    if( shapeNm === "circle" ){
        makeId = shapeNm + svgOjb.svgShapeNum.circle;
    }
    if( shapeNm === "ellipse" ){
        makeId = shapeNm + svgOjb.svgShapeNum.ellipse;
    }
    if( shapeNm === "arc" ){
        makeId = shapeNm + svgOjb.svgShapeNum.arc;
    }
    if( shapeNm === "pie" ){
        makeId = shapeNm + svgOjb.svgShapeNum.pie;
    }
    if ( shapeNm === "hexagon" ) {
        makeId = shapeNm + svgOjb.svgShapeNum.hexagon;
    }
    if ( shapeNm === "point" ) {
        makeId = shapeNm + svgOjb.svgShapeNum.point;
    }
    if( shapeNm === "image" ){
        makeId = shapeNm + svgOjb.svgShapeNum.image;
    }
    if( shapeNm === "marker" ){
        makeId = shapeNm + svgOjb.svgShapeNum.marker;
    }
    if( shapeNm === "text" ){
        makeId = shapeNm + svgOjb.svgShapeNum.text;
        svgOjb.svgShapeNum.text += 1;
    }
    if( shapeNm === "milsymbol" ){
        makeId = shapeNm + svgOjb.svgShapeNum.milsymbol;
    }
    if( shapeNm === "editor" ){
        makeId = shapeNm + svgOjb.svgShapeNum.editor;
    }

    return(makeId);
}

//캔버스상 모든 객체 정보
SVGCanvas.prototype.getSvgOjbInfo = function( type, id ) {
  // 1) 얻고자 하는 객체 종류          : 셀렉터, 도형, 그룹 (selector, shape, group)
  // 2) 얻고자 하는 객체 ID               : 아이디를 가지고 종류 판단 가능하도록
  // 3) 얻고자 하는 정보
  // 4) 어떻게 리턴 할것인가           : Ojb로
  var self = this;
  var params = {};
  if( type === null || type === undefined || type === "" || type === '' ){
      console.log('type 항목이 없습니다.');
      return;
  }

  if( id === null || id === undefined || id === "" || id === '' ){
      console.log('id 항목이 없습니다.');
      return;
  }

  if( type === "shape" ){     //도형
      var shape = self.Shapes.shape[id];
      return shape;
  }

  if( type === "selector" ){  //셀렉터
      var selector = self.Shapes.selector[id];
      return selector;
  }

  if( type === "group" ){     //그룹

  }
}

//객체 그려주는 메소드
SVGCanvas.prototype.addDrawObj = function(parsam, id, textId) {
  var self = this;
  d3Canvas.svg.selectAll("g."+id).node().appendChild(parsam);
}

//텍스트 추가
SVGCanvas.prototype.addTextObj = function(textId, text) {
    var self = this;
    //텍스트 추가
    self.svg.selectAll('#'+textId).text(text);
}

//스타일 적용 메소드
SVGCanvas.prototype.updateStyleObj = function(id, styleNm, value) {
  var self = this;
  //스타일 적용
/*   console.log(id);
  console.log(styleNm);
  console.log(value);*/
  self.svg.selectAll("#"+id).style(styleNm,value);
}

//속성 적용 메소드
SVGCanvas.prototype.updateAttrObj = function(id, attrNm, value) {
    var self = this;
    /*
      console.log(id);
      console.log(attrNm);
      console.log(value);
      */
    self.svg.selectAll("#"+id).attr(attrNm,value);
}

//마커 생성 메소드
SVGCanvas.prototype.makeMarker = function(d, markerCol, shapeId, reverseYn) {
  var self = this;
  var markerId = shapeId+'_'+idMaker(self,"marker")+'_'+d.id;
  var drefX = "0";

  /* 예외처리
   * 1) fill = none 이 필수인 마커에서만 사용
   * - 사각형 채움(lineCapRectangle_F)
     - 원채움(lineCapCircle_F)
     - 마름모 채움(lineCapDiamond_F)
     2) 원 처리 할경우 refX = -2.5 값 넣어야함
     - 원(lineCapCircle)
  */
  //fill = none 처리변수
  var fillYn = false;
  if( d.id === "lineCapRectangle" || d.id === "lineCapCircle" || d.id === "lineCapDiamond"  ){
      fillYn = true;
  }

  //원 예외처리 변수
  if( d.id === "lineCapCircle"){
      drefX = "-2.5";
  }

  if( reverseYn === "Y" ){
      orient = "auto-start-reverse";
  } else {
      orient = "auto";
  }

  if( fillYn ){
      this.svg.select('defs')
      .append('svg:marker')
      .attr('id', markerId)
      .attr('markerWidth', 10)
      .attr('markerHeight', d.height)
      .attr('markerUnits', 'strokeWidth')
      .attr('orient',  orient)
      .attr('refX', drefX)
      .attr('refY', 0)
      .attr('viewBox', d.viewbox)
      .append('svg:path')
      .attr('d', d.path)
      .attr('fill', "none")
      .attr('stroke', markerCol);
  } else {
      this.svg.select('defs')
      .append('svg:marker')
      .attr('id', markerId)
      .attr('markerWidth', 10)
      .attr('markerHeight', d.height)
      .attr('markerUnits', 'strokeWidth')
      .attr('orient',  orient)
      .attr('refX', 0)
      .attr('refY', 0)
      .attr('viewBox', d.viewbox)
      .append('svg:path')
      .attr('d', d.path)
      .attr('fill', markerCol);
  }

  comAttr.svgShapeNum.marker += 1;
  return markerId;
}

//속성 저장 메소드
SVGCanvas.prototype.saveAttrObj = function(id, paramOjbj) {
    var self = this
    var shapeOjb = self.Shapes.shape[id];

    shapeOjb.attr = paramOjbj;
}

//음영 속성 생성
SVGCanvas.prototype.fillHatchInit = function ( shapeId , hatchOjb ) {
    console.log('캔버스 패턴 생성');
    var self = this;

    var defs = this.svg.append('defs')
        .attr('id', 'hatchPatternDefs');

    var hatchCode = null;
    var hatchForeCol = null;
    var hatchBackCol = null;
    var hatchId = null;
    var patternId = null;
    var hatchPattern = null

    hatchCode = hatchOjb.hs;
    hatchForeCol = hatchOjb.fg;
    hatchBackCol = hatchOjb.bg;

    hatchId = enumType.enum_hatchStyle[hatchCode].id;
    hatchPattern = enumType.enum_hatchStyle[hatchCode].pattern;

    //내일 할일 음영(해치) 아이디 각자 만들기
    //shapeId+영문명(ex : rect0_horizontal)
    patternId = shapeId+"_"+hatchId;

    var hatchs = defs.append('pattern')
            .attr('id', patternId)
            .attr('patternUnits', 'userSpaceOnUse')
            .attr('width', '10')
            .attr('height', '10')
            .html(hatchPattern);

    return 0;
}

SVGCanvas.prototype.hatchSelect = function ( shapeId, hatchOjb ) {
    console.log('메인 캔버스 음영 선택');
//    console.log(svgId);
//    console.log(hatchOjb);
    var self = this;
    var hatchCode = null;
    var hatchForeCol = null;
    var hatchBackCol = null;
    var hatchId = null;

    hatchCode = hatchOjb.hs;
    hatchForeCol = hatchOjb.fg;
    hatchBackCol = hatchOjb.bg;

    hatchId = enumType.enum_hatchStyle[hatchCode].id;
    hatchIdMod = enumType.enum_hatchStyle[hatchCode].id+"Mod";

    // 패턴 적용을 위한 세팅 (d3 syntax)
    var patternId = shapeId+"_"+hatchId;
    var hatchIdForm = "url(#"+patternId+")";

    //전경색 세팅
    self.svg.selectAll('defs').selectAll('#'+patternId).selectAll('path').style("stroke",hatchForeCol);
    //배경색 세팅
    self.svg.selectAll('defs').selectAll('#'+patternId).selectAll('rect').attr("fill",hatchBackCol);
    //패턴 적용
    //self.svg.selectAll('#'+shapeId).attr("fill",hatchIdForm);
}


function patternCanvas(options) {
  // An SVG-based drawing
  var self = this;
  // Define the global SVG options
  this.options = options || {};
  this.options.h = options.h || 250; // SVG Height and Width
  this.options.w = options.w || 250;
  this.options.addTo = options.addTo || 'body'; // Where to add the SVG (a css selector)
  this.options.addBorderRect = options.addBorderRect || true; // Whether to add a border around the SVG.
  this.options.hatch = options.hatch || null; // 초기화시 선택될 패턴

  // Make the SVG
  this.svg = d3.select(this.options.addTo)
    .append('svg')
    .attr('height', this.options.h)
    .attr('width', this.options.w)
    .attr('class', 'display-svg')
    .attr('id',this.options.svgId);

  // Add border if requested
  if (this.options.addBorderRect) {
    this.svg.append('rect')
      .attr('height', this.options.h)
      .attr('width', this.options.w)
      .attr('stroke', 'black')
      .attr('stroke-width', 1)
      .attr('opacity', 0.25)
      .attr('fill-opacity', 0.0)
      .attr('class', 'border-rect');

    //음영 패턴 초기화
    this.hatchInit();
  }
}

patternCanvas.prototype.hatchInit = function () {
    console.log(' 음영 초기화(채움 속성) ');
    var self = this;

    var defs = this.svg.append('defs')
        .attr('id', 'hatchPattern');

    //정의된 음영 데이터 가져와서 패턴 초기화
    var fillHatch = Object.values(enumType.enum_hatchStyle);

    /* 원본(받아온 패턴과 수정될 패턴의 id를 구분하기 위해
        - 서로다른 svg 에 속해있어도 상대 svg id의 패턴을 가져옴(버그 인지 아닌지 알수없음)
        */
    var canvasNum = self.svg._groups[0][0].id;

    var hatchs = defs.selectAll('pattern')
        .data(fillHatch)
        .enter()
        .append('pattern')
            .attr('id', function(d){
                    if( canvasNum === "canvas2"  ) {
                        return d.id + "Mod";
                    }
                    return d.id;
                })
            .attr('patternUnits', 'userSpaceOnUse')
            .attr('width', '10')
            .attr('height', '10')
            .html(function(d){ return d.pattern})
    return 0;
}

patternCanvas.prototype.hatchSelect = function ( svgId , hatchOjb ) {
    console.log('음영 선택');
//    console.log(svgId);
//    console.log(hatchOjb);
    // Methods for adding Line to the svg.
    var self = this;
    var hatchCode = null;
    var hatchForeCol = null;
    var hatchBackCol = null;
    var hatchId = null;

    hatchCode = hatchOjb.hs;
    hatchForeCol = hatchOjb.fg;
    hatchBackCol = hatchOjb.bg;

    hatchId = enumType.enum_hatchStyle[hatchCode].id;
    hatchIdMod = enumType.enum_hatchStyle[hatchCode].id+"Mod";

    // 패턴 적용을 위한 세팅 (d3 syntax)
    var hatchIdForm = "url(#"+hatchId+")";
    var hatchIdModForm = "url(#"+hatchIdMod+")";

    if( svgId == "canvas1" ){
        //전경색 세팅
        self.svg.selectAll('defs').selectAll('#'+hatchId).selectAll('path').style("stroke",hatchForeCol);
        //배경색 세팅
        self.svg.selectAll('defs').selectAll('#'+hatchId).selectAll('rect').attr("fill",hatchBackCol);
        //패턴 적용
        self.svg.selectAll('#patternOrgin').attr("fill",hatchIdForm);
    } else if ( svgId == "canvas2" ) {
        //전경색 세팅
        self.svg.selectAll('defs').selectAll('#'+hatchIdMod).selectAll('path').style("stroke",hatchForeCol);
        //배경색 세팅
        self.svg.selectAll('defs').selectAll('#'+hatchIdMod).selectAll('rect').attr("fill",hatchBackCol);
        //패턴 적용
        self.svg.selectAll('#patternModify').attr("fill",hatchIdModForm);
    }
}

function selectedPatternCanvas(options) {
  // An SVG-based drawing
  var self = this;
  // Define the global SVG options
  this.options = options || {};
  this.options.h = options.h || 59; // SVG Height and Width
  this.options.w = options.w || 198;
  this.options.addTo = options.addTo || 'body'; // Where to add the SVG (a css selector)
  this.options.addBorderRect = options.addBorderRect || true; // Whether to add a border around the SVG.
  this.options.hatch = options.hatch || null; // 초기화시 선택될 패턴

  // Make the SVG
  this.svg = d3.select(this.options.addTo)
    .append('svg')
    .attr('height', this.options.h)
    .attr('width', this.options.w)
    .attr('class', 'display-svg')
    .attr('id',this.options.svgId);

  // Add border if requested
  if (this.options.addBorderRect) {
    this.svg.append('rect')
      .attr('height', this.options.h)
      .attr('width', this.options.w)
      .attr('stroke', 'black')
      .attr('stroke-width', 1)
      .attr('opacity', 0.25)
      .attr('fill-opacity', 0.0)
      .attr('class', 'border-rect');

    // 패턴을 보여줄 rect
    this.svg.append('rect')
      .attr('x', '0')
      .attr('y', '0')
      .attr('height', '59')
      .attr('width', '198')
      .attr('stroke', 'black')
      .attr('stroke-width', 1)
      .attr('fill', 'none')
      .attr('id', 'hatchPattern')
    //음영 패턴 초기화

    var hatchId = this.hatchInit(this.options.hatch);
  }
}

selectedPatternCanvas.prototype.hatchInit = function (attrOjb) {
    var self = this;

    var defs = this.svg.append('defs')
        .attr('id', 'hatchPatternDefs');

    var hatchCode = attrOjb.hs;
    var hatchForeCol = attrOjb.fg;
    var hatchBackCol = attrOjb.bg;
    var hatchId = null;
    var hatchPattern = null

    hatchId = enumType.enum_hatchStyle[hatchCode].id;
    hatchPattern = enumType.enum_hatchStyle[hatchCode].pattern;

    var hatchs = defs.append('pattern')
            .attr('id', hatchId+"selected")
            .attr('patternUnits', 'userSpaceOnUse')
            .attr('width', '10')
            .attr('height', '10')
            .html(hatchPattern)

    return hatchId;
}

selectedPatternCanvas.prototype.hatchSelect = function (attrOjb) {
    var self = this;
    var hatchCode = attrOjb.hs;
    var hatchForeCol = attrOjb.fg;
    var hatchBackCol = attrOjb.bg;
    var hatchId = null;
    var hatchPattern = null

    hatchId = enumType.enum_hatchStyle[hatchCode].id+"selected";
    var hatchIdForm = "url(#"+hatchId+")";

    //전경색 세팅
    self.svg.selectAll('defs').selectAll('#'+hatchId).selectAll('path').style("stroke",hatchForeCol);
    //배경색 세팅
    self.svg.selectAll('defs').selectAll('#'+hatchId).selectAll('rect').attr("fill",hatchBackCol);
    //패턴 적용
    self.svg.selectAll('#hatchPattern').style("fill",hatchIdForm);
}

// 투명도 전환 대상 객체들 투명도 캔버스로 전환 ( map ==> Canvas )
SVGCanvas.prototype.milSymbolMapToCanvas = function ( params ) {
    var self = this;
    var m = self.mouseOffset();

    var _milSymbolData = params;
    var _lon = null; //경도
    var _lat = null; //위도
    var point = {};     // 경위도 -> 화면좌표 변환용
    var result = null;

    var shapeId = null;
    var centerX = null;
    var centerY = null;
    var sBB = null;

    self.Milsymbol = { // Current Selection
           'r': null,
           'g': null,
           'x': null,
           'y': null,
           'id': null,
           'geo': null,
           'attr': null,
           'cx': null,
           'cy': null,
           'symbolInfo' : null
     };

    //symbolInfo, data
    for( let [key, value] of Object.entries(_milSymbolData) ){
        shapeId = idMaker(self,"milsymbol");
        centerX = null;
        centerY = null;
        point.lon = value.coords[0];
        point.lat = value.coords[1];
        //경위도 => 화면좌표 변환
        result = stmp.mapObject.project(point);
        var coodX = result.x;
        var coodY = result.y;

        //캔버스에 symbol 그리기
       var milSymbolInfo = self.svg //self.
          .append('g')
          .append('image')
          .attr('x', result.x)
          .attr('y', result.y)
          .attr('id', shapeId)
          .attr("xmlns","http://www.w3.org/2000/svg")
          .attr("xmlns:xlink", "http://www.w3.org/1999/xlink")
          .attr('xlink:href', value.data)
          .on("click",
                  function (d){
                      self.selectShape(shapeId);
                  }
              );

       // 군대부호 중심위치 조정
       //setTimeout(self.milSymbolCenterFix( milSymbolInfo , shapeId , coodX, coodY ), 100000);
       self.milSymbolCenterFix( milSymbolInfo , shapeId , coodX, coodY );

       self.Milsymbol.x = centerX;
       self.Milsymbol.y = centerY;
       self.Milsymbol.id = shapeId;
       self.Milsymbol.cx = centerX;
       self.Milsymbol.cy = centerY;
       self.Shapes.shape[shapeId] =  self.Milsymbol;
       comAttr.svgShapeNum.milsymbol += 1;
    }
    // Make it active.
    self.setActive(shapeId);
}

// 군대부호 중심위치 조정
SVGCanvas.prototype.milSymbolCenterFix = function( milSymbolInfo, shapeId, coodX, coodY ) {
  var self = this;

  //중심좌표만큼 좌표 이동
  var sBB = d3.selectAll("#"+shapeId).node().getBBox();
  var centerX = coodX - (sBB.width/ 2);
  var centerY = coodY - (sBB.height / 2);

  milSymbolInfo.attr('x',centerX).attr('y',centerY);
}

// 객체 복사
SVGCanvas.prototype.shapeCopy = function( paramOjb, shapeId ) {
    var self = this;
    var shapeTargetInfo = stmp.cloneDeep(paramOjb);
    var copyHtmlOjb = d3Canvas.svg.selectAll("."+shapeId).clone(true);

    // Actions/Listeners
    self.shapePaste = self.addShapePaste(shapeTargetInfo, copyHtmlOjb, shapeId);

    // tempShapeInfo
    self.tempShapeInfo = shapeTargetInfo;

    self.svg.call(d3.drag().on('start',self.shapePaste.start).on('drag',self.shapePaste.drag).on('end',self.shapePaste.end));
}

//객체 잘라내기
SVGCanvas.prototype.shapeCut = function( paramOjb, shapeId ) {
    var self = this;
    var shapeTargetInfo = stmp.cloneDeep(paramOjb);
    var copyHtmlOjb = d3Canvas.svg.selectAll("."+shapeId).clone(true);
    var mode = "cut";

    // Actions/Listeners
    self.shapePaste = self.addShapePaste(shapeTargetInfo, copyHtmlOjb, shapeId, mode);

    // tempShapeInfo
    self.tempShapeInfo = shapeTargetInfo;

    self.svg.call(d3.drag().on('start',self.shapePaste.start).on('drag',self.shapePaste.drag).on('end',self.shapePaste.end));
}

//객체 삭제
SVGCanvas.prototype.shapeDelete = function( paramOjb, shapeId ) {
    var self = this;
    //객체 및 요소 삭제
    self.svg.selectAll("#"+shapeId).remove();
    self.svg.selectAll('#selectorGroup_'+shapeId).remove();
    self.svg.selectAll('g.active').remove();

    SelectedShape(self,"select");
}

// 잘라내거나 복사한값 붙여넣기
SVGCanvas.prototype.addShapePaste = function( paramOjb, copyHtmlOjb, shapeId, mode) {
    var self = this;
    var _shapeOjb = paramOjb;
    var newShapeId = null;
    var distX = null;
    var distY = null;
    var attrTranslate = null;
    var shapeType = null;

    var shapeInfo = null;
    var selectorId = null;
    var selectorGid = null;
    var selectorInfo = null;
    var cx = null;
    var cy = null;

    start = function() {
        var m = self.mouseOffset();
        shapeType = numberRemove(shapeId);
        //shapeId 변경_새로 만든 id로
        if( mode === "cut" ){
            newShapeId = shapeId;

            //기존 도형 삭제를 위한 아이디 변환
            self.svg.selectAll("#"+shapeId).attr("id", 'delTarget');
            copyHtmlOjb.selectAll("#delTarget").attr("id", shapeId);

            //셀렉터 삭제를 위한 변환
            d3Canvas.svg.selectAll("#delTarget").node().parentElement.classList.value="delTarget";

            self.svg.selectAll("#delTarget").remove();
            self.svg.selectAll(".delTarget").remove();
            copyHtmlOjb.selectAll("#delTarget").attr("id", shapeId);
        } else {
            newShapeId = idMaker(self, shapeType);
        }

        selectorId = "selectedBox_" + shapeId;
        selectorInfo = self.Shapes.selector[selectorId];
        shapeInfo = self.Shapes.shape[shapeId];

        cx = selectorInfo.centerX;
        cy = selectorInfo.centerY;


        attrTranslate = "translate(10, 10)";

        //transForm 처리 함수
        var attrTransformStart = self.updateTransForm( shapeId, 'translate', attrTranslate);
        //copyHtmlOjb.selectAll("#"+shapeId).attr("transform", "translate(10,10)").attr("id",newShapeId).style("display",null);

        copyHtmlOjb.selectAll("#"+shapeId).attr("transform", attrTransformStart).attr("id",newShapeId).style("display",null);
    }

    drag = function() {
        var m = self.mouseOffset();

        distX = m.x - cx;
        distY = m.y - cy;

        attrTranslate = "translate("+distX+","+distY+")";
        //var attrTransform = attrTranslate;

        //transForm 처리 함수
        var attrTransform = self.updateTransForm( shapeId, 'translate', attrTranslate);

        copyHtmlOjb.selectAll("#"+newShapeId).attr("transform", attrTransform)
        .on("click",
                function (d){
                    self.selectShape(newShapeId);
                }
            )
        .on("contextmenu",
                function (d){
                    //우클릭시 서브 메뉴바 띄우기 예제
                    d3.contextMenu(menu, self, newShapeId);
                }
            );

        //상위 g 의 클래스명 변경
        var fistClass = d3Canvas.svg.selectAll("#"+newShapeId).node().parentNode.classList[0];
        d3Canvas.svg.selectAll("#"+newShapeId).node().parentNode.classList.value = fistClass +" "+ newShapeId;
    }

    end = function() {
        var m = self.mouseOffset();
        _shapeOjb.id = newShapeId;
        _shapeOjb.cx = m.x;
        _shapeOjb.cy = m.y;
        _shapeOjb['translateInfo'] =  attrTranslate;
        self.Shapes.shape[newShapeId] = _shapeOjb;
        self.Shapes.shape['translateInfo'] = attrTranslate;
        comAttr.svgShapeNum[shapeType] += 1;

        self.setActive(newShapeId);
    }

    return {
         start: start,
         drag: drag,
         end: end
    };
}

// transForm 처리 함수 ( 회전 및 이동 정보를 받아와서 업데이트 해주는 기능)
SVGCanvas.prototype.updateTransForm = function( shapeId, updateKind, updateInfo ) {
    var self = this;
    var attrTranslate = " ";
    var attrRotate = " ";
    var attrTransform = " ";
    var shapeInfo = stmp.cloneDeep(self.Shapes.shape[shapeId]);

    // 현재 정보 가져오기(업데이트 전)
    // 회전정보
    if( shapeInfo.hasOwnProperty('rotateInfo') ){
        attrRotate = shapeInfo.rotateInfo;
    }

    // 이동정보
    if( shapeInfo.hasOwnProperty('translateInfo') ){
        attrTranslate = shapeInfo.translateInfo;
    }

    /*
     * updateKind 업데이트 희망 정보
     * 1. 회전 rotate
     * 2. 이동 translate
     * 3. 모두 all
     * */
    if( updateKind === "rotate" ){
        attrRotate = updateInfo;

        attrTransform = attrTranslate + " " + attrRotate;
    }

    if( updateKind === "translate" ){
        attrTranslate = updateInfo;
        attrTransform = attrTranslate + " " + attrRotate;
    }

    if( updateKind === "all" ){
        attrTransform = updateInfo;
    }

    return attrTransform;
}

// 이동을 한 도형을 기준으로 좌표값 계산해서 리턴
SVGCanvas.prototype.convertTransToCoords = function( transform ) {
    var self = this;
    var _transform = transform;
    var transCoords = transform;
    var transX = 0;
    var transY = 0;
    var resultCoords = [];

    _transform = _transform.split(" ");
    if( _transform[0].indexOf("translate") > -1 ){
        transCoords = _transform[0].replace("translate(","").replace(")","").split(",");

        transX = Number(transCoords[0]);
        transY = Number(transCoords[1]);
    }
    resultCoords[0] = transX;
    resultCoords[1] = transY;
    return resultCoords;
}

//show Editor
SVGCanvas.prototype.createEditor = function() {
   var self = this;
   var contents = shareEditorData;
   var editorId = idMaker(self, "editor");

   // 캔버스에 요소 추가
   var selectorGroup = addSvgElementFromJson({
     element: 'g',
     attr: {'class': 'g-editor '+editorId},
   });

   // this holds a reference to the path rect Rect로 만든 셀렉터
   var editorDetail = selectorGroup.appendChild(
           addSvgElementFromJson({
               element: 'foreignObject',
               attr : {
                  id: editorId,
                   x: '100',
                   y: '100',
                   width: '300',
                   height: '200',
               }
           })
       );

   // SVG에 추가
   document.getElementById("svgMain").appendChild(selectorGroup);

   self.svg.selectAll("#"+editorId)
       .style("overflow","visible")
       .html(contents)
       .on("click",
                   function (d){
                       self.selectShape(editorId);
                   }
               )
       .on("contextmenu",
               function (d){
                   //우클릭시 서브 메뉴바 띄우기 예제
                   d3.contextMenu(menuEditor, self, editorId);
               }
           );

   comAttr.svgShapeNum.editor += 1;

   // 캔버스 데이터에 저장
   self.editorInfo = {
           'x': 100,
           'y': 100,
           'id': editorId,
           'geo': null,
           'cx': null,
           'cy': null
   }
   self.Shapes.shape[editorId] = self.editorInfo;
   self.selectEditor(editorId);

   //에디터 데이터 초기화
   shareEditorData = '';
}


// Update Editor
SVGCanvas.prototype.updateEditor = function(editorId) {
     var self = this;
     var contents = shareEditorData;

     self.svg.selectAll("#"+editorId)
         .html(contents);

     // 공유 오브젝트 초기화
     shareEditorData = '';
}

//에디터 이동 메소드
function editorMover(self, editorId){
    var editorInfo = null;

    self.svg.call(
        d3.drag()
        .on('start', function(){
            editorInfo = self.Shapes.shape[editorId];
            self.selectShape(editorId);
        })
        .on('drag', function(){
            var m = self.mouseOffset();

            self.svg.selectAll("#"+editorId)
                .attr('x', m.x)
                .attr('y', m.y);

            editorInfo.x = m.x;
            editorInfo.y = m.y;
            editorInfo.id = editorId;
        })

        .on('end', function(){
            self.setActive(editorId, false);
        })
    )
    return;
}

// 에디터를 편집모드 또는 삭제 모드로 수정
SVGCanvas.prototype.editorEdit = function( mode, id ) {
    var self = this;

    if( mode === "edit" ){
        shareEditorData = d3Canvas.svg.selectAll('#'+id).html();
        stmp.openSMTAlert("/ui/CF/SI/SMT/CFSIOLOverlayEditorPopup.xml", "390", "투명도 설명편집", "editor", false, 550, 350, {id : id });

      } else if ( mode === "delete" ) {      //에디터 삭제
          var selectorId = ('selectedBox_'+id);
          var selectorInfo = self.Shapes.selector[selectorId];
          var selectorGid = selectorInfo.gid;

          self.delSelection( selectorGid );
          self.svg.selectAll('.'+id).remove();
      }

    SelectedShape(self,"select");
}

/* 재편집을 위해 설정 초기화
 *  편집을 위한 요소들 캔버스에 올리기 및 숨겨진 요소들 다시 보여주기
 * */
/*canvasModeReturn = function(canvasId) {
    var self = canvasId;
    var canvasShapes = self.Shapes.shape;

    // 2. 전체 객체 서칭
    for( let [key, value] of Object.entries( canvasShapes ) ){
        self.svg.selectAll('#'+value.id).style('display', null);
    }

    SelectedShape(self,"select");
}*/

/*
 * 군대부호 생성(milSymbol.Jsp 컴포넌트를 이용)
 * */
SVGCanvas.prototype.newMilSymbol = function ( params ) {
    var self = this;
    var m = self.mouseOffset();

    var _milSymbolData = params;
    var shapeId = null;
    shapeId = idMaker(self,"milsymbol");
    var milSymbolInfo = null;

    self.Milsymbol = { // Current Selection
           'r': null,
           'g': null,
           'x': null,
           'y': null,
           'id': null,
           'geo': null,
           'SIDC': null,
           'cx': null,
           'cy': null,
           'symbolInfo' : null
     };

    self.svg.call(
        d3.drag()
        .on('start', function(){
            var m = self.mouseOffset();

            milSymbolInfo = self.svg //self.
               .append('g')
               .attr('class', 'g-milSym ' + shapeId)
               .append('image')
               .attr('x', m.x)
               .attr('y', m.y)
               .attr('id', shapeId )
               .attr('xlink:href', _milSymbolData.symbolImgData )
               .on("click",
                       function (d){
                           self.selectShape(shapeId);
                       }
                   )
               .on("contextmenu",
                       function (d){
                           //우클릭시 서브 메뉴바 띄우기 예제
                           d3.contextMenu(menuMilSym, self, shapeId);
                       }
                   );

            self.Milsymbol.id  = shapeId;
            self.Milsymbol.SIDC  = _milSymbolData.sidc;
            self.Milsymbol.symbolInfo = _milSymbolData.symbolOptions;
        })
        .on('drag', function(){
            var m = self.mouseOffset();
            milSymbolInfo
                .attr('x', m.x)
                .attr('y', m.y);

            self.Milsymbol.x  = m.x;
            self.Milsymbol.y  = m.y;
        })
        .on('end', function(){
            self.Shapes.shape[shapeId] =  self.Milsymbol;
            comAttr.svgShapeNum.milsymbol += 1;

            // Make it active.
            self.setActive(shapeId);

            SelectedShape(self,"select");
        })
    )
};

//군대 부호 편집 및 삭제
SVGCanvas.prototype.milSymEdit = function( mode, id ) {
    var self = this;

    if( mode === "edit" ) {
        stmp.milSymEditOjb = d3Canvas.Shapes.shape[id].symbolInfo;
        stmp.milSymEditOjb.id = id;
        stmp.openSMTAlert("/ui/CF/SI/SMT/CFSISMMilSymbolPopup.xml", "933", "군대부호 도시", "MilSymbol");
    } else if ( mode === "delete" ) {      //에디터 삭제
        var selectorId = ('selectedBox_'+id);
        var selectorInfo = self.Shapes.selector[selectorId];
        var selectorGid = selectorInfo.gid;

        self.delSelection( selectorGid );
        self.svg.selectAll('.'+id).remove();
    }

    SelectedShape(self,"select");
};

// 군대 부호 수정
SVGCanvas.prototype.modMilSymbol = function( params, id ) {
    var self = this;
    var m = self.mouseOffset();

    var _milSymbolData = params;
    var shapeId = id;
    var milSymbolInfo = null;

    milSymbolInfo = self.svg.selectAll('#'+shapeId)
       .attr('xlink:href', _milSymbolData.symbolImgData );

    self.Milsymbol.SIDC  = _milSymbolData.sidc;
    self.Milsymbol.symbolInfo = _milSymbolData.symbolOptions;
    //공통 변수 초기화
    stmp.milSymEditOjb = null;

    // Make it active.
    self.setActive(shapeId);

    SelectedShape(self,"select");
}


/* 편집중 맵이동이 필요할때 설정변환
 * 1. 모든 셀렉터 감추기 or 삭제
 * 2. 화면 고정인 객체만 화면 고정시켜 이동
 * 3. 화면 고정 이외의 객체는 맵에 적용
 * */
let editingMoveMap = function(canvasId) {
    var self = canvasId;
    var canvasShapes = self.Shapes.shape;
    var transformInfo = null;

    // 투명도 편집중 맵 이동 여부 체크 활성화
    stmp.editingToMoveMapYn = true;

    // 공통속성 임시 저장
    stmp.svgCommonAttr = comAttr;

    // 1. 모든 셀렉터 감추기 or 삭제
    $('[id^=selectorGroup]').each(function(e,i) {
        var selectorId = i.id;
        self.delSelection( selectorId );
    });

    // 모든 엑티브 상태 제거
    self.svg.selectAll('g.active').classed('active', false);

    // 2. 전체 객체 서칭
    for( let [key, value] of Object.entries( canvasShapes ) ){
        transformInfo = null;
        var classNm = null;
        classNm = self.svg.selectAll('#'+value.id).node().classList.value.toString();
        if( classNm.indexOf('fixed') > -1 ){
            // 고정인 객체 처리
        } else {
            // 3. 화면 고정 이외의 객체는 맵에 적용 (에디터 제외)
            var _shapeId = value.id;
            var shapeElement = self.svg.selectAll("."+_shapeId).node();
            var shapeObjHtml = shapeElement.outerHTML.toString();
            var shapeMeta = self.getBbox(_shapeId);
            var result = null;
            var rotateInfo = null;

            // 이동 및 회전 체크를 위한 데이터 가져오기
            transformInfo = self.svg.selectAll('#'+_shapeId).node().getAttribute('transform');

            //화면좌표 => 경위도 변환
            var point = {};
            point.x = shapeMeta.cx;
            point.y = shapeMeta.cy;

            if( transformInfo !== null ){
                // 도형이 이동한 상태이면 중심좌표를 translate 한 위치적용
                var transDivid =transformInfo.split(' ');

                // 이동 및 회전 정보를 체크하여 가져오기
                transDivid.forEach(
                    function(d, i){
                        if( d.indexOf('translate') > -1) {
                            var transCxy = self.convertTransToCoords( transformInfo );
                            point.x += Number(transCxy[0]);
                            point.y += Number(transCxy[1]);
                        }

                        if( d.indexOf('rotate') > -1) {
                            rotateInfo = d;
                        }
                    }
                );
                //중심좌표 적용 했으므로 로테이션 정보만 남긴다.
                self.svg.selectAll('#'+_shapeId).attr("transform", rotateInfo);
            }

            result = stmp.mapObject.unproject(point);
            var dLat = result.lat;
            var dLng = result.lng;

            // 현재 그려진 정보 그대로 객체로 넘긴다 (다시 그릴때 재사용)
            var shapeNodeInfo = self.svg.selectAll("#"+_shapeId).node().parentElement.cloneNode(true);

            // 캔버스 값
/*            var canvasWidth = parseInt(scwin.m.wfm_mapFrame.getStyle("width"));
            var canvasHeight = parseInt(scwin.m.wfm_mapFrame.getStyle("height"));*/

            // 도형객체 정보 이미지 변환 후 Uri 가져오기
            var svgUri = self.getShapeToBase64Uri( _shapeId, numberRemove(_shapeId), rotateInfo);
            console.log(svgUri);

            var featureParams = {
                id : key,
                layerId : canvasToMapNm,
                type : stmp.DRAW_TYPE_KIND.IMAGE,
                coordInfo : {
                    type : stmp.COORDINATE_SYSTEM.WGS84,
//                    coords : stmp.getMapCenter()
                    coords : [dLng, dLat]
                },
                styleInfo : {
                    image : {
                        path : svgUri
                    },
                    style : {                                       // 군대부호 외 스타일 정의
                        size : 1
                    },
                    overlayData : {
                        nodeInfo : shapeNodeInfo
                        ,bboxInfo : shapeMeta
                    },
                },
                properties : {
                    nodeInfo : shapeNodeInfo
                }
            };

            stmp.addFeature(featureParams);

            //맵에 적용후 해당 객체 삭제
            shapeElement.remove();
        }

        // 외부 객체 숨기기 처리
        self.svg.selectAll('foreignObject').style('display', 'none');
    }
};

// BBox 좌표를 기준으로 센터 및 좌표 구하기
SVGCanvas.prototype.getBbox = function( id ) {
    var self = this;
    var sBB = d3.selectAll("#"+id).node().getBBox();
    var p = {
        x: sBB.x,
        y: sBB.y,
        w: sBB.width,
        h: sBB.height,
        cx: Math.round(sBB.x + sBB.width / 2),
        cy: Math.round(sBB.y + sBB.height / 2),
        // 중심좌표와 시작 점 간의 거리
        disX: sBB.width / 2,
        disY : sBB.height / 2,
    };

    return p;
};

//도형 => base64 형태로 변환
SVGCanvas.prototype.getShapeToBase64Uri = function( id, shapeType, rotateInfo ) {
  var self = this;
  var gBB = self.svg.selectAll("#"+id).node().parentElement.getBBox();
  // 깨짐을 막기 위한 임시 갭
  const gap = 5;
  const canvasGap = 15;
  var p = {
      x: Math.round(gBB.x),
      y: Math.round(gBB.y),
      w: Math.round(gBB.width),
      h: Math.round(gBB.height),
      cx: Math.round(gBB.width / 2),
      cy: Math.round(gBB.height / 2),
      disX: gBB.width / 2,
      disY : gBB.height / 2,
  };

 // 로테이션 정보 생성(기준좌표 변경)
  var makeRotateForm = null;

  // 이미지 생성을 위해 임시 복사본 만들기
  var gSelection = self.svg.selectAll("."+id).clone(true).attr("id", "copy");
  var shapeSelection = gSelection.selectAll("#"+id);
  var sBB = gSelection.selectAll("#"+id).node().getBBox();
  var sP = {
          x: Math.round(sBB.x),
          y: Math.round(sBB.y),
          w: Math.round(sBB.width),
          h: Math.round(sBB.height),
          cx: Math.round(sBB.width / 2),
          cy: Math.round(sBB.height / 2),
          disX: sBB.width / 2,
          disY : sBB.height / 2,
      };

  if ( shapeType === "milsymbol") {
      shapeSelection
      .attr("xmlns","http://www.w3.org/2000/svg")
      .attr("xmlns:xlink", "http://www.w3.org/1999/xlink")
      .attr("x", Math.round(gap))
      .attr("y", Math.round(gap));
  }

  //중심 좌표 이동을 통해 이미지 그리기
  self.centerMoveReDraw( id,  shapeSelection, [p.cx+gap, p.cy+gap], sP );

  // 회전 여부에 따른 회전 적용
  if( rotateInfo === undefined || rotateInfo === null ){
      //로테이션 정의가 없을때 정의
  } else {
      var tempInfo =  rotateInfo.split(',');

      makeRotateForm = tempInfo[0] + ','+(p.cx+gap)+','+(p.cy+gap)+')';
      shapeSelection.attr('transform', makeRotateForm);
  }

  var copyHtmlOjb = gSelection.node().outerHTML;
  console.log(copyHtmlOjb);
  // 기본 레이어 추가
  //var svg = '<svg xmlns="http://www.w3.org/2000/svg" height="1000" width="1600" >';
  var svg = '<svg xmlns="http://www.w3.org/2000/svg" height="'+(p.h+canvasGap)+'" width="'+(p.w+canvasGap)+'" >';
  svg += copyHtmlOjb.toString();
  svg += '</svg>';

  //복사에 사용한 객체 삭제
  self.svg.selectAll('#copy').remove();

  return svg = 'data:image/svg+xml;base64,' + btoa(unescape(encodeURIComponent((svg))));
};


/* 편집중 맵이동 에서 다시 편집 모드로 돌아올때
 * - 화면 고정 객체 이외의 모든 객체 복원
 * 1) 현재 맵으로 이동시킨 객체들( layerId = 'canvasToMap' )의 리스트를 받아온다
 * 2) 이동한 맵을 기준으로 화면 좌표 반환
 * 3) 각 객체들 다시 그리기
 * */
let mapMoveToEditing = function(canvasId) {
    var self = canvasId;

    //속성 공통에 임시 저장( 공통속성 복귀 )
    let comAttr = stmp.svgCommonAttr;

    // 맵의 레이어로 부터 객체 정보 가져오기
    stmp.mapObject.getSource(canvasToMapNm)._data.features.forEach(
        function (d, i){
            self.reDrawingShape(d);
        }
    );

    //  현재 맵의 레이어 리스트 가져오기
    var layerList = stmp.getLayerList2d().map;

    // 레이어 삭제
    // 레이어 Id중 canvasToMapNm의 이름을 포함하는 레이어 삭제(임시 맵에 적용 했던 레이어)
    for( let [key, value] of Object.entries( layerList ) ){
        // 레이어 이름중 canvasToMapNm을 포함하는지 여부
        if( key.indexOf(canvasToMapNm) > -1 ){
            stmp.mapObject.removeLayer(key);
        }
    }


    //  현재 생성된 전체 객체 리스트
    var featureList = stmp.getGlobalFeatures().map;

    // feature 제거
    // 맵이동 중 임시로 적용되어있는 객체들 맵에서 제거
    for( let [key, value] of Object.entries( featureList ) ){
        // 전체 feature 중 canvasToMapNm을 포함하는 요소들 제거
        if( key.indexOf(canvasToMapNm) > -1 ){
            stmp.getGlobalFeatures().remove(key);
        }
    }

    SelectedShape(self,"select");
};

/*재 편집 대상 객체들 다시 투명도 캔버스에 그리기*/
SVGCanvas.prototype.reDrawingShape = function( params ) {
    var self = this;
    var shapeId = params.id;
    var featureId = canvasToMapNm+"_"+shapeId;
    var featureOjb = stmp.getGlobalFeatures().get(featureId);
    var nodeInfo = featureOjb.styleInfo.overlayData.nodeInfo;
    var bboxInfo = featureOjb.styleInfo.overlayData.bboxInfo;
    var shapeMapCoord = featureOjb.getCoordinates();
    var shapePixelCoord = null;
    var shapeSelection = null;
    var coordResult = null;

    // 맵(경위도) 좌표 ==> 화면좌표 변환
    var point = {};
    point.lon = shapeMapCoord[0];
    point.lat = shapeMapCoord[1];
    //경위도 => 화면좌표 변환
    coordResult = stmp.mapObject.project(point);
    var coodX = coordResult.x;
    var coodY = coordResult.y;

    // 요소 새로 삽입
    self.svg.node().appendChild(nodeInfo);

    shapeSelection = self.svg.selectAll('#'+shapeId);

    // 재생성된 객체에 수정 정보 붙이기(새로운 좌표 및 연결 메소드)
    self.centerMoveReDraw( shapeId,  shapeSelection, [coodX, coodY], bboxInfo );

    shapeSelection
    .on("click",
            function (d){
                self.selectShape(shapeId);
            }
        )
    .on("contextmenu",
            function (d){
                //우클릭시 서브 메뉴바 띄우기 예제
            d3.contextMenu(menu, self, shapeId);
            }
        );
}


/* 중심 이동에 따른 도형 새로그리기 : 도형 타입별 처리
 * shapeId  string
 * shapeSelection   d3Ojb
 * centerCoord  array
 * */
SVGCanvas.prototype.centerMoveReDraw = function( shapeId,  shapeSelection, centerCoord, bboxInfo) {
    var cx = centerCoord[0];
    var cy = centerCoord[1];
    var shapeType = numberRemove(shapeId);
    var _bboxInfo = bboxInfo;
    var disX = _bboxInfo.disX;
    var disY = _bboxInfo.disY;
    var x1 = cx-disX;
    var y1 = cy-disY;
    var x2 = cx+disX;
    var y2 = cy+disY;
    var height = _bboxInfo.h;
    var width = _bboxInfo.w;

    // 타입별 모양
    if( shapeType === "circle" || shapeType === "ellipse" ){
        shapeSelection
                .attr("cx", Math.round(cx))
                .attr("cy", Math.round(cy));
    } else if ( shapeType === "rect" || shapeType === "roundrect" ) {
        shapeSelection
            .attr("x", Math.round(x1))
            .attr("y", Math.round(y1));
    } else if ( shapeType === "line" ) {
        var s1 = shapeSelection.property('x1').animVal.value;
        var s2 = shapeSelection.property('x2').animVal.value;
        var d1 = shapeSelection.property('y1').animVal.value;
        var d2 = shapeSelection.property('y2').animVal.value;

        // LINE Case (화살표)
        if( d1 < d2 ){
            //↘
            if( s1 < s2 ){
                dx1 = x1;
                dy1 = y1;
                dx2 = x2;
                dy2 = y2;
            }
            else {
                //↙
                dx2 = x1;
                dy2 = y2;
                dx1 = x2;
                dy1 = y1;
            }
        } else {
            //↖
            if( s1 > s2 ){
                dx1 = x2;
                dy1 = y2;
                dx2 = x1;
                dy2 = y1;
            }
            else {
                //↗
                dx1 = x1;
                dy1 = y2;
                dx2 = x2;
                dy2 = y1;
            }
        }

        shapeSelection
        .attr("x1", dx1)
        .attr("y1", dy1)
        .attr("x2", dx2)
        .attr("y2", dy2);
    } else if(  shapeType === "triangle"  ) {
        var nx = cx;
        var ny = y1;
        var swx = x1;
        var swy = y2;
        var sex = x2;
        var sey = y2;

        var points = nx + "," + ny + " "+
        swx + "," + swy + " "+
        sex + "," + sey + " ";

        shapeSelection.attr('points', points);
    } else if(  shapeType === "pie"  ) {
        /*중심좌표*/
        var distanceX = disX;
        var distanceY =  disY;

       var x1  = cx;
       var y1  = cy;

       var x0  = x1 - Math.abs(distanceX);
       var x2  = x1 + Math.abs(distanceX);
       var y0  = y1 - Math.abs(distanceY);
       var y2  = y1 + Math.abs(distanceY);

       var top     = x1 + " " + y0;
       var middle  = x1 + " " + y1;
       var bottom  = x1 + " " + y2;
       var left    = x0 + " " + y1;
       var right   = x2 + " " + y1;

       var makeD = "M "+left+
       " C "+left+" "+x0+" "+y0+" "+ top+
       " C "+top+" "+x2+" "+y0+" "+ right+
       " L "+right+" "+ bottom+
       " Z ";

        // Set the coordinates of shape
       shapeSelection.attr('d', makeD);
    } else if(  shapeType === "hexagon"  ) {
        /*중심좌표*/
        var distanceX = disX;
        var distanceY =  disY;

       // 좌표 간의 거리를 구하는 공식 직각삼각형 빗변 계산 공식 (피타고라스 정리)
       var cr = Math.abs(Math.sqrt(Math.pow(Math.abs(distanceX),2)+Math.pow(Math.abs(distanceY),2)));
       var startX = Math.sqrt((2 * cr * cr),2);
       var disX = startX / 2

       /*사용 좌표 중심좌표 */
       var x0  = cx - startX;
       var x1  = x0 + disX;
       var x2  = x1 + disX;
       var x3  = x2 + disX;
       var y0  = cy - distanceY;
       var y1  = cy;
       var y2  = y1+distanceY;

       var top1     = x1 + "," + y2 + " ";
       var top2     = x2 + "," + y2 + " ";
       var bottom1  = x1 + "," + y0 + " ";
       var bottom2  = x2 + "," + y0 + " ";
       var left     = x0 + "," + y1 + " ";
       var right    = x3 + "," + y1 + " ";

       // 2. Update the attributes of the Points
       var makeP = left + top1 + top2 + right + bottom2 + bottom1;

       shapeSelection.attr('points', makeP);
    } else if(  shapeType === "milsymbol"  ) {
        shapeSelection
            .attr("x", Math.round(x1))
            .attr("y", Math.round(y1));
    }

    var rotateInfo =  d3Canvas.svg.selectAll("#"+shapeId).node().getAttribute('transform');
    // 로테이션 정보 생성(기준좌표 변경)
    var makeRotateForm = null;

    // 회전 여부에 따른 회전 적용
    // 회전 여부에 따른 회전 적용
    if( rotateInfo === undefined || rotateInfo === null ){
        //로테이션 정의가 없을때 정의
    } else {
        var tempInfo =  rotateInfo.split(',');

        makeRotateForm = tempInfo[0] + ','+cx+','+cy+')';
        shapeSelection.attr('transform', makeRotateForm);
    }
}

/* 현재까지 모든 작업 저장 (dma_insertInfoOvlay, data:json,dma_ovlayDrawData)
 * 1) 현재 캔버스에 그려진 모든 정보를 객체화 하여 전달
 * 2)
 * 3)
 * */
let saveCanvasData = function(canvasId) {
    var self = canvasId;
    var canvasShapes = self.Shapes.shape;
    var transformInfo = null;

    var shapeData = new Array();

    // 공통속성 임시 저장
    stmp.svgCommonAttr = comAttr;

    // 2. 전체 객체 서칭
    for( let [key, value] of Object.entries( canvasShapes ) ){
        transformInfo = null;

        //저장 객체 데이터 만들기( 전송용 )
        var _shapeId = value.id;
        var shapeElement = self.svg.selectAll("."+_shapeId).node();
        var shapeObjHtml = shapeElement.outerHTML.toString();
        var shapeMeta = self.getBbox(_shapeId);
        var result = null;
        var rotateInfo = null;

        // 서버에 보낼 도형들의 객체 정보 (OVS의 정보가 대부분)
        var saveShapesInfo = new Object();
        // 서버에 보낼 도형 모양이름 (OVS 의 st 속성)
        var saveShapeType = numberRemove(_shapeId);
        // 서버에 보낼 좌표 ( TYPE_GEO의 pt 속성)
        var saveCoords = null;
        // 서버에 보낼 속성정보 ( 색, 채움, 문자열 등등 )
        var saveAttrInfo = self.Shapes.shape[_shapeId].attr;

        // 이동 및 회전 체크를 위한 데이터 가져오기
        transformInfo = self.svg.selectAll('#'+_shapeId).node().getAttribute('transform');

        //화면좌표 => 경위도 변환 bbox  : point1(x1, y1), point2(x2, y2)
        var point1 = {};
        var point2 = {};
        point1.x = shapeMeta.x;
        point1.y = shapeMeta.y;
        point2.x = shapeMeta.x + shapeMeta.w;
        point2.y = shapeMeta.y + shapeMeta.h;
/*
        if( transformInfo !== null ){
            // 도형이 이동한 상태이면 중심좌표를 translate 한 위치적용
            var transDivid =transformInfo.split(' ');

            // 이동 및 회전 정보를 체크하여 가져오기
            transDivid.forEach(
                function(d, i){
                    if( d.indexOf('translate') > -1) {
                        var transCxy = self.convertTransToCoords( transformInfo );
                        point1.x += Number(transCxy[0]);
                        point1.y += Number(transCxy[1]);
                        point2.x += Number(transCxy[0]);
                        point2.y += Number(transCxy[1]);
                    }

                    if( d.indexOf('rotate') > -1) {
                        rotateInfo = d;
                    }
                }
            )
            //중심좌표 적용 했으므로 로테이션 정보만 남긴다.
            //self.svg.selectAll('#'+_shapeId).attr("transform", rotateInfo);
            if( rotateInfo !== null ){

            }
        }*/
        result = stmp.mapObject.unproject(point1);
        // 도형의 geo 좌표       TODO : 도형에 따라 다르게 적용되도록 변경 예정 현재 (bbox 형태의 좌표를 필요한 도형만 처리가능)
        var dLat = result.lat;
        var dLng = result.lng;
        saveCoords = dLng + "," + dLat;

        result = stmp.mapObject.unproject(point2);
        dLat = result.lat;
        dLng = result.lng;

        saveCoords += "|"+dLng + "," + dLat;

        console.log("Savecoords");
        console.log(saveCoords);


        // save 객체에 정도 저장
        saveShapesInfo['shapeType'] = saveShapeType;
        saveShapesInfo['coords'] = saveCoords;
        saveShapesInfo['attrInfo'] = saveAttrInfo;

        shapeData.push(saveShapesInfo);
    }
    //결과 생성
    stmp.saveShapeData = shapeData;
};

//점 타입 변경
//pointTypeRect       : 1 //점 부호를 사각형으로 도시
//pointTypeCircle    : 2 //점 부호를 원으로 도시     (다른 요소를 사용 주의!!)
//pointTypeDia       : 3 //점 부호를 마름모로 도시
//pointTypeTri       : 4 //점 부호를 정삼각형으로 도시
//pointTypeInvTri    : 5 //점 부호를 역삼각형으로 도시
//주의 : 원 타입만 circle 요소를 사용하고 그외에는 전부 polygon 을 사용하므로 해당처리시
//약간의 수정사항이 발생(요소 제거후 생성)

SVGCanvas.prototype.changePointType = function( shapeId, pointShape, size ) {
     var self = this;
     //도형 중심좌표 구하기
     var _centerXY = self.getCenterCoord(shapeId);
     //도형 중심 좌표
     var _centerX = _centerXY[0];
     var _centerY = _centerXY[1];
     var _radius = size/2;
     var polCnt = self.svg.selectAll('.'+shapeId).selectAll('polygon').size();
     var cirCnt = self.svg.selectAll('.'+shapeId).selectAll('circle').size();

     // 요소 재생성 (원형 타입에 의해 초기화 필요)
     if( pointShape === enumType.enum_pointShape.pointTypeCircle ){
         //원형 타입일 경우(circle)
         if( polCnt > 0 ) {      //polygon 존재 하면 삭제
             self.svg.selectAll('.'+shapeId).selectAll('polygon').node().remove();
         }

         //circle이 없을경우 새로 생성
         if( cirCnt === 0 ) {
             var nodeG = self.svg.selectAll('.' + shapeId).node();
             var makeCircle = nodeG.appendChild(
                 addSvgElementFromJson({
                     element: 'circle',
                     attr: {
                         id: shapeId,
                         cx: _centerX,
                         cy: _centerY,
                         r: _radius,
                         stroke: 'yellow',
                     }
                 })
             );
         }
     } else {
         //원형 이외의 경우(polygon)
         if( cirCnt > 0 ) {      //circle 존재 하면 삭제
             self.svg.selectAll('.'+shapeId).selectAll('circle').node().remove();
         }

         //폴리곤이 없을경우 새로 만든다.
         if( polCnt === 0 ){
             var nodeG = self.svg.selectAll('.'+shapeId).node();
             var makeCircle = nodeG.appendChild(
                 addSvgElementFromJson({
                     element: 'polygon',
                     attr: {
                         id: shapeId,
                         points: ' ',
                     }
                 })
             );
             // 스타일 처리 고민
         }
     }

     if( pointShape === enumType.enum_pointShape.pointTypeRect ){
         //점 부호를 사각형으로 도시
         // 사각형의 4개 좌표
         var pointA = (_centerX-_radius) + "," + (_centerY-_radius) + " ";
         var pointB = (_centerX+_radius) + "," + (_centerY-_radius) + " ";
         var pointC = (_centerX+_radius) + "," + (_centerY+_radius) + " ";
         var pointD = (_centerX-_radius) + "," + (_centerY+_radius) + " ";

         var points = pointA + pointB + pointC + pointD;
         self.updateAttrObj(shapeId, "points", points);
     } else if (  pointShape === enumType.enum_pointShape.pointTypeCircle ){
         //점 부호를 원으로 도시
         var nodeG = self.svg.selectAll('.'+shapeId).node();
         var makeCircle = nodeG.appendChild(
             addSvgElementFromJson({
                 element: 'circle',
                 attr: {
                     id: shapeId,
                     fill: 'none',
                     cx: _centerX,
                     cy: _centerY,
                     r: _radius,
                     stroke: 'yellow',
                 }
             })
         );
     } else if (  pointShape === enumType.enum_pointShape.pointTypeDia ){
         //점 부호를 마름모로 도시
         // 마름모의 4개 좌표
         var pointA = (_centerX) + "," + (_centerY-_radius) + " ";
         var pointB = (_centerX+_radius) + "," + (_centerY) + " ";
         var pointC = (_centerX) + "," + (_centerY+_radius) + " ";
         var pointD = (_centerX-_radius) + "," + (_centerY) + " ";

         var points = pointA + pointB + pointC + pointD;
         self.updateAttrObj(shapeId, "points", points);
     } else if (  pointShape === enumType.enum_pointShape.pointTypeTri ){
         //점 부호를 정삼각형으로 도시
         // 정삼각형 세 꼭지점
         var distX = Math.abs(Math.cos(Math.PI/12)) * _radius;
         var distY = Math.abs(Math.sin(Math.PI/12)) * _radius;
         var pointA = _centerX + "," + (_centerY-_radius) + " ";
         var pointB = (_centerX - distX) + ","
             + (_centerY + distY) + " ";
         var pointC = (_centerX + distX) + ","
             + (_centerY + distY) + " ";

         var points = pointA + pointB + pointC;
         self.updateAttrObj(shapeId, "points", points);
     } else if ( pointShape === enumType.enum_pointShape.pointTypeInvTri ){
         //점 부호를 역삼각형으로 도시
         // 역삼각형 세 꼭지점
         var distX = Math.abs(Math.cos(Math.PI/12)) * _radius;
         var distY = Math.abs(Math.sin(Math.PI/12)) * _radius;

         console.log(distX);
         console.log(distY);

         var pointA = (_centerX - distX) + "," + (_centerY - distY) + " ";
         var pointB = (_centerX + distX) + "," + (_centerY - distY) + " ";
         var pointC = _centerX + "," + (_centerY+_radius) + " ";
         var points = pointA + pointB + pointC;
         self.updateAttrObj(shapeId, "points", points);
     }

     self.setActive(shapeId);
};

//도형의 bbox를 이용하여 중점 좌표 가져오기
SVGCanvas.prototype.getCenterCoord = function( shapeId ) {
     var self = this;
     var sBB = self.svg.selectAll("."+shapeId).node().getBBox();
     var p = {
         x: sBB.x,
         y: sBB.y,
         w: sBB.width,
         h: sBB.height,
         cx: (sBB.x + sBB.width/2),
         cy: (sBB.y + sBB.height/2)
     };

     return [p.cx, p.cy];
};

//2개의 좌표 bbox 정보생성
SVGCanvas.prototype.get2CoordBbox = function( point1, point2 ) {
     var self = this;
     var x1 = Number(point1[0]);
     var y1 = Number(point1[1]);
     var x2 = Number(point2[0]);
     var y2 = Number(point2[1]);
     var p = {
         x: x1,
         y: y1,
         w: Math.abs(x1-x2),
         h: Math.abs(y1-y2),
         cx: (x1 + Number(Math.abs(x1-x2)/2)),
         cy: (y1 + Number(Math.abs(y1-y2)/2))
     };

     return p;
};

//저장된 투명도 파일 받아와서 처리
//서버로 부터 xml로 부터 1차 파싱된 객체를 받아와서 2차 가공
//TestData:[{"shapeType":"rect","coords":"100,200|200,300", attrData:{}}]
SVGCanvas.prototype.xmlDataToSvg = function( param ) {
     var self = this;
//     var _shapeData = '[' +
//         '{"shapeType":"rect","coords":"100,200|200,300","attrData":{"type_lp":{"lc":"white"}}}' +
//         ',{"shapeType":"rect","coords":"300,400|400,500","attrData":{"type_fp":{"ft":"0","fc":"red"}}}' +
//         ',{"shapeType":"circle","coords":"800,200|900,300"}' +
//         ',{"shapeType":"ellipse","coords":"600,100|700,150"}' +
//         ']';
     var _shapeData = param;

//     console.log("전송된 xml 데이터");
//     console.log(_shapeData);
     var _jsonObj = JSON.parse(_shapeData);

//     console.log(전송된 xml 데이터 파싱된 객체);
//     console.log(_jsonObj);
     var svgMain = self.svg.node();
     //var _shapeData = param;

     //symbolInfo, data
     for( let [key, value] of Object.entries( _jsonObj ) ) {
         var _shapeType = null;
         var _paramCoords = null;
         var _shapeId = null;
         var _shapeSelection = null;
         if( value.hasOwnProperty('shapeType') ){
             _shapeType = value.shapeType;
         }
         if( value.hasOwnProperty('coords') ){
             _paramCoords = value.coords;
         }

         //ToDo. 객체데이터 저장부분 추가!!  2020.03.01
         //circle 는 존재 하지 않는다 ellipse 형태로 처리

/*         var point = {};
         var point1 = [];
         var point2 = [];
         var result = null;*/
         //도형별 처리    ====================================
         if( _shapeType === "rect" ){
/*             var temp = _paramCoords.split('|');
             var temp1 = temp[0].split(',');

             //좌표 변환 경위도 => 화면좌표        lon, lat  순서 주의!!!!!!!!
             point.lon = temp1[0];
             point.lat = temp1[1];
             //경위도 => 화면좌표 변환
             result = stmp.mapObject.project(point);
             point1[0] = result.x;
             point1[1] = result.y;

             var temp2 = temp[1].split(',');

             //좌표 변환 경위도 => 화면좌표       lon, lat  순서 주의!!!!!!!!
             point.lon = temp2[0];
             point.lat = temp2[1];
             //경위도 => 화면좌표 변환
             result = stmp.mapObject.project(point);
             point2[0] = result.x;
             point2[1] = result.y;

             var cvtPoint = cvtBboxCoordMaptoPix(_paramCoords);
             console.log(cvtPoint);
             console.log(point1);
             console.log(point2);*/

             //var pointBbox = self.get2CoordBbox(point1, point2);

             // db에서 가져온 좌표를 화면 좌표로 변환
             var cvtPoint = cvtBboxCoordMaptoPix(_paramCoords);
             var pointBbox = self.get2CoordBbox(cvtPoint.point1, cvtPoint.point2);

             _shapeId = idMaker(self,"rect");

             var elemG = svgMain.appendChild(
                 addSvgElementFromJson({
                     element: 'g',
                     attr: {
                         class: ('g-rect ' + _shapeId)
                     }
                 })
             );

             _shapeSelection = elemG.appendChild(
                 addSvgElementFromJson({
                     element: _shapeType,
                     attr: {
                         id: _shapeId,
                         x: pointBbox.x,
                         y: pointBbox.y,
                         width: pointBbox.w,
                         height: pointBbox.h
                     }
                 })
             );

             self.svg.selectAll('#'+_shapeId)
              .style('stroke', comAttr.commonAttr.stroke)
              .style('stroke-width', comAttr.commonAttr.strokeWidth)
              .style('fill', comAttr.commonAttr.color)
              .on("click",
                 function (d){
                     self.selectShape(self.Rect.id);
                 }
               )
               .on("contextmenu",
                   function (d){
                        //우클릭시 서브 메뉴바 띄우기 예제
                        d3.contextMenu(menu, self, self.Rect.id);
                    }
               );

             //객체 기록
             self.Rect = {};
             self.Rect.id = _shapeId;
             self.Rect.x = pointBbox.x;
             self.Rect.y = pointBbox.y;
             self.Rect.r = _shapeSelection;
             self.Rect.width = pointBbox.w,
             self.Rect.height = pointBbox.h,
             self.Rect.cx =  (self.Rect.x+self.Rect.width) / 2;
             self.Rect.cy =  (self.Rect.y+self.Rect.height) / 2;
             self.Shapes.shape[self.Rect.id] =  self.Rect;
         }

         if( _shapeType === "circle" ){
             var temp1 = _paramCoords.split('|');
             var point1 = temp1[0].split(',');
             var point2 = temp1[1].split(',');
             var pointBbox = self.get2CoordBbox(point1, point2);
             _shapeId = idMaker(self,"circle");

             var elemG = svgMain.appendChild(
                 addSvgElementFromJson({
                     element: 'g',
                     attr: {
                         class: ('g-circle ' + _shapeId)
                     }
                 })
             );

             _shapeSelection = elemG.appendChild(
                 addSvgElementFromJson({
                     element: _shapeType,
                     attr: {
                         id: _shapeId,
                         cx: pointBbox.cx,
                         cy: pointBbox.cy,
                         r: pointBbox.w/2
                     }
                 })
             );

             //객체 데이터 저장
             self.Circle = {};
             self.Circle.id = _shapeId;
             self.Circle.cx = pointBbox.cx;
             self.Circle.cy = pointBbox.cy;
             self.Circle.r = _shapeSelection;
             self.Circle.distance = pointBbox.w/2;        //반지름
             self.Shapes.shape[self.Circle.id] =  self.Circle;
         }

         if( _shapeType === "ellipse" ){
             debugger;
             // db에서 가져온 좌표를 화면 좌표로 변환
             var cvtPoint = cvtBboxCoordMaptoPix(_paramCoords);
             var pointBbox = self.get2CoordBbox(cvtPoint.point1, cvtPoint.point2);

             _shapeId = idMaker(self,"ellipse");

             var elemG = svgMain.appendChild(
                 addSvgElementFromJson({
                     element: 'g',
                     attr: {
                         class: ('g-ellipse ' + _shapeId)
                     }
                 })
             );

             _shapeSelection = elemG.appendChild(
                 addSvgElementFromJson({
                     element: _shapeType,
                     attr: {
                         id: _shapeId,
                         cx: pointBbox.cx,
                         cy: pointBbox.cy,
                         rx: pointBbox.w/2,
                         ry: pointBbox.h/2
                     }
                 })
             );
/*
             self.svg.selectAll('#'+_shapeId)
              .style('stroke', comAttr.commonAttr.stroke)
              .style('stroke-width', comAttr.commonAttr.strokeWidth)
              .style('fill', comAttr.commonAttr.color)
              .on("click",
                 function (d){
                     self.selectShape(self.Rect.id);
                 }
               )
               .on("contextmenu",
                   function (d){
                        //우클릭시 서브 메뉴바 띄우기 예제
                        d3.contextMenu(menu, self, self.Rect.id);
                    }
               );
             */
             //객체 데이터 저장
             self.Ellipse = {};
             self.Ellipse.id = _shapeId;
             self.Ellipse.cx = pointBbox.cx;
             self.Ellipse.cy = pointBbox.cy;
             self.Ellipse.rx = pointBbox.w/2;
             self.Ellipse.ry = pointBbox.h/2;
             self.Shapes.shape[self.Ellipse.id] =  self.Ellipse;
         }

         comAttr.svgShapeNum[_shapeType] += 1;
         //==================================== 도형별 처리

         //속성 처리
         if( value.hasOwnProperty('attrData') ){
             updateShapeAttrs( value.attrData, _shapeId );
         }
     }
}

//SVG 종료 함수 (편집 종료 및 편집레이어 변경시)
SVGCanvas.prototype.closeCanvas = function( ) {
    var self = this;
    //각종 공통변수들 초기화
    /******************  투명도 관련 공통 객체 모음(공통변수에서 발췌 cfsiStmpCommon.js) ********************/
    stmp.mapToCanvasMilSymbol = {};
    //투명도 전환여부
    stmp.overlayYn = '';
    //투명도 편집 여부
    stmp.drawMode = false;
    //투명도 캔버스 ID
    stmp.d3Canvas = null;
    //군대부호 정보(그려진 부호 편집을 위해)
    stmp.milSymEditOjb = null;
    //투명도 편집중 맵이동 여부 체크
    stmp.editingToMoveMapYn = false;
    // 공통속성 오브젝트 저장
    stmp.svgCommonAttr = null;
    //  서버 저장용
    stmp.saveShapeData = null;
    /*****************************************************************************************************************/
    //기본 속성 객체
    comAttr = null;
    d3Canvas = null;

    //현재 사용중인 캔버스 삭제
    self.svg.remove();
}

//  127.61616516113753,37.16570373791254|128.22933959961318,37.01343411032141 형태의 2개의 좌표
let cvtBboxCoordMaptoPix = function( mapBboxData ) {
    debugger;
    var point = {};
    var point1 = [];
    var point2 = [];
    var resultPoint = {};
    var temp = mapBboxData.split('|');
    var temp1 = temp[0].split(',');

    //좌표 변환 경위도 => 화면좌표        lon, lat  순서 주의!!!!!!!!
    point.lon = temp1[0];
    point.lat = temp1[1];
    //경위도 => 화면좌표 변환
    var result = stmp.mapObject.project(point);
    point1[0] = result.x;
    point1[1] = result.y;

    var temp2 = temp[1].split(',');

    //좌표 변환 경위도 => 화면좌표       lon, lat  순서 주의!!!!!!!!
    point.lon = temp2[0];
    point.lat = temp2[1];
    //경위도 => 화면좌표 변환
    result = stmp.mapObject.project(point);
    point2[0] = result.x;
    point2[1] = result.y;

    //결과 세팅
    resultPoint['point1'] = point1;
    resultPoint['point2'] = point2;

    return resultPoint;
};
