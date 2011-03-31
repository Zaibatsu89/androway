<?php

require_once("../../init.php");

$alternativeDb = new DatabaseManager("androway_logging", "androway_logging", "hz7bkaxw");
init("http://m.androway.nl/", "http://www.androway.nl/", "androway_framework", "androway", "hz7bkaxw", true, $alternativeDb);
handleAuth(true);

require_once("../../engine/lib/session.php");

/*
 * Name: Rinse Cramer
 * Date: 30-03-2011
 * Version: 0.11
 * 
 * Class to serve the session
 */
if(isset($_REQUEST["action"]))
{
	switch($_REQUEST["action"])
	{
		case "getGridData":
		{
			function getRows($sessions)
			{			
				$rows = array();
				
				foreach ($sessions as $session)
				{
					$rows[] = array
					(
						"id" => $session->data["id"],
						"cell" => array
						(
							$session->data["name"],
							date("d-m-y",$session->data["date_time"])." ".date("G:i",$session->data["date_time"]),
							$session->data["user_id"],
							'edit',
							'remove'
						)
					);
				}
				
				return $rows;
			}
				
			$page = $_POST['page'];
			$rp = $_POST['rp'];
			
			$qtype = (isset($_POST['qtype']))?($_POST['qtype']):("");
			$query = (isset($_POST['query']))?($_POST['query']):("");
			
			$sortname = $_POST['sortname'];
			$sortorder = $_POST['sortorder'];
			
			if (!$sortname) $sortname = 'name';
			if (!$sortorder) $sortorder = 'desc';
			
			if (!$page) $page = 1;
			if (!$rp) $rp = 10;
			
			$start = (($page - 1) * $rp);
			
			$json = array
			(
				"page" => $page,
				"total" => Session::total($qtype, $query, $sortname, $sortorder),
				"rows" => getRows(Session::loadSorted($qtype, $query, $sortname, $sortorder, $start, $rp))
			);
			
			echo json_encode($json);
			break;
		}
		case "removeRow":
		{
			if (isset($_REQUEST["id"]) && !empty($_REQUEST["id"]))
			{
				$session = new Session($_REQUEST["id"]);
				
				$session->removeSession();
			}
		
			break;	
		}
		case "getSession":
		{
			$json = array();
			
			if(isset($_REQUEST["id"]))
			{
				$session = new Session($_REQUEST["id"]);				
				$json = $session->data;
			}
			
			echo json_encode($json);
			
			break;
		}
		case "editSession":
		{
			if(isset($_REQUEST["id"]))
			{
				$session = null;
				
				if($_REQUEST["id"] != "")
				{
					$session = new Session($_REQUEST["id"]);
					$session->editSession($_REQUEST["name"]);
				}
			}
			
			break;
		}
	}
}

?>