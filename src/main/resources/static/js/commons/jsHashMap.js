/**
 * 
 */

var jsMap = function() {
	this.map = [];
};

jsMap.prototype = {
	// key, value 값으로 구성된 데이터 추가
	put: function(key, value){
		this.map[key] = value;
	},
	// key 값의 value 값을 반환
	get: function(key){
		return this.map[key];
	},
	// key 값이 존재하는지 확인
	containsKey: function(key){
		return key in this.map;
	},
	// value 값이 존재하는지 확인
	containsValue: function(value){
		for(var prop in this.map){
			if(this.map[prop] === value){
				return true;
			}
		}
		return false;
	},
	// 데이터 초기화
	clear: function(){
		for(var prop in this.map){
			delete this.map[prop];
		}
	},
	// key 에 해당하는 데이터 삭제
	remove: function(key){
		delete this.map[key];
	},
	// 배열로 key 값 반환
	keys: function(){
		var arryKey = [];
		for(var prop in this.map){
			arryKey.push(prop);
		}
		return arryKey;
	},
	// 배열로 value 값 반환
	values: function(){
		var arryValue = [];
		for(var prop in this.map){
			arryValue.push(this.map[prop]);
		}
		return arryValue;
	},
	// map 에 저장되어 있는 개수 반환
	size: function(){
		var count = 0;
		for(var prop in this.map){
			count++;
		}
		return count;
	}
};