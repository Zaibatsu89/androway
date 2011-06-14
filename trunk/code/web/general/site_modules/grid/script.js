var userName = '';
var rowsPerPage = 6;

function loadGrid(data)
{
	var gridElement = $('#grid'+ data.grid_id);
	
	if(gridElement.exists())
		data.pageNumber = getActiveGridPage(data.grid_id);
	else
		data.pageNumber = 1;
		
	data.rowsPerPage = rowsPerPage;
	
	loadModuleData('grid', data, function(columnData)
	{
		data = columnData.grid_data;
		
		if(!gridElement.exists())
		{
			// The grid doesn't exist yet, so create the whole grid
			$('#pageMain').append
			(
				'<div data-role="content" id="grid'+ data.grid_id +'" class="grid">'
			+		'<ul data-role="listview" data-inset="' + !fromApp + '" data-divider-theme="a" class="grid-holder">'
			+			'<li data-role="list-divider" class="grid-header">' + data.title + '</li>'
			+			createGrid(data, columnData)
			+			'<li data-role="list-divider">'
			+				'<div id="gridPaging'+ data.grid_id +'" class="paging" style="text-align: center;">'
			+					createPaging(data.grid_id, columnData)
			+				'</div>'
			+			'</li>'		
			+		'</ul>'
			+	'</div>'
			).page();
		}
		else
		{
	 		// The grid allready exists, so only reload the content
			gridElement.find('.grid-holder .page-holder').empty();
			gridElement.find('.grid-holder .grid-header').after(createGrid(data, columnData));
			gridElement.find('.grid-holder .page-holder').page();
			$.mobile.pageLoading(true);
		}
		
		$('#grid'+ data.grid_id).page();
		
		var i = 0;
		$('.ui-dialog .ui-title').each(function()
		{
			if(columnData[i].user != null)
				userName = columnData[i].user;
				
			var dialogTitle = ucFirst(userName) + ' - ' + columnData[i].date_time;
			
			if($(this).is(':visible'))
			{
				if($(this).html() == dialogTitle)
				{
					$(this).parent().parent().dialog('destroy');
					
					i++;
				}
			}
		});
		
		$('#grid'+ data.grid_id +' select').each(function()
		{
			$(this).change(handleAction);
			
			var total = parseInt($(this).parent().parent().find('.ui-li-count').html());
			
			if (total < 1)
				$(this).selectmenu('disable');
		});
		
		$.mobile.pageLoading(true);
	});
}

function pageChangedEvent()
{
	if (pageChangedFromReplay)
		$('#replayPage').remove();
}

function handlePaging(gridId, pageNumber)
{
	$.mobile.pageLoading(false);
	var pagingElement = $('#grid'+ gridId).find('.paging');
	
	if(pagingElement.exists())
	{
		pagingElement.find('a.ui-btn').each(function()
		{
			$(this).removeClass('ui-btn-active').find('.ui-btn-inner').removeClass('ui-btn-up-b');
			
			var page = parseInt($(this).find('.ui-btn-text').html());
			
			if(page == pageNumber)
			{
				$(this).addClass('ui-btn-active').find('.ui-btn-inner').addClass('ui-btn-up-b');
				
				var data = {};
				data.grid_id = gridId;
				
				loadGrid(data);	
			}
		});
	}
}

function getActiveGridPage(gridId)
{
	var pagingElement = $('#grid'+ gridId).find('.paging');
	var result = -1;
	
	if(pagingElement.exists())
	{
		pagingElement.find('.ui-btn').each(function()
		{
			if($(this).hasClass('ui-btn-active'))
			{
				result = parseInt($(this).find('.ui-btn-text').html());
				return false;
			}
		});
	}
	
	return result;
}

function handleAction()
{
	$.mobile.pageLoading(false);
	
	try
	{
		// Try the eval, if it exists we want to execute it
		eval('load' + ucFirst($(this).val()) + '($(this))');
	}
	catch(err)
	{
		// If the eval failed, it means the function doesn't exist, so it is a child grid
		createChildGrid($(this));
	}
}

