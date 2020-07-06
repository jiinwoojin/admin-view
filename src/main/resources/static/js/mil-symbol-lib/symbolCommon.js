/**
* Option Data Type
 * JSON 
 * { "mil_code"+"_"+"key" : "code_kor_name", ... }
 */

var symbolDefNew ;
var selectedID;  //20200207
var bChangeID = false;

var image = document.createElement("img");
image.id = "ImageSymbol";
document.getElementById("CanvasSymbol").appendChild(image);

// check symbol function
var SymbolDefTable = armyc2.c2sd.renderer.utilities.SymbolDefTable;
var SymbolUtilities = armyc2.c2sd.renderer.utilities.SymbolUtilities;
var ms2525Bch2 = 0;
var ms2525C = 1;
var symStd = ms2525C;

//Activates tab for choosen standard
function activateTab(id){
	
	// Symbol Menu Control
	document.getElementById("2525C").className = "";
	document.getElementById('m_style').className = "";
	document.getElementById('m_value').className = "";
	document.getElementById(id).className = "active";
	
	var openDIV = 'letter';
	
	document.getElementById("letterSIDCtab").className = "";
	document.getElementById("styleSIDCtab").className = "";
	document.getElementById("valueSIDCtab").className = "";
	
	if(id != '2525C'){
		openDIV = ((id == "m_style")?"style":"letter");
		openDIV = ((id == "m_value")?"value":"style");
	}
	// Symbol Menu Control
	
	activeStandard = "letter";
	standardVersion = "US";

	if(function_sidc == ''){
		ms.setStandard("2525");
		//Make Select Options  
		letterSIDC.sidccodingscheme(); // coding sheme
		getBatteDimension(); // battle dimension draw
		
		letterSIDC.affiliation(); 
		letterSIDC.status();
	}
	
	document.getElementById(openDIV+"SIDCtab").className = "active";
}


// 군대부호 변경 EVENT
function changeSIDC(){
	var SIDC_dom = document.getElementById('SIDC');
	SIDC_dom.onchange = function(e){
		if(SIDC_dom.value.charAt(0) != 'G' && SIDC_dom.value.charAt(0) != 'W'){ // symbol S/I/O/E
			sidcCreatorInit(); //symbol 그려주기
			drawSymbol();
		} else {
			document.getElementById('ImageSymbol').src = '';
		}
	};
}


function drawSymbol(){ 
	
	var cs = document.getElementById('SIDCCODINGSCHEME').value;
	var SUB_mod = document.getElementsByClassName('SUB_mod');
	var sidc = document.getElementById('SIDC').value;//20200313
	monoColorSetting(sidc);//20200313
	if(cs.charAt(0) == 'G' || cs.charAt(0) == 'W'){
		document.getElementById('ImageSymbol').src = '';
		if ( $('input:checkbox[id=MonoColorChk]').is(':checked') ) {//20200304
			$('.MonoColor2').removeAttr('style');
		}
		drawMsymbol();
	} else {
		if ( $('input:checkbox[id=MonoColorChk]').is(':checked') ) {//20200304
			$('.MonoColor2').attr('style', "display:none;");
		}
		bChangeID = false;
		drawSsymbol();
	}
	
	for(var i = 0; i < SUB_mod.length; i++){
		if (cs.charAt(0) == 'G' || cs.charAt(0) == 'W'){
			SUB_mod[i].style.display = 'none';
		} else {
			SUB_mod[i].style.display = 'block';
		}
	}
}

function monoColorSetting(sidc){//20200313
	//cs로 property 받아오기
	if ( sidc !== "") {
		var searchData = milcodeChange(sidc);
		JSONfile = CONTEXT + '/json/milsym/'+searchData.charAt(0)+'.json';
		var property = "";
		$.ajax({
			url: JSONfile,
			dataType: 'json',
			async: false,
			success: function(data){
				$.each(Object.keys(data), function(index, item) {
					var milItem = data[item];
					if (milItem.MIL_CODE === searchData) {
						milCode = milItem.MIL_CODE;
						property = milItem.PROPERTIES.split('.');
						return false;
					}
				});
			}
		});

		for (var i = 0; i < property.length; i++) {
			if (property[i].length === 3 && property[i].charAt(0) === 'E') {
				//E10 => E : MonoColor, charAt(1) : MonoColor1, charAt(2) : MonoColor2
				// 0 : display:none, 1 : normal, 2 : readonly
				if ( $('input:checkbox[id=MonoColorChk]').is(':checked') ) {
					if ( property[i].charAt(1) === '1') {
						$('.MonoColor1').removeAttr('style');
						if (property[i].charAt(2) === '1') {
							$('.MonoColor2').removeAttr('style');
						} else if (property[i].charAt(2) === '2') {
							//ICOPS에서 설정은 열리는데 색상 적용이 안되는 경우를 구분하여 disable처리
							$('.MonoColor2').removeAttr('style');
							$('.MonoColor2').addClass("-disable");
							$('#MonoColor2').val("");
						} else {
							$('.MonoColor2').attr('style', "display:none;");
							$('.MonoColor2').removeClass("-disable");
							$('#MonoColor2').val("");
						}
					} else {
						if (property[i].charAt(2) === '1') {//IN (2.2.2.4.18, 2.2.2.4.28) E01
							//특이한 경우. 실제 MonoColor2를 가지고 운용해야 하지만
							//open source가 MonoColor1을 따라가는 구조라서 크게 수정하지 않기 위해 MonoColor1 사용
							$('.MonoColor1').removeAttr('style');
							$('.MonoColor2').attr('style', "display:none;");
						}
					}
				}
			}
		}
	}
}

