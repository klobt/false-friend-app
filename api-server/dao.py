from datetime import date, datetime, timedelta
import json
from os.path import join
from typing import Any, Generator, Mapping, Optional, Self

from model import Card, Exercise, ExerciseType, parse_card, parse_exercise, Session, parse_session
import sqlite3 as sql
import dotenv

class GetBuilder:
    def __init__(self, dao: 'Dao') -> None:
        self.cursor = dao.conn.cursor()
        self.table = dao.table
        self.select_sql = ['*']
        self.where_sql = ['1']
        self.join_sql = []
        self.order_by = None
        self.limit = None
        self.offset = None
        self.where_params = []

    def select(self, cols: list[str]) -> Self:
        self.select_sql = cols
        return self

    def inner_join(self, table: str, col: str, op: str, rhs: str) -> Self:
        self.join_sql.append(f'INNER JOIN {table} ON {col} {op} {rhs}')
        return self

    def where_raw(self, col: str, op: str, raw: str) -> Self:
        self.where_sql.append(f'{col} {op} {raw}')
        return self

    def where(self, col: str, op: str, value) -> Self:
        if isinstance(value, (str, bytes)):
            self.where_raw(col, op, '?')
            self.where_params += [value]
            return self
        try:
            value_it = iter(value)
            value_list = list(value_it)
            placeholders = ['?'] * len(value_list) if len(value_list) > 0 else ['NULL']
            self.where_raw(col, op, '(' + ', '.join(placeholders) + ')')
            self.where_params += value_list
        except TypeError :
            self.where_raw(col, op, '?')
            self.where_params += [value]
        return self

    def with_limit(self, limit: int, offset: int) -> Self:
        self.limit = limit
        self.offset = offset
        return self

    def fetch_rows(self) -> list:
        params = []

        select_sql = ', '.join(self.select_sql)

        join_sql = ' '.join(self.join_sql)

        where_sql = ' AND '.join(map(lambda c: f'({c})', self.where_sql))
        params += self.where_params

        limit_sql = ''
        if self.limit is not None:
            limit_sql += ' LIMIT ? '
            params.append(self.limit)
        if self.offset is not None:
            if self.limit is None:
                limit_sql += ' LIMIT -1 '
            limit_sql += ' OFFSET ? '
            params.append(self.offset)

        query = f'SELECT {select_sql} FROM "{self.table}" {join_sql} WHERE {where_sql} {limit_sql}'

        self.cursor.execute(query, params)
        return self.cursor.fetchall()

class Dao:
    def __init__(self, table: str, conn: sql.Connection | None = None) -> None:
        db = dotenv.get_key('.env', 'DB') or 'main.db'
        self.conn = conn or sql.connect(db, check_same_thread=False)
        self.conn.row_factory = sql.Row
        self.table = table

    def total(self) -> int:
        cursor = self.conn.cursor()
        cursor.execute(f'SELECT COUNT(*) AS "total" FROM "{self.table}"')
        row = cursor.fetchone()

        return row['total']

    def _get(self) -> GetBuilder:
        return GetBuilder(self)

    def create_rows(self, data: list[Mapping[str, Any]], commit: bool = True) -> None:
        if len(data) == 0:
            return

        fields = list(data[0].keys())
        if any(list(row.keys()) != fields for row in data):
            raise ValueError('All rows must have identical fields')

        fields_sql = ', '.join(map(lambda col: f'"{col}"', fields))
        placeholders = ', '.join(['?'] * len(fields))
        params = [tuple(row[field] for field in fields) for row in data]

        query = f'INSERT INTO "{self.table}" ({fields_sql}) VALUES ({placeholders})'

        self.conn.executemany(query, params)

        if commit:
            self.conn.commit()

