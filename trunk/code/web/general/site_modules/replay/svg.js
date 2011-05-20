var idAll = 'all';
var idPath = 'path';
var idCircleA = 'circleA';
var idCircleB = 'circleB';

var path;
var androwayCoordinates = [];

var angle = 0;
var heading = 0;

var xMin = 0;
var xMax = 0;
var yMin = 0;
var yMax = 0;

var scale = 1;

$.fn.initSVG = function()
{	
	var parentWidth = $(this).parent().width() - 5;
	var parentHeight = Math.round(parentWidth / 16 * 5);
	
	$(this).width(parentWidth).height(parentHeight);
	$(this).svg();
	
	var svg = $('#svg_frame').svg('get');
	var defs = svg.defs();
	var rectFill = svg.rect(0, 0, 0, 0, {fill: 'white', width: '100%', height: '100%'});
	
	var all = svg.group({id: idAll, transform: 'translate(0 0) scale(' + scale + ')'});
	
	path = svg.createPath();
	svg.path(all, path, {id: idPath, stroke: '#D90000', strokeWidth: 1});
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
	
	checkBorders();
	
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
		
		var xChange = 0;
		var yChange = 0;
		
		margin *= scale;
		xTotal += (2 * margin);
		yTotal += (2 * margin);
		
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
					yChange = Math.abs(yMax) - margin;
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
	}
	
	console.log('~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~');
}