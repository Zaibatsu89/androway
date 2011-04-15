<?php

require_once("../../init.php");

init();
handleAuth(true);

require_once("../../engine/lib/log.php");

/*
 * Name: Rinse Cramer
 * Date: 31-03-2011
 * Version: 0.1
 * 
 * Class to serve the log
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
					$rows[] = array
					(
						"id" => $log->data["id"],
						"cell" => array
						(
							$log->data["session_id"],
							$log->data["time"],
							$log->data["subject"],
							$log->data["message"],
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
			if(isset($_REQUEST["id"]))
			{
				$log = null;
				
				if($_REQUEST["id"] != "")
				{
					$log = new Log($_REQUEST["id"]);
					$log->editLog($_REQUEST["subject"], $_REQUEST["message"]);
				}
			}
			
			break;
		}
	}
}

?>