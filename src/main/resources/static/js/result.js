function drawPieChart(inputData, parameter, elementId) {
    var data = new google.visualization.DataTable();
    data.addColumn('string', 'Hashtag');
    data.addColumn('number', 'Top per ' + parameter);

    data.addRows(inputData);
    data.sort({column: 1, desc: true});

    var options = {
        title: 'Top Hashtags by ' + parameter,
        'width':600,
        'height':400,
        is3D: true
    };

    var chart = new google.visualization.PieChart(document.getElementById(elementId));
    chart.draw(data, options);
}

function drawTable(inputData) {
    var data = new google.visualization.DataTable();
    data.addColumn('string', 'Hashtag');
    data.addColumn('number', 'Count');

    data.addRows(inputData);
    data.sort({column: 1, desc: true});

    var table = new google.visualization.Table(document.getElementById('hashTag_table'));
    table.draw(data, {showRowNumber: true, width: '100%', height: '100%', allowHtml: true});
}

function drawAllCharts(tweetPeriodDataMap, languageDataMap) {
    var inputDataPerPeriod = [];
    for(var item in tweetPeriodDataMap) {
        inputDataPerPeriod.push([item, tweetPeriodDataMap[item]]);
    }

    var inputDataByLanguage = [];
    for(var item in languageDataMap) {
        inputDataByLanguage.push([item, languageDataMap[item]]);
    }

    drawPieChart(inputDataPerPeriod, 'Period', 'piechart_3d');
    drawPieChart(inputDataByLanguage, 'Language', 'piechart2_3d');
    drawTable(inputDataPerPeriod);
    drawGeoChart();
}

function drawGeoChart() {
    var data = google.visualization.arrayToDataTable([
        ['Lat', 'Long', 'Name'],
        [37.4232, -122.0853, 'Work'],
        [37.4289, -122.1697, 'University'],
        [37.6153, -122.3900, 'Airport'],
        [37.4422, -122.1731, 'Shopping']
    ]);

    var map = new google.visualization.Map(document.getElementById('map_div'));
    map.draw(data, {
        showTooltip: true,
        showInfoWindow: true
    });
}