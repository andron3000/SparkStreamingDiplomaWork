function drawGeoMap(countries) {
    var inputData = [];
    for(var item in countries) {
        inputData.push([item, countries[item]]);
    }

    var data = new google.visualization.DataTable();
    data.addColumn('string', 'Country');
    data.addColumn('number', 'Count');

    data.addRows(inputData);

    var options = {
        colorAxis: {colors: ['#00853f', 'black', '#e31b23']},
        backgroundColor: '#81d4fa'
    };

    var chart = new google.visualization.GeoChart(document.getElementById('regions_div'));

    chart.draw(data, options);
}