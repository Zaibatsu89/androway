<?php
/*
 * Name: Rinse Cramer
 * Date: 12-05-2011
 * Version: 0.1
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

var strAdd = '0 0, 0';
var strMove1 = '┐ 70, 5';
var strMove2 = '│ 70, 25';
var strMove3 = '─ 50, 50';
var strMove4 = '┌ 30, 70';
var strMove5 = '─ -50, -50';
var strMove6 = '┘ -70, -10';
var strMove7 = '└ -10, -70';
var strMove8 = '│ 40, -60';

var idTextPosition = 'textPosition';
var idTextHeading = 'textHeading';
var idAll = 'all';
var idPath = 'path';
var idCircleA = 'circleA';
var idCircleB = 'circleB';

var angle = 0;
var heading = 0;

var xDiff = 0;
var yDiff = 0;
var oldX = 0;
var oldY = 0;
var oldXDiff = 0;
var oldYDiff = 0;
var xD = 0;
var yD = 0;

var path;

var xFlag = false;
var yFlag = false;

var svgFramePadding = 10;
var browserWidth = $(window).width() - 2 * svgFramePadding;
var browserHeight = Math.round(browserWidth / 16 * 5) - 2 * svgFramePadding;

var xMin = 0;
var xMax = 0;
var yMin = 0;
var yMax = 0;

var scale = 1;

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
	svg.path(all, path, {id: idPath, stroke: '#D90000', strokeWidth: 1});
}

function drawCommand()
{
	switch ($(this).attr('id'))
	{
		case 'add_segway':
			move(0, 0);
			break;
		case 'move_1':
			move(70, 5);
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
			move(40, -60);
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
	
	svg.circle(all, androwayCoordinates['center'].x, androwayCoordinates['center'].y, 3, {heading: heading % 360, fill: '#666', id: idCircleB});
	
	var p = svg.getElementById(idPath);
	var move = path.move(xBefore, yBefore).line(xAfter, yAfter);
	
	svg.change(p, {d: move._path});
	
	if ($('#' + idCircleA).length <= 0)
		
		svg.circle(all, androwayCoordinates['center'].x, androwayCoordinates['center'].y, 4, {fill: 'black', id: idCircleA});
	else
	{
		var circleA = svg.getElementById(idCircleA);
		svg.change(circleA, {cx: androwayCoordinates['center'].x, cy: androwayCoordinates['center'].y});
	}
	
	if (androwayCoordinates['center'].x < xMin)
		xMin = androwayCoordinates['center'].x;
	if (androwayCoordinates['center'].x > xMax)
		xMax = androwayCoordinates['center'].x;
	if (androwayCoordinates['center'].y < yMin)
		yMin = androwayCoordinates['center'].y;
	if (androwayCoordinates['center'].y > yMax)
		yMax = androwayCoordinates['center'].y;
		
	console.log('xMin: ' + xMin, 'xMax: ' + xMax, 'yMin: ' + yMin, 'yMax: ' + yMax);
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
	return 0.5 * (left + right) * Math.sin(heading * Math.PI / 180);
}

function getY(left, right)
{	
	return -0.5 * (left + right) * Math.cos(heading * Math.PI / 180);
}

function checkBorders()
{	
	var svg = $('#svg_frame').svg('get');
	var all = svg.getElementById(idAll);
	
	var xTotal = Math.abs(xMin - xMax);
	var yTotal = Math.abs(yMin - yMax);
	
	var width = $('#svg_frame').width();
	var height = $('#svg_frame').height();
	
	if (typeof(androwayCoordinates['center']) != 'undefined')
	{
		var zoomedIn = false;
		var margin = 20;
		
		// Zoom in		
		//if (yMin * scale > -height && yMax * scale < height)
		
		//var xScaleDiff = width - xTotal;
		//var yScaleDiff = height - yTotal;
		
		//console.log('x-diff:' + xScaleDiff);
		//console.log('y-diff:' + yScaleDiff);
		
		var xChange = 0;
		var yChange = 0;
		
		margin *= scale;
		xTotal -= (2 * margin);
		yTotal -= (2 * margin);
		
		var scaledWidth = (scale == 1 ? width : width * Math.pow(scale, -1));
		
		if (xMin < 0 + margin)
		{
			// xMin is out of the canvas
			console.log('1.       xMin < 0 + margin:                     ' + xMin + ' < ' + margin);
			
			if (xTotal < scaledWidth)
			{
				console.log('1.1.     xTotal < scaledWidth:                  ' + xTotal + ' < ' + scaledWidth);
				
				xChange = Math.abs(xMin) + margin;
				console.log('1.1.1.   xChange = abs(xMin) + margin:          ' + xChange + ' = ' + Math.abs(xMin) + ' + ' + margin);
			}
			else
			{
				console.log('1.2.     xTotal >= scaledWidth:                 ' + xTotal + ' >= ' + scaledWidth);
				
				// Scale
				var xScaleDiff = scaledWidth - xTotal;
				console.log('1.2.1.   xScaleDiff = scaledWidth - xTotal:     ' + xScaleDiff + ' = ' + scaledWidth + ' - ' + xTotal);
				
				scale += xScaleDiff / scaledWidth;
				console.log('1.2.2.   scale += xScaleDiff / scaledWidth:     ' + scale + ' += ' + xScaleDiff + ' / ' + scaledWidth);
				
				xChange = width / 2 - 0.5 * margin;
				console.log('1.2.3.   xChange = width / 2 - 0.5 * margin:   ' + xChange + ' = ' + width + ' / 2' + ' - ' + 0.5 * margin);
			}
		}
		else if (xMax > width - margin)
		{
			// xMax is out of the canvas
			console.log('2.       xMax > width - margin:                 ' + xMax + ' > ' + width + ' - ' + margin);
			
			if (xTotal < scaledWidth)
			{
				console.log('2.1.     xTotal < scaledWidth:                  ' + xTotal + ' < ' + scaledWidth);
				
				xChange = width - Math.abs(xMax) - margin;
				console.log('2.1.1.   xChange = width - abs(xMax) - margin:  ' + xChange + ' = ' + width + '-' + Math.abs(xMax) + ' - ' + margin);
			}
			else
			{
				console.log('2.2.     xTotal >= scaledWidth:                 ' + xTotal + ' >= ' + scaledWidth);
				
		 		//Scale
		 		var xScaleDiff = scaledWidth - xTotal;
				console.log('2.2.1.   xScaleDiff = xTotal - scaledWidth:     ' + xScaleDiff + ' = ' + xTotal + ' - ' + scaledWidth);
				
				scale += xScaleDiff / scaledWidth;
				console.log('2.2.2.   scale += xScaleDiff / scaledWidth:     ' + scale + ' += ' + xScaleDiff + ' / ' + scaledWidth);
				
				xChange = -(width / 2) + 1.5 * margin;
				console.log('2.2.3.   xChange = -(width / 2) + 1.5 * margin: ' + xChange + ' = -(' + width + ' / 2)' + ' + ' + 1.5 * margin);
			}
		}
		
		var scaledHeight = (scale == 1 ? height : height * Math.pow(scale, -1));
		
		if (yMin < 0 + margin)
		{
			// yMin is out of the canvas						
			console.log('3.       yMin < 0 + margin:                     ' + yMin + ' < ' + margin);
			
			if (yTotal < scaledHeight)
			{
				console.log('3.1.     yTotal < scaledHeight:                 ' + yTotal + ' < ' + scaledHeight);
				
				if (Math.abs(yMin) < Math.abs(yMax))
				{
					yChange = Math.abs(yMin) + margin;
					console.log('3.1.1.   yChange = abs(yMin) + margin:          ' + yChange + ' = ' + Math.abs(yMin) + ' + ' + margin);
				}
				else
				{
					yChange = Math.abs(yMax) + margin;
					console.log('3.1.2.   yChange = abs(yMax) - margin:          ' + yChange + ' = ' + Math.abs(yMax) + ' - ' + margin);
				}
			}
			else
			{
				console.log('3.2.     yTotal >= scaledHeight:                ' + yTotal + ' >= ' + scaledHeight);
				
				// Scale
				var yScaleDiff = scaledHeight - yTotal;
				console.log('3.2.1.   yScaleDiff = scaledHeight - yTotal:    ' + yScaleDiff + ' = ' + scaledHeight + ' - ' + yTotal);
				
				scale += yScaleDiff / scaledHeight;
				console.log('3.2.2.   scale += yScaleDiff / scaledHeight:    ' + scale + ' += ' + yScaleDiff + ' / ' + scaledHeight);
				
				if (Math.abs(yMin) > Math.abs(yMax) - 2 * margin)
				{
					yChange = (height / 2) + yScaleDiff + (2 * margin);
					console.log('3.2.3.1. yChange = height / 2 - 0.5 * margin:   ' + yChange + ' = ' + height + ' / 2' + ' - ' + 0.5 * margin);
				}
				else
				{
					yChange = 0;
					console.log('3.2.3.2. yChange = ?: ' + yChange);
				}
			}
		}
		else if(yMax > height - margin)
		{
			// yMax is out of the canvas
			console.log('4.       yMax > height - margin:                ' + yMax + ' > ' + height + ' - ' + margin);
			
			if (yTotal < scaledHeight)
			{
				console.log('4.1.     yTotal < scaledHeight:                 ' + yTotal + ' < ' + scaledHeight);
				
				yChange = height - Math.abs(yMax) - margin;
				console.log('4.1.1.   yChange = height - abs(yMax) - margin: ' + yChange + ' = ' + height + '-' + Math.abs(yMax) + ' - ' + margin);
			}
			else
			{
				console.log('4.2.     yTotal >= scaledHeight:                ' + yTotal + ' >= ' + scaledHeight);
				
		 		//Scale
		 		var yScaleDiff = scaledHeight - yTotal;
				console.log('4.2.1.   yScaleDiff = scaledHeight - yTotal:    ' + yScaleDiff + ' = ' + scaledHeight + ' - ' + yTotal);
				
				scale += yScaleDiff / scaledHeight;
				console.log('4.2.2.   scale += yScaleDiff / scaledHeight:    ' + scale + ' += ' + yScaleDiff + ' / ' + scaledHeight);
				
				yChange = -(height / 2) + 1.5 * margin;
				console.log('4.2.3.   yChange = -(height / 2) + 1.5 * margin:' + yChange + ' = -(' + height + ' / 2)' + ' + ' + 1.5 * margin);
			}
		}
		
		var scaleWidth = 0;
		var scaleHeight = 0;
		
		if (scale < 0.31)
			scale = 0.31;
//		if (scale > 3.5)
//			scale = 3.5;
		
		if (scale != 1)
		{
			scaleWidth = ((width - width * scale) - margin) / 2;
			scaleHeight = ((height - height * scale) - margin) / 2;
		}
		
		svg.change(all, {transform: 'translate(' + (xChange + scaleWidth) + ', ' + (yChange + scaleHeight) + ') scale(' + scale + ')'});	
		
		/*
		if ((androwayCoordinates['center'].y < yMax && androwayCoordinates['center'].y > yMin) || (androwayCoordinates['center'].x < xMax && androwayCoordinates['center'].x > xMin))
		{
			var xScaleDiff = width - xTotal;
			var yScaleDiff = height - yTotal;
			
			if (xScaleDiff > 0 && yScaleDiff > 0)
			{
				var xScale = xScaleDiff / width;
				var yScale = yScaleDiff / height;
				
				if (xScale > yScale)
					scale = yScale;
				else
					scale = xScale;
				
				zoomedIn = true;
			}
		}
		
		console.log('zoomed in: ' + zoomedIn);
	
		// Zoom out
		if (yTotal > (yMax / 2) && !zoomedIn)
		{
			console.log('y > (yMax / 2): ' + yTotal + ' > ' + yMax / 2);
			scale = height / (yTotal * 2);
		}		
		*/
		
		/*
		if (scale < 0.2)
			scale = 0.2;
		if (scale > 20)
			scale = 20;
		
		var scaleWidth = 0;
		var scaleHeight = 0;
		
		if(scale != 1)
		{
			scaleWidth = (width - width * scale) / 2;
			scaleHeight = (height - height * scale) / 2;
		}
		
		var xHalf = $('#svg_frame').width() / 2;
		var yHalf = $('#svg_frame').height() / 2;
		
		oldXDiff = xDiff;
		oldYDiff = yDiff;
		
		xDiff = xHalf - androwayCoordinates['center'].x;
		yDiff = yHalf - androwayCoordinates['center'].y;
		
		xDiff *= scale;
		yDiff *= scale;
		
		svg.change(all, {transform: 'translate(' + (xDiff + scaleWidth) + ', ' + (yDiff + scaleHeight) + ') scale(' + scale + ')'});
		*/
		//if (androwayCoordinates['center'].x < xMin)
