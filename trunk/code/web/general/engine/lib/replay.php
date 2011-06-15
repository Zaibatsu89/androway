<?php
require_once("model.php");
/**
 * Class for replay registration.
 * @author Rinse Cramer
 * @date 14-06-2011
 * @version 0.5
 */
class Replay extends Model
{
	/**
	 * Database table name of replay.
	 */
	private $replayTable = "logs";
	
	/**
	 * Constructor.
	 * @param int $id ID.
	 */
	public function __construct($id = null)
	{
		parent::__construct($id, "id", "replay", true);
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