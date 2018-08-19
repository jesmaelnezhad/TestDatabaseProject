<%@page import="org.json.simple.JSONObject"%>
<%@page import="rm.basestations.Sensor"%>
<%@page import="rm.ResourceManager"%>
<%@page import="um.UserManager"%>
<%@page import="request_handlers.ResponseConstants.ResponseCode"%>
<%@page import="request_handlers.ResponseHelper"%>
<%@page import="rm.parking_structure.City"%>
<%@page import="um.User"%>
<%@page import="rm.basestations.SensorId"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<style type="text/css">
		@page { size: 11.69in 8.27in; margin: 0.79in }
		p { margin-bottom: 0.1in; line-height: 120% }
		td p { margin-bottom: 0in }
		a:link { so-language: zxx }
	</style>

<title>Search for Sensor Information</title>
</head>
<body lang="en-US" dir="ltr">

<table width="972" cellpadding="4" cellspacing="0">
	<col width="962">
	<tr>
		<td width="962" valign="top" style="border: 1px solid #000000; padding: 0.04in">
			<p align="center"><font size="5" style="font-size: 20pt">Search
			for a sensor using sensor ID:</font></p><br/>
			<center>
			<form action="./sensors.jsp" method="get">
				<input type="text" placeholder="Enter sensor id here." name="id" />
				<input type="submit" value="Search!"/>
			</form>
			</center><br/>

		</td>
	</tr>
</table>
<%
	String sensorIdString = request.getParameter("id");
