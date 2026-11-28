CREATE TABLE "words" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "word" TEXT NOT NULL,
    "is_false_friend" INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE "word_exercises" (
    "word_id" INTEGER NOT NULL,
    "exercise_id" INTEGER NOT NULL,
    PRIMARY KEY ("word_id", "exercise_id"),
    FOREIGN KEY ("word_id") REFERENCES "words" ("id") ON DELETE CASCADE,
    FOREIGN KEY ("exercise_id") REFERENCES "exercises" ("id") ON DELETE CASCADE
);

CREATE INDEX "idx_word_exercises_exercise_id" ON "word_exercises" ("exercise_id");
