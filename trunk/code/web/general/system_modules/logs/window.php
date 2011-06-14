<form id="editLogForm" class="uniForm">
	<fieldset class="inlineLabels">
		<input type="hidden" name="action" value="editLog" />
		<input type="hidden" name="log_id" id="val_log_id" />
		
		<div class="ctrlHolder">
			<label for="val_left_wheel">Left wheel</label>
			<input type="text" name="left_wheel" id="val_left_wheel"/>
		</div>
		
		<div class="ctrlHolder">
			<label for="val_right_wheel">Right wheel</label>
			<input type="text" name="right_wheel" id="val_right_wheel"/>
		</div>
		
		<div class="ctrlHolder">
			<label for="val_inclination">Inclination</label>
			<input type="text" name="inclination" id="val_inclination"/>
		</div>
		
		<div class="buttonHolder">
			<input class="button save_edit_form" type="button" value="Submit"/>
			<input class="button cancel_edit_form" type="button" value="Cancel"/>
		</div>
	</fieldset>
</form>