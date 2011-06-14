var website_title = '';

$(function()
{
	loadSiteMenu();
	loadPage();
	
	if(fromApp)
		hideHeaders();
});

function hideHeaders()
{
	$('.ui-page').each(function()
	{
		$(this).find('.ui-header').hide();
	});
}

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
							+			data.name
							+		'</a>'
							+	'</li>';
			
			$(append_to).append(append_string);
			
			generateMenu(data.children, level + 1);
		}
	});
}

function loadPage(pageId)
{
	if (!isDefined(pageId))
		pageId = false;
	
	$.mobile.pageLoading(false);
	
	$.getJSON('webservices/siteService.php', { action : 'loadPage', page_id : pageId }, function(pageData)
	{
		document.title = website_title + pageData.page_title;
		$('#pageMain').find('div[data-role=header]').html('<h1 class="ui-title" tabindex="0" role="heading" aria-level="1">' + document.title + '</h1>');
		
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
	
	$.getJSON('webservices/siteService.php', { action: 'loadModule', module_name : moduleName, id : eval('moduleData.' + moduleName + '_id'), module_data : moduleData }, function(data)
	{
		callback(data);
	});
}