
function handleResult(resultData) {
//	console.log("Receive all genres from backend server");
	
	let rowHTML = "";
	for (let i = 0; i < resultData.length; ++i) {
		genre = resultData[i]["name"];
		rowHTML += "<a style='margin: 12px 10px;' href='movies.html?genre=" + genre + "'>" + genre + "</a>";
		if ((i+1) % 5 == 0) {
			rowHTML += "<br>";
		}
	}
	
//    console.log(rowHTML);
    // Append the row created to the table body, which will refresh the page
    $(".modal-body.all_genres").append(rowHTML);
	
}

jQuery.ajax( {
	datatype: "json",
	method: "GET",
	url: "api/index",
	success: (resultData) => handleResult(resultData)
});

// For test: to see what url is actually sent to Backend Java Servlet
console.log("api/index");