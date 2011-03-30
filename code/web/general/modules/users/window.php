<form id="editUserForm">
	<fieldset>
		<input type="hidden" name="action" value="editUser" />
		<input type="hidden" name="id" id="val_id" />
		Name: <input type="name" name="name" id="val_name" /><br />
		E-mail: <input type="email" name="email" id="val_email" /><br />
		Verander wachtwoord: <input type="checkbox" name="change_password" id="val_change_password" checked="true" onChange="triggerPassword(true);" /><br />
		<div id="changePassword">
			Wachtwoord: <input type="password" name="password" id="val_password" /><br />
			Herhaal wachtwoord: <input type="password" name="confirm_password" id="val_confirm_password" /><br />
		</div>
		Level: <input type="name" name="level" id="val_level" /><br />
		<input type="button" value="Cancel" class="cancel_edit_form" />
		<input type="button" value="Submit" class="save_edit_form" />
	</fieldset>
</form>