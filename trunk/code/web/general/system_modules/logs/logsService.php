<?php
require_once("../../init.php");
init();
handleAuth(true);
require_once("../../engine/lib/log.php");
/**
 * Service for logs.
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
			function getRows($logs)
			{				
				$rows = array();
				
				foreach ($logs as $log)
				{
					$time = $log->data["time"];
                        
    				if (strlen($time) > 10)
      					$time = round($time / 1000);
                        
          			$time = date("d-m-Y",$time)." ".date("G:i:s",$time);
					
					$rows[] = array
					(
						"id" => $log->data["log_id"],
						"cell" => array
						(
							$log->data["session_id"],
							$time,
							$log->data["left_wheel"],
							$log->data["right_wheel"],
							$log->data["inclination"],
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
				"total" => Log::total($qtype, $query, $sortname, $sortorder, $user),
				"rows" => getRows(Log::loadSorted($qtype, $query, $sortname, $sortorder, $start, $rp, $user))
			);
			
			echo json_encode($json);
			break;
		}
		case "removeRow":
		{
			if (isset($_REQUEST["id"]) && !empty($_REQUEST["id"]))
			{
				$log = new Log($_REQUEST["id"]);				
				$log->removeLog();
			}
		
			break;	
		}
		case "getLog":
		{
			$json = array();
			
			if(isset($_REQUEST["id"]))
			{
				$log = new Log($_REQUEST["id"]);				
				$json = $log->data;
			}
			
			echo json_encode($json);
			
			break;
		}
		case "editLog":
		{
			if(isset($_REQUEST["log_id"]))
			{
				$log = null;
				
				if($_REQUEST["log_id"] != "")
				{
					$log = new Log($_REQUEST["log_id"]);
					$log->editLog($_REQUEST["left_wheel"], $_REQUEST["right_wheel"], $_REQUEST["inclination"]);
				}
			}
			
			break;
		}
	}
}

?>