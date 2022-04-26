/*
 * The Flyway tool applies this migration to create the database.
 *
 * Follow this style guide https://about.gitlab.com/handbook/business-ops/data-team/platform/sql-style-guide/
 * use VARCHAR(63) for names in organization and schema
 *
 * Copy a version of this comment into the next migration
 *
 */

-- 1.
-- replaces earlier brin index on just created/sender
DROP INDEX IF EXISTS idx_report_file_created_and_sender;

-- adds item_count so queries counting up items per date and sender can do an index-only scan
CREATE INDEX idx_report_file_created_and_sender
  ON report_file
  USING BTREE(created_at, sending_org, item_count);

-- 2.
