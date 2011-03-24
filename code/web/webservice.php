<?php

/*
 * Name: Rinse Cramer
 * Date: 21-03-2011
 * Version: 0.1
 * 
 * Webservice for use by Android application Androway.
 */
require_once("init.php");

init("http://m.androway.nl/", "http://www.androway.nl/", $_REQUEST["dbName"], "androway_logging", "hz7bkaxw", false);

if(isset($_REQUEST["query1"]))
	$_REQUEST["query"] = $_REQUEST["query1"];

if(isset($_REQUEST["query2"]))
	$_REQUEST["query"] .= $_REQUEST["query2"];

switch($_REQUEST["function"])
{
	case "executeNonQuery":
	{
		$sql = stripslashes(urldecode($_REQUEST["query"]));
		$dbManager->executeNonQuery($sql);
		break;
	}
	case "getData":
	{
		$sql = stripslashes(urldecode($_REQUEST["query"]));
		$json = $dbManager->getData($sql, $_REQUEST["dbName"]);
		echo json_encode($json);
		break;
	}
}

?>