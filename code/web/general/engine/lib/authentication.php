<?php

/*
 * Name: Rinse Cramer & Tymen Steur
 * Date: 06-04-2011
 * Version: 0.15
 *
 * Class for user authentication
 */
require_once("model.php");

class Authentication extends Model
{
private $maxLifeTime = null;
private $userEmailClmn= "email";
private $userPasswordClmn = "password";
private $userLevelClmn = "level";

public function __construct($id = null, $maxLifeTime)
{
	parent::__construct($id, "id", "users");
	
	$this->maxLifeTime = $maxLifeTime;
}

public function login($email, $password)
{
	// Encrypt the password
	$password = md5($password);
	
	// Execute query through secureQuery function to prevent sql injections
	$result = self::$db->getDataSecured("SELECT * FROM ".$this->dbTable." WHERE $this->userEmailClmn = :email AND $this->userPasswordClmn = :password ;", array(":email" => $email, ":password" => $password));
	
	if(!empty($result))
	{
		$result = $result[0];
		
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
	{
		$this->logout();
		return false;
	}
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
		
		if(!empty($result) && ($_SESSION['lastActive'] + $this->maxLifeTime > time()))
		{
			$_SESSION['lastActive'] = time();
			return $result[0];
		}
		else
			return false;
	}
	else
		return false;
	}
}

?>