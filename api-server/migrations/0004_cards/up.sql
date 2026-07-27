ALTER TABLE "enums" ADD COLUMN "data" TEXT DEFAULT '';

INSERT INTO "enums"
    ("id", "class_id", "value", "data")
VALUES
    (201, 2, 'new', '1'),
    (202, 2, 'hard', '2'),
    (203, 2, 'easy', '7'),
    (204, 2, 'mature', '30');

CREATE TABLE "cards" (
    "user_id" INTEGER DEFAULT 1,
    "exercise_id" INTEGER,
    "box_id" INTEGER DEFAULT 201,
    "review_at" TIMESTAMP,
    PRIMARY KEY ("user_id", "exercise_id"),
    FOREIGN KEY ("exercise_id") REFERENCES "exercises" ("id") ON DELETE RESTRICT,
    FOREIGN KEY ("box_id") REFERENCES "enums" ("id") ON DELETE RESTRICT
);
