function loadGrid(data)
{
	loadModuleData('grid', data, function(columnData)
	{
		$('#content').append
		(
				'<div data-role="content">'
			+		'<ul data-role="listview" data-inset="true" data-theme="c" data-divider-theme="a">'
			+			'<li data-role="list-divider">Sessions</li>'
			+			createGrid(data, columnData)
			+		'</ul>'
			+	'</div>'
		).page();
	});
}

function createGrid(gridData, rows)
{
	var gridString = '';
	
	$.each(rows, function(i, row)
	{
		gridString +=	'<li>'
					+		'<h3>'
					+ 			row.name + '<font style="font-weight: normal; font-size: 11px;">&nbsp;&nbsp;' + row.date_time + '</font>'
					+ 		'</h3>'
					+		'<p>'
					+ 			'<span class="ui-li-count">' + row.children.length + '</span>'
					+ 		'</p>'
					+		'<ul data-inset="true" data-theme="c" data-divider-theme="a">'
					+			'<li data-role="list-divider">Logs</li>'
					+			getRowChildren(gridData, row.children)
					+		'</ul>'
					+	'</li>';
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
				+		'<ul data-inset="true" data-theme="c" data-divider-theme="a">';
		
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