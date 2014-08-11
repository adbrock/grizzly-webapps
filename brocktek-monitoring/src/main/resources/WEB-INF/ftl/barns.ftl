<html>
	<head>
  		<title>Barn Monitor</title>
  		<link rel="stylesheet" type="text/css" href="static/styles/base.css">
  		<script src="static/javascript/jquery-2.1.1.min.js" type="text/javascript"></script>
  		<script>
  			jQuery(document).ready(function($) {
      			$(".clickableRow").click(function() {
            		window.document.location = $(this).attr("href");
      			});
			});
  		</script>
	</head>	
	<body>
		<div id="content">
			<table border="1" align="center" cellspacing="4" cellpadding="4">
				<thead>
					<caption><h1>Current Status</h1></caption>
					<col width="100">
  					<col width="200">
  					<col width="200">
  					<col width="200">
  					<tr>
						<#list headers as header>
    						<th>${header}</th>
						</#list>
  					</tr>
  				</thead>
  				<tbody>
					<#list barns as barn>
  						<tr class='clickableRow<#if (!barn.online)><#if (time - barn.timestamp < 14400000)> alertRow</#if></#if>' href="/barns/${barn.address64?c}">					
  							<td style="padding-left: 10px;">${barn.id}</td>
  							<td align="center" style="padding-right: 10px;padding-left: 10px;padding-top: 4px;padding-bottom: 4px;">${barn.online?string('ONLINE', 'OFFLINE')}</td>
  							<td align="center" style="padding-right: 10px;padding-left: 10px;padding-top: 4px;padding-bottom: 4px;">${barn.wetBulbTemp?string("000.0")}&deg;F</td>
  							<td align="center" style="padding-right: 10px;padding-left: 10px;padding-top: 4px;padding-bottom: 4px;">${barn.dryBulbTemp?string("000.0")}&deg;F</td>
  						</tr>	
					</#list>
				</tbody>
			</table>
		</div>
	</body>	
</html> 