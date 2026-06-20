from model import Exercise, parse_exercise
import sqlite3 as sql
import dotenv

class ExerciseDao:
    def __init__(self) -> None:
        db = dotenv.get_key('.env', 'DB') or 'main.db'
        self.conn = sql.connect(db, check_same_thread=False)
        self.conn.row_factory = sql.Row

    def get(self, ids: list[int]) -> list[Exercise]:
        if len(ids) == 0:
            return []

        cursor = self.conn.cursor()
        cursor.execute(f'SELECT * FROM "exercises" WHERE "id" IN ({",".join(["?" for _ in ids])})', tuple(ids))
        rows = cursor.fetchall()

        return list(map(lambda r: parse_exercise(dict(r)), rows))
