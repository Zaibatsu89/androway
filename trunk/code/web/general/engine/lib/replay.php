<?php

require_once("model.php");

/*
 * Name: Rinse Cramer
 * Date: 12-05-2011
 * Version: 0.1
 * 
 * Class for replay registration
 */
class Replay extends Model
{
	private $replayTable = "logs";
	
	public function __construct($id = null)
	{
		parent::__construct($id, "id", "replay", true);
	}
	
	public static function loadSorted($qtype, $query, $sortname, $sortorder, $start, $limit, User $user)
	{
		$sqlQuery = "";
		$completeQuery = "";		
		
		if($user->data["level"] > 0)
		{
			if($query != "" && $qtype != "")
				$sqlQuery = "AND log.$qtype LIKE '%$query%'";
			
			$completeQuery = "SELECT log.* FROM logs AS log, sessions AS session WHERE log.session_id = session.id AND session.user_id = " . $user->data["id"] . " $sqlQuery ORDER BY $sortname $sortorder LIMIT $start, $limit";
		}
		else
		{
			if($query != "" && $qtype != "")
				$sqlQuery = "WHERE $qtype LIKE '%$query%'";
				
			$completeQuery = "SELECT * FROM logs $sqlQuery ORDER BY $sortname $sortorder LIMIT $start, $limit";
		}
			
		$rows = self::$dbAlternative->getData($completeQuery);
		
		$logs = array();
		
		foreach ($rows as $row)
		{
			$logs[] = new Log($row["id"]);
		}
		
		return $logs;
	}
		
	public static function total($qtype, $query, $sortname, $sortorder, User $user)
	{
		$sqlQuery = "";
		$completeQuery = "";		
		
		if($user->data["level"] > 0)
		{
			if($query != "" && $qtype != "")
				$sqlQuery = "AND log.$qtype LIKE '%$query%'";
			
			$completeQuery = "SELECT log.* FROM logs AS log, sessions AS session WHERE log.session_id = session.id AND session.user_id = " . $user->data["id"] . " $sqlQuery";
		}
		else
		{
			if($query != "" && $qtype != "")
				$sqlQuery = "WHERE $qtype LIKE '%$query%'";
				
			$completeQuery = "SELECT * FROM logs $sqlQuery";
		}
			
		$rows = self::$dbAlternative->getData($completeQuery);
		
		return count($rows);
	}
}

?>