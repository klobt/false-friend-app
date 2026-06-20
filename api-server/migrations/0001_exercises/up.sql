INSERT INTO "enums"
    ("id", "class_id", "value")
VALUES
    (101, 1, 'translation'),
    (102, 1, 'definition'),
    (103, 1, 'connect');

CREATE TABLE "exercises" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "gen_id" INTEGER,
    "type" INTEGER,
    "data" TEXT,
    FOREIGN KEY ("type") REFERENCES "enums" ("id") ON DELETE RESTRICT
);

CREATE INDEX "idx_exercises_gen_id" ON "exercises" ("gen_id");
