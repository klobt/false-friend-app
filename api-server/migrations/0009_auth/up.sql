ALTER TABLE "users" ADD COLUMN "email" TEXT;
ALTER TABLE "users" ADD COLUMN "password_hash" TEXT;
ALTER TABLE "users" ADD COLUMN "email_verified" INTEGER DEFAULT 0;

CREATE UNIQUE INDEX "idx_users_email" ON "users" ("email") WHERE "email" IS NOT NULL;

CREATE TABLE "email_codes" (
    "user_id" INTEGER PRIMARY KEY,
    "code" TEXT,
    "expires_at" TIMESTAMP,
    FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE
);

CREATE TABLE "auth_identities" (
    "provider" TEXT,
    "provider_user_id" TEXT,
    "user_id" INTEGER,
    PRIMARY KEY ("provider", "provider_user_id"),
    FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE
);
