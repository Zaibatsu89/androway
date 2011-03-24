<?php

abstract class Model
{
	// The database manager to use
	protected static $db;
	
	protected $dbTable;
	protected $data;

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