class ExerciseDao(Dao):
    def __init__(self, conn: sql.Connection | None = None) -> None:
        super().__init__("exercises", conn)

    def from_rows(self, rows: list[Mapping[str, Any]]) -> list[Exercise]:
        return list(map(lambda r: parse_exercise(dict(r)), rows))

    def get(self, ids: list[int]) -> list[Exercise]:
        if len(ids) == 0:
            return []
        rows = self._get().where('id', 'IN', ids).fetch_rows()
        return self.from_rows(rows)

    def get_all(self, limit: int = 10, without_ids: list[int] | None = None) -> Generator[Exercise, None, None]:
        without_ids = without_ids or []
        offset = 0

        while True:
            gb = self._get().with_limit(limit, offset)
            if len(without_ids) > 0:
                gb.where('id', 'NOT IN', without_ids)
            batch = gb.fetch_rows()
            if len(batch) == 0:
                break
            for item in self.from_rows(batch):
                yield item
            offset += limit

class SessionDao(Dao):
    def __init__(self, conn: sql.Connection | None = None) -> None:
        super().__init__("sessions", conn)

    def from_rows(self, rows: list[Mapping[str, Any]]) -> list[Session]:
        return list(map(lambda r: parse_session(dict(r)), rows))

    def get(self, limit: int, offset: int) -> list[Session]:
        rows = self._get().with_limit(limit, offset).fetch_rows()
        return self.from_rows(rows)

    def create(self, session: Session) -> bool:
        user_id = session.user_id or 1
        try:
            with self.conn:
                self.create_rows([{
                    'user_id': user_id,
                    'results': json.dumps(list(map(lambda r: r.model_dump(), session.results))),
                }], commit=False)

                cards = CardDao(self.conn)
                cards.sync_with_exercises(user_id, commit=False)
                cards.resolve_session(session, commit=False)
                StatsDao(self.conn).update_after_session(user_id, session.results, commit=False)
                return False
        except Exception:
            return True

class CardDao(Dao):
    def __init__(self, conn: sql.Connection | None = None) -> None:
        super().__init__("cards", conn)

    def from_rows(self, rows: list[Mapping[str, Any]]) -> list[Card]:
        return list(map(lambda r: parse_card(dict(r)), rows))

    def get(self, limit: int, offset: int, only_for_review: bool = False, user_id: int = 1) -> list[Card]:
        get_builder = self._get().where('user_id', '=', user_id).with_limit(limit, offset)
        if only_for_review:
            get_builder.where_raw('review_at', '<=', 'CURRENT_TIMESTAMP')
        rows = get_builder.fetch_rows()
        return self.from_rows(rows)

    def get_all_exercise_ids(self, user_id: int = 1, limit: int = 10) -> Generator[int, None, None]:
        offset = 0

        while True:
            rows = (
                self._get()
                .select(['exercise_id'])
                .where('user_id', '=', user_id)
                .with_limit(limit, offset)
                .fetch_rows()
            )
            if len(rows) == 0:
                break
            for row in rows:
                yield row['exercise_id']
            offset += limit

    def get_review_exercise_ids(
        self,
        type_filter: Optional[ExerciseType] = None,
        limit: int = 10,
        offset: int = 0,
        user_id: int = 1
    ) -> list[int]:
        self.sync_with_exercises(user_id)

        row_builder = self._get().select(['exercise_id']).where('user_id', '=', user_id)

        if type_filter is not None:
            row_builder = row_builder.inner_join('exercises', 'exercises.id', '=', 'exercise_id').where('exercises.type', '=', type_filter.value)

        rows = (
            row_builder
            .where_raw('review_at', '<=', 'CURRENT_TIMESTAMP')
            .with_limit(limit, offset)
            .fetch_rows()
        )

        return list(map(lambda row: row['exercise_id'], rows))

    def sync_with_exercises(self, user_id: int = 1, commit: bool = True) -> None:
        query = '''
            INSERT INTO "cards" ("user_id", "exercise_id", "box_id", "review_at")
            SELECT ?, "id", 201, CURRENT_TIMESTAMP
            FROM "exercises"
            WHERE NOT EXISTS (
                SELECT 1
                FROM "cards"
                WHERE "cards"."user_id" = ?
                AND "cards"."exercise_id" = "exercises"."id"
            )
        '''

        self.conn.execute(query, [user_id, user_id])

        if commit:
            self.conn.commit()

    def resolve_session(self, session: Session, commit: bool = True) -> None:
        if len(session.results) == 0:
            return

        user_id = session.user_id or 1
        results = {result.exercise_id: result.correct for result in session.results}
        exercise_ids = list(results.keys())
        placeholders = ', '.join(['?'] * len(exercise_ids))

        cards = self.conn.execute(
            f'''
                SELECT "exercise_id", "box_id"
                FROM "cards"
                WHERE "user_id" = ?
                AND "exercise_id" IN ({placeholders})
            ''',
            [user_id, *exercise_ids]
        ).fetchall()

        boxes = self.conn.execute(
            '''
                SELECT "id", CAST("data" AS INTEGER) AS "days"
                FROM "enums"
                WHERE "class_id" = 2
                ORDER BY "id"
            '''
        ).fetchall()

        box_ids = list(map(lambda row: row['id'], boxes))
        box_days = {row['id']: row['days'] for row in boxes}
        box_indexes = {box_id: idx for idx, box_id in enumerate(box_ids)}

        updates = []
        for card in cards:
            current_box_id = card['box_id']
            if results[card['exercise_id']]:
                current_idx = box_indexes[current_box_id]
                box_id = box_ids[min(current_idx + 1, len(box_ids) - 1)]
            else:
                box_id = box_ids[0]

            updates.append((
                box_id,
                f'+{box_days[box_id]} days',
                user_id,
                card['exercise_id']
            ))

        self.conn.executemany(
            '''
                UPDATE "cards"
                SET "box_id" = ?,
                    "review_at" = datetime('now', ?)
                WHERE "user_id" = ?
                AND "exercise_id" = ?
            ''',
            updates
        )

        if commit:
            self.conn.commit()

