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

// Function Executes on click of login button.
function validate(){
  var username = document.getElementById("username").value;
  var password = document.getElementById("password").value;
  postData('https://dota-stonks.herokuapp.com/login', {'username':username, 'password':password})
    .then((response) => {
      const uid = response.mData;
      if (uid > 0) {
        getPortfolio(uid);
        getTeams(uid);
        getUsers();
        document.getElementById("tabs").style.display = "block"
        document.getElementById("login").style.display = "none"
      }
      else {
        alert("Error on login");
      }
    });
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
      const res = response.mData
      getPortfolio(uid, false)
      //UPDATE THINGS?
      if (res <= 0) {
        window.alert("Nothing happened :(")
        return
      }
      if (isBuy) {
        window.alert(res+" now owned!")
      } 
      else {
        window.alert("+ $"+res)
      }
    })
}

//function to refresh our data
async function getPortfolio(uid, show=true) {
    console.log("fetching users/"+uid)
    fetch('https://dota-stonks.herokuapp.com/users/'+uid)
        .then((response) => response.json())
        .then((data) => {  
            console.log(data)   

            document.getElementById("portfolio").replaceChildren()

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
                img.setAttribute("src", tid+".png");
                insert.appendChild(img);

                var sellButton = document.createElement("button")
                sellButton.innerText = "Sell 1"
                sellButton.onclick = function ()  { trade(uid, tid , false, 1) }
                var sell5Button = document.createElement("button")
                sell5Button.innerText = "Sell 5"
                sell5Button.onclick = function ()  { trade(uid, tid , false, 5) }

                const teaminfo = "Owned: " + data.mData.ownerships[i].count;
                var teamtext = document.createElement("p")
                teamtext.innerHTML = teaminfo;
                
                var teamname = document.createElement("h3");
                teamname.innerHTML = data.mData.ownerships[i].name +"     $ " + data.mData.ownerships[i].price;

                insert.appendChild(teamname);
                insert.appendChild(teamtext);
                insert.appendChild(sellButton)
                insert.appendChild(sell5Button)
                document.getElementById("portfolio").appendChild(insert)
            }
            if (show) {
              document.getElementById("Home").style.display = "block"
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
                img.setAttribute("src", tid+".png");
                insert.appendChild(img);

                var buyButton = document.createElement("button")
                buyButton.innerText = "Buy 1"
                buyButton.onclick = function ()  { trade(uid, tid , true, 1) }
                var buy5Button = document.createElement("button")
                buy5Button.innerText = "Buy 5"
                buy5Button.onclick = function ()  { trade(uid, tid , true, 5) }
                
                var teamname = document.createElement("h3");
                teamname.innerHTML = data.mData[i].name +"     $ " + data.mData[i].price;

                for (j = 0; j < data.mData[i].history.length; j++) {
                  data.mData[i].history[j].index = j;
                }

                var svg = LineChart(data.mData[i].history, {
                  x: d => d.index,
                  y: d => d.price,
                  yLabel: "Price ($)",
                  height: 200,
                  color: "steelblue",
                  strokeWidth: 4,
                  xType: d3.scaleLinear
                })
                svg.style.float="right";

                insert.appendChild(teamname);
                insert.appendChild(svg)
                insert.appendChild(buyButton);
                insert.appendChild(buy5Button);
                document.getElementById("teamList").appendChild(insert)
            }
        })
}

async function getUsers() {
    fetch('https://dota-stonks.herokuapp.com/users')
        .then((response) => response.json())
        .then((data) => {
            console.log(data.mData)
                const userlist = data.mData

                const bc = BarChart(userlist, {
                  y: d => d.username,
                  x: d => d.networth,
                  yDomain: d3.groupSort(userlist, ([d]) => -d.networth, d => d.username), // sort by networth?
                  xLabel: "Net Worth →",
                  width: 600,
                  color: "steelblue",
                  marginLeft: 100
                })

                document.getElementById("leaderboard").appendChild(bc)
        })
}

