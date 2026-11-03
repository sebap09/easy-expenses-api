BEGIN;
ALTER TABLE user_expense_sub_categories
    DROP CONSTRAINT fk_user_expense_sub_categories_user_profile,
    DROP CONSTRAINT uk_user_expense_sub_categories_user_name_category,
    DROP COLUMN user_id;
ALTER TABLE user_expense_sub_categories
    ADD CONSTRAINT uk_user_expense_sub_categories_name_category UNIQUE (name, user_expense_category_id);
COMMIT;