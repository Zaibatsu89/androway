<?php

require_once("model.php");

/*
 * Name: Rinse Cramer & Tymen Steur
 * Date: 29-03-2011
 * Version: 0.13
 * 
 * Class for user registration
 */
class User extends Model
{
	private $userTable = "users";
	private $userNameClmn= "name";
	private $userEmailClmn= "email";
	private $userPasswordClmn = "password";
	private $userLevelClmn = "level";
	private $userDateTimeClmn= "date_time";

	public function __construct($id = null)
	{
		parent::__construct($id, "users");
	}
	
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
			self::$db->executeNonQuery("UPDATE ".$this->dbTable." SET $this->userNameClmn = '$name', $this->userEmailClmn = '$email', $this->userLevelClmn = $level $passwordQuery WHERE id = $id;");
			
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
	
	public function removeUser()
	{
		var_dump($this->data);
		echo "<hr>";
	
		if(!empty($this->data))
		{
			echo "Query: SELECT * FROM ".$this->dbTable." WHERE id = ".$this->data["id"].";";
			echo "<hr>";
		
			$userExists = self::$db->getData("SELECT * FROM ".$this->dbTable." WHERE id = ".$this->data["id"].";");
			
			if (!empty($userExists))
			{
				echo "Query: DELETE FROM ".$this->dbTable." WHERE id = ".$this->data["id"].";";
				echo "<hr>";
				
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
	
	public function passwordReset($userEmail)
	{
		// Generate a new random password
		$newPassword = $this->createPassword(6);
		
		// Encrypt the new password
		$newPassword = md5($newPassword);
		
		// Update the database with the new password
		self::$db->executeNonQuery("UPDATE ".$this->dbTable." SET $this->userPasswordClmn = '$newPassword' WHERE $this->userEmailClmn ='$userEmail'");
		
		// SEND MAIL USING PHP MAILER
	}
	
	// Create a random alphanumerical password with the given length
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
	
	public static function loadSorted($qtype, $query, $sortname, $sortorder, $start, $limit)
	{
		$sqlQuery = "";
		
		if($query != "" && $qtype != "")
			$sqlQuery = "WHERE $qtype LIKE '%$query%'";
		
		$rows = self::$db->getData("SELECT * FROM users $sqlQuery ORDER BY $sortname $sortorder LIMIT $start, $limit");
		
		$users = array();
		
		foreach ($rows as $row)
		{
			$users[] = new User($row["id"]);
		}
		
		return $users;
	}
		
	public static function total($qtype, $query, $sortname, $sortorder)
	{
		$sqlQuery = "";
		
		if($query != "" && $qtype != "")
			$sqlQuery = "WHERE $qtype LIKE '%$query%'";
		
		$rows = self::$db->getData("SELECT * FROM users $sqlQuery");
		
		return count($rows);
	}
}

?>