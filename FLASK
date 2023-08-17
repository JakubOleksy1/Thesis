# A very simple Flask Hello World app for you to get started with...
from sqlalchemy.orm.attributes import flag_modified
from flask import Flask, redirect, render_template, request, jsonify, abort, flash
from flask_sqlalchemy import SQLAlchemy
#from wtforms import  IntegerField, TextAreaField, SubmitField, RadioField, SelectField
#from sqlalchemy import desc ,asc
from flask_marshmallow import Marshmallow
from datetime import datetime
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
#import requests

#from marshmallow import Schema, fields, pprint
#from datetime import datetime, timedelta
#import  os
#from os.path import isfile, join
#from os import listdir
#import json
#from io import StringIO
#from werkzeug.wrappers import Response
#import itertools
#import random
#import string

SQLALCHEMY_DATABASE_URI = "mysql+mysqlconnector://{username}:{password}@{hostname}/{databasename}".format(
    username="JakubOleksy",
    password="KubaaS_01", # database passowrd hidden
    hostname="JakubOleksy.mysql.pythonanywhere-services.com",
    databasename="JakubOleksy$Tetris_Database",
)

engine = create_engine(SQLALCHEMY_DATABASE_URI)

app = Flask(__name__)
app.config["SQLALCHEMY_DATABASE_URI"] = SQLALCHEMY_DATABASE_URI
app.config["SQLALCHEMY_POOL_RECYCLE"] = 299 # connection timeouts
app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False # no warning disruptions

if __name__ == '__main__':
    app.run()

db = SQLAlchemy(app)
ma = Marshmallow(app)

class Users(db.Model):

    __tablename__ = "User"
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.VARCHAR(255))
    password = db.Column(db.VARCHAR(255))
    created_at = db.Column(db.DATETIME)


    def __init__(self, username, password, created_at):
        self.username = username
        self.password = password
        self.created_at = created_at

    def as_dict(self):
        return {
            "id": self.id,
            "username": self.username,
            "password": self.password,
            "created_at": self.created_at.strftime("%Y-%m-%d %H:%M:%S")
        }

class UsersSchema(ma.Schema):
    class Meta:
        # Fields to expose
        fields = ('id' ,'username', 'password', 'created_at')

user_schema = UsersSchema()
users_schema = UsersSchema(many=True)

@app.route("/users", methods=["GET"])
def get_all_users():
    User_many = Users.query.all()
    result = users_schema.dump(User_many)

    return jsonify(result)

@app.route("/users/<id>", methods=["GET"])
def get_user(id):
    User = Users.query.get(id)
    result = user_schema.dump(User)

    return jsonify(result)

@app.route("/users/add", methods=["POST"])
def add_user():
    data = request.get_json()

    try:
        username = data["username"]
        password = data["password"]
        created_at = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

        if not username or not password or not created_at:
            return jsonify({"error": "Missing required fields"}), 400

        new_user = Users(username, password, created_at)
        db.session.add(new_user)
        db.session.commit()
        user = Users.query.get(new_user.id)

        return user_schema.jsonify(user), 201

    except (KeyError, ValueError):
        db.session.rollback()
        return jsonify({"error"}), 500





class Machine(db.Model):
    __tablename__ = "Machine"
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer)
    gave_prize = db.Column(db.Boolean)
    addres = db.Column(db.VARCHAR(255))

    def __init__(self, user_id, gave_prize, addres):
        self.user_id = user_id
        self.gave_prize = gave_prize
        self.addres = addres

class MachineSchema(ma.Schema):
    class Meta:
        # Fields to expose
        fields = ('user_id', 'gave_prize', 'addres')

machine_schema = MachineSchema()
machines_schema = MachineSchema(many=True)

@app.route("/machines", methods=["GET"])
def get_all_machines():
    Machines_many = Machine.query.all()
    result = machines_schema.dump(Machines_many)

    return jsonify(result)

@app.route("/machine/<id>", methods=["GET"])
def get_machine(id):
    machine = Machine.query.get(id)
    result = machine_schema.dump(machine)

    return jsonify(result)

@app.route("/machine/add", methods=["POST"])
def add_machine():
    user_id = request.json["user_id"]
    gave_prize = request.json["gave_prize"]
    addres = request.json["addres"]
    new_machine = Machine(user_id, gave_prize, addres)
    db.session.add(new_machine)
    db.session.commit()
    machine = Machine.query.get(new_machine.id)

    return machine_schema.jsonify(machine)

