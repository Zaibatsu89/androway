var dataForReplay = null;

function loadReplay(data)
{
	loadModuleData('replay', data, function(replayData)
	{
		dataForReplay = replayData;
		
		$('#contentReplay').html
		(
				'<div id="replayWrapper" class="grid-module-component ui-content" style="display: none;">'
			+		'<input type="hidden" id="replayWrapperId"/>'
			+		'<div id="buttons">'
			+			'<button id="play" onclick="startReplay();">play</button>'
			+			'<button id="pause">pause</button>'
			+			'<button id="show_svg" onclick="displaySVG();">show svg</button>'
			+			'<label class="select" for="replaySpeed" style="display: none;">Kies replay snelheid</label>'
			+			'<select id="replaySpeed">'
			+				'<option value="1000">Kies replay snelheid</option>'
			+				'<option value="500">1/2 seconde</option>'
			+				'<option value="1000">1 seconde</option>'
			+				'<option value="2000">2 secondes</option>'
			+				'<option value="3000">3 secondes</option>'
			+				'<option value="4000">4 secondes</option>'
			+			'</select>'
			+		'</div>'
			+		'<div id="svg_frame"></div>'
			+		'<div id="svg_code"></div>'
			+		'<div id="replay_data">'
			+			'<div id="replayForm" style="font-weight: bold;">'
			+				'Session id: <span id="val_session_id" style="font-weight: normal;"></span><br/>'
			+				'Time: <span id="val_time" style="font-weight: normal;"></span><br/>'
			+				'Subject: <span id="val_subject" style="font-weight: normal;"></span><br/>'
			+				'Message: <span id="val_message" style="font-weight: normal;"></span><br/>'
			+			'</div>'
			+		'</div>'
			+	'</div>'
		).page();
		
		$('.ui-dialog .ui-title').each(function()
		{			
			if($(this).html() == 'Kies replay snelheid')
				$(this).parent().parent().dialog('destroy');
		});
	});
}

function startReplay()
{
	$('#svg_frame').initSVG();

	var delay = $('#replaySpeed').val();
	var counter = 0;
	
	$.each(dataForReplay, function(i, log)
	{
		if (log.session_id == $('#replayWrapperId').val())
		{
			setTimeout(function()
			{
				$('#replayForm').fillDiv(log);
				move(counter * 10, counter * 10);
			}, counter * delay);
			
			counter ++;
		}
	});
}

function displaySVG()
{
	var svg = $('#svg_frame').svg('get').toSVG();
	
	$('#svg_code').html(svg.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;'));
}