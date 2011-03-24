<?php 

/*
 * Name: Rinse Cramer
 * Date: 23-03-2011
 * Version: 0.21
 * 
 * Initialization script.
 */
require_once("engine/lib/authentication.php");
require_once("engine/lib/databaseManager.php");
require_once("engine/lib/model.php");
require_once("engine/lib/redirect.php");
require_once("engine/lib/sessionHandler.php");
require_once("engine/lib/user.php");

error_reporting(E_ALL);

setlocale(LC_CTYPE, array('nl_NL.utf8', 'dutch', 'nl_NL.utf8', 'nl_NL'));
setlocale(LC_TIME, array('nl_NL.utf8', 'dutch', 'nl_NL.utf8', 'nl_NL'));

$dbManager = null;
$sessionHandler = new SessionHandler();
$sessionHandler->start();

function init($mobileRedirectUrl, $generalRedirectUrl, $dbName, $dbUser, $dbPassword, $authenticate = true)
{
	global $sessionHandler, $dbManager;
	
	Redirect::handleRedirect($mobileRedirectUrl, $generalRedirectUrl);
	
	$dbManager = new DatabaseManager($dbName, $dbUser, $dbPassword);
	
	Model::init($dbManager);
	
	if($sessionHandler->authenticate($authenticate))
	{
		// Logged in
		$currentUser = $sessionHandler->getCurrentUser();
	}
//	else
//	{
//		// User is not logged in, so redirect to root
//		header("Location: /");
//		die();
//	}
}

?>