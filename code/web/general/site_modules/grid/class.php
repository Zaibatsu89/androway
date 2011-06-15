<?php
require_once("lib/log.php");
require_once("lib/model.php");
/**
 * Class for loading grid data.
 * @author Tymen Steur & Rinse Cramer
 * @date 14-06-2011
 * @version 0.5
 */
class Grid extends Model
{
	/**
	 * Constructor.
	 * @param int $id	ID.
	 */
	public function __construct($id = null)
	{
		parent::__construct($id, "grid_id", "module_grid", false);
	}
	
	/**
	 * Load grid data.
	 * @param mixed $data Data from logs table.
	 * @return Data array. 
	 */
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
					
						if ($objectName != "user")
							$dbRow[$objectName] = $object->data;
						else
							$dbRow[$objectName] = $object->data["name"];
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
			
			$time = date("d-m-Y",$time)." ".date("G:i",$time);
			
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
	
	/**
	 * Load grid child data.
	 * @param int $rowId Row ID.
	 * @return Data array. 
	 */
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