<?php

/*
 * Name: Tymen Steur
 * Date: 25-03-2011
 * Version: 0.1
 * 
 * Class for database manager model
 */
abstract class Model
{
	// The database manager to use
	protected static $db;
	protected static $dbAlternative;
	
	protected $useAlternative;
	protected $dbTable;
	public $data;

	protected function __construct($id = null, $dbTable, $useAlternative = false)
	{
		$this->dbTable = $dbTable;
		$this->useAlternative = $useAlternative;
		
		if($id != null)
		{
			$managerToUse = self::$db;
			
			if($useAlternative)
				$managerToUse = self::$dbAlternative;
				
			$dbData = $managerToUse->getData("SELECT * FROM $dbTable WHERE id = $id;");
			
			if (!empty($dbData))
				$this->data = $dbData[0];
		}
		else
			$this->data = array();
	}
	
	public function init(DatabaseManager $db, DatabaseManager $alternativeDb = null)
	{
		Model::$db = $db;
		
		if($alternativeDb != null)
			Model::$dbAlternative = $alternativeDb;
	}
}

?>