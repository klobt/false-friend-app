import json

from model import Exercise, parse_exercise, Session, parse_session
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

class SessionDao:
    def __init__(self) -> None:
        db = dotenv.get_key('.env', 'DB') or 'main.db'
        self.conn = sql.connect(db, check_same_thread=False)
        self.conn.row_factory = sql.Row

    def get(self, limit: int, offset: int) -> list[Session]:
        cursor = self.conn.cursor()
        cursor.execute(f'SELECT * FROM "sessions" LIMIT ? OFFSET ?', [limit, offset])
        rows = cursor.fetchall()

        return list(map(lambda r: parse_session(dict(r)), rows))

    def total(self) -> int:
        cursor = self.conn.cursor()
        cursor.execute(f'SELECT COUNT(*) AS "total" FROM "sessions"');
        row = cursor.fetchall()

        return row[0]['total']

    def create(self, session: Session) -> None:
        cursor = self.conn.cursor()
        sql = f'''
            INSERT INTO "sessions"
            (
                "results",
                "correct_answers",
                "total_answers",
                "total_time_ms"
            )
            VALUES (
                :results,
                :correct_answers,
                :total_answers,
                :total_time_ms
            )
        '''

        cursor.executemany(sql, [{
            'results': json.dumps(list(map(lambda r: json.loads(r.json()), session.results))),
            'correct_answers': session.correct_answers,
            'total_answers': session.total_answers,
            'total_time_ms': session.total_time_ms,
        }]);

        self.conn.commit()
