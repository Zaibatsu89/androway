$.fn.exists = function()
{
	return $(this).length > 0;
}

$.fn.fillForm = function(formData)
{
	var self = this;
	
	$.each(formData, function(item)
	{
		var element = self.find('#val_' + item);
		
		if (element.is('select'))
			element.find('option[@value=' + formData[item] + ']').attr('selected', 'selected');		
		else
			element.val(formData[item]);
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

function loadModuleDialog(url, dialogId, dialogWidth, dialogHeight, callback)
{
	if(!isDefined(callback))
		callback = function(){};
	
	if(!$('#' + dialogId).exists())
		$('body').append('<div id="' + dialogId + '"></div>');

	var dialogElement = $('#' + dialogId);

	dialogElement.load(url, function()
	{
		dialogElement.dialog
		({
			width: dialogWidth,
			height: dialogHeight
		});
		
		callback();
	});
}