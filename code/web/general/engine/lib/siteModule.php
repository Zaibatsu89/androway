<?php
require_once("model.php");
/**
 * Class for site module registration
 * @author Rinse Cramer
 * @date 23-05-2011
 * @version 0.5
 */
class SiteModule extends Model
{
	/**
	 * Database table name of site module.
	 */
	private $siteModuleTable = "site_module";
	/**
	 * Database column name of title.
	 */
	private $siteModuleTitleClmn = "title";
	/**
	 * Database column name of tag.
	 */
	private $siteModuleTagClmn = "tag";
	
	/**
	 * Constructor.
	 * @param int $id ID.
	 */
	public function __construct($id = null)
	{
		parent::__construct($id, "id", "site_module", true);
	}
	
	/**
	 * Load sorted data from query.
	 * @param string $qtype		Query type.
	 * @param string $query		Query.
	 * @param string $sortname	Column to be sorted.
	 * @param string $sortorder	Sorting order.
	 * @param int $start		First row.
	 * @param int $limit		Number of rows.
	 * @return					Site module array.
	 */
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
	
	/**
	 * Get number of data rows.
	 * @param string $qtype		Query type.
	 * @param string $query		Query.
	 * @param string $sortname	Column name to be sorted.
	 * @param string $sortorder	Sorting order.
	 * @return					Number of site module rows.
	 */
	public static function total($qtype, $query, $sortname, $sortorder)
	{
		$sqlQuery = "";
		
		if($query != "" && $qtype != "")
			$sqlQuery = "WHERE $qtype LIKE '%$query%'";
		
		$rows = self::$db->getData("SELECT * FROM site_module $sqlQuery");
		
		return count($rows);
	}
}