#!/usr/bin/env python3
import MySQLdb
import psycopg2
import scrypt

import argparse
import secrets

import utils

def run():
    args = parse_args()
    db_source = source_connect(args)
    db_target = target_connect(args)

    role_map = build_role_map(db_source)
    competence_map = transfer_competences(db_source, db_target)
    person_application_map = transfer_persons(db_source, db_target, role_map)
    transfer_availabilities(db_source, db_target, person_application_map)
    transfer_competence_profiles(db_source, db_target, competence_map, person_application_map)

    db_target.commit()
    db_source.close()
    db_target.close()

def parse_args():
    arg_parser = argparse.ArgumentParser(description="""
    Imports data from the IV1201's example model to Appman.

    The source database is assumed to be MySQL(/MariaDB), the patron saint of
    shoddy legacy databases everywhere.
    """)

    arg_parser.add_argument('--source-host', type=str, default=None,
                            help="The address to the source database")
    arg_parser.add_argument('--source-port', type=int, default=3306,
                            help="The port to the source database (if applicable)")
    arg_parser.add_argument('--source-user', type=str, default='root',
                            help="The user to the source database")
    arg_parser.add_argument('--source-password', type=str, default='',
                            help="The password to the source database")
    arg_parser.add_argument('--source-db', type=str, required=True,
                            help="The source database name")

    arg_parser.add_argument('--target-host', type=str, default=None,
                            help="The address to the target database")
    arg_parser.add_argument('--target-port', type=int, default=5432,
                            help="The port to the target database (if applicable)")
    arg_parser.add_argument('--target-user', type=str, default=None,
                            help="The user to the target database")
    arg_parser.add_argument('--target-password', type=str, default='',
                            help="The password to the target database")
    arg_parser.add_argument('--target-db', type=str, default='appman',
                            help="The target database name")

    return arg_parser.parse_args()

def source_connect(args):
    connect_args = {
        'host': args.source_host,
        'port': args.source_port,
        'user': args.source_user,
        'password': args.source_password,
        'db': args.source_db,
    }
    return MySQLdb.connect(**utils.dict_drop_none(connect_args))

def target_connect(args):
    connect_args = {
        'host': args.target_host,
        'port': args.target_port,
        'user': args.target_user,
        'password': args.target_password,
        'dbname': args.target_db,
    }
    return psycopg2.connect(**utils.dict_drop_none(connect_args))

def build_role_map(db_source):
    """
    Maps the old model's roles to the new system.
    """
    cursor = db_source.cursor()
    role_map = {None: 'Applicant'}
    cursor.execute('SELECT role_id, name FROM role')
    for role_id, name in cursor:
        # It's ambiguous whether recruit is supposed to be a new employee or
        # somehow even lower than applicant. We assume it's an employee so
        # that we have multiple roles to map to.
        if name == 'recruit' or name == 'employee':
            role_map[role_id] = 'Employee'
        else:
            role_map[role_id] = 'Applicant'
    return role_map

def transfer_competences(db_source, db_target):
    src_cursor = db_source.cursor()
    tgt_cursor = db_target.cursor()
    competence_map = {}
    src_cursor.execute('SELECT competence_id, name FROM competence')
    for competence_id, name in src_cursor:
        tgt_cursor.execute('INSERT INTO competences(name) VALUES(%s) RETURNING id', (name,))
        competence_map[competence_id], = tgt_cursor.fetchone()
    return competence_map

def hash_password(password):
    password = password.encode("UTF-8")
    salt = secrets.token_bytes(32)
    # These MUST match ScryptPasswordHasher in PasswordHasher.scala
    hashed = scrypt.hash(password, salt, N=1<<15, r=8, p=1, buflen=64)
    return f"{salt.hex()}&{hashed.hex()}"

def transfer_persons(db_source, db_target, role_map):
    src_cursor = db_source.cursor()
    tgt_cursor = db_target.cursor()
    person_application_map = {}
    src_cursor.execute('SELECT person_id, name, surname, email, password, role_id, username FROM person')
    for person_id, name, surname, email, password, role_id, username in src_cursor:
        if password is not None:
            hashed_password = hash_password(password)
        else:
            hashed_password = None
        tgt_cursor.execute(
            'INSERT INTO users(username, email, password, firstname, surname, role) VALUES(%s, %s, %s, %s, %s, %s) RETURNING id',
            (username, email, hashed_password, name, surname, role_map[role_id]))
        user_id = tgt_cursor.fetchone()
        tgt_cursor.execute(
            'INSERT INTO applications("user") VALUES(%s) RETURNING id',
            (user_id,))
        person_application_map[person_id], = tgt_cursor.fetchone()
    return person_application_map

def transfer_availabilities(db_source, db_target, person_application_map):
    src_cursor = db_source.cursor()
    tgt_cursor = db_target.cursor()
    src_cursor.execute('SELECT availability_id, person_id, from_date, to_date FROM availability')
    for availability_id, person_id, from_date, to_date in src_cursor:
        if person_id is None:
            print(f"Availability {availability_id} does not contain a person_id, skipping...")
            continue
        tgt_cursor.execute(
            'INSERT INTO availabilities(application, "from", "to") VALUES(%s, %s, %s)',
            (person_application_map[person_id], from_date, to_date))

def transfer_competence_profiles(db_source, db_target, competence_map, person_application_map):
    src_cursor = db_source.cursor()
    tgt_cursor = db_target.cursor()
    src_cursor.execute('SELECT competence_profile_id, person_id, competence_id, years_of_experience FROM competence_profile')
    for competence_profile_id, person_id, competence_id, years_of_experience in src_cursor:
        if person_id is None:
            print(f"Competence_profile {competence_profile_id} does not contain a person_id, skipping...")
            continue
        if competence_id is None:
            print(f"Competence_profile {competence_profile_id} does not contain a competence_id, skipping...")
            continue
        tgt_cursor.execute(
            'INSERT INTO application_competences(competence, years_of_experience, application) VALUES(%s, %s, %s)',
            (competence_map[competence_id],
             years_of_experience,
             person_application_map[person_id]))

if __name__ == '__main__':
    run()
