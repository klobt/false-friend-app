CREATE TABLE "user_stats" (
    "user_id" INTEGER PRIMARY KEY,
    "total_correct" INTEGER DEFAULT 0,
    "total_answers" INTEGER DEFAULT 0,
    "current_streak" INTEGER DEFAULT 0,
    "longest_streak" INTEGER DEFAULT 0,
    "last_active_date" TEXT,
    FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE
);
