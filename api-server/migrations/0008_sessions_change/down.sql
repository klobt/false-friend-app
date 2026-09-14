ALTER TABLE "sessions" ADD COLUMN "correct_answers" INTEGER DEFAULT 0;
ALTER TABLE "sessions" ADD COLUMN "total_answers" INTEGER DEFAULT 0;
ALTER TABLE "sessions" ADD COLUMN "total_time_ms" INTEGER DEFAULT 0;
