<?php
require_once("commonFunctions.php");
/**
 * Abstract class for database manager model.
 * @author Tymen Steur
 * @date 14-06-2011
 * @version 0.5
 */
abstract class Model
{
	/**
	 * Database manager to use: standard.
	 */
	protected static $db;
	/**
	 * Database manager to use: alternative.
	 */
	protected static $dbAlternative;
	/**
	 * Database manager configuration.
	 */
	protected static $config;
	/**
	 * Use alternative database manager?
	 */
	protected $useAlternative;
	/**
	 * Database table to use.
	 */
	protected $dbTable;
	/**
	 * Public variable to store data. 
	 */
	public $data;
	
	/**
	 * Constructor.
	 * @param int $id				ID.
	 * @param string $idColumn		ID column.
	 * @param string $dbTable		Database table.
	 * @param bool $useAlternative	Use alternative database manager?
	 */
	protected function __construct($id = null, $idColumn, $dbTable, $useAlternative = false)
	{
		$this->dbTable = $dbTable;
		$this->useAlternative = $useAlternative;
		
		if($id != null)
		{
			$managerToUse = self::$db;
			
			if($useAlternative)
				$managerToUse = self::$dbAlternative;
				
			$dbData = $managerToUse->getData("SELECT * FROM $dbTable WHERE $idColumn = $id;");
			
			if (!empty($dbData))
				$this->data = $dbData[0];
		}
		else
			$this->data = array();
	}
	
	/**
	 * Initialize database manager.
	 * @param mixed $config						Configuration.
	 * @param DatabaseManager $db				Standard database manager.
	 * @param DatabaseManager $alternativeDb	Alternative database manager.
	 */
	public function init($config, DatabaseManager $db, DatabaseManager $alternativeDb = null)
	{
		Model::$config = $config;
		Model::$db = $db;
		
		if($alternativeDb != null)
			Model::$dbAlternative = $alternativeDb;
	}
}

?>