function loadText(data)
{
	$('#content').append
	(
			'<div class="text_module">'
		+		'<div class="title">'+ data.title +'</div>'
		+		data.content
		+	'</div>'
	);
}