<?php

require_once("lib/log.php");
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
	
	public function loadData($data)
	{
		$dbToUse = self::$db;
		
		if($this->data["database"] == "alternative")
			$dbToUse = self::$dbAlternative;
		
		$pageNumber = intval($data["pageNumber"]);
		$rowsPerPage = intval($data["rowsPerPage"]);
		$start = $pageNumber * $rowsPerPage - $rowsPerPage;
		
		// Load the main data, and the total children (done in subquery)
		$dbRows = $dbToUse->getData("SELECT " . $this->data["main_columns"] . ", (SELECT COUNT(*) FROM ".$this->data["child_table"] . " AS c WHERE p." . $this->data["main_id"]. "=  c." . $this->data["main_id"] . ") AS total_children FROM " . $this->data["main_table"] . " AS p LIMIT $start, $rowsPerPage");
		
		$resultArray = array();
		
		foreach($dbRows as $dbRow)
		{
			foreach($dbRow as $key => $value)
			{			
				if(!is_numeric($key) && strpos($key, "_id") > 0)
				{
					// Extract the actual object name from the key
					$objectName = substr($key, 0, strpos($key, "_id"));
					$objectNameCamel = toCamelCase($objectName, true);
					
					// Create an object based on the extracted name
					try
					{
						$object = new $objectNameCamel($value);
					
						$dbRow[$objectName] = $object->data;
					}
					catch (Exception $e)
					{
						// There was no object with the extracted name. So don't do anything.
					}
				}
			}
			
			$time = $dbRow["date_time"];
			
			if (strlen($time) > 10)
				$time = round($time / 1000);
			
			$time = date("d-m-Y",$time)." ".date("G:i:s",$time);
			
			$dbRow["date_time"] = $time;
			
			$resultArray[] = $dbRow;
		}
		
		$resultArray["grid_data"] = $this->data;
		
		$totalRows = $dbToUse->getData("SELECT COUNT(*) FROM " . $this->data["main_table"]);
		$totalRows = $totalRows[0];
		
		if(array_key_exists("COUNT(*)", $totalRows))
			$totalRows = $totalRows["COUNT(*)"];
		else
			$totalRows = 0;
		
		$resultArray["grid_data"]["total_rows"] = $totalRows;
		
		return $resultArray;
	}
	
	public function loadChildData($rowId)
	{
		$dbToUse = self::$db;
		
		if($this->data["database"] == "alternative")
			$dbToUse = self::$dbAlternative;
		
		// Load the child data
		$childRows = $dbToUse->getData("SELECT ".$this->data["child_columns"] ." FROM ".$this->data["child_table"] ." WHERE ".$this->data["main_id"]."=".$rowId." ORDER BY ".$this->data["child_id"]." ASC");
		
		$resultArray = array();
		
		foreach($childRows as $childRow)
		{
			foreach($childRow as $key => $value)
			{
				if(!is_numeric($key) && strpos($key, "_id") > 0)
				{
					// Extract the actual object name from the key
					$objectName = substr($key, 0, strpos($key, "_id"));
					$objectNameCamel = toCamelCase($objectName, true);
					
					// Create an object based on the extracted name
					try
					{
						$object = new $objectNameCamel($value);
					
						$childRow[$objectName] = $object->data;
					}
					catch (Exception $e)
					{
						// There was no object with the extracted name. So don't do anything.
					}
				}
			}
			
			$time = $childRow["time"];
			
			if (strlen($time) > 10)
				$time = round($time / 1000);
			
			$time = date("d-m-Y",$time)." ".date("G:i:s",$time);
			
			$childRow["time"] = $time;
			
			$resultArray[] = $childRow;
		}
		
		return $resultArray;
	}
}

?>