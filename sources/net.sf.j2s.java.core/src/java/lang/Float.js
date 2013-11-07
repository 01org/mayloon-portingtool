Clazz.load (["java.lang.Comparable", "$.Number"], "java.lang.Float", null, function () {
java.lang.Float = Float = function () {
Clazz.instantialize (this, arguments);
};
Clazz.decorateAsType (Float, "Float", Number, Comparable, null, true);
Float.prototype.valueOf = function () { return 0; };
Float.toString = Float.prototype.toString = function () {
	if (arguments.length != 0) {
		return "" + arguments[0];
	} else if (this === Float) {
		return "class java.lang.Float"; // Float.class.toString
	}
	return "" + this.valueOf ();
};
Clazz.makeConstructor (Float, 
function () {
this.valueOf = function () {
	return 0.0;
};
});
Clazz.makeConstructor (Float, 
function (value) {
this.valueOf = function () {
	return value;
};
}, "Number");
Clazz.makeConstructor (Float, 
function (s) {
var value = null;
if (s != null) {
	value = Float.parseFloat (s);
} else {
	value = 0;
}
this.valueOf = function () {
	return value;
};
}, "String");
Float.serialVersionUID = Float.prototype.serialVersionUID = -2671257302660747028;
Float.MIN_VALUE = Float.prototype.MIN_VALUE = 1.4e-45;
Float.MAX_VALUE = Float.prototype.MAX_VALUE = 3.4028235e+38;
Float.NEGATIVE_INFINITY = Number.NEGATIVE_INFINITY;
Float.POSITIVE_INFINITY = Number.POSITIVE_INFINITY;
Float.NaN = Number.NaN;
Float.TYPE = Float.prototype.TYPE = Float;

Clazz.defineMethod (Float, "parseFloat", 
function (s) {
if (s == null) {
throw  new NumberFormatException ("null");
}
s = s.trim();
if (s == "NaN" || s == "+NaN" || s == "-NaN") {
    return Number.NaN;
}
var lastChar = s.charAt(s.length-1).toLowerCase();
if (lastChar == 'd' || lastChar == 'f') {
    s = s.substring(0, s.length - 1);
}
if (isNaN(s) || s.length == 0) {
    throw new NumberFormatException("Not a Number : " + s);
}
var floatVal = parseFloat (s);
return floatVal;
}, "String");
Float.parseFloat = Float.prototype.parseFloat;

Clazz.defineMethod (Float, "$valueOf", 
function (s) {
return new Float(Float.parseFloat (s, 10));
}, "String");

Clazz.defineMethod (Float, "$valueOf", 
function (s) {
if (s == null) {
   throw new NullPointerException("null");
}
return new Float(s);
}, "Number");

Float.$valueOf = Float.prototype.$valueOf;
Clazz.defineMethod (Float, "isNaN", 
function (num) {
return isNaN (num);
}, "Number");
Float.isNaN = Float.prototype.isNaN;
Clazz.defineMethod (Float, "isInfinite", 
function (num) {
return !isFinite (num);
}, "Number");
Float.isInfinite = Float.prototype.isInfinite;

Clazz.defineMethod (Float, "equals", 
function (s) {
if(s == null || ! Clazz.instanceOf(s, Float) ){
	return false;
}
return Number.compare(s.valueOf(), this.valueOf()) == 0;
}, "Object");
});
