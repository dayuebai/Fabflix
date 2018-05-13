/**
 * Handle the data returned by LoginServlet
 */
function handleLoginResult(resultDataString) {
	try {
		resultDataJson = JSON.parse(resultDataString);
		
		console.log("handle login response");
		console.log(resultDataJson);
		console.log(resultDataJson["status"]);
		
		if (resultDataJson["status"] === "success") {
			window.location.replace("index.html");
		}
		else {
			console.log("show error message");
			console.log(resultDataJson["message"]);
			jQuery("#login_error_message").text(resultDataJson["message"]);
			grecaptcha.reset();
		}
	} catch (err) {
		console.log("Recaptcha not received.");
		jQuery("#login_error_message").text("Please click checkbox");
	}

}


function submitLoginForm(formSubmitEvent){
	console.log("submit login form");
	
	// Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
	formSubmitEvent.preventDefault();
	
	jQuery.post(
			"api/login",
			// Serialize
			jQuery("#login_form").serialize(),
			(resultDataString) => handleLoginResult(resultDataString));
}

// Bind the submit action of the form to a handler function
jQuery("#login_form").submit((event) => submitLoginForm(event)); 