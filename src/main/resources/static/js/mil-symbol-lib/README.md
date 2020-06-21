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

## 필수교체파일

    cfsiStmp2DMap.js - 기존 군대부호 관련 함수 milSymbolLoader.js 로 모두 이동 되었습니다.
    cfsiStmp3DMap.js
    cfsiStmpCommon.js
