<?php
require_once("../../init.php");
init();
handleAuth(true);
require_once("../../engine/lib/user.php");
?>

<form id="editUserForm" class="uniForm">
	<fieldset class="inlineLabels">
		<input type="hidden" name="action" value="editUser" />
		<input type="hidden" name="id" id="val_id" />
		
		<div class="ctrlHolder">
			<label for="val_name">Name</label>
			<input type="text" id="val_name" name="name">
		</div>		
		<div class="ctrlHolder">
			<label for="val_email">Email</label>
			<input type="text" id="val_email" name="email">
		</div>		
		<div class="ctrlHolder">
			<label for="val_change_password">Change password</label>
			<input type="checkbox" name="change_password" id="val_change_password" checked="true" onChange="triggerPassword(true);" />
		</div>		
		<div id="changePassword">
			<div class="ctrlHolder">
				<label for="val_password">Password</label>
				<input type="password" name="password" id="val_password" />
			</div>
			<div class="ctrlHolder">
				<label for="val_confirm_password">Repeat password</label>
				<input type="password" name="confirm_password" id="val_confirm_password" />
			</div>
		</div>
		
		<?php
		
		$user = $sessionHandler->getCurrentUser();
		
		if($user->data["level"] == 0)
		{
		?>
		<div class="ctrlHolder">
			<label for="val_level">Level</label>			
			<select name="level" id="val_level">
				<option value="0">administrator</option>
				<option value="2">moderator</option>
				<option value="4">user</option>
			</select>
		</div>
		<?
		}
		
		?>
		
				
		<div class="buttonHolder">
			<input type="button" value="Submit" class="button save_edit_form">
			<input type="button" value="Cancel" class="button cancel_edit_form">
		</div>
	</fieldset>
</form>