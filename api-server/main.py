from fastapi import FastAPI, Query
from dao import ExerciseDao, SessionDao
from model import Session

app = FastAPI()

@app.get("/")
async def root():
    return {"message": "Hello World"}

@app.get("/exercises/")
async def get_exercises(ids: list[int] = Query(default=[])):
    return {
        "data": ExerciseDao().get(ids)
    }


@app.get("/sessions/")
async def get_sessions(limit: int = Query(default=10), offset: int = Query(default=0)):
    return {
        "data": SessionDao().get(limit, offset),
        "total": SessionDao().total()
    }

@app.post("/sessions/")
async def post_session(session: Session):
    SessionDao().create(session)
    return {
        "success": True
    }
