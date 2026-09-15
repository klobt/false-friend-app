DROP TABLE "auth_identities";
DROP TABLE "email_codes";
DROP INDEX "idx_users_email";
ALTER TABLE "users" DROP COLUMN "email_verified";
ALTER TABLE "users" DROP COLUMN "password_hash";
ALTER TABLE "users" DROP COLUMN "email";
