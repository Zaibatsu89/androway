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

#svg_frame { width: 100%; height: 500px; border: 1px solid; }
</style>
<script type="text/javascript" src="scripts/lib/jquery.1.5.1.min.js"></script>
<script type="text/javascript" src="scripts/lib/jquery.svg.pack.js"></script>
<script type="text/javascript">
$(function() {
	$('#svg_frame').svg({function:init()});
	$('button').click(drawCommand);
});

var strAdd = 'Set left wheel motor power at 0% and right wheel motor power at 0%';
var strMove1 = 'Set left wheel motor power at 70% and right wheel motor power at 10%';
var strMove2 = 'Set left wheel motor power at 55% and right wheel motor power at 35%';
var strMove3 = 'Set left wheel motor power at 50% and right wheel motor power at 50%';

function init()
{
	$('#add_segway').text(strAdd);
	$('#move_1').text(strMove1);
	$('#move_2').text(strMove2);
	$('#move_3').text(strMove3);
}

function drawCommand() {
	
	var command = this.id;
	var svg = $('#svg_frame').svg('get');
	
	if (command != null)
	{
		if (command == 'add_segway')
		{
			if($('#' + command).text() == 'Undo move')
			{
				$('#' + command).text(strAdd);
				move(0, false, 0, 0);
			}
			else
			{
				$('#' + command).text('Undo move');
				move(0, true, 0, 0);
			}
		}
		else if (command == 'move_1') {
			if($('#' + command).text() == 'Undo move')
			{
				$('#' + command).text(strMove1);
				move(1, false, 70, 10);
			}
			else
			{
				$('#' + command).text('Undo move');
				move(1, true, 70, 10);
			}
		}
		else if (command == 'move_2') {
			if($('#' + command).text() == 'Undo move')
			{
				$('#' + command).text(strMove2);
				move(2, false, 55, 35);
			}
			else
			{
				$('#' + command).text('Undo move');
				move(2, true, 55, 35);
			}
		}
		else if (command == 'move_3') {
			if($('#' + command).text() == 'Undo move')
			{
				$('#' + command).text(strAdd);
				move(3, false, 50, 50);
			}
			else
			{
				$('#' + command).text('Undo move');
				move(3, true, 50, 50);
			}
		}
	}
}

function drawSegway() {
	var svg = $('#svg_frame').svg('get');
	var x = Math.round(document.body.clientWidth / 2);
	var y = Math.round(document.body.clientHeight / 2);
	svg.circle(x, y, 3, {fill: 'black', stroke: 'black', 'stroke-width': 1});
	var g = svg.group({stroke: 'black', 'stroke-width': 2});
	svg.line(g, x - 20, y - 4, x - 20, y + 4);
	svg.line(g, x + 20, y - 4, x + 20, y + 4);
}

function move(id, draw, left, right)
{
	var x = Math.round(document.body.clientWidth / 2);
	var y = Math.round(document.body.clientHeight / 2);
	var x = x + left * 2;
	var y = y - right * 2;
	var x1 = x - 20;
	var y1 = y - 4;
	var x2 = x - 20;
	var y2 = y + 4;
	var x3 = x + 20;
	var y3 = y - 4;
	var x4 = x + 20;
	var y4 = y + 4;
	
	var svg = $('#svg_frame').svg('get');
	var line1 = "line(g, x1, y1, x2, y2)";
	var line2 = "line(g, x3, y3, x4, y4)";
	
	if (draw)
	{	
		var circle = svg.circle(x, y, 3, {fill: 'black', stroke: 'black', 'stroke-width': 1});
		var g = svg.group({stroke: 'black', 'stroke-width': 2});
		eval('svg.' + line1);
		eval('svg.' + line2);
	}
	else
	{
		var circle = svg.circle(x, y, 4, {fill: 'white', stroke: 'white', 'stroke-width': 1});
		var g = svg.group({stroke: 'white', 'stroke-width': 2});
		eval('svg.' + line1);
		eval('svg.' + line2);
	}
}

function random(range) {
	return Math.floor(Math.random() * range);
}
</script>
</head>
<body>
<div id="svg_frame"></div>
<p>
	<button id="add_segway"></button>
	<button id="move_1"></button>
	<button id="move_2"></button>
	<button id="move_3"></button>
</p>
</body>
</html>