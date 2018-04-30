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

let cId = getParameterByName('cId') ? getParameterByName('cId'): "";
let cName = getParameterByName('cName') ? getParameterByName('cName'): "";
let cAmount = getParameterByName('cAmount') ? getParameterByName('cAmount'): "";
let amount = getParameterByName('amount') ? getParameterByName('amount'): "";
let queryUrl= "?cId=" + cId + "&cName=" + cName + "&cAmount=" + cAmount + "&amount=" + amount;

function handleResult(resultData) {
	let cartInfoElement = jQuery("#cart_info");
	let rowHTML = "";
	for (let i = 0; i < resultData.length; ++i)
	{
		var mId = resultData[i]["id"];
		var mName = resultData[i]["title"];
		var mQuantity = resultData[i]["quantity"];
		rowHTML += "<div class='container'>";
		rowHTML += "<form class='form-inline form-control' action='cart.html'" + ">";
		rowHTML += mId + " " + mName + " ";
		rowHTML += "<input type='number' placeholder=" + mQuantity + " name='amount'" + " value=" + mQuantity + " required>";
		rowHTML += "<input type='hidden' name='cId' value='" + mId + "'>";
		rowHTML += "<input type='hidden' name='cName' value='"+ mName + "'>";
		rowHTML += "<input type='hidden' name='cAmount' value=''>";
		rowHTML += "<button type='submit' class='btn btn-primary'>Submit</button>";
		
		
		rowHTML += "</form></div>";
	}
	cartInfoElement.append(rowHTML);
	
}



jQuery.ajax( {
	datatype: "json",
	method: "GET",
	url: "cart" + queryUrl,
	success: (resultData) => handleResult(resultData)
});