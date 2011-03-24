<?php

require_once("model.php");

/*
 * Name: Rinse Cramer
 * Date: 23-03-2011
 * Version: 0.12
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
	
		echo "wtf1<hr>";
	
		$dateTime = time();
		
		$existingUser = self::$db->getData("SELECT * FROM ".$this->dbTable." WHERE $this->userEmailClmn = '$email';");
		
		echo "wtf2<hr>";
		
		if(empty($existingUser))
		{
			$password = md5($password);
			
			echo "wtf3<hr>";
			
			// Insert the user into the database
			self::$db->executeNonQuery("INSERT INTO ".$this->dbTable." ($this->userNameClmn, $this->userEmailClmn, $this->userPasswordClmn, $this->userLevelClmn, $this->userDateTimeClmn) VALUES ('$name', '$email', '$password', $level, $dateTime);");
			
			echo "wtf4<hr>";
			
			// Store the new user data in the objects data variable
			$dbData = self::$db->getData("SELECT * FROM $this->dbTable WHERE $this->userEmailClmn = '$email'");

			echo "wtf5<hr>";
			
			if (!empty($dbData))
			{
				var_dump($dbData[0]);
				echo "<hr>";
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
		if (!empty($existingUser))
			$existingEmail = ($existingUser[0]["email"]);
		else
		{
			echo "A user with this email address already exists.";
			return;
		}
		
		if ($existingEmail = $email)
		{
			$id = $existingUser[0]['id'];
			$password = md5($password);
			
			// Update the user in the database
			self::$db->executeNonQuery("UPDATE ".$this->dbTable." SET $this->userNameClmn = '$name', $this->userEmailClmn = '$email', $this->userPasswordClmn = '$password', $this->userLevelClmn = $level WHERE id = $id;");
			
			// Store the new user data in the objects data variable
			$dbData = self::$db->getData("SELECT * FROM $dbTable WHERE $this->userEmailClmn = '$email'");			
			if (!empty($dbData))
				$this->data = $dbData[0];
		
			return true;
		}
		else
			return false;
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
}

?>