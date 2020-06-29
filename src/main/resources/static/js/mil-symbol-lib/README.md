# 군대부호

## 관련 라이브러리

    [milSymbolLoader.js] 파일 1개만 import 합니다.
    
    따로 환경세팅은 안해도 되도록 구성하였습니다.
    [milSymbolLoader] 객체를 사용합니다. 
    ex) <script th:src="@{/js/commons/milSymbolLoader.js}"></script>
    
    - Folder : /src/main/resources/static/js/mil-symbol-lib
    - Files : "milsymbol.js", "savm-bc.js", "symbol.js", "symbolCommon.js", "graphics.js", "html2canvas.min.js", "mb-milSymbol.js", "cmm-milSymbol.js"
    - Import : /src/main/resources/static/js/commons/milSymbolLoader.js
    
    
## 초기화 스크립트

    지도 변경 (2D <> 3D) 시 호출
    
    - 호출위치1 : cfsiStmp2DMap.js::32LINE
    - 호출위치2 : cfsiStmp3DMap.js::45LINE

```
milSymbolLoader.init([param], [callback])
ex) milSymbolLoader.init({map : evt.target},function(){})
```

## 그리기 모드 진입 스크립트

    군대부호 그리기 시작시 호출 
   
    - 호출위치 : mb-milSymbol.js

```
var options = stmp.getMilsymbolOptions()
milSymbolLoader.drawMilsymbol(options)
```

## 직접그리기

    @param1 sidc
    @param2 points [x,y] or [[x,y]] or [[x1,y1],[x2,y2],[x3,y3]]
    ex)
        milSymbolLoader.draw("SFG*IGA---H****",[127.02712249755984, 36.992322299618536])
        milSymbolLoader.draw("SFG*IGA---H****",[[127.17818450927143, 37.06413332353205]])
        milSymbolLoader.draw("GFT-K-----****X",[[126.85958099365331,37.07673435836169],[126.9701309204122,36.99396758638515],[127.05046844482507,37.0153530751237]])

## 필수교체파일

    cfsiStmp2DMap.js - 기존 군대부호 관련 함수 milSymbolLoader.js 로 모두 이동 되었습니다.
    cfsiStmp3DMap.js
    cfsiStmpCommon.js

