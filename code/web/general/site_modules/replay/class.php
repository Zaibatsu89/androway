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

		return $dbToUse->getData("SELECT ".$this->data["child_columns"] ." FROM ".$this->data["child_table"]." WHERE ".$this->data["parent_id"] ." = ".$parentId." ORDER BY ".$this->data["child_id"]." ASC");
	}
}

?>