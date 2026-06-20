import sqlite3 as sql
import click
import os
import dotenv

def read_migrations_from_disk(directory: str):
    migrations = []
    migration_dirs = [
        d for d in os.listdir(directory)
        if os.path.isdir(os.path.join(directory, d))
    ]
    migration_dirs.sort()
    for migration_dir in migration_dirs:
        path = os.path.join(directory, migration_dir)
        up_path = os.path.join(path, 'up.sql')
        down_path = os.path.join(path, 'down.sql')
        with open(up_path, 'r') as up_file:
            up_sql = up_file.read()
        with open(down_path, 'r') as down_file:
            down_sql = down_file.read()
        migrations.append({
            "key": migration_dir,
            "up": up_sql,
            "down": down_sql
        })
    return migrations

def get_current_migration_keys(conn: sql.Connection) -> set[str]:
    cursor = conn.cursor()
    cursor.execute("SELECT name FROM sqlite_master WHERE type='table' AND name='migrations'")
    if cursor.fetchone() is None:
        cursor.execute("CREATE TABLE migrations (key TEXT PRIMARY KEY)")
        conn.commit()
        return set()
    cursor.execute("SELECT key FROM migrations")
    return set(row[0] for row in cursor.fetchall())

def get_db() -> str:
    return dotenv.get_key('.env', 'DB') or 'main.db'

def migrate(migrations) -> bool:
    conn = sql.connect(get_db())
    cursor = conn.cursor()

    current_migrations = get_current_migration_keys(conn)
    pending_migrations = [m for m in migrations if m['key'] not in current_migrations]

    result = False

    for migration in pending_migrations:
        cursor.executescript(migration['up'])
        cursor.execute("INSERT INTO migrations (key) VALUES (?)", (migration['key'],))
        click.echo(f"{migration['key']}: up")
        result = True

    conn.commit()
    conn.close()

    return result

def rollback(migrations, steps=None) -> bool:
    conn = sql.connect(get_db())
    cursor = conn.cursor()

    current_migrations = get_current_migration_keys(conn)
    applied_migrations = [m for m in migrations if m['key'] in current_migrations]
    applied_migrations.sort(key=lambda m: m['key'])
    migrations_to_rollback = applied_migrations[-steps:] if steps and steps > 0 else applied_migrations

    result = False

    for migration in migrations_to_rollback:
        cursor.executescript(migration['down'])
        cursor.execute("DELETE FROM migrations WHERE key = ?", (migration['key'],))
        click.echo(f"{migration['key']}: down")
        result = True

    conn.commit()
    conn.close()

    return result

@click.group()
def cli():
    pass

@cli.command()
def up():
    """Apply pending migrations."""
    migrations = read_migrations_from_disk('migrations')
    if migrate(migrations):
        click.echo("Migrations applied successfully.")
    else:
        click.echo("Up to date.")

@cli.command()
@click.option('--steps', default=1, help='Number of migrations to rollback')
def down(steps):
    """Rollback applied migrations."""
    migrations = read_migrations_from_disk('migrations')
    if rollback(migrations, steps=steps):
        click.echo("Migrations rolled back successfully.")
    else:
        click.echo("Nothing to roll back.")

if __name__ == "__main__":
    cli()
