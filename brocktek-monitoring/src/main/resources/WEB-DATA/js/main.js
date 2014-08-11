const
minContentWidth = 1080;
const
minContentHeight = 720;

var barnTable;

$(document).ready(function() {
	$('#content').load('dashboard.html', initDashboard);
});

$(window).resize(function() {
	chart.setSize($('#chart').width(), $('#barnTable').height() + 50);
});

function initDashboard() {
	buildTable();
}

function handleHomeAction() {
	$('#content').load('dashboard.html', initDashboard);
}

function handleConfigAction() {
	$('#content').load('config.html');
}

function handleNetworkAction() {
	
}

function buildTable() {
	barnTable = $('#barnTable').dataTable({
		"lengthChange" : false,
		"searching" : false,
		"bSort" : false,
		"autoWidth" : false,
		"iDisplayLength" : 15,
		"ajax" : "/api/barns",
		"columns" : [ {
			"data" : "id"
		}, {
			"data" : "status"
		}, {
			"data" : "wetBulbTemp"
		}, {
			"data" : "dryBulbTemp"
		}, {
			"data" : "address"
		} ],
		"aoColumnDefs" : [ {
			"sClass" : "center",
			"aTargets" : [ 1 ]
		}, {
			"sClass" : "center",
			"aTargets" : [ 2 ]
		}, {
			"sClass" : "center",
			"aTargets" : [ 3 ]
		}, {
			"sClass" : "center",
			"aTargets" : [ 4 ],
			visible : false,
		} ],
		"fnInitComplete" : buildChart
	});

	$('#barnTable tbody').on('click', 'tr', function() {
		if ($(this).hasClass('selected')) {
			$(this).removeClass('selected');
		} else {
			barnTable.$('tr.selected').removeClass('selected');
			$(this).addClass('selected');
			chart.setTitle({
				text: "Barn " + barnTable.fnGetData(this, 0),
			});
			requestChartData();
		}
	});
}

function buildChart() {
	var ph = $('#barnChart').width();
	var ph = $('#barnChart').height();
	chart = new Highcharts.Chart({
		chart : {
			renderTo : 'barnChart',
			width : $('#barnChart').width(),
			height : $('#barnTable').height() + 50,
			defaultSeriesType : 'spline',
			zoomType : 'x',
			events : {
				load : requestChartData
			}
		},

		title : {
			text : 'Barn 1'
		},
		plotOptions : {
			series : {
				animation : 500,
				marker : {
					enabled : false
				}
			}
		},
		xAxis : {
			type : 'datetime',
			minRange : 3600000,
			maxRange : 7 * 24 * 3600000,
			maxZoom : 20 * 1000
		},
		yAxis : {
			min : 0,
			max : 250,
			minPadding : 0.2,
			maxPadding : 0.2,
			title : {
				text : 'Temperature (\u00B0F)',
				margin : 10
			}
		},
		series : [ {
			name : 'Dry Bulb',
			data : [],
		}, {
			name : 'Wet Bulb',
			data : [],
		} ],
		credits : false,
	});
}

function requestChartData() {
	var adr;
	if (barnTable.fnGetData($('tr.selected'), 4) != null) {
		adr = barnTable.fnGetData($('tr.selected'), 4);
	} else {
		adr = barnTable.fnGetData($('#barnTable tbody tr:first')[0], 4);
	}
	$.ajax({
		url : "/api/barns/history",
		type : "GET",
		dataType : "json",
		data : {
			address : adr,
			days : 7
		},
		success : function(json) {
			chart.series[0].setData(json.wetBulbSeries, false);
			chart.series[1].setData(json.dryBulbSeries, false);
			chart.redraw();
		},
		cache : false
	});
}
