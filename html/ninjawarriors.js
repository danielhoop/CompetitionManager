window.onload = get_categories();
var posCateg;
var nrCateg;
var storedhash=0;
var inputs;
get_initial_content();


setTimeout(function() { getcateg(); }, 1000);
posCateg = 0;

function getcateg()
{
storedhash = stringToHash(document.getElementById("content").innerHTML);
console.log("Hashkey:" + storedhash);
inputs = document.getElementsByName("category");
nrCateg = inputs.length;

console.log(nrCateg);
swap_tabs();
startInterval();

}
//swap_tabs();
//startInterval();



function startInterval() {
   // setInterval("startTime();",1000);
	setInterval("swap_tabs();",6000);
	setInterval("check_if_content_updated();",5000);
}



function get_categories() {
  var xhttp = new XMLHttpRequest();
   xhttp.open("GET", "category_content.txt", true);
   xhttp.send();
   xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
		
      document.getElementById("categories").innerHTML =
      this.responseText;
	  
    

	  
    }
  };
  xhttp.onload = function () {
	 
	  
  }
 
  xhttp.close;
  console.log("Category IDs retrieved:");
 
  

}


function swap_tabs()
{
	if (document.getElementById("autoon").checked == true)
	{
	
	
	if (posCateg >= nrCateg) {posCateg = 0}
	
	document.getElementById("tab" + inputs[posCateg].value).checked = true;
	console.log("PosCateg:" + posCateg + "NrCateg:" + nrCateg);
	posCateg++;
	}

}

function get_initial_content() {
	var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      document.getElementById("content").innerHTML =
      this.responseText;
	  
    }
  };
  xhttp.open("GET", "content.txt", true);
  xhttp.send();
  xhttp.close;
  console.log("Initial Content retrieved");

}

function stringToHash(string) { 
                  
                var hash = 0; 
                  
                if (string.length == 0) return hash; 
                  
                for (i = 0; i < string.length; i++) { 
                    char = string.charCodeAt(i); 
                    hash = ((hash << 5) - hash) + char; 
                    hash = hash & hash; 
                } 
                  
                return hash; 
            }
			


function check_if_content_updated()
{
	var content_response;
	var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
     content_response =  this.responseText;
	 console.log("Updated:" +  stringToHash(content_response) + " Old:" + storedhash);
	 if (storedhash !=  stringToHash(content_response)) 
  {
	  document.getElementById("content").innerHTML = content_response;
	  storedhash = stringToHash(content_response);
	  document.getElementById("tab" + inputs[PosCateg].value).checked = true;
	  console.log("Content updated" + content_response);
  }
  else 
  {
	  console.log("No changes");
  }
    }
  };
  xhttp.open("GET", "content.txt", true);
  xhttp.send();
  xhttp.close;
  
	  
}




