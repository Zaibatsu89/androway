<?php
	require_once("init.php");
	init();
?>
<html> 
	<head>
		<!-- The required style files -->
		<link rel="stylesheet" href="styles/framework.css"/>
		<!--<link rel="stylesheet" href="styles/flexigrid.css"/>-->
		<link rel="stylesheet" href="styles/jquery.mobile.min.css"/> 
		
		<!-- The required script files -->
		<script type="text/javascript" src="scripts/lib/jquery.min.js"></script>
		<script type="text/javascript">
			$(document).bind('mobileinit',function(){
				$.mobile.selectmenu.prototype.options.nativeMenu = false;
			});
		</script>
		<script type="text/javascript" src="scripts/lib/jquery.mobile.min.js"></script>
		
		<script type="text/javascript" src="scripts/lib/jquery.ui.all.js"></script>
		<!--
		<script type="text/javascript" src="scripts/lib/jquery.flexigrid.js"></script>
		-->
		<script type="text/javascript" src="scripts/framework.functions.js"></script>
		
		<script type="text/javascript" src="scripts/framework.site.js"></script>
		
		<script type="text/javascript" src="site_modules/grid/script.js"></script>
		
		<script type="text/javascript" src="site_modules/text/script.js"></script>
		<!--
		<script type="text/javascript" src="site_modules/logs/script.js"></script>
		<script type="text/javascript" src="site_modules/sessions/script.js"></script>
		-->
		

		
		<?php
			// Protected script functions. Only included when logged in.
			if($sessionHandler->authenticate())
			{
				?>
				<script type="text/javascript" src="scripts/framework.protected_functions.js"></script>
				<script type="text/javascript" src="system_modules/logs/script.js"></script>
				<script type="text/javascript" src="system_modules/sessions/script.js"></script>
				<script type="text/javascript" src="system_modules/users/script.js"></script>
				<?
			}
		?>
		
		<script type="text/javascript">
			<?php
				if(!$sessionHandler->authenticate() && isset($_GET['admin']))
					echo "$(function(){ $('#loginDialog').dialog(); });";
			?>

			function login(formId)
			{
				var loginForm = $('#' + formId);
				
				// Een ajax post met de values array
				$.getJSON('webservices/authService.php', loginForm.serializeArray(), function(data)
				{
					if (data.success == 'true')
					{
						loginForm.find('.message_box').fadeTo(200, 0.1, function()
						{
							$(this).html('Logging in...').addClass('auth_valid').fadeTo(900, 1, function()
						  	{
								loginForm.find('.message_box').removeClass('auth_valid').hide();

								window.location = '/site_index.php';
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

			function logout()
			{
				// Een ajax post om uit te loggen
				$.post('webservices/authService.php', { authType : 'logout' }, function()
				{
					// Redirect
					window.location = '/site_index.php';
				});
			}
		</script> 
	</head>
	<body>
		<?php
		if ($sessionHandler->authenticate())
		{
		?>
		<!-- Html only shown when logged in -->
		<div id="main_menu">
			<div class="menu_item_left" onClick="startModule('users', 'dialog');">
				Users
			</div>
			<div class="menu_item_left" onClick="startModule('sessions', 'dialog');">
				Sessions
			</div>
			<div class="menu_item_left" onClick="startModule('logs', 'dialog');">
				Logs
			</div>
			<div class="menu_item_right" onClick="logout();">
				Logout
			</div>
		</div>
		<?
		}
		else if (isset($_GET['admin']))
		{
		?>
		<!-- Html only shown when not logged in -->
		<div id="loginDialog" title="Authentication">
			<form id="loginForm">
			<fieldset>
				<input type="hidden" name="authType" value="login"/>
				Email: <input type="email" name="email" /><br />
				Password: <input type="password" name="password" /><br />
				<input type="button" value="Login" onclick="login('loginForm');"/><br />
				<div class="message_box auth_normal"></div>
			</fieldset>
			</form>
		</div>
		<?
		}
		else
		{
		?>
		<div data-role="page" id="page">
			<div data-role="content" id="contentText"></div>
			<div data-role="content" id="contentGrid"></div>
		</div>
		<?
		}
		?>
	</body>
</html>