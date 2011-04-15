<?php

require_once("../init.php");

init(false);
handleAuth(false);

/*
 * Name: Rinse Cramer
 * Date: 08-04-2011
 * Version: 0.11
 * 
 * Service to serve the site
 */
switch($_REQUEST["action"])
{
	case "loadMenu":
	{
		require_once("lib/page.php");
		
		function getMenuItems($pages)
		{
			$menu_items = array();
	
			foreach($pages as $page)
			{	
				$menu_items[] = array
				(
					"id" => $page->data["id"],
					"parent_id" => $page->data["parent_id"],
					"name" => $page->data["title"],
					"is_visible" => $page->data["is_visible"],
					"children" => getMenuItems($page->children)
				);
			}
			
			return $menu_items;
		}
		
		$json = array
		(
			"website_title" => $configuration["websiteTitle"],
			"menu_items" => getMenuItems(Page::loadMenuData())
		);
		
		echo json_encode($json);
		
		break;
	}
	case "loadPage":
	{
		require_once("lib/page.php");
		
		echo Page::load($_REQUEST["page_id"]);
		
		break;
	}
	case "loadModule":
	{
		require_once("../site_modules/" . $_REQUEST["module_name"] . "/class.php");
		
		// Should be CamelCase instead of ucfirst
		$moduleClass = ucfirst($_REQUEST["module_name"]);
		$instance = new $moduleClass($_REQUEST["id"]);		
		$result = $instance->loadData();
		
		echo json_encode($result);
		
		break;
	}
}

?>