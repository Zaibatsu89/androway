function initSessionsModule()
{	
	$('#sessionsTable').flexigrid
	({
		moduleName: 'sessions',
		url: 'modules/sessions/sessionsService.php',
		colModel:
		[
			{display: 'Name', name : 'name', width : 140, sortable : true, align: 'left'},
			{display: 'Date Time', name : 'date_time', width : 140, sortable : true, align: 'left'},
			{display: 'User ID', name : 'user_id', width : 140, sortable : true, align: 'left'},
			{display: '', name : 'edit', width : 40, sortable : false, align: 'center'},
			{display: '', name : 'remove', width : 40, sortable : false, align: 'center'}
		],
		searchitems :
		[
			{display: 'Name', name : 'name'}
		],
		sortname: "date_time",
		sortorder: "asc",
		width: '100%',
		onSuccess: function(){ },
		onEdit: function(scope)
		{
			onEditSession($(scope).attr('id'));
		},
		onRemove: function(scope)
		{
			if(confirm('Are you sure you want to perminantly delete this session?'))
			{
				var rowId = $(scope).attr('id');
				
				$.post('modules/sessions/sessionsService.php',{action : 'removeRow', id : rowId}, function()
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
	loadModuleDialog('modules/sessions/window.php', idName, 600, 200, function()
	{
		// Check if the session id is defined, if so load the existing session data into the form for editing
		if(isDefined(sessionId))
		{
			$.getJSON('modules/sessions/sessionsService.php', {id: sessionId, action: 'getSession'}, function(data)
			{
				$('#editSessionForm').fillForm(data);
			});
		}
		
		$('#' + idName).find('.save_edit_form').unbind();
		$('#' + idName).find('.save_edit_form').click(function()
		{
			// Een ajax post met de values array
			$.post('modules/sessions/sessionsService.php', $('#editSessionForm').serializeArray(), function(data)
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