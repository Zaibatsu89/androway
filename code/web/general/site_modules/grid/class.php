<?php

require_once("lib/model.php");

/*
 * Name: Tymen Steur & Rinse Cramer
 * Date: 12-04-2011
 * Version: 0.12
 * 
 * Class for page registration
 */
class Grid extends Model
{
	public function __construct($id = null)
	{
		parent::__construct($id, "grid_id", "module_grid", false);
	}
	
	public function loadData()
	{
		$dbToUse = self::$db;
		
		if($this->data["database"] == "alternative")
			$dbToUse = self::$dbAlternative;
		
		$dbRows = $dbToUse->getData("SELECT ".$this->data["main_columns"] ." FROM ".$this->data["main_table"]);
		$resultArray = array();
		
		foreach($dbRows as $dbRow)
		{
			$dbRow["date_time"] = date("d-m-Y",$dbRow["date_time"])." ".date("G:i",$dbRow["date_time"]);
			
			$childRows = $dbToUse->getData("SELECT ".$this->data["child_columns"] ." FROM ".$this->data["child_table"] ." WHERE ".$this->data["main_id"]."=".$dbRow[$this->data["main_id"]]);
			
			if(!empty($childRows))
				$dbRow["children"] = $childRows;
			else
				$dbRow["children"] = array();
			
			$resultArray[] = $dbRow;
		}
		
		return $resultArray;
	}
}

?>