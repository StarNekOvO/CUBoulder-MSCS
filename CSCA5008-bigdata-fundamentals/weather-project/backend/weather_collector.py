import requests
import pika
import json
import random
from time import sleep


def get_temperature():
    try:
        response = requests.get("https://api.weatherapi.com/v1/current.json?key=YOUR_API_KEY&q=London")
        response.raise_for_status()  # Raise an exception for HTTP errors
        return response.json()["current"]["temp_c"]
    except requests.RequestException:
        # If there's an error, return a random temperature
        return random.randint(-10, 35)


def main():
    connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
    channel = connection.channel()

    channel.queue_declare(queue='weather_data')

    while True:
        temperature = get_temperature()
        message = json.dumps({'temperature': temperature})
        channel.basic_publish(exchange='', routing_key='weather_data', body=message)
        print(f"Sent: {message}")
        sleep(300)  # Wait for 5 minutes before sending the next message


if __name__ == "__main__":
    main()
