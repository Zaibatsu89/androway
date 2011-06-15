<?php
require_once("model.php");
/**
 * Class for page registration.
 * @author Rinse Cramer
 * @date 14-06-2011
 * @version 0.5
 */
class Page extends Model
{
	/**
	 * Database table name of pages.
	 */
	private $pageTable = "page";
	/**
	 * Database column name of title.
	 */
	private $pageTitleClmn = "title";
	/**
	 * Database column name of date/time.
	 */
	private $pageDateTimeClmn = "date_time";
	/**
	 * Public array of page children.
	 */
	public $children = array();
	
	/**
	 * Constructor.
	 * @param int $id ID.
	 */
	public function __construct($id = null)
	{
		parent::__construct($id, "id", "page", false);
		
		if($id !== null)
			$this->children = $this->getChildren();
	}
	
	/**
	 * Get page children array.
	 * @return Page children array.
	 */
	protected function getChildren()
	{
		$dbRows = self::$db->getData("SELECT * FROM page WHERE parent_id = " . $this->data["id"]);
		$children = array();
		
		foreach($dbRows as $row)
		{
			$children[] = new Page($row["id"]);
		}
		
		return $children;
	}
	
	/**
	 * Load sorted data from query.
	 * @param string $qtype		Query type.
	 * @param string $query		Query.
	 * @param string $sortname	Column to be sorted.
	 * @param string $sortorder	Sorting order.
	 * @param int $start		First row.
	 * @param int $limit		Number of rows.
	 * @return					Page array.
	 */
	public static function loadSorted($qtype, $query, $sortname, $sortorder, $start, $limit)
	{
		$sqlQuery = "";
		
		if($query != "" && $qtype != "")
			$sqlQuery = "WHERE $qtype LIKE '%$query%'";
		
		$rows = self::$db->getData("SELECT * FROM page $sqlQuery ORDER BY $sortname $sortorder LIMIT $start, $limit");
		
		$page = array();
		
		foreach ($rows as $row)
		{
			$page[] = new Page($row["id"]);
		}
		
		return $page;
	}
	
	/**
	 * Get number of data rows.
	 * @param string $qtype		Query type.
	 * @param string $query		Query.
	 * @param string $sortname	Column name to be sorted.
	 * @param string $sortorder	Sorting order.
	 * @return					Number of pages.
	 */
	public static function total($qtype, $query, $sortname, $sortorder)
	{
		$sqlQuery = "";
		
		if($query != "" && $qtype != "")
			$sqlQuery = "WHERE $qtype LIKE '%$query%'";
		
		$rows = self::$db->getData("SELECT * FROM page $sqlQuery");
		
		return count($rows);
	}
	
	/**
	 * Load page.
	 * @param int $id	Page ID.
	 * @return			Page data as JSON array.
	 */
	public static function load($id)
	{
		$resultArray = array();
		
		if($id == false || $id == "false")
			$id = self::$config["homePageId"];
			
		$page = self::$db->getData("SELECT * FROM page WHERE id = $id");
		$page = $page[0];
		
		if(!empty($page))
		{
			$resultArray["page_title"] = $page["title"];
			$resultArray["page_is_visible"] = $page["is_visible"];
			
			$pageModules = self::$db->getData("SELECT * FROM page_modules WHERE page_id = $id");
			$resultArray["modules"] = array();
			
			foreach($pageModules as $pageModule)
			{
				$module_id = $pageModule["module_id"];
				$tag = $pageModule["tag"];
				
				$module = self::$db->getData("SELECT * FROM module_$tag WHERE ".$tag."_id = $module_id");
				$module = $module[0];
				
				$moduleData = array();				
				foreach($module as $key => $value)
				{
					if(!is_numeric($key))
						$moduleData[$key] = $value;
				}
				
				$resultArray["modules"][] = array
				(
					"id" => $module_id,
					"tag" => $tag,
					"data" => $moduleData
				);
			}
		}
		
		return json_encode($resultArray);
	}
	
	/**
	 * Load menu.
	 * @return Menu pages.
	 */
	public static function loadMenuData()
	{
		$menuPages = array();
		
		$dbRows = self::$db->getData("SELECT * FROM page WHERE parent_id = -1");
		
		foreach($dbRows as $row)
		{	
			$menuPages[] = new Page($row["id"]);
		}
		
		return $menuPages;
	}
}