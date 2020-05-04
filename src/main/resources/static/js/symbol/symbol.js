/**
 * Military Single Point Symbol SIDC Event
 * */

var activeStandard, standardVersion; // single point S/I/O/E

var function_sidc = ''; // function sidc
var battle_sidc = ''; // battle dimension sidc
	
	//This is for initiating the letterbased UI with a SIDC
	function sidcCreatorInit(existingSidc){
		// Load할 때와 코드를 입력하여 Symbol을 바꿀 때 동작하는 Function
		var i;
		var sidc = existingSidc;
		if(window.location.hash){sidc = decodeURIComponent(window.location.hash.substring(1));}
		if(document.getElementById("SIDC").value){sidc = document.getElementById("SIDC").value;}
		//We have to initiate this if we start with a number sidc and then switch to a letter sidc
		letterSIDC.modifier11();
		letterSIDC.modifier12();
		
		if(isNaN(sidc)){ 
			activateTab('2525C');
			value = sidc.charAt(0).toUpperCase();
			selection = document.getElementById('SIDCCODINGSCHEME');
			for (i in selection) { 
				if(isNaN(i))break;
				var select_option = selection.options[i].value;
				if (select_option.charAt(0) == value) {
					if(select_option != 'S*Z*------*****'){
						selection.selectedIndex = i;
						break;
					}
				}
			}
			
			value = sidc.charAt(1).toUpperCase();
			selection = document.getElementById('SIDCAFFILIATION');
			for (i in selection){
				if(isNaN(i))break;
				if(selection.options[i].value == value){
					selection.selectedIndex = i;
					break;
				}
			}
			value = sidc.charAt(3).toUpperCase();
			selection = document.getElementById('SIDCSTATUS');
			for (i in selection){
				if(isNaN(i))break;
				if(selection.options[i].value == value){
					selection.selectedIndex = i;
					break;
				}
			}
			value = sidc.charAt(10).toUpperCase();
			selection = document.getElementById('SIDCSYMBOLMODIFIER11');
			for (i in selection){
				if(isNaN(i))break;
				var select_option = selection.options[i].value.split('_');
				if (select_option[0] == value) {
					selection.selectedIndex = i;
					break;
				}
			}
			letterSIDC.modifier12();
			value = sidc.charAt(11).toUpperCase();
			selection = document.getElementById('SIDCSYMBOLMODIFIER12');
			for (var i in selection){
				if(isNaN(i))break;
				if(selection.options[i].value == value){
					selection.selectedIndex = i;
					break;
				}
			}
			getsidcInfo(sidc); // Tree Function
			
		}
	}
	
	
	//Draws a symbol when something changes
	function drawSsymbol(){
		function validateTexts(id,valid){
			if(valid){
				document.getElementById(id).className = '';
			}else{
				document.getElementById(id).className = 'textError';
			}
		}
		var options = stmp.getMilsymbolOptions()
		validateTexts("C",(!isNaN(options.quantity) && options.quantity.length <= 9));
		validateTexts("F",(options.reinforcedReduced.length <= 3));
		validateTexts("G",(options.staffComments.length <= 20));
		validateTexts("H",(options.additionalInformation.length <= 20));
		validateTexts("K",(options.combatEffectiveness.length <= 5));
		validateTexts("L",(options.signatureEquipment.length <= 1));
		validateTexts("M",(options.higherFormation.length <= 21));
		validateTexts("N",(options.hostile.length <= 3));
		validateTexts("P",(options.iffSif.length <= 5));
		validateTexts("Q",(!isNaN(options.direction) && options.direction.length <= 4));
		validateTexts("T",(options.uniqueDesignation.length <= 21));
		validateTexts("V",(options.type.length <= 24));
		validateTexts("W",(options.dtg.length <= 16));
		validateTexts("X",(options.altitudeDepth.length <= 14));
		validateTexts("Y",(options.location.length <= 19));
		validateTexts("Z",(options.speed.length <= 8));
		validateTexts("AA",(options.specialHeadquarters.length <= 9));
		var sidc = options.SIDC
		document.getElementById("SIDC").value = sidc;
		window.location.hash = sidc
		var symbol = new ms.Symbol(options)
		console.log(symbol, options)
		document.getElementById("ImageSymbol").style.width = symbol.getSize().width +'px';
		document.getElementById("ImageSymbol").style.height = symbol.getSize().height +'px';
		document.getElementById("ImageSymbol").src = symbol.asCanvas(window.devicePixelRatio).toDataURL();
	}
	
	var letterSIDC = {
		modifier11: function(){
			var JSONfile = CONTEXT + '/json/milsymInfo/Modifier_11.json';
			$.ajax({
				url : JSONfile,
				dataType: 'json',
				async: false,
				success: function(data){
					selection = document.getElementById('SIDCSYMBOLMODIFIER11');
					value = selection.value;
					selection.innerHTML = "";
					for (var key in data){
						selection.add(new Option(data[key]['CODE_KOR_NAME'], key))
					}
					for (var i in selection){
						if(!isNaN(i)){
							if(selection.options[i].value === value){
								selection.selectedIndex = i;
								break;
							}
						}
					}
				}
			});
		},
		modifier12: function(){
			var JSONfile = CONTEXT + '/json/milsymInfo/Modifier_12.json';
			$.ajax({
				url : JSONfile,
				dataType: 'json',
				async: false,
				success: function(data){
					var keyCode = document.getElementById('SIDCSYMBOLMODIFIER11').value;
					if(keyCode === 'M'){
						values = data['M'];
						selection = document.getElementById('R');
					} else if (keyCode === 'N'){
						values = data['N'];
						selection = document.getElementById('AG');
					} else {
						values = data['A'];
						selection = document.getElementById('SIDCSYMBOLMODIFIER12');
					}
					value = selection.value;
					selection.innerHTML = "";
					for (var key in values){
						selection.add(new Option(values[key]['CODE_KOR_NAME'], key))
					}
					for (var i in selection){
						if(!isNaN(i)){
							if(selection.options[i].value === value){
								selection.selectedIndex = i;
								break;
							}
						}
					}
				}
			});
		},
		affiliation: function(){ 
			var JSONfile = CONTEXT + '/json/milsymInfo/Affiliation.json';
			$.ajax({
				url : JSONfile,
				dataType: 'json',
				async: false,
				success: function(data){
					selection = document.getElementById('SIDCAFFILIATION');
					value = selection.value;
					selection.innerHTML = "";
					for (var key in data){
						selection.add(new Option(data[key]['CODE_KOR_NAME'], key))
					}
					for (var i in selection){
						if(!isNaN(i)){
							if(selection.options[i].value === value){
								selection.selectedIndex = i;
								break;
							}
						}
					}
				}
			});
		},
		status: function(){
			var JSONfile = CONTEXT + '/json/milsymInfo/Status.json';
			$.ajax({
				url : JSONfile,
				dataType: 'json',
				async: false,
				success: function(data){
					var keyCode = document.getElementById('SIDCCODINGSCHEME').value.charAt(0);
					if(keyCode === 'S'){
						values = data['S'];
					} else {
						values = data['O'];
					}
					selection = document.getElementById('SIDCSTATUS');
					value = selection.value;
					selection.innerHTML = "";
					for (var key in values){
						selection.add(new Option(values[key]['CODE_KOR_NAME'], key))
					}
					for (var i in selection){
						if(!isNaN(i)){
							if(selection.options[i].value === value){
								selection.selectedIndex = i;
								break;
							}
						}
					}
				}
			});
		},
		sidccodingscheme: function(){ 
			var JSONfile = CONTEXT + '/json/milsym/CodingScheme.json';
			$.ajax({
				url : JSONfile,
				dataType: 'json',
				async: false,
				success: function(data){
					selection = document.getElementById('SIDCCODINGSCHEME');
					value = selection.value;
					selection.innerHTML = "";
					for (var key in data){
						selection.add(new Option(data[key]['CODE_KOR_NAME'], key))
					}
					for (var i in selection){
						if(!isNaN(i)){
							if(selection.options[i].value === value){
								selection.selectedIndex = i;
								break;
							}
						}
					}
				}
			});
		}
	};
	
