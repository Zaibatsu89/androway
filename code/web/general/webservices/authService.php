<?php
require_once("../init.php");
init(false);
/**
 * Service for authentication. Returns json because the Android Androway application requires it.
 * @author Tymen Steur
 * @date 14-06-2011
 * @version 0.5
 */
if(isset($_REQUEST["authType"]))
{
	if($_REQUEST["authType"] == "login")
	{
		$json = array();
	
		if($sessionHandler->login(urldecode($_REQUEST["email"]), $_REQUEST["password"]))
			$json["success"] = "true";  
		else
			$json["success"] = "false";
		
		if(isset($_REQUEST["fromApp"]) && ($_REQUEST["fromApp"] == "true" || $_REQUEST["fromApp"] == true) && $sessionHandler->authenticate())
		{
			// Add some extra data to the json array (needed for the app)			
			$user = $sessionHandler->getCurrentUser();
			
			// The user logged in from the app, which means it will start a new session. Create a new session with the users id.
			$newSession = new Session();
			$newSession->createSession($user->data["id"]);
			
			$json["sessionId"] = $newSession->data["session_id"];
			$json["userId"] = $user->data["id"];
		}
		
		echo json_encode($json);
	}
	else if($_REQUEST["authType"] == "logout")
	{
		$sessionHandler->logout();
	}
}

?>