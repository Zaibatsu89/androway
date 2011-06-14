<?php
/*
 * Name: Rinse Cramer
 * Date: 27-05-2011
 * Version: 1.0
 * 
 * HTML SVG development: display Androway device route based on database logs
 */
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
     "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<title>Androway SVG development</title>
<link rel="stylesheet" href="styles/svg.css" />
<link rel="stylesheet" href="styles/jquery.svg.css" />

<script type="text/javascript" src="scripts/lib/jquery.min.js"></script>
<script type="text/javascript" src="scripts/lib/jquery.svg.pack.js"></script>
<script type="text/javascript" src="scripts/lib/jquery.svganim.pack.js"></script>
<script type="text/javascript">
$(function()
{
	var svgFramePadding = 10;
	var browserWidth = $(window).width() - 2 * svgFramePadding;
	var browserHeight = Math.round(browserWidth / 16 * 5) - 2 * svgFramePadding;
	
	$('#svg_frame').width(browserWidth).height(browserHeight).css('margin', svgFramePadding + 'px');
	$('#svg_frame').svg({onLoad: init});
	$('button').click(drawCommand);
});

var androwayCoordinates = [];

var strAdd = '● 0, 0';
var strMove1 = '► 100, 0';
var strMove2 = '┌ 70, 25';
var strMove3 = '▲ 50, 50';
var strMove4 = '┐ 30, 70';
var strMove5 = '▼ -50, -50';
var strMove6 = '└ -70, -10';
var strMove7 = '┘ -10, -70';
var strMove8 = '┌ 25, 25';

var idTextPosition = 'textPosition';
var idTextHeading = 'textHeading';
var idAll = 'all';
var idPath = 'path';
var idCircleA = 'circleA';
var idCircleB = 'circleB';

var angle = 0;
var heading = 0;

var path;

var xFlag = false;
var yFlag = false;

var svgFramePadding = 10;
var browserWidth = $(window).width() - 2 * svgFramePadding;
var browserHeight = Math.round(browserWidth / 16 * 5) - 2 * svgFramePadding;

var xMinPrev = 0;
var xMaxPrev = 0;
var yMinPrev = 0;
var yMaxPrev = 0;

var xMin = 0;
var xMax = 0;
var yMin = 0;
var yMax = 0;

var xMinDiff = 0;
var yMaxDiff = 0;
var yMinDiff = 0;
var yMaxDiff = 0;

var xTotalPrev = 0;
var yTotalPrev = 0;
var xTotal = 0;
var yTotal = 0;
var xTotalDiff = 0;
var yTotalDiff = 0;

var xChange = 0;
var yChange = 0;

var scale = 1;
var margin = 20;

var xScale = 1;
var yScale = 1;

var scaleWidth = 0;
var scaleHeight = 0;

var xMargin = 0;
var yMargin = 0;

var scaleLock = false;

function init(svg)
{
	$('#add_segway').text(strAdd);
	$('#move_1').text(strMove1);
	$('#move_2').text(strMove2);
	$('#move_3').text(strMove3);
	$('#move_4').text(strMove4);
	$('#move_5').text(strMove5);
	$('#move_6').text(strMove6);
	$('#move_7').text(strMove7);
	$('#move_8').text(strMove8);
	$('#show_svg').text('SVG');
	
	var defs = svg.defs();
	var ptn = svg.pattern(defs, 'grid', 0, 0, 20, 20, {patternUnits: 'userSpaceOnUse'});
	var rect1 = svg.rect(ptn, 0, 0, 10, 10, {fill: 'black', opacity: '0.1'});
	var rect2 = svg.rect(ptn, 10, 0, 10, 10, {fill: 'white'});
	var rect3 = svg.rect(ptn, 10, 10, 10, 10, {fill: 'black', opacity: '0.1'});
	var rect4 = svg.rect(ptn, 0, 10, 10, 10, {fill: 'white'});
	var rectFill = svg.rect(0, 0, 0, 0, {fill: 'url(#grid)', width: '100%', height: '100%'});
	
	var all = svg.group({id: idAll, transform: 'translate(0 0) scale(' + scale + ')'});
	
	path = svg.createPath();
	svg.path(all, path, {id: idPath, stroke: '#D90000', strokeWidth: 3});
}

function drawCommand()
{
	switch ($(this).attr('id'))
	{
		case 'add_segway':
			move(0, 0);
			break;
		case 'move_1':
			move(100, 0);
			break;
		case 'move_2':
			move(70, 25);
			break;
		case 'move_3':
			move(50, 50);
			break;
		case 'move_4':
			move(30, 70);
			break;
		case 'move_5':
			move(-50, -50);
			break;
		case 'move_6':
			move(-70, -10);
			break;
		case 'move_7':
			move(-10, -70);
			break;
		case 'move_8':
			move(25, 25);
			break;
		case 'show_svg':
			displaySVG();
			break;
	}
	
	checkBorders();
	
	displayValues();
}

function move(left, right)
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
	var speed = (Math.abs(left) + Math.abs(right)) / 200;
	return speed * (left + right) * Math.sin(heading * Math.PI / 180);
}

function getY(left, right)
{
	var speed = (Math.abs(left) + Math.abs(right)) / 200;
	return -speed * (left + right) * Math.cos(heading * Math.PI / 180);
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

function displayValues()
{
	var svg = $('#svg_frame').svg('get');
	
	if (typeof(androwayCoordinates['center']) != 'undefined')
	{
		if ($('#' + idTextPosition).length <= 0)
		{
			svg.text(5, 18, 'Dimensions: (' + $('#svg_frame').width() + ', ' + $('#svg_frame').height() + ')', {style: 'font-family:Calibri;font-size:16px;'});
			svg.text(5, 36, 'Position: (' + androwayCoordinates['center'].x + ', ' + androwayCoordinates['center'].y + ')', {id: idTextPosition, style: 'font-family:Calibri;font-size:16px;'});
			svg.text(5, 54, 'Heading: ' + heading % 360, {id: idTextHeading, style: 'font-family:Calibri;font-size:16px;'});
		}
		else
		{
			$('#'+idTextPosition).text('Position: (' + androwayCoordinates['center'].x + ', ' + androwayCoordinates['center'].y + ')');
			$('#'+idTextHeading).text('Heading: ' + heading % 360 + '°');
		}
	}
}

function displaySVG()
{
	var svg = $('#svg_frame').svg('get').toSVG();
	
	$('#svg_code').html(svg.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;'));
}
</script>
</head>
<body>
<div id="svg_frame"></div>
<p id="buttons">
	<button id="move_4"></button>
	<button id="move_3"></button>
	<button id="move_1"></button>
	<button id="move_8"></button>
	<button id="add_segway"></button>
	<button id="move_2"></button>
	<button id="move_7"></button>
	<button id="move_5"></button>
	<button id="move_6"></button>
	<button id="show_svg"></button>
</p>
<p><div id="svg_code"></div></p>
</body>
</html>