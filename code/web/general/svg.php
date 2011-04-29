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

var idText = 'text';
var idCircleGroup = 'all';
var idCircleA = 'circleA';
var idCircleB = 'circleB';

var xDiff = 0;
var yDiff = 0;
var oldX = 0;
var oldY = 0;
var oldXDiff = 0;
var oldYDiff = 0;
var xD = 0;
var yD = 0;

var xFlag = false;
var yFlag = false;

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
	$('#show_svg').text('Show SVG');
	
	var defs = svg.defs();
	var ptn = svg.pattern(defs, 'grid', 0, 0, 20, 20, {patternUnits: 'userSpaceOnUse'});
	var rect1 = svg.rect(ptn, 0, 0, 10, 10, {fill: 'black', opacity: '0.1'});
	var rect2 = svg.rect(ptn, 10, 0, 10, 10, {fill: 'white'});
	var rect3 = svg.rect(ptn, 10, 10, 10, 10, {fill: 'black', opacity: '0.1'});
	var rect4 = svg.rect(ptn, 0, 10, 10, 10, {fill: 'white'});
	var rectFill = svg.rect(0, 0, 0, 0, {fill: 'url(#grid)', width: '100%', height: '100%'});
	
	var circleGroup = svg.group({id: idCircleGroup, transform: 'translate(0 0) rotate(0)'});
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
	
	displayValues();
	
	//checkBorders();
	
	moveCircleGroup();
}

function move(left, right)
{	
	var svg = $('#svg_frame').svg('get');
	var circleGroup = svg.getElementById(idCircleGroup);
	
	if (typeof(androwayCoordinates['center']) == 'undefined')
	{
		//androwayCoordinates['center'] = {x: $('#svg_frame').width() / 2, y: $('#svg_frame').height() / 2};
		androwayCoordinates['center'] = {x: 0, y: 0};
		oldX = androwayCoordinates['center'].x;
		oldY = androwayCoordinates['center'].y;
	}
	else
	{
		oldX = androwayCoordinates['center'].x;
		oldY = androwayCoordinates['center'].y;
		androwayCoordinates['center'] = {x: androwayCoordinates['center'].x + getX(left, right), y: androwayCoordinates['center'].y + getY(left, right)};
		
		console.log(androwayCoordinates['center'].x, androwayCoordinates['center'].y);
	}
	
	svg.circle(circleGroup, androwayCoordinates['center'].x, androwayCoordinates['center'].y, 2, {fill: 'gray', id: idCircleB});
	
	if ($('#' + idCircleA).length <= 0)
		
		svg.circle(circleGroup, androwayCoordinates['center'].x, androwayCoordinates['center'].y, 3, {fill: 'black', id: idCircleA});
	else
	{
		var circleA = svg.getElementById(idCircleA);
		svg.change(circleA, {cx: androwayCoordinates['center'].x, cy: androwayCoordinates['center'].y});
	}
}

function getX(left, right)
{
	var x;
	var angle = 0;
	
	if (left < 0)
		angle = (right - left) * 0.9;
	if (left > 0)
		angle = (left - right) * 0.9;
	
	x = 0.3 * Math.sin(angle) * left;
	
	if (Math.abs(left) < Math.abs(right))
		x *= 2;
	
	return x;
}1

function getY(left, right)
{
	var y;
	var angle = 0;
	
	if (left > 0)
		angle = (right - left) * 0.9;
	if (left < 0)
		angle = (left - right) * 0.9;
	
	y = 0.3 * Math.cos(angle) * right;
	
	if (Math.abs(left) > Math.abs(right))
		y *= 2;
	
	return -y;
}

function moveCircleGroup()
{
	var svg = $('#svg_frame').svg('get');
	
	var circleGroup = svg.getElementById(idCircleGroup);
	
	var x = $('#svg_frame').width() / 2;
	var y = $('#svg_frame').height() / 2;
	
	oldXDiff = xDiff;
	oldYDiff = yDiff;
	
	xDiff = x - androwayCoordinates['center'].x;
	yDiff = y - androwayCoordinates['center'].y;
	
	xD = xDiff - oldXDiff;
	yD = yDiff - oldYDiff;
	
	svg.change(circleGroup, {transform: 'translate(' + xDiff + ' ' + yDiff + ')'});
}

function checkBorders()
{	
	var width = $('#svg_frame').width();
	var height = $('#svg_frame').height();
	
	if (typeof(androwayCoordinates['center']) != 'undefined')
	{
		if (androwayCoordinates['center'].x < 6)
			androwayCoordinates['center'] = {x: 7.5, y: androwayCoordinates['center'].y};
		if (androwayCoordinates['center'].x > width - 6)
			androwayCoordinates['center'] = {x: width - 7.5, y: androwayCoordinates['center'].y};
		if (androwayCoordinates['center'].y < 6)
			androwayCoordinates['center'] = {x: androwayCoordinates['center'].x, y: 10};
		if (androwayCoordinates['center'].y > height - 6)
			androwayCoordinates['center'] = {x: androwayCoordinates['center'].x, y: height - 10};
	}
}

function displayValues()
{
	var svg = $('#svg_frame').svg('get');
	
	if (typeof(androwayCoordinates['center']) != 'undefined')
	{
		if ($('#' + idText).length <= 0)
			svg.text(5, 18, 'Position: (' + androwayCoordinates['center'].x + ', ' + androwayCoordinates['center'].y + ')', {id: idText, style: 'font-family:Calibri;font-size:16px;'});
		else
			$('#'+idText).text('Position: (' + androwayCoordinates['center'].x + ', ' + androwayCoordinates['center'].y + ')');
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
<p><div id="svg_code"></p>
</body>
</html>