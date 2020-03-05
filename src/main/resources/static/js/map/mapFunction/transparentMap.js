/**
 * transparentMap 
 */
function tansparentMapViewer(type){
	var viewer = $('#TM_style_info');
	
	viewer.fadeIn();
	
	var divTop = $('#topMenu').outerHeight() + $('.content-header').outerHeight();
	var divLeft = window.innerWidth - ($('#viewTransMapWindow').innerWidth()+(10*1));
	
	viewer.css({
		'top': divTop,
		'left': divLeft,
		'position': 'absolute',
		'z-index': '999'
	}).show();
	
	if(type == 'save'){
		if($('#saveTMapBtn').hasClass('hidden')){
			$('#saveTMapBtn').removeClass('hidden');
			$('#modTMapBtn').addClass('hidden');
		}
		$('#file_name').val('');
		$('#saveTransMapWindow').show();
	} else if(type == 'view'){
		$('#viewTransMapWindow').show();
		roadTMapFileList();
		dragElement(document.getElementById('TM_style_info'), 'viewTransMapWindow');
		
		/**
		 * search file information and show file list Transparent Map Function
		 */
		$('input[name=search_text]').keydown(function(key){
			if(key.keyCode == 13) {
				var searchData = $(this).val();
				roadTMapFileList(searchData);
			}
		});
	}
	
}

$('#saveTM_close').on('click', function(){
	$('#saveTransMapWindow').hide();
});

$('#viewTM_close').on('click', function(){
	$('#viewTransMapWindow').hide();
});

$('#saveTMapBtn').on('click', function(){
	if(confirm('저장하시겠습니까?')){
		createTransparentMap();
	}
});

function roadTMapFileList(searchData){
	var list_box = $('#list_box');
	list_box.html('');
	
	var searchCase = '';
	if(searchData != undefined){
		searchCase = searchData;
	} 
	
	$.ajax({
		url : '../TMap/getTMapList.json',
		type : 'POST',
		async: false,
		data : {
			searchCase : searchCase
		},
		success: function(data){
			var data = JSON.parse(data)['data'];
			if(data.length > 0){
				var ul = document.createElement('ul');
				ul.classList.add('list-unstyled');
				for(var i = 0; i < data.length; i++){
					var li = document.createElement('li');
					
					var checkbox = document.createElement('input');
					if((i+1) < 10){
						checkbox.id = 'clayer_0'+(i+1);
					} else {
						checkbox.id = 'clayer_'+(i+1);
					}
					checkbox.type = 'checkbox';
					checkbox.value = data[i]['file_idx'];
					checkbox.onchange = function(e){
						if($('#delActivationBtn').html() == '삭제 활성화'){
							if($(this).is(':checked')){
								if(confirm('선택한 레이어를 적용하시겠습니까?')){
									var layer_name = $(this).parent().children('input[type=hidden]').val();
									var index = layer_name.split('_')[1];
									var file_info = {};
									file_info.file_name = $(this).parent().children('label').html();
									var file_idx = $(this).val();
									showTransparentMap(layer_name, file_idx);
									
									selectTransparentMapList(file_idx, file_info, index);
								} else {
									$(this).prop('checked', false);
								}
							}
						}
					}
					li.appendChild(checkbox);
					
					var hidden = document.createElement('input');
					hidden.type = 'hidden';
					if((i+1) < 10){
						hidden.value = 'tms_0'+(i+1);
					} else {
						hidden.value = 'tms_'+(i+1);
					}
					li.appendChild(hidden);
					
					var label = document.createElement('label');
					label.innerHTML = data[i]['file_name'];
					if((i+1) < 10){
						label.htmlFor = 'clayer_0'+(i+1);
					} else {
						label.htmlFor = 'clayer_'+(i+1);
					}
					label.className = 'TMaplist_element';
					li.appendChild(label);
					
					ul.appendChild(li);
				}
				list_box.append(ul);
			}
		}
	});
}

