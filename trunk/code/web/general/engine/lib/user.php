<?php
require_once("model.php");
/**
 * Class for user registration.
 * @author Rinse Cramer & Tymen Steur
 * @date 14-06-2011
 * @version 0.5
 */
class User extends Model
{
	/**
	 * Database table name of users.
	 */
	private $userTable = "users";
	/**
	 * Database column name of name.
	 */
	private $userNameClmn = "name";
	/**
	 * Database column name of email address.
	 */
	private $userEmailClmn = "email";
	/**
	 * Database column name of password.
	 */
	private $userPasswordClmn = "password";
	/**
	 * Database column name of authority level.
	 */
	private $userLevelClmn = "level";
	/**
	 * Database column name of date/time.
	 */
	private $userDateTimeClmn = "date_time";
	
	/**
	 * Constructor.
	 * @param int $id ID.
	 */
	public function __construct($id = null)
	{
		parent::__construct($id, "id", "users");
	}
	
	/**
	 * Create new user.
	 * @param string $name		Name.
	 * @param string $email		Email address.
	 * @param string $password	Password.
	 * @param string $level		Authority level.
	 * @return					True, if successful. False, otherwise.
	 */
	public function createUser($name, $email, $password, $level)
	{
		$dateTime = time();
		
		$existingUser = self::$db->getData("SELECT * FROM ".$this->dbTable." WHERE $this->userEmailClmn = '$email';");
		
		if(empty($existingUser))
		{
			$password = md5($password);
			
			// Insert the user into the database
			self::$db->executeNonQuery("INSERT INTO ".$this->dbTable." ($this->userNameClmn, $this->userEmailClmn, $this->userPasswordClmn, $this->userLevelClmn, $this->userDateTimeClmn) VALUES ('$name', '$email', '$password', $level, $dateTime);");
			
			// Store the new user data in the objects data variable
			$dbData = self::$db->getData("SELECT * FROM $this->dbTable WHERE $this->userEmailClmn = '$email'");
			
			if (!empty($dbData))
			{
				$this->data = $dbData[0];
			}
			
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Edit existing user.
	 * @param string $name		Name.
	 * @param string $email		Email address.
	 * @param string $password	Password.
	 * @param string $level		Authority level.
	 * @return					True, if successful. False, otherwise.
	 */
	public function editUser($name, $email, $password, $level)
	{
		$dateTime = time();
		
		$existingUser = self::$db->getData("SELECT * FROM ".$this->dbTable." WHERE $this->userEmailClmn = '$email';");
		
		if ((!empty($existingUser) && $this->data["email"] == $email) || empty($existingUser))
		{
			$id = $this->data["id"];			
			$passwordQuery = "";
			
			if(!empty($password))
				$passwordQuery = ", $this->userPasswordClmn = '".md5($password)."'";
			
			// Update the user in the database
			self::$db->executeNonQuery("UPDATE ".$this->dbTable." SET $this->userNameClmn = '$name', $this->userEmailClmn = '$email', $this->userLevelClmn = $level, $this->userDateTimeClmn = $dateTime $passwordQuery WHERE id = $id;");
			
			// Store the new user data in the objects data variable
			$dbData = self::$db->getData("SELECT * FROM ".$this->dbTable." WHERE $this->userEmailClmn = '$email'");
					
			if (!empty($dbData))
				$this->data = $dbData[0];
		
			return true;
		}
		else
		{
			echo "A user with this email address already exists.";
			return false;
		}
	}
	
	/**
	 * Remove existing user.
	 * @return True, if successful. False, otherwise.
	 */
	public function removeUser()
	{
		if(!empty($this->data))
		{
			$userExists = self::$db->getData("SELECT * FROM ".$this->dbTable." WHERE id = ".$this->data["id"].";");
			
			if (!empty($userExists))
			{
				// Remove the user from the database
				self::$db->executeNonQuery("DELETE FROM ".$this->dbTable." WHERE id = ".$this->data["id"].";");
			
				return true;
			}
			else
				return false;
		}
		else
			return false;
	}
	
	/**
	 * Reset password.
	 * @param string $userEmail	User email address.
	 * @todo Send mail using php mailer.
	 */
	public function passwordReset($userEmail)
	{
		// Generate a new random password
		$newPassword = $this->createPassword(6);
		
		// Encrypt the new password
		$newPassword = md5($newPassword);
		
		// Update the database with the new password
		self::$db->executeNonQuery("UPDATE ".$this->dbTable." SET $this->userPasswordClmn = '$newPassword' WHERE $this->userEmailClmn ='$userEmail'");
	}
	
	
	/**
	 * Create a random alphanumerical password.
	 * @param int $passwordLength Length of new password.
	 * @return New password.
	 */
	public function createPassword($passwordLength)
	{
		$chars = "abcdefghijkmnopqrstuvwxyz023456789";
		srand((double)microtime() * 1000000);
		$password = "";
		
		for($i = 0; i < $passwordLength + 1; $i++)
		{
			$num = rand() % 33;
			$tmp = substr($chars, $num, 1);
			$password = $password . $tmp;
		}
		
		return $password;
	}
	
	/**
	 * Load sorted data from query.
	 * @param string $qtype		Query type.
	 * @param string $query		Query.
	 * @param string $sortname	Column to be sorted.
	 * @param string $sortorder	Sorting order.
	 * @param int $start		First row.
	 * @param int $limit		Number of rows.
	 * @param User $user		User.
	 * @return					Users array.
	 */
	public static function loadSorted($qtype, $query, $sortname, $sortorder, $start, $limit, User $user)
	{
		$sqlQuery = "";
		
		if($query != "" && $qtype != "")
			$sqlQuery = "WHERE $qtype LIKE '%$query%'";

		if($user->data["level"] > 0)
		{
			if(empty($sqlQuery))
				$sqlQuery = "WHERE id = " . $user->data["id"];
			else
				$sqlQuery .= " AND id = " . $user->data["id"];
		}
		
		$rows = self::$db->getData("SELECT * FROM users $sqlQuery ORDER BY $sortname $sortorder LIMIT $start, $limit");
		
		$users = array();
		
		foreach ($rows as $row)
		{
			$users[] = new User($row["id"]);
		}
		
		return $users;
	}
	
	/**
	 * Get number of data rows.
	 * @param string $qtype		Query type.
	 * @param string $query		Query.
	 * @param string $sortname	Column name to be sorted.
	 * @param string $sortorder	Sorting order.
	 * @param User $user		User.
	 * @return					Number of users.
	 */
	public static function total($qtype, $query, $sortname, $sortorder, User $user)
	{
		$sqlQuery = "";
		
		if($query != "" && $qtype != "")
			$sqlQuery = "WHERE $qtype LIKE '%$query%'";
			
		if($user->data["level"] > 0)
		{
			if(empty($sqlQuery))
				$sqlQuery = "WHERE id = " . $user->data["id"];
			else
				$sqlQuery .= " AND id = " . $user->data["id"];
		}
		
		$rows = self::$db->getData("SELECT * FROM users $sqlQuery");
		
		return count($rows);
	}
}

?>