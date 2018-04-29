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

function handleResult(resultData) {
	
    console.log("handleResult: populating result movie list");
    let movieSearchResultElement = jQuery("#search_info");
    let resultCount = 0;
    
    if (resultData[0] === undefined)
    	movieSearchResultElement.append("<h5>Movie Not Found</h5>");
    else{
    	resultCount = resultData[0]["totalFound"];
    	movieSearchResultElement.append("<h5>About " + resultData[0]["totalFound"].toString() + " results</h5>");
    }
    
    let movieTableBodyElement = jQuery("#movie_list_body");
    for (let i = 0; i < Math.min(movieNumber, resultData.length); i++) {
        let rowHTML = "<div class='container'>";
        rowHTML += "<div class='jumbotron'>";
        
//        	"<tr><td style='text-align:left;'>Title:</td><td style='text-align:left;'>" + movieTitle + "</td></tr>"
//      rowHTML += "<tr><td style='text-align:left;'><h2>Genres:</h2></td><td style='text-align:left;'><h2>" + resultData[i]["listofGenres"] + "</h2></td></tr>";
//      rowHTML += "<tr><td style='text-align:left;'><h2>ID:</h2></td><td style='text-align:left;'><h2>" + resultData[i]["movieId"] + "</h2></td></tr>";
        
        rowHTML += "<a style='font-weight: bold; font-size: 200%; text-decoration: underline;' href='movies.html?id=" + resultData[i]["movieId"] + "'" + " class='movieTitle'>" + resultData[i]["movieName"] + "</a>";
        
        rowHTML += "<table width='800' border='0' cellspacing='0' cellpadding='0'><tr><td width='200'>&nbsp;</td><td>&nbsp;</td></tr>";
        rowHTML += "<tr><td style='text-align:left;'><h2>Year:</h2></td><td style='text-align:left;'><h2>" + resultData[i]["movieYear"] + "</h2></td></tr>";
        rowHTML += "<tr><td style='text-align:left;'><h2>Director:</h2></td><td style='text-align:left;'><h2>" + resultData[i]["movieDirector"] + "</h2></td></tr>";
        rowHTML += "<tr><td style='text-align:left;'><h2>Rating:</h2></td><td style='text-align:left;'><h2>" + (resultData[i]["movieRating"] ? resultData[i]["movieRating"] : "0.0") + "</h2></td></tr>";

        rowHTML += "<tr><td style='text-align:left;'><h2>Genres:</h2></td><td style='text-align:left;'>";
        genreArray = resultData[i]["listofGenres"].split(",");
        var k;
        for (k = 0; k < genreArray.length - 1; k++)
        	rowHTML += "<a style='font-size: 150%; text-decoration: underline;' href='movies.html?genre=" + genreArray[k] + "'>" + genreArray[k] + "</a>" + ", ";   
        rowHTML += "<a style='font-size: 150%; text-decoration: underline;' href='movies.html?genre=" + genreArray[k] + "'>" + genreArray[k] + "</a>";  
        rowHTML += "</td></tr>";
        
        rowHTML += "<tr><td style='text-align:left;'><h2>Stars:</h2></td><td style='text-align:left;'>";
        starArray = resultData[i]["listofStars"].split(",");
        starIdArray = resultData[i]["listofIds"].split(",");
      
        var j;
        for (j = 0; j < starArray.length - 1; j++)
        	rowHTML += "<a style='font-size: 150%; text-decoration: underline;' href='stars.html?starId=" + starIdArray[j] + "'>" + starArray[j] + "</a>" + ", ";   
        rowHTML += "<a style='font-size: 150%; text-decoration: underline;' href='stars.html?starId=" + starIdArray[j] + "'>" + starArray[j] + "</a>";  
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
    for (let i = 1; i <= Math.min(3, resultCount / movieNumber); ++i)
    {
    	paginationElement.append("<li class='page-item'><a class='page-link' href=" + "movies.html" + queryUrl + i.toString() + "&movieNumber=" + movieNumber.toString() + ">" + i.toString() + "</a></li>");
    }
	paginationElement.append("<li class='page-item disabled'><a class='page-link' href='#'>...</a></li>");
	paginationElement.append("<li class='page-item'><a class='page-link' href=" + "movies.html" + queryUrl + Math.ceil(resultCount / movieNumber).toString() + "&movieNumber=" + movieNumber.toString() + ">" + Math.ceil(resultCount / movieNumber).toString() + "</a></li>");
	console.log(Math.ceil(resultCount / movieNumber).toString());
	if (pageNumber < Math.ceil(resultCount / movieNumber))
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