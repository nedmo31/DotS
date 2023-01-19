// Example POST method implementation:
async function postData(url = '', data = {}) {
    // Default options are marked with *
    const response = await fetch(url, {
      method: 'POST', // *GET, POST, PUT, DELETE, etc.
      mode: 'cors', // no-cors, *cors, same-origin
      cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
      credentials: 'same-origin', // include, *same-origin, omit
      headers: {
        'Content-Type': 'application/json'
        // 'Content-Type': 'application/x-www-form-urlencoded',
      },
      redirect: 'follow', // manual, *follow, error
      referrerPolicy: 'no-referrer', // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
      body: JSON.stringify(data) // body data type must match "Content-Type" header
    });
    return response.json(); // parses JSON response into native JavaScript objects
  }

function openTab(evt, tabName) {
    // Declare all variables
    var i, tabcontent, tablinks;
  
    // Get all elements with class="tabcontent" and hide them
    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
      tabcontent[i].style.display = "none";
    }
  
    // Get all elements with class="tablinks" and remove the class "active"
    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
      tablinks[i].className = tablinks[i].className.replace(" active", "");
    }
  
    // Show the current tab, and add an "active" class to the button that opened the tab
    document.getElementById(tabName).style.display = "block";
    evt.currentTarget.className += " active";
  }

//function to refresh our data
async function getPortfolio(uid) {
    console.log("fetching users/"+uid)
    fetch('/users/'+uid)
        .then((response) => response.json())
        .then((data) => {  
            console.log(data)   
            const userInfo = data.mData.username + "\t\t\t\t\t$ " + data.mData.money
            document.getElementById("portHeader").innerHTML = userInfo  
            const owned = data.mData.ownerships.length    
            if (owned == 0) {
                document.getElementById("portfolio").innerHTML = "Nothing currently owned"
            }                
            for (i = 0; i < owned; i++) {
                var insert = document.createElement("li");

                var sellButton = document.createElement("button")
                sellButton.innerText = "Sell 1"
                sellButton.style.fontSize = 'medium'

                const stockInfo = data.mData.ownerships[i].tid + "\t\t\t Owned: " + data.mData.ownerships[i].count;
                var stock = document.createTextNode(stockInfo);
                insert.appendChild(stock)
                insert.appendChild(sellButton)
                document.getElementById("portfolio").appendChild(insert)
            }
        });
}

async function getTeams() {
    fetch('/teams')
        .then((response) => response.json())
        .then((data) => {
            console.log(data.mData)
            for (i = 0; i < data.mData.length; i++) {
                var insert = document.createElement("li");

                var buyButton = document.createElement("button")
                buyButton.innerText = "Buy 1"
                buyButton.style.fontSize = 'medium'

                const teaminfo = '['+data.mData[i].tid+'] '+data.mData[i].name + "      $ " + data.mData[i].price;
                var teamname = document.createTextNode(teaminfo);
                insert.appendChild(teamname)
                insert.appendChild(buyButton)
                document.getElementById("teamList").appendChild(insert)
            }
        })
}

async function getUsers() {
    fetch('/users')
        .then((response) => response.json())
        .then((data) => {
            console.log(data.mData)
            for (i = 0; i < data.mData.length; i++) {
                var insert = document.createElement("li");
                const teaminfo = data.mData[i].username + "\t\t\t$ " + data.mData[i].networth;
                var teamname = document.createTextNode(teaminfo);
                insert.appendChild(teamname)
                document.getElementById("userList").appendChild(insert)
            }
        })
}

getTeams();
getUsers();

const username = prompt("Please enter username. New users will be registered");
const pass = prompt("Please enter password");
postData('/login', {'username':username, 'password':pass})
    .then((response) => getPortfolio(response.mData));


