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

let title = getParameterByName('title') ? getParameterByName('title') : "";
let year = getParameterByName('year') ? getParameterByName('year') : "";
let director = getParameterByName('director') ? getParameterByName('director') : "";
let star = getParameterByName('star') ? getParameterByName('star') : "";
let genre= getParameterByName('genre') ? getParameterByName('genre') : "";
let pageNumber = getParameterByName('pageNumber') ? getParameterByName('pageNumber') : 1;
let movieNumber = getParameterByName('movieNumber') ? getParameterByName('movieNumber') : 10;
let queryUrl = "?title=" + title + "&genre=" + genre + "&year=" + year + "&director=" + director + "&star=" + star + "&pageNumber=";

function handleResult(resultData) {
	
    console.log("handleResult: populating result movie list");
    let movieSearchResultElement = jQuery("#search_info");
    movieSearchResultElement.append("<h5>About " + resultData[0]["totalFound"].toString() + " results</h5>");
    
    let movieTableBodyElement = jQuery("#movie_list_body");
    for (let i = 0; i < Math.min(movieNumber, resultData.length); i++) {
        let rowHTML = "<div class='container'>";
        rowHTML += "<div class='jumbotron'>";
        
//        	"<tr><td style='text-align:left;'>Title:</td><td style='text-align:left;'>" + movieTitle + "</td></tr>"
        	
        rowHTML += "<a style='font-weight: bold; font-size: 200%; text-decoration: underline;' href='#' class='movieTitle'>" + resultData[i]["movieName"] + "</a>";
        	
        rowHTML += "<table width='800' border='0' cellspacing='0' cellpadding='0'><tr><td width='200'>&nbsp;</td><td>&nbsp;</td></tr>";
        rowHTML += "<tr><td style='text-align:left;'><h2>Year:</h2></td><td style='text-align:left;'><h2>" + resultData[i]["movieYear"] + "</h2></td></tr>";
        rowHTML += "<tr><td style='text-align:left;'><h2>Director:</h2></td><td style='text-align:left;'><h2>" + resultData[i]["movieDirector"] + "</h2></td></tr>";
        rowHTML += "<tr><td style='text-align:left;'><h2>Rating:</h2></td><td style='text-align:left;'><h2>" + (resultData[i]["movieRating"] ? resultData[i]["movieRating"] : "0.0") + "</h2></td></tr>";
        rowHTML += "<tr><td style='text-align:left;'><h2>Genres:</h2></td><td style='text-align:left;'><h2>" + resultData[i]["listofGenres"] + "</h2></td></tr>";
        rowHTML += "<tr><td style='text-align:left;'><h2>ID:</h2></td><td style='text-align:left;'><h2>" + resultData[i]["movieId"] + "</h2></td></tr>";
        
        rowHTML += "<tr><td style='text-align:left;'><h2>Stars:</h2></td><td style='text-align:left;'>";
        starArray = resultData[i]["listofStars"].split(",");
        var j;
        for (j = 0; j < starArray.length - 1; j++)
        	rowHTML += "<a style='font-size: 150%; text-decoration: underline;' href='#'>" + starArray[j] + "</a>" + ", ";   
        rowHTML += "<a style='font-size: 150%; text-decoration: underline;' href='#'>" + starArray[j] + "</a>";  
        rowHTML += "</td></tr>";
        
//        "<a style='font-size: large; color: red; text-decoration: underline;' href='#'>" 
        
        rowHTML += "</table></div></div>";
        console.log(rowHTML);
        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
    
    let paginationElement = jQuery(".pagination");
    
    if (pageNumber > 1)
    	paginationElement.append("<li class='page-item'><a class='page-link' href=" + "movies.html" + queryUrl + (parseInt(pageNumber) - 1).toString() + "&movieNumber=" + movieNumber.toString() + ">" + "Previous</a></li>");
    for (let i = 1; i <= Math.min(3, resultData[0]["totalFound"] / movieNumber); ++i)
    {
    	paginationElement.append("<li class='page-item'><a class='page-link' href=" + "movies.html" + queryUrl + i.toString() + "&movieNumber=" + movieNumber.toString() + ">" + i.toString() + "</a></li>");
    }
	paginationElement.append("<li class='page-item disabled'><a class='page-link' href='#'>...</a></li>");
	paginationElement.append("<li class='page-item'><a class='page-link' href=" + "movies.html" + queryUrl + Math.ceil(resultData[0]["totalFound"] / movieNumber).toString() + "&movieNumber=" + movieNumber.toString() + ">" + Math.ceil(resultData[0]["totalFound"] / movieNumber).toString() + "</a></li>");
	console.log(Math.ceil(resultData[0]["totalFound"] / movieNumber).toString());
	if (pageNumber < Math.ceil(resultData[0]["totalFound"] / movieNumber))
		paginationElement.append("<li class='page-item'><a class='page-link' href=" + "movies.html" + queryUrl + (parseInt(pageNumber) + 1).toString() + "&movieNumber=" + movieNumber.toString() + ">" + "Next</a></li>");
	
	let dropupElement = jQuery(".dropdown-menu");
	
	dropupElement.append("<a class='dropdown-item' href=" + "movies.html" + queryUrl + "1&movieNumber=10>" + "10</a>");
	dropupElement.append("<a class='dropdown-item' href=" + "movies.html" + queryUrl + "1&movieNumber=25>" + "25</a>");
	dropupElement.append("<a class='dropdown-item' href=" + "movies.html" + queryUrl + "1&movieNumber=50>" + "50</a>");
	dropupElement.append("<a class='dropdown-item' href=" + "movies.html" + queryUrl + "1&movieNumber=100>" + "100</a>");
}


jQuery.ajax( {
	datatype: "json",
	method: "GET",
	url: "api/db" + queryUrl + pageNumber + "&movieNumber=" + movieNumber,
	success: (resultData) => handleResult(resultData)
});