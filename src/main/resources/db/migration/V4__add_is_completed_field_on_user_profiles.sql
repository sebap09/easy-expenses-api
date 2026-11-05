BEGIN;
ALTER TABLE user_profiles
    ADD COLUMN is_completed BOOLEAN NOT NULL DEFAULT false;

WITH CTE_Completed_User_Profiles AS
(
    SELECT DISTINCT user_profiles.user_id
    FROM user_profiles
    INNER JOIN user_payment_methods
        ON user_profiles.user_id = user_payment_methods.user_id
    INNER JOIN user_expense_categories
        ON user_profiles.user_id = user_expense_categories.user_id
    INNER JOIN user_expense_sub_categories
        ON user_expense_categories.id = user_expense_sub_categories.user_expense_category_id
)

UPDATE user_profiles
    SET is_completed = true
WHERE user_id IN (
        SELECT user_id FROM CTE_Completed_User_Profiles
    );
COMMIT;