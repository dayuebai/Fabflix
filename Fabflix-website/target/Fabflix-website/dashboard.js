function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Use regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

let metaData;

function handleResult(resultData) {
	console.log("handleResult: populating result metadata");
	let metaDataTable = jQuery(".container");
	
	if (resultData.length > 1)
		metaData = resultData;
	else if (resultData[0]["message"] === undefined){
		document.getElementById("updateStar").reset();
		alert("Successfully insert a star: " + resultData[0]["starName"]);
	}
	else if (resultData[0]["starName"] === undefined){
		document.getElementById("updateMovie").reset();
		alert(resultData[0]["message"]);
	}
	for (let i = 0; i < metaData.length; ++i) 
	{
		let table = metaData[i][0]["table_name"];
		let rowHTML = "<div class='jumbotron bg-dark'>";
		rowHTML += "<h2 style='color: #c44f1f;'>" + table + "</h2>";
		rowHTML += "<table width='800' border='0' cellspacing='0' cellpadding='0'><tr><td width='200'>&nbsp;</td><td>&nbsp;</td></tr>";
		let metaArray = metaData[i];
		for (let i = 1; i < metaArray.length; ++i) {
			rowHTML += "<tr><td style='text-align:left;'><h4>" + metaArray[i]["attr_name"] + "</h4></td><td style='text-align:left;'><h4>" + metaArray[i]["attr_type"] + "</h4></td></tr>";
		}
		rowHTML += "</table></div>";
		console.log(rowHTML);
		metaDataTable.append(rowHTML);
	}
	
}


function submitStarForm(formSubmitEvent){
	console.log("submit update star form");
	
	// Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
	formSubmitEvent.preventDefault();
	
	jQuery.get(
			"api/dashboard",
			// Serialize
			jQuery("#updateStar").serialize(),
			(resultData) => handleResult(resultData));
}

function submitMovieForm(formSubmitEvent){
	console.log("submit update movie form");
	
	// Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
	formSubmitEvent.preventDefault();
	
	jQuery.get(
			"api/dashboard",
			// Serialize
			jQuery("#updateMovie").serialize(),
			(resultData) => handleResult(resultData));
}

// Bind the submit action of the form to a handler function
jQuery("#updateStar").submit((event) => submitStarForm(event)); 
jQuery("#updateMovie").submit((event) => submitMovieForm(event)); 

jQuery.ajax( {
	datatype: "json",
	method: "GET",
	url: "api/dashboard",
	success: (resultData) => handleResult(resultData)
});

// For test: to see what url is actually sent to Backend Java Servlet
console.log("Front end sends: api/dashboard")