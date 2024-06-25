import pika
import json
from database import db, app, Weather


def analyze_data(ch, method, properties, body):
    data = json.loads(body)
    temperature = data['temperature']

    with app.app_context():
        new_entry = Weather(temperature=temperature)
        db.session.add(new_entry)
        db.session.commit()
        print(f"Stored temperature: {temperature}")


def main():
    connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
    channel = connection.channel()

    channel.queue_declare(queue='weather_data')

    channel.basic_consume(queue='weather_data', on_message_callback=analyze_data, auto_ack=True)

    print('Waiting for messages. To exit press CTRL+C')
    channel.start_consuming()


if __name__ == "__main__":
    main()