class StatsDao(Dao):
    def __init__(self, conn: sql.Connection | None = None) -> None:
        super().__init__("user_stats", conn)

    def get(self, user_id: int = 1) -> dict[str, Any]:
        rows = self._get().where('user_id', '=', user_id).fetch_rows()
        stats = dict(rows[0]) if rows else {
            'user_id': user_id, 'total_correct': 0, 'total_answers': 0,
            'current_streak': 0, 'longest_streak': 0, 'last_active_date': None
        }
        stats['accuracy'] = stats['total_correct'] / stats['total_answers'] if stats['total_answers'] else 0.0
        return stats

    def update_after_session(self, user_id: int, results: list, today: date | None = None, commit: bool = True) -> None:
        today = today or date.today()
        stats = self.get(user_id)
        correct = sum(1 for r in results if r.correct)

        if stats['last_active_date'] == today.isoformat():
            streak = stats['current_streak'] or 1
        elif stats['last_active_date'] == (today - timedelta(days=1)).isoformat():
            streak = stats['current_streak'] + 1
        else:
            streak = 1

        self.conn.execute(
            '''
                INSERT INTO "user_stats"
                    ("user_id", "total_correct", "total_answers", "current_streak", "longest_streak", "last_active_date")
                VALUES (?, ?, ?, ?, ?, ?)
                ON CONFLICT ("user_id") DO UPDATE SET
                    "total_correct" = excluded."total_correct",
                    "total_answers" = excluded."total_answers",
                    "current_streak" = excluded."current_streak",
                    "longest_streak" = excluded."longest_streak",
                    "last_active_date" = excluded."last_active_date"
            ''',
            [
                user_id,
                stats['total_correct'] + correct,
                stats['total_answers'] + len(results),
                streak,
                max(stats['longest_streak'], streak),
                today.isoformat(),
            ]
        )

        if commit:
            self.conn.commit()

