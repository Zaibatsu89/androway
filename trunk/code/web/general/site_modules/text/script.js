function loadText(data)
{
	$('#contentText').append
	(
		//'<div class="title">'+ data.title +'</div>'
		data.content
	).page();
}