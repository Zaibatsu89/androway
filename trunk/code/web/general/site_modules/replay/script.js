var counter;
var num;
var totalNum;
var dataForReplay = null;
var isPaused = false;

function loadReplay(element)
{
	$.getJSON('webservices/siteService.php', {action: 'loadModuleData', module_name : 'replay', function_name: 'loadData', module_id : 1, row_id: element.attr('name')}, function(data)
	{
		dataForReplay = data;
		
		$('#pageMain').after
		(
				'<div data-role="page" id="replayPage">'
			+		'<div data-role="header" class="page-header"></div>'
			+		'<div data-role="content" class="page-content"></div>'
			+	'</div>'
		);
		
		$('#replayPage .page-header').empty().append('<h1>Replay ' + element.attr('name') + ' || ' + dataForReplay[0].date + '</h1>');
		$('#replayPage .page-content').empty().append
		(
				'<input type="hidden" id="replayWrapperId" value="' + element.attr('name') + '"/>'
			+	'<div id="buttons" data-role="fieldcontain">'
			+		'<button data-inline="true" id="play" onclick="startReplay();">play</button>'
			+		'<button data-inline="true" id="pause" onclick="pauseReplay();">pause</button>'
			+		'<button data-inline="true" id="show_svg" onclick="displaySVG();">show svg</button>'
			+		'<select id="replaySpeed">'
			+			'<option value="999">Kies replay snelheid</option>'
			+			'<option value="500">1/2 seconde</option>'
			+			'<option value="1000">1 seconde</option>'
			+			'<option value="2000">2 seconden</option>'
			+			'<option value="3000">3 seconden</option>'
			+			'<option value="4000">4 seconden</option>'
			+		'</select>'
			+	'</div>'
			+	'<div id="svg_frame"></div>'
			+	'<div id="svg_code"></div>'
			+	'<div id="replay_data">'
			+		'<div id="replayForm" style="font-weight: bold;">'
			+			'Log #: <span id="val_log_num" style="font-weight: normal;"></span>'
			+			'Time: <span id="val_time" style="font-weight: normal;"></span><br/>'
			+			'Left wheel: <span id="val_left_wheel" style="font-weight: normal;"></span><br/>'
			+			'Right wheel: <span id="val_right_wheel" style="font-weight: normal;"></span><br/>'
			+			'Inclination: <span id="val_inclination" style="font-weight: normal;"></span><br/>'
			+		'</div>'
			+	'</div>'
		);
		
		$('#replayPage').page();
		$('#replayPage .page-header').page();
		
		
		
		$('#replayPage .page-header .ui-btn-left').attr('href', 'site_index.php#pageMain');
		$('#replayPage .page-header .ui-btn-left').attr('onclick', 'resetSelects(' + element.attr('grid_id') + '); $.mobile.changePage($(\'#pageMain\'));');
		
		$('.ui-dialog .ui-title').each(function()
		{
			if($(this).is(':visible'))
			{
				if($(this).html() == $('#replaySpeed option[value=\'999\']').text())
					$(this).parent().parent().dialog('destroy');
			}
		});
		
		$.mobile.changePage($('#replayPage'));
		
		$.mobile.pageLoading(true);
		
		prepareSVG();
		
		num = 0;
	});
}

function prepareSVG()
{
	if(typeof($('#svg_frame').svg('get')) != 'undefined')
		$('#svg_frame').svg('destroy');
	
	var parentWidth = $('#svg_frame').parent().parent().width() - 50;
	var parentHeight = Math.round(parentWidth / 16 * 5);
	
	$('#svg_frame').width(parentWidth).height(parentHeight);
	
	$('#svg_frame').svg();
	
	var svg = $('#svg_frame').svg('get');
	
	var defs = svg.defs();
	var rectFill = svg.rect(0, 0, 0, 0, {fill: 'white', width: '100%', height: '100%'});
	
	var all = svg.group({id: idAll, transform: 'translate(0 0) scale(1)'});
	
	path = svg.createPath();
	svg.path(all, path, {id: idPath, stroke: '#D90000', strokeWidth: 2});
	
	$('#svg_frame').initSVG();
}

function startReplay()
{
	if (num < dataForReplay.length)
	{
		if (isPaused)
			isPaused = false;
		
		loopReplay(dataForReplay);
	}
}

function loopReplay(data, i)
{
	if(!isDefined(i))
	{
		if (num > 0)
		{
			num--;
			i = num;
		}
		else
			i = 0;
	}
	
	totalNum = data.length;
	
	if (!isPaused && i < totalNum)
	{
		var log = data[i];
		var left = parseInt(log.left_wheel);
		var right = parseInt(log.right_wheel);
		var perc = Math.round(100*(num/(totalNum-1)));
		
		$('#replayForm').fillDiv(log);
		$('#val_log_num').html('<div id="percentageBox"><div id="percentageBar"></div><div id="percentageText">' + (num + 1) + ' / ' + totalNum + '</div></div>');
		$('#percentageBar').width(perc + '%');
		$('#svg_frame').move(left, right);
		
		setTimeout(function()
		{
			loopReplay(data, i + 1);
		}, $('#replaySpeed').val());
	}
	
	num = i + 1;
}

function pauseReplay()
{
	if (!isPaused)
		isPaused = true;
}

function displaySVG()
{
	if ($('#svg_frame').html() != '' && $('#svg_frame').html() != ' ')
	{
		var svg = $('#svg_frame').svg('get').toSVG();
		
		$('#svg_code').html(svg.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;'));
	}
}