// Copyright 2021 Observable, Inc.
// Released under the ISC license.
// https://observablehq.com/@d3/line-chart
function LineChart(data, {
  x = ([x]) => x, // given d in data, returns the (temporal) x-value
  y = ([, y]) => y, // given d in data, returns the (quantitative) y-value
  defined, // for gaps in data
  curve = d3.curveLinear, // method of interpolation between points
  marginTop = 20, // top margin, in pixels
  marginRight = 30, // right margin, in pixels
  marginBottom = 30, // bottom margin, in pixels
  marginLeft = 40, // left margin, in pixels
  width = 640, // outer width, in pixels
  height = 400, // outer height, in pixels
  xType = d3.scaleUtc, // the x-scale type
  xDomain, // [xmin, xmax]
  xRange = [marginLeft, width - marginRight], // [left, right]
  yType = d3.scaleLinear, // the y-scale type
  yDomain, // [ymin, ymax]
  yRange = [height - marginBottom, marginTop], // [bottom, top]
  yFormat, // a format specifier string for the y-axis
  yLabel, // a label for the y-axis
  color = "currentColor", // stroke color of line
  strokeLinecap = "round", // stroke line cap of the line
  strokeLinejoin = "round", // stroke line join of the line
  strokeWidth = 1.5, // stroke width of line, in pixels
  strokeOpacity = 1, // stroke opacity of line
} = {}) {
  // Compute values.
  const X = d3.map(data, x);
  const Y = d3.map(data, y);
  const I = d3.range(X.length);
  if (defined === undefined) defined = (d, i) => !isNaN(X[i]) && !isNaN(Y[i]);
  const D = d3.map(data, defined);

  // Compute default domains.
  if (xDomain === undefined) xDomain = d3.extent(X);
  if (yDomain === undefined) yDomain = [0, d3.max(Y)];

  // Construct scales and axes.
  const xScale = xType(xDomain, xRange);
  const yScale = yType(yDomain, yRange);
  const xAxis = d3.axisBottom(xScale).ticks(width / 80).tickSizeOuter(0);
  const yAxis = d3.axisLeft(yScale).ticks(height / 40, yFormat);

  // Construct a line generator.
  const line = d3.line()
      .defined(i => D[i])
      .curve(curve)
      .x(i => xScale(X[i]))
      .y(i => yScale(Y[i]));

  const svg = d3.create("svg")
      .attr("width", width)
      .attr("height", height)
      .attr("viewBox", [0, 0, width, height])
      .attr("style", "max-width: 100%; height: auto; height: intrinsic;");

  svg.append("g")
      .attr("transform", `translate(0,${height - marginBottom})`)
      .call(xAxis);

  svg.append("g")
      .attr("transform", `translate(${marginLeft},0)`)
      .call(yAxis)
      .call(g => g.select(".domain").remove())
      .call(g => g.selectAll(".tick line").clone()
          .attr("x2", width - marginLeft - marginRight)
          .attr("stroke-opacity", 0.1))
      .call(g => g.append("text")
          .attr("x", -marginLeft)
          .attr("y", 10)
          .attr("fill", "currentColor")
          .attr("text-anchor", "start")
          .text(yLabel));

  svg.append("path")
      .attr("fill", "none")
      .attr("stroke", color)
      .attr("stroke-width", strokeWidth)
      .attr("stroke-linecap", strokeLinecap)
      .attr("stroke-linejoin", strokeLinejoin)
      .attr("stroke-opacity", strokeOpacity)
      .attr("d", line(I));

  return svg.node();
}

