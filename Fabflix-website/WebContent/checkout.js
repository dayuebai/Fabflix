function handleResult(resultData) {
	
	console.log("handle login response");
	console.log(resultData);
	console.log(resultData["status"]);
	
	if (resultData["status"] === "fail"){
		console.log("show error message");
		console.log(resultData["message"]);
		
		document.getElementById("payment_form").reset();
//		let rowHTML = resultData["message"];
//		responseElement.append(rowHTML);
		alert("Payment information not match");
	}
	else if (resultData["status"] === "success") {
		window.location.replace("confirmation.html");
	}

}


function submitLoginForm(formSubmitEvent){
	console.log("submit payment form");
	
	// Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
	formSubmitEvent.preventDefault();
	
	jQuery.post(
			"checkout",
			// Serialize
			jQuery("#payment_form").serialize(),
			(resultData) => handleResult(resultData));
}

//jQuery.ajax( {
//	datatype: "json",
//	method: "GET",
//	url: "checkout",
//	success: (resultData) => handleResult(resultData)
//});

//Bind the submit action of the form to a handler function
jQuery("#payment_form").submit((event) => submitLoginForm(event)); 