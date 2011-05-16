var website_title = '';

$(function()
{
	loadSiteMenu();
	loadPage();
});

function loadSiteMenu()
{
	$.getJSON('webservices/siteService.php', { action: 'loadMenu' }, function(data)
	{
		website_title = data.website_title;
		document.title = website_title;
		
		generateMenu(data.menu_items);
	});
}

function generateMenu(menuData, level)
{
	if(!isDefined(level))
		level = 0;	
	
	var alphaString = 'abcdefghijklmnopqrstuvwxyz';
	
	$.each(menuData, function(i, data)
	{
		if(data.is_visible == '1')
		{
			var append_to = '';
			if(data.parent_id == '-1')
				append_to = '#site_menu ul';
			else
				append_to = '#site_submenu ul';
			
			var append_string = '';
			append_string	+=	'<li class="ui-block-'+ alphaString.charAt(i) +'">'
							+		'<a href="#" data-theme="a" onClick="loadPage(' + data.id + ');">'
							+		data.name
							+		'</a>'
							+	'</li>';
			
			$(append_to).append(append_string);
			
			generateMenu(data.children, level + 1);
		}
	});
	
	/*if(!isDefined(level))
		level = 0;
	
	$.each(menuData, function(i, data)
	{
		if(data.is_visible == '1')
		{
			var append_to = '';
			if(data.parent_id == '-1')
				append_to = '#site_menu';
			else
				append_to = '#page' + data.parent_id;
			
			var spacing = '';
			for(var j = 0; j < level; j++)
				spacing += '&nbsp;&nbsp;&nbsp;&nbsp;';
			
			var append_string = '';
			if(data.children.length > 0)
			{
				append_string = 	'<div id="page'+ data.id +'">'
								+		'<div class="menu_item" onclick="loadPage('+ data.id +')">'
								+			spacing + data.name
								+		'</div>'
								+	'</div>';
			}
			else
			{
				append_string = 	'<div id="page'+ data.id +'" class="menu_item" onclick="loadPage('+ data.id +')">'
								+		spacing + data.name
								+	'</div>';
			}
			
			$(append_to).append(append_string);
			
			generateMenu(data.children, level + 1);
		}
	});*/
}

function loadPage(pageId)
{
	if (!isDefined(pageId))
		pageId = false;
	
	$.getJSON('webservices/siteService.php', { action : 'loadPage', page_id : pageId }, function(pageData)
	{	
		document.title = website_title + pageData.page_title;
		$('#content').empty();
		processPageModules(pageData.modules);
	});
}

function processPageModules(modules)
{
	$.each(modules, function(i, module)
	{	
		var moduleFunction = 'load' + ucFirst(module.tag) + '(' + stringify(module.data) + ');';
		
		// Execute the generated function
		eval(moduleFunction);
	});
}

function loadModuleData(moduleName, moduleData, callback)
{
	if (!isDefined(callback))
		callback = function(){};
	
	$.getJSON('webservices/siteService.php', { action: 'loadModule', module_name : moduleName, id : moduleData.id }, function(data)
	{
		callback(data);
	});
}