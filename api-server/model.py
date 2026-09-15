from datetime import date, datetime
from enum import IntEnum
import json
from typing import Any, Annotated, Literal, Optional, Union, NamedTuple

from pydantic import BaseModel, EmailStr, Field, TypeAdapter

class ExerciseType(IntEnum):
    translation = 101
    definition = 102
    connect = 103

class BaseExercise(BaseModel):
    id: int
    type: ExerciseType

class ChooseOneData(BaseModel):
    word: str
    answers: list[str]
    correct_idx: int

class ConnectData(BaseModel):
    left: list[str]
    right: list[str]

class TranslationExercise(BaseModel):
    id: int
    type: Literal[ExerciseType.translation] = ExerciseType.translation
    data: ChooseOneData

class DefinitionExercise(BaseModel):
    id: int
    type: Literal[ExerciseType.definition] = ExerciseType.definition
    data: ChooseOneData

class ConnectExercise(BaseModel):
    id: int
    type: Literal[ExerciseType.connect] = ExerciseType.connect
    data: ConnectData

Exercise = Annotated[
    Union[TranslationExercise, DefinitionExercise, ConnectExercise],
    Field(discriminator='type')
]

ExerciseAdapter = TypeAdapter(Exercise)

def parse_exercise(row: dict) -> Exercise:
    row['data'] = json.loads(row['data'])
    row_json = json.dumps(row)
    return ExerciseAdapter.validate_json(row_json)

class SessionResult(BaseModel):
    exercise_id: int
    correct: bool
    time_ms: float

class Session(BaseModel):
    user_id: Optional[int] = Field(default=1)
    results: list[SessionResult]

SessionAdapter = TypeAdapter(Session)

def parse_session(row: dict) -> Session:
    row['results'] = json.loads(row['results'])
    row_json = json.dumps(row)
    return SessionAdapter.validate_json(row_json)

class Card(BaseModel):
    user_id: Optional[int] = Field(default=1)
    exercise_id: int
    box_id: int
    review_at: datetime

CardAdapter = TypeAdapter(Card)

def parse_card(row: dict) -> Card:
    row_json = json.dumps(row)
    return CardAdapter.validate_json(row_json)

class PublicUserData(BaseModel):
    data: dict[str, Any]

class FriendAction(BaseModel):
    accept: bool

class DeviceToken(BaseModel):
    token: str
    platform: Optional[str] = None

class PushMessage(BaseModel):
    title: str
    body: str
    data: Optional[dict[str, Any]] = None
class UserStats(BaseModel):
    user_id: int
    total_correct: int
    total_answers: int
    current_streak: int
    longest_streak: int
    last_active_date: Optional[date] = None
    accuracy: float

class RegisterRequest(BaseModel):
    email: EmailStr
    password: str

class VerifyEmailRequest(BaseModel):
    email: EmailStr
    code: str

class LoginRequest(BaseModel):
    email: EmailStr
    password: str

class SocialLoginRequest(BaseModel):
    id_token: str

class ChangePasswordRequest(BaseModel):
    current_password: str
    new_password: str

class TokenResponse(BaseModel):
    access_token: str
    token_type: str = 'bearer'
