<?php

/*
 * Name: Rinse Cramer
 * Date: 23-03-2011
 * Version: 0.12
 * 
 * General database managing class
 * Uses PDO
 */
class DatabaseManager
{
	private $databaseType = "mysql";
	private $hostName = "localhost";
	private $dbName = null;
	private $userName = null;
	private $password = null;
		
	private $pdoDb = null;
	
	public function __construct($dbName, $userName, $password)
	{
		$this->dbName = $dbName;
		$this->userName = $userName;
		$this->password = $password;
		
		$this->pdoDb = new PDO("$this->databaseType:host=$this->hostName;dbname=$this->dbName", $this->userName, $this->password);
	}
	
	public function executeNonQuery($query)
	{
		try
		{
			$this->pdoDb->exec($query);
		}
		catch(PDOException $e)
		{
			$this->pdoDb->rollback();
			echo $query . '<br />' . $e->getMessage();
		}
	}
	
	public function executeSecureNonQuery($query, $values)
	{
		try
		{
			$dbObject = $this->pdoDb->prepare($query, array(PDO::ATTR_CURSOR => PDO::CURSOR_FWDONLY));
			$dbObject->execute($values);
		}
		catch(PDOException $e)
		{
			$this->pdoDb->rollback();
			echo $query . '<br />' . $e->getMessage();
		}
    }
	
	/*
	 * Result is an array with key value pairs.
	 * $result as $key => $value
	 */
	public function getData($query, $dbName = null)
	{
		$result = array();
		
		try
		{
			$rows = $this->pdoDb->query($query);		
			
			if(!empty($rows))
			{
				foreach ($rows as $row)
				{
					if ($row !== false)
						$result[] = $row;
				}
			}
		}
		catch(PDOException $e)
		{
			echo $e->getMessage();
		}
	    
	   	return $result;
	}
	
	public function getDataSecured($query, $values)
	{
		$result = array();
		
		try
		{
			$dbObject = $this->pdoDb->prepare($query, array(PDO::ATTR_CURSOR => PDO::CURSOR_FWDONLY));
			$dbObject->execute($values);			
			$rows = $dbObject->fetchAll();
			
			foreach ($rows as $row)
			{
				if ($row !== false)
					$result[] = $row;
			}
		}
		catch(PDOException $e)
		{
			echo $e->getMessage();
		}
		
		return $result;
    }
}

?>