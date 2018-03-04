function drawPieChart(inputData, parameter, elementId) {
    var data = new google.visualization.DataTable();
    data.addColumn('string', 'Hashtag');
    data.addColumn('number', 'Top per ' + parameter);

    data.addRows(inputData);

    var options = {
        title: 'Top Hashtags per ' + parameter,
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

    var table = new google.visualization.Table(document.getElementById('table_div'));

    table.draw(data, {showRowNumber: true, width: '100%', height: '100%'});
}



function drawAllCharts() {
    var inputData = [
        ['Work', 11],
        ['Eat', 2],
        ['Commute', 2],
        ['Watch TV', 2]
    ];

    drawPieChart(inputData, 'Day', 'piechart_3d');
    drawPieChart(inputData, 'Country', 'piechart2_3d');
    drawTable(inputData);
}