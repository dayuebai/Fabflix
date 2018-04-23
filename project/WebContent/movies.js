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

let title = "";
let year = "";
let director = "";
let star = "";
let genre= getParameterByName('genre') ? getParameterByName('genre') : "";
let pageNumber = getParameterByName('pageNumber') ? getParameterByName('pageNumber') : 1;
let movieNumber = getParameterByName('movieNumber');
let queryUrl = "?title=" + title + "&genre=" + genre + "&year=" + year + "&director=" + director + "&star=" + star + "&pageNumber=";

function handleResult(resultData) {
	
    console.log("handleResult: populating result movie list");
    let movieSearchResultElement = jQuery("#search_info");
    movieSearchResultElement.append("<h5>About " + resultData[0]["totalFound"].toString() + " results</h5>");
    
    let movieTableBodyElement = jQuery("#movie_list_body");
    for (let i = 0; i < Math.min(movieNumber, resultData.length); i++) {
        let rowHTML = "<div class='container'>";
        rowHTML += "<div class='jumbotron'>";
        rowHTML += "<h2 style='color:red;'>" + resultData[i]["movieName"] + "</h2>";
        rowHTML += "<h3>Year: " + resultData[i]["movieYear"] + "</h3>";
        rowHTML += "<h3>Director: " + resultData[i]["movieDirector"] + "</h3>";
        rowHTML += "<h3>Rating: " + resultData[i]["movieRating"] + "</h3>";
        rowHTML += "<h3>Stars: " + resultData[i]["listofStars"] + "</h3>";
        rowHTML += "<h3>Genres: " + resultData[i]["listofGenres"] + "</h3>";
        rowHTML += "<h3>ID:" + resultData[i]["movieId"] + "</h3>";
        rowHTML += "</div></div>";
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