//		{
//			console.log(androwayCoordinates['center'].x + ' < xMin');
//			
//			scale *= 0.99;
//			svg.change(all, {transform: 'translate(' + x + ', ' + y + ') scale(' + scale + ')'});
//			
//			xMin = androwayCoordinates['center'].x;
//		}
//		if (androwayCoordinates['center'].x > xMax)
//		{
//			console.log(androwayCoordinates['center'].x + ' > xMax');
//			
//			scale *= 0.99;
//			svg.change(all, {transform: 'translate(' + x + ', ' + y / 2 + ') scale(' + scale + ')'});
//			
//			xMax = androwayCoordinates['center'].x;
//		}
//		if (androwayCoordinates['center'].y < yMin)
//		{
//			console.log(androwayCoordinates['center'].y + ' < yMin');
//			
//			scale = height / Math.abs(yMin);
//			
//			if (scale > 1)
//				scale = 1;
//			
//			x /= scale;
//			y /= scale;
//			
//			console.log('xTransform: ' + x, 'yTransform: ' + y);
//			
//			svg.change(all, {transform: 'translate(' + x + ', ' + y + ') scale(' + scale + ')'});
//			
//			yMin = androwayCoordinates['center'].y;
//		}
//		if (androwayCoordinates['center'].y > yMax)
//		{
//			console.log(androwayCoordinates['center'].y + ' > yMax');
//			
//			scale *= 0.99;
//			svg.change(all, {transform: 'translate(' + x + ', ' + y + ') scale(' + scale + ')'});
//			
//			yMax = androwayCoordinates['center'].y;
//		}
	}
	
	console.log('~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~');
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