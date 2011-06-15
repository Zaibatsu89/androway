<?php
require_once("model.php");
/**
 * Class for log registration.
 * @author Rinse Cramer
 * @date 14-06-2011
 * @version 0.5
 */
class Log extends Model
{
	/**
	 * Database table name of logs.
	 */
	private $logTable = "logs";
	/**
	 * Database column name of session ID.
	 */
	private $logSessionIdClmn = "session_id";
	/**
	 * Database column name of date/time.
	 */
	private $logTimeClmn = "time";
	/**
	 * Database column name of left wheel.
	 */
	private $logLeftWheelClmn = "left_wheel";
	/**
	 * Database column name of right wheel.
	 */
	private $logRightWheelClmn = "right_wheel";
	/**
	 * Database column name of inclination.
	 */
	private $logInclinationClmn = "inclination";
	
	/**
	 * Constructor.
	 * @param int $id	ID.
	 */
	public function __construct($id = null)
	{
		parent::__construct($id, "log_id", "logs", true);
	}
	
	/**
	 * Edit existing log.
	 * @param int $leftWheel	Left wheel.
	 * @param int $rightWheel	Right wheel.
	 * @param int $inclinatoin	Inclination.
	 * @return					True, if successful. False, otherwise.
	 */
	public function editLog($leftWheel, $rightWheel, $inclination)
	{	
		$id = $this->data["log_id"];
		
		// Update the log in the database
		self::$dbAlternative->executeNonQuery("UPDATE ".$this->dbTable." SET $this->logLeftWheelClmn = $leftWheel, $this->logRightWheelClmn = $rightWheel, $this->logInclinationClmn = $inclination WHERE log_id = $id;");
		
		// Store the new log data in the objects data variable
		$dbData = self::$dbAlternative->getData("SELECT * FROM ".$this->dbTable." WHERE log_id = $id;");
				
		if (!empty($dbData))
			$this->data = $dbData[0];
	
		return true;
	}
	
	/**
	 * Remove existing log.
	 * @return True, if successful. False, otherwise.
	 */
	public function removeLog()
	{	
		if(!empty($this->data))
		{		
			$logExists = self::$dbAlternative->getData("SELECT * FROM ".$this->dbTable." WHERE log_id = ".$this->data["log_id"].";");
			
			if(!empty($logExists))
			{
				var_dump("DELETE FROM ".$this->dbTable." WHERE log_id = ".$this->data["log_id"].";");
				
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
	
	/**
	 * Load sorted data from query.
	 * @param string $qtype		Query type.
	 * @param string $query		Query.
	 * @param string $sortname	Column to be sorted.
	 * @param string $sortorder	Sorting order.
	 * @param int $start		First row.
	 * @param int $limit		Number of rows.
	 * @param User $user		User.
	 * @return					Logs array.
	 */
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
	
	/**
	 * Get number of data rows.
	 * @param string $qtype		Query type.
	 * @param string $query		Query.
	 * @param string $sortname	Column name to be sorted.
	 * @param string $sortorder	Sorting order.
	 * @param User $user		User.
	 * @return					Number of logs.
	 */
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