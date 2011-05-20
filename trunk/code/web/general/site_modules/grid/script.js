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
		
		var name = '';
		
		for(var i = 0; i < columnData.length; i++)
			name += columnData[i].name;
		
		$('.ui-dialog .ui-title').each(function()
		{
			if($(this).html() == name)
				$(this).parent().parent().dialog('destroy');
		});
		
		$('#contentGrid select').each(function()
		{
			$(this).change(handleAction);
			
			var total = parseInt($(this).parent().parent().find('.ui-li-count').html());
			
			if (total < 1)
				$(this).selectmenu('disable');
		});
	});
}

function handleAction()
{
	$('.grid-module-component').hide();
	
	var element = $('#' + $(this).attr('name') + ucFirst($(this).val()));
	
	if(element.exists())
		element.show().page();
	else
	{
		var idString = '#' + $(this).val() + 'Wrapper';
		$(idString + ' ' + idString + 'Id').val($(this).attr('name'));
		$(idString).show().page();
	}
}

function createGrid(gridData, rows)
{
	var gridString = '';
	
	$.each(rows, function(i, row)
	{
		gridString +=	'<li data-role="fieldcontain">'
					+		'<label class="select" for="select-choice-a">' + row.name + '</label>'
					+		'<select name="' + eval('row.' + gridData.main_id) + '" id="select-choice-a">'
					+ 			'<option>Kies actie</option>'
					+ 			'<option value="replay">Bekijk herhaling</option>'
					+			'<option value="logs">Bekijk logs</option>'
					+ 		'</select>'
					+		'<p>'
					+ 			'<span class="ui-li-count">' + row.children.length + '</span>'
					+ 		'</p>'
					+	'</li>';
		
		$('#contentLogs').append
		(
				'<div id="' + eval('row.' + gridData.main_id) + 'Logs" class="grid-module-component" style="display: none;">'
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
		result +=	'<li id="' + eval('child.'+gridData.child_id) + 'Log" onclick="styleHeaders();">'
				+		child.subject
				+		'<ul data-role="listview" data-inset="true">';
		
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

function styleHeaders()
{
	$('.ui-page').each(function()
	{
		var headerElement = $(this).find('.ui-header');
		
		if(headerElement.hasClass('ui-bar-b'))
		{
			headerElement.removeClass('ui-bar-b').addClass('ui-bar-a');
			
			var buttonElement = headerElement.find('.ui-btn-icon-left');
			
			buttonElement.removeClass('ui-btn-up-b').addClass('ui-btn-up-a');
			buttonElement.attr('data-theme', 'a');
		}
			
		headerElement.attr('data-theme', 'a');
	});
	
	if(fromApp)
		hideHeaders();
}