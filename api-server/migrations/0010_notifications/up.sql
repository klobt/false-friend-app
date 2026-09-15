CREATE TABLE "device_tokens" (
    "token" TEXT PRIMARY KEY,
    "user_id" INTEGER NOT NULL,
    "platform" TEXT,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE
);

CREATE INDEX "idx_device_tokens_user" ON "device_tokens" ("user_id");
