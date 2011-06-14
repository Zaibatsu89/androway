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

$.fn.fillDiv = function(divData)
{
	var self = this;
	
	$.each(divData, function(item)
	{
		self.find('#val_' + item).html(divData[item]);
	});
}

function ucFirst (str)
{
    str += '';
    var f = str.charAt(0).toUpperCase();
    return f + str.substr(1);
}

function stringify (obj)
{
	var t = typeof(obj);
	
	if(t != "object" || obj === null)
	{
		// simple data type
		if(t == "string") obj = '"'+obj+'"';
		return String(obj);
	}
	else
	{
		// recurse array or object
		var n, v, json = [], arr = (obj && obj.constructor == Array);
		
		for (n in obj)
		{
			v = obj[n];
			t = typeof(v);
			
			if(t == "string")
				v = '"'+v+'"';
			else if(t == "object" && v !== null)
				v = JSON.stringify(v);
			
			json.push((arr ? "" : '"' + n + '":') + String(v));
		}
		
		return (arr ? "[" : "{") + String(json) + (arr ? "]" : "}");
	}
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

function loadModuleDialog(url, dialogId, title, dialogWidth, dialogHeight, callback, moduleId)
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
			height: dialogHeight,
			title: title
		});
		
		callback();
	});
}