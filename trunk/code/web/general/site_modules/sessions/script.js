function initSessionsModule()
{	
	$('#sessionsTable').flexigrid
	({
		moduleName: 'sessions',
		url: 'system_modules/sessions/sessionsService.php',
		showLogsRow: true,
		editRow: true,
		removeRow: true,
		colModel:
		[
			{display: 'name', name : 'name', width : 140, sortable : true, align: 'left'},
			{display: 'date/time', name : 'date_time', width : 140, sortable : true, align: 'left'},
			{display: 'user id', name : 'user_id', width : 80, sortable : true, align: 'left'},
			{display: '', name : 'show_module', width : 40, sortable : false, align: 'center'},
			{display: '', name : 'edit', width : 40, sortable : false, align: 'center'},
			{display: '', name : 'remove', width : 40, sortable : false, align: 'center'}
		],
		searchitems :
		[
			{display: 'name', name : 'name'}
		],
		sortname: 'date_time',
		sortorder: 'desc',
		onSuccess: function(){ },	
		onShowLogs: function(scope)
		{
			onShowSessionLogs($(scope).attr('id'));
		},
		onEdit: function(scope)
		{
			onEditSession($(scope).attr('id'));
		},
		onRemove: function(scope)
		{
			if(confirm('Are you sure you want to permanently delete this session?'))
			{
				var rowId = $(scope).attr('id');
				
				$.post('system_modules/sessions/sessionsService.php',{action : 'removeRow', id : rowId}, function()
				{
					$('#sessionsTable').flexReload();
				});
			}
		}
	});
}

function onEditSession(sessionId)
{	
	var idName = 'editSession';
	
	// Show the edit session dialog
	loadModuleDialog('system_modules/sessions/window.php', idName, 600, 200, function()
	{
		// Check if the session id is defined, if so load the existing session data into the form for editing
		if(isDefined(sessionId))
		{
			$.getJSON('system_modules/sessions/sessionsService.php', {id: sessionId, action: 'getSession'}, function(data)
			{
				$('#editSessionForm').fillForm(data);
			});
		}
		
		$('#' + idName).find('.save_edit_form').unbind();
		$('#' + idName).find('.save_edit_form').click(function()
		{
			// Een ajax post met de values array
			$.post('system_modules/sessions/sessionsService.php', $('#editSessionForm').serializeArray(), function(data)
			{
				$('#sessionsTable').flexReload();
			});
			
			$('#editSession').dialog('destroy');
		});
		
		$('#' + idName).find('.cancel_edit_form').unbind();
		$('#' + idName).find('.cancel_edit_form').click(function()
		{
			$('#editSession').dialog('destroy');
		});
	});
}

function onShowSessionLogs(sessionId)
{
	startModule('logs', 'dialog', sessionId);
}