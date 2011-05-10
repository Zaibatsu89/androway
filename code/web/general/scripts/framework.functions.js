$.fn.exists = function()
{
	return $(this).length > 0;
}

$.fn.fillForm = function(formData)
{
	var self = this;
	
	$.each(formData, function(item)
	{
		self.find('#val_' + item).val(formData[item]);
	});
}

function isDefined(variable)
{
	result = false;
	
	if(typeof(variable) != 'undefined')
		result = true;
	
	return result;
}

function loadDialog(dialogId, dialogWidth, dialogHeight, callback)
{
	if(!isDefined(callback))
		callback = function(){};

	if($('#' + dialogId).exists())
	{
		$('#' + dialogId).dialog
		({
			width: dialogWidth,
			height: dialogHeight
		});
	}
	
	callback();
}

function loadModuleDialog(url, dialogId, dialogWidth, dialogHeight, callback, moduleId)
{
	if(!isDefined(moduleId))
		moduleId = '';
	else
		moduleId = (url.indexOf('?') > -1 ? '&' : '?') + 'module_id=' + moduleId;
	
	if(!isDefined(callback))
		callback = function(){};
	
	if(!$('#' + dialogId).exists())
		$('body').append('<div id="' + dialogId + '"></div>');

	var dialogElement = $('#' + dialogId);
	
	dialogElement.load(url + moduleId, function()
	{
		dialogElement.dialog
		({
			width: dialogWidth,
			height: dialogHeight
		});
		
		callback();
	});
}