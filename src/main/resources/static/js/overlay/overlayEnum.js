/*  enum 속성
    1)점 모양 종류       - 점부호 모양, 선 끝 모양 정의에 사용하는 도형 종류
    2)수평,수직,좌우,상하 - 정렬 위치
    3)색상 설정 방법     - 전술부호 색상 설정 방식
    4)패턴 반복 방식     - 패턴 채움 방식
*/

//enum 속성 정리
var enumType = {
    //원호 선 모양 원호 모양 구분 코드
    enum_arcType :  {
        arcTypeArc      : 1 //원호 부호를 호로 도시
        ,arcTypeChord   : 2 //원호 부호를 현으로 도시
        ,arcTypePie     : 3 //원호 부호를 부채꼴로 도시
    }

    //선적용 좌표 종류 구분 코드
    ,enum_coordType : {
        geographic  : 0 //선이 치우치지 않음
        ,screen     : 1 //선이 안쪽으로 치우침
    }

    //면 채움 방법
    ,enum_fillType  : {
        none                : -1    //채우지 않음
        ,solidcolor         : 0     //단색 채움
        ,hatchfill          : 1     //음영 채움
        ,pathgradient       : 3     //경로형 그라데이션 채움
        ,lineargradation    : 4     //선형 그라데이션 채움
    }

    //음영 모양
    ,enum_hatchStyle :{
        "0" : {
                id           : 'horizontal',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
         },
        "1" : {
                id           : 'vertical',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10 M9,11 12,-2' stroke='blue' stroke-width='1' />",
            },
        "2" : {
                id           : 'forwardDiagonal',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "3" : {
                id           : 'backwardDiagonal',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "4" : {
                id           : 'cross',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "5" : {
                id           : 'diagonalCross',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "6" : {
                id           : 'percent05',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "7" : {
                id           : 'percent10',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "8" : {
                id           : 'percent20',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "9" : {
                id           : 'percent25',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "10" : {
                id           : 'percent30',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "11" : {
                id           : 'percent40',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "12" : {
                id           : 'percent50',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "13" : {
                id           : 'percent60',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "14" : {
                id           : 'percent70',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "15" : {
                id           : 'percent75',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "16" : {
                id           : 'percent80',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "17" : {
                id           : 'percent90',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "18" : {
                id           : 'lightDownwardDiagonal',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "19" : {
                id           : 'lightUpwardDiagonal',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "20" : {
                id           : 'darkDownwardDiagonal',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "21" : {
                id           : 'darkUpwardDiagonal',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "22" : {
                id           : 'wideDownwardDiagonal',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "23" : {
                id           : 'wideUpwardDiagonal',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "24" : {
                id           : 'lightVertical',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "25" : {
                id           : 'lightHorizontal',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "26" : {
                id           : 'narrowVertical',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "27" : {
                id           : 'narrowHorizontal',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "28" : {
                id           : 'darkVertical',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "29" : {
                id           : 'darkHorizontal',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "30" : {
                id           : 'dashedDownwardDiagonal',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "31" : {
                id           : 'dashedUpwardDiagonal',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "32" : {
                id           : 'dashedHorizontal',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "33" : {
                id           : 'dashedVertical',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "34" : {
                id           : 'smallConfetti',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "35" : {
                id           : 'largeConfetti',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "36" : {
                id           : 'zigZag',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "37" : {
                id           : 'wave',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "38" : {
                id           : 'diagonalBrick',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "39" : {
                id           : 'horizontalBrick',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "40" : {
                id           : 'weave',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "41" : {
                id           : 'plaid',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "42" : {
                id           : 'divot',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "43" : {
                id           : 'dottedGrid',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "44" : {
                id           : 'dottedDiamond',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "45" : {
                id           : 'shingle',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "46" : {
                id           : 'trellis',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "47" : {
                id           : 'sphere',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "48" : {
                id           : 'smallGrid',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "49" : {
                id           : 'smallCheckerBoard',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "50" : {
                id           : 'largeCheckerBoard',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "51" : {
                id           : 'outlinedDiamond',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
        "52" : {
                id           : 'solidDiamond',
                pattern   : "<rect width='10' height='10' fill='none' /><path d='M-1,1 l2,-2 M0,10 l10,-10' stroke='blue' stroke-width='1' />",
            },
    }
    //선 치우침 상태
    , enum_lineAlignment : {
        center : 0  //선이 치우치지 않음
        ,inset   : 1    //선이 안쪽으로 치우침
    }
    //선 끝 모양 구분 코드
    , enum_lineCap       : {
        "0" : {
                id      : 'lineCapNone',
                pa1th    : '',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10'
                },          //없음,
        "1" : {
                id      : 'lineCapArrow',
                path    : 'M 1,0 -5,3 -5,2 -1,0 -5,-2 -5,-3 1,0',
                //path    : 'M -1,0 5,3 5,2 1,0 5,-2 5,-3 -1,0',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10'
              },          //화살표
        "2" : {
                id      : 'lineCapArrow_L',
                path    : 'M 2,-0.5 0,0.5 -5,3 -5,2 0,-0.5',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10'
              },          //화살표(좌)
        "3" : {
                id      : 'lineCapArrow_R',
                path    : 'M 2,0.5 0,-0.5 -5,-3 -5,-2 0,0.5',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10'
              },          //화살표(우)
        "4" : {
                id      : 'lineCapTail',
                path    : 'M -1,0 5,3 5,2 1,0 5,-2 5,-3 -1,0',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10'
              },          //화살꼬리
        "5" : {
                id      : 'lineCapTail_L',
                path    : 'M -2,0.5 0,-0.5 5,-3 5,-2 0,0.5',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10'
              },          //화살꼬리(좌)
        "6" : {
                id      : 'lineCapTail_R',
                path    : 'M -2,-0.5 0,0.5 5,3 5,2 0,-0.5',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10'
              },          //화살꼬리(우)
        "7" : {
                id      : 'lineCapTail_F',
                path    : 'M -0.5,0.5 5,3 5,-3 -0.5,-0.5',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10'
              },          //화살꼬리채움
        "8" : {
                id      : 'lineCapTail_LF',
                path    : 'M -0.5,-0.5 5,-3 5,0.5 0,0.5M 0, 0 m 5, -5 L 5,0 L 5,5 Z',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10'
              },          //화살꼬리채움(좌)
        "9" : {
                id      : 'lineCapTail_RF',
                path    : 'M -0.5,0.5 5,3 5,-0.5 0,-0.5',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10'
              },          //화살꼬리채움(우)
        "10" : {
                id      : 'lineCapTent_L',
                path    : 'M -1,0.5 2,-3 5,0.5 4,0.5 2,-2 0,0.5',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10'
              },          //텐트(좌)
        "11" : {
                id      : 'lineCapTent_R',
                path    : 'M -1,-0.5 2,3 5,-0.5 4,-0.5 2,2 0,-0.5',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10'
              },          //텐트(우)
        "12" : {
                id      : 'lineCapTent_LF',
                path    : 'M 1,0.5 -2,-3 -5,0.5',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10'
              },          //텐트채움(좌)
        "13" : {
                id      : 'lineCapTent_RF',
                path    : 'M 1,-0.5 -2,3 -5,-0.5',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10'
              },          //텐트채움(우)
        "14" : {
                id      : 'lineCapSlash_L',
                path    : 'M 2.75,2 2,2.75 -2.75,-2 -2,-2.75',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10'
              },          //사선
        "15" : {
                id      : 'lineCapSlash_R',
                path    : 'M 2.75,-2 2,-2.75 -2.75,2 -2,2.75',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10'
              },          //역사선
        "16" : {
                id      : 'lineCapCross',
                path    : 'M 0.75,0 2.75,2 2,2.75 0,0.75 -2,2.75 -2.75,2 -0.75,0 -2.75,-2 -2,-2.75 0,-0.75 2,-2.75 2.75,-2',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10'
              },          //가위표
        "17" : {
                id      : 'lineCapTriangle',
                path    : 'M 4,0 0,2 0,0 -0.5,0 -0.5,3 5,0 -0.5,-3 -0.5,0 0,0 0,-2 ',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10'
              },          //삼각형
        "18" : {
                id      : 'lineCapTriangle_F',
                path    : 'M -0.5,3 5,0 -0.5,-3',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10'
              },          //삼각형채움
        "19" : {
                id      : 'lineCapTriangle_L',
                path    : 'M 3.25,0 0,-2 0,0 -0.5,0 -0.5,-3 5,0.5 -0.5,0.5 -0.5,0',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10'
              },          //삼각형(좌)
        "20" : {
                id      : 'lineCapTriangle_LF',
                path    : 'M -0.5,-3 5,0.5 -0.5,0.5',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10'
              },          //삼각형채움(좌)
        "21" : {
                id      : 'lineCapTriangle_R',
                path    : 'M 3.25,0 0,2 0,0 -0.5,0 -0.5,3 5,-0.5 -0.5,-0.5 -0.5,0',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10'
              },          //삼각형(우)
        "22" : {
                id      : 'lineCapTriangle_RF',
                path    : 'M -0.5,3 5,-0.5 -0.5,-0.5',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10'
              },          //삼각형채움(우)
        "23" : {
                id      : 'lineCapRectangle',
                path    : 'M 0,2 4,2 4,-2 0,-2 0,2',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10',
              },          //사각형
        "24" : {
                id      : 'lineCapRectangle_F',
                path    : 'M 1,3 -5,3 -5,-3 1,-3',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10'
              },          //사각형채움
        "25" : {
                id      : 'lineCapCircle',
                path    : 'm -2.5,0 a 2.5,2.5 0,1,0 5,0 a 2.5,2.5 0,1,0 -5,0',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10'
              },          //원
        "26" : {
                id      : 'lineCapCircle_F',
                path    : 'm -5,0 a 2.5,2.5 1,1,0 5,0 a 2.5,2.5 1,1,1 0,0 Z',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10',
              },          //원채움
        "27" : {
                id      : 'lineCapDiamond',
                path    : 'M 0,0 -2.5,2.5 -5,0 -2.5,-2.5 0,0',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10'
              },          //마름모
        "28" : {
                id      : 'lineCapDiamond_F',
                path    : 'M 1,0 -2,3 -5,0 -2,-3 1,0 Z',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10',
              },          //마름모채움
        "29" : {
                id      : 'lineCapThick',
                path    : 'M 0,0 -5,2 -5,3.5 5,0 -5,-3.5 -5,-2 0,0 Z',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '10'
              },          //겹화살표
        "30" : {
                id      : 'lineCapDoubleArrow',
                path    : 'M 6,0 0,3 0,2 4,0 0,-2 0,-3 6,0 M 1,0 -5,3 -5,2 -1,0 -5,-2 -5,-3 1,0 M -1,0.5 -1,-0.5 4,-0.5 4,0.5',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '20'
              },          //이중화살표
        "31" : {
                id      : 'lineCapDoubleArrow_L',
                path    : 'M 4,0.5 0,-2 0,-3 6.5,0.5 M -1,0 -5,-2 -5,-3 1,0 M -1,0.5 -1,-0.5 4,-0.5 4,0.5',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '20'
              },          //이중화살표(좌)
        "32" : {
                id      : 'lineCapDoubleArrow_R',
                path    : 'M 6.5,-0.5 0,3 0,2 4,-0.5 M 1,0 -5,3 -5,2 -1,0 M -1,0.5 -1,-0.5 4,-0.5 4,0.5',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '20'
              },          //이중화살표(우)
        "33" : {
                id      : 'lineCapDoubleTail',
                path    : 'M -6,0 0,3 0,2 -4,0 0,-2 0,-3 -6,0 M -1,0 5,3 5,2 1,0 5,-2 5,-3 -1,0',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '20'
              },          //이중화살꼬리
        "34" : {
                id      : 'lineCapDoubleTail_L',
                path    : 'M -7,0.5 -5,-0.5 0,-3 0,-2 -5,0.5 M -2,0.5 0,-0.5 5,-3 5,-2 0,0.5',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '20'
              },          //이중화살꼬리(좌)

        "35" : {
                id      : 'lineCapDoubleTail_R',
                path    : 'M -7,-0.5 -5,0.5 0,3 0,2 -5,-0.5 M -2,-0.5 0,0.5 5,3 5,2 0,-0.5',
                viewbox : '-5 -5 10 10',
                orient   : 'auto',
                height  : '20'
              },          //이중화살꼬리(우)
    }

    //점선 사이의 선 끝 모양 구분 코드
    , enum_lineDashCap : {
        flat        : 0 //선이 치우치지 않음
        ,round      : 2 //선이 안쪽으로 치우침
        ,triangle   : 3 //선이 안쪽으로 치우침
    }

    //점선 모양 구분 코드
    /*        solid     : 0 //실선
            ,dash       : 1 //점선
            ,dot        : 2 //둥근모양 점선
            ,dashDot    : 3 //일점쇄선
            ,dashDotDot : 4 //이점쇄선
            ,custom     : 5 //사용자 지정 점선 패턴 적용
    */
    , enum_lineDashStyle : {
            0 : ['0','0'],
            1 : [ 'stroke-dasharray', '5.5' ],
            2 : [ 'stroke-dasharray', '5.5' ],
            3 : [ 'stroke-dasharray', '10,6,3,6' ],
            4 : [ 'stroke-dasharray', '10,5,3,6,3,5' ],
            5 : ['0','0']
        }

    //운용조건 표시 위치 구분 코드
    , enum_opPos : {
        opPosNone       : 0 //미표시
        ,opPosCenter    : 1 //중단
        ,opPosBottom    : 2 //하단
    }

    //투명도 부호 종류 구분 코드
    , enum_overlaySymbolType : {
        olsTypeGroup    : 0 //그룹화된 부호
        ,olsTypePoint   : 1 //점 모양의 투명도 부호
        ,olsTypeRect    : 2 //사각형 모양의 투명도 부호
        ,olsTypeEllipse : 3 //타원 모양의 투명도 부호
        ,olsTypeArc     : 4 //원호 모양의 투명도 부호
        ,olsTypePolyline: 5 //다각형 모양의 투명도 부호
        ,olsTypeTacSym  : 6 //한국군군대부호 표준에 등록된 군대부호를 사용하는 투명도 부호

    }

    //점 부호(Point)의 점 모양 구분 코드
    , enum_pointShape : {
        pointTypeRect       : 0 //점 부호를 사각형으로 도시
        ,pointTypeCircle    : 1 //점 부호를 원으로 도시
        ,pointTypeDia       : 2 //점 부호를 마름모로 도시
        ,pointTypeTri       : 3 //점 부호를 정삼각형으로 도시
        ,pointTypeInvTri    : 4 //점 부호를 역삼각형으로 도시
    }

    //다각선의 직선/곡선 종류
    , enum_polylineType : {
        polylineTypeStraight    : 0 //점 부호를 사각형으로 도시
        ,polylineTypeSpline     : 1 //점 부호를 원으로 도시
        ,polylineTypeBezier     : 2 //점 부호를 마름모로 도시
    }

    //비밀등급
    , enum_security : {
    //  0   평문
    //  1   대외비
    //  2   III급
    //  3   II급
    //  4   I급
    }

    //점부호 모양, 선 끝 모양 정의에 사용하는 도형 종류
    , enum_shapeType : {
        rectangle   : 0 //사각형 도형
        ,ellipse    : 1 //타원 도형
        ,polygon    : 2 //다각형 도형
    }

    //정렬 위치
    , enum_alignment : {
        near    : 0 //가까움
        ,center : 1 //중앙
        ,far    : 2 //떨어짐
    }

    //전술부호 색상 설정 방식
    , enum_tacsymColorMode : {
        autoAffiliation : 0 //가까움
        ,userAffiliation: 1 //중앙
        ,userFixedIcon  : 2 //떨어짐
    }

    //패턴 채움 방식
    , enum_textureWrapMode : {
        tile        : 0 //반복
        ,tileFlipX  : 1 //가로축 대칭
        ,tileFlipY  : 2 //세로축 대칭
        ,tileFlipXY : 3 //가로,세로축 대칭
        ,clamp      : 4 //중앙
    }
}