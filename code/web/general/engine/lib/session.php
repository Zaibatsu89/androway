<?php

require_once("model.php");

/*
 * Name: Rinse Cramer
 * Date: 30-03-2011
 * Version: 0.12
 * 
 * Class for session registration
 */
class Session extends Model
{
	private $sessionTable = "sessions";
	private $sessionNameClmn = "name";
	private $sessionDateTimeClmn = "date_time";
	private $sessionUserClmn = "user_id";
	
	public function __construct($id = null)
	{
		parent::__construct($id, "session_id", "sessions", true);
	}
	
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
	
	public function editSession($name)
	{
		$dateTime = time();
		
		$existingSession = self::$dbAlternative->getData("SELECT * FROM ".$this->dbTable." WHERE $this->sessionNameClmn = '$name';");
		
		if ((!empty($existingSession) && $this->data["name"] == $name) || empty($existingSession))
		{
			$id = $this->data["id"];
			
			// Update the session in the database
			self::$dbAlternative->executeNonQuery("UPDATE ".$this->dbTable." SET $this->sessionNameClmn = '$name', $this->sessionDateTimeClmn = $dateTime WHERE id = $id;");
			
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
	
	public function removeSession()
	{	
		if(!empty($this->data))
		{		
			$sessionExists = self::$dbAlternative->getData("SELECT * FROM ".$this->dbTable." WHERE id = ".$this->data["id"].";");
			
			if (!empty($sessionExists))
			{
				// Remove the session from the database
				self::$dbAlternative->executeNonQuery("DELETE FROM ".$this->dbTable." WHERE id = ".$this->data["id"].";");
			
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