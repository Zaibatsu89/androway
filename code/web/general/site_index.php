<?php
	require_once("init.php");
	
	init("http://m.androway.nl/", "http://www.androway.nl/", "androway_framework", "androway", "********");
?>
<html> 
	<head>
		<!-- The required style files -->
		<link rel="stylesheet" href="styles/framework.css"/>
		<link rel="stylesheet" href="styles/flexigrid.css"/> 
		
		<!-- The required script files -->
		<script type="text/javascript" src="scripts/lib/jquery.1.5.1.min.js"></script>
		<script type="text/javascript" src="scripts/lib/jquery.ui.all.js"></script>
		<script type="text/javascript" src="scripts/lib/jquery.flexigrid.js"></script>
		
		<script type="text/javascript" src="scripts/framework.functions.js"></script>
		<script type="text/javascript" src="scripts/framework.protected_functions.js"></script>
		<script type="text/javascript" src="modules/sessions/script.js"></script>
		<script type="text/javascript" src="modules/users/script.js"></script>
		
		<script type="text/javascript"> 
			$(function()
			{
				$('#loginDialog').dialog();
			});

			function login(formId)
			{
				var loginForm = $('#' + formId);
				
				// Een ajax post met de values array
				$.post('webservices/authService.php', loginForm.serializeArray(), function(data)
				{
					if (data)
					{
						loginForm.find('.message_box').fadeTo(200, 0.1, function()
						{
							$(this).html('Logging in...').addClass('auth_valid').fadeTo(900, 1, function()
						  	{
								loginForm.find('.message_box').removeClass('auth_valid').hide();
						  		$('#loginDialog').dialog('close');

						  		$('#main_menu').show();
							});
						});
					}
					else
					{
						loginForm.find('.message_box').fadeTo(200, 0.1, function()
						{
							$(this).html('Login failed').addClass('auth_error').fadeTo(900, 1);
						});
					}
				});
			}	
		</script> 
	</head>
	<body>
		<div id="main_menu">
			<div class="menu_item" onClick="startModule('users', 'dialog');">
				Users
			</div>
			<div class="menu_item" onClick="startModule('sessions', 'dialog');">
				Sessions
			</div>
		</div>
		<div id="loginDialog" title="Authentication">
			<form id="loginForm">
			<fieldset>
				<input type="hidden" name="authType" value="login"/>
				Email: <input type="email" name="email" />
				Password: <input type="password" name="password" />
				<input type="button" value="Login" onclick="login('loginForm');"/>
				<div class="message_box auth_normal"></div>
			</fieldset>
			</form>
			
			<!--
			<form id="logoutForm">
				<fieldset>
					<input type="hidden" name="authType" value="logout"/>
					<input type="button" value="Logout" onclick="logout('logoutForm');"/>
					<div class="message_box auth_normal"></div>
				</fieldset>
			</form>
			-->
		</div>
	</body>
</html>