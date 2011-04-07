<?php 

/*
 * Name: Rinse Cramer
 * Date: 07-04-2011
 * Version: 0.1
 * 
 * Initialization script.
 */
set_include_path($_SERVER['DOCUMENT_ROOT']."/engine/");
require_once("lib/redirect.php");
require_once("lib/xml.php");

$configuration = xml2array(get_include_path()."config.xml");
$configuration = $configuration["config"];

Redirect::handleRedirect($configuration["mobileRedirectUrl"], $configuration["generalRedirectUrl"]);

?>