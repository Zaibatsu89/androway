<?php
	if(isset($_REQUEST["module_id"]))
		echo "<input class=\"module_id\" type=\"hidden\" value=\"" . $_REQUEST["module_id"] ."\">";
?>
<script type="text/javascript">
	$(function()
	{
		initLogsModule();
	});
</script>
<table id="logsTable" style="display: none;"></table>