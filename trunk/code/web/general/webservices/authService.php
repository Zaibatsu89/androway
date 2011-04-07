<?php

require_once("../init.php");
init(false);

/*
 * Name: Tymen Steur
 * Date: 29-03-2011
 * Version: 0.1
 * 
 * Class to serve the authentication (returns json because the Android Androway application requires it)
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
		
		echo json_encode($json);
	}
	else if($_REQUEST["authType"] == "logout")
	{
		$sessionHandler->logout();
	}
}

?>