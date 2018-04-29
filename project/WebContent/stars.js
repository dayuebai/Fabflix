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
	let starSearchResultElement = jQuery("#search_info");
	starSearchResultElement.append("<h2>Search Result: </h2>");
	
	let starTableBodyElement = jQuery("#star_list_body");
	console.log(resultData.length);
	starSearchResultElement.append("<p>" + resultData[0]["starId"] + " " + resultData[0]["starName"] + " " + resultData[0]["starBirth"] + "</p>");
	
	let rowHTML = "";
	movieArray = resultData[0]["listofMovieName"].split(",");
	movieIdArray = resultData[0]["listofMovieId"].split(",");
	
	var i;
	for (i = 0; i < movieArray.length - 1; i++)
		rowHTML += "<a href='movies.html?id=" + movieIdArray[i] + "'>" + movieArray[i] + "</a>" + ", ";
	rowHTML += "<a href='movies.html?id=" + movieIdArray[i] + "'>" + movieArray[i] + "</a>";
	
	starSearchResultElement.append(rowHTML);
}

jQuery.ajax( {
	datatype: "json",
	method: "GET",
	url: "singleStar" + queryUrl,
	success: (resultData) => handleResult(resultData)
});