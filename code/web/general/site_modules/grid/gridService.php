<?php

require_once("model.php");

/*
 * Name: Rinse Cramer
 * Date: 08-04-2011
 * Version: 0.11
 * 
 * Class for page registration
 */
class Grid extends Model
{
	private $gridTable = "module_grid";
	
	public function __construct($id = null)
	{
		parent::__construct($id, "module_grid", false);
	}
	
	public function loadData()
	{
		$managerToUse = self::$db;
		
		if($this->database == "alternative")
			$managerToUse = self::$dbAlternative;
		
		$dbRows = $managerToUse->getData("SELECT $this->columns FROM $this->database_table");
		
		return $dbRows;
	}
}

?>