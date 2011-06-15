<?php
require_once("../../init.php");
init();
handleAuth(true);
require_once("../../engine/lib/user.php");
/**
 * Service for users.
 * @author Tymen Steur
 * @date 14-06-2011
 * @version 0.5
 */
if(isset($_REQUEST["action"]))
{
	switch($_REQUEST["action"])
	{
		case "getGridData":
		{
			function getRows($users)
			{
				$rows = array();
				
				foreach ($users as $user)
				{
					$userLevel = "";
					
					switch($user->data["level"])
					{
						case 0:
							$userLevel = "administrator";
							break;
						case 2:
							$userLevel = "moderator";
							break;
						case 4:
							$userLevel = "user";
							break;
					}
					
					$time = $user->data["date_time"];
                        
    				if (strlen($time) > 10)
      					$time = round($time / 1000);
                        
          			$time = date("d-m-Y",$time)." ".date("G:i",$time);
					
					$rows[] = array
					(
						"id" => $user->data["id"],
						"cell" => array
						(
							$user->data["name"],
							$user->data["email"],
							$time,
							$userLevel,
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
			$user = $sessionHandler->getCurrentUser();
			
			$json = array
			(
				"page" => $page,
				"total" => User::total($qtype, $query, $sortname, $sortorder, $user),
				"rows" => getRows(User::loadSorted($qtype, $query, $sortname, $sortorder, $start, $rp, $user))
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