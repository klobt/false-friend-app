from datetime import date, datetime
from enum import IntEnum
import json
from typing import Annotated, Literal, Optional, Union, NamedTuple

from pydantic import BaseModel, Field, TypeAdapter

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
    correct_answers: int
    total_answers: int
    total_time_ms: float

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
