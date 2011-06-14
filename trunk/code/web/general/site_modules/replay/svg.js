var idAll = 'all';
var idPath = 'path';
var idCircleA = 'circleA';
var idCircleB = 'circleB';

var path;
var androwayCoordinates;

var lineScale;
var angle;
var heading;

var xMinPrev;
var xMaxPrev;
var yMinPrev;
var yMaxPrev;

var xMin;
var xMax;
var yMin;
var yMax;

var xMinDiff;
var yMaxDiff;
var yMinDiff;
var yMaxDiff;

var xTotalPrev;
var yTotalPrev;
var xTotal;
var yTotal;
var xTotalDiff;
var yTotalDiff;

var xChange;
var yChange;

var scale;
var margin;

var xScale;
var yScale;

var scaleWidth;
var scaleHeight;

var xMargin;
var yMargin;

var scaleLock = false;

$.fn.initSVG = function()
{
	idAll = 'all';
	idPath = 'path';
	idCircleA = 'circleA';
	idCircleB = 'circleB';
	
	androwayCoordinates = [];
	
	lineScale = 1;
	angle = 0;
	heading = 0;
	
	xMinPrev = 0;
	xMaxPrev = 0;
	yMinPrev = 0;
	yMaxPrev = 0;
	
	xMin = 0;
	xMax = 0;
	yMin = 0;
	yMax = 0;
	
	xMinDiff = 0;
	yMaxDiff = 0;
	yMinDiff = 0;
	yMaxDiff = 0;
	
	xTotalPrev = 0;
	yTotalPrev = 0;
	xTotal = 0;
	yTotal = 0;
	xTotalDiff = 0;
	yTotalDiff = 0;
	
	xChange = 0;
	yChange = 0;
	
	scale = 1;
	margin = 20;
	
	xScale = 1;
	yScale = 1;
	
	scaleWidth = 0;
	scaleHeight = 0;
	
	xMargin = 0;
	yMargin = 0;
	
	scaleLock = false;
}

$.fn.move = function(left, right)
{ 
	var svg = $('#svg_frame').svg('get');
	var all = svg.getElementById(idAll);
	
	var xBefore = $('#svg_frame').width() / 2;
	var yBefore = $('#svg_frame').height() / 2;
	
	if (typeof(androwayCoordinates['center']) == 'undefined')
	{
		var width = $('#svg_frame').width() / 2;
		var height = $('#svg_frame').height() / 2;
		
		xMin = width;
		yMin = height;
		
		androwayCoordinates['center'] = {x: width, y: height};
	}
	else
	{
		setHeading(left, right);
		
		xBefore = androwayCoordinates['center'].x;
		yBefore = androwayCoordinates['center'].y;
		
		androwayCoordinates['center'] = {x: androwayCoordinates['center'].x + getX(left, right), y: androwayCoordinates['center'].y + getY(left, right)};
	}
	
	var xAfter = androwayCoordinates['center'].x;
	var yAfter = androwayCoordinates['center'].y;
	
	svg.circle(all, androwayCoordinates['center'].x, androwayCoordinates['center'].y, 5, {heading: heading % 360, fill: '#666', id: idCircleB});
	
	var p = svg.getElementById(idPath);
	var move = path.move(xBefore, yBefore).line(xAfter, yAfter);
	
	svg.change(p, {d: move._path});
	
	if ($('#' + idCircleA).length <= 0)
		
		svg.circle(all, androwayCoordinates['center'].x, androwayCoordinates['center'].y, 7, {fill: 'black', id: idCircleA});
	else
	{
		var circleA = svg.getElementById(idCircleA);
		svg.change(circleA, {cx: androwayCoordinates['center'].x, cy: androwayCoordinates['center'].y});
	}
	
	xMinPrev = xMin;
	xMaxPrev = xMax;
	yMinPrev = yMin;
	yMaxPrev = yMax;
	
	if (androwayCoordinates['center'].x < xMin)
		xMin = androwayCoordinates['center'].x;
	if (androwayCoordinates['center'].x > xMax)
		xMax = androwayCoordinates['center'].x;
	if (androwayCoordinates['center'].y < yMin)
		yMin = androwayCoordinates['center'].y;
	if (androwayCoordinates['center'].y > yMax)
		yMax = androwayCoordinates['center'].y;
	
	xMinDiff = xMin - xMinPrev;
	xMaxDiff = xMax - xMaxPrev;
	yMinDiff = yMin - yMinPrev;
	yMaxDiff = yMax - yMaxPrev;
	
	checkBorders();
}