//-------------------------------------------------------------------------------------------------------------------------------
/** 
 * 2018.01.29 tree 형식 
 * */	
	// get battle dimension li list
	function getBatteDimension(){
		/*var ul = $('#FUNCTIONS');
		ul.html('');*/
		var ul = document.getElementById('FUNCTIONS');
		ul.innerHTML = '';
		$.ajax({
		       url: CONTEXT + '/json/milsym/CodingSchemeInfo.json',
		       dataType: 'json',
		       async: false,
		       success: function(data){
				$.each(data, function(index, item){	
				if(item !== undefined){
					var li = document.createElement('li');
					li.id = item['MIL_CODE']+'_'+index+'_'+item['PROPERTIES']+'_'+item['MODIFIER'];
					li.classList.add('treeview');
					var a = document.createElement('a');
					a.href = '#';
					a.onclick = function(e){
						selectMotion(this);
						getFunction(1, this );
						saveFuncSIDC(this.parentElement.id);
					}
					var span = document.createElement('span');
					span.innerText = item['CODE_KOR_NAME'];
					var i_angle = document.createElement('i');
					var i_miner = document.createElement('i'); //2020-02-3 i 태그 추가
					i_angle.classList.add('fa');
					i_angle.classList.add('fa-plus-square'); //2020-02-3 css 명칭변경
					i_miner.classList.add('fa'); //2020-02-3 css 추가
					i_miner.classList.add('fa-minus-square'); //2020-02-3 css 추가
					span.insertBefore(i_miner, undefined); //2020-02-3 span 태그에 prepend 로 추가
					span.insertBefore(i_angle, undefined); //2020-02-3 span 태그에 prepend 로 변경
					a.appendChild(span);
					li.appendChild(a);
					ul.appendChild(li);
				}
				});
		       }
		});
	}
	var jsonData;
	// function UI Tree로 변경
	function getFunction(id_num, e){ 
		$('#FUNCTION_' + id_num).remove();
		// get keyCode
		var Selector = e.parentElement;
		for(var i = 0; i < Selector.classList.length; i++){
			// menu가 open 되어 있을 경우 function 실행을 중단
			if(Selector.classList[i] == 'menu-open')
				return;
		}
		if ( selectedID !== Selector.id) {
			bChangeID = true;
		}else{
			bChangeID = false;
		}		
		
		var milCode = Selector.id;
		var keyCode = '';
			keyCode = Selector.id.split('_')[1];
		var ul = document.createElement('ul');
		ul.id = 'FUNCTION_' + id_num;
		ul.classList.add('treeview-menu');
		
		if(typeof $(e).closest("#FUNCTIONS > li.menu-open").attr("id") !== 'undefined' ){
			milCode = $(e).closest("#FUNCTIONS > li.menu-open").attr("id");
		}
		$('#SIDCCODINGSCHEME option:eq('+milCode.split('_')[1].charAt(0)+')').prop("selected", true);//20200207	
		//20200212
		if ( Selector.id.split('_')[0].charAt(10) === '*' || Selector.id.split('_')[0].charAt(10) === '-') {
			$('#SIDCSYMBOLMODIFIER11 option:eq(0)').prop("selected", true);
		}else{
			$("#SIDCSYMBOLMODIFIER11").val(Selector.id.split('_')[0].charAt(10)).prop("selected", true);
		}
		
		var JSONfile = CONTEXT + '/json/milsym/'+milCode.charAt(0)+'.json';
		$.ajax({
				url: JSONfile,
				dataType: 'json',
				async: false,
				success: function(data){
					for(var kl = 1; kl < 50; kl++){
						var key = keyCode+'.'+kl;
						if(data.hasOwnProperty(key)){
							var li = document.createElement('li');
							li.id = data[key]['MIL_CODE']+'_'+key+'_'+data[key]['PROPERTIES']+'_'+data[key]['MODIFIER'];
							li.classList.add('treeview');
							var a = document.createElement('a');
							a.href = '#';
							a.onclick = function(e){
								if(milCode.charAt(0) === 'G' || milCode.charAt(0) === 'W'){
									selectMotion(this);
									getFunction((id_num+1), this);
									createStyles();
									createModifiers(this.parentElement.id.split('_')[0]);
									G_coordinates = new Array();
									saveFuncSIDC(this.parentElement.id);
								}else{
									selectMotion(this);
									getFunction((id_num+1), this);
									saveFuncSIDC(this.parentElement.id);									
								}
							}
						
							var span = document.createElement('span');
							span.innerText =  data[key]['CODE_KOR_NAME'];
							if(data[key+'.1'] != undefined){
								var i_angle = document.createElement('i');
								var i_miner = document.createElement('i');
								i_angle.classList.add('fa');
								i_angle.classList.add('fa-plus-square');
								i_miner.classList.add('fa');
								i_miner.classList.add('fa-minus-square');
								span.insertBefore(i_miner,undefined);
								span.insertBefore(i_angle,undefined);
							}
							a.appendChild(span);
							li.appendChild(a);
							ul.appendChild(li);
						}
					}
					Selector.appendChild(ul);
			}
		});
		$('#FUNCTION_' + id_num).show();// 주석 처리하면 초기 리스트도 보이지 않음.
	}
	
	// 수정정보 setting
	function getsidcInfo(sidc){
		getBatteDimension(); // battle dimension draw
		var BD_list = $('#FUNCTIONS').children('li');	
		var sidc_val = '';
		if(sidc.charAt(0) == 'G' || sidc.charAt(0) == 'W'){
			sidc_val = SymbolUtilities.getBasicSymbolID(sidc);
			document.getElementById('ImageSymbol').src = '';
		} else {
			var symbolID = sidc.substring(0,1);
			if(sidc.charAt(0) == 'O'){
				symbolID += '*';
			}else{
				symbolID += '-';	
			}	        
	        symbolID += sidc.substring(2,3);
	        symbolID += '*';//20200208
			symbolID += sidc.substring(4,10);
			if( sidc.substring(10,11) != "-" && sidc.substring(10,11) != "*" //20200211
				&& sidc.substring(10,11) != "M" && sidc.substring(10,11) != "N") {  //20200304 
				symbolID += sidc.substring(10,11);
			}else{
				symbolID += '-';
			}
			symbolID += '----';
			//symbolID += '-----';
			sidc_val = symbolID;
		}
		
		battle_sidc = sidc_val;
		var selID = "";
		for(var i = 0; i < BD_list.length; i++){
			var match_BD = '';
			var match_sidc = '';
			match_BD = BD_list[i].id.split('_')[0].substring(0,2);
			match_sidc = sidc_val.substring(0,2);
			if (sidc.charAt(0) == 'W'){
				match_sidc = "W-"
			}			
			if(match_BD === match_sidc){
				var selector = BD_list[i].children[0];
				selectMotion(selector);
				getFunction(1,selector);
				BD_list[i].classList.add('menu-open');
				BD_list[i].parentElement.classList.add('active');
				//sidc_val = sidc.substring(2,10).toUpperCase();//20200207					
				function_sidc = sidc;
				if (sidc.charAt(0) === 'W'){
					searchItem = '';
					JSONfile = CONTEXT + '/json/milsym/W.json';
					$.ajax({
						url: JSONfile,
						dataType: 'json',
						async: false,
						success: function(data){
							$.each(Object.keys(data), function(index, item){
								sidcKey = item;
								var milItem = data[item];	
								if(milItem.MIL_CODE === sidc){
									milCode = milItem.MIL_CODE;
									careateSidc = milItem.MIL_CODE+'_'+item+'_'+milItem.PROPERTIES+'_'+milItem.MODIFIER;
									milCodeSearch = false;
									searchItem = item;
									//return false;
								}else if(milItem.MIL_CODE === milcodeChange(sidc)){
									milCode = milItem.MIL_CODE;
									careateSidc = milItem.MIL_CODE+'_'+item;
									milCodeSearch = false;
									searchItem = item;
									//return false;
								}	
							});
						}
					});
					var tempItem = searchItem.split('.')[0];
					selID = sidc + '___';//20200213
					for(var j = 1; j < searchItem.split('.').length; j ++) {
						tempItem = tempItem + '.' + searchItem.split('.')[j];
						var func_list = $(selector).parent().children('ul').children('li');
						for(var k = 0; k < func_list.length; k++){
							var func;
								func = func_list[k].id.split('_')[1];								
							if(func == tempItem){
								selID = func_list[k].id;
								selector = func_list[k].children[0];
								selectMotion(selector);
								getFunction((j+1), selector);
								func_list[k].classList.add('menu-open');
								func_list[k].parentElement.classList.add('active');
								document.getElementById("SIDC_HIER").value = func_list[k].id.split('_')[1];
								break;
							}
						}
					} 
				}else{
					sidc_val = sidc_val.substring(2,10).toUpperCase();//20200207
					for(var j = 1; j <= 8; j ++) {
						var func_val = sidc_val.substring(0,j);
						if(func_val == '-') { //20200213
							changeModifer(function_sidc + "___");
							return;
						}	
						var func_list = $(selector).parent().children('ul').children('li');
						for(var k = 0; k < func_list.length; k++){
							var func;
								func = func_list[k].id.split('_')[0].substring(2,10);
								func = func.replace(/-/gi,'');
							if(func == sidc_val.substring(0,(j+1))){
								selID = func_list[k].id;
								selector = func_list[k].children[0];
								selectMotion(selector);
								getFunction((j+1), selector);
								func_list[k].classList.add('menu-open');
								func_list[k].parentElement.classList.add('active');
								document.getElementById("SIDC_HIER").value = func_list[k].id.split('_')[1];
								break;
							} else if(func == sidc_val.substring(0,j)){
								selID = func_list[k].id;
								selector = func_list[k].children[0];
								selectMotion(selector);
								getFunction((j+1), selector);
								func_list[k].classList.add('menu-open');
								func_list[k].parentElement.classList.add('active');
								document.getElementById("SIDC_HIER").value = func_list[k].id.split('_')[1];
								break;
							}
						}
					}
				}
				break;
			}
		}
		createStyles();
		createModifiers(sidc);
		changeModifer(selID);
	}

	
	// Multiple 도 가능하게 수정 function
	function saveFuncSIDC(id){	
		var cs = document.getElementById('SIDCCODINGSCHEME').value;
		var key_code = "";
		var symbol_code = "";
		if ( id.split('_').length == 4 ) {
			key_code = id.split('_')[1];
			symbol_code = id.split('_')[0];
		}
		document.getElementById("SIDC_HIER").value = key_code;
		document.getElementById("SIDC").value = symbol_code;

		if(key_code.replace(/-/gi,'').length == 2){
			battle_sidc = symbol_code;
			function_sidc = '------';
		} else {
			function_sidc = symbol_code;
		}
		
		if ( selectedID !== id) {			
			bChangeID = true;
		}else{
			bChangeID = false;
		}	
		changeModifer(id);		
		
		if(cs.charAt(0) == 'G' || cs.charAt(0) == 'W'){
			drawMsymbol();
		} else {
			drawSsymbol();
		}
		
		letterSIDC.status(); //20200212 속성->상태구분 변경		
	}

	// 기본설정과 수식정보, 속성 및 확인 버튼 control UPDATE //20200208
	function changeModifer(id){
		var property = "";
		var modifer = "";
		
		if ( id.split('_').length == 4 ) {
			property = id.split('_')[2].split('.');
			modifer = id.split('_')[3].split('.');
		}
		
		// A:피아구분, B:상태구분. C:부호크기. D:투명, E:피아색상, F:선굵기  ( <== 고정 ) 
		// G:운용조건, H:문자크기, I:기능부호, J:외형부호, K:외형채움, L:민간구분
		$('#property_G').addClass("hidden");
		$('#property_G').children('div').children('select').attr("disabled", true); 
		$('#property_H').addClass("hidden");
		$('#property_I').addClass("hidden");
		$('#property_J').addClass("hidden");
		$('#property_K').addClass("hidden");
		$('#property_L').addClass("hidden");
		$('#property_L').children('label').children('input').attr("disabled", true); 
		for (var i = 0; i < property.length; i++) {			
			if (property[i] === 'G1') {
				$('#property_G').removeClass("hidden");				
				$('#property_G').children('div').children('select').attr("disabled", false); 
			}else if (property[i] === 'G0') {
				$('#property_G').removeClass("hidden");
			}
			if (property[i] === 'H') {
				$('#property_H').removeClass("hidden");
			}
			if (property[i] === 'I') {
				$('#property_I').removeClass("hidden");
			}
			if (property[i] === 'J') {
				$('#property_J').removeClass("hidden");
			}
			if (property[i] === 'K') {
				$('#property_K').removeClass("hidden");
			}
			if (property[i] === 'L1') {
				$('#property_L').removeClass("hidden");
				$('#property_L').children('label').children('input').attr("disabled", false); 
			}else if (property[i] === 'L0') {
				$('#property_L').removeClass("hidden");
			}
		}
		
		// A:기본부호지정, B:부대단위, C:장비수량, D:기동부대식별,
		// F:부대증감, G:군 및 국가 구분, H:추가사항, H1:추가사항, H2:추가사항, 
		// J:평가등급, K:전투효과, L:신호정보장비, M:상급부대, N:적군표시,
		// P:피아식별모드/코드, Q:이동방향, R:이동수단, R2:신호정보 장비 이동성,
		// S:지휘소표시/실제위치표시, T:고유명칭, T1:고유명칭1, V:장비명, W:활동시각, W1:활동시각1, 
		// X:고도/심도, X1:고도/심도1, XN:고도/심도[], Y:위치, Z:속도, AA:지휘통제소, AB:가장/가상식별부호,
		// AD:기반형태, AE:장비분해시간, AF:공통명칭, AG:보조장비 식별부호,
		// AH:불확정영역, AI:선위의 추측선, AJ:속도선, AM:거리(미터), AN:각도(도)
		//$('#modifier_A').addClass("hidden");
		$('#modifier_B').addClass("hidden");
		$('#modifier_C').addClass("hidden");
		$('#modifier_D').addClass("hidden");
		$('#modifier_F').addClass("hidden");
		$('#modifier_G').addClass("hidden");
		$('#modifier_H').addClass("hidden");
		$('#modifier_H1').addClass("hidden");
		$('#modifier_H2').addClass("hidden");
		$('#modifier_J').addClass("hidden");
		$('#modifier_K').addClass("hidden");
		$('#modifier_L').addClass("hidden");
		$('#modifier_M').addClass("hidden");
		$('#modifier_N').addClass("hidden");
		$('#modifier_P').addClass("hidden");
		$('#modifier_Q').addClass("hidden");
		$('#modifier_R').addClass("hidden");
		$('#modifier_R2').addClass("hidden");
		$('#modifier_S').addClass("hidden");
		$('#modifier_T').addClass("hidden");
		$('#modifier_T1').addClass("hidden");
		$('#modifier_V').addClass("hidden");
		$('#modifier_W').addClass("hidden");
		$('#modifier_W1').addClass("hidden");
		$('#modifier_X').addClass("hidden");
		$('#modifier_X1').addClass("hidden");
		$('#modifier_XN').addClass("hidden");
		$('#modifier_Y').addClass("hidden");
		$('#modifier_Z').addClass("hidden");
		$('#modifier_AA').addClass("hidden");
		$('#modifier_AB').addClass("hidden");
		$('#modifier_AD').addClass("hidden");
		$('#modifier_AE').addClass("hidden");
		$('#modifier_AF').addClass("hidden");
		$('#modifier_AG').addClass("hidden");
		$('#modifier_AH').addClass("hidden");
		$('#modifier_AI').addClass("hidden");
		$('#modifier_AJ').addClass("hidden");
		$('#modifier_AM').addClass("hidden");
		$('#modifier_AN').addClass("hidden");
		for (var j = 0; j < modifer.length; j++) {		
			/*if (modifer[j] === 'A') {
				$('#modifier_A').removeClass("hidden");
			} */
			if (modifer[j] === 'B') {
				$('#modifier_B').removeClass("hidden");
			}
			if (modifer[j] === 'C') {
				$('#modifier_C').removeClass("hidden");
			}
			if (modifer[j] === 'D') {
				$('#modifier_D').removeClass("hidden");
			}
			if (modifer[j] === 'F') {
				$('#modifier_F').removeClass("hidden");
			}
			if (modifer[j] === 'G') {
				$('#modifier_G').removeClass("hidden");
			}
			if (modifer[j] === 'H') {
				$('#modifier_H').removeClass("hidden");
			}
			if (modifer[j] === 'H1') {
				$('#modifier_H1').removeClass("hidden");
			}
			if (modifer[j] === 'H2') {
				$('#modifier_H2').removeClass("hidden");
			}
			if (modifer[j] === 'J') {
				$('#modifier_J').removeClass("hidden");
			}
			if (modifer[j] === 'K') {
				$('#modifier_K').removeClass("hidden");
			}
			if (modifer[j] === 'L') {
				$('#modifier_L').removeClass("hidden");				
			}
			if (modifer[j] === 'M') {
				$('#modifier_M').removeClass("hidden");
			}
			if (modifer[j] === 'N') {
				$('#modifier_N').removeClass("hidden");
			}
			if (modifer[j] === 'P') {
				$('#modifier_P').removeClass("hidden");
			}		
			if (modifer[j] === 'Q') {
				$('#modifier_Q').removeClass("hidden");
			}		
			if (modifer[j] === 'R') {
				$('#modifier_R').removeClass("hidden");
			}
			if (modifer[j] === 'R2') {
				$('#modifier_R2').removeClass("hidden");
			}
			if (modifer[j] === 'S') {
				$('#modifier_S').removeClass("hidden");
			}
			if (modifer[j] === 'T') {
				$('#modifier_T').removeClass("hidden");
			}
			if (modifer[j] === 'T1') {
				$('#modifier_T1').removeClass("hidden");
			}
			if (modifer[j] === 'V') {
				$('#modifier_V').removeClass("hidden");
			}
			if (modifer[j] === 'W') {
				$('#modifier_W').removeClass("hidden");
			}
			if (modifer[j] === 'W1') {
				$('#modifier_W1').removeClass("hidden");
			}
			if (modifer[j] === 'X') {
				$('#modifier_X').removeClass("hidden");
			}
			if (modifer[j] === 'X1') {
				$('#modifier_X1').removeClass("hidden");
			}
			if (modifer[j] === 'XN') {
				$('#modifier_XN').removeClass("hidden");
			}
			if (modifer[j] === 'Y') {
				$('#modifier_Y').removeClass("hidden");
			}
			if (modifer[j] === 'Z') {
				$('#modifier_Z').removeClass("hidden");
			}
			if (modifer[j] === 'AA') {
				$('#modifier_AA').removeClass("hidden");
			}
			if (modifer[j] === 'AB') {
				$('#modifier_AB').removeClass("hidden");
			}
			if (modifer[j] === 'AD') {
				$('#modifier_AD').removeClass("hidden");
			}
			if (modifer[j] === 'AE') {
				$('#modifier_AE').removeClass("hidden");
			}
			if (modifer[j] === 'AF') {
				$('#modifier_AF').removeClass("hidden");
			}
			if (modifer[j] === 'AG') {
				$('#modifier_AG').removeClass("hidden");
			}			
			if (modifer[j] === 'AH') {
				$('#modifier_AH').removeClass("hidden");
			}
			if (modifer[j] === 'AI') {
				$('#modifier_AI').removeClass("hidden");
			}
			if (modifer[j] === 'AJ') {
				$('#modifier_AJ').removeClass("hidden");
			}
			if (modifer[j] === 'AM') {
				$('#modifier_AM').removeClass("hidden");
			}
			if (modifer[j] === 'AN') {
				$('#modifier_AN').removeClass("hidden");
			}
		}

		if ( id === '' || (id.split('_')[2] === '' && id.split('_')[3] === '') ) {
			$('#btn_property').css("display","none");
			$('#add').css("display","none");
			$('#CanvasSymbol #ImageSymbol').css("display","none");
		}else{
			$('#btn_property').css("display","");
			$('#add').css("display","");
			$('#CanvasSymbol #ImageSymbol').css("display","");
		}
		if ( bChangeID ) {
			for (var j = 0; j < modifer.length; j++) {		
				if (modifer[j] === 'B') {
					$('#SIDCSYMBOLMODIFIER12').find("option:eq(0)").prop("selected", true);
				}else if (modifer[j] === 'J' || modifer[j] === 'R' || modifer[j] === 'R2' || modifer[j] === 'AD' || modifer[j] === 'AG') {
					if ($('#'+modifer[j]) !== undefined) {
						$('#'+modifer[j]).find("option:eq(0)").prop("selected", true);
					}
				}else{
					if ( modifer[j] !== '' && $('#'+modifer[j]) !== undefined) {
						$('#'+modifer[j]).val("");					
					}
				}
			}
			drawSymbol();
			selectedID = id;
		}
	}
	
	function selectMotion(e){
		$(e).parent().parent('ul').find('a').css('color', '#2c3b41');
		$(e).css('color', '#8aa4af');
	}
	
	// 수식정보  html 생성
	function createModifiers(symbolCode){
		// C -> 숫자(특수한경우), H -> 문자(20), N -> ENY표시, T -> 문자(15), V -> 문자(20), W -> 숫자,기호 YY:MM:DD(16)
		// X -> 숫자(14), Y -> 문자(19) , AM -> 숫자(6), AN -> 숫자(3)
		if(symbolCode == '' || symbolCode == undefined){
			symbolCode = document.getElementById('SIDCCODINGSCHEME').value;
		}
		/* 20200208 주석처리
		var values = {'------':'-'};
		if(symbolCode.charAt(0) == 'G' || symbolCode.charAt(0) == 'W')values = {
				"A":{"kor_name":"기본 부호 지정","input_id":"A","code":"기본부호코드"},
				"B":{"kor_name":"부대단위","input_id":"B","code":"부대코드"},
				"C":{"kor_name":"용량","input_id":"C","code":"제한없음"},
				"H":{"kor_name":"추가사항","input_id":"H","code":"20자"},
				"H1":{"kor_name":"추가사항","input_id":"H1","code":"20자"},
				"H2":{"kor_name":"활동사항","input_id":"H2","code":"20자"},
				"N":{"kor_name":"적군 표시","input_id":"N","code":"ENY 표기"},
				"Q":{"kor_name":"이동 방향","input_id":"Q","code":"화생방핵 보고 부호"},
				"S":{"kor_name":"실제위치 표시","input_id":"S","code":"실제 위치로 부터 떨어진 위치"},
				"T":{"kor_name":"고유 명칭","input_id":"T","code":"15자"},
				"T1":{"kor_name":"고유 명칭","input_id":"T1","code":"15자"},
				"V":{"kor_name":"장비 종류","input_id":"V","code":"24자"},
				"W":{"kor_name":"활동 시각","input_id":"W","code":"YY:MM:DD 16자"},
				"W1":{"kor_name":"활동 시각","input_id":"W1","code":"YY:MM:DD 16자"},
				"X":{"kor_name":"고도/심도/거리","input_id":"X","code":"14자"},
				"X1":{"kor_name":"고도/심도/거리","input_id":"X1","code":"14자"},
				"Y":{"kor_name":"위치","input_id":"Y","code":"19자"},
				"Z":{"kor_name":"속도","input_id":"Z","code":"8 자"},
				"AM":{"kor_name":"거리","input_id":"AM","code":"(,) 구분"},
				"AN":{"kor_name":"방위","input_id":"AN","code":"(,) 구분"}
		};
		if(symbolCode.charAt(0) != 'G' && symbolCode.charAt(0) != 'W')values = {
				"C":{"kor_name":"수량","input_id":"Quantity","code":"제한없음"},
				"F":{"kor_name":"부대 증감","input_id":"ReinforcedReduced","code":"9 자"},
				"G":{"kor_name":"군 및 국가구분","input_id":"StaffComments","code":"3 자"},
				"H":{"kor_name":"추가 사항","input_id":"AdditionalInformation","code":"20 자"},
				"J":{"kor_name":"평가 등급","input_id":"evaluationRating","code":"2 자"},
				"K":{"kor_name":"전투효과(전투력)","input_id":"combatEffectiveness","code":"5 자"},
				"L":{"kor_name":"신호장비 정보","input_id":"signatureEquipment","code":"1 자"},
				"M":{"kor_name":"상급 부대","input_id":"higherFormation","code":"21 자"},
				"N":{"kor_name":"적군 표시","input_id":"hostile","code":"3 자"},
				"P":{"kor_name":"피아식별 모드 및 코드(IFF/SIF)","input_id":"iffSif","code":"5 자"},
				"Q":{"kor_name":"이동 방향","input_id":"Direction","code":"4 자(도)"},
				"T":{"kor_name":"고유 명칭","input_id":"uniqueDesignation","code":"21 자"},
				"V":{"kor_name":"장비명,종류","input_id":"type","code":"24 자"},
				"W":{"kor_name":"활동시각(DTG)","input_id":"Dtg","code":"16 자"},
				"X":{"kor_name":"고도/심도","input_id":"altitudeDepth","code":"14 자"},
				"Y":{"kor_name":"위치","input_id":"location","code":"19 자"},
				"Z":{"kor_name":"속도","input_id":"speed","code":"8 자"},
				"AA":{"kor_name":"지휘통제소","input_id":"specialHeadquarters","code":"9 자"}
		};
		
		var tableArea = document.getElementById('textField');
		tableArea.innerHTML = '';

		if(symbolCode.charAt(0) == 'G' || symbolCode.charAt(0) == 'W'){
			symbolCode = SymbolUtilities.getBasicSymbolID(symbolCode);
			// multiple
			if(SymbolDefTable.getSymbolDef(symbolCode, symStd)){
				var mtgs = SymbolDefTable.getSymbolDef(symbolCode, symStd);
				var mtgs_split = mtgs.modifiers.split('.');
				for(var i = 0; i < mtgs_split.length; i++){
					var tr = document.createElement('tr');
					if(mtgs_split[i] != ''){
						var info = values[mtgs_split[i]];
						for (var keys in info){
							var td = document.createElement('td');
							if(keys == 'kor_name'){
								var text = document.createTextNode(info[keys]);
								td.appendChild(text);
							} else if(keys == "input_id"){
								var input = document.createElement('input');
								input.type = 'text';
								input.id = info[keys];
								
								td.appendChild(input);
							} else if(keys == "code"){
								var code = document.createElement('code');
								var text = document.createTextNode(info[keys]);
								code.appendChild(text);
								td.appendChild(code);
							}
							tr.appendChild(td);
						}
					}
					tableArea.appendChild(tr);
				}
			}
		} else {
			// single
			for(var key in values){
				var tr = document.createElement('tr');
				var info = values[key];
				for(var keys in info){
					var td = document.createElement('td');
					if(keys == 'kor_name'){
						var text = document.createTextNode(info[keys]);
						td.appendChild(text);
					} else if(keys == "input_id"){
						var input = document.createElement('input');
						input.type = 'text';
						input.id = info[keys];
						input.onkeyup = function(e){
							drawSymbol();
						}
						td.appendChild(input);
					} else if(keys == "code"){
						var code = document.createElement('code');
						var text = document.createTextNode(info[keys]);
						code.appendChild(text);
						td.appendChild(code);
					}
					tr.appendChild(td);
				}
				tableArea.appendChild(tr);
			}
		} */
	}
	
	function createStyles(){
		var coding_scheme = document.getElementById('SIDCCODINGSCHEME').value;
		if(coding_scheme.charAt(0) == 'G' || coding_scheme.charAt(0) == 'W'){
			type = 'Msymbol';
		} else {
			type = 'Ssymbol';
		}
		
		/* var ids = ["MonoColor","InfoColor","outlineColor"]; 
		for(var i = 0; i < ids.length; i++){
			var id = ids[i];
			if(type == 'Msymbol' && ids[i] == 'outlineColor')
				break;
			
			getColor(id, type);
		} */
	}
	
	function getColor(id, type){
		var values = {};
		var selector = document.getElementById(id);
		if(id == 'outlineColor' && type == 'Ssymbol'){
			values = {"#efefef":"매우 밝은 회색","#ffffff":"흰색",
					"#c0c0c0":"은색","#7f7f7f":"회색","#000000":"검정색",
					"#ff0000":"적색","#800000":"밤색","#ffff00":"황색",
					"#6b8e23":"올리브색","#32cd32":"라임색","#00ff00":"녹색",
					"#7fffd4":"아쿠아색","#008080":"틸색","#00ffff":"파란색",
					"#000080":"남색","#ff0080":"자홍색","#ff00ff":"보라색"}
		} else if(type == 'Msymbol') {
			values = {"":"피아관계 지정색","#000000":"검은색","#ffffff":"흰색",
					"#ff0000":"적색","#00ffff":"청색","#00ff00":"녹색",
					"#ffff00":"황색","#ff00ff":"보라색","#800000":"밤색"}
		} else {
			values = {"":"-","#ffffff":"흰색",
					"#c0c0c0":"은색","#7f7f7f":"회색","#000000":"검정색",
					"#ff0000":"적색","#800000":"밤색","#ffff00":"황색",
					"#6b8e23":"올리브색","#32cd32":"라임색","#00ff00":"녹색",
					"#7fffd4":"아쿠아색","#008080":"틸색","#00ffff":"파란색",
					"#000080":"남색","#ff0080":"자홍색","#ff00ff":"보라색"}
		}
		
		selector.innerHTML = '';
		for (var key in values){
			selector.add(new Option(values[key], key));
		}
	}
	
	
	function getDrawGraphicsInfo(sidc){
		var drawInfo = {};
		drawInfo.draw_type = '';
		drawInfo.constraint = '';
		drawInfo.min_point = 0;
		drawInfo.max_point = 0;
		
		var code = SymbolUtilities.getBasicSymbolID(sidc);
		
		var symbolDef = SymbolDefTable.getSymbolDef(code, symStd);
		
		symbolDefNew = symbolDef;
		if(symbolDef != null){
			drawInfo.min_point = symbolDef.minPoints;
			drawInfo.max_point = symbolDef.maxPoints;
			
			if(symbolDef != null){
				switch(symbolDef.drawCategory){
				case 0 : return; // Do not draw
				break;
				case 1 : drawInfo.draw_type = 'MultiLineString'; // Line
				break;
				case 2 : drawInfo.draw_type = 'LineString'; // Squashed circle
				break;
				case 3 : drawInfo.draw_type = 'Polygon'; // Polygon (MultiPoints)
				break;
				case 4 : drawInfo.draw_type = 'MultiLineString'; // Arrow
				break;
				case 5 : drawInfo.draw_type = 'MultiLineString'; // Arrow : last point -> width
				break;
				case 6 : drawInfo.draw_type = 'LineString'; // Only Two Point Line
				break;
				case 8 : drawInfo.draw_type = 'Point'; // Point
				break;
				case 9 : drawInfo.draw_type = 'LineString'; // polyline with 2 points (entered in reverse order)  
				break;
				case 15 : drawInfo.draw_type = 'LineString'; // Super Auto Shape
				break;
				case 16 : drawInfo.draw_type = 'Point'; // figure Circle, requires modifier 1 AM 
				drawInfo.constraint = {'AM':'1'};
				break;
				case 17 : drawInfo.draw_type = 'Point'; // Rectangle that requires 2 AM modifier and 1 AN
				drawInfo.constraint = {'AM':'2','AN':'1'};
				break;
				case 18 : drawInfo.draw_type = 'Point'; // Rectangle that requires 2 AM modifier and 2 AN
				drawInfo.constraint = {'AM':'2','AN':'2'};
				break;
				case 19 : drawInfo.draw_type = 'Point'; // requires at least 1 distance/AM
				drawInfo.constraint = {'AM':'1'};
				break;
				case 20 : drawInfo.draw_type = 'Polygon'; // requires 1 AM
				drawInfo.constraint = {'AM':'1'};
				break;
				case 40 : return; // 3D airspace, not a milstd graphic
				break;
				case 99 : return; // UNKOWN
				break;
				}
			}
		}
		return drawInfo;
	}
	
	function buildSymbolID(basicID)	{
	    if(basicID != '' && basicID.charAt(0) !== 'W')   {
	        var affiliation = document.getElementById("SIDCAFFILIATION")[document.getElementById("SIDCAFFILIATION").selectedIndex].value;
	        var status = document.getElementById("SIDCSTATUS")[document.getElementById("SIDCSTATUS").selectedIndex].value;
	        var SIDCSYMBOLMODIFIER11 = document.getElementById("SIDCSYMBOLMODIFIER11")[document.getElementById("SIDCSYMBOLMODIFIER11").selectedIndex].value.split('_')[0];
			var SIDCSYMBOLMODIFIER12 = document.getElementById("SIDCSYMBOLMODIFIER12")[document.getElementById("SIDCSYMBOLMODIFIER12").selectedIndex].value;
	        var modifiers = SIDCSYMBOLMODIFIER11 + SIDCSYMBOLMODIFIER12;

	        var symbolID = basicID.substring(0,1);
	        symbolID += affiliation;
	        symbolID += basicID.substring(2,3);
	        symbolID += status;
	        symbolID += basicID.substring(4,10);
	        if(basicID.charAt(0) !== 'G'){
	        	symbolID += modifiers;
	        }else{
	        	symbolID += (modifiers.length === 5) ? modifiers : basicID.substring(10,15);
			}
	        return symbolID;
	    }
	    else
	        return basicID
	}

	function buildSymbolID(basicID, options)	{
	    if(basicID != '' && basicID.charAt(0) !== 'W')   {
	        var affiliation = document.getElementById("SIDCAFFILIATION")[document.getElementById("SIDCAFFILIATION").selectedIndex].value;
	        var status = document.getElementById("SIDCSTATUS")[document.getElementById("SIDCSTATUS").selectedIndex].value;
	        var SIDCSYMBOLMODIFIER11 = document.getElementById("SIDCSYMBOLMODIFIER11")[document.getElementById("SIDCSYMBOLMODIFIER11").selectedIndex].value.split('_')[0];
			var SIDCSYMBOLMODIFIER12 = "-";
			if ( document.getElementById("SIDCSYMBOLMODIFIER12").selectedIndex > -1 ) {
				SIDCSYMBOLMODIFIER12 = document.getElementById("SIDCSYMBOLMODIFIER12")[document.getElementById("SIDCSYMBOLMODIFIER12").selectedIndex].value;
			}
			var modifiers = SIDCSYMBOLMODIFIER11 + SIDCSYMBOLMODIFIER12;
			if(basicID.charAt(0) !== 'G')   {				
				if (options.AG !== "") {
					modifiers = "N" + options.AG;
				}
				if ( options.R !== "") {
					modifiers = "M" + options.R;
				}
			}
	        var symbolID = basicID.substring(0,1);
	        symbolID += affiliation;
	        symbolID += basicID.substring(2,3);
	        symbolID += status;
	        symbolID += basicID.substring(4,10);
	        if(basicID.charAt(0) !== 'G'){
	        	symbolID += modifiers;
	        }else{
	        	symbolID += (modifiers.length === 5) ? modifiers : basicID.substring(10,15);
			}
	        return symbolID;
	    }
	    else
	        return basicID
	}


