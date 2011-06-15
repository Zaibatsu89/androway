<?php
require_once("../init.php");
init(false);
handleAuth(false);
/**
 * Service for website. Returns json because the Android Androway application requires it.
 * @author Rinse Cramer
 * @date 14-06-2011
 * @version 0.5
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
		$result = $instance->loadData($_REQUEST["module_data"]);
		
		echo json_encode($result);
		
		break;
	}
	case "loadModuleData":
	{
		require_once("../site_modules/" . $_REQUEST["module_name"] . "/class.php");
		
		// Should be CamelCase instead of ucfirst
		$moduleClass = ucfirst($_REQUEST["module_name"]);
		$functionName = $_REQUEST["function_name"];
		
		$instance = new $moduleClass($_REQUEST["module_id"]);
		$result = $instance->$functionName($_REQUEST["row_id"]);
		
		echo json_encode($result);
		
		break;
	}
}

?>