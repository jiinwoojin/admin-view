/**
 * Military Single Point Symbol SIDC Event
 * */

var mySymbol = new ms.Symbol(''), activeStandard, standardVersion; // single point S/I/O/E

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
		
		var options = {};
		if ( document.getElementById("ColorMode").selectedIndex > -1 ) {
			options.colorMode = document.getElementById("ColorMode")[document.getElementById("ColorMode").selectedIndex].value;
		}
		if ( document.getElementById("MonoColor").selectedIndex > -1 ) {
			options.monoColor = document.getElementById("MonoColor")[document.getElementById("MonoColor").selectedIndex].value;
		}
		options.hqStafLength = document.getElementById("hqStafLength").value;
		if ( document.getElementById("InfoColor").selectedIndex > -1 ) {
			options.infoColor = document.getElementById("InfoColor")[document.getElementById("InfoColor").selectedIndex].value;
		}
	    options.infoSize = document.getElementById("infoSize").value;
	    options.frame =  document.getElementById("Frame").checked;
	    options.fill = document.getElementById("Fill").checked;
	    options.fillOpacity = document.getElementById("FillOpacity").value;
	    options.icon = document.getElementById("DisplayIcon").checked;
		options.civilianColor = document.getElementById("CivilianColors").checked;
		options.infoFields = document.getElementById("infoFields").checked;
		if ( document.getElementById("outlineColor").selectedIndex > -1 ) {
			options.outlineColor = document.getElementById("outlineColor")[document.getElementById("outlineColor").selectedIndex].value;
		}
	    options.outlineWidth = document.getElementById("outlineWidth").value;
	    options.size = document.getElementById("Size").value;
		options.strokeWidth = document.getElementById("StrokeWidth").value;

	
		/* //FieldID C
		options.quantity = document.getElementById("Quantity").value
		validateTexts("Quantity",(!isNaN(options.quantity) && options.quantity.length <= 9));
	
		//FieldID F
		options.reinforcedReduced = document.getElementById("ReinforcedReduced").value
		validateTexts("ReinforcedReduced",(options.reinforcedReduced.length <= 3));

		//FieldID G
		options.staffComments = document.getElementById("StaffComments").value
		validateTexts("StaffComments",(options.staffComments.length <= 20));
	
		//FieldID H
		options.additionalInformation = document.getElementById("AdditionalInformation").value
		validateTexts("AdditionalInformation",(options.additionalInformation.length <= 20));
	
		//FieldID J
		options.evaluationRating = document.getElementById("evaluationRating").value
		validateTexts("evaluationRating",(options.evaluationRating.length <= 2));
	
		//FieldID K
		options.combatEffectiveness = document.getElementById("combatEffectiveness").value
		validateTexts("combatEffectiveness",(options.combatEffectiveness.length <= 5));
	
		//FieldID L
		options.signatureEquipment = document.getElementById("signatureEquipment").value
		validateTexts("signatureEquipment",(options.signatureEquipment.length <= 1));
	
		//FieldID M
		options.higherFormation = document.getElementById("higherFormation").value
		validateTexts("higherFormation",(options.higherFormation.length <= 21));
	
		//FieldID N
		options.hostile = document.getElementById("hostile").value
		validateTexts("hostile",(options.hostile.length <= 3));
	
		//FieldID P
		options.iffSif = document.getElementById("iffSif").value
		validateTexts("iffSif",(options.iffSif.length <= 5));
	
		//FieldID Q
		options.direction = document.getElementById("Direction").value
		validateTexts("Direction",(!isNaN(options.direction) && options.direction.length <= 4));
	
		//FieldID T
		options.uniqueDesignation = document.getElementById("uniqueDesignation").value
		validateTexts("uniqueDesignation",(options.uniqueDesignation.length <= 21));
	
		//FieldID V
		options.type = document.getElementById("type").value
		validateTexts("type",(options.type.length <= 24));
	
		//FieldID W
		options.dtg = document.getElementById("Dtg").value
		validateTexts("Dtg",(options.dtg.length <= 16));
	
		//FieldID X
		options.altitudeDepth = document.getElementById("altitudeDepth").value
		validateTexts("altitudeDepth",(options.altitudeDepth.length <= 14));
	
		//FieldID Y
		options.location = document.getElementById("location").value
		validateTexts("location",(options.location.length <= 19));
	
		//FieldID Z
		options.speed = document.getElementById("speed").value
		validateTexts("speed",(options.speed.length <= 8));
	
		//FieldID AA
		options.specialHeadquarters = document.getElementById("specialHeadquarters").value
		validateTexts("specialHeadquarters",(options.specialHeadquarters.length <= 9)); */

		//FieldID B
		if ( document.getElementById("SIDCSYMBOLMODIFIER12").selectedIndex > -1 ) {
			options.sidcsymbolModifier12 = document.getElementById("SIDCSYMBOLMODIFIER12")[document.getElementById("SIDCSYMBOLMODIFIER12").selectedIndex].value;
		}

		//FieldID C
		options.quantity = document.getElementById("C").value
		validateTexts("C",(!isNaN(options.quantity) && options.quantity.length <= 9));
	
		//FieldID F
		options.reinforcedReduced = document.getElementById("F").value
		validateTexts("F",(options.reinforcedReduced.length <= 3));

		//FieldID G
		options.staffComments = document.getElementById("G").value
		validateTexts("G",(options.staffComments.length <= 20));
	
		//FieldID H
		options.additionalInformation = document.getElementById("H").value
		validateTexts("H",(options.additionalInformation.length <= 20));
	
		//FieldID J
		if ( document.getElementById("J").selectedIndex > -1 ) {
			options.evaluationRating = document.getElementById("J")[document.getElementById("J").selectedIndex].value;
		}
		//options.evaluationRating = document.getElementById("J").value
		//validateTexts("J",(options.evaluationRating.length <= 2));
	
		//FieldID K
		options.combatEffectiveness = document.getElementById("K").value
		validateTexts("K",(options.combatEffectiveness.length <= 5));
	
		//FieldID L
		options.signatureEquipment = document.getElementById("L").value
		validateTexts("L",(options.signatureEquipment.length <= 1));
	
		//FieldID M
		options.higherFormation = document.getElementById("M").value
		validateTexts("M",(options.higherFormation.length <= 21));
	
		//FieldID N
		options.hostile = document.getElementById("N").value
		validateTexts("N",(options.hostile.length <= 3));
	
		//FieldID P
		options.iffSif = document.getElementById("P").value
		validateTexts("P",(options.iffSif.length <= 5));
	
		//FieldID Q
		options.direction = document.getElementById("Q").value
		validateTexts("Q",(!isNaN(options.direction) && options.direction.length <= 4));
	
		//FieldID T
		options.uniqueDesignation = document.getElementById("T").value
		validateTexts("T",(options.uniqueDesignation.length <= 21));
	
		//FieldID V
		options.type = document.getElementById("V").value
		validateTexts("V",(options.type.length <= 24));
	
		//FieldID W
		options.dtg = document.getElementById("W").value
		validateTexts("W",(options.dtg.length <= 16));
	
		//FieldID X
		options.altitudeDepth = document.getElementById("X").value
		validateTexts("X",(options.altitudeDepth.length <= 14));
	
		//FieldID Y
		options.location = document.getElementById("Y").value
		validateTexts("Y",(options.location.length <= 19));
	
		//FieldID Z
		options.speed = document.getElementById("Z").value
		validateTexts("Z",(options.speed.length <= 8));
	
		//FieldID AA
		options.specialHeadquarters = document.getElementById("AA").value
		validateTexts("AA",(options.specialHeadquarters.length <= 9)); 
		
		// 수정 부분
		var sidc = '';
		if(activeStandard == 'letter'){ 
			// 주 메뉴
			var SIDCFUNCTIONID = function_sidc; 
			sidc = buildSymbolID(SIDCFUNCTIONID);
		}
		document.getElementById("SIDC").value = sidc;
		window.location.hash = sidc;	
		options.SIDC = sidc;
		
		mySymbol.setOptions(options);
		
		/*document.getElementById("SvgSymbol").innerHTML = mySymbol.asSVG();*/
		
		document.getElementById("ImageSymbol").style.width = mySymbol.getSize().width +'px';
		document.getElementById("ImageSymbol").style.height = mySymbol.getSize().height +'px';
		document.getElementById("ImageSymbol").src = mySymbol.asCanvas(window.devicePixelRatio).toDataURL();
	}
	
	var letterSIDC = {
		modifier11: function(){
			var JSONfile = '/json/milsymInfo/Modifier_11.json';
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
							if(selection.options[i].value == value){
								selection.selectedIndex = i;
								break;
							}
						}
					}
				}
			});
		},
		modifier12: function(){
			var JSONfile = '/json/milsymInfo/Modifier_12.json';
			$.ajax({
				url : JSONfile,
				dataType: 'json',
				async: false,
				success: function(data){
					var keyCode = document.getElementById('SIDCSYMBOLMODIFIER11').value;
					if(keyCode == 'M'){
						values = data['M'];
					} else if (keyCode == 'N'){
						values = data['N'];
					} else {
						values = data['A'];
					}
					selection = document.getElementById('SIDCSYMBOLMODIFIER12');
					value = selection.value;
					selection.innerHTML = "";
					for (var key in values){
						selection.add(new Option(values[key]['CODE_KOR_NAME'], key))
					}
					for (var i in selection){
						if(!isNaN(i)){
							if(selection.options[i].value == value){
								selection.selectedIndex = i;
								break;
							}
						}
					}
				}
			});
		},
		affiliation: function(){ 
			var JSONfile = '/json/milsymInfo/Affiliation.json';
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
							if(selection.options[i].value == value){
								selection.selectedIndex = i;
								break;
							}
						}
					}
				}
			});
		},
		status: function(){
			var JSONfile = '/json/milsymInfo/Status.json';
			$.ajax({
				url : JSONfile,
				dataType: 'json',
				async: false,
				success: function(data){
					var keyCode = document.getElementById('SIDCCODINGSCHEME').value.charAt(0);
					if(keyCode == 'S'){
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
							if(selection.options[i].value == value){
								selection.selectedIndex = i;
								break;
							}
						}
					}
				}
			});
		},
		sidccodingscheme: function(){ 
			var JSONfile = '/json/milsym/CodingScheme.json';
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
							if(selection.options[i].value == value){
								selection.selectedIndex = i;
								break;
							}
						}
					}
				}
			});
		}
	};
	