class UserDao(Dao):
    def __init__(self, conn: sql.Connection | None = None) -> None:
        super().__init__("users", conn)

    def get(self, limit: int = 10, offset: int = 0) -> list[dict[str, Any]]:
        rows = self._get().with_limit(limit, offset).fetch_rows()
        return list(map(lambda row: {
            'id': row['id'],
            'public_data': json.loads(row['public_data'])
        }, rows))

    def get_public_data(self, user_id: int = 1) -> dict[str, Any]:
        rows = (
            self._get()
            .select(['public_data'])
            .where('id', '=', user_id)
            .fetch_rows()
        )

        if len(rows) == 0:
            return {}

        return json.loads(rows[0]['public_data'])

    def set_public_data(
        self,
        data: Mapping[str, Any],
        user_id: int = 1
    ) -> None:
        self.conn.execute(
            '''
                INSERT INTO "users" ("id", "public_data")
                VALUES (?, ?)
                ON CONFLICT ("id") DO UPDATE SET
                    "public_data" = excluded."public_data"
            ''',
            [user_id, json.dumps(dict(data))]
        )
        self.conn.commit()

    def get_by_id(self, user_id: int) -> Optional[dict[str, Any]]:
        rows = self._get().where('id', '=', user_id).fetch_rows()
        return dict(rows[0]) if rows else None

    def get_by_email(self, email: str) -> Optional[dict[str, Any]]:
        rows = self._get().where('email', '=', email).fetch_rows()
        return dict(rows[0]) if rows else None

    def create_with_email(self, email: Optional[str], password_hash: Optional[str]) -> int:
        cursor = self.conn.cursor()
        cursor.execute(
            'INSERT INTO "users" ("email", "password_hash", "public_data") VALUES (?, ?, ?)',
            [email, password_hash, '{}']
        )
        self.conn.commit()
        return cursor.lastrowid

    def set_verified(self, user_id: int) -> None:
        self.conn.execute('UPDATE "users" SET "email_verified" = 1 WHERE "id" = ?', [user_id])
        self.conn.commit()

    def set_password(self, user_id: int, password_hash: str) -> None:
        self.conn.execute('UPDATE "users" SET "password_hash" = ? WHERE "id" = ?', [password_hash, user_id])
        self.conn.commit()

class EmailCodeDao(Dao):
    def __init__(self, conn: sql.Connection | None = None) -> None:
        super().__init__("email_codes", conn)

    def set(self, user_id: int, code: str, ttl_minutes: int = 15) -> None:
        expires_at = (datetime.utcnow() + timedelta(minutes=ttl_minutes)).isoformat()
        self.conn.execute(
            '''
                INSERT INTO "email_codes" ("user_id", "code", "expires_at")
                VALUES (?, ?, ?)
                ON CONFLICT ("user_id") DO UPDATE SET
                    "code" = excluded."code",
                    "expires_at" = excluded."expires_at"
            ''',
            [user_id, code, expires_at]
        )
        self.conn.commit()

    def verify(self, user_id: int, code: str) -> bool:
        rows = self._get().where('user_id', '=', user_id).fetch_rows()
        if len(rows) == 0 or rows[0]['code'] != code or datetime.fromisoformat(rows[0]['expires_at']) < datetime.utcnow():
            return False
        self.conn.execute('DELETE FROM "email_codes" WHERE "user_id" = ?', [user_id])
        self.conn.commit()
        return True

class IdentityDao(Dao):
    def __init__(self, conn: sql.Connection | None = None) -> None:
        super().__init__("auth_identities", conn)

    def get_user_id(self, provider: str, provider_user_id: str) -> Optional[int]:
        rows = self._get().where('provider', '=', provider).where('provider_user_id', '=', provider_user_id).fetch_rows()
        return rows[0]['user_id'] if rows else None

    def link(self, provider: str, provider_user_id: str, user_id: int) -> None:
        self.conn.execute(
            'INSERT OR IGNORE INTO "auth_identities" ("provider", "provider_user_id", "user_id") VALUES (?, ?, ?)',
            [provider, provider_user_id, user_id]
        )
        self.conn.commit()
