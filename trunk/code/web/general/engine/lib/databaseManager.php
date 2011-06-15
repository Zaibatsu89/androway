<?php
/**
 * General database managing class. Uses PDO.
 * @author Rinse Cramer
 * @date 14-06-2011
 * @version 0.5
 */
class DatabaseManager
{
	/**
	 * Database type.
	 */
	private $databaseType = "mysql";
	/**
	 * Host name.
	 */
	private $hostName = "localhost";
	/**
	 * Database name.
	 */
	public $dbName = null;
	/**
	 * User name.
	 */
	private $userName = null;
	/**
	 * Password.
	 */
	private $password = null;
	
	/**
	 * PDO database.
	 */	
	private $pdoDb = null;
	
	/**
	 * Constructor.
	 * @param string $dbName	Database name.
	 * @param string $userName	User name.
	 * @param string $password	Password.
	 */
	public function __construct($dbName, $userName, $password)
	{
		$this->dbName = $dbName;
		$this->userName = $userName;
		$this->password = $password;
		
		$this->pdoDb = new PDO("$this->databaseType:host=$this->hostName;dbname=$this->dbName", $this->userName, $this->password);
	}
	
	/**
	 * Execute non query.
	 * @param string $query	Query.
	 */
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
	
	/**
	 * Execute secure non query (protection against LulzSec :P).
	 * @param string $query		Query.
	 * @param string $values	Values.
	 */
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
	
	/**
	 * Get data as an array with key value pairs. $result as $key => $value.
	 * @param string $query	Query.
	 * @return				Data array.
	 */
	public function getData($query)
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
	
	/**
	 * Get data as an array with key value pairs secured (protection against LulzSec :P). $result as $key => $value.
	 * @param string $query		Query.
	 * @param string $values	Values.
	 * @return					Data array.
	 */
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