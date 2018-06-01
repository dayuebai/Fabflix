function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

let starId = getParameterByName("starId");
let queryUrl = "?starId=" + starId;

function handleResult(resultData) {
	
	let starTableBodyElement = jQuery("#star_list_body");
	
	console.log(resultData.length);
	
    let rowHTML = "<div class='container'>";
    rowHTML += "<div class='jumbotron bg-dark' style='background-color:#415472'>";
    
    rowHTML += "<div class='row justify-content-end'>" + 
				   "<div class='col'>" + 
				   		"<p style='color: #d1d3d6; font-size: 200%; font-weight: bold;' class='movieStar'>" + resultData[0]["starName"] + "</p>" + 
				   "</div>" + 
			  "</div>";
    
    rowHTML += "<table width='800' border='0' cellspacing='0' cellpadding='0'><tr><td width='200'>&nbsp;</td><td>&nbsp;</td></tr>";
    rowHTML += "<tr><td style='text-align:left;'><h2>Birth Year:</h2></td><td style='text-align:left;'><h3>" + (resultData[0]["starBirth"] ? resultData[0]["starBirth"] : "Birth record not found") + "</h3></td></tr>";
	
    // For list of movies the star performed
    rowHTML += "<tr><td style='text-align:left;'><h2>Films:</h2></td><td style='text-align:left;'>";
	movieArray = resultData[0]["listofMovieName"].split(",");
	movieIdArray = resultData[0]["listofMovieId"].split(",");
    
	
	var i;
	for (i = 0; i < movieArray.length - 1; i++)
		rowHTML += "<a class='movie' href='movies.html?id=" + movieIdArray[i] + "'>" + movieArray[i] + "</a>" + ", ";
	rowHTML += "<a class='movie' href='movies.html?id=" + movieIdArray[i] + "'>" + movieArray[i] + "</a>";
	
	rowHTML += "</td></tr>";
    rowHTML += "</table></div></div>";
	
	starTableBodyElement.append(rowHTML);
	
	// For copyright footer
	let copyrightElement = jQuery(".footer");
			
	
    let footerString =  "<hr style='padding: 0; margin: 0; border-color: #ffffff; border-width: 2px;' noshade>" + 
					"<ul style='align: middle; padding: 0;'>" + 
						"<li><a href='index.html'>Home</a>" + 
						"<li></li>" + 
						" <li>|</li> " + 
						"<li><a href='https://www.ics.uci.edu/~dayueb/'>About Fabflix</a>" + 
						"<li></li>" + 
						" <li>|</li> " + 
						"<li><a href='login.html'>Register an Account</a>" + 
						"<li></li>" + 
						" <li>|</li> " + 
						"<li><a href='cart.html'>Checkout</a>" + 
						"<li></li>" + 
						" <li>|</li> " + 
						"<li><a href='#'>Privacy Policy</a></li>" + 
					"</ul>" + 
					"<p align='middle' class='copyright'>Copyright &copy; 2018 by Michael Wang & Dayue Bai. All rights reserved.</p>";
    copyrightElement.append(footerString);
}

jQuery.ajax( {
	datatype: "json",
	method: "GET",
	url: "singleStar" + queryUrl,
	success: (resultData) => handleResult(resultData)
});