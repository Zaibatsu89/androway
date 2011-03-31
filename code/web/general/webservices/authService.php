<?php

require_once("../init.php");
init("http://m.androway.nl/", "http://www.androway.nl/", "androway_framework", "androway", "hz7bkaxw");

/*
 * Name: Tymen Steur
 * Date: 29-03-2011
 * Version: 0.1
 * 
 * Class to serve the authentication
 */
if(isset($_REQUEST["authType"]))
{
	if($_REQUEST["authType"] == "login")
	{
		if($sessionHandler->login(urldecode($_REQUEST["email"]), $_REQUEST["password"]))
			echo "true";
	}
	else if($_REQUEST["authType"] == "logout")
	{
		$sessionHandler->logout();
	}
}

?>