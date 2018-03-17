function realTimeData() {
    drawRealTimeChart();
    setInterval(drawRealTimeChart, 2000);
}

function drawRealTimeChart() {
    $.ajax({
        url: 'http://localhost:8085/realTimeData',
        type: 'get',
        success: function (response) {
            var data = new google.visualization.DataTable();
            var inputData = [];
            for(var indx in response) {
                inputData.push([response[indx].value, response[indx].language]);
            }

            if(inputData.length > 0) {
                data.addColumn('string', 'Hashtag');
                data.addColumn('string', 'Language');

                data.addRows(inputData);
                data.sort({column: 1, desc: true});

                var table = new google.visualization.Table(document.getElementById('real_time_div'));
                table.draw(data, {showRowNumber: true, width: '100%', height: '100%', allowHtml: true});
            }
        }
    });
}