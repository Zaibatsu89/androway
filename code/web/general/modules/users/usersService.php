<?php

require_once("../../init.php");

init("http://m.androway.nl/", "http://www.androway.nl/", "androway_framework", "androway", "********");
handleAuth(true);

require_once("../../engine/lib/user.php");

/*
 * Name: Tymen Steur
 * Date: 29-03-2011
 * Version: 0.1
 * 
 * Class to serve the user
 */
if(isset($_REQUEST["action"]))
{
	switch($_REQUEST["action"])
	{
		case "getGridData":
		{
			function getRows($Users)
			{
				$rows = array();
				
				foreach ($Users as $User)
				{	
					$rows[] = array
					(
						"id" => $User->data["id"],
						"cell" => array
						(
							$User->data["name"],
							$User->data["email"],
							date("d-m-y",$User->data["date_time"])." ".date("G:i",$User->data["date_time"]),
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
				"total" => User::total($qtype, $query, $sortname, $sortorder),
				"rows" => getRows(User::loadSorted($qtype, $query, $sortname, $sortorder, $start, $rp))
			);
			
			echo json_encode($json);
			break;
		}
		case "removeRow":
		{
			if (isset($_REQUEST["id"]) && !empty($_REQUEST["id"]))
			{
				$user = new User($_REQUEST["id"]);
				
				$user->removeUser();
			}
		
			break;	
		}
		case "getUser":
		{
			$json = array();
			
			if(isset($_REQUEST["id"]))
			{
				$user = new User($_REQUEST["id"]);				
				$json = $user->data;
			}
			
			echo json_encode($json);
			
			break;
		}
		case "editUser":
		{
			if(isset($_REQUEST["id"]))
			{
				$user = null;
				
				if($_REQUEST["id"] == "")
				{
					$user = new User();
					$user->createUser($_REQUEST["name"], $_REQUEST["email"], $_REQUEST["password"], $_REQUEST["level"]);
				}
				else
				{
					$user = new User($_REQUEST["id"]);
					$user->editUser($_REQUEST["name"], $_REQUEST["email"], $_REQUEST["password"], $_REQUEST["level"]);
				}
			}
			
			break;
		}
	}
}

?>