function handleResult(resultData) {
    let confirmationInfoElement = jQuery("#confirmation_info");
    let rowHTML = "<div style='text-align: center;'><h2><i>Congratulations! Your purchase has been processed</i></h2>";
    rowHTML += "<a href='movies.html'><h2><i>Back to see more movies</i></h2></a></div>"
    console.log(resultData.length);
    for (let i = 0; i < resultData.length; ++i) {
    	rowHTML += "<div class='container'>";
    	rowHTML += "<div class='jumbotron bg-dark'>";
    	rowHTML += "<div class='row justify-content-center'>";
    	rowHTML += "<div class='col text-center'>";
    	rowHTML += "<a style='font-size: 180%; color: #c44f1f;' href='movies.html?id=" + resultData[i]["movieId"] + "' " + "data-toggle='tooltip' data-placement='top' title='Sales ID: " 
    		+ resultData[i]['saleIdList'] + "'>" + resultData[i]['name'] + "</a></div>";
    	rowHTML +=  "<div class='col text-center'>";
    	rowHTML +=  "<h3 style='color: #ffffff;'>" + resultData[i]['amount'] + "</h3>";
    	
    	rowHTML += "</div></div></div></div>";
    	
    }
    
    confirmationInfoElement.append(rowHTML);
    
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
	url: "api/confirmation",
	success: (resultData) => handleResult(resultData)
});
