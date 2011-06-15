<?php
require_once("authentication.php");
require_once("user.php");
/**
 * Class handles login sessions
 * @author Tymen Steur
 * @date 07-04-2011
 * @version 0.5
 */
class SessionHandler
{
	/**
	 * Current session.
	 */
	private $currentSession = null;
	/**
	 * Current user.
	 */
	private $currentUser = null;
	/**
	 * Authentication handler.
	 */
	public $authenticationHandler = null; 
	
	/**
	 * Constructor.
	 * @param int $maxLifeTime	Maximum age of user session.
	 */
	public function __construct($maxLifeTime)
	{
		$this->authenticationHandler = new Authentication(null, $maxLifeTime);
	}
	
	/**
	 * Destruct existing login session.
	 */
	public function __destruct()
	{
		session_write_close();
	}
	
	/**
	 * Start existing login session.
	 */
	public function start()
	{
		session_start();
	}
	
	/**
	 * Stop existing login session.
	 */
	public function stop()
	{
		$this->authenticationHandler->logout();
	}
	
	/**
	 * Log user in.
	 * @return True, if successful. False, otherwise.
	 */
	public function login($email, $password)
	{	
		return $this->authenticationHandler->login($email, $password);
	}
	
	/**
	 * Log user out.
	 */
	public function logout()
	{
		$this->authenticationHandler->logout();
	}

	/**
	 * Authenticate user.
	 * @return True, if successful. False, otherwise.
	 */
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
	
	/** Get current logged in user.
	 * @return Current user.
	 */
	public function getCurrentUser()
	{
		return $this->currentUser;
	}
}
?>