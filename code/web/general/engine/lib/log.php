<?php

require_once("model.php");

/*
 * Name: Rinse Cramer
 * Date: 31-03-2011
 * Version: 0.1
 * 
 * Class for log registration
 */
class Log extends Model
{
	private $logTable = "logs";
	private $logSessionIdClmn = "session_id";
	private $logTimeClmn = "time";
	private $logLeftWheelClmn = "left_wheel";
	private $logRightWheelClmn = "right_wheel";
	private $logInclinationClmn = "inclination";
	
	public function __construct($id = null)
	{
		parent::__construct($id, "log_id", "logs", true);
	}
	
	public function editLog($subject, $message, $leftWheel, $rightWheel, $inclination)
	{		
		$id = $this->data["log_id"];
		
		// Update the log in the database
		self::$dbAlternative->executeNonQuery("UPDATE ".$this->dbTable." SET $this->logTimeClmn = '". (time() * 1000) ."', $this->logLeftWheelClmn = $leftWheel, $this->logRightWheelClmn = $rightWheel, $this->logInclinationClmn = $inclination WHERE log_id = $id;");
		
		// Store the new log data in the objects data variable
		$dbData = self::$dbAlternative->getData("SELECT * FROM ".$this->dbTable." WHERE log_id = $id;");
				
		if (!empty($dbData))
			$this->data = $dbData[0];
	
		return true;
	}
	
	public function removeLog()
	{	
		if(!empty($this->data))
		{		
			$logExists = self::$dbAlternative->getData("SELECT * FROM ".$this->dbTable." WHERE log_id = ".$this->data["log_id"].";");
			
			if (!empty($logExists))
			{
				// Remove the log from the database
				self::$dbAlternative->executeNonQuery("DELETE FROM ".$this->dbTable." WHERE log_id = ".$this->data["log_id"].";");
			
				return true;
			}
			else
				return false;
		}
		else
			return false;
	}
	
	public static function loadSorted($qtype, $query, $sortname, $sortorder, $start, $limit, User $user)
	{
		$sqlQuery = "";
		$completeQuery = "";		
		
		if($user->data["level"] > 0)
		{
			if($query != "" && $qtype != "")
				$sqlQuery = "AND log.$qtype LIKE '%$query%'";
			
			$completeQuery = "SELECT log.* FROM logs AS log, sessions AS session WHERE log.session_id = session.session_id AND session.user_id = " . $user->data["id"] . " $sqlQuery ORDER BY $sortname $sortorder LIMIT $start, $limit";
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
			$logs[] = new Log($row["log_id"]);
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