// Copyright 2021 Observable, Inc.
// Released under the ISC license.
// https://observablehq.com/@d3/horizontal-bar-chart
function BarChart(data, {
  x = d => d, // given d in data, returns the (quantitative) x-value
  y = (d, i) => i, // given d in data, returns the (ordinal) y-value
  title, // given d in data, returns the title text
  marginTop = 30, // the top margin, in pixels
  marginRight = 0, // the right margin, in pixels
  marginBottom = 10, // the bottom margin, in pixels
  marginLeft = 30, // the left margin, in pixels
  width = 640, // the outer width of the chart, in pixels
  height, // outer height, in pixels
  xType = d3.scaleLinear, // type of x-scale
  xDomain, // [xmin, xmax]
  xRange = [marginLeft, width - marginRight], // [left, right]
  xFormat, // a format specifier string for the x-axis
  xLabel, // a label for the x-axis
  yPadding = 0.1, // amount of y-range to reserve to separate bars
  yDomain, // an array of (ordinal) y-values
  yRange, // [top, bottom]
  color = "currentColor", // bar fill color
  titleColor = "white", // title fill color when atop bar
  titleAltColor = "currentColor", // title fill color when atop background
} = {}) {
  // Compute values.
  const X = d3.map(data, x);
  const Y = d3.map(data, y);

  // Compute default domains, and unique the y-domain.
  if (xDomain === undefined) xDomain = [0, d3.max(X)];
  if (yDomain === undefined) yDomain = Y;
  yDomain = new d3.InternSet(yDomain);

  // Omit any data not present in the y-domain.
  const I = d3.range(X.length).filter(i => yDomain.has(Y[i]));

  // Compute the default height.
  if (height === undefined) height = Math.ceil((yDomain.size + yPadding) * 25) + marginTop + marginBottom;
  if (yRange === undefined) yRange = [marginTop, height - marginBottom];

  // Construct scales and axes.
  const xScale = xType(xDomain, xRange);
  const yScale = d3.scaleBand(yDomain, yRange).padding(yPadding);
  const xAxis = d3.axisTop(xScale).ticks(width / 80, xFormat);
  const yAxis = d3.axisLeft(yScale).tickSizeOuter(0);

  // Compute titles.
  if (title === undefined) {
    const formatValue = xScale.tickFormat(100, xFormat);
    title = i => `${formatValue(X[i])}`;
  } else {
    const O = d3.map(data, d => d);
    const T = title;
    title = i => T(O[i], i, data);
  }

  const svg = d3.create("svg")
      .attr("width", width)
      .attr("height", height)
      .attr("viewBox", [0, 0, width, height])
      .attr("style", "max-width: 100%; height: auto; height: intrinsic;");

  svg.append("g")
      .attr("transform", `translate(0,${marginTop})`)
      .call(xAxis)
      .call(g => g.select(".domain").remove())
      .call(g => g.selectAll(".tick line").clone()
          .attr("y2", height - marginTop - marginBottom)
          .attr("stroke-opacity", 0.1))
      .call(g => g.append("text")
          .attr("x", width - marginRight)
          .attr("y", -22)
          .attr("fill", "currentColor")
          .attr("text-anchor", "end")
          .text(xLabel));

  svg.append("g")
      .attr("fill", color)
    .selectAll("rect")
    .data(I)
    .join("rect")
      .attr("x", xScale(0))
      .attr("y", i => yScale(Y[i]))
      .attr("width", i => xScale(X[i]) - xScale(0))
      .attr("height", yScale.bandwidth());

  svg.append("g")
      .attr("fill", titleColor)
      .attr("text-anchor", "end")
      .attr("font-family", "sans-serif")
      .attr("font-size", 15)
    .selectAll("text")
    .data(I)
    .join("text")
      .attr("x", i => xScale(X[i]))
      .attr("y", i => yScale(Y[i]) + yScale.bandwidth() / 2)
      .attr("dy", "0.35em")
      .attr("dx", -4)
      .text(title)
      .call(text => text.filter(i => xScale(X[i]) - xScale(0) < 20) // short bars
          .attr("dx", +4)
          .attr("fill", titleAltColor)
          .attr("text-anchor", "start"));

  svg.append("g")
      .attr("transform", `translate(${marginLeft},0)`)
      .call(yAxis);

  return svg.node();
}