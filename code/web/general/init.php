<?php

/*
 * Name: Rinse Cramer
 * Date: 06-04-2011
 * Version: 0.24
 * 
 * Initialization script.
 */
set_include_path($_SERVER['DOCUMENT_ROOT']."/engine/");
require_once("lib/authentication.php");
require_once("lib/databaseManager.php");
require_once("lib/model.php");
require_once("lib/redirect.php");
require_once("lib/session.php");
require_once("lib/sessionHandler.php");
require_once("lib/user.php");
require_once("lib/xml.php");

error_reporting(E_ALL);

setlocale(LC_CTYPE, array('nl_NL.utf8', 'dutch', 'nl_NL.utf8', 'nl_NL'));
setlocale(LC_TIME, array('nl_NL.utf8', 'dutch', 'nl_NL.utf8', 'nl_NL'));

$dbManager = null;
$loggedIn = false;

$configuration = xml2array(get_include_path()."config.xml");
$configuration = $configuration["config"];

$sessionHandler = new SessionHandler(intval($configuration["maxLifeTime"]));
$sessionHandler->start();

function init($redirect = true)
{
	global $sessionHandler, $dbManager, $loggedIn, $configuration;
	
	if($redirect)
		Redirect::handleRedirect($configuration["mobileRedirectUrl"], $configuration["generalRedirectUrl"]);
	
	$standardDbConfig = $configuration["standardDatabase"];
	$dbManager = new DatabaseManager($standardDbConfig["name"], $standardDbConfig["user"], $standardDbConfig["password"]);
	
	$alternativeDbConfig = $configuration["alternativeDatabase"];
	$dbAlternativeManager = new DatabaseManager($alternativeDbConfig["name"], $alternativeDbConfig["user"], $alternativeDbConfig["password"]);
	
	Model::init($dbManager, $dbAlternativeManager);
	
	$loggedIn = $sessionHandler->authenticate();
}

function handleAuth($redirect)
{
	global $loggedIn;
	
	if($redirect)
	{	
		if(!$loggedIn)
		{
			// User is not logged in, so redirect to root
			header("Location: /site_index.php");
			die();
		}
	}
}

?>