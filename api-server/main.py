from fastapi import FastAPI, Query
from dao import ExerciseDao

app = FastAPI()

@app.get("/")
async def root():
    return {"message": "Hello World"}

@app.get("/exercises")
async def get_exercises(ids: list[int] = Query(default=[])):
    return {
        "data": ExerciseDao().get(ids)
    }
