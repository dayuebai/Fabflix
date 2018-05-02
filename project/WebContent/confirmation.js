function handleResult(resultData) {
    let confirmationInfoElement = jQuery("#confirmation_info");
    let rowHTML = "<h2 style='text-align: center; margin-bottom: 30px;'><i>Congratulations! Your purchase has been processed</i></h2>";
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
}

jQuery.ajax( {
	datatype: "json",
	method: "GET",
	url: "api/confirmation",
	success: (resultData) => handleResult(resultData)
});
