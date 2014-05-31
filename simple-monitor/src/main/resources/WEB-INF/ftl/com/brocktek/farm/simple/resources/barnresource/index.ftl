<html>
	<head>
  		<title>Barn Monitor</title>
  		<link rel="stylesheet" type="text/css" href="/simpleMonitor.css">
	</head>
	<body bgcolor="#E6E6FA">
		<table border="1" align="center" style="padding:50px" " cellspacing="4" cellpadding="4"> 
			<col width="100">
  			<col width="200">
  			<col width="200">
  			<col width="200">
  			<tr>
				<#list headers as header>
    				<th>${header}</th>
				</#list>
  			</tr>
			<#list barns as barn>
  				<tr <#if (time - barn.lastUpdate < 300000)> <#if (!barn.online)> bgcolor="#FF1414" </#if> </#if>> 						
  					<td style="padding-left: 10px;">${barn.id}</td>
  					<td align="center" style="padding-right: 10px;padding-left: 10px;padding-top: 4px;padding-bottom: 4px;">${barn.online?string('ONLINE', 'OFFLINE')}</td>
  					<td align="center" style="padding-right: 10px;padding-left: 10px;padding-top: 4px;padding-bottom: 4px;">${barn.wetBulbTemp?string("000.0")}&deg;F</td>
  					<td align="center" style="padding-right: 10px;padding-left: 10px;padding-top: 4px;padding-bottom: 4px;">${barn.dryBulbTemp?string("000.0")}&deg;F</td>			
			</#list>
		</table>
	</body>
</html> 