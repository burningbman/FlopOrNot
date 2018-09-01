function onFormSubmit(e) {
	// prevent the form from reloading the page
	e.preventDefault();
	
	// make an ajax call using jquery
	$.ajax({
		url: window.origin + '/FlopOrNot/calculate',
		success: function(rating) {
			alert(rating);
		},
		method: "PUT"
	})
}

function setupListeners() {
	// make the form submit use our method
	document.getElementById('form').onsubmit = onFormSubmit;
}

// when the DOM has been loaded, setup our form listener
document.addEventListener("DOMContentLoaded", setupListeners);