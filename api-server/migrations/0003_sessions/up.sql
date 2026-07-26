CREATE TABLE "sessions" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "user_id" INTEGER DEFAULT 1,
    "results" TEXT,
    "correct_answers" INTEGER DEFAULT 0,
    "total_answers" INTEGER,
    "total_time_ms" INTEGER,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX "idx_sessions_user_id" ON "sessions" ("user_id");
