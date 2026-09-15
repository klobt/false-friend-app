CREATE TABLE "friendships" (
    "user_id_1" INTEGER NOT NULL,
    "user_id_2" INTEGER NOT NULL,
    "status" TEXT NOT NULL DEFAULT 'pending',
    "requested_by" INTEGER NOT NULL,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY ("user_id_1", "user_id_2"),
    FOREIGN KEY ("user_id_1") REFERENCES "users" ("id") ON DELETE CASCADE,
    FOREIGN KEY ("user_id_2") REFERENCES "users" ("id") ON DELETE CASCADE,
    CHECK ("user_id_1" < "user_id_2")
);
