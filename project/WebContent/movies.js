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

let sortKind = getParameterByName('sort') ? getParameterByName('sort') : "rating";
let sortOrder = getParameterByName('order') ? getParameterByName('order') : "desc";
console.log(sortKind + " " + sortOrder);
let title = getParameterByName('title') ? getParameterByName('title') : "";
let year = getParameterByName('year') ? getParameterByName('year') : "";
let director = getParameterByName('director') ? getParameterByName('director') : "";
let star = getParameterByName('star') ? getParameterByName('star') : "";
let genre= getParameterByName('genre') ? getParameterByName('genre') : "";
let pageNumber = getParameterByName('pageNumber') ? getParameterByName('pageNumber') : 1;
let movieNumber = getParameterByName('movieNumber') ? getParameterByName('movieNumber') : 10;
let mId = getParameterByName('id') ? getParameterByName('id') : "";
let queryUrl = "?id=" + mId + "&sort=" + sortKind + "&order=" + sortOrder + "&title=" + title + "&genre=" + genre + "&year=" + year + "&director=" + director + "&star=" + star + "&pageNumber=";
let urlHelper = "&title=" + title + "&genre=" + genre + "&year=" + year + "&director=" + director + "&star=" + star;

function handleResult(resultData) {
	
    console.log("handleResult: populating result movie list");
    
    // #id: search_info
    let movieSearchResultElement = jQuery("#search_info");
    let resultCount = 0;
    
    if (resultData[0] === undefined)
    	movieSearchResultElement.append("<h5>Movie Not Found</h5>");
    else{
    	resultCount = resultData[0]["totalFound"];
    	movieSearchResultElement.append("<p>" + resultData[0]["totalFound"].toString() + " result(s) found</p>");
    }
    
    // #id: sort
    let sortElement = jQuery("#sort_bar");

    if (resultCount > 1) {
    	sortElement.append("<div class='container'>" + 
    							"<div class='row justify-content-center'>" + 
    								"<div class='text-center'>" + 
    									"<a href='movies.html?sort=title&order=asc" + urlHelper + "'>Title</a>" + 
    									"<i style='color:red; vertical-align: bottom;' class='fa fa-sort-asc' aria-hidden='true'></i>" + 
    									" | " + 
    									"<a href='movies.html?sort=title&order=desc" + urlHelper + "'>Title</a>" + 
    									"<i style='color:green; vertical-align: top;' class='fa fa-sort-desc' aria-hidden='true'></i>" +    									
    									" | " + 
    									"<a href='movies.html?sort=rating&order=asc" + urlHelper + "'>Rating</a>" + 
    									"<i style='color:red; vertical-align: bottom;' class='fa fa-sort-asc' aria-hidden='true'></i>" + 
    									" | " + 
    									"<a href='movies.html?sort=rating&order=desc" + urlHelper + "'>Rating</a>" + 
    									"<i style='color:green; vertical-align: top;' class='fa fa-sort-desc' aria-hidden='true'></i>" +
    								"</div>" + 
    							"</div>" + 
    						"</div>"
    						);
    }
    
    
    // #id: movie_list_body
    let movieTableBodyElement = jQuery("#movie_list_body");
    for (let i = 0; i < Math.min(movieNumber, resultData.length); i++) {
        let rowHTML = "<div class='container'>";
        rowHTML += "<div class='jumbotron bg-dark'>";
        
//        	"<tr><td style='text-align:left;'>Title:</td><td style='text-align:left;'>" + movieTitle + "</td></tr>"
//      rowHTML += "<tr><td style='text-align:left;'><h2>Genres:</h2></td><td style='text-align:left;'><h2>" + resultData[i]["listofGenres"] + "</h2></td></tr>";
//      rowHTML += "<tr><td style='text-align:left;'><h2>ID:</h2></td><td style='text-align:left;'><h2>" + resultData[i]["movieId"] + "</h2></td></tr>";
       
        rowHTML += "<div class='row justify-content-end'>" + 
	        		   "<div class='col'>" + 
	        				"<a style='font-weight: bold; font-size: 200%;' href='movies.html?id=" + resultData[i]["movieId"] + "'" + " class='movieTitle'>" + resultData[i]["movieName"] + "</a>" + 
	        		   "</div>"	+ 
	        		   "<div class='col-2'>" + 
	       					"<a style='margin-right: 8px;' class='btn btn-danger' style='color:white' href='cart.html?" + "cId=" + resultData[i]["movieId"] + "&cName=" + resultData[i]["movieName"] + "&cAmount=1&amount=" + "'>" + 
	   						"<i class='fa fa-shopping-cart fa-lg' aria-hidden='true'></i> Add to Cart</a>" +
	        		   "</div>" + 
	        	   "</div>";
        		   
        rowHTML += "<table width='800' border='0' cellspacing='0' cellpadding='0'><tr><td width='200'>&nbsp;</td><td>&nbsp;</td></tr>";
        rowHTML += "<tr><td style='text-align:left;'><h2>Year:</h2></td><td style='text-align:left;'><h2>" + resultData[i]["movieYear"] + "</h2></td></tr>";
        rowHTML += "<tr><td style='text-align:left;'><h2>Director:</h2></td><td style='text-align:left;'><h2>" + resultData[i]["movieDirector"] + "</h2></td></tr>";
        rowHTML += "<tr><td style='text-align:left;'><h2>Rating:</h2></td><td style='text-align:left;'><h2>" + (resultData[i]["movieRating"] ? resultData[i]["movieRating"] : "0.0") + "</h2></td></tr>";

        rowHTML += "<tr><td style='text-align:left;'><h2>Genres:</h2></td><td style='text-align:left;'>";
        genreArray = resultData[i]["listofGenres"].split(",");
        var k;
        for (k = 0; k < genreArray.length - 1; k++)
        	rowHTML += "<a class='genre' href='movies.html?genre=" + genreArray[k] + "'>" + genreArray[k] + "</a>" + ", ";   
        rowHTML += "<a class='genre' href='movies.html?genre=" + genreArray[k] + "'>" + genreArray[k] + "</a>";  
        rowHTML += "</td></tr>";
        
        rowHTML += "<tr><td style='text-align:left;'><h2>Stars:</h2></td><td style='text-align:left;'>";
        starArray = resultData[i]["listofStars"].split(",");
        starIdArray = resultData[i]["listofIds"].split(",");
      
        var j;
        for (j = 0; j < starArray.length - 1; j++)
        	rowHTML += "<a class='star' href='stars.html?starId=" + starIdArray[j] + "'>" + starArray[j] + "</a>" + ", ";   
        rowHTML += "<a class='star' href='stars.html?starId=" + starIdArray[j] + "'>" + starArray[j] + "</a>";  
        rowHTML += "</td></tr>";
        
//        "<a style='font-size: large; color: red; text-decoration: underline;' href='#'>" 
        
        rowHTML += "</table></div></div>";
        console.log(rowHTML);
        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
    
    // #id: footer
    let footerElement = jQuery("#footerBar");
    
    
	if (resultCount > 1) {   
	    var pagString = "<div class='container'>" + 
							"<div class='row justify-content-center'>" + 
								"<a style='color: #b5462b; margin: 0; text-decoration: underline;' href='#top'>Back to top</a>" + 
							"</div>" + 
    						"<div class='row justify-content-center'>" + 
    							"<div style='margin: 5px;' class='col-xs-5'>" +
    								"<ul class='pagination'>";
	    
	    if (pageNumber > 1)
	    	pagString += "<li class='page-item'><a class='page-link' href='movies.html" + queryUrl + (parseInt(pageNumber) - 1).toString() + "&movieNumber=" + movieNumber.toString() + "'>" + "Previous</a></li>";
	    
	    for (let i = 1; i <= Math.min(3, Math.ceil(resultCount / movieNumber)); ++i)
	    {
	    	pagString += "<li class='page-item'><a class='page-link' href='movies.html" + queryUrl + i.toString() + "&movieNumber=" + movieNumber.toString() + "'>" + i.toString() + "</a></li>";
	    }
	    
	    if (Math.ceil(resultCount / movieNumber) >= 4) {
	    	pagString += "<li class='page-item'><a class='page-link' href=''>...</a></li>";
	    	pagString += "<li class='page-item'><a class='page-link' href='movies.html" + queryUrl + Math.ceil(resultCount / movieNumber).toString() + "&movieNumber=" + movieNumber.toString() + "'>" + Math.ceil(resultCount / movieNumber).toString() + "</a></li>";
	    }
			
		if (pageNumber < Math.ceil(resultCount / movieNumber))
			pagString += "<li class='page-item'><a class='page-link' href='movies.html" + queryUrl + (parseInt(pageNumber) + 1).toString() + "&movieNumber=" + movieNumber.toString() + "'>" + "Next</a></li>";
		
		pagString += "</ul></div>";
		
		pagString += "<div style='margin: 5px;' class='col-xs-5'>" + 
						"<div class='dropup'>" + 
							"<button type='button' class='btn btn-danger dropdown-toggle' data-toggle='dropdown'>" + 
								"Movies per Page" + 
							"</button>" + 
							"<div class='dropdown-menu'>" + 
								"<a class='dropdown-item' href='movies.html" + queryUrl + "1&movieNumber=10'>" + "10</a>" + 
								"<a class='dropdown-item' href='movies.html" + queryUrl + "1&movieNumber=25'>" + "25</a>" + 
								"<a class='dropdown-item' href='movies.html" + queryUrl + "1&movieNumber=50'>" + "50</a>" + 
								"<a class='dropdown-item' href='movies.html" + queryUrl + "1&movieNumber=100'>" + "100</a>" + 
							"</div></div></div></div>" + "</div>";
		
		footerElement.append(pagString);
    }
	
	if (resultCount != 0)
	{
		// class footer copyright
		let copyrightElement = jQuery(".footer");
		
	    footerString =  "<hr style='padding: 0; margin: 0; border-color: #ffffff; border-width: 2px;' noshade>" + 
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
							"<li><a href='checkout.html'>Checkout</a>" + 
							"<li></li>" + 
							" <li>|</li> " + 
							"<li><a href='#'> Privacy Policy</a></li>" + 
						"</ul>" + 
						"<p align='middle' class='copyright'>Copyright &copy; 2018 by Michael Wang & Dayue Bai. All rights reserved.</p>";
	    copyrightElement.append(footerString);
	}
	// For test
	console.log(Math.ceil(resultCount / movieNumber).toString());
	//	let dropupElement = jQuery(".dropdown-menu");
}


jQuery.ajax( {
	datatype: "json",
	method: "GET",
	url: "api/db" + queryUrl + pageNumber + "&movieNumber=" + movieNumber,
	success: (resultData) => handleResult(resultData)
});

// For test: to see what url is actually sent to Backend Java Servlet
console.log("api/db" + queryUrl + pageNumber + "&movieNumber=" + movieNumber)