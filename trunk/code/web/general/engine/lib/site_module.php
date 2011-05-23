<?php

require_once("model.php");

/*
 * Name: Rinse Cramer
 * Date: 07-04-2011
 * Version: 0.1
 * 
 * Class for site module registration
 */
class SiteModule extends Model
{
	private $siteModuleTable = "site_module";
	private $siteModuleTitleClmn = "title";
	private $siteModuleTagClmn = "tag";
	
	public function __construct($id = null)
	{
		parent::__construct($id, "id", "site_module", true);
	}
	
	public static function loadSorted($qtype, $query, $sortname, $sortorder, $start, $limit)
	{
		$sqlQuery = "";
		
		if($query != "" && $qtype != "")
			$sqlQuery = "WHERE $qtype LIKE '%$query%'";
		
		$rows = self::$db->getData("SELECT * FROM site_module $sqlQuery ORDER BY $sortname $sortorder LIMIT $start, $limit");
		
		$site_module = array();
		
		foreach ($rows as $row)
		{
			$site_module[] = new SiteModule($row["id"]);
		}
		
		return $site_module;
	}
		
	public static function total($qtype, $query, $sortname, $sortorder)
	{
		$sqlQuery = "";
		
		if($query != "" && $qtype != "")
			$sqlQuery = "WHERE $qtype LIKE '%$query%'";
		
		$rows = self::$db->getData("SELECT * FROM site_module $sqlQuery");
		
		return count($rows);
	}
}