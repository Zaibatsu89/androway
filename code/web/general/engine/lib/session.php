<?php
require_once("model.php");
/**
 * Class for session registration.
 * @author Rinse Cramer
 * @date 14-06-2011
 * @version 0.5
 */
class Session extends Model
{
	/**
	 * Database table name of sessions.
	 */
	private $sessionTable = "sessions";
	/**
	 * Database column name of name.
	 */
	private $sessionNameClmn = "name";
	/**
	 * Database column name of date/time.
	 */
	private $sessionDateTimeClmn = "date_time";
	/**
	 * Database column name of user ID.
	 */
	private $sessionUserClmn = "user_id";
	
	/**
	 * Constructor.
	 * @param int $id ID.
	 */
	public function __construct($id = null)
	{
		parent::__construct($id, "session_id", "sessions", true);
	}
	
	/**
	 * Create new session.
	 * @param int $userId	User ID.
	 * @return				True, if successful. False, otherwise.
	 */
	public function createSession($userId)
	{
		$dateTime = time();
		
		// Insert the new session into the database
		self::$dbAlternative->executeNonQuery("INSERT INTO $this->dbTable ($this->sessionDateTimeClmn, $this->sessionUserClmn) VALUES ($dateTime, $userId);");
		
		// Get the new session data and store it in the objects data variable
		$dbData = self::$dbAlternative->getData("SELECT * FROM $this->dbTable WHERE $this->sessionUserClmn = $userId ORDER BY $this->sessionDateTimeClmn DESC LIMIT 1;");
		
		if(!empty($dbData))
		{
			$this->data = $dbData[0];
			
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Edit existing session.
	 * @param int $id	Session ID.
	 * @return			True, if successful. False, otherwise.
	 */
	public function editSession($id)
	{		
		$existingSession = self::$dbAlternative->getData("SELECT * FROM ".$this->dbTable." WHERE $this->sessionNameClmn = '$name';");
		
		if ((!empty($existingSession) && $this->data["name"] == $name) || empty($existingSession))
		{
			$id = $this->data["id"];
			
			// Update the session in the database
			self::$dbAlternative->executeNonQuery("UPDATE ".$this->dbTable." SET $this->sessionNameClmn = '$name', WHERE id = $id;");
			
			// Store the new session data in the objects data variable
			$dbData = self::$dbAlternative->getData("SELECT * FROM ".$this->dbTable." WHERE $this->sessionNameClmn = '$name'");
					
			if (!empty($dbData))
				$this->data = $dbData[0];
		
			return true;
		}
		else
		{
			echo "A session with this name already exists.";
			return false;
		}
	}
	
	/**
	 * Remove existing session.
	 * @return True, if successful. False, otherwise.
	 */
	public function removeSession()
	{	
		if(!empty($this->data))
		{		
			$sessionExists = self::$dbAlternative->getData("SELECT * FROM ".$this->dbTable." WHERE session_id = ".$this->data["session_id"].";");
			
			if (!empty($sessionExists))
			{
				// Remove the session from the database
				self::$dbAlternative->executeNonQuery("DELETE FROM ".$this->dbTable." WHERE session_id = ".$this->data["session_id"].";");
			
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
	 * @return					Sessions array.
	 */
	public static function loadSorted($qtype, $query, $sortname, $sortorder, $start, $limit, User $user)
	{
		$sqlQuery = "";
		
		if($query != "" && $qtype != "")
			$sqlQuery = "WHERE $qtype LIKE '%$query%'";
			
		if($user->data["level"] > 0)
		{
			if(empty($sqlQuery))
				$sqlQuery = "WHERE user_id = " . $user->data["id"];
			else
				$sqlQuery .= " AND user_id = " . $user->data["id"];
		}
		
		$rows = self::$dbAlternative->getData("SELECT * FROM sessions $sqlQuery ORDER BY $sortname $sortorder LIMIT $start, $limit");		
		
		$sessions = array();
		
		foreach ($rows as $row)
		{
			$sessions[] = new Session($row["session_id"]);
		}
		
		return $sessions;
	}
	
	/**
	 * Get number of data rows.
	 * @param string $qtype		Query type.
	 * @param string $query		Query.
	 * @param string $sortname	Column name to be sorted.
	 * @param string $sortorder	Sorting order.
	 * @param User $user		User.
	 * @return					Number of sessions.
	 */
	public static function total($qtype, $query, $sortname, $sortorder, User $user)
	{
		$sqlQuery = "";
		
		if($query != "" && $qtype != "")
			$sqlQuery = "WHERE $qtype LIKE '%$query%'";
			
		if($user->data["level"] > 0)
		{
			if(empty($sqlQuery))
				$sqlQuery = "WHERE user_id = " . $user->data["id"];
			else
				$sqlQuery .= " AND user_id = " . $user->data["id"];
		}
		
		$rows = self::$dbAlternative->getData("SELECT * FROM sessions $sqlQuery");
		
		return count($rows);
	}
}

?>