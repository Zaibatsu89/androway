function loadGrid(data)
{	
	loadModuleData('grid', data, function(columnData)
	{
		$('#contentGrid').append
		(
			'<ul data-role="listview" data-inset="true" data-divider-theme="a">'
		+		'<li data-role="list-divider">Sessions'		
		+			createGrid(data, columnData)
		+		'</li>'
		+	'</ul>'
		).page();		
		
		$('.ui-dialog .ui-title').each(function()
		{
			var name = columnData[0].name + columnData[1].name;
			
			if($(this).html() == name)
				$(this).parent().parent().dialog('destroy');
		});
		
		$('#contentGrid select').each(function()
		{
			$(this).change(handleAction);
		});
	});
}

function handleAction()
{
	$('.logsList').hide();
	$('#' + $(this).attr('name') + ucFirst($(this).val())).show().page();
}

function createGrid(gridData, rows)
{
	var gridString = '';
	
	$.each(rows, function(i, row)
	{
		gridString +=	'<li data-role="fieldcontain">'
					+		'<label class="select" for="select-choice-a">' + row.name + '</label>'
					+		'<select name="' + row.id + '" id="select-choice-a">'
					+ 			'<option>Kies actie</option>'
					+ 			'<option value="replay">Bekijk herhaling</option>'
					+			'<option value="logs">Bekijk logs</option>'
					+ 		'</select>'
					+		'<p>'
					+ 			'<span class="ui-li-count">' + row.children.length + '</span>'
					+ 		'</p>'
					+	'</li>';
		
		$('#page').append
		(
				'<div id="' + row.id + 'Logs" class="logsList ui-content" style="display: none;">'
			+		'<ul data-role="listview" data-inset="true" data-divider-theme="a">'
			+			'<li data-role="list-divider">Logs ' + row.date_time		
			+				getRowChildren(gridData, row.children)
			+			'</li>'
			+		'</ul>'
			+	'</div>'
		);
	});
	
	return gridString;
}

function getRowChildren(gridData, children)
{
	var result = '';
	
	$.each(children, function(i, child)
	{				
		result +=	'<li>'
				+		child.subject
				+		'<ul data-inset="true" data-divider-theme="a">';
		
		var childColumns = (gridData.child_columns).split(',');
		$.each(childColumns, function(j, childColumn)
		{
			result +=	'<li>'
					+		'<b>' + childColumn + ' | </b>'
					+		eval('child.' + childColumn);
					+	'</li>'
		});
		
		result +=	'</ul></li>';
	});
	
	return result;	
}