$(function () {
    $('[data-toggle="tooltip"]').tooltip()
});

function onmouseenter_server_status(center){
    // AJAX 서버 요청 : 서버 상태 호출

    $("#" + event.target.id)
        .attr('data-original-title', `
            <p id="server_basic" class="text-left" style="color: white; overflow-x: hidden;">
                전체 용량 : <span>1.00</span> / <span>9876.54</span> GB <br/>
                메모리 용량 : <span>4.11</span> / <span>80.32</span> GB <br/>
                CPU 점유율 : <span>76</span> % <br/>
                서버 커넥션 Pool : <span>5</span>
            </p>
            <p id="server_services" class="text-left" style="color: white; overflow-x: hidden;">
                MapServer 상태 : <i class="fas fa-check"></i> <br/> 
                MapProxy 상태 : <i class="fas fa-check"></i> <br/>
                Terrain Builder 상태 : <i class="fas fa-check"></i> <br/>
                Mapnik 상태 : <i class="fas fa-check"></i> <br/>
                Tegola 상태 : <i class="fas fa-check"></i> <br/>
                PostgreSQL 상태 : <i class="fas fa-check"></i> <br/>
                H2 Database 상태 : <i class="fas fa-times"></i>
            </p>
        `)
        .tooltip('show');
}

function onmouseenter_sync_status(center){
    // AJAX 서버 요청 : 동기화 상태 호출

    $("#" + event.target.id)
        .attr('data-original-title', `
            <p id="sync_basic" class="text-left" style="color: white; overflow-x: hidden;">
                최근 진행 일자<br/>
                - <span>2020-04-22 10:00:00</span><br/>
                최근 동기화 파일<br/>
                - <span>A.tif 이외 10 개</span><br/>
                동기화 기본 디렉토리<br/>
                - <span>/data/center/sync</span>
            </p>
            <p id="sync_services" class="text-left" style="color: white; overflow-x: hidden;">
                PGPool 2 상태 : <i class="fas fa-check"></i> <br/> 
                Syncthing 상태 : <i class="fas fa-check"></i> <br/>
                타 체계 파일 동기화 준비 : <i class="fas fa-check"></i> <br/>
                타 체계 DB 동기화 준비 : <i class="fas fa-check"></i> <br/>
                타 센터 파일 동기화 준비 : <i class="fas fa-check"></i> <br/>
                타 센터 DB 동기화 준비 : <i class="fas fa-check"></i> <br/>
            </p>
        `)
        .tooltip('show');
}