@app.route('/machine/update/<machine_id>', methods=['PUT'])
def update_machine(machine_id):
    machine = Machine.query.get(machine_id)
    db.session.commit()

    return machine_schema.jsonify(machine)

@app.route('/machine/update_status/<machine_id>', methods=['PUT'])
def update_status_machine(machine_id):
    machine = Machine.query.get(machine_id)
    gave_prize = request.json['gave_prize']
    machine.gave_prize = gave_prize
    db.session.commit()

    return machine_schema.jsonify(machine)



class Action(db.Model):
    __tablename__ = "Action"
    action_id = db.Column(db.Integer, primary_key=True)
    machine_id = db.Column(db.Integer)
    points = db.Column(db.Integer)
    pause = db.Column(db.Boolean)
    give_prize = db.Column(db.Boolean)
    code = db.Column(db.VARCHAR(255))
    is_used = db.Column(db.Boolean)

    def __init__(self, machine_id, points, pause, give_prize, code, is_used):
        self.machine_id = machine_id
        self.points = points
        self.pause = pause
        self.give_prize = give_prize
        self.code = code
        self.is_used = is_used


class ActionSchema(ma.Schema):
    class Meta:
        # Fields to expose
        fields = ('action_id','machine_id','points', 'pause', 'give_prize', 'code', 'is_used')

action_schema = ActionSchema()
actions_schema = ActionSchema(many=True)

@app.route("/actions", methods=["GET"])
def get_all_actions():
    Actions_many = Action.query.all()
    result = actions_schema.dump(Actions_many)

    return jsonify(result)

@app.route("/action/<id>", methods=["GET"])
def get_action(id):
    action = Action.query.get(id)
    result = action_schema.dump(action)

    return jsonify(result)

@app.route("/action/last", methods=["GET"])
def get_last_action():
    all_actions = Action.query.all()
    last_action = all_actions[-1]
    result = action_schema.dump(last_action)

    return jsonify(result)


@app.route("/action/add", methods=["POST"])
def add_action():
    data = request.get_json()

    try:
        machine_id = data["machine_id"]
        if not isinstance(machine_id, int):
            return jsonify({"error": "machine_id should be an integer"}), 400

        points = data["points"]
        if not isinstance(points, int):
            return jsonify({"error": "points should be an integer"}), 400

        pause = data["pause"]
        if not isinstance(pause, bool):
            return jsonify({"error": "pause should be a boolean"}), 400

        give_prize = data["give_prize"]
        if not isinstance(give_prize, bool):
            return jsonify({"error": "give_prize should be a boolean"}), 400

        code = data["code"]
        if not isinstance(code, str):
            return jsonify({"error": "code should be a string"}), 400

        is_used = data["is_used"]
        if not isinstance(is_used, bool):
            return jsonify({"error": "is_used should be a boolean"}), 400


        new_action = Action(machine_id, points, pause, give_prize, code, is_used)
        db.session.add(new_action)
        db.session.commit()

        return action_schema.jsonify(new_action), 201

    except (KeyError, ValueError):
        db.session.rollback()
        return jsonify({"error": "Invalid input data"}), 500



@app.route("/action/check", methods=["POST"])
def check_code():
    data = request.get_json()

    try:
        entered_code = data["code"]
        action = Action.query.filter_by(code=entered_code).first()

        if action:
            response_data = {
                "is_valid": True,
                "is_used": action.is_used
            }
        else:
            response_data = {
                "is_valid": False,
                "is_used": False
            }

        return jsonify(response_data)

    except KeyError:
        return jsonify({"error": "Invalid input data"}), 400




@app.route("/action/update_status/<code>", methods=["PUT"])
def update_code_status(code):
    data = request.get_json()

    try:
        is_used = data["is_used"]
        if not isinstance(is_used, bool):
            return jsonify({"error": "is_used should be a boolean"}), 400

        action = Action.query.filter_by(code=code).first()

        if action:
            action.is_used = is_used
            db.session.commit()

            response_data = {
                "message": f"Code '{code}' updated successfully",
                "is_used": is_used
            }
        else:
            response_data = {
                "error": f"Code '{code}' not found"
            }

        return jsonify(response_data)

    except KeyError:
        return jsonify({"error": "Invalid input data"}), 400



@app.route('/')
def hello_world():
    return 'Tetris z nagrodami Komendy: /users pokaz wszystich /users/id pokaz konkretnego /users/add dodaj nowego analogicznie machine i actions'
