<?php

require_once("authentication.php");
require_once("user.php");

/*
 * Name: Tymen Steur
 * Date: 25-03-2011
 * Version: 0.1
 * 
 * Class for handling of login sessions
 */
class SessionHandler
{
	private $currentSession = null;
	private $currentUser = null;
	public $authenticationHandler = null; 
	
	public function __construct()
	{
		$this->authenticationHandler = new Authentication();
	}
	
	public function __destruct()
	{
		session_write_close();
	}

	public function start()
	{
		session_start();
	}
	
	public function stop()
	{
		$this->authenticationHandler->logout();
	}
	
	public function login($email, $password)
	{	
		return $this->authenticationHandler->login($email, $password);
	}
	
	public function logout()
	{
		$this->authenticationHandler->logout();
	}

	public function authenticate()
	{
		$result = $this->authenticationHandler->checkLogin();
		
		if($result == false)
			return false;
		else
		{
			// Store the user in the local $currentUser variable
			$this->currentUser = new User($result["id"]);
		
			return true;
		}
	}

	public function getCurrentUser()
	{
		return $this->currentUser;
	}
}
?>