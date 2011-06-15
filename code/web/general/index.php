<?php
	require_once("init.php");
	/**
	 * Index of Androway website.
	 * @author Rinse Cramer & Tymen Steur
	 * @date 14-06-2011
	 * @version 0.5
	 */
	init(false);
?>
<html>
	<head>
		<!-- The required style files -->
		<link rel="stylesheet" href="styles/framework.css"/>
		<link rel="stylesheet" href="styles/uni-form.css"/>
		
		<!-- The site favicon -->
		<link rel="shortcut icon" href="images/favicon.ico" />
		
		<!-- The required script files -->
		<script type="text/javascript" src="scripts/lib/jquery.min.js"></script>	
		
		<script type="text/javascript" src="scripts/lib/jquery.ui.all.js"></script>
		<script type="text/javascript" src="scripts/lib/jquery.uni-form.js"></script>
		<script type="text/javascript" src="scripts/framework.functions.js"></script>

		<?php
			if($sessionHandler->authenticate())
			{
				// Protected script and style resources. Only included when logged in.
				?>
				<link rel="stylesheet" href="styles/flexigrid.css"/>
				<link rel="stylesheet" href="styles/jquery.ui.css"/>
				
				<script type="text/javascript" src="scripts/lib/jquery.flexigrid.js"></script>
				<script type="text/javascript" src="scripts/framework.protected_functions.js"></script>
				<script type="text/javascript" src="system_modules/logs/script.js"></script>
				<script type="text/javascript" src="system_modules/sessions/script.js"></script>
				<script type="text/javascript" src="system_modules/users/script.js"></script>
				<?php
			}
			else if(!isset($_GET['admin']))
			{
				// Public script and style resources. Only included when NOT logged in.
				?>
				<link rel="stylesheet" href="styles/jquery.mobile.min.css"/>
				<link rel="stylesheet" href="styles/svg.css" />
				<link rel="stylesheet" href="styles/jquery.svg.css" />
				
				<script type="text/javascript">
				<?php
					if(!isset($_REQUEST["from_app"]))
					{
						?>
						$(document).bind('mobileinit',function()
						{
							$.mobile.selectmenu.prototype.options.nativeMenu = false;
						});
						<?php
					}
				?>
				</script>
				
				<script type="text/javascript" src="scripts/lib/jquery.mobile.min.js"></script>
				
				<script type="text/javascript" src="scripts/lib/jquery.svg.pack.js"></script>
				<script type="text/javascript" src="scripts/framework.site.js"></script>
				<script type="text/javascript" src="site_modules/grid/script.js"></script>		
				<script type="text/javascript" src="site_modules/replay/script.js"></script>		
				<script type="text/javascript" src="site_modules/replay/svg.js"></script>		
				<script type="text/javascript" src="site_modules/text/script.js"></script>		
				<script type="text/javascript" src="site_modules/logs/script.js"></script>
				<script type="text/javascript" src="site_modules/sessions/script.js"></script>
				<?php
			}
		?>
		
		<script type="text/javascript">
			<?php
				if(!$sessionHandler->authenticate() && isset($_GET['admin']))
					echo "$(function(){ $('#loginDialog').dialog({ width: 550 }); });";
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

								window.location = '/';
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
					window.location = '/';
				});
			}
			
			var fromApp = <?php echo (isset($_REQUEST["from_app"])) ? ("true") : ("false");?>;
		</script> 
	</head>
	<body>
		<?php
		if ($sessionHandler->authenticate())
		{
		?>
		<!-- Html only shown when logged in -->
		<div class="panel-bg">
			<div id="main_menu" class="ui-widget-header">
				<div class="menu_item menu_item_left" onClick="startModule('users', 'dialog');">
					Users
				</div>
				<div class="menu_item menu_item_left" onClick="startModule('sessions', 'dialog');">
					Sessions
				</div>
				<div class="menu_item menu_item_left" onClick="startModule('logs', 'dialog');">
					Logs
				</div>
				<div class="menu_item menu_item_right" onClick="logout();">
					Logout
				</div>
			</div>
			<div class="panel-logo"></div>
		</div>
		<?php
		}
		else if (isset($_GET['admin']))
		{
		?>
		<link rel="stylesheet" href="styles/jquery.ui.css"/>
		
		<!-- Html only shown when not logged in -->
		<div id="loginDialog" title="Authentication">
			<form id="loginForm" class="uniForm">
				<fieldset class="inlineLabels">
					<input type="hidden" name="authType" value="login"/>					
					<div class="ctrlHolder">
						<label for="email">Email</label>
						<input type="email" name="email"/>
					</div>					
					<div class="ctrlHolder">
						<label for="password">Password</label>
						<input type="password" name="password" onkeydown="if(event.keyCode == 13)$(this).parent().parent().find('#submitLogin').click();"/>
					</div>					
					<div class="buttonHolder">
						<input class="button" type="button" value="Login" onclick="login('loginForm');" id="submitLogin"/>
					</div>					
					<div class="message_box auth_normal"></div>
				</fieldset>
			</form>
		</div>
		<?php
		}
		else
		{
		?>
		<div data-role="page" id="pageMain">
			<div data-role="header" id="headerMain"></div>
			<div data-role="content" id="contentText"></div>
		</div>
		<?php
		}
		?>
	</body>
</html>