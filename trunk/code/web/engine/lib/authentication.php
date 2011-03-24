<?php

require_once("model.php");

/*
 * Name: Rinse Cramer & Tymen Steur
 * Date: 23-03-2011
 * Version: 0.12
 * 
 * Class for user authentication
 */
class Authentication extends Model
{
	private $maxLifetime = 1800;
	private $userEmailClmn= "email";
	private $userPasswordClmn = "password";
	private $userLevelClmn = "level";
	
	public function __construct($id = null)
	{
		parent::__construct($id, "users");
	}
	
	public function login($email, $password)
	{
		// Encrypt the password
		$password = md5($password);
		
		// Execute query through secureQuery function to prevent sql injections
		$result = self::$db->getDataSecured("SELECT * FROM ".$this->dbTable." WHERE $this->userEmailClmn = :email AND $this->userPasswordClmn = :password ;", array(":email" => $email, ":password" => $password));
		
		if(!empty($result))
		{
			if(!empty($result[$this->userEmailClmn]) && !empty($result[$this->userPasswordClmn]))
			{
				// Register sessions
				// You can add additional session information here if needed
				$_SESSION['loggedIn'] = $result[$this->userPasswordClmn];
				$_SESSION['userLevel'] = $result[$this->userLevelClmn];
				$_SESSION['lastActive'] = time();
				return true;
			}
			else
			{
				$this->logout();
				return false;
			}
		}
		else
			return false;
	}
	
	public function logout()
	{
		$_SESSION = array();
		session_destroy();
		return;
	}
	
	// Check if the user is logged in
	public function checkLogin()
	{
		if (isset($_SESSION['loggedIn']))
		{
			$password = $_SESSION['loggedIn'];
			$result = self::$db->getDataSecured("SELECT * FROM ".$this->dbTable." WHERE $this->userPasswordClmn = :password ;", array(":password" => $password));
			
			if(count($result) > 0 && ($_SESSION['lastActive'] + $this->maxLifetime > time()))
			{
				$_SESSION['lastActive'] = time();
				return $result;
			}
			else
				return false;
			}
		else
			return false;
	}
}

?>