function setHeading(left, right)
{
	if (left < 0)
		angle = (right - left) * 0.9;
	else if (left > 0)
		angle = (left - right) * 0.9;
	else
		angle = 0;
	
	heading += angle;
}

function getX(left, right)
{
	var speed = (Math.abs(left) + Math.abs(right)) / 2;
	return lineScale * speed / 200 * (left + right) * Math.sin(heading * Math.PI / 180);
}

function getY(left, right)
{
	var speed = (Math.abs(left) + Math.abs(right)) / 2;
	return -lineScale * speed / 200 * (left + right) * Math.cos(heading * Math.PI / 180);
}

function checkBorders()
{	
	var svg = $('#svg_frame').svg('get');
	var all = svg.getElementById(idAll);
	
	var width = $('#svg_frame').width();
	var height = $('#svg_frame').height();
	
	xTotalPrev = xTotal;
	yTotalPrev = yTotal;
	xTotal = Math.abs(xMin - xMax);
	yTotal = Math.abs(yMin - yMax);
	xTotalDiff = Math.abs(xTotal - xTotalPrev);
	yTotalDiff = Math.abs(yTotal - yTotalPrev);
	
	if (typeof(androwayCoordinates['center']) != 'undefined')
	{
		if (!scaleLock)
		{
			if (xScale < yScale)
				xMargin = margin / xScale;
			else if (xScale > yScale)
				yMargin = margin / yScale;
		}
		
		var scaledWidth = width * (1 / scale);
		var scaledHeight = height * (1 / scale);
		
		var xScaleDiff = scaledWidth * scale + xTotal - 2 * xMargin;
		var yScaleDiff = scaledHeight * scale + yTotal - 2 * yMargin;
		
		if (!scaleLock)
		{	
			xScale = width / xScaleDiff;
			yScale = height / yScaleDiff;
		}
		
		if (xMin <= 0 + xMargin)
		{
			xTotal -= xTotalDiff + xMinDiff;
			xChange = -(width / 2) + xTotal * xScale + 0.5 * xMargin + margin;
		}
		else if (xMax >= width - xMargin)
		{
			xTotal -= xTotalDiff + -xMaxDiff;
			xChange = width / 2 - xTotal * xScale + 0.5 * xMargin - margin;
		}
		
		if (yMin <= 0 + yMargin)
		{
			yTotal -= yTotalDiff + yMinDiff;
			yChange = -(height / 2) + yTotal * yScale + 0.5 * yMargin + margin;
		}
		else if (yMax >= height - yMargin)
		{
			yTotal -= yTotalDiff + -yMaxDiff;
			yChange = height / 2 - yTotal * yScale + 0.5 * yMargin - margin;
		}
		
		if (!scaleLock)
		{			
			if (xScale <= yScale)
				scale = xScale;
			else
				scale = yScale;
		}
		
		if (scale != 1)
		{
			scaleWidth = ((width - width * scale) - xMargin) / 2;
			scaleHeight = ((height - height * scale) - yMargin) / 2;
		}
		
		if (xTotal > width)
		{
			scaleLock = true;
			scaleWidth = 0;
			if (Math.abs(xMin) > Math.abs(xMax))
				xChange = (width / 2 * xScale) + xTotal * xScale;
			else
				xChange = (width / 2 * xScale) - xTotal * xScale;
		}
		if (yTotal > height)
		{
			scaleLock = true;
			scaleHeight = 0;
			if (Math.abs(yMin) > Math.abs(yMax))
				yChange = (height / 2 * yScale) + yTotal * yScale;
			else
				yChange = (height / 2 * yScale) - yTotal * yScale;
		}
		
		svg.change(all, {transform: 'translate(' + (xChange + scaleWidth) + ', ' + (yChange + scaleHeight) + ') scale(' + scale + ')'});
	}
}