if(sensorIdString != null){
	int sensorId = -1;
	try{
		sensorId = Integer.parseInt(sensorIdString);
		
	}catch(NumberFormatException e){
%>
		<table width="972" cellpadding="4" cellspacing="0">
			<col width="962">
			<tr>
				<td width="962" valign="top"
					style="border: 1px solid #000000; padding: 0.04in">
					<p align="center">
						<font size="5" style="font-size: 20pt;color:red">Sensor ID must be a number.</font>
					</p>
					<br />
				</td>
			</tr>
		</table>
		<%
			}
			
			ResourceManager rm = ResourceManager.getRM();
			User customer = UserManager.getCM().getUser(request);
			
			City city = null;
			if(customer != null) {
				city = customer.selected_city;
			}
			if(customer == null){
		%>
		<table width="972" cellpadding="4" cellspacing="0">
			<col width="962">
			<tr>
				<td width="962" valign="top"
					style="border: 1px solid #000000; padding: 0.04in">
					<p align="center">
						<font size="5" style="font-size: 20pt;color:red">Authentication required.</font>
					</p>
					<br />
				</td>
			</tr>
		</table>
		<%
	}else if(city == null){
			%>
		<table width="972" cellpadding="4" cellspacing="0">
			<col width="962">
			<tr>
				<td width="962" valign="top"
					style="border: 1px solid #000000; padding: 0.04in">
					<p align="center">
						<font size="5" style="font-size: 20pt;color:red">City of this request is not determined.</font>
					</p>
					<br />
				</td>
			</tr>
		</table>
		<%
	}else if(sensorId != -1){
		
		SensorId sensorIdObj = SensorId.toSensorId(sensorId);
		JSONObject sensorJSON = rm.readSensor(city, sensorId);
		boolean full = (Boolean)sensorJSON.get("full");
		
%>
<table width="972" cellpadding="4" cellspacing="0" style="page-break-before: auto">
	<col width="224">
	<col width="730">
	<tr valign="top">
		<td width="224" height="143" style="border-top: none; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: none; padding-top: 0in; padding-bottom: 0.04in; padding-left: 0.04in; padding-right: 0in">
			<p><img src="data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEJ7AnsAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wgARCADzAMgDASIAAhEBAxEB/8QAGwABAAIDAQEAAAAAAAAAAAAAAAMEAQIGBwX/xAAVAQEBAAAAAAAAAAAAAAAAAAAAAf/aAAwDAQACEAMQAAAB78ADSKuX8wzAAAAAGqCEvopQAABX3qCwsA0N2uwAAAaZM1bYoWoYy+03AGMwEG8dw3NDFRub2ddgAACli5TLe9G4ZpXoSG58+4SAUrdEltwzCnZpG12KYFQtqdwAKMxY03FCZCXwUZMxF4EdOzWLu+MkFeaMuZCCvJgnlA+X9Qr1/oUC5vXsEdO/RLckE5FUuUy+wI6tymX86blaOWAvgppa5fR0jk+98k9YN6FisWLEchj596kWpo5COnbqF1sMUfoUixLUtmlL6FMnlpXDNO4KfMdlx58Xq61c6KafYGpBCmLGQrRJC0BXsalK7T3Lem4oSS1i+rWT5XxI8Ga3w/uHcycx05rUzqLmNxFJSNble4AAQ1foVjaenMTabjVt8E+H3XKdIcR3nnvenDegcB25LnYGKwiWTfcAAAK0N/QxJjI4Hu+ANofRtTyT61L1M85+71Hnx6GCtDdwaTAAAAAAB8Cr8aA9C04775wXqHlXoB9jh9KZ6Ha5LrQAAAAAABHJzxzPo/KdWUea6+keUd/wfrxpYxueb+kefdqXAAAAAAAPOPQfMzs/rchSO/qct9A4L2Txf0w+xrxMR9LOPhnpwAAAAAAOZpR/dLsOxKHOdl8xfNO44f10q2skl4Pt/jL9u/zfSAAAAAAGIphXzOIPjdB8w8i9m899HNU4g2lAAAAAAAAAAAAAAAAAH//EACsQAAEDBAAGAgICAwEAAAAAAAEAAgMEBRAREhMhMDEyFCAkJRVAIjVBUP/aAAgBAQABBQL68Q7u9IEHtOfiPx25MNfvsPdhrO85msNdv7POgmNySAgd9guAW95e3S8IHf1J2mDeXO4cR9h3sDpA7GCNJh0cvPRAaCJ0PKa3fZI2iOFNOsvHRNOxh3VzB1w87LRs9ojYUZy7oYzg+FH4TjoJg0MOftM85PljsyBDpmQJvQp/qm+FJ4zIeia3X0e3DTsJ3qm+qk9cSeMyJvth/sz2xFX081RgqP6R+E/1Q8SeuZE32xJ7M9sUHI/nMFR/SPwn+qb4f6oeFJ9JB0TXcSrZ/j0cQfSyghwe7DRoJ3qmeqk9cHwmHondQmHphzdJntenukeXzzzWifnUCYzMhXnMiHnDhoxnrh40WnhP0ofy7vSf7ul/DvWSdLyoxlx2Yx1xIMA7CcOIJr9ZuE/x6KzywR0lI/8Ad3WoiFUx4exE6TncSA3l50E0aGSNJh1lzd4jOLw4z1EtgYVDQzTVk1jbDSWSfm0Sf7AbTRw5cdlg2fo9u8MdktBQACJ0Lefm3dUB/feVQH4V5RaCgNZe7eGjh+z2rwg8fS7T8m32RrY6PjVCf3vGr03l1UUgmhy5+8MbrsPYv+t8YvLzPWSWSphP8hcaMwVhgq/kXSuTrNO2GxT8yjxJhrNdrhGSQ0W0GsuyIBVtAN2xRfg3rBAKDQO9d5+TQWSIQ0XMCL3atx/Z8ZXMCvjOCoglE8H9C9SGas4LpQqC+6TLnSTMoHBtwmu1HEpb3JIZae5VMVin46bvucGtto+Zd1LSQTqpscPBBHzpobHTRqKCKFeVRH4N5794n5NBYoeXRriCqZfxqQ6q+YFxDF9i5dTTTCop+9fJ+ZV0dRAYeAlcsKs02ip+lTwgrlhcLgrvPBJSWGfih7r3iNlFD/I3GawLhu1Cob84Ke5UtRQxHU013pIVLfJpD8W6VyhsMbVRk0N27t6m5dFY2COnMxXMepYI5lV2yKOEdVFaYGqONsK5j0Jle4+Gro5vkUncvFLVVE0EPJg0tLSuPS3haWlpaVzpnT0lognp4O7wrhWlpXTpbU0f46Wlwrh/p10DqqkFkqy9jeFn/if/xAAUEQEAAAAAAAAAAAAAAAAAAABw/9oACAEDAQE/AVD/xAAUEQEAAAAAAAAAAAAAAAAAAABw/9oACAECAQE/AVD/xAA1EAABAwEEBggFBQEAAAAAAAABAAIDERASITEEICIwQHEjMkJRYYGh0RNBUmJyM1CRscHw/9oACAEBAAY/AtXPe47vDfDdUFmO+8N1U8adwd1T9rpqU4GhtrqBDgK6hsG5CGu6Bj9sah1jYNUIa810d9z/AHUOsbBaNzLJ8wMOabP9Ba4+aBCpqHgodFjF4u2qLSoxorrz2gFv00TQeszZsqbaagQ3s+k9lmy1aZyCm0fsS7TdauvXW8LZH/PIc0GCRvxHGpC0vl7LR54ngyMOICDhkceF0fQ2/M1KrDKR4OT9HDxebmVI/wCIXSNFRhQL4ZOMZp5b+h1alS6T2W5f5ZPzdZJo56rtn21sNxXWf3v2Qi6u081KyUh8XLEKHSGdb2TJBk4V1MN1hqxaK3/iVe0aWvoVSdhI+8f6jpF2ueC6MOa37cB/KfLJIC8CtBijGc2H04Ikp+knIVd7WYqOo+ZtfCeq7Z9uCf3v2Qr5zkNfKw0Cip9SxFkOks5eYTJBk4V4GPR29n+ysA4sHdtBUmgx72I3ZgDTJ2ChJOF5fqXz3MxV3R4AOeJTpJQ64BWhw9E6E5sOHLgC45DFOnIwFX+1nSQtd40TnRPcygrTNMjrS+aKshdIf4XRxtbyFhiPVJue3AOAzfsoyfN59LM1L+BUP5izOyOdvaHqFHKO0N+IhlGPVRxQyt2RSixNk/4FRfmFlZgUYzI34gNQAnwnNhqOW+c85AVTjJ1TVzlWGbyesLzmeG0FSeHzapgySjiw4FM/ILr3z9iu6PDTniV0ri1v3mnoumkLvBuC+G7Ktw765XGQ08k+U5vNByWAWa6RgdzT5Yy4XRWiAVXXn81SMXOSzWITZm9seoUcneMee9a6Nl6NopgUyPuGpNyQ1dgVe01ATmzCgrVo4KbkP7sHLh3wtIBPeqG4B31TR3D9l//EACoQAQACAQIDCAMBAQEAAAAAAAEAETEQIUFRoSAwYXGBkbHRweHwQPFQ/9oACAEBAAE/Ieyi00YU7neIFqphHuqtsueljf3l75INImyQtjs9xRzOOnH9neoOzMw/Sbm40ypTk7WGy6eYa5RqALMdxkmAFjZrYsx8QbCZhizsLRbLiy1bg66kObFVVbZe+TuC73GOrH9w7jRBKYyR/wCzZXD2KqOMzVZYQhodzFsrmZzsfMAKDHcAKY6px8xVZiCIJpfcyaVLx1dvhtLr8tbU4E2XhAoo7o6mJSjmZVqLThN55t9FSeWgq/PSxdN5cuu8DHzEhrHYSpbv4lvM13AeUVhOEzpvL0i0Sp6CiaOgc4bp4zGlNDjM4lS3PYyH10oXQ3pO9ZxDcny6GNMxN+tvkWfDfWwp4ez5aZgpT0mI0cVMT5ewumnyaGNM4262VjrWuFfYf46q1ZxtHDMzL59hdFBb03todhhsjy3mdLLHCblJswB4ypWqPwQY3flT7m0YqxLJUIz8aVJx0daQo9hNiC2aXV5aC0M6WHw0zLlmPiFSmCIZVYueX5jAt6Pxe2IyLdv6Y6RsW8yyuBrigLAZmNHaPWGy1ueTKK89fJWbrwmca7GKm49/rPjf1jv+7E3lt9p9npKOIXqAtiqVzN1Wti8JuXlrYU4Q2SskoHS0IiKOzMR/SZ0p9x9d/XHQGraT79IwqFdG8bgWUvbzxzPWJPY00AW4iLwIyA/5AAAxpjculTedUsRlhJepdnprnGY2WOyRrTho9Np/D9svEvIvrAObrlprrygBrkB7G71mXH1D8miVjgiKiCKM6KG7FV4SlXB2aFmTS3mfOuZJhICbAS97pz8dui9MtlHrEBSWMTKFHz0baZA3gCjXAW0C0AtZSo7W7SUZRlC7/PY2q11rPS4dA1QcHD79ZZoHHQZY7asGXDDB8HcTHCxribwwmZlOe4s3gF2Tn7675tV/Hl8yh1Hj1oDmfqej9w7ltrPP6js1vJ1m/WZ748n12/MsF+c/d6p2OE3dgtZmue63VrebGjFUBbMjr7m3s0AQCMDAR20OTMRBKcS6KmfNx+muwJMAb99RBrr2ekQ1nsTYiWC4hKKIg+SvhlcDi2RVW7kcNwmIZ/4W/wBuyva6fMC14LA/Ne0tAqZ4Ho/cBUC4yEIN1Xh6y8NhwPliXcDsO49MfM4AMr7Ob0l7MrzP3f8AgRygtEuZPI/TS6uubd75gbbVmz76zY9Vvyv5lJ4nbPvrBKJ8iIBEsYr5F+a/+P8ABeKnp+YKZWz8n7uYieRAKB/RLAcPlhxBILgaLsZoWGwQX+vf3OMjzb/EQpB3KX0fHTAGd4EgCvjiscvkjlCKYUnhGIpgUt/qlrj3A/ffNTV5DCuJNf28HdQ8j8/qcSNzdLJ0im/ebXR+5WoCba7f3GAlQBPLeXwUuAvriU8Wx+EfuZiXwHs+pXK/kPuXsq3xhx1p77b9PsH16z4fwP3OBKivF6T3exv9x/ruNI/mC1lW1KdVzwHT7gFMeELgPN5xcezB2Ae3+iDd2/ade926ssl8dvaVNQmPi/8AexxY/u82K5MNh2OgHIAZ/iH9beLd896glMa4mzJPLPLD5oOjSx/BPLPLoCZ/xVKQQ04RoAuPY9PxLB3QNK/8P//aAAwDAQACAAMAAAAQ88kc88888I0888ssc88880YA88sEcE888808c8880UM0Q4Q0Yk08c8I8gc0Qcw8EAU8o04wwEcUcUwYcYwIQAI4g4s88EkYQMhcA00k888QkM00ccsEc888ocgwVt0gc888888Ng8wU88888884gYJQc8888888s80J4d0888888kTCVTEU888888cgA1A8c8888888888888888/8QAGREBAQADAQAAAAAAAAAAAAAAATAAEUBw/9oACAEDAQE/EPCGrVzVGryf/8QAFBEBAAAAAAAAAAAAAAAAAAAAcP/aAAgBAgEBPxBQ/8QAKhAAAQIDBwQCAwEAAAAAAAAAAQARECFBMVFhcYGRoSAwsfDB0UDh8VD/2gAIAQEAAT8Q6W3EFVMe5LcKq3a5egDMpdwMkWcoL+jPY58Q+D7oDAsAphr9Y9WyDCHjg6NDruw8adUwPVpvOF3SJx70PDZKITQDJOxjgktVwJvgF0KZFotFMvjo3yZIBQ10s4bYBfG0sgPYD3cFomFNVwXrSYhncaQ0dGO3Bb/RyNAuXih0yIPXtgN0DTnQgVs6WTTIYUnhmpDL2UM/jSBgVXxDEdOkL/UGO/RX2iBg0KmpoZ0lDI2GKBBnjICDcpkgrfzh0eMIbeGGkHhpBoByhJnJF0QWlQ6iJyMOihPAUEOBBaCq0CEztQ9Y8Ac8CIUuinEKw6BwkeoiGhC2Jo3oz7uAmxnYgVna6HhEC2kSodEnCLKk4HlsNSsix0gMG7TJApRzmFOUUuKXIqSAZq2TP0OmyTGFQZhaqxg19GGgGhySBcoAzksoYZyNC70cQna2SBCbPSmIkKWEPwiHAeqKUjD6QGJ0ImEs9d9gDRBqwtigj50q+0QEQrO2OPhx0PbmBcwIHIgQgKAjsgn6XOBR6okh2IjBseYMh5gaPu4wmm1Qjs6WXTR0+FGsMeYcAlYjRCwDB+oDAbPsTocvIsAncA1KY0LvcQyx9Z6XEiBh85bwQ4jubBAtVoxTDQwIa+D02Pf28EffXQZKCwVPlBlMSFHLHyCLpbNmxiNimC/20EAcSFA0lgTItEnTFA8zdP0UhhJAhu1TDfADuLRYCxJB6f7cQ4sMXlAKzWakpIkk0AVphSNA+AKH8wz9U24AxBqFKReTd9LYc6AmWtHf5jC0Ad2PV5cLkPmllqD0KMFfoCkOgBI5AbqoceQK7SBsVwIKyUIXmUCNkW0XmBmDxJTdsb4fX27HmwhJjJpExpNsgX4A2YlA5gCP4cqWR5rFhaIw1+aAHElFoOvA5GQGZQ5BK4lARgagiZZPmgEwXrpw7R1qMgIL5JjGgEyrAnL0Mho8QtWgAhwVMDdgLYSQDEV0jEImLHvQZvjVEbNLnQe9P0QYM2+gd06CYL/6B1VoHBT0QNjqmkOqh9FaAElEAqgc2PCopnFCRMaH8GS3AybSj4EovKBDZWEUNgUxQNNurixkdEwWQgwCdqeq8H9E4xjCcgCHEREWQA74B0pRMOZ6X/gOImIaABynQTLzcZBu4gJwAA2kwGp2B2oiqsKHmM7IcuQHwhF4YGyOZqhtAGINQioj70E5+q/gSEGyzMthyrcIzQCCU8aJrGYJzq9kuK8IJrzolCwJAXjg7EbKWGBi6obv37FN9AWzEJknZKAmbRB9neED5Qb617aWQ0feAj82MpoFeSJESsmKslg9zby7z4Yj4AOqqSyTsD5hssMweNoQ8WEcyF2wF41QYKmt8bIodEIkCE3ATg92H0xToiTEJJwIMIZQC9GgueZcK1KdNVN3hZ4dtWT+CKSNQJ/sI7KmV4l18TGXssFUnMVrALipaDlcrIXAjCNiB8aE83RKUCjlmMh8FLCfKEuA91sq4QMMuR9JKyQOavl0DI34o8NCtuHQJ6VfFBA1cITdzaBlprLBz39inpCrIZ8vEaC/GPwWgtQfjYIM2yV+0q5ADktEEm9gyb/Ef//Z" name="Image3" align="left" width="99" height="120" border="0"/>
<br/>

			</p>
		</td>
		<td width="730" style="border-top: none; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000; padding-top: 0in; padding-bottom: 0.04in; padding-left: 0.04in; padding-right: 0.04in">
			<p><font size="6" style="font-size: 24pt"><b>Base station info.</b></font></p>
			<p><br/>

			</p>
			<table width="732" cellpadding="4" cellspacing="0">
				<col width="117">
				<col width="599">
				<tr valign="top">
					<td width="117" style="border: none; padding: 0in">
						<p align="right"><font size="5" style="font-size: 18pt">ID</font></p>
					</td>
					<td width="599" style="border: none; padding: 0in">
						<p style="margin-left: 0.16in"><font size="5" style="font-size: 18pt"><%=sensorIdObj.basestationIndex %></font></p>
					</td>
				</tr>
			</table>
			<p style="margin-bottom: 0in; line-height: 100%"><br/>

			</p>
		</td>
	</tr>
	<tr valign="top">
		<td width="224" height="99" style="border-top: none; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: none; padding-top: 0in; padding-bottom: 0.04in; padding-left: 0.04in; padding-right: 0in">
			<p><img src="data:image/jpeg;base64,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" name="Image1" align="left" width="135" height="90" border="0"/>
<br/>

			</p>
		</td>
		<td width="730" style="border-top: none; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000; padding-top: 0in; padding-bottom: 0.04in; padding-left: 0.04in; padding-right: 0.04in">
			<p><font size="6" style="font-size: 24pt"><b>Parkometer info.</b></font></p>
			<p><br/>

			</p>
			<table width="732" cellpadding="4" cellspacing="0">
				<col width="117">
				<col width="599">
				<tr valign="top">
					<td width="117" style="border: none; padding: 0in">
						<p align="right"><font size="5" style="font-size: 18pt">ID</font></p>
					</td>
					<td width="599" style="border: none; padding: 0in">
						<p style="margin-left: 0.16in"><font size="5" style="font-size: 18pt"><%=sensorIdObj.parkometerIndex %></font></p>
					</td>
				</tr>
			</table>
			<p style="margin-bottom: 0in; line-height: 100%"><br/>

			</p>
		</td>
	</tr>
	<tr valign="top">
		<td width="224" height="131" style="border-top: none; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: none; padding-top: 0in; padding-bottom: 0.04in; padding-left: 0.04in; padding-right: 0in">
			<p><img src="data:image/jpeg;base64,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" name="Image2" align="left" width="223" height="125" border="0"/>
<br/>

			</p>
		</td>
		<td width="730" style="border-top: none; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000; padding-top: 0in; padding-bottom: 0.04in; padding-left: 0.04in; padding-right: 0.04in">
			<p><font size="6" style="font-size: 24pt"><b>Sensor info.</b></font></p>
			<p><br/>

			</p>
			<table width="732" cellpadding="4" cellspacing="0">
				<col width="136">
				<col width="208">
				<col width="364">
				<tr valign="top">
					<td rowspan="4" width="136" style="border: none; padding: 0in">
						<p><img src="./images/<%=full?"red_circle.jpg":"green_circle.png" %>" name="Image4" align="left" width="128" height="128" border="0"/>
<br/>

						</p>
					</td>
					<td width="208" style="border: none; padding: 0in">
						<p align="right"><font size="5" style="font-size: 18pt">ID</font></p>
					</td>
					<td width="364" style="border: none; padding: 0in">
						<p style="margin-left: 0.16in"><font size="5" style="font-size: 18pt"><%=sensorId %></font></p>
					</td>
				</tr>
				<tr valign="top">
					<td width="208" style="border: none; padding: 0in">
						<p align="right"><font size="5" style="font-size: 18pt">Last
						time changed</font></p>
					</td>
					<td width="364" style="border: none; padding: 0in">
						<p style="margin-left: 0.16in"><font size="5" style="font-size: 18pt"><%=sensorJSON.get("lastTimeUpdated") %></font></p>
					</td>
				</tr>
				<tr valign="top">
					<td width="208" style="border: none; padding: 0in">
						<p align="right"><font size="5" style="font-size: 18pt">Last
						time updated</font></p>
					</td>
					<td width="364" style="border: none; padding: 0in">
						<p style="margin-left: 0.16in"><font size="5" style="font-size: 18pt"><%=sensorJSON.get("lastTimeUpdated") %></font></p>
					</td>
				</tr>
				<tr valign="top">
					<td width="208" style="border: none; padding: 0in">
						<p align="right"><font size="5" style="font-size: 18pt">Parkometer
						Index</font></p>
					</td>
					<td width="364" style="border: none; padding: 0in">
						<p style="margin-left: 0.16in"><font size="5" style="font-size: 18pt"><%=sensorIdObj.sensorIndex %></font></p>
					</td>
				</tr>
			</table>
			<p><br/>

			</p>
		</td>
	</tr>
</table>
<%

	}
}

%>
</body>
</html>