function selectTransparentMapList(file_idx, file_info, index){
	var ul = $('#select_list_box').children('ul');
	
	var li = document.createElement('li');
	
	var checkbox = document.createElement('input');
	checkbox.type = 'checkbox';
	checkbox.value = file_idx;
	checkbox.defaultChecked = true;
	checkbox.onchange = function(e){
		var layer_name = $(this).parent().children('input[type=radio]').val();
		if($(this).is(':checked')){
			var hide_style = new ol.style.Style({
				visibility: 'hidden'
			});
			var layer = chooseLayer(layer_name);
			layer.setVisible(true);
		} else {
			var layer = chooseLayer(layer_name);
			layer.setVisible(false);
		}
	}
	li.appendChild(checkbox);
	
	var radio = document.createElement('input');
	radio.id = 'layer_'+index;
	radio.name = 'TMaplayers';
	radio.type = 'radio';
	radio.value = 'tms_'+index;
	radio.defaultChecked = true;
	radio.onchange = function(e){
		if($(this).is(':checked')){
			var layerName = $(this).val();
			selectLayer = chooseLayer(layerName);
		}
	}
	li.appendChild(radio);
	
	var label = document.createElement('label');
	label.innerHTML = file_info['file_name'];
	label.htmlFor = 'layer_'+index;
	label.className = 'TMaplist_element';
	li.appendChild(label);
	
	var div = document.createElement('div');
	div.className = 'pull-right';
	
	var editI = document.createElement('i');
	editI.className = 'fa fa-edit TMaplist_element';
	editI.onclick = function(e){ 
		$(this).parent().parent().children('input[type=checkbox]').prop('checked', true); // view true
		var file_idx = $(this).parent().parent().children('input[type=checkbox]').val();
		
		$(this).parent().parent().children('input[type=radio]').prop('checked', true); // mod true
		var layer_name = $(this).parent().parent().children('input[type=radio]').val();
		selectLayer = chooseLayer(layer_name);

		if(file_idx == ''){
			alert('레이어를 저장 후 수정해주세요.');
		} else {
			if(confirm('선택한 레이어를 수정하시겠습니까?')){
				var file_name = $(this).parent().parent().children('label').html();
				
				if($('#modTMapBtn').hasClass('hidden')){
					$('#saveTMapBtn').addClass('hidden');
					$('#modTMapBtn').removeClass('hidden');
				}
				$('#file_name').val(file_name);
				$('#file_idx').val(file_idx);
				$('#saveTransMapWindow').show();
			}
		}
	}
	div.appendChild(editI);
	
	var treshI = document.createElement('i');
	treshI.className = 'fa fa-trash-o TMaplist_element';
	treshI.onclick = function(e){
		if(confirm('선택한 레이어를 목록에서 제외 하시겠습니까?')){
			var layer_name = $(this).parent().parent().children('input[type=radio]').val()
			var layer = chooseLayer(layer_name);
			map.removeLayer(layer);
			
			$(this).parent().parent().remove();
			if($('#clayer_'+layer_name.split('_')[1])[0] != undefined){
				var list_checkBox = $('#clayer_'+layer_name.split('_')[1])[0];
				list_checkBox.checked = false;
			}
		}
	}
	div.appendChild(treshI);
	
	li.appendChild(div);
	
	ul.append(li);
}

// 투명도 수정 버튼 EVENT
$('#modTMapBtn').on('click', function(){
	var select_idx = $('#file_idx').val();
	modifyTransparentMap(select_idx);
	$('#saveTransMapWindow').hide();
});

function deleteActivation(){
	var select_list_box = $('#select_list_box');
	
	var delBtn = $('#delTMapBtn');
	var delActivationBtn = $('#delActivationBtn');
	
	if(delActivationBtn.html() == '삭제 활성화'){
		if(delBtn.hasClass('hidden')){
			delBtn.removeClass('hidden');
			delActivationBtn.removeClass('btn-danger');
			delActivationBtn.addClass('btn-default');
			delActivationBtn.html('활성화 취소');
		}
		select_list_box.parent('div').hide();
	} else if (delActivationBtn.html() == '활성화 취소'){
		if(!delBtn.hasClass('hidden')){
			delBtn.addClass('hidden');
			delActivationBtn.removeClass('btn-default');
			delActivationBtn.addClass('btn-danger');
			delActivationBtn.html('삭제 활성화');
		}
		$('#list_box>ul>li>input[type=checkbox]').prop('checked', false);
		select_list_box.parent('div').show();
	}
}

function createTransparentMapLayer(name, type){
	// milSymbol maker layer
	var TMapLayer = new ol.layer.Vector({
		source: new ol.source.Vector({
			features: [],
			projection: 'EPSG:4326'
		})
	});
	if(name == undefined){
		name = 'tms_00';
	} 
	
	TMapLayer.set('name', name);
	
	if(type == undefined){
		var check = tmsLayerCheck();
		if(check){
			map.addLayer(TMapLayer);
			selectLayer = chooseLayer(name);
			
			var file_info = {};
			file_info.file_name = '새 레이어 0';
			var index = '00';
			selectTransparentMapList('', file_info, index);
		} else {
			alert('생성된 투명도가 있습니다.');
			return;
		}
	} else if (type == 'road') {
		map.addLayer(TMapLayer);
		selectLayer = chooseLayer(name);
	}
}

