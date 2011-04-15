function initLogsModule(moduleId)
{
	var table = $('#logsTable');
	var qType = 'session_id';
	
	if(!isDefined(moduleId) && table.parent().find('.module_id').exists())
		moduleId = table.parent().find('.module_id').val();
	else
		moduleId = '';
	
	table.flexigrid
	({
		moduleName: 'logs',
		url: 'system_modules/logs/logsService.php',
		editRow: true,
		removeRow: true,
		qtype: qType,
		query: moduleId,
		colModel:
		[
			{display: 's. id', name : 'session_id', width : 40, sortable : true, align: 'left'},
			{display: 'date/time', name : 'time', width : 130, sortable : true, align: 'left'},
			{display: 'subject', name : 'subject', width : 130, sortable : true, align: 'left'},
			{display: 'message', name : 'message', width : 130, sortable : true, align: 'left'},
			{display: '', name : 'edit', width : 40, sortable : false, align: 'center'},
			{display: '', name : 'remove', width : 40, sortable : false, align: 'center'}
		],
		searchitems :
		[
			{display: 'subject', name : 'subject', isdefault: true},
			{display: 'message', name : 'message'}
		],
		sortname: 'time',
		sortorder: 'desc',
		onSuccess: function(){ },
		onEdit: function(scope)
		{			
			onEditLog($(scope).attr('id'));
		},
		onRemove: function(scope)
		{
			if(confirm('Are you sure you want to permanently delete this log?'))
			{
				var rowId = $(scope).attr('id');
				
				$.post('system_modules/logs/logsService.php',{action : 'removeRow', id : rowId}, function()
				{
					$('#logsTable').flexReload();
				});
			}
		}
	});
}

function onEditLog(logId)
{	
	var idName = 'editLog';
	
	// Show the edit log dialog
	loadModuleDialog('system_modules/logs/window.php', idName, 600, 200, function()
	{
		// Check if the log id is defined, if so load the existing log data into the form for editing
		if(isDefined(logId))
		{
			$.getJSON('system_modules/logs/logsService.php', {id: logId, action: 'getLog'}, function(data)
			{
				$('#editLogForm').fillForm(data);
			});
		}
		
		$('#' + idName).find('.save_edit_form').unbind();
		$('#' + idName).find('.save_edit_form').click(function()
		{
			// Een ajax post met de values array
			$.post('system_modules/logs/logsService.php', $('#editLogForm').serializeArray(), function(data)
			{
				$('#logsTable').flexReload();
			});
			
			$('#editLog').dialog('destroy');
		});
		
		$('#' + idName).find('.cancel_edit_form').unbind();
		$('#' + idName).find('.cancel_edit_form').click(function()
		{
			$('#editLog').dialog('destroy');
		});
	});
}