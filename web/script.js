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

async function trade(uid, tid, isBuy, count=1) {
  postData('https://dota-stonks.herokuapp.com/trade', {'uid':uid, 'isBuy':isBuy, 'tid':tid, 'amount':count})
    .then((response) => {
      console.log(response);
      //getTeams(uid)
    })
}

//function to refresh our data
async function getPortfolio(uid) {
    console.log("fetching users/"+uid)
    fetch('https://dota-stonks.herokuapp.com/users/'+uid)
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
                const tid = data.mData.ownerships[i].tid
                var insert = document.createElement("li");
                var img = document.createElement("img");
                img.setAttribute("src", "dota_logo.png");
                insert.appendChild(img);

                var sellButton = document.createElement("button")
                sellButton.setAttribute("class", "sellButton")
                sellButton.innerText = "Sell 1"
                sellButton.onclick = function ()  { trade(uid, tid , false, 1) }

                const teaminfo = "Owned: " + data.mData.ownerships[i].count;
                var teamtext = document.createElement("p")
                teamtext.innerHTML = teaminfo;
                
                var teamname = document.createElement("h3");
                teamname.innerHTML = data.mData.ownerships[i].name +"     $ " + data.mData.ownerships[i].price;

                insert.appendChild(teamname);
                insert.appendChild(teamtext);
                insert.appendChild(sellButton)
                document.getElementById("portfolio").appendChild(insert)
            }
        });
}

async function getTeams(uid) {
    fetch('https://dota-stonks.herokuapp.com/teams')
        .then((response) => response.json())
        .then((data) => {
            console.log(data.mData)
            for (i = 0; i < data.mData.length; i++) {
                const tid = data.mData[i].tid;
                var insert = document.createElement("li");
                var img = document.createElement("img");
                img.setAttribute("src", "dota_logo.png");
                insert.appendChild(img);

                var buyButton = document.createElement("button")
                buyButton.setAttribute("class", "buyButton");
                buyButton.innerText = "Buy 1"
                buyButton.onclick = function ()  { trade(uid, tid , true, 1) }

                const teaminfo = JSON.stringify(data.mData[i].history);
                var teamtext = document.createElement("p")
                teamtext.innerHTML = teaminfo;
                
                var teamname = document.createElement("h3");
                teamname.innerHTML = data.mData[i].name +"     $ " + data.mData[i].price;

                insert.appendChild(teamname);
                insert.appendChild(teamtext);
                insert.appendChild(buyButton);
                document.getElementById("teamList").appendChild(insert)
            }
        })
}

async function getUsers() {
    fetch('https://dota-stonks.herokuapp.com/users')
        .then((response) => response.json())
        .then((data) => {
            console.log(data.mData)
            for (i = 0; i < data.mData.length; i++) {
                var insert = document.createElement("li");

                const name = data.mData[i].username; 
                var mark = document.createElement("mark");
                mark.innerHTML = name;

                const networth =  " $" + data.mData[i].networth;
                var small = document.createElement("small");
                small.innerHTML = networth;

                insert.appendChild(mark);
                insert.appendChild(small);
                document.getElementById("userList").appendChild(insert)
            }
        })
}



const username = prompt("Please enter username. New users will be registered");
const pass = prompt("Please enter password");
postData('https://dota-stonks.herokuapp.com/login', {'username':username, 'password':pass})
    .then((response) => {
      const uid = response.mData;
      getPortfolio(uid);
      getTeams(uid);
      getUsers();
    });


