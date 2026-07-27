DROP TABLE "cards";

DELETE FROM "enums" WHERE "class_id" = 2;

ALTER TABLE "enums" DROP COLUMN "data";
