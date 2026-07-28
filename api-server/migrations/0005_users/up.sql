CREATE TABLE "users" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "public_data" TEXT NOT NULL DEFAULT '{}',
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO "users" ("id", "public_data")
VALUES (1, '{}');
