<?php
/*
 * Name: Rinse Cramer
 * Date: 15-04-2011
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
<style type="text/css">
@import "styles/jquery.svg.css";

#svg_frame { height: 500px; border: 1px solid; }
</style>
<script type="text/javascript" src="scripts/lib/jquery.1.5.1.min.js"></script>
<script type="text/javascript" src="scripts/lib/jquery.svg.pack.js"></script>
<script type="text/javascript" src="scripts/lib/jquery.svganim.pack.js"></script>
<script type="text/javascript">
$(function() {
	$('#svg_frame').svg({function:init()});
	$('button').click(drawCommand);
});

var androwayCoordinates = [];
var lastLeft = 0;
var lastRight = 0;
var totalLeft = 0;
var totalRight = 0;

var strAdd = 'Set left wheel motor power at 0% and right wheel motor power at 0%';
var strMove1 = 'Set left wheel motor power at 70% and right wheel motor power at 10%';
var strMove2 = 'Set left wheel motor power at 55% and right wheel motor power at 35%';
var strMove3 = 'Set left wheel motor power at 50% and right wheel motor power at 50%';
var strMove4 = 'Set left wheel motor power at -50% and right wheel motor power at -50%';

var idG = 'gGroup';

var idLeftText = 'leftText';
var idCenterText = 'centerText';
var idRightText = 'rightText';

var idLeftWheel = 'leftWheel';
var idCenterCircle = 'centerCircle';
var idRightWheel = 'rightWheel';

function init()
{
	$('#add_segway').text(strAdd);
	$('#move_1').text(strMove1);
	$('#move_2').text(strMove2);
	$('#move_3').text(strMove3);
	$('#move_4').text(strMove4);
	$('#show_svg').text('Show SVG');
}

function drawCommand()
{	
	switch ($(this).attr('id'))
	{
		case 'add_segway':
			move(0, 0);
			break;
		case 'move_1':
			move(70, 10);
			break;
		case 'move_2':
			move(55, 35);
			break;
		case 'move_3':
			move(50, 50);
			break;
		case 'move_4':
			move(-50, -50);
			break;
		case 'show_svg':
			displaySVG();
			break;
	}
	
	displayValues();
	
	checkBorders();
}

function move(left, right)
{
	lastLeft = left;
	lastRight = right;
	
	var svg = $('#svg_frame').svg('get');

	if (typeof(androwayCoordinates['center']) == 'undefined')
	{
		androwayCoordinates['center'] = {x: $('#svg_frame').width() / 2, y: $('#svg_frame').height() / 2};
		androwayCoordinates['left'] = {x1: androwayCoordinates['center'].x - 20, y1: androwayCoordinates['center'].y - 4, x2: androwayCoordinates['center'].x - 20, y2: androwayCoordinates['center'].y + 4};
		androwayCoordinates['right'] = {x1: androwayCoordinates['center'].x + 20, y1: androwayCoordinates['center'].y - 4, x2: androwayCoordinates['center'].x + 20, y2: androwayCoordinates['center'].y + 4};
	}
	else if (left != 0 && right != 0)
	{
		totalLeft += left / 100;
		totalRight += right / 100;
	
		androwayCoordinates['left'] = {x1: androwayCoordinates['left'].x1 + Math.cos(totalLeft), y1: androwayCoordinates['left'].y1 + Math.sin(totalLeft) - 0.5, x2: androwayCoordinates['left'].x2 + Math.cos(totalLeft), y2: androwayCoordinates['left'].y2 + Math.sin(totalLeft) - 0.5};
		androwayCoordinates['right'] = {x1: androwayCoordinates['right'].x1 + Math.cos(totalRight), y1: androwayCoordinates['right'].y1 + Math.sin(totalRight) - 0.5, x2: androwayCoordinates['right'].x2 + Math.cos(totalRight), y2: androwayCoordinates['right'].y2 + Math.sin(totalRight) - 0.5};
	}
	
	// put center circle in the horizontal middle between the left and right wheel
	androwayCoordinates['center'].x = (((androwayCoordinates['left'].x1 + androwayCoordinates['left'].x2) / 2) + ((androwayCoordinates['right'].x1 + androwayCoordinates['right'].x2) / 2)) / 2;
	// put center circle in the vertical middle between the left and right wheel
	androwayCoordinates['center'].y = (((androwayCoordinates['left'].y1 + androwayCoordinates['left'].y2) / 2) + ((androwayCoordinates['right'].y1 + androwayCoordinates['right'].y2) / 2)) / 2;
	
	if ($('#' + idLeftWheel).length <= 0)
	{
		var g = svg.group({id: idG, stroke: 'black', 'stroke-width': 1});
		
		svg.line(g, androwayCoordinates['left'].x1, androwayCoordinates['left'].y1, androwayCoordinates['left'].x2, androwayCoordinates['left'].y2, {id: idLeftWheel});
		svg.circle(g, androwayCoordinates['center'].x, androwayCoordinates['center'].y, 3, {fill: 'black', id: idCenterCircle});
		svg.line(g, androwayCoordinates['right'].x1, androwayCoordinates['right'].y1, androwayCoordinates['right'].x2, androwayCoordinates['right'].y2, {id: idRightWheel});
	}
	else
	{
		var g = svg.getElementById(idG);
		var leftWheel = svg.getElementById(idLeftWheel);
		var centerCircle = svg.getElementById(idCenterCircle);
		var rightWheel = svg.getElementById(idRightWheel);
		
		svg.change(leftWheel, {x1: androwayCoordinates['left'].x1, y1: androwayCoordinates['left'].y1, x2: androwayCoordinates['left'].x2, y2: androwayCoordinates['left'].y2});
		svg.change(centerCircle, {cx: androwayCoordinates['center'].x, cy: androwayCoordinates['center'].y});
		svg.change(rightWheel, {x1: androwayCoordinates['right'].x1, y1: androwayCoordinates['right'].y1, x2: androwayCoordinates['right'].x2, y2: androwayCoordinates['right'].y2});
	}
}

function checkBorders()
{	
	var width = $('#svg_frame').width();
	var height = $('#svg_frame').height();
	
	if (typeof(androwayCoordinates['left']) != 'undefined')
	{
		switch (true)
		{
			case androwayCoordinates['left'].x1 < 0:
				console.log('x1 left = ' + androwayCoordinates['left'].x1);
				break;
			case androwayCoordinates['left'].x1 > width:
				console.log('x1 left = ' + androwayCoordinates['left'].x1);
				break;
			case androwayCoordinates['left'].y1 < 4:
				androwayCoordinates['left'] = {x1: androwayCoordinates['center'].x - 20, y1: androwayCoordinates['left'].y1 + lastLeft / 10, x2: androwayCoordinates['center'].x - 20, y2: androwayCoordinates['left'].y2 + lastLeft / 10};
				androwayCoordinates['right'] = {x1: androwayCoordinates['center'].x + 20, y1: androwayCoordinates['right'].y1 + lastRight / 10, x2: androwayCoordinates['center'].x + 20, y2: androwayCoordinates['right'].y2 + lastRight / 10};
				break;
			case androwayCoordinates['left'].y2 > height - 4:
				androwayCoordinates['left'] = {x1: androwayCoordinates['center'].x - 20, y1: androwayCoordinates['left'].y1 - lastLeft / 10 - 10, x2: androwayCoordinates['center'].x - 20, y2: androwayCoordinates['left'].y2 - lastLeft / 10 - 10};
				androwayCoordinates['right'] = {x1: androwayCoordinates['center'].x + 20, y1: androwayCoordinates['right'].y1 - lastRight / 10 - 10, x2: androwayCoordinates['center'].x + 20, y2: androwayCoordinates['right'].y2 - lastRight / 10 - 10};
				break;
		}
	}
	
	if (typeof(androwayCoordinates['right']) != 'undefined')
	{
		switch (true)
		{
			case androwayCoordinates['right'].x1 < 0:
				console.log('x1 right = ' + androwayCoordinates['right'].x1);
				break;
			case androwayCoordinates['right'].x1 > width:
				console.log('x1 right = ' + androwayCoordinates['right'].x1);
				break;
			case androwayCoordinates['right'].y1 < 4:
				androwayCoordinates['left'] = {x1: androwayCoordinates['center'].x - 20, y1: androwayCoordinates['left'].y1 + lastLeft / 10, x2: androwayCoordinates['center'].x - 20, y2: androwayCoordinates['left'].y2 + lastLeft / 10};
				androwayCoordinates['right'] = {x1: androwayCoordinates['center'].x + 20, y1: androwayCoordinates['right'].y1 + lastRight / 10, x2: androwayCoordinates['center'].x + 20, y2: androwayCoordinates['right'].y2 + lastRight / 10};
				break;
			case androwayCoordinates['right'].y2 > height - 4:
				androwayCoordinates['left'] = {x1: androwayCoordinates['center'].x - 20, y1: androwayCoordinates['left'].y1 - lastLeft / 10 - 10, x2: androwayCoordinates['center'].x - 20, y2: androwayCoordinates['left'].y2 - lastLeft / 10 - 10};
				androwayCoordinates['right'] = {x1: androwayCoordinates['center'].x + 20, y1: androwayCoordinates['right'].y1 - lastRight / 10 - 10, x2: androwayCoordinates['center'].x + 20, y2: androwayCoordinates['right'].y2 - lastRight / 10 - 10};
				break;
		}
	}
}

function displayValues()
{
	var svg = $('#svg_frame').svg('get');
	
	if ($('#' + idLeftText).length <= 0)
	{
		svg.text(5, 18, 'Left: (' + androwayCoordinates['left'].x1 + ', ' + androwayCoordinates['left'].y1 + ', ' + androwayCoordinates['left'].x2 + ', ' + androwayCoordinates['left'].y2 + ')', {id: idLeftText, style: 'font-family:Calibri;font-size:16px;'});
		svg.text(5, 40, 'Center: (' + androwayCoordinates.center.x + ', ' + androwayCoordinates.center.y + ')', {id: idCenterText, style: 'font-family:Calibri;font-size:16px;'});
		svg.text(5, 62, 'Right: (' + androwayCoordinates['right'].x1 + ', ' + androwayCoordinates['right'].y1 + ', ' + androwayCoordinates['right'].x2 + ', ' + androwayCoordinates['right'].y2 + ')', {id: idRightText, style: 'font-family:Calibri;font-size:16px;'});
	}
	else
	{
		$('#'+idLeftText).text('Left: (' + androwayCoordinates['left'].x1 + ', ' + androwayCoordinates['left'].y1 + ', ' + androwayCoordinates['left'].x2 + ', ' + androwayCoordinates['left'].y2 + ')');
		$('#'+idCenterText).text('Center: (' + androwayCoordinates.center.x + ', ' + androwayCoordinates.center.y + ')');
		$('#'+idRightText).text('Right: (' + androwayCoordinates['right'].x1 + ', ' + androwayCoordinates['right'].y1 + ', ' + androwayCoordinates['right'].x2 + ', ' + androwayCoordinates['right'].y2 + ')');
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
	<button id="add_segway"></button>
	<button id="move_1"></button>
	<button id="move_2"></button>
	<button id="move_3"></button>
	<button id="move_4"></button>
	<button id="show_svg"></button>
</p>
<p><div id="svg_code"></p>
</body>
</html>