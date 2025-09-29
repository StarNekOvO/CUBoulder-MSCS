from flask import Flask, jsonify
from database import app, db, Weather


@app.route('/', methods=['GET'])
def get_temperatures():
    temperatures = Weather.query.all()
    return jsonify([{'datetime': entry.datetime, 'temperature': entry.temperature} for entry in temperatures])


if __name__ == '__main__':
    app.run(debug=True)
