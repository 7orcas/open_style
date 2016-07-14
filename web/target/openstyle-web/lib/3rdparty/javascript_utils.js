
//Insert into array at given index
Array.prototype.insert = function (index, item) {
  this.splice(index, 0, item);
};


testNaN = function(value){
	return !(!isNaN(parseFloat(value)) && isFinite(value));
};