function createGrid(gridData, rows)
{
	var gridString = '<div class="page-holder">';
	
	$.each(rows, function(i, row)
	{
		if(i != 'grid_data')
		{
			if(row.user != null)
				userName = row.user;
			
			gridString +=	'<li data-role="fieldcontain" class="grid-row ui-li ui-li-static ui-body-c ui-field-contain ui-body ui-br">'
						+		'<input type="hidden" id="' + gridData.grid_id + 'ChildColumns" value="' + gridData.child_columns + '">'
						+		'<label class="select-session-view" for="selectSession' + i + '"><b>' + ucFirst(userName) + '</b><font style="font-size: 13px;"> - ' + row.date_time + '</font></label>'
						+		'<select name="' + eval('row.' + gridData.main_id) + '" id="selectSession' + i + '" grid_id="'+gridData.grid_id+'">'
						+ 			'<option value="choose">Kies actie</option>'
						+ 			'<option value="replay">Bekijk herhaling</option>'
						+			'<option value="logs">Bekijk logs</option>'
						+ 		'</select>'
						+		'<p class="ui-li-desc">'
						+ 			'<span class="ui-li-count ui-btn-up-c ui-btn-corner-all">' + row.total_children + '</span>'
						+ 		'</p>'
						+	'</li>';
		}
	});
	
	return gridString + '</div>';
}

function createPaging(gridId, rows)
{
	var result = '';
	
	var pages = Math.ceil(rows.grid_data.total_rows / rowsPerPage);
	
	for(var i = 1; i <= pages; i++)
	{
		result += '<a href="#" onclick="handlePaging('+ gridId +', ' + i + ');" data-inline="true" data-role="button" data-theme="c" class="ui-btn ui-btn-inline ui-btn-corner-all ui-shadow ui-btn-up-c"><span class="ui-btn-inner ui-btn-corner-all" style="cursor: pointer;"><span class="ui-btn-text">' + i + '</span></span></a>';
	}
	
	return result;
}

function createChildGrid(element)
{
	$.getJSON('webservices/siteService.php', { action: 'loadModuleData', module_name : 'grid', function_name: 'loadChildData', module_id : element.attr('grid_id'), row_id: element.attr('name')}, function(rows)
	{
		if(!$('#' + element.val() + 'Page').exists())
		{
			$('#pageMain').after
			(
					'<div data-role="page" id="' + element.val() + 'Page">'
				+		'<div data-role="header" class="page-header"></div>'
				+		'<div data-role="content" class="page-content"></div>'
				+	'</div>'
			);
		}
		
		// If the h1 doesn't exist, we create it.
		if(!$('#' + element.val() + 'Page .page-header').find('h1').exists())
			$('#' + element.val() + 'Page .page-header').append('<h1></h1>');
		
		// In any case the h1 exists. (Either existed before, or is created). Set the new title in the h1
		$('#' + element.val() + 'Page .page-header').find('h1').html(ucFirst(element.val()) + ' - ' + rows[0].session_id);
		
		$('#' + element.val() + 'Page .page-content').empty().append('<ol data-role="listview" data-inset="' + !fromApp + '" data-divider-theme="a"></ol>');
		
		$.each(rows, function(i, row)
		{	
			var elementString = '';
			
			elementString = 		'<li onclick="styleHeaders();">'
								+		row.time
								+		'<ul data-role="listview" data-inset="' + !fromApp + '">';
		
			var childColumns = ($('#' + element.attr('grid_id') + 'ChildColumns').val()).split(',');			
			$.each(childColumns, function(j, childColumn)
			{
				elementString +=			'<li>'
								+				'<b>' + childColumn + ' | </b>'
								+				eval('row.' + childColumn);
								+			'</li>'
			});
		
			elementString 		+=		'</ul>'
								+	'</li>'
			
			$('#' + element.val() + 'Page .page-content ol').append(elementString);
		});
		
		$('#' + element.val() + 'Page').page();
		$('#' + element.val() + 'Page .page-header').page();
		$('#' + element.val() + 'Page ol').page();
		
		$('#' + element.val() + 'Page .page-header .ui-btn-left').removeClass('ui-btn-active').attr('onclick', 'resetSelects(' + element.attr('grid_id') + '); $.mobile.changePage($(\'#pageMain\'));');
		
		$.mobile.changePage($('#' + element.val() + 'Page'));
		
		$.mobile.pageLoading(true);
	});
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

function resetSelects(gridId)
{
	$('#grid'+ gridId +' select').each(function()
	{
		// Reset state of clicked select option after the apropriate function was called1
		$(this).val('choose');	
		$(this).parent().find('.ui-btn-text').text($(this).find('option[value=\'choose\']').text());
	});
}