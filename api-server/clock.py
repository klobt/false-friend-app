import os
from datetime import date, datetime, timedelta

import dotenv

dotenv.load_dotenv()

# Time control is for testing only; endpoints return 404 unless this is enabled.
ENABLED = os.getenv('ALLOW_TIME_CONTROL', 'false').strip().lower() in ('1', 'true', 'yes', 'on')

# Server-wide offset (in-memory, resets on restart).
_offset = timedelta(0)

def offset() -> timedelta:
    return _offset if ENABLED else timedelta(0)

def advance(delta: timedelta) -> None:
    global _offset
    _offset += delta

def reset() -> None:
    global _offset
    _offset = timedelta(0)

def utcnow() -> datetime:
    return datetime.utcnow() + offset()

def today() -> date:
    return (datetime.now() + offset()).date()

def sql_modifier() -> str:
    """SQLite datetime() modifier for the current offset, e.g. '+86400 seconds'."""
    return f'{int(offset().total_seconds()):+d} seconds'

def sql_now() -> str:
    """SQL expression equal to CURRENT_TIMESTAMP shifted by the offset."""
    return f"datetime('now', '{sql_modifier()}')"
