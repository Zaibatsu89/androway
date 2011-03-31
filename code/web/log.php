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
	private $sessionNameClmn= "name";
	private $sessionDateTimeClmn= "date_time";
	private $sessionUserClmn= "user_id";
	
	public function __construct($id = null)
	{
		parent::__construct($id, "sessions", true);
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
	
	public static function loadSorted($qtype, $query, $sortname, $sortorder, $start, $limit)
	{
		$sqlQuery = "";
		
		if($query != "" && $qtype != "")
			$sqlQuery = "WHERE $qtype LIKE '%$query%'";
		
		$rows = self::$dbAlternative->getData("SELECT * FROM sessions $sqlQuery ORDER BY $sortname $sortorder LIMIT $start, $limit");
		
		$sessions = array();
		
		foreach ($rows as $row)
		{
			$sessions[] = new Session($row["id"]);
		}
		
		return $sessions;
	}
		
	public static function total($qtype, $query, $sortname, $sortorder)
	{
		$sqlQuery = "";
		
		if($query != "" && $qtype != "")
			$sqlQuery = "WHERE $qtype LIKE '%$query%'";
		
		$rows = self::$dbAlternative->getData("SELECT * FROM sessions $sqlQuery");
		
		return count($rows);
	}
}

?>