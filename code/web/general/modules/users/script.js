function initUsersModule()
{	
	$('#usersTable').flexigrid
	({
		moduleName: 'users',
		url: 'modules/users/usersService.php',
		colModel:
		[
			{display: 'Name', name : 'name', width : 140, sortable : true, align: 'left'},
			{display: 'Email', name : 'email', width : 160, sortable : true, align: 'left'},
			{display: 'Date time', name : 'date_time', width : 140, sortable : true, align: 'left'},
			/*DEZE REGEL MOEST ER NOG BIJ :D*/{display: '', name : 'edit', width : 40, sortable : false, align: 'center'},
			{display: '', name : 'remove', width : 40, sortable : false, align: 'center'}
		],
		searchitems :
		[
			{display: 'Name', name : 'name', isdefault: true},
			{display: 'Email', name : 'email'}
		],
		sortname: "name",
		sortorder: "asc",
		width: '100%',
		onSuccess: function(){ },
		onEdit: function(scope)
		{
			onEditUser($(scope).attr('id'));
		},
		onRemove: function(scope)
		{
			if(confirm('Are you sure you want to perminantly delete this user?'))
			{
				var rowId = $(scope).attr('id');
				
				$.post('modules/users/usersService.php',{action : 'removeRow', id : rowId}, function()
				{
					$('#usersTable').flexReload();
				});
			}
		}
	});
}

function onEditUser(userId)
{	
	var idName = 'editUser';
	
	// Show the edit user dialog
	loadModuleDialog('modules/users/window.php', idName, 600, 200, function()
	{
		// Check if the user id is defined, if so load the existing user data into the form for editing
		if(isDefined(userId))
		{
			$.getJSON('modules/users/usersService.php', {id: userId, action: 'getUser'}, function(data)
			{
				$('#editUserForm').fillForm(data);
				$('#editUserForm').find('#val_password').val('');
				triggerPassword(false, 'hide');
			});
		}
		
		$('#' + idName).find('.save_edit_form').unbind();
		$('#' + idName).find('.save_edit_form').click(function()
		{
			// Een ajax post met de values array
			$.post('modules/users/usersService.php', $('#editUserForm').serializeArray(), function(data)
			{
				$('#usersTable').flexReload();
			});
			
			$('#editUser').dialog('destroy');
		});
		
		$('#' + idName).find('.cancel_edit_form').unbind();
		$('#' + idName).find('.cancel_edit_form').click(function()
		{
			$('#editUser').dialog('destroy');
		});
	});
}

function triggerPassword(effect, action)
{
	var element = $('#changePassword');
	
	if(element.is(':visible') || action == 'hide')
	{
		$('#val_change_password').removeAttr('checked');
		
		if(effect)
			element.fadeOut();
		else
			element.hide();
	}
	else if(!element.is(':visible') || action == 'show')
	{
		$('#val_change_password').attr('checked', 'true');
		
		if(effect)
			element.fadeIn();
		else
			element.show();
	}
}