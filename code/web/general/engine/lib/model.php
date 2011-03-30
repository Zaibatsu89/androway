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
	
	protected $dbTable;
	public $data;

	protected function __construct($id = null, $dbTable)
	{
		$this->dbTable = $dbTable;
		
		if($id != null)
		{
			$dbData = self::$db->getData("SELECT * FROM $dbTable WHERE id = $id;");
			
			if (!empty($dbData))
				$this->data = $dbData[0];
		}
		else
			$this->data = array();
	}
	
	public function init(DatabaseManager $db)
	{
		Model::$db = $db;
	}
}

?>