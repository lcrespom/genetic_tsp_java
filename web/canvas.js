$(function() {
	var cities = [];
	
	//------------------------------ Drawing ------------------------------

	function setupContext() {
		var canvas = document.getElementById('canvas');
		if (!canvas|| !canvas.getContext) return null;
		var ctx = canvas.getContext('2d');
		ctx.clearRect(0, 0, canvas.width, canvas.height);
		var w = canvas.width;
		canvas.width = 1;
		canvas.width = w;
		return ctx;
	}

	function drawCities(ctx) {
		for (var i = 0; i < cities.length; i++)
			ctx.fillRect(cities[i].x - 4, cities[i].y - 4, 8, 8);
	}
	
	function drawPath(ctx, path) {
		var city;
		for (var i = 0; i < path.length; i++) {
			city = cities[path[i]];
			if (i == 0) ctx.moveTo(city.x, city.y);
			else ctx.lineTo(city.x, city.y);
		}
		city = cities[path[0]];
		ctx.lineTo(city.x, city.y);
		ctx.stroke();
	}

	function drawSolution(path) {
		if (cities.length == 0) return;
		var ctx = setupContext();
		if (!ctx) return;
		ctx.fillStyle = '#00f';
		ctx.strokeStyle = '#000';
		ctx.lineWidth = 2;
		drawPath(ctx, path);
		drawCities(ctx);
	}
	

	//------------------------------ Statistics ------------------------------

	function formatNum(nStr) {
		//TODO: check browser locale and decide whether to use ',' or '.'
		nStr += '';
		var rgx = /(\d+)(\d{3})/;
		while (rgx.test(nStr))
			nStr = nStr.replace(rgx, '$1' + '.' + '$2');
		return nStr;
	}
	
	function prepend0(num) {
		if (num < 10) return '0' + num;
		return num;
	}
	
	function formatTime(t) {
		var h, m, s;
		s = Math.round(t/1000);
		m = Math.floor(s/60);
		h = Math.floor(m/60);
		return '' + h + ':' + prepend0(m % 60) + ':' + prepend0(s % 60);
	}

	function updateStatistics(status) {
		$('#status\\.generation').text(formatNum(status.generation));
		$('#status\\.gpm').text(formatNum(status.gpm));
		$('#status\\.eval').text(formatNum(Math.round(status.eval)));
		$('#status\\.lastIncumbentGen').text(formatNum(status.lastIncumbentGen));
		$('#status\\.elapsed').text(formatTime(status.elapsed));
		$('#status\\.lastIncumbentWhen').text(formatTime(status.lastIncumbentWhen));
	}

	
	//------------------------------ AJAX & event handling ------------------------------

	function getStatus() {
		$.getJSON('/status', function(status) {
			updateStatistics(status);
			drawSolution(status.cities);
			setTimeout(getStatus, 1000);
		});
	}
	
	function getCities() {
		$.getJSON('/cities', function(data) {
			cities = [];
			for (var i = 0; i < data.cityX.length; i++)
				cities.push({x: data.cityX[i], y: data.cityY[i]});
			getStatus();
		});
	}
	
	function setupEventHandlers() {
		$('#start').click(function() {
			getCities();
			$('#start').hide();
		});
	}

	setupEventHandlers();

});
