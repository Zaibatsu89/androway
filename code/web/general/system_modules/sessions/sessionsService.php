<?php
require_once("../../init.php");
init();
handleAuth(true);
require_once("../../engine/lib/session.php");
require_once("../../engine/lib/user.php");
/**
 * Service for sessions.
 * @author Rinse Cramer
 * @date 14-06-2011
 * @version 0.5
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
					$user = new User($session->data["user_id"]);
					
					$time = $session->data["date_time"];
                        
    				if (strlen($time) > 10)
      					$time = round($time / 1000);
                        
          			$time = date("d-m-Y",$time)." ".date("G:i",$time);
					
					$rows[] = array
					(
						"id" => $session->data["session_id"],
						"cell" => array
						(
							$session->data["session_id"],
							ucwords($user->data["name"]),
							$time,
							'showLogs',
							//'edit',
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
			
			$user = $sessionHandler->getCurrentUser();
			
			$json = array
			(
				"page" => $page,
				"total" => Session::total($qtype, $query, $sortname, $sortorder, $user),
				"rows" => getRows(Session::loadSorted($qtype, $query, $sortname, $sortorder, $start, $rp, $user))
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