function tmsLayerCheck(){
	var check = true;
	map.getLayers().forEach(function (layer){
		if (layer.get('name') != undefined){
			if(layer.get('name').substring(0, 3) == 'tms'){
				check = false;
			}
		}
	});
	
	return check;
}

function chooseLayer(name){
	var TMaplayer;
	map.getLayers().forEach(function (layer){
		if (layer.get('name') != undefined){
			if(layer.get('name') == name){
				TMaplayer = layer;
			}
		}
	});
	return TMaplayer;
}

function showMilSymbolExampleTxt(){
	var viewer = $('#symbol_example_info');
	
	var divTop = $('#topMenu').outerHeight() + $('.content-header').outerHeight();
	var divLeft = window.innerWidth - (400+(10*1));
	
	viewer.css({
		'top': divTop,
		'left': divLeft,
		'width': '400px',
		'position': 'absolute',
		'z-index': '999'
	}).show();
	
	$.ajax({
	       url: '/example/EX_milSymbol.txt',
	       dataType: 'html',
	       async: false,
	       contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
	       success: function(data){
	    	   $('#milSymbol_example-body').html(data);
	       }
	});
	
	dragElement(document.getElementById('symbol_example_info'), 'milSymbol_example');
	
	$('#symbol_example_close').on('click', function(){
		$('#symbol_example_info').hide();
	});
}

/**
 * 표준 XML 내보내기 기능 FUNCTION
 *  */
function maketrandardXML(){
	var transparentMapJSON = wirteXMLTransparentMap();
	
	var file_info = {};
	var file_name = $('input:radio[name=TMaplayers]:checked')[0].parentNode.innerText;
	file_info.file_name = file_name;
	file_info.save_user = 'Tester'; //파일 생성자
	file_info.save_date = ''; //저장 일자
	file_info.modif_date = ''; //수정 일자
	file_info.save_path = 'C:\\DEV\\XMLTest\\'; //파일 저장 경로
	
	$.ajax({
		url : '../TMap/xmlTMap.do',
		type : 'POST',
		async: false,
		data : {
			file_info : JSON.stringify(file_info),
			TMapJSON : transparentMapJSON
		},
		success : function(data){
			alert("xml파일이 생성되었습니다.");
			
			postPaging('../TMap/getXML.do', {'file_name':file_info.file_name, 'file_path':file_info.save_path});
		}
	});
}

var OpenWin;
function roadTo3D(){
	window.name = 'main_2DMap';
	OpenWin = window.open(
		'../map/cesium3DMap.do',
		'cesium_3DMap',
		'_blank'
	);
}



// post paging function
function postPaging(path, params, method){
	method = method || "POST";

	var form = document.createElement("form");
	form.setAttribute("method", method);
	form.setAttribute("action", path);
	document.body.appendChild(form);

	for(var key in params){
		var inputHidden = document.createElement("input");
		inputHidden.setAttribute("type","hidden");
		inputHidden.setAttribute("name", key);
		inputHidden.setAttribute("value", params[key]);

		form.appendChild(inputHidden);
	}

	document.body.appendChild(form); 
	form.submit();
}

// save Transparent Map List Box Drag and Move EVENT
function dragElement(elmnt, moveElement) {
	var pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0;
	if (document.getElementById(moveElement + "-header")) {
		/* if present, the header is where you move the DIV from:*/
		document.getElementById(moveElement + "-header").onmousedown = dragMouseDown;
	} else {
		/* otherwise, move the DIV from anywhere inside the DIV:*/
		elmnt.onmousedown = dragMouseDown;
	}

	function dragMouseDown(e) {
		e = e || window.event;
		// get the mouse cursor position at startup:
		pos3 = e.clientX;
		pos4 = e.clientY;
		document.onmouseup = closeDragElement;
		// call a function whenever the cursor moves:
		document.onmousemove = elementDrag;
	}

	function elementDrag(e) {
		e = e || window.event;
		// calculate the new cursor position:
		pos1 = pos3 - e.clientX;
		pos2 = pos4 - e.clientY;
		pos3 = e.clientX;
		pos4 = e.clientY;
		// set the element's new position:
		elmnt.style.top = (elmnt.offsetTop - pos2) + "px";
		elmnt.style.left = (elmnt.offsetLeft - pos1) + "px";
	}

	function closeDragElement() {
		/* stop moving when mouse button is released:*/
		document.onmouseup = null;
		document.onmousemove = null;
	}
}