from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from datetime import datetime

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///../instance/Weather.sqlite3'

db = SQLAlchemy(app)


class Weather(db.Model):
    datetime = db.Column(db.DateTime, primary_key=True, default=datetime.utcnow)
    temperature = db.Column(db.Integer, nullable=False)


with app.app_context():
    db.create_all()
