<?php
require_once("model.php");
/**
 * Class for user authentication.
 * @author Rinse Cramer & Tymen Steur
 * @date 23-05-2011
 * @version 0.5
 */
class Authentication extends Model
{
	/**
	 * Maximum age of user session.
	 */
	private $maxLifeTime = null;
	/**
	 * Database column name of user email address.
	 */
	private $userEmailClmn = "email";
	/**
	 * Database column name of user password.
	 */
	private $userPasswordClmn = "password";
	/**
	 * Database column name of user level.
	 */
	private $userLevelClmn = "level";
	
	/**
	 * Constructor.
	 * @param int $id			ID.
	 * @param int $maxLifeTime	Maximum age of user session.
	 */
	public function __construct($id = null, $maxLifeTime)
	{
		parent::__construct($id, "id", "users");
		
		$this->maxLifeTime = $maxLifeTime;
	}
	
	/**
	 * Create new user session.
	 * @param string $email		User email address.
	 * @param string $password	User password.
	 * @return					True, if given credentials are valid. False, otherwise.
	 */
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
	
	/**
	 * Empty and destroy existing user session.
	 */
	public function logout()
	{
		$_SESSION = array();
		session_destroy();
		return;
	}
	
	/**
	 * Check if user is logged in.
	 * @return True, if user is logged in. False, otherwise.
	 */
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