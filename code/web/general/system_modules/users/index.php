<?php
	require_once("../../init.php");
	init();
	handleAuth(true);
	require_once("../../engine/lib/user.php");
?>

<script type="text/javascript">
	$(function()
	{
		initUsersModule();
	});
</script>

<?
$user = $sessionHandler->getCurrentUser();
	
if($user->data["level"] == 0)
{
?>
<div onclick="onEditUser()" style="background: url('../../images/add.png') no-repeat 0px 2px; cursor: pointer; height: 25px; padding-left: 21px;">New user</div>
<?
}
?>
<table id="usersTable" style="display: none;"></table>