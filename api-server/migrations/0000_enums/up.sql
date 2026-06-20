CREATE TABLE "enums" (
    "id" INTEGER PRIMARY KEY,
    "class_id" INTEGER,
    "value" TEXT
);

CREATE INDEX "enums_class_id_idx" ON "enums" ("class_id");
