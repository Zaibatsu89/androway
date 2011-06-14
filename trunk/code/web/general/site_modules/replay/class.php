<?php

require_once("lib/model.php");

/*
 * Name: Rinse Cramer
 * Date: 17-05-2011
 * Version: 0.1
 * 
 * Class for replay registration
 */
class Replay extends Model
{
	public function __construct($id = null)
	{
		parent::__construct($id, "replay_id", "module_replay", false);
	}
	
	public function loadData($parentId)
	{
		$dbToUse = self::$db;
		
		if($this->data["database"] == "alternative")
			$dbToUse = self::$dbAlternative;

		$childRows = $dbToUse->getData("SELECT ".$this->data["child_columns"] ." FROM ".$this->data["child_table"]." WHERE ".$this->data["parent_id"] ." = ".$parentId." ORDER BY ".$this->data["child_id"]." ASC");
		
		$resultArray = array();
		
		foreach($childRows as $childRow)
		{
			$time = $childRow["time"];
			
			if (strlen($time) > 10)
				$time = round($time / 1000);
			
			$date = date("d-m-Y",$time);
			$time = date("G:i:s",$time);
			
			$childRow["date"] = $date;
			$childRow["time"] = $time;
			
			$resultArray[] = $childRow;
		}
		
		return $resultArray;
	}
}

?>