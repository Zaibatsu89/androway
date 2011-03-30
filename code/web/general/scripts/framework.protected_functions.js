function logout(formId)
{
	var logoutForm = $('#' + formId);
	
	logoutForm.find('.message_box').fadeTo(200, 0.1, function()
	{
		$(this).html('Logging out...').addClass('auth_valid').fadeTo(900, 1, function()
	  	{
			logoutForm.find('.message_box').removeClass('auth_valid').hide();
	  		//window.location = '/';
		});
	});
}

function startModule(moduleName, moduleType)
{
	switch(moduleType)
	{
		case 'dialog':
		{
			var moduleElement = moduleName + 'Module';
			
			loadModuleDialog('/modules/' + moduleName + '/index.php', moduleElement, 600, 375);

			moduleElement = $(moduleElement);
			
			break;
		}
	}
}