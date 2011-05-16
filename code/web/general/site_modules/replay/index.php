<?php
/*
 * Name: Rinse Cramer
 * Date: 15-04-2011
 * Version: 0.1
 * 
 * HTML SVG development: display Androway device route based on database logs
 */

if(isset($_REQUEST["module_id"]))
	echo "<input class=\"module_id\" type=\"hidden\" value=\"" . $_REQUEST["module_id"] ."\">";
?>

<link rel="stylesheet" href="styles/svg.css" />
<link rel="stylesheet" href="styles/jquery.svg.css" />

<script type="text/javascript">
	$(function()
	{
		initReplayModule();
	});
</script>
<script type="text/javascript" src="scripts/lib/jquery.svg.pack.js"></script>
<script type="text/javascript" src="scripts/lib/jquery.svganim.pack.js"></script>
<script type="text/javascript" src="site_modules/replay/svg.js"></script>

<div id="svg_frame"></div>
<p id="buttons">
	<button id="play"></button>
	<button id="pause"></button>
	<button id="show_svg"></button>
</p>
<div id="svg_code"></div>
<div id="replay_data"></div>