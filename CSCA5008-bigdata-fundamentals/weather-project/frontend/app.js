async function fetchWeatherData() {
    const response = await fetch('http://localhost:5000/weather');
    const data = await response.json();

    const weatherDataDiv = document.getElementById('weather-data');
    weatherDataDiv.innerHTML = '<ul>' + data.map(item => `<li>${item.datetime}: ${item.temperature}°C</li>`).join('') + '</ul>';
}

fetchWeatherData();
setInterval(fetchWeatherData, 300000); // Refresh every 5 minutes
