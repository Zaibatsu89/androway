<?php
require_once("../init.php");
init(false);
handleAuth(false);
/**
 * Service for logging. Returns json because the Android Androway application requires it.
 * @author Rinse Cramer
 * @date 14-06-2011
 * @version 0.5
 */
if(isset($_REQUEST["query1"]))
	$_REQUEST["query"] = $_REQUEST["query1"];

if(isset($_REQUEST["query2"]))
	$_REQUEST["query"] .= $_REQUEST["query2"];

$db = $dbManager;
		
if($_REQUEST["dbName"] == $dbAlternativeManager->dbName)
	$db = $dbAlternativeManager;
	
switch($_REQUEST["function"])
{
	case "executeNonQuery":
	{
		$sql = stripslashes(urldecode($_REQUEST["query"]));		
		$db->executeNonQuery($sql);		
		break;
	}
	case "getData":
	{
		$sql = stripslashes(urldecode($_REQUEST["query"]));
		$json = $db->getData($sql);
		echo json_encode($json);
		break;
	}
}

?>