function drawCustomCharts(hashTags) {
    var inputData = [];
    for(var item in hashTags) {
        inputData.push([hashTags[item].value, hashTags[item].language]);
    }
    if(hashTags) {
        drawTable(inputData);
    }
}

function drawTable(inputData) {
    var data = new google.visualization.DataTable();
    data.addColumn('string', 'Hashtag');
    data.addColumn('string', 'Message');

    data.addRows(inputData);
    data.sort({column: 1, desc: true});

    var options =  {
        showRowNumber: true,
        width: '100%',
        height: '100%',
        allowHtml: true,
        page: 'enable',
        cssClassNames: {
            tableCell: 'small-font'
        }
    };

    var table = new google.visualization.Table(document.getElementById('custom_table'));
    table.draw(data, options);
}