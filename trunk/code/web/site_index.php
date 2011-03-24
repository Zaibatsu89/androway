<?php
	require_once("init.php");
	
	init("http://m.androway.nl/", "http://www.androway.nl/", "androway_framework", "androway", "hz7bkaxw");
?>
<html> 
	<head> 
		<script type="text/javascript" src="scripts/lib/jquery-1.5.1.min.js"></script>  
		
		<script type="text/javascript"> 
			// wait for the DOM to be loaded 
			$(document).ready(function() { 
				// bind 'myForm' and provide a simple callback function 
				$('#form').submit(function(){
					var values = $(this).serializeArray();

					console.log(values);

					// Een ajax post met met de values array
					
					
					return false;
				});
			}); 
		</script> 
	</head>
	<body>
		Globale website.
		<hr>
		<form id="form" action="login.php" method="post">
			E-mail: <input type="email" name="email" />
			<!-- encrypt password before sending -->
			Wachtwoord: <input type="password" name="password" />
			<input type="submit" value="Login" />
		</form>
	</body>
</html>