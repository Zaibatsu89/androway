<?php
set_include_path($_SERVER['DOCUMENT_ROOT']."/engine/");
require_once("lib/authentication.php");
require_once("lib/databaseManager.php");
require_once("lib/model.php");
require_once("lib/redirect.php");
require_once("lib/session.php");
require_once("lib/sessionHandler.php");
require_once("lib/user.php");
require_once("lib/xml.php");
/**
 * Initialization script.
 * @author Rinse Cramer
 * @date 14-06-2011
 * @version 0.5
 */

/**
 * Standard database manager.
 */
$dbManager = null;
/**
 * Alternative database manager.
 */
$dbAlternativeManager = null;
/**
 * Is user logged in?
 */
$loggedIn = false;
/**
 * Configuration.
 */
$configuration = xml2array(get_include_path()."config.xml");
$configuration = $configuration["config"];
/**
 * Session handler.
 */
$sessionHandler = new SessionHandler(intval($configuration["maxLifeTime"]));
$sessionHandler->start();

/**
 * Initialization.
 * @param bool $redirect Is mobile redirecting enabled?
 */
function init($redirect = true)
{
	global $sessionHandler, $dbManager, $dbAlternativeManager, $loggedIn, $configuration;
	
	if($redirect)
		Redirect::handleRedirect($configuration["mobileRedirectUrl"], $configuration["generalRedirectUrl"]);
	
	$standardDbConfig = $configuration["standardDatabase"];
	$dbManager = new DatabaseManager($standardDbConfig["name"], $standardDbConfig["user"], $standardDbConfig["password"]);
	
	$alternativeDbConfig = $configuration["alternativeDatabase"];
	$dbAlternativeManager = new DatabaseManager($alternativeDbConfig["name"], $alternativeDbConfig["user"], $alternativeDbConfig["password"]);
	
	Model::init($configuration, $dbManager, $dbAlternativeManager);
	
	$loggedIn = $sessionHandler->authenticate();
}

/**
 * Handle authentication.
 * @param bool $redirect Is mobile redirecting enabled?
 */
function handleAuth($redirect)
{
	global $loggedIn;
	
	if($redirect)
	{
		if(!$loggedIn)
		{
			//User is not logged in, so redirect to root
			header("Location: /");
			die();
